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

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import org.junit.Before;
import org.junit.Test;

import playn.core.util.Clock;
import pythagoras.f.Dimension;
import react.RFuture;
import react.Slot;
import tripleplay.anim.Animator;
import edu.bsu.issgame.core.Settings;
import edu.bsu.issgame.core.net.HeadlessTestCase;

public class CardTest extends HeadlessTestCase {

	private Animator anim;

	@Before
	public void setUp() {
		anim = new Animator();
	}

	@Test
	public void testFlip_useFutureToKnowEndOfAnimation() {
		Clock.Source clock = new Clock.Source(0);
		Slot<Boolean> slot = makeSlotWithoutWarnings();
		Card card = new Card(CardType.BUTTERFLY, anim, new Dimension(10, 10));
		RFuture<Card> future = card.flip();
		future.isComplete().connect(slot);
		for (int i = 0; i < 4; i++) {
			clock.update(1000);
			anim.paint(clock);
		}
		verify(slot).onEmit(Boolean.TRUE);
	}

	private Slot<Boolean> makeSlotWithoutWarnings() {
		@SuppressWarnings("unchecked")
		Slot<Boolean> slot = mock(Slot.class);
		return slot;
	}

	@Test
	public void testFlip_facingIsUpdatedAfterFlip() {
		Clock.Source clock = new Clock.Source(0);
		Slot<Facing> slot = createFacingSlotWithoutWarnings();
		Animator anim = new Animator();
		Card card = new Card(CardType.BUTTERFLY, anim, new Dimension(10, 10));
		card.facing().connect(slot);
		card.flip();
		for (int i = 0; i < 4; i++) {
			clock.update(1000);
			anim.paint(clock);
		}
		assertTrue(card.facing().get().isFaceUp());
	}

	private Slot<Facing> createFacingSlotWithoutWarnings() {
		@SuppressWarnings("unchecked")
		Slot<Facing> slot = mock(Slot.class);
		return slot;
	}

	@Test
	public void testThatCardsWithSameImageMatch() {
		Settings.createDefaults();
		Card card = new Card(CardType.BUTTERFLY, anim, new Dimension(10, 10));
		Card secondCard = new Card(CardType.BUTTERFLY, anim, new Dimension(10,
				10));
		assertTrue(card.isMatch(secondCard));

	}
	
	

}
