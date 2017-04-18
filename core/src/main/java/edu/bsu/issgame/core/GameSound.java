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
import playn.core.Sound;

public enum GameSound {

	CARD_FLIP("card_flip"), //
	SUCCESS("card_match_success"), //
	CARD_MATCH_FAILURE("card_match_failure"), //
	MEMORY_BOARD_COMPLETE("complete_match_set"), //
	BUTTON_CLICK("button_click"), //
	TILE_SLIDE("tile_slide"), //
	SLIDING_PUZZLE_COMPLETE("sliding_puzzle_complete"),//
	ELECTRICITY("electricity"), //
	ZAP("rotation_zap"), //
	SEQUENCE_INCORRECT("sequence_incorrect"),//
	SEQUENCE_BIOLOGY("sequence_biology"), //
	SEQUENCE_EXERCISE("sequence_exercise"), //
	SEQUENCE_SLEEP("sequence_snore"), //
	SEQUENCE_PHYSICS("sequence_physics");

	public final Sound sound;
	public final String path;

	private GameSound(String relativePath) {
		this.path = "sfx/" + relativePath;
		this.sound = assets().getSound(this.path);
	}
}
