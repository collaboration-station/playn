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

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import pythagoras.i.IPoint;
import pythagoras.i.Point;
import pythagoras.i.Rectangle;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import edu.bsu.issgame.core.util.ReadingOrderComparator;

public class TilePuzzle {

	public static Builder columns(int columns) {
		return new Builder(columns);
	}

	public final static class Builder {
		private int columns;
		private int rows = Integer.MIN_VALUE;
		private Point holeLocation;

		private Builder(int columns) {
			checkArgument(columns > 0);
			this.columns = columns;
		}

		public Builder rows(int rows) {
			checkArgument(rows > 0);
			this.rows = rows;
			return this;
		}

		public TilePuzzle build() {
			checkState(rows > 0, "Rows must be specified as a positive integer");
			checkNotNull(holeLocation, "Hole location must be specified");
			return new TilePuzzle(this);
		}

		public Builder withHoleAt(int column, int row) {
			checkState(rows > 0,
					"Rows must be specified before the hole location");
			checkArgument(column >= 0);
			checkArgument(column < columns);
			checkArgument(row >= 0);
			checkArgument(row < rows);
			holeLocation = new Point(column, row);
			return this;
		}
	}

	public final int rows;
	public final int cols;
	
	private final Map<Point, Tile> board = Maps.newHashMap();
	private final Rectangle bounds;

	private Point hole;

	private TilePuzzle(Builder builder) {
		rows = builder.rows;
		cols = builder.columns;
		this.bounds = new Rectangle(0, 0, builder.columns, builder.rows);
		for (int row = 0; row < bounds.height; row++) {
			for (int column = 0; column < bounds.width; column++) {
				Point p = new Point(column, row);
				Tile tile = new Tile(p);
				board.put(p, tile);
			}
		}
		this.hole = builder.holeLocation;
		board.remove(hole);
	}
	
	private TilePuzzle(int cols, int rows, Point hole, List<Tile> tiles)
	{
		this.cols=cols;
		this.rows=rows;
		this.hole=hole;
		this.bounds = new Rectangle(0,0, cols, rows);
		for(Tile tile: tiles)
		{
			board.put((Point) tile.position().get(), tile.clone());
		}
	}

	public IPoint getHole() {
		return new Point(hole);
	}
	
	public int getHoleIndex()
	{
		IPoint hole = getHole();
		int holeNumber = hole.x()+hole.y()*cols;
		return holeNumber;
		
	}

	public Set<Move> getPossibleMoves() {
		PossibleMoveSetBuilder moveSetBuilder = new PossibleMoveSetBuilder();
		for (Direction d : Direction.values()) {
			moveSetBuilder.checkDirectionFromHole(d);
		}
		return moveSetBuilder.set;
	}
	
	public Move getRandomMove()
	{
		ArrayList<Move> moves = new ArrayList<Move>(getPossibleMoves());
		int index = (int) (Math.random()*moves.size());
		return moves.get(index);
	}

	private final class PossibleMoveSetBuilder {
		private final Set<Move> set = Sets.newHashSet();

		public void checkDirectionFromHole(Direction d) {
			Point p = d.findNeighbor(hole);
			if (isInPuzzle(p)) {
				Tile t = board.get(p);
				checkState(t != null, "Tile should be present at " + p);
				set.add(new Move(t, d.opposite()));
			}
		}
	}

	private boolean isInPuzzle(Point p) {
		return bounds.outcode(p) == 0;
	}

	public final class Move {

		public final Tile tile;
		public final Direction direction;

		public Move(Tile tile, Direction direction) {
			this.tile = tile;
			this.direction = direction;
		}

		public void make() {
			updateTilePosition();
			updateHolePosition();
		}

		private void updateTilePosition() {
			IPoint tileOriginalPosition = tile.position().get();
			Point tileNewPosition = direction
					.findNeighbor(tileOriginalPosition);
			board.remove(tileOriginalPosition);
			board.put(tileNewPosition, tile);
			tile.setPosition(tileNewPosition);
		}

		private void updateHolePosition() {
			Direction toMoveHole = direction.opposite();
			hole = toMoveHole.findNeighbor(hole);
		}

		public boolean isForTile(Tile t) {
			return this.tile.equals(t);
		}
	}
	
	public boolean isSolved()
	{
		boolean solved=true;
		
		for(Tile tile: getTiles())
		{
			if(!tile.isInProperPosition())
			{
				solved=false;
			}
		}
		
		return solved;
	}

	public List<Tile> getTiles() {
		
		List<Tile> tiles = new ArrayList<Tile>(board.values());
		Collections.sort(tiles, new ReadingOrderComparator());
		
		return tiles;
	}
	
	public Tile getTile(Point initialPosition)
	{
		List<Tile> tiles = getTiles();
		
		for(Tile tile: tiles)
		{
			;
			if(tile.initialPosition.get().equals(initialPosition))
				return tile;
		}
		return null;
	}
	
	public boolean canMove(Tile tile)
	{
		Set<Move> possibleMoves = getPossibleMoves();
		for(Move possibleMove: possibleMoves)
		{
			if(possibleMove.isForTile(tile))
				return true;
		}
		return false;
	}
	
	public int checkNumberOfTilesOutOfPlace()
	{
		int numberOfTilesOutOfPlace=0;
		for(Tile tile: getTiles())
		{
			if(!tile.isInProperPosition())
			{
				numberOfTilesOutOfPlace++;
			}
		}
		return numberOfTilesOutOfPlace;
	}
	
	public Move getMove(Tile tile, Direction direction)
	{
		return new Move(tile, direction);
	}
	
	public TilePuzzle clone()
	{
		return new TilePuzzle(this.cols, this.rows, this.hole, this.getTiles());
	}
}