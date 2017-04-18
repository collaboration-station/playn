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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import edu.bsu.issgame.core.net.HeadlessTestCase;
import edu.bsu.issgame.core.sequence.Sequence;
import edu.bsu.issgame.core.sequence.SequenceButton;
import edu.bsu.issgame.core.sequence.SequenceItemType;

public class SequenceTest extends HeadlessTestCase {

	Sequence sequence;
	
	@Before
	public void setup(){
		sequence = new Sequence();
	}
	
	@Test
	public void testAddButton() {
		sequence.addItem();
		assertTrue((sequence.getSequence().size() == 1));
	}

	@Test
	public void testIfAClickIsCorrect_True() {
		sequence.addItem();
		assertTrue(isClickCorrectAllButtons());

	}

	@Test
	public void testIfTwoClicksAreCorrect_True() {
		sequence.addItem();
		assertTrue(isClickCorrectAllButtons());
		sequence.addItem();
		sequence.incrementPointer();
		assertTrue(isClickCorrectAllButtons());
	}
	
	@Test
	public void testIfIsComplete_True(){
		sequence.addItem();
		sequence.incrementPointer();
		assertTrue(sequence.isComplete());
	}
	
	@Test
	public void testIfIsComplete_False(){
		sequence.addItem();
		sequence.addItem();
		sequence.incrementPointer();
		assertFalse(sequence.isComplete());
	}
	
	@Test
	public void testReset(){
		sequence.addItem();
		sequence.reset();
		assertTrue(sequence.isComplete());
	}
	
	@Test
	public void testIfAClickIsCorrectAfterReset_True(){
		sequence.addItem();
		sequence.incrementPointer();
		sequence.reset();
		sequence.resetPointer();
		sequence.addItem();
		assertTrue(isClickCorrectAllButtons());
		
	}

	private boolean isClickCorrectAllButtons() {
		return (sequence.isClickCorrect(new SequenceButton(
				SequenceItemType.BIOLOGY))
				|| sequence.isClickCorrect(
						new SequenceButton(SequenceItemType.EXERCISE))
				|| sequence.isClickCorrect(
						new SequenceButton(SequenceItemType.SLEEP)) 
				|| sequence.isClickCorrect(
						new SequenceButton(SequenceItemType.PHYSICS)));
	}
}
