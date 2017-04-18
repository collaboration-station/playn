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
import pythagoras.f.Point;
import react.Connection;
import react.Slot;
import react.UnitSlot;
import tripleplay.game.trans.SlideTransition;
import tripleplay.ui.Background;
import tripleplay.ui.Button;
import tripleplay.ui.Label;
import tripleplay.ui.Shim;
import tripleplay.ui.Style;
import tripleplay.ui.layout.AbsoluteLayout;
import tripleplay.ui.layout.AxisLayout;
import edu.bsu.issgame.core.net.ClientId;
import edu.bsu.issgame.core.net.client.Client;

public class SearchingForHostScreen extends AbstractGameScreen {
	private static final String CANCEL_TEXT_KEY = "search.button.cancel";
	private Connection clientStartedConnection;
	private Connection networkErrorConnection;
	private Label searchingLabel = new Label(UIProperties.instance()
			.getProperty("search.text"))//
			.addStyles(Style.FONT.is(GameFont.TITLE.font),
					Style.COLOR.is(Palette.ORANGE.color));
	private boolean hasTransitioned = false;

	protected SearchingForHostScreen(AbstractGameScreen previous) {
		super(previous);

		iface.createRoot(AxisLayout.vertical(), CustomStyleSheet.instance(),
				layer)
				.setSize(graphics().width(), graphics().height())
				.addStyles(
						Style.BACKGROUND.is(Background
								.image(GameImage.SEARCHING_FOR_HOST_BG.image)))
				.add(new Shim(0f, percentOfScreenHeight(0.15f)))
				.add(searchingLabel);

		iface.createRoot(new AbsoluteLayout(), CustomStyleSheet.instance(),
				layer)
				.setSize(graphics().width(), graphics().height())
				.add(AbsoluteLayout.at(makeCancelButton(), //
						new Point(graphics().width() * 0.75f, graphics()
								.height() * 0.85f)));

		clientStartedConnection = net.onClientCreated().connect(
				new Slot<Client>() {
					@Override
					public void onEmit(final Client client) {
						log().debug("Got client start signal.");
						SearchingForHostScreen.this.client = checkNotNull(client);
						client.onVersionMismatch().connect(new UnitSlot() {
							@Override
							public void onEmit() {
								log().debug(
										"Client has the wrong version number!!!");
								client.cancel();
								net.shutdown();
								Popup.on(SearchingForHostScreen.this)
										.show("Wrong Version! Please Update Game!!!")//
										.onSuccess(new UnitSlot() {
											@Override
											public void onEmit() {
												screenStack
														.replace(
																new WelcomeScreen(
																		SearchingForHostScreen.this),
																new SlideTransition(
																		screenStack)
																		.right());
											}
										});
							}
						});

						client.onClientsUpdated().connect(
								new Slot<Iterable<ClientId>>() {
									@Override
									public void onEmit(Iterable<ClientId> event) {
										clientStartedConnection.disconnect();
										if (!hasTransitioned) {
											screenStack
													.replace(
															new AwaitingPlayersScreen(
																	SearchingForHostScreen.this),
															screenStack.slide()
																	.up());
											hasTransitioned = true;
										} else {
											screenStack
													.replace(new AwaitingPlayersScreen(
															SearchingForHostScreen.this));
										}
									}
								});
					}
				});

		networkErrorConnection = net.onNetworkError().connect(
				new Slot<String>() {
					@Override
					public void onEmit(String event) {
						cancel();
					}
				});
	}

	private Button makeCancelButton() {
		return new Button(UIProperties.instance().getProperty(CANCEL_TEXT_KEY))//
				.onClick(new Slot<Button>() {
					@Override
					public void onEmit(Button event) {
						cancel();
					}
				});
	}

	@Override
	public void update(int delta) {
		super.update(delta);
	}

	@Override
	public void wasRemoved() {
		super.wasRemoved();
		clientStartedConnection.disconnect();
		networkErrorConnection.disconnect();
	}

	private void cancel() {
		clientStartedConnection.disconnect();
		networkErrorConnection.disconnect();
		net.shutdown();
		if (client != null) {
			client.cancel();
		}
		screenStack.replace(new RoleSelectScreen(SearchingForHostScreen.this),
				screenStack.slide());
	}

}
