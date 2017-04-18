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
package edu.bsu.issgame.core;

import static com.google.common.base.Preconditions.checkNotNull;
import static playn.core.PlayN.graphics;
import static playn.core.PlayN.log;
import static tripleplay.ui.layout.TableLayout.COL;

import java.io.IOException;
import java.util.List;

import playn.core.Image;
import react.Connection;
import react.Slot;
import tripleplay.ui.Background;
import tripleplay.ui.Button;
import tripleplay.ui.Element;
import tripleplay.ui.Group;
import tripleplay.ui.Label;
import tripleplay.ui.Root;
import tripleplay.ui.Shim;
import tripleplay.ui.Style;
import tripleplay.ui.layout.AxisLayout;
import tripleplay.ui.layout.TableLayout;

import com.google.common.collect.Lists;

import edu.bsu.issgame.core.assetmgt.Jukebox;
import edu.bsu.issgame.core.mission.Exposition;
import edu.bsu.issgame.core.net.ClientId;
import edu.bsu.issgame.core.net.client.Client;

public final class AwaitingPlayersScreen extends AbstractGameScreen {

	private Group clientGroup;
	private Button startButton;
	private final List<Connection> connections = Lists.newArrayList();
	private Image scaledBackgroundImage;
	private Label expeditionLabel = new Label(" ");
	private Label commanderInstructionLabel;
	private Group buttonGroup;
	private Root root;

	private Slot<Button> startAction = new Slot<Button>() {
		@Override
		public void onEmit(Button event) {
			startButton.setEnabled(false);
			try {
				client.requestStartMission();
			} catch (IOException ioe) {
				throw new RuntimeException(ioe);
			}
		}
	};

	public AwaitingPlayersScreen(AbstractGameScreen previous) {
		super(previous);
		scenario = null;
		if (client != null) {
			log().debug("Client was already created; I must not be the host.");
			watchForGameStart();
			configureExpeditionLabelWatcher();
		}
		connections.add(net.onNetworkError().connect(new Slot<String>() {
			@Override
			public void onEmit(String event) {
				cancel();
			}
		}));
		Connection clientStartedConnection = net.onClientCreated().connect(
				new Slot<Client>() {
					@Override
					public void onEmit(final Client client) {
						log().debug("Got client start signal.");
						AwaitingPlayersScreen.this.client = checkNotNull(client);
						Connection clientListUpdateConnection = client
								.onClientsUpdated().connect(
										new Slot<Iterable<ClientId>>() {
											@Override
											public void onEmit(
													Iterable<ClientId> event) {
												updateClientGroupOnUiThread();
												updateWhetherStartButtonCanBeClickedOrNot();
											}
										});
						connections.add(clientListUpdateConnection);
						configureExpeditionLabelWatcher();
						watchForGameStart();
					}
				});
		connections.add(clientStartedConnection);

		Image backgroundImage = GameImage.LOBBY_BG.image;
		scaledBackgroundImage = scale.getBackgroundSubImage(backgroundImage);
		if (isCommander) {
			createHostingInterface();
		} else {
			createJoiningInterface();
		}

		updateClientGroupOnUiThread();
		enableFlagAssignmentAnimation();
	}

	private void configureExpeditionLabelWatcher() {
		if (client.getExpeditionName() != null) {
			updateExpeditionLabelOnUIThread();
		} else {
			Connection labelWatcher = client
					.onExpeditionNameChange(new Slot<String>() {
						@Override
						public void onEmit(String event) {
							updateExpeditionLabelOnUIThread();
						}
					});
			connections.add(labelWatcher);
		}
	}

	private void updateExpeditionLabelOnUIThread() {
		log().debug(
				"I am on top of the screenstack: "
						+ (screenStack.top() == this));
		anim.action(new Runnable() {
			@Override
			public void run() {
				expeditionLabel.addStyles(Style.FONT.is(GameFont.TITLE.font));
				expeditionLabel.text.update(client.getExpeditionName());
			}
		});
	}

	private void createJoiningInterface() {
		iface.createRoot(AxisLayout.vertical(), CustomStyleSheet.instance(),
				layer)
				.setSize(graphics().width(), graphics().height())
				.addStyles(
						Style.BACKGROUND.is(Background
								.image(scaledBackgroundImage)))
				.add(expeditionLabel)
				.add(clientGroup = new Group(new TableLayout(COL
						.minWidth(graphics().width() / 2).fixed().alignLeft())))//
				.add(new Group(AxisLayout.horizontal()).add(new Label(
						"Awaiting Commander")));
	}

	private void createHostingInterface() {
		buttonGroup = new Group(AxisLayout.horizontal());//
		addButtonsToButtonGroup();
		commanderInstructionLabel = new Label(
				"Tell your crew to join the expedition.");
		root = iface
				.createRoot(AxisLayout.vertical(), CustomStyleSheet.instance(),
						layer)
				.setSize(graphics().width(), graphics().height())
				.addStyles(
						Style.BACKGROUND.is(Background
								.image(scaledBackgroundImage)))
				.add(expeditionLabel)
				.add(clientGroup = new Group(new TableLayout(COL
						.minWidth(graphics().width() / 2).fixed().alignLeft())))//
				.add(commanderInstructionLabel)//
				.add(buttonGroup);
	}

	private void addButtonsToButtonGroup() {
		buttonGroup.add(getStartButton())//
				.add(new Shim(graphics().height() * .05f, 0))//
				.add(getCancelButton());
	}

	private Element<?> getCancelButton() {
		return new Button(UIProperties.instance().getProperty("button.cancel"))//
				.onClick(new Slot<Button>() {
					@Override
					public void onEmit(Button event) {
						cancel();
					}
				});
	}

	private Element<?> getStartButton() {
		startButton = new Button(UIProperties.instance().getProperty(
				"button.begin"))//
				.onClick(startAction)//
				.setEnabled(false);
		return startButton;
	}

	private void watchForGameStart() {
		checkNotNull(client,
				"This may only be called after client has been set.");
		Connection onGameStartConnection = client.onGameStarted().connect(
				new Slot<Exposition>() {
					@Override
					public void onEmit(Exposition introduction) {
						Jukebox.instance().stop();
						MissionIntroductionScreen screen = new MissionIntroductionScreen(
								AwaitingPlayersScreen.this, introduction);
						screenStack.push(screen, screenStack.slide());
					}
				});
		connections.add(onGameStartConnection);
	}

	private void updateClientGroupOnUiThread() {
		anim.action(new Runnable() {
			public void run() {
				if (client != null) {
					clientGroup.removeAll();
					List<ClientId> connectedClients = client
							.getConnectedClients();
					log().debug("Found " + connectedClients.size() + " clients");
					for (ClientId clientId : client.getConnectedClients()) {
						if (client.uuid.equals(clientId.uuid)) {
							clientGroup.add(createCountryLabel(clientId));
							updateCountryIfNotAlreadySet(clientId.country);
						} else {
							clientGroup.add(createCountryLabel(clientId));
						}
					}
					frameCountryNames();
					for (int i = 0; i < (4 - client.getConnectedClients()
							.size()); i++) {
						clientGroup.add(new Label(" "));
					}
					handleCommanderInstructionLabel();
				} else {
					clientGroup.add(new Label("Waiting..."));
				}
			}

			private void handleCommanderInstructionLabel() {
				if (isCommander) {
					if (client.getConnectedClients().size() == 2) {
						root.remove(commanderInstructionLabel);
						root.remove(buttonGroup);
						root.add(new Label(" "))//
								.add(buttonGroup);
					}
				}
			}

			private Element<?> createCountryLabel(ClientId clientId) {
				return CountryLabelFactory.instance().createCountryLabel(
						clientId.country);
			}

			private void updateCountryIfNotAlreadySet(Country myCountry) {
				if (country.get() != myCountry) {
					country.update(myCountry);
				}
			}

			private void frameCountryNames() {
				if (!client.getConnectedClients().isEmpty()) {
					clientGroup.add(0, new Label("Commander")
							.addStyles(Style.FONT.is(GameFont.TITLE.font)));
					clientGroup.add(2, new Label("Crew").addStyles(Style.FONT
							.is(GameFont.TITLE.font)));
				}
			}
		});
	}

	private void updateWhetherStartButtonCanBeClickedOrNot() {
		if (isCommander && (client.getConnectedClients().size() > 1)) {
			startButton.setEnabled(true);
		}

	}

	public void cancel() {
		net.shutdown();
		if (client != null) {
			client.cancel();
		}
		screenStack.replace(new RoleSelectScreen(AwaitingPlayersScreen.this),
				screenStack.slide());
	}

	@Override
	public void wasHidden() {
		super.wasHidden();
		for (Connection connectionsToRemoveOnExit : connections) {
			connectionsToRemoveOnExit.disconnect();
		}
		connections.clear();
	}
}
