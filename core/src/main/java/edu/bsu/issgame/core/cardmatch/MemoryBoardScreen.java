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
package edu.bsu.issgame.core.cardmatch;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;
import static playn.core.PlayN.log;
import static playn.core.PlayN.pointer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import playn.core.Pointer;
import playn.core.Pointer.Event;
import pythagoras.f.Dimension;
import pythagoras.f.IDimension;
import react.RFuture;
import react.RList;
import react.Signal;
import react.SignalView;
import react.Slot;
import tripleplay.util.Input.Registration;
import tripleplay.util.PointerInput;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

import edu.bsu.issgame.core.AbstractGameScreen;
import edu.bsu.issgame.core.CommonGameScreenUI;
import edu.bsu.issgame.core.GameImage;
import edu.bsu.issgame.core.GameSound;
import edu.bsu.issgame.core.MinigameType;
import edu.bsu.issgame.core.Settings;

public final class MemoryBoardScreen extends CommonGameScreenUI {

	private static final int ROWS = 4;
	private static final int COLUMNS = 5;
	private static final int CONSECUTIVE_FLIP_DELAY = 100;
	private static final float HOW_MUCH_THE_CARD_FILLS_ITS_CELL = 0.9f;
	private final int POINTS_PER_COMPLETED_BOARD = settings.get(Settings.POINTS_PER_COMPLETED_MEMORY_BOARD);
	private final int POINTS_PER_MATCH = settings.get(Settings.POINTS_PER_MATCH);

	private final IDimension CARD_SIZE = new Dimension(//
			HOW_MUCH_THE_CARD_FILLS_ITS_CELL
					* getScreenScale().getScaledScreenSize().width
					/ (COLUMNS + 1), //
			HOW_MUCH_THE_CARD_FILLS_ITS_CELL
					* getScreenScale().getScaledScreenSize().height
					/ (ROWS + 1));
	private final int DOWN_SHIFT = getScreenScale().getScaledScreenSize().height / 55;
	private final PointerInput input = new PointerInput();

	private List<Card> listOfAllCards = Lists.newArrayList();
	private Signal<Match> onMatch = Signal.create();
	private List<Card> cardsToMatchList = new ArrayList<Card>();
	private Match match;
	private Card firstCard;
	private Card secondCard;
	private Map<Card, Registration> cardToRegistrationMap = new HashMap<Card, Registration>();
	private MemoryBoardScreen nextBoard;
	private AnimationTracker animationTracker = new AnimationTracker();

	private State state;

	public static class Builder {
		private AbstractGameScreen previous;

		public Builder fromPreviousScreen(AbstractGameScreen previous) {
			this.previous = checkNotNull(previous);
			return this;
		}

		public MemoryBoardScreen build() {
			return new MemoryBoardScreen(this);
		}
	}

	public MemoryBoardScreen(Builder builder) {
		super(builder.previous);
		setBackground(GameImage.MEMORY_BOARD_BACKGROUND.image);
		configureGameScreen();
		enterState(allCardsFaceDownState);
	}

	private void configureGameScreen() {
		pointer().setListener(input.plistener);
		initCardList();
		initCards();
	}

	@Override
	protected void setMinigameType() {
		thisMinigame = MinigameType.MEMORY;
	}

	private void initCardList() {
		listOfAllCards.clear();
		for (int i = 0; i < ROWS * COLUMNS / 2; i++) {
			listOfAllCards.add(new Card(CardType.values()[i], anim, CARD_SIZE));
			listOfAllCards.add(new Card(CardType.values()[i], anim, CARD_SIZE));
		}
		if (isRandomizedBoard()) {
			Collections.shuffle(listOfAllCards);
		}
	}

	private void initCards() {
		int index = 0;
		for (int row = 1; row < ROWS + 1; row++) {
			for (int col = 1; col < COLUMNS + 1; col++, index++) {
				final Card card = listOfAllCards.get(index);
				float x = getScreenScale().getScaledPosition().x
						+ (col * (getScreenScale().getScaledScreenSize().width / (COLUMNS + 1)));
				float y = getScreenScale().getScaledPosition().y
						+ (row * (getScreenScale().getScaledScreenSize().height / (ROWS + 1)));
				card.layer.setTranslation(x, y + DOWN_SHIFT);
				layer.add(card.layer);
				registerInputOn(card);
			}
		}
	}

	public void repopulateBoard() {
		score.update(score.get().addScience(POINTS_PER_COMPLETED_BOARD));
		initCardList();
		initCards();
	}

	private void registerInputOn(final Card card) {
		Registration reg = input.register(card.layer, new Pointer.Adapter() {
			@Override
			public void onPointerStart(Event event) {
				handleClickOn(card);
			}
		});
		cardToRegistrationMap.put(card, reg);
	}

	protected RFuture<Card> handleClickOn(Card card) {
		if (card.isCardInputAllowed()) {
			return state.handleClickOn(card);
		} else
			return null;
	}

	@Override
	protected void handleScenarioEnd() {
		if (!animationTracker.isAnimationPlaying()) {
			super.handleScenarioEnd();
		}
	}

	private void handleMatch() {
		checkForMatch();
		resetCardsToMatch();
		if (timeHasRunOut()&&!animationTracker.isAnimationPlaying()) {
			super.handleScenarioEnd();
		}
	}

	private boolean timeHasRunOut() {
		return timer.timeRemaining().get() <= 0;
	}

	private void checkForMatch() {
		if (firstCard.isMatch(secondCard)) {
			handleSuccessfulMatch();
		} else {
			handleFailedMatch();
		}
	}

	private void handleSuccessfulMatch() {
		log().debug("In handleSuccess");
		match = new Match(firstCard, secondCard);
		onMatch.emit(match);
		removeMatchedCardsFromPlay();
		incrementScore();
		GameSound.SUCCESS.sound.play();
		if (cardToRegistrationMap.isEmpty()) {
			handleFinishedBoard();
		}
	}

	private void handleFinishedBoard() {
		GameSound.MEMORY_BOARD_COMPLETE.sound.play();
		repopulateBoard();	
	}

	private void removeMatchedCardsFromPlay() {
		final Card firstCardToRemove = firstCard;
		final Card secondCardToRemove = secondCard;
		firstCard.setInputDisabled();
		secondCard.setInputDisabled();
		cardToRegistrationMap.get(firstCardToRemove).cancel();
		cardToRegistrationMap.get(secondCardToRemove).cancel();
		cardToRegistrationMap.remove(firstCardToRemove);
		cardToRegistrationMap.remove(secondCardToRemove);
		anim.tweenAlpha(firstCardToRemove.layer).to(0f).in(1000).then()
				.action(new Runnable() {
					@Override
					public void run() {
						layer.remove(firstCardToRemove.layer);
					}

				});
		anim.tweenAlpha(secondCardToRemove.layer).to(0f).in(1000).then()
				.action(new Runnable() {
					@Override
					public void run() {
						layer.remove(secondCardToRemove.layer);
					}
				});
	}

	private void incrementScore() {
		score.update(score.get().addScience(POINTS_PER_MATCH));
	}

	private void handleFailedMatch() {
		if (firstCard.facing().get().isFaceUp()) {
			RFuture<Card> animFuture = firstCard.flip();
			animationTracker.track(animFuture);
		}
		if (secondCard.facing().get().isFaceUp()) {
			RFuture<Card> animFuture = secondCard.flip();
			animationTracker.track(animFuture);
		}
		GameSound.CARD_MATCH_FAILURE.sound.play();
	}

	private void resetCardsToMatch() {
		cardsToMatchList.remove(firstCard);
		firstCard = null;
		cardsToMatchList.remove(secondCard);
		secondCard = null;
	}

	private void addCardToMatchList(Card card) {
		if (!cardsToMatchList.contains(card)) {
			cardsToMatchList.add(card);
		}
	}

	private RFuture<Card> activateFirstCard(Card card) {
		checkState(firstCard == null);
		firstCard = card;
		if (!cardsToMatchList.contains(firstCard)) {
			addCardToMatchList(firstCard);
		}
		RFuture<Card> future = firstCard.flip();
		animationTracker.track(future);
		return future;
	}

	private RFuture<Card> activateSecondCard(Card card, boolean delayed) {
		checkState(firstCard != null);
		checkState(secondCard == null);
		secondCard = card;
		if (firstCard == secondCard)
			return null;
		if (cardsToMatchList.contains(secondCard)) {
			addCardToMatchList(secondCard);
		}

		RFuture<Card> future;
		if (!delayed) {
			future = secondCard.flip();
		} else {
			future = secondCard.flip(CONSECUTIVE_FLIP_DELAY);
		}
		animationTracker.track(future);

		return future;
	}

	private RFuture<Card> handleActiveCardFlip(Card card) {
		checkState(card == firstCard);
		RFuture<Card> future = card.flip();
		animationTracker.track(future);
		if (firstCard == card) {
			cardsToMatchList.remove(firstCard);
			firstCard = null;
		}
		return future;
	}

	private MemoryBoardScreen enterState(State state) {
		this.state = state;
		return this;
	}

	private interface State {
		RFuture<Card> handleClickOn(Card card);
	}

	private final State allCardsFaceDownState = new State() {
		@Override
		public RFuture<Card> handleClickOn(Card card) {
			RFuture<Card> future = activateFirstCard(card);
			enterState(new OneCardFlippingState(future));
			return future;
		}

		@Override
		public String toString() {
			return "AllCardsFaceDownState";
		}
	};

	private final class OneCardFlippingState implements State {

		public OneCardFlippingState(RFuture<Card> inProgressFlip) {
			inProgressFlip.onSuccess(new Slot<Card>() {
				@Override
				public void onEmit(Card cardToMatch) {
					if (iAmTheCurrentState()) {
						enterState(oneCardFlippedState);
					}
				}

				private boolean iAmTheCurrentState() {
					return MemoryBoardScreen.this.state == OneCardFlippingState.this;
				}
			});
		}

		@Override
		public String toString() {
			return "OneCardFlippingState";
		}

		@Override
		public RFuture<Card> handleClickOn(Card card) {
			RFuture<Card> future;
			if (card == firstCard) {
				future = handleActiveCardFlip(card);
				enterState(allCardsFaceDownState);
			} else {
				future = activateSecondCard(card, false);
				if (future != null) {
					enterState(new TwoCardsFlippingState(future));
				}
			}
			return future;
		}
	}

	private final State oneCardFlippedState = new State() {
		@Override
		public RFuture<Card> handleClickOn(Card card) {
			RFuture<Card> future;
			if (card == firstCard) {
				future = handleActiveCardFlip(card);
				enterState(allCardsFaceDownState);
			} else {
				future = activateSecondCard(card, false);
				// Technically there is only one card flipping now, but the
				// *logic* is the same as if two cards were actually flipping. I
				// am
				// having trouble thinking of a better name right now.
				if (future != null) {
					enterState(new TwoCardsFlippingState(future));
				}
			}
			return future;
		}

		@Override
		public String toString() {
			return "OneCardFlippedState";
		}
	};

	private final class TwoCardsFlippingState implements State {
		public TwoCardsFlippingState(RFuture<Card> secondInProgressFlip) {
			secondInProgressFlip.onSuccess(new Slot<Card>() {
				@Override
				public void onEmit(Card currentCard) {
					try {
						checkState(currentCard.facing().get().isFaceUp());
					} catch (IllegalStateException ise) {
						ise.printStackTrace();
					}
					handleMatch();
					if (cardsToMatchList.isEmpty())
						enterState(allCardsFaceDownState);
					else if (cardsToMatchList.size() == 1) {
						handleOneCardInMatchList();
					} else {
						handleTwoUnmatchedCardsInMatchList();
					}
				}

				private void handleOneCardInMatchList() {
					Card card = cardsToMatchList.get(0);
					RFuture<Card> future = activateFirstCard(card);
					enterState(new OneCardFlippingState(future));
				}

				private void handleTwoUnmatchedCardsInMatchList() {
					activateFirstCard(cardsToMatchList.get(0));
					RFuture<Card> future2 = activateSecondCard(
							cardsToMatchList.get(1), true);
					enterState(new TwoCardsFlippingState(future2));
				}
			});
		}

		@Override
		public RFuture<Card> handleClickOn(Card card) {
			addCardToMatchList(card);
			return null;
		}

		@Override
		public String toString() {
			return "TwoCardsFlippingState";
		}
	}

	public Match getMatch() {
		return match;
	}

	public SignalView<Match> onMatch() {
		return onMatch;
	}

	private boolean isRandomizedBoard() {
		return settings.get(Settings.BOARD_RANDOMIZATION);
	}

	public ImmutableList<Card> cards() {
		return ImmutableList.copyOf(listOfAllCards);
	}

	public MemoryBoardScreen getNextBoard() {
		return nextBoard;
	}

	private final class AnimationTracker {
		final RList<RFuture<Card>> list = RList.create();

		public void track(final RFuture<Card> animationFuture) {
			list.add(animationFuture);
			animationFuture.onSuccess(new Slot<Card>() {
				@Override
				public void onEmit(Card event) {
					list.remove(animationFuture);
				}
			});
		}

		public boolean isAnimationPlaying() {
			return !list.isEmpty();
		}
	}

	@Override
	public void wasHidden() {
		super.wasHidden();
		pointer().setListener(null);
	}
}
