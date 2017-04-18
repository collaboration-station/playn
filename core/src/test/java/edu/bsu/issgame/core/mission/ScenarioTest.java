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

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import org.junit.Test;
import org.mockito.Matchers;

import edu.bsu.issgame.core.Score;
import edu.bsu.issgame.core.mission.Result.Failure;
import edu.bsu.issgame.core.mission.Result.Success;
import edu.bsu.issgame.core.mission.Result.Visitor;

public class ScenarioTest {

	private static final Exposition SAMPLE_EXPOSITION = TextExposition
			.fromText("Space Monkeys.");
	private static final Score NONZERO_SCORE = Score.maintenance(10)
			.science(11);
	private static final Score ZERO_SCORE = Score.create();

	@Test
	public void testEvaluate_insufficientPoints_returnsFailure() {
		Scenario scenario = Scenario.withExposition(SAMPLE_EXPOSITION).andGoal(
				NONZERO_SCORE);
		Visitor visitor = mock(Visitor.class);
		scenario.evaluate(ZERO_SCORE).accept(visitor);
		verify(visitor).visit(Matchers.any(Failure.class));
	}

	@Test
	public void testEvaluate_sufficientPoints_returnsSuccess() {
		Scenario scenario = Scenario.withExposition(SAMPLE_EXPOSITION).andGoal(
				ZERO_SCORE);
		Visitor visitor = mock(Visitor.class);
		scenario.evaluate(NONZERO_SCORE).accept(visitor);
		verify(visitor).visit(Matchers.any(Success.class));
	}
}
