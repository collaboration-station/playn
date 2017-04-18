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

import static com.google.common.base.Preconditions.checkArgument;
import pythagoras.i.IPoint;
import pythagoras.i.Point;

public enum Direction {
	NORTH(0, -1), EAST(1, 0), SOUTH(0, 1), WEST(-1, 0);

	final public int xMod;
	public final int yMod;

	private Direction(int xMod, int yMod) {
		this.xMod = xMod;
		this.yMod = yMod;
	}

	public static Direction getRandom(){
		int totalNumberOfDirections = Direction.values().length;
		int randomIndex = (int) (Math.random()*totalNumberOfDirections);
		return Direction.values()[randomIndex];
	}
	
	public static Direction fromOffset(IPoint p) {
		final int x = p.x();
		final int y = p.y();
		checkArgument(x == 0 ^ y == 0, "One axis must be zero");
		if (x > 0)
			return EAST;
		else if (x < 0)
			return WEST;
		else if (y > 0)
			return SOUTH;
		else
			return NORTH;
	}

	public Direction opposite() {
		switch (this) {
		case NORTH:
			return SOUTH;
		case SOUTH:
			return NORTH;
		case EAST:
			return WEST;
		case WEST:
			return EAST;
		default:
			throw new IllegalStateException();
		}
	}

	public IPoint of(IPoint start) {
		return new Point(start.x() + xMod, start.y() + yMod);
	}

	public Direction turnRight() {
		switch (this) {
		case NORTH:
			return EAST;
		case SOUTH:
			return WEST;
		case EAST:
			return SOUTH;
		case WEST:
			return NORTH;
		default:
			throw new IllegalStateException();
		}
	}
}
