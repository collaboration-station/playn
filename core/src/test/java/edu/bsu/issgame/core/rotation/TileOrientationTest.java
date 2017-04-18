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
import static org.junit.Assert.assertTrue;

import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

public class TileOrientationTest {
	
	@Test
	public void testTileOrientation_tileType_Bar(){
		Set<Direction> directions = new HashSet<Direction>();
		directions.add(Direction.NORTH);
		directions.add(Direction.SOUTH);
		TileOrientation tileOrientation = new TileOrientation(directions);
		assertTrue(tileOrientation.type.equals(TileType.BAR_TILE));
	}
	
	@Test
	public void testTileOrientation_tileType_L(){
		Set<Direction> directions = new HashSet<Direction>();
		directions.add(Direction.NORTH);
		directions.add(Direction.EAST);
		TileOrientation tileOrientation = new TileOrientation(directions);
		assertTrue(tileOrientation.type.equals(TileType.L_TILE));
	}
	
	@Test 
	public void testTileOrientation_tileType_T(){
		Set<Direction> directions = new HashSet<Direction>();
		directions.add(Direction.NORTH);
		directions.add(Direction.EAST);
		directions.add(Direction.SOUTH);
		TileOrientation tileOrientation = new TileOrientation(directions);
		assertEquals(TileType.T_TILE, tileOrientation.type);
	}
	
	@Test
	public void testRightTurns_properlyOrientedTile_0(){
		Set<Direction> directions = new HashSet<Direction>();
		directions.add(Direction.NORTH);
		directions.add(Direction.SOUTH);
		TileOrientation tileOrientation = new TileOrientation(directions);
		int expected = 0;
		int actual = tileOrientation.rightTurns;
		assertEquals(expected, actual);
	}
	
	@Test
	public void testRightTurns_eastWestBarTile_1(){
		Set<Direction> directions = new HashSet<Direction>();
		directions.add(Direction.EAST);
		directions.add(Direction.WEST);
		TileOrientation tileOrientation = new TileOrientation(directions);
		int expected = 1;
		int actual = tileOrientation.rightTurns;
		assertEquals(expected, actual);
	}
	
	@Test
	public void testRightTurns_eastSouthLTile(){
		Set<Direction> directions = new HashSet<Direction>();
		directions.add(Direction.EAST);
		directions.add(Direction.SOUTH);
		TileOrientation tileOrientation = new TileOrientation(directions);
		int expected = 1;
		int actual = tileOrientation.rightTurns;
		assertEquals(expected, actual);
	}
	
	@Test
	public void testRightTurns_southWestLTile(){
		Set<Direction> directions = new HashSet<Direction>();
		directions.add(Direction.SOUTH);
		directions.add(Direction.WEST);
		TileOrientation tileOrientation = new TileOrientation(directions);
		int expected = 2;
		int actual = tileOrientation.rightTurns;
		assertEquals(expected, actual);
	}
	
	@Test
	public void testRightTurns_westNorthLTile(){
		Set<Direction> directions = new HashSet<Direction>();
		directions.add(Direction.WEST);
		directions.add(Direction.NORTH);
		TileOrientation tileOrientation = new TileOrientation(directions);
		int expected = 3;
		int actual = tileOrientation.rightTurns;
		assertEquals(expected, actual);
	}
	
	@Test
	public void testRightTurns_eastSouthWestTTile(){
		Set<Direction> directions = new HashSet<Direction>();
		directions.add(Direction.EAST);
		directions.add(Direction.SOUTH);
		directions.add(Direction.WEST);
		TileOrientation tileOrientation = new TileOrientation(directions);
		int expected = 1;
		int actual = tileOrientation.rightTurns;
		assertEquals(expected, actual);
	}
	
	@Test
	public void testRightTurns_southWestNorthTTile(){
		Set<Direction> directions = new HashSet<Direction>();
		directions.add(Direction.SOUTH);
		directions.add(Direction.WEST);
		directions.add(Direction.NORTH);
		TileOrientation tileOrientation = new TileOrientation(directions);
		int expected = 2;
		int actual = tileOrientation.rightTurns;
		assertEquals(expected, actual);
	}
	
	@Test
	public void testRightTurns_westNorthEastTTile(){
		Set<Direction> directions = new HashSet<Direction>();
		directions.add(Direction.WEST);
		directions.add(Direction.NORTH);
		directions.add(Direction.EAST);
		TileOrientation tileOrientation = new TileOrientation(directions);
		int expected = 3;
		int actual = tileOrientation.rightTurns;
		assertEquals(expected, actual);
	}
}
