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
package edu.bsu.issgame.core.assetmgt;

import static playn.core.PlayN.assets;
import playn.core.Image;

public enum LoadableImage {
	INTRO_1("intro_image1.png"), //
	INTRO_2("intro_image2.png"), //
	INTRO_3("intro_image3.png"), //
	INTRO_4("intro_image4.png"), //
	TITLE("title.png"), //
	WELCOME_BG("title_screen.png"), //
	SEARCHING_FOR_HOST_BG("searching_background.png"), //
	LOBBY_BG("lobby_background.png"), //
	CARD_BACK("memorycard_cardback.png"), //
	SCORE_SCREEN_BACKGROUND("score_background.png"), //
	MEMORY_BOARD_BACKGROUND("memory_background.png"), //
	SCENARIO_INFORMATION_BACKGROUND("scenario_info_background.png"), //
	TASK_ASSIGNMENT_BACKGROUND("task_assignment_background.png"), //
	SLIDING_PUZZLE_BACKGROUND("slidingpuzzle_background.png"), //
	SLIDING_IMAGE_ORBITPATH("sliding_image_orbitpath.png"), //
	SLIDING_PUZZLE_ROBOT("sliding_image_robot.png"), //
	SLIDING_PUZZLE_ASTRONAUT("sliding_tile_maintenance_1.png"), //
	LOSE_SCIENCE_SCREEN("lose_science_screen.png"), //
	LOSE_MAINTAINANCE_SCREEN("lose_maintenance_screen.png"), //
	LOSE_FINAL_SCREEN("lose_final_screen.png"), //
	WIN_FINAL_SCREEN("win_final_screen.png"), //
	SCIENCE_ICON("icon_science.png"), //
	MAINTAINENCE_ICON("icon_maintainence.png"), //
	RESET_BUTTON("sliding_image_robot.png"), //
	L_WIRE("l_square_pipe.png"), //
	T_WIRE("t_square_pipe.png"), //
	BAR_WIRE("bar_square_pipe.png"), //
	L_WIRE_GLOW("l_square_pipe_glow.png"), //
	T_WIRE_GLOW("t_square_pipe_glow.png"), //
	BAR_WIRE_GLOW("bar_square_pipe_glow.png"), //
	L_WIRE_CIRCUIT("l_square_pipe_circuit.png"), //
	T_WIRE_CIRCUIT("t_square_pipe_circuit.png"), //
	BAR_WIRE_CIRCUIT("bar_square_pipe_circuit.png"), //
	TILE_ROTATION_BACKGROUND("pipe_background.png"), //
	FINISH_ROTATION_COMPLETE("finish_rotation_complete.png"), //
	START_ROTATION("start_rotation_puzzle.png"), //
	FINISH_ROTATION("finish_rotation_puzzle.png"), //
	SEQUENCE_MATCHING_BACKGROUND("iss_pods_background.png"), //
	TCM_LOGO("tcmlogo.png"), //
	BSU_LOGO("bsulogo.png"), //
	SMS_LOGO("sms_logo.png"), //
	CREDITS_BG("credits_background.png");

	private final String path;

	private LoadableImage(String partialPath) {
		this.path = "images/" + partialPath;
	}

	public Image loadAsync() {
		return assets().getImage(path);
	}

	public Image loadSync() {
		return assets().getImageSync(path);
	}
}
