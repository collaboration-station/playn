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
import static org.mockito.Mockito.mock;

import org.junit.Test;

import edu.bsu.issgame.core.net.ClientId;

public class ScoreboardTest {

	private static final Score MAINTENANCE_1_SCIENCE_1 = Score.maintenance(1)
			.science(1);
	private static final Score MAINTENANCE_0_SCIENCE_1 = Score.maintenance(0)
			.science(1);

	private Scoreboard scoreboard;

	@Test
	public void testSum_zeroItems_zeroScore() {
		scoreboard = new Scoreboard();
		assertEquals(Score.ZERO, scoreboard.sum());
	}

	@Test
	public void testSum_twoItems() {
		givenAScoreboardWithTwoScoresRecorded();
		Score expected = Score.maintenance(1).science(2);
		assertEquals(expected, scoreboard.sum());
	}

	private void givenAScoreboardWithTwoScoresRecorded() {
		ClientId clientId1 = mock(ClientId.class);
		ClientId clientId2 = mock(ClientId.class);
		scoreboard = new Scoreboard();
		scoreboard.put(clientId1, MAINTENANCE_0_SCIENCE_1);
		scoreboard.put(clientId2, MAINTENANCE_1_SCIENCE_1);
	}

	@Test
	public void testClear_sizeBecomesZero() {
		givenAScoreboardWithTwoScoresRecorded();
		scoreboard.clear();
		assertEquals(0, scoreboard.size());
	}
	
	@Test
	public void testClear_sumBecomesZero() {
		givenAScoreboardWithTwoScoresRecorded();
		scoreboard.clear();
		assertEquals(Score.ZERO, scoreboard.sum());
	}
}
