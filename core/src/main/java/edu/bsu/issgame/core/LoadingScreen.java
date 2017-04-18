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
import static playn.core.PlayN.assets;
import static playn.core.PlayN.graphics;
import playn.core.AssetWatcher;
import playn.core.Image;
import tripleplay.game.ScreenStack;
import tripleplay.game.trans.FadeTransition;
import tripleplay.ui.Background;
import tripleplay.ui.Style;
import tripleplay.ui.layout.AbsoluteLayout;
import edu.bsu.issgame.core.cardmatch.CardType;
import edu.bsu.issgame.core.net.NetworkInterface;

public final class LoadingScreen extends AbstractGameScreen {

	private static final String TEAM_LOGO = "images/team_logo.png";
	private static final float FADE_DURATION_MS = 1000f;

	public LoadingScreen(ScreenStack screenStack, NetworkInterface net) {
		super(null);
		this.screenStack = checkNotNull(screenStack);
		this.net = checkNotNull(net);
		initBackground();
		goToPlayingScreenAfterAllAssetsAreLoaded(screenStack);
	}

	private void initBackground() {
		Image image = assets().getImageSync(TEAM_LOGO);
		iface.createRoot(new AbsoluteLayout(), CustomStyleSheet.instance(),
				layer)//
				.setSize(graphics().width(), graphics().height())//
				.setStyles(Style.BACKGROUND.is(Background.image(image)));
	}

	private void goToPlayingScreenAfterAllAssetsAreLoaded(
			final ScreenStack screenStack) {
		AssetWatcher watcher = new AssetWatcher(new AssetWatcher.Listener() {
			@Override
			public void error(Throwable e) {
				throw new RuntimeException(e);
			}

			@Override
			public void done() {
				screenStack.push(new WelcomeScreen(LoadingScreen.this),
						new FadeTransition(screenStack).duration(FADE_DURATION_MS));
			}
		});
		for (CardType type : CardType.values()) {
			watcher.add(type.image);
		}
		for (GameImage gameImage : GameImage.values()) {
			watcher.add(gameImage.image);
		}
		for (GameSound gameSound : GameSound.values()) {
			watcher.add(gameSound.sound);
		}
		for (CountryFlagImage countryFlagType : CountryFlagImage.values()) {
			watcher.add(countryFlagType.image);
		}
		watcher.start();
	}
}
