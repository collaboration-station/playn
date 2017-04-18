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

import static playn.core.PlayN.assets;
import playn.core.Image;

public enum GameImage {

	SEARCHING_FOR_HOST_BG("images/searching_background.png"), //
	LOBBY_BG("images/lobby_background.png"), //
	CARD_BACK("images/memorycard_cardback.png"), //
	SCORE_SCREEN_BACKGROUND("images/score_background.png"), //
	MEMORY_BOARD_BACKGROUND("images/memory_background.png"), //
	SCENARIO_INFORMATION_BACKGROUND("images/scenario_info_background.png"), //
	TASK_ASSIGNMENT_BACKGROUND("images/task_assignment_background.png"), //
	SLIDING_PUZZLE_BACKGROUND("images/slidingpuzzle_background.png"), //
	SLIDING_IMAGE_ORBITPATH("images/sliding_image_orbitpath.png"), //
	SLIDING_PUZZLE_ROBOT("images/sliding_image_robot.png"), //
	SLIDING_PUZZLE_ASTRONAUT("images/sliding_tile_maintenance_1.png"), //
	LOSE_SCIENCE_SCREEN("images/lose_science_screen.png"), //
	LOSE_MAINTAINANCE_SCREEN("images/lose_maintenance_screen.png"), //
	LOSE_FINAL_SCREEN("images/lose_final_screen.png"), //
	WIN_FINAL_SCREEN("images/win_final_screen.png"), //
	SCIENCE_ICON("images/icon_science.png"), //
	MAINTAINENCE_ICON("images/icon_maintainence.png"), //
	RESET_BUTTON("images/sliding_image_robot.png"), //
	L_WIRE("images/l_square_pipe.png"), //
	T_WIRE("images/t_square_pipe.png"), //
	BAR_WIRE("images/bar_square_pipe.png"), //
	L_WIRE_GLOW("images/l_square_pipe_glow.png"), //
	T_WIRE_GLOW("images/t_square_pipe_glow.png"), //
	BAR_WIRE_GLOW("images/bar_square_pipe_glow.png"), //
	L_WIRE_CIRCUIT("images/l_square_pipe_circuit.png"), //
	T_WIRE_CIRCUIT("images/t_square_pipe_circuit.png"), //
	BAR_WIRE_CIRCUIT("images/bar_square_pipe_circuit.png"), //
	TILE_ROTATION_BACKGROUND("images/pipe_background.png"), //
	FINISH_ROTATION_COMPLETE("images/finish_rotation_complete.png"), //
	START_ROTATION("images/start_rotation_puzzle.png"), //
	FINISH_ROTATION("images/finish_rotation_puzzle.png"), //
	SEQUENCE_MATCHING_BACKGROUND("images/iss_pods_background.png");

	public final Image image;
	public final String path;

	private GameImage(String path) {
		this.image = assets().getImage(path);
		this.path = path;
	}

}
