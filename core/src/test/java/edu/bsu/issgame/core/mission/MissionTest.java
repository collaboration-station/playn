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
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import org.junit.After;
import org.junit.Test;
import org.mockito.Matchers;

import edu.bsu.issgame.core.Score;
import edu.bsu.issgame.core.mission.Mission.Result.Failure;
import edu.bsu.issgame.core.mission.Mission.Result.MissionSuccess;
import edu.bsu.issgame.core.mission.Mission.Result.ScenarioSuccess;

public class MissionTest {

	private static final Exposition EXPOSITION = TextExposition
			.fromText("Science!");
	private static final Exposition CONCLUSION = TextExposition
			.fromText("The end.");
	private static final Score NONZERO_SCORE = Score.maintenance(1).science(1);
	private static final Score ZERO_SCORE = Score.create();
	private static final Exposition INTRODUCTION = TextExposition
			.fromText("Intro");

	private Scenario scenario;
	private Mission mission;

	@After
	public void tearDown() {
		scenario = null;
		mission = null;
	}

	@Test
	public void testScenario_initial_getsFirstScenario() {
		givenAMissionWithOneEasyScenario();
		assertEquals(scenario, mission.scenario());
	}

	private void givenAMissionWithOneEasyScenario() {
		scenario = Scenario.withExposition(EXPOSITION).andGoal(ZERO_SCORE);
		mission = Mission.createWithIntroduction(INTRODUCTION)
				.andScenario(scenario).andConclusion(CONCLUSION);
	}

	@Test
	public void testEvaluate_failure() {
		Scenario scenario = Scenario.withExposition(EXPOSITION).andGoal(
				NONZERO_SCORE);
		mission = Mission.createWithIntroduction(INTRODUCTION)
				.andScenario(scenario).andConclusion(CONCLUSION);
		Mission.Result.Visitor visitor = mock(Mission.Result.Visitor.class);
		mission.evaluateCurrentScenario(ZERO_SCORE).accept(visitor);
		verify(visitor).visit(Matchers.any(Failure.class));
	}

	@Test
	public void testEvaluate_successOnLastScenario_successMission() {
		givenAMissionWithOneEasyScenario();
		Mission.Result.Visitor visitor = mock(Mission.Result.Visitor.class);
		mission.evaluateCurrentScenario(NONZERO_SCORE).accept(visitor);
		verify(visitor).visit(Matchers.any(MissionSuccess.class));
	}

	@Test(expected = IllegalStateException.class)
	public void testScenario_afterSuccessOnLastScenario_throwsException() {
		givenAMissionWithOneEasyScenario();
		Mission.Result.Visitor visitor = mock(Mission.Result.Visitor.class);
		mission.evaluateCurrentScenario(NONZERO_SCORE).accept(visitor);
		mission.scenario();
	}

	@Test
	public void testScenario_successOnNonLastScenario_scenarioSuccess() {
		givenAMissionWithTwoEasyScenarios();
		Mission.Result.Visitor visitor = mock(Mission.Result.Visitor.class);
		mission.evaluateCurrentScenario(NONZERO_SCORE).accept(visitor);
		verify(visitor).visit(Matchers.any(ScenarioSuccess.class));
	}

	private void givenAMissionWithTwoEasyScenarios() {
		Scenario scenario = Scenario.withExposition(EXPOSITION).andGoal(
				ZERO_SCORE);
		mission = Mission.createWithIntroduction(INTRODUCTION)
				.andScenarios(scenario, scenario).andConclusion(CONCLUSION);
	}

	@Test
	public void testCreate_hasIntroduction() {
		givenAMissionWithOneEasyScenario();
		assertEquals(INTRODUCTION, mission.introduction);
	}

	@Test
	public void testCreate_hasConclusion() {
		givenAMissionWithOneEasyScenario();
		assertEquals(CONCLUSION, mission.conclusion);
	}

}