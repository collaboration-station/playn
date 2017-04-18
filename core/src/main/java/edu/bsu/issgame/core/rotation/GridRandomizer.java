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
package edu.bsu.issgame.core.rotation;

import java.util.Random;

import edu.bsu.issgame.core.rotation.Grid.Cell;

public class GridRandomizer {

	private static final GridRandomizer INSTANCE = new GridRandomizer();

	public static GridRandomizer instance() {
		return INSTANCE;
	}

	private final Random random = new Random();

	private GridRandomizer() {
	}

	public void randomize(Grid grid) {
		do {
			for (Cell cell : grid.cells()) {
				int randomRotationCount = random.nextInt(3);
				for (int i = 0; i < randomRotationCount; i++) {
					cell.turnRight();
				}
			}
		} while (grid.isSolved());
	}

}
