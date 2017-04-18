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
import static playn.core.PlayN.keyboard;
import static playn.core.PlayN.log;
import static playn.core.PlayN.pointer;
import playn.core.Image;
import playn.core.ImageLayer;
import playn.core.Key;
import playn.core.Keyboard;
import playn.core.Keyboard.Event;
import playn.core.Layer;
import playn.core.Pointer;
import react.Slot;
import tripleplay.game.trans.FadeTransition;
import tripleplay.ui.Background;
import tripleplay.ui.Button;
import tripleplay.ui.Constraints;
import tripleplay.ui.Group;
import tripleplay.ui.Root;
import tripleplay.ui.Shim;
import tripleplay.ui.Style;
import tripleplay.ui.layout.AxisLayout;
import tripleplay.util.PointerInput;
import tripleplay.util.Timer;
import edu.bsu.issgame.core.assetmgt.Jukebox;
import edu.bsu.issgame.core.assetmgt.LoadableImage;
import edu.bsu.issgame.core.assetmgt.Jukebox.Track;
import edu.bsu.issgame.core.intro.IntroScreen;
import edu.bsu.issgame.core.sequence.SequenceMatchScreen;

public final class WelcomeScreen extends AbstractGameScreen {

	private static final Key DEBUG_MODE_KEY = Key.BACKQUOTE;
	private final boolean allowSkippingToMinigame = false;

	private Root root;
	private Button playButton;
	private Timer ambientSoundTimer = new Timer();

	private final Slot<Button> playGameAction = new Slot<Button>() {
		@Override
		public void onEmit(Button event) {
			playButton.setEnabled(false);
			screenStack.replace(new RoleSelectScreen(WelcomeScreen.this),
					screenStack.slide());
		}
	};

	private final Slot<Button> startIntroductionAction = new Slot<Button>() {
		@Override
		public void onEmit(Button event) {
			screenStack.replace(new IntroScreen(WelcomeScreen.this));
		}
	};

	private final Slot<Button> goToCreditsScreenAction = new Slot<Button>() {
		@Override
		public void onEmit(Button event) {
			screenStack.replace(new CreditsScreen(WelcomeScreen.this),
					new FadeTransition(screenStack));
		}
	};

	public WelcomeScreen(AbstractGameScreen previous) {
		super(previous);
		country.update(null);
		client = null;
		miniGameMap = null;
		initTPUI();
		initBackground();
		initLogo();
		configureDebugModeHook();
		if (allowSkippingToMinigame) {
			log().warn("Skipping to minigame is enabled.");
			configureTapCornerToSkipToMinigame();
		}
	}

	private void initTPUI() {
		Group buttonGroup = new Group(AxisLayout.vertical()//
				.gap((int) percentOfScreenHeight(0.08f)))
				.add(playButton = makeButton(
						UIProperties.instance().getProperty("button.play"))//
						.onClick(playGameAction))
				//
				.add(makeButton("Intro")//
						.onClick(startIntroductionAction))
				.add(makeButton("Credits").onClick(goToCreditsScreenAction));

		root = iface.createRoot(AxisLayout.vertical(),//
				CustomStyleSheet.instance(), layer)//
				.setSize(graphics().width(), graphics().height())//
				.add(new Shim(0, graphics().height() / 2))//
				.add(buttonGroup);
	}

	private void initLogo() {
		final Image logo = LoadableImage.TITLE.loadSync();
		final ImageLayer logoLayer = graphics().createImageLayer(logo);
		logoLayer.setOrigin(logoLayer.width() / 2, logoLayer.height() / 2);
		final float desiredWidth = graphics().width() * 0.7f;
		final float scale = desiredWidth / logoLayer.width();
		logoLayer.setScale(scale);
		logoLayer.setTranslation(graphics().width() / 2,
				graphics().height() * 0.33f);
		layer.add(logoLayer);
	}

	private Button makeButton(String text) {
		return new Button(text)//
				.setConstraint(Constraints
						.fixedWidth(graphics().width() * 0.2f));
	}

	private void initBackground() {
		checkNotNull(root, "Initialize root before calling this method.");
		Image bg = LoadableImage.WELCOME_BG.loadSync();
		Image scaledBackgroundImage = scale.getBackgroundSubImage(bg);
		root.addStyles(Style.BACKGROUND.is(Background
				.image(scaledBackgroundImage)));
	}

	private void configureDebugModeHook() {
		keyboard().setListener(new Keyboard.Adapter() {
			@Override
			public void onKeyDown(Event event) {
				if (event.key() == DEBUG_MODE_KEY) {
					keyboard().setListener(new DebugMode(WelcomeScreen.this));
				}
			}
		});
	}

	private void configureTapCornerToSkipToMinigame() {
		final float size = 100f;
		PointerInput pInput = new PointerInput();
		pointer().setListener(pInput.plistener);
		Layer.HasSize trigger = graphics().createGroupLayer(size, size);
		trigger.setOrigin(size, 0);
		layer.addAt(trigger, graphics().width(), 0);
		pInput.register(trigger, new Pointer.Adapter() {
			private int tapsRemaining = 3;

			@Override
			public void onPointerStart(playn.core.Pointer.Event event) {
				if (--tapsRemaining == 0) {
					screenStack
							.push(new SequenceMatchScreen(WelcomeScreen.this));
				}
			}
		});
	}

	@Override
	public void wasShown() {
		setUpMusic();
		playButton.setEnabled(true);
	}

	private void setUpMusic() {
		Jukebox.instance().loop(Track.WELCOME_MUSIC);
	}

	@Override
	public void update(int delta) {
		super.update(delta);
		ambientSoundTimer.update();
	}
}
