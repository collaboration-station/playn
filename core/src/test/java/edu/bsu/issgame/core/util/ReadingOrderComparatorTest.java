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

import static org.junit.Assert.*;

import org.junit.Test;

import edu.bsu.issgame.core.slidingtile.Tile;
import pythagoras.i.Point;

public class ReadingOrderComparatorTest {

	private ReadingOrderComparator c = new ReadingOrderComparator();
	
	@Test
	public void testCompareToSortsTwoItemsWithDifferentY() {
		Tile a =  new Tile(new Point(0, 0));
		Tile b =  new Tile(new Point(0, 1));
		int result = c.compare(a, b);
		assertTrue(result < 0);
	}

	@Test
	public void testCompareSortsTwoItemsWithDifferentX() {
		Tile a =  new Tile(new Point(0, 0));
		Tile b =  new Tile(new Point(1, 0));
		int result = c.compare(a, b);
		assertTrue(result < 0);
	}

	@Test
	public void testCompareSortsTwoItemsWithDifferentXAndY() {
		Tile a = new Tile(new Point(0,0));
		Tile b = new Tile (new Point(0, 1));
		int result = c.compare(a, b);
		assertTrue(result < 0);
	}
	
	@Test
	public void testCompareSortsTwoEqualItems() {
		Tile a =  new Tile(new Point(1, 0));
		Tile b =  new Tile(new Point(1, 0));
		int result = c.compare(a, b);
		assertEquals(0, result);
	}
}
