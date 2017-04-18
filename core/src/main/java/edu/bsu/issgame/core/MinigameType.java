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

import edu.bsu.issgame.core.cardmatch.MemoryBoardScreen;
import edu.bsu.issgame.core.rotation.TileRotationGameScreen;
import edu.bsu.issgame.core.sequence.SequenceMatchScreen;
import edu.bsu.issgame.core.slidingtile.SlidingTilePuzzleScreen;

public enum MinigameType {
	MEMORY(MinigameCategory.SCIENCE) {
		@Override
		public AbstractGameScreen getNextScreen(AbstractGameScreen previous) {
			return new MemoryBoardScreen.Builder().fromPreviousScreen(previous)
					.build();
		}
	},

	SLIDING_PUZZLE(MinigameCategory.MAINTENANCE) {
		@Override
		public AbstractGameScreen getNextScreen(AbstractGameScreen previous) {
			return new SlidingTilePuzzleScreen(previous);
		}
	},

	TILE_ROTATION(MinigameCategory.MAINTENANCE) {
		@Override
		public AbstractGameScreen getNextScreen(AbstractGameScreen previous) {
			return new TileRotationGameScreen(previous);
		}
	},
	
	PATTERN_REPEAT(MinigameCategory.SCIENCE){
		@Override
		public AbstractGameScreen getNextScreen(AbstractGameScreen previous) {
			return new SequenceMatchScreen(previous);
		}
	};

	MinigameCategory category;

	MinigameType(MinigameCategory category) {
		this.category = category;
	}

	public abstract AbstractGameScreen getNextScreen(AbstractGameScreen previous);

	public String getDescription() {
		switch (this) {
		case MEMORY:
			return "Find the matches to earn Science points for your team!";
		case SLIDING_PUZZLE:
			return "Fix the pictures to earn Maintenance points for your team!";
		case TILE_ROTATION:
			return "Turn the tiles and complete the broken circuit to earn Maintenance points for your team!";
		case PATTERN_REPEAT:
			return "There are four experiment areas. Touch them the order shown to earn Science Points for your team!";
		}
		return "Unknown, must abort.";
	}

}