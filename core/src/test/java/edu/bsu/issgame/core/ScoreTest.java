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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class ScoreTest {
	private static final Score ZERO = Score.create();
	private static final Score MAINTENANCE_1_SCIENCE_1 = Score.maintenance(1).science(1);
	private static final Score MAINTENANCE_0_SCIENCE_1 = Score.maintenance(0).science(1);

	@Test
	public void testThatScienceScoreStartsAtZero() {
		assertEquals(0, ZERO.science);
	}

	@Test
	public void testAddToScienceScore() {
		final int amountToAdd = 13;
		Score sum = ZERO.addScience(amountToAdd);
		assertEquals(sum.science, amountToAdd);
	}

	@Test
	public void testAddScience_doesNotChangeCallee() {
		final int amountToAdd = 13;
		ZERO.addScience(amountToAdd);
		assertEquals(0, ZERO.science);
	}

	@Test
	public void testAddToMaintenanceScore() {
		final int amountToAdd = 15;
		Score sum = ZERO.addMaintenance(amountToAdd);
		assertEquals(amountToAdd, sum.maintenance);
	}

	@Test
	public void testCreateWithNonzeroInitialValues() {
		Score score = Score.maintenance(5).science(6);
		assertEquals(5, score.maintenance);
	}

	@Test
	public void testMeetsOrExceeds_bothValuesLess_false() {
		assertFalse(ZERO.meetsOrExceeds(MAINTENANCE_1_SCIENCE_1));
	}

	@Test
	public void testMeetsOrExceeds_maintenanceLess_false() {
		assertFalse(MAINTENANCE_0_SCIENCE_1.meetsOrExceeds(MAINTENANCE_1_SCIENCE_1));
	}

	@Test
	public void testMeetsOrExceeds_true() {
		assertTrue(MAINTENANCE_1_SCIENCE_1.meetsOrExceeds(ZERO));
	}

	@Test
	public void testEquals_identity() {
		assertTrue(ZERO.equals(ZERO));
	}

	@Test
	public void testEquals_unequal_false() {
		assertFalse(ZERO.equals(MAINTENANCE_0_SCIENCE_1));
	}

	@Test
	public void testEquals_equal_equivalentDifferentObjects() {
		final int maintenance = 5;
		final int science = 10;
		Score a = Score.maintenance(maintenance).science(science);
		Score b = Score.maintenance(maintenance).science(science);
		assertTrue(a.equals(b));
	}
}
