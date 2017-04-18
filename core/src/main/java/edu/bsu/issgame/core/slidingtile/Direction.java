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

import pythagoras.i.IPoint;
import pythagoras.i.Point;

public enum Direction {

	UP(0, -1), DOWN(0, +1), LEFT(-1, 0), RIGHT(+1, 0);

	private final int rowAdjustment;
	private final int columnAdjustment;

	private Direction(int columnAdjustment, int rowAdjustment) {
		this.rowAdjustment = rowAdjustment;
		this.columnAdjustment = columnAdjustment;
	}

	public Point findNeighbor(IPoint p) {
		return new Point(p.x() + columnAdjustment, p.y() + rowAdjustment);
	}

	public Direction opposite() {
		switch (this) {
		case LEFT:
			return RIGHT;
		case RIGHT:
			return LEFT;
		case UP:
			return DOWN;
		case DOWN:
			return UP;
		default:
			throw new IllegalStateException("Unhandled direction " + this);
		}
	}
}
