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

import pythagoras.i.Point;

public enum PuzzleDifficulty {
	EASY(new Point(3, 2), 9, 2), //
	MEDIUM(new Point(3, 3), 12, 3), //
	HARD(new Point(4, 3), 15, 5), //
	EXTREME(new Point(4, 4), 18, 6);

	public final Point puzzleSize;
	public final int score;
	public final int minimumMovesRequired;

	PuzzleDifficulty(Point puzzleSize, int score, int minMovesRequired) {
		this.puzzleSize = puzzleSize;
		this.score = score;
		this.minimumMovesRequired = minMovesRequired;
	}
}