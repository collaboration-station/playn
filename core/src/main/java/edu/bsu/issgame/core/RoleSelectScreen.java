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
import edu.bsu.issgame.core.assetmgt.LoadableImage;
import playn.core.Image;
import react.Slot;
import tripleplay.game.Screen;
import tripleplay.ui.Background;
import tripleplay.ui.Button;
import tripleplay.ui.Constraints;
import tripleplay.ui.Group;
import tripleplay.ui.Label;
import tripleplay.ui.Root;
import tripleplay.ui.Shim;
import tripleplay.ui.Style;
import tripleplay.ui.layout.AxisLayout;

public class RoleSelectScreen extends AbstractGameScreen {

	private Root root;
	private Button hostButton;
	private Button joinButton;

	private final Slot<Button> hostGameAction = new Slot<Button>() {
		@Override
		public void onEmit(Button event) {
			isCommander = true;
			hostButton.setEnabled(false);
			net.startGameService()//
					.onFailure(new Slot<Throwable>() {
						@Override
						public void onEmit(Throwable cause) {
							throw new IllegalStateException(
									"Game failed to start because "
											+ cause.getMessage(), cause);
						}
					})//
					.onSuccess(new Slot<Boolean>() {
						@Override
						public void onEmit(Boolean playerEnabledBluetooth) {
							if (playerEnabledBluetooth) {
								screenStack.replace(new AwaitingPlayersScreen(
										RoleSelectScreen.this), screenStack
										.slide());
							} else {
								screenStack.replace(new RoleSelectScreen(
										RoleSelectScreen.this),//
										screenStack.slide());
							}
						}
					});
		}
	};
	private final Slot<Button> joinGameAction = new Slot<Button>() {
		@Override
		public void onEmit(Button event) {
			isCommander = false;
			joinButton.setEnabled(false);
			Screen searchingForHostScreen = new SearchingForHostScreen(
					RoleSelectScreen.this);
			screenStack.replace(searchingForHostScreen, screenStack.slide()
					.down());
			net.discoverGameService();
		}
	};

	protected RoleSelectScreen(AbstractGameScreen previous) {
		super(previous);
		isCommander = false;
		country.update(null);
		initTPUI();
		initBackground();
	}

	private void initTPUI() {
		Group buttonGroup = new Group(AxisLayout.vertical().gap(
				(int) percentOfScreenHeight(0.02f)))//
				.add(hostButton = makeButton("button.host")//
						.onClick(hostGameAction))//
				.add(joinButton = makeButton("button.join")//
						.onClick(joinGameAction))//
				.add(makeButton("button.cancel").onClick(new Slot<Button>() {
					@Override
					public void onEmit(Button event) {
						screenStack.replace(new WelcomeScreen(
								RoleSelectScreen.this));
					}
				}));

		Group labelGroup = new Group(AxisLayout.vertical())
				.add(new Shim(0, graphics().height() / 6))
				.add(new Label(UIProperties.instance()//
						.getProperty("label.roleSelect"))//
						.addStyles(Style.TEXT_WRAP.on))
				.add(new Label(UIProperties.instance()//
						.getProperty("label.roleSelect.join"))//
						.addStyles(Style.OUTLINE_WIDTH.is(5f),
								Style.HIGHLIGHT.is(Palette.DARK_BLUE.color),
								Style.TEXT_EFFECT.vectorOutline,
								Style.TEXT_WRAP.on));

		root = iface.createRoot(AxisLayout.vertical(),//
				CustomStyleSheet.instance(), layer)
				.setSize(graphics().width(), graphics().height())
				.add(labelGroup).add(new Shim(0, graphics().height() / 10))//
				.add(buttonGroup);
	}

	private Button makeButton(String text) {
		return new Button(UIProperties.instance().getProperty(text))//
				.setConstraint(Constraints
						.fixedWidth(graphics().width() * 0.40f));
	}

	private void initBackground() {
		checkNotNull(root, "Initialize root before calling this method.");
		Image bg = LoadableImage.WELCOME_BG.loadSync();
		Image scaled_bg_image = scale.getBackgroundSubImage(bg);
		root.addStyles(Style.BACKGROUND.is(Background.image(scaled_bg_image)));
	}
}
