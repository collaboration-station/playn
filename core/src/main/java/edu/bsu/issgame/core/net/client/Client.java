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
package edu.bsu.issgame.core.net.client;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;
import static playn.core.PlayN.log;

import java.io.EOFException;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ExecutorService;

import react.Connection;
import react.Signal;
import react.SignalView;
import react.Slot;
import react.UnitSignal;
import react.UnitSlot;
import react.Value;

import com.google.common.base.MoreObjects;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import edu.bsu.issgame.core.MinigameType;
import edu.bsu.issgame.core.PlayerMinigameMap;
import edu.bsu.issgame.core.Score;
import edu.bsu.issgame.core.Scoreboard;
import edu.bsu.issgame.core.Settings;
import edu.bsu.issgame.core.mission.Exposition;
import edu.bsu.issgame.core.mission.Scenario;
import edu.bsu.issgame.core.net.ClientId;
import edu.bsu.issgame.core.net.Message;
import edu.bsu.issgame.core.net.Message.AdvanceToScenarioSetup;
import edu.bsu.issgame.core.net.Message.Hello;
import edu.bsu.issgame.core.net.Message.LogVerbosityLevel;
import edu.bsu.issgame.core.net.Message.ScenarioFinishedInFailure;
import edu.bsu.issgame.core.net.Message.ScenarioFinishedSuccesfullyEndingMission;
import edu.bsu.issgame.core.net.Message.ScenarioFinishedWithSuccess;
import edu.bsu.issgame.core.net.Message.ServerGoingDown;
import edu.bsu.issgame.core.net.Message.StartMission;
import edu.bsu.issgame.core.net.Message.StartScenario;
import edu.bsu.issgame.core.net.Message.Welcome;
import edu.bsu.issgame.core.net.MessageIO;
import edu.bsu.issgame.core.net.PingService;
import edu.bsu.issgame.core.util.BlockingReadOperationInterruptedException;

public class Client implements Runnable {

	public static Builder withIO(MessageIO io) {
		return new Builder(io);
	}

	public static final class Builder {
		private final MessageIO io;
		private int versionCode;

		private Builder(MessageIO io) {
			this.io = checkNotNull(io);
		}

		public Client withVersionCode(int versionCode) {
			this.versionCode = versionCode;
			return new Client(this);
		}
	}

	public final UUID uuid;
	private final MessageIO messageIO;
	private boolean cancelled = false;

	private Signal<Iterable<ClientId>> onClientsUpdated = Signal.create();
	private Signal<Exposition> gameStarted = Signal.create();
	private Signal<Scenario> scenarioSetup = Signal.create();
	private Signal<PlayerMinigameMap> scenarioStarted = Signal.create();
	private Signal<Message.ScenarioFinishedWithSuccess> scenarioFinishedWithSuccess = Signal
			.create();
	private UnitSignal sentVersionMismatch = new UnitSignal();
	private UnitSignal serverDown = new UnitSignal();
	private Signal<Scoreboard> scenarioFailed = Signal.create();
	private Signal<ScenarioFinishedSuccesfullyEndingMission> missionComplete = Signal
			.create();
	private List<ClientId> connectedClients = Lists.newArrayList();
	private Value<String> expeditionName = Value.create(null);
	private final int versionCode;
	private final PingService.Client pingClient;

	private Message.Visitor messageHandler = new Message.Visitor.Adapter() {

		@Override
		public void visit(Welcome welcome, Object... args) {
			connectedClients = ImmutableList.copyOf(welcome.clients);
			onClientsUpdated.emit(connectedClients);
		}

		@Override
		public void visit(Hello hello, Object... args) {
			try {
				checkState(uuid != null, "UUID must not be null.");
				expeditionName.update(hello.expeditionName);
				if (hello.verisonCode != versionCode) {
					messageIO.send(new Message.VersionMismatch());
					sentVersionMismatch.emit();
				} else {
					messageIO.send(new Message.Introduce(uuid));
				}
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}

		@Override
		public void visit(StartMission startGame, Object... args) {
			gameStarted.emit(startGame.introduction);
		}

		@Override
		public void visit(AdvanceToScenarioSetup advanceToScenarioSetup,
				Object... args) {
			scenarioSetup.emit(advanceToScenarioSetup.scenario);
		}

		@Override
		public void visit(StartScenario startScenario, Object... args) {
			scenarioStarted.emit(startScenario.map);
		}

		@Override
		public void visit(ScenarioFinishedWithSuccess message, Object... args) {
			checkNotNull(message.nextScenario);
			scenarioFinishedWithSuccess.emit(message);
		}

		@Override
		public void visit(ScenarioFinishedSuccesfullyEndingMission finished,
				Object... args) {
			missionComplete.emit(finished);
		}

		@Override
		public void visit(ScenarioFinishedInFailure failure, Object... args) {
			scenarioFailed.emit(failure.scoreboard);
		}

		@Override
		public void visit(ServerGoingDown serverGoingDown, Object... args) {
			pingClient.stop();
			serverDown.emit();
			cancelled = true;
		}

	};

	private Client(Builder builder) {
		this.uuid = UUID.randomUUID();
		this.messageIO = builder.io;
		this.versionCode = builder.versionCode;
		pingClient = PingService.createClient(messageIO);
		pingClient.onFailure(new UnitSlot() {
			@Override
			public void onEmit() {
				log().error(" +++ CLIENT PING FAILURE +++");
				serverDown.emit();
			}
		});
	}

	public void startPingClient(ExecutorService executorService) {
		checkNotNull(executorService);
		if (messageIO.isNetworked()) {
			log().debug("Client starting the ping service.");
			executorService.execute(pingClient);
		} else {
			log().debug(
					"Non-networked client; ignoring request to start ping client");
		}
	}

	@Override
	public void run() {
		log().debug("Client started");
		while (!cancelled) {
			try {
				Message message = messageIO.read();
				receive(message);
			} catch (EOFException e) {
				log().debug("Stream closed. Terminating.");
				cancelled = true;
			} catch (BlockingReadOperationInterruptedException e) {
				log().debug(
						"Client got blocking read interrupt; probably cancelled.");
			} catch (IOException e) {
				if (!cancelled) {
					log().debug("Problem reading message: " + e.getMessage());
					log().debug("Stopping client.");
					break;
				} else {
					log().debug(
							"Client cancelled so ignoring ioexception: "
									+ e.getMessage());
				}
			} catch (ClassNotFoundException e) {
				throw new RuntimeException(e);
			}
		}
		log().debug("Client finished.");
	}

	void receive(Message message) throws IOException {
		if (shouldLog(message)) {
			log().debug("Client received " + message);
		}
		message.accept(pingClient.visitor);
		message.accept(messageHandler);
	}

	private boolean shouldLog(Message message) {
		return Settings.SETTINGS.get(Settings.LOG_TRACE_MESSAGES)
				|| message.logVerbosityLevel() == LogVerbosityLevel.DEBUG;
	}

	public void cancel() {
		log().debug("Cancelling the client");
		cancelled = true;
		messageIO.cancel();
	}

	public void requestStartMission() throws IOException {
		messageIO.send(Message.RequestStartMission.instance());
		requestStartGame();
	}

	public void requestStartGame() throws IOException {
		messageIO.send(new Message.RequestStartGame());
	}

	public SignalView<Void> onVersionMismatch() {
		return sentVersionMismatch;
	}

	public SignalView<Iterable<ClientId>> onClientsUpdated() {
		return onClientsUpdated;
	}

	public SignalView<Exposition> onGameStarted() {
		return gameStarted;
	}

	public SignalView<Scenario> onScenarioSetup() {
		return scenarioSetup;
	}

	public SignalView<PlayerMinigameMap> onScenarioStarted() {
		return scenarioStarted;
	}

	public SignalView<Message.ScenarioFinishedWithSuccess> onScenarioFinishedWithSuccess() {
		return scenarioFinishedWithSuccess;
	}

	public SignalView<Scoreboard> onScenarioFailed() {
		return scenarioFailed;
	}

	public SignalView<ScenarioFinishedSuccesfullyEndingMission> onMissionComplete() {
		return missionComplete;
	}

	public void requestStartScenario(PlayerMinigameMap playerGameMap)
			throws IOException {
		checkArgument(coversAllConnectedClients(playerGameMap));
		messageIO.send(new Message.RequestStartScenario(playerGameMap));
		log().debug("Calling the requestStartScenario funness.");
	}

	private boolean coversAllConnectedClients(
			Map<ClientId, MinigameType> playerGameMap) {
		Set<ClientId> clients = Sets.newHashSet(connectedClients);
		clients.removeAll(playerGameMap.keySet());
		return clients.isEmpty();
	}

	public String getExpeditionName() {
		return expeditionName.get();
	}

	public void reportScore(Score score) throws IOException {
		messageIO.send(new Message.ReportScore(score));
	}

	public List<ClientId> getConnectedClients() {
		return ImmutableList.copyOf(connectedClients);
	}

	public SignalView<Void> onServerDown() {
		return serverDown;
	}

	@Override
	public String toString() {
		return MoreObjects.toStringHelper(this).toString();
	}

	public void advanceToScenarioSetup() throws IOException {
		messageIO.send(new Message.RequestAdvanceToScenarioSetup());
	}

	public void stopPingClient() {
		pingClient.stop();
	}

	public Connection onExpeditionNameChange(Slot<String> slot) {
		return expeditionName.connect(slot);
	}
}
