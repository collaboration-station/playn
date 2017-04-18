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
package edu.bsu.issgame.core.sequence;

import static com.google.common.base.Preconditions.checkState;
import static playn.core.PlayN.graphics;
import static playn.core.PlayN.log;
import static playn.core.PlayN.pointer;

import java.util.Map;

import playn.core.Layer;
import playn.core.Pointer;
import playn.core.Pointer.Event;
import pythagoras.f.IPoint;
import pythagoras.f.Point;
import tripleplay.anim.AnimBuilder;
import tripleplay.sound.MultiClip;
import tripleplay.sound.SoundBoard;
import tripleplay.ui.Label;
import tripleplay.ui.Root;
import tripleplay.ui.Shim;
import tripleplay.ui.layout.AxisLayout;
import tripleplay.util.PointerInput;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

import edu.bsu.issgame.core.AbstractGameScreen;
import edu.bsu.issgame.core.CommonGameScreenUI;
import edu.bsu.issgame.core.CustomStyleSheet;
import edu.bsu.issgame.core.GameImage;
import edu.bsu.issgame.core.GameSound;
import edu.bsu.issgame.core.MinigameType;
import edu.bsu.issgame.core.Score;

public class SequenceMatchScreen extends CommonGameScreenUI {
	private static final IPoint percentOfScreen(float w, float h) {
		return new Point(graphics().width() * w, graphics().height() * h);
	}

	private static final ImmutableMap<SequenceItemType, SequenceButton> makeMap(
			SequenceItemType... types) {
		ImmutableMap.Builder<SequenceItemType, SequenceButton> builder = ImmutableMap
				.builder();
		for (SequenceItemType type : types) {
			SequenceButton button = new SequenceButton(type);
			builder.put(type, button);
		}
		return builder.build();
	}

	private static final float DURATION_OF_PRESSED_IMAGE = 600f;
	private static final float SCALE_TWEEN_TIME = 100f;
	private static final float SCALE_MAX = 1.05f;
	private static final float TOP_ROW = 0.30f;
	private static final float BOTTOM_ROW = 0.75f;
	private static final float LEFT = 0.20f;
	private static final float BETWEEN = 0.20f;
	private ImmutableMap<SequenceItemType, IPoint> buttonLocationMap = ImmutableMap
			.of(SequenceItemType.BIOLOGY,
					percentOfScreen(LEFT, TOP_ROW),
					SequenceItemType.EXERCISE,
					percentOfScreen(LEFT + BETWEEN, BOTTOM_ROW),
					SequenceItemType.SLEEP,
					percentOfScreen(LEFT + 2 * BETWEEN, TOP_ROW), //
					SequenceItemType.PHYSICS,
					percentOfScreen(LEFT + 3 * BETWEEN, BOTTOM_ROW));

	private final PointerInput input = new PointerInput();
	private final ImmutableMap<SequenceItemType, SequenceButton> buttonMap = makeMap(
			SequenceItemType.BIOLOGY, //
			SequenceItemType.EXERCISE,//
			SequenceItemType.SLEEP, //
			SequenceItemType.PHYSICS);
	private Sequence sequence;
	private Root root;
	private SoundBoard soundBoard = new SoundBoard();
	private Label watchCloselyLabel = new Label("Watch closely...");
	private Map<SequenceItemType, MultiClip> clipMap = Maps.newHashMap();

	public SequenceMatchScreen(AbstractGameScreen previous) {
		super(previous);
		createRoot();
		setBackground(GameImage.SEQUENCE_MATCHING_BACKGROUND.image);
		placeSequenceButtonsOnScreen();
		sequence = new Sequence().addItem();
		root.add(watchCloselyLabel);
		initializeSoundBoard();
		enterState(pauseBetweenRoundsState);
	}

	private void createRoot() {
		root = iface.createRoot(AxisLayout.vertical(),//
				CustomStyleSheet.instance(), layer)//
				.setSize(graphics().width(), graphics().height());
		root.add(new Shim(15, 15));
	}

	private void initializeSoundBoard() {
		for (SequenceItemType type : SequenceItemType.values()) {
			MultiClip clip = new MultiClip(soundBoard, type.sfx.path, 2, 0.4f);
			clipMap.put(type, clip);
		}
	}

	private void placeSequenceButtonsOnScreen() {
		for (SequenceButton button : buttonMap.values()) {
			IPoint ipoint = buttonLocationMap.get(button.getButtonType());
			layer.addAt(button.getImageLayer(), ipoint.x(), ipoint.y());
			registerInputOn(button);
		}
	}

	private void registerInputOn(final SequenceButton button) {
		input.register(button.getImageLayer(), new Pointer.Adapter() {
			@Override
			public void onPointerStart(Event event) {
				state.onPress(button);
			}

			@Override
			public void onPointerEnd(Event event) {
				state.onRelease(button);
			}
		});
	}

	private void playSfxFor(SequenceItemType buttonType) {
		clipMap.get(buttonType).reserve().play();
	}

	private void updateScore() {
		int delta = TriangularMathUtil.sum(sequence.getLength());
		Score scoreToAdd = Score.create().addScience(delta);
		score.update(score.get().add(scoreToAdd));
	}

	@Override
	protected void setMinigameType() {
		thisMinigame = MinigameType.PATTERN_REPEAT;
	}

	private void disableInput() {
		pointer().setListener(null);
	}

	private void enableInput() {
		pointer().setListener(input.plistener);
	}

	private State state;

	private void enterState(State newState) {
		if (this.state != null) {
			this.state.onExit();
		}
		this.state = newState;
		newState.onEnter();
	}

	private interface State {
		void onEnter();

		void onExit();

		void onPress(SequenceButton button);

		void onRelease(SequenceButton button);
	}

	private abstract class AbstractState implements State {

		private final String name;

		protected AbstractState(String name) {
			this.name = name;
		}

		@Override
		public String toString() {
			return name;
		}

		@Override
		public void onEnter() {
		}

		@Override
		public void onExit() {
		}

		@Override
		public void onPress(SequenceButton button) {
			logWarning(button);
		}

		@Override
		public void onRelease(SequenceButton button) {
			logWarning(button);
		}

		private void logWarning(SequenceButton button) {
			log().warn(
					"Unexpected interaction with " + button + " from state "
							+ toString());
		}
	}

	private final State showSequenceState = new AbstractState(
			"showSequenceState") {

		private final int delayBetweenSequenceItems = 100;
		private final int delayBetweenWatchMessageAndSequenceStart = 500;
		private final Runnable showSequenceAction = new Runnable() {
			@Override
			public void run() {
				showSequence();
			}
		};
		private final Runnable enterYourTurnStateAction = new Runnable() {
			@Override
			public void run() {
				enterState(yourTurnState);
			}
		};
		private AnimBuilder builder;

		@Override
		public void onEnter() {
			AnimBuilder builder = anim.delay(0).then();
			if (sequence.getLength() == 1) {
				builder = builder.tweenAlpha(watchCloselyLabel.layer)//
						.to(1f)//
						.in(250f)//
						.then()//
						.delay(delayBetweenWatchMessageAndSequenceStart)//
						.then()//
						.tweenAlpha(watchCloselyLabel.layer)//
						.to(0f)//
						.in(250f)//
						.then();
			}
			builder.action(showSequenceAction);
		}

		private void showSequence() {
			checkState(builder == null);
			builder = anim.delay(0).then();
			for (SequenceItemType buttonType : sequence.getSequence()) {
				animateButton(buttonType);
			}
			builder.action(enterYourTurnStateAction);
			builder = null;
		}

		private void animateButton(SequenceItemType buttonType) {
			final SequenceButton button = buttonMap.get(buttonType);
			builder = builder//
					.action(new Runnable() {
						@Override
						public void run() {
							button.setPressed();
							playSfxFor(button.getButtonType());
							pulseScale(button.getImageLayer());
						}
					})//
					.then()//
					.delay(DURATION_OF_PRESSED_IMAGE)//
					.then()//
					.action(new Runnable() {
						@Override
						public void run() {
							button.setUnpressed();
						}
					})//
					.then()//
					.delay(delayBetweenSequenceItems)//
					.then();
		}

		private void pulseScale(Layer buttonLayer) {
			final float holdDelay = Math.max(0, DURATION_OF_PRESSED_IMAGE - 2
					* SCALE_TWEEN_TIME);
			anim.tweenScale(buttonLayer)//
					.to(SCALE_MAX)//
					.in(SCALE_TWEEN_TIME)//
					.easeIn()//
					.then()//
					.delay(holdDelay)//
					.then()//
					.tweenScale(buttonLayer)//
					.to(1f)//
					.in(SCALE_TWEEN_TIME)//
					.easeOut();
		}
	};

	private final State pauseBetweenRoundsState = new AbstractState(
			"pauseBetweenRoundsState") {

		private final int DELAY_BETWEEN_ROUNDS = 500;

		private final Runnable enterShowSequenceState = new Runnable() {
			@Override
			public void run() {
				enterState(showSequenceState);
			}
		};

		@Override
		public void onEnter() {
			anim.delay(DELAY_BETWEEN_ROUNDS)//
					.then()//
					.action(enterShowSequenceState);
		}

	};

	private final State yourTurnState = new AbstractState("yourTurnState") {

		@Override
		public void onEnter() {
			activateAllButtons();
			enableInput();
		}

		private void activateAllButtons() {
			for (SequenceButton button : buttonMap.values()) {
				button.setActive();
			}
		}

		@Override
		public void onPress(SequenceButton button) {
			playSfxFor(button.getButtonType());
			button.setPressed();
			button.getImageLayer().setScale(SCALE_MAX);
		}

		@Override
		public void onRelease(SequenceButton button) {
			button.setUnpressed();
			if (sequence.isClickCorrect(button)) {
				handleCorrect();
			} else {
				handleIncorrect();
			}
			anim.tweenScale(button.getImageLayer())//
					.to(1f)//
					.easeOut()//
					.in(SCALE_TWEEN_TIME);
		}

		private void handleCorrect() {
			sequence.incrementPointer();
			if (sequence.isComplete()) {
				updateScore();
				extendSequence();
				disableInput();
			}
		}

		private void extendSequence() {
			sequence.addItem();
			sequence.resetPointer();
			enterState(pauseBetweenRoundsState);
		}

		private void handleIncorrect() {
			enterState(incorrectState);
		}

		@Override
		public void onExit() {
			deactivateAllButtons();
		}

		private void deactivateAllButtons() {
			for (SequenceButton button : buttonMap.values()) {
				button.setInactive();
			}
		}
	};

	private final State incorrectState = new AbstractState("incorrectState") {

		private final Runnable enterPauseBetweenRoundsState = new Runnable() {
			@Override
			public void run() {
				enterState(pauseBetweenRoundsState);
			}
		};

		@Override
		public void onEnter() {
			GameSound.SEQUENCE_INCORRECT.sound.play();
			disableInput();
			sequence.reset();
			sequence.resetPointer();
			sequence.addItem();
			pauseAndShakeButtonsThenShowNewSequence();
		}

		private void pauseAndShakeButtonsThenShowNewSequence() {
			for (SequenceButton button : buttonMap.values()) {
				anim.shake(button.getImageLayer());
			}
			anim.shake(watchCloselyLabel.layer);
			anim.shake(layer)//
					.then()//
					.action(enterPauseBetweenRoundsState);
		}

	};

	@Override
	public void wasHidden() {
		super.wasHidden();
		disableInput();
	}
	
}