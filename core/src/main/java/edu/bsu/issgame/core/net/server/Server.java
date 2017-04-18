/*
  Copyright 2015 Ball State University

  This file is part of Collaboration Station.

  Collaboration Station is free software: you can redistribute it
  and/or modify it under the terms of the GNU General Public
  License as published by the Free Software Foundation, either
  version 3 of the License, or (at your option) any later version.

  Collaboration Station is distributed in the hope that it will
  be useful, but WITHOUT ANY WARRANTY; without even the implied
  warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR
  PURPOSE.  See the GNU General Public License for more details.

  You should have received a copy of the GNU General Public
  License along with Collaboration Station.  If not, see
  <http://www.gnu.org/licenses/>.
*/
package edu.bsu.issgame.core.net.server;

import static com.google.common.base.Preconditions.checkNotNull;
import static playn.core.PlayN.log;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutorService;

import react.SignalView;
import react.Slot;
import react.UnitSignal;
import react.UnitSlot;

import com.google.common.base.MoreObjects;
import com.google.common.collect.Lists;

import edu.bsu.issgame.core.Score;
import edu.bsu.issgame.core.Scoreboard;
import edu.bsu.issgame.core.mission.Mission;
import edu.bsu.issgame.core.mission.Mission.Result;
import edu.bsu.issgame.core.mission.Mission.Result.Failure;
import edu.bsu.issgame.core.mission.Mission.Result.MissionSuccess;
import edu.bsu.issgame.core.mission.Mission.Result.ScenarioSuccess;
import edu.bsu.issgame.core.mission.MissionFactory;
import edu.bsu.issgame.core.mission.Scenario;
import edu.bsu.issgame.core.net.ClientId;
import edu.bsu.issgame.core.net.Message;
import edu.bsu.issgame.core.net.Message.Introduce;
import edu.bsu.issgame.core.net.Message.ReportScore;
import edu.bsu.issgame.core.net.Message.RequestAdvanceToScenarioSetup;
import edu.bsu.issgame.core.net.Message.RequestStartGame;
import edu.bsu.issgame.core.net.Message.RequestStartScenario;
import edu.bsu.issgame.core.net.Message.VersionMismatch;
import edu.bsu.issgame.core.net.Message.Visitor;
import edu.bsu.issgame.core.net.PingService;

public final class Server implements Runnable {

	static final int MAX_PLAYERS = 4;

	private final ConnectionAccepter connector;
	private boolean stopRequested = false;
	private final ExecutorService executorService;
	private State state;
	private List<ClientHandler> clientHandlers = Lists.newArrayList();

	private UnitSignal serverStart = new UnitSignal();
	private UnitSignal serverStopped = new UnitSignal();

	public final String expeditionName;
	public final int versionCode;

	public Server(ExecutorService executorService,
			ConnectionAccepter connector, String expeditionName, int versionCode) {
		this.connector = checkNotNull(connector);
		this.executorService = checkNotNull(executorService);
		this.state = lobbyState;
		this.expeditionName = checkNotNull(expeditionName);
		this.versionCode = checkNotNull(versionCode);
	}

	public SignalView<Void> onServerStart() {
		return serverStart;
	}

	public SignalView<Void> onServerStop() {
		return serverStopped;
	}

	public void run() {
		int connections = 0;
		serverStart.emit();
		while (!stopRequested && connections <= MAX_PLAYERS) {
			try {
				final ClientHandler handler = connector
						.acceptClientConnection(this);
				runClientHandler(handler);
				connections++;
			} catch (IOException ioe) {
				if (!stopRequested) {
					log().error(
							"Problem connecting to client: " + ioe.getMessage());
					ioe.printStackTrace();
				}
			}
		}
		try {
			log().debug(
					"Done accepting connections; shutting down client connector.");
			connector.shutdown();
		} catch (IOException e) {
			e.printStackTrace();
		}
		serverStopped.emit();
		log().debug("Server is done.");
	}

	public void runClientHandler(ClientHandler handler) {
		log().debug("A client has connected, starting the handler thread.");
		directMessagesFromClientToTheServerState(handler);
		executorService.execute(handler);
	}

	private void directMessagesFromClientToTheServerState(ClientHandler handler) {
		handler.onReceived().connect(new Slot<MessageReceivedEvent>() {
			@Override
			public void onEmit(MessageReceivedEvent event) {
				state.onReceive(event);
			}
		});
	}

	private void broadcast(Message message) {
		checkNotNull(message);
		synchronized (clientHandlers) {
			for (ClientHandler clientHandler : clientHandlers) {
				try {
					clientHandler.io.send(message);
				} catch (IOException e) {
					log().warn("Problem writing message. " + e.getMessage());
					e.printStackTrace();
				}
			}
		}
	}

	public void shutdown() {
		if (stopRequested) {
			log().info(
					"Shutdown already requested; ignoring duplicate request.");
		} else {
			stopRequested = true;
			log().debug("Server.shutdown()-- broadcasting server going down");
			broadcast(new Message.ServerGoingDown());
			try {
				connector.shutdown();
			} catch (IOException ioe) {
				ioe.printStackTrace();
			}
			for (ClientHandler handler : clientHandlers) {
				handler.cancel();
			}
		}
	}

	public boolean wasShutdownRequested() {
		return stopRequested;
	}

	private void startPingServicesOn(final ClientHandler handler) {
		if (handler.io.isNetworked()) {
			PingService.Server pingServer = PingService
					.createServer(handler.io);
			handler.onReceived().connect(pingServer);
			pingServer.onFailure(new UnitSlot() {
				@Override
				public void onEmit() {
					log().error(" --- PING FAILURE on client " + handler);
					broadcast(new Message.ServerGoingDown());
					shutdown();
				}
			});
			log().debug("Server is starting the ping service.");
			executorService.execute(pingServer);
		}
	}

	public void startGameWithCurrentClients() {
		try {
			stopAcceptingNewClientConnections();
		} catch (IOException e) {
			log().debug(
					"Problem shutting down connection accepter: "
							+ e.getMessage());
		}
		enterState(inGameState);
	}

	private void stopAcceptingNewClientConnections() throws IOException {
		stopRequested = true;
		connector.shutdown();		
	}

	private interface State {
		void onReceive(MessageReceivedEvent event);

		void onEnter();

		void onExit();

		abstract class AbstractState implements State {

			@Override
			public void onEnter() {
			}

			@Override
			public void onExit() {
			}

		}
	}

	private void enterState(State state) {
		this.state.onExit();
		this.state = state;
		state.onEnter();
	}

	private final State lobbyState = new State.AbstractState() {
		@Override
		public void onReceive(MessageReceivedEvent event) {
			event.message.accept(messageProcessor, event.handler);
		}

		private Message.Visitor messageProcessor = new Message.Visitor.Adapter() {
			@Override
			public void visit(Introduce introduce, Object... args) {
				ClientHandler handler = (ClientHandler) args[0];
				synchronized (clientHandlers) {
					clientHandlers.add(handler);
				}
				List<ClientId> clientIdList = makeClientIdList();
				Message.Welcome welcome = new Message.Welcome(clientIdList);
				broadcast(welcome);
				startPingServicesOn(handler);
			}

			private List<ClientId> makeClientIdList() {
				List<ClientId> list = Lists.newArrayList();
				synchronized (clientHandlers) {
					for (ClientHandler handler : clientHandlers) {
						list.add(handler.id());
					}
				}
				return list;
			}

			@Override
			public void visit(VersionMismatch versionMismatch, Object... args) {
				ClientHandler handler = (ClientHandler) args[0];
				handler.cancel();
			}

			@Override
			public void visit(RequestStartGame request, Object... args) {
				startGameWithCurrentClients();
			}
		};

	};

	private final State inGameState = new InGameState();

	private final class InGameState extends State.AbstractState {

		private Scoreboard scoreboard = new Scoreboard();

		private Mission mission;

		private final Visitor visitor = new CautiousVisitor() {

			@Override
			public void visit(RequestAdvanceToScenarioSetup request,
					Object... args) {
				Scenario scenario = mission.scenario();
				broadcast(new Message.AdvanceToScenarioSetup(scenario));
			}

			@Override
			public void visit(RequestStartScenario request, Object... args) {
				broadcast(new Message.StartScenario(request));
			}

			@Override
			public void visit(ReportScore reportScore, Object... args) {
				ClientHandler sender = (ClientHandler) args[0];
				scoreboard.put(sender.id(), reportScore.score);
				if (allClientsHaveReportedScore()) {
					Score total = scoreboard.sum();
					Result result = mission.evaluateCurrentScenario(total);
					result.accept(new Mission.Result.Visitor() {
						@Override
						public void visit(ScenarioSuccess success) {
							Scenario nextScenario = mission.scenario();
							broadcast(new Message.ScenarioFinishedWithSuccess(
									scoreboard, nextScenario));
						}

						@Override
						public void visit(MissionSuccess success) {
							broadcast(new Message.ScenarioFinishedSuccesfullyEndingMission(
									scoreboard, mission.conclusion));
						}

						@Override
						public void visit(Failure failure) {
							broadcast(new Message.ScenarioFinishedInFailure(
									scoreboard));
						}
					});
					scoreboard = new Scoreboard();
				}
			}

			@Override
			public void visit(RequestStartGame request, Object... args) {
				enterState(inGameState);
			}

			private boolean allClientsHaveReportedScore() {
				return scoreboard.size() == clientHandlers.size();
			}
		};

		@Override
		public void onEnter() {
			log().debug("Entering inGame state");
			if (mission == null) {
				mission = MissionFactory.createWithNumberOfPlayers(
						clientHandlers.size()).createDefaultMission();
			}
			broadcast(new Message.StartMission(mission.introduction));
		}

		@Override
		public void onExit() {
			scoreboard.clear();
		}

		@Override
		public void onReceive(MessageReceivedEvent event) {
			event.message.accept(visitor, event.handler);
		}
	};

	@Override
	public String toString() {
		return MoreObjects.toStringHelper(this).toString();
	}

	public void broadcastNewsOfMyDemise() {
		broadcast(new Message.ServerGoingDown());
	}
}
