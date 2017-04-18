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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import org.junit.Before;
import org.junit.Test;

import playn.core.util.Clock;
import react.RFuture;
import react.Slot;
import tripleplay.game.Screen;
import tripleplay.game.ScreenStack;
import edu.bsu.issgame.core.AbstractGameScreen;
import edu.bsu.issgame.core.Settings;
import edu.bsu.issgame.core.net.HeadlessTestCase;
import edu.bsu.issgame.core.net.MessageIO;
import edu.bsu.issgame.core.net.NetworkInterface;
import edu.bsu.issgame.core.net.client.Client;

public class MemoryBoardScreenTest extends HeadlessTestCase {

	private static final AbstractGameScreen DUMMY_PREVIOUS = new AbstractGameScreen(
			null) { 
		{
			client = Client.withIO(mock(MessageIO.class)).withVersionCode(1);
			net = mock(NetworkInterface.class);
			screenStack = mock(ScreenStack.class);
			settings = Settings.createDefaults()
					.set(Settings.BOARD_RANDOMIZATION).to(false);
		}
	};
	private Clock.Source clock = new Clock.Source(33);
	private MemoryBoardScreen screen;

	@Before
	public void setUp() {
		screen = new MemoryBoardScreen(new MemoryBoardScreen.Builder()//
				.fromPreviousScreen(DUMMY_PREVIOUS));
	}

	@Test
	public void testCreate_hasCards() {
		assertTrue(screen.cards().size() > 0);
	}

	@Test
	public void testCreate_allCardsAreFaceDown() {
		for (Card card : screen.cards()) {
			assertTrue(card.facing().get().isFaceDown());
		}
	}

	@Test
	public void testClickOnCard_noCardsCurrentlyFlipped_theCardFlips() {
		Card card = screen.cards().get(0);
		RFuture<Card> handled = screen.handleClickOn(card);
		simulateElapsedTimeUntil(handled);
		assertTrue(card.facing().get().isFaceUp());
	}

	@Test
	public void testHandleClickOnCard_oneAlreadyFlipping_secondOneFlips() {
		clickCard(0);
		RFuture<Card> flipFuture = clickCard(1);
		simulateElapsedTimeUntil(flipFuture);
		assertTrue(cardIsFaceUp(1));
	}

	@Test
	public void testHandleClickOnCard_matchingPairFlipped_emitsMatchSignal() {
		@SuppressWarnings("unchecked")
		Slot<Match> slot = mock(Slot.class);
		screen.onMatch().connect(slot);
		clickCardAndWaitLongEnoughForFlipToFinish(0);
		clickCardAndWaitLongEnoughForFlipToFinish(1);
		Match expected = screen.getMatch();
		verify(slot).onEmit(expected);
	}

	@Test
	public void testThatAfterFailedMatch_cardsFlipDown() {
		clickCardAndWaitLongEnoughForFlipToFinish(1);
		clickCardAndWaitLongEnoughForFlipToFinish(2);
		simulateMoreThanEnoughTimeForCardsToHaveCompletedFlipping();
		assertTrue(cardIsFaceDown(1) && cardIsFaceDown(2));
	}

	@Test
	public void testThatAfterFailedMatch_nextCardFlipsUp() {
		clickCardAndWaitLongEnoughForFlipToFinish(1);
		clickCardAndWaitLongEnoughForFlipToFinish(2);
		clickCardAndWaitLongEnoughForFlipToFinish(3);
		assertTrue(cardIsFaceDown(1) && cardIsFaceDown(2) && cardIsFaceUp(3));
	}

	@Test
	public void testThatAfterSuccessfulMatch_cardsStayUp() {
		clickCardAndWaitLongEnoughForFlipToFinish(0);
		clickCardAndWaitLongEnoughForFlipToFinish(1);
		simulateMoreThanEnoughTimeForCardsToHaveCompletedFlipping();
		assertTrue(cardIsFaceUp(0) && cardIsFaceUp(1));
	}

	@Test
	public void testThatAfterSuccesfulMatch_cardsCannotBeFlipped() {
		clickCardAndWaitLongEnoughForFlipToFinish(2);
		clickCardAndWaitLongEnoughForFlipToFinish(3);
		simulateMoreThanEnoughTimeForCardsToHaveCompletedFlipping();
		clickCardAndWaitLongEnoughForFlipToFinish(2);
		assertTrue(cardIsFaceUp(2));
	}

	@Test
	public void testThatAfterSecondCardFlipsUpAndAnimationFinishes_matchedCardStaysUp() {
		clickCard(0);
		clickCard(1);
		simulateMoreThanEnoughTimeForCardsToHaveCompletedFlipping();
		assertTrue(cardIsFaceUp(0));
	}

	@Test
	public void testThatSameCardCanBeFlippedWithoutCallingMatch() {

		clickCardAndWaitLongEnoughForFlipToFinish(2);
		clickCardAndWaitLongEnoughForFlipToFinish(2);
		assertTrue(cardIsFaceDown(2));

	}

	@Test
	public void testThatFourConsecutiveCardsAreHandledCorrectly() {

		Card card1 = screen.cards().get(0);
		Card card2 = screen.cards().get(1);
		Card card3 = screen.cards().get(2);
		Card card4 = screen.cards().get(3);
		clickCard(0);
		clickCard(1);
		clickCard(2);
		clickCard(3);
		simulateMoreThanEnoughTimeForCardsToHaveCompletedFlipping();
		if (card1.isMatch(card2)) {
			assertTrue(cardIsFaceUp(0));
			assertTrue(cardIsFaceUp(1));
		} else {
			assertTrue(!cardIsFaceUp(0));
			assertTrue(!cardIsFaceUp(1));
		}
		simulateMoreThanEnoughTimeForCardsToHaveCompletedFlipping();
		simulateMoreThanEnoughTimeForCardsToHaveCompletedFlipping();
		if (card3.isMatch(card4)) {
			assertTrue(cardIsFaceUp(2));
			assertTrue(cardIsFaceUp(3));
		} else {
			assertTrue(!cardIsFaceUp(2));
			assertTrue(!cardIsFaceUp(3));
		}

	}

	@Test
	public void testThatNewBoardHasBeenPushed() {
		Screen firstScreen = screen;
		matchAllCards();
		screen.repopulateBoard();
		Screen secondScreen = screen.getNextBoard();
		assertFalse(firstScreen == secondScreen);
	}
	
	@Test
	public void testThatAnimatingCardHasInputDisabled() {
		
		
		clickCard(0);
		assertTrue(!screen.cards().get(0).isCardInputAllowed());
		simulateMoreThanEnoughTimeForCardsToHaveCompletedFlipping();
		assertTrue(screen.cards().get(0).isCardInputAllowed());
		clickCard(0);
		assertTrue(!screen.cards().get(0).isCardInputAllowed());
		simulateMoreThanEnoughTimeForCardsToHaveCompletedFlipping();
		assertTrue(screen.cards().get(0).isCardInputAllowed());
	}

	private void matchAllCards() {
		for (int i = 0; i < screen.cards().size(); i++) {
			clickCardAndWaitLongEnoughForFlipToFinish(i);
			i++;
			clickCardAndWaitLongEnoughForFlipToFinish(i);
		}
	}

	private boolean cardIsFaceDown(int i) {
		return screen.cards().get(i).facing().get().isFaceDown();
	}

	private void clickCardAndWaitLongEnoughForFlipToFinish(int i) {
		Card card1 = screen.cards().get(i);
		screen.handleClickOn(card1);
		simulateMoreThanEnoughTimeForCardsToHaveCompletedFlipping();
	}

	private void simulateMoreThanEnoughTimeForCardsToHaveCompletedFlipping() {
		for (int i = 0; i < 4; i++) {
			simulateElapsedTime(Card.FLIP_TIME_MS);
		}
	}

	private RFuture<Card> clickCard(int i) {
		Card card = screen.cards().get(i);
		return screen.handleClickOn(card);
	}

	private boolean cardIsFaceUp(int i) {
		Card card = screen.cards().get(i);
		return card.facing().get().isFaceUp();
	}

	private void simulateElapsedTimeUntil(RFuture<Card> handled) {
		final int delta = 33;
		while (!handled.isComplete().get()) {
			simulateElapsedTime(delta);
		}
	}

	private void simulateElapsedTime(int delta) {
		clock.update(delta);
		screen.update(delta);
		screen.paint(clock);
	}
}
