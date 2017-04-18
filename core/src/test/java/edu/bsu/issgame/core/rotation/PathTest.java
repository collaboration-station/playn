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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class PathTest {

	@Test(expected = IllegalArgumentException.class)
	public void testNoncontiguousPathThrowsException() {
		Path.create()//
				.add(0, 0)//
				.add(2, 2);
	}

	@Test
	public void testNodeDirectionToNext() {
		Path path = Path.create()//
				.add(0, 0)//
				.add(1, 0);
		Path.Node node = path.getNode(0);
		assertEquals(Direction.EAST, node.directionToNext());
	}

	@Test
	public void testNodeDirectionToPrev() {
		Path path = Path.create()//
				.add(0, 0)//
				.add(1, 0);
		Path.Node node = path.getNode(1);
		assertEquals(Direction.WEST, node.directionFromPrevious());
	}

	@Test(expected = IllegalStateException.class)
	public void testNode_directionToNext_oneElementPath_throwsException() {
		Path path = Path.create().add(0, 0);
		path.getNode(0).directionToNext();
	}

	@Test
	public void testEquals_equalPathsAreEqual() {
		Path path1 = Path.create().add(0, 0);
		Path path2 = Path.create().add(0, 0);
		assertTrue(path1.equals(path2));
	}
	
	@Test
	public void testEquals_differentPathsAreDifferent() {
		Path path1 = Path.create().add(0, 1);
		Path path2 = Path.create().add(0, 0);
		assertFalse(path1.equals(path2));
	}
}
