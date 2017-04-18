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
package edu.bsu.issgame.core.mission;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import edu.bsu.issgame.core.Score;

public class ScoreScalerTest {
	@Test
	public void testScale_twoPlayers_fivePointsPerPlayer_5M5S(){
		ScoreScaler scoreScaler = new ScoreScaler(2);
		Score expected = Score.maintenance(5).science(5);
		Score actual = scoreScaler.scale(5);
		assertEquals(expected, actual);
	}
	
	@Test
	public void testScale_fourPlayers_fivePointsPerPlayer_10M10S(){
		ScoreScaler scoreScaler = new ScoreScaler(4);
		Score expected = Score.maintenance(10).science(10);
		Score actual = scoreScaler.scale(5);
		assertEquals(expected, actual);
	}
	
	@Test
	public void testScaleWithScienceEmphasis_twoPlayers_fivePointsPerPlayer_5M5S(){
		ScoreScaler scoreScaler = new ScoreScaler(2);
		Score expected = Score.maintenance(5).science(5);
		Score actual = scoreScaler.withScienceEmphasis().scale(5);
		assertEquals(expected, actual);
	}
	
	@Test
	public void testScaleWithScienceEmphasis_threePlayers_fivePointsPerPlayer_5MS10(){
		ScoreScaler scoreScaler = new ScoreScaler(3);
		Score expected = Score.maintenance(5).science(10);
		Score actual = scoreScaler.withScienceEmphasis().scale(5);
		assertEquals(expected, actual);
	}
	
	@Test
	public void testScaleWithScienceEmpahsis_threePlayers_fivePointsPerPlayer_10M5S(){
		ScoreScaler scoreScaler = new ScoreScaler(3);
		Score expected = Score.maintenance(10).science(5);
		Score actual = scoreScaler.withMaintenanceEmphasis().scale(5);
		assertEquals(expected, actual);
	}
	
	@Test
	public void testScaleWithScienceEmpahsis_threePlayers_sevenPointsPerPlayer_10M5S(){
		ScoreScaler scoreScaler = new ScoreScaler(3);
		Score expected = Score.maintenance(14).science(7);
		Score actual = scoreScaler.withMaintenanceEmphasis().scale(7);
		assertEquals(expected, actual);
	}
}
