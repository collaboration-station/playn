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

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import pythagoras.i.Point;

public class DirectionTest {

	@Test
	public void testFromOffset() {
		Point right = new Point(1, 0);
		Direction d = Direction.fromOffset(right);
		assertEquals(Direction.EAST, d);
	}
	
	@Test
	public void testOf() {
		Point start = new Point(0,0);
		Point expected = new Point(1,0);
		assertEquals(expected, Direction.EAST.of(start));
	}
}
