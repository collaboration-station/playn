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

import java.util.Set;

public class TileOrientation {
	public int rightTurns = 0;
	public TileType type;
	private Set<Direction> directions;

	public TileOrientation(Set<Direction> directions){
		this.directions = directions;
		setTileType(directions);
		findNumberOfRightTurns();
	}

	private void setTileType(Set<Direction> directions) {
		if (isBarTile(directions)) {
			type = TileType.BAR_TILE;
		} else if (isLTile(directions)) {
			type = TileType.L_TILE;
		} else {
			type = TileType.T_TILE;
		}
	}
	
	
	private boolean isBarTile(Set<Direction> directions) {
		if ((directions.contains(Direction.NORTH) && //
				directions.contains(Direction.SOUTH) &&
				directions.size() == 2)//
				|| (directions.contains(Direction.EAST) && //
				directions.contains(Direction.WEST) &&
				directions.size() == 2)) {
			return true;
		} else {
			return false;
		}
	}

	private boolean isLTile(Set<Direction> directions) {
		if ((directions.contains(Direction.NORTH) && //
				directions.contains(Direction.EAST) &&//
				directions.size() == 2)
				|| (directions.contains(Direction.EAST) && //
				directions.contains(Direction.SOUTH) && //
				directions.size() == 2) //
				|| (directions.contains(Direction.SOUTH) && //
				directions.contains(Direction.WEST) && //
				directions.size() == 2) //
				|| (directions.contains(Direction.WEST) && //
				directions.contains(Direction.NORTH) && //
				directions.size() == 2)) {
			return true;
		} else {
			return false;
		}
	}
	
	private void findNumberOfRightTurns() {
		switch (type) {
		case BAR_TILE:
			evaluateAsBarTile();
			break;
		case L_TILE:
			evaluateAsLTile();
			break;
		case T_TILE:
			evaluateAsTTile();
			break;
		}
	}

	private void evaluateAsBarTile() {
		if (directions.contains(Direction.EAST) && //
				directions.contains(Direction.WEST)){
			rightTurns = 1;
		}
	}

	private void evaluateAsLTile() {
		if(directions.contains(Direction.EAST) && directions.contains(Direction.SOUTH)){
			rightTurns = 1;
		} else if (directions.contains(Direction.SOUTH) && directions.contains(Direction.WEST)){
			rightTurns = 2;
		} else if (directions.contains(Direction.WEST) && directions.contains(Direction.NORTH)){
			rightTurns = 3;
		}
	}

	private void evaluateAsTTile() {
		if(!directions.contains(Direction.NORTH)){
			rightTurns = 1;
		} else if (!directions.contains(Direction.EAST)){
			rightTurns = 2;
		} else if (!directions.contains(Direction.SOUTH)){
			rightTurns = 3;
		}
	}
}
