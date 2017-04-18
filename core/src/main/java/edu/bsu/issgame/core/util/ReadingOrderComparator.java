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
package edu.bsu.issgame.core.util;

import java.util.Comparator;

import edu.bsu.issgame.core.slidingtile.Tile;

public class ReadingOrderComparator implements Comparator<Tile> {

	@Override
	public int compare(Tile a, Tile b) {
		int yDiff = a.position().get().y() - b.position().get().y();
		if (yDiff != 0) {
			return yDiff;
		} else {
			return a.position().get().x()-b.position().get().x();
		}
	}
}
