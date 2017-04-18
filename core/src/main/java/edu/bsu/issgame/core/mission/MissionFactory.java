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

import static playn.core.PlayN.log;
import edu.bsu.issgame.core.assetmgt.Jukebox.Track;

public final class MissionFactory {

	private static final Exposition INTRODUCTION = RoleBasedExposition
			.forCrew(
					"You are an Astronaut on the International Space Station. Your Commander will assign you tasks.")//
			.andCommanderText(
					"You are the Commander. Your crew needs to get science and maintainance points every round. Assign tasks to each astronaut.");

	private static final Exposition CONCLUSION = TextExposition
			.fromText("Congratulations! You maintained the equipment, and provided information about living in space. You may now begin another expedition, or return home to Earth a hero!");

	private static final Exposition SCENARIO_1 = TextExposition
			.fromText("You've made it safely aboard the ISS! Now the crew needs to complete experiments and maintain the space station.");

	private static final Exposition SCENARIO_2 = TextExposition
			.fromText("The crew has done a great job abord the ISS in the first two months! But, living in space gets harder, not easier. So, keep working!");

	private static final Exposition SCENARIO_3 = TextExposition
			.fromText("Your expedition is almost complete! Before you leave, make sure your experiments are finished and the Station is in working order.");

	public static MissionFactory createWithNumberOfPlayers(int numberOfPlayers) {
		return new MissionFactory(numberOfPlayers);
	}

	private final int numberOfPlayers;

	private MissionFactory(int numberOfPlayers) {
		if (numberOfPlayers < 2) {
			log().warn("Unexpected number of players: " + numberOfPlayers);
		}
		this.numberOfPlayers = numberOfPlayers;
	}

	public Mission createDefaultMission() {
		ScoreScaler scoreScaler = new ScoreScaler(numberOfPlayers);
		Scenario[] scenarios = new Scenario[] {
				Scenario.withExposition(SCENARIO_1)
						.andTrack(Track.MINIGAME_MUSIC_1)
						.andGoal(
								scoreScaler.withMaintenanceEmphasis().scale(25)),
				Scenario.withExposition(SCENARIO_2)
						.andTrack(Track.MINIGAME_MUSIC_2)
						.andGoal(scoreScaler.withScienceEmphasis().scale(40)),
				Scenario.withExposition(SCENARIO_3)
						.andTrack(Track.MINIGAME_MUSIC_3)
						.andGoal(
								scoreScaler.withMaintenanceEmphasis().scale(55)) };

		return Mission.createWithIntroduction(INTRODUCTION)//
				.andScenarios(scenarios)//
				.andConclusion(CONCLUSION);
	}
}
