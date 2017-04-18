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
package edu.bsu.issgame.core.slidingtile;

import static com.google.common.base.Preconditions.checkNotNull;

public final class PuzzleBundle {

	public final PuzzleViewData image;
	public final PuzzleDifficulty difficulty;
	public final int numberOfMoves;

	public PuzzleBundle(PuzzleViewData image, PuzzleDifficulty difficulty,
			int numberOfMoves) {
		this.image = checkNotNull(image);
		this.difficulty = checkNotNull(difficulty);
		this.numberOfMoves = numberOfMoves;
	}
}
