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
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import pythagoras.i.IDimension;
import pythagoras.i.IPoint;
import pythagoras.i.Point;
import pythagoras.i.Rectangle;
import react.Signal;
import react.SignalView;

import com.google.common.base.MoreObjects;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import edu.bsu.issgame.core.rotation.Path.Node;

public class Grid {

	public static Builder create(IDimension size) {
		return new Builder(size);
	}

	public static final class Builder {

		private final IDimension size;
		private Direction startDirection;
		private IPoint startCell;
		private Direction finishDirection;
		private IPoint finishCell;

		public Builder(IDimension size) {
			this.size = checkNotNull(size);
		}

		public StartBuilder withStart(Direction direction) {
			startDirection = checkNotNull(direction);
			return new StartBuilder();
		}

		public FinishBuilder andFinish(Direction direction) {
			finishDirection = checkNotNull(direction);
			return new FinishBuilder();
		}

		public class StartBuilder {
			public Builder of(int x, int y) {
				startCell = new Point(x, y);
				return Builder.this;
			}
		}

		public class FinishBuilder {
			public Grid of(int x, int y) {
				finishCell = new Point(x, y);
				return build();
			}
		}

		private Grid build() {
			if (startCell.equals(finishCell))
				throw new IllegalArgumentException(
						"Start and finish must be different");
			return new Grid(this);
		}
	}

	public final class Cell {
		private boolean isConnectedToStart;
		private final IPoint position;
		private final EnumSet<Direction> directions = EnumSet
				.noneOf(Direction.class);

		private final Signal<Cell> onTurnRight = Signal.create();

		private Cell(IPoint position) {
			this.position = position;
		}

		public boolean isConnectedToStart() {
			return isConnectedToStart;
		}

		public void setConnectedToStart(boolean isConnectedToStart) {
			this.isConnectedToStart = isConnectedToStart;
		}

		public boolean has(Direction d) {
			return directions.contains(d);
		}

		public Cell add(Direction d) {
			isConnectedToStart = false;
			directions.add(d);
			return this;
		}

		public ImmutableSet<Direction> directions() {
			return Sets.immutableEnumSet(directions);
		}

		@Override
		public String toString() {
			return MoreObjects.toStringHelper(this)//
					.add("directions", directions)//
					.toString();
		}

		public SignalView<Cell> onTurnRight() {
			return onTurnRight;
		}

		public void turnRight() {
			EnumSet<Direction> rotatedDirections = EnumSet
					.noneOf(Direction.class);
			for (Direction dir : directions) {
				rotatedDirections.add(dir.turnRight());
			}
			directions.clear();
			directions.addAll(rotatedDirections);
			onTurnRight.emit(this);
		}

		public boolean isConnectedTo(Direction direction) {
			if (has(direction)) {
				IPoint neighborPosition = direction.of(position);
				if (isOnGrid(neighborPosition)) {
					return at(neighborPosition).has(direction.opposite());
				}
			}
			return false;
		}

		public Collection<Cell> connectedNeighbors() {
			List<Cell> result = Lists.newArrayList();
			for (Direction direction : Direction.values()) {
				if (isConnectedTo(direction)) {
					Cell neighbor = at(direction.of(position));
					result.add(neighbor);
				}
			}
			return result;
		}
	}

	public final Direction startDirection;
	public final Direction finishDirection;
	private final IDimension size;
	private final IPoint startCell;
	private final IPoint finishCell;
	private final Rectangle bounds;
	private static final float T_PROBABILITY = 0.2f;

	private Map<IPoint, Cell> map = Maps.newHashMap();

	private Grid(Builder builder) {
		this.size = builder.size;
		this.startDirection = builder.startDirection;
		this.finishDirection = builder.finishDirection;
		this.startCell = builder.startCell;
		this.finishCell = builder.finishCell;
		this.bounds = new Rectangle(0, 0, size.width(), size.height());
		initializeEmptyMap();
		putDirectionMarkersForStartAndFinishCells();
	}

	private void initializeEmptyMap() {
		for (int col = 0; col < size.width(); col++) {
			for (int row = 0; row < size.height(); row++) {
				Point position = new Point(col, row);
				map.put(position, new Cell(position));
			}
		}
	}

	private void putDirectionMarkersForStartAndFinishCells() {
		map.get(startCell).directions.add(startDirection);
		map.get(finishCell).directions.add(finishDirection);
	}

	public boolean doesPathConnectStartToEnd(Path path) {
		return path.start().location().equals(startCell)
				&& path.end().location().equals(finishCell);
	}

	public void layPath(Path path) {
		for (Node node : path) {
			checkArgument(isOnGrid(node.location()), "Path node not on grid: "
					+ node);
			Cell cell = map.get(node.location());
			if (node.hasPrevious()) {
				Direction d = node.directionFromPrevious();
				cell.directions.add(d);
			}
			if (node.hasNext()) {
				Direction d = node.directionToNext();
				cell.directions.add(d);
			}
		}
	}

	public boolean isOnGrid(IPoint location) {
		return bounds.contains(location);
	}

	public final Cell at(int x, int y) {
		return at(new Point(x, y));
	}

	public Cell at(IPoint location) {
		if (!isOnGrid(location)) {
			throw new IndexOutOfBoundsException();
		} else {
			return map.get(location);
		}
	}

	public int width() {
		return size.width();
	}

	public int height() {
		return size.height();
	}

	public void addFillCells() {
		for (IPoint p : map.keySet()) {
			Cell cell = map.get(p);
			int numberOfDirections;
			if (1 - Math.random() < T_PROBABILITY) {
				numberOfDirections = 3;
			} else {
				numberOfDirections = 2;
			}
			while (cell.directions().size() < numberOfDirections) {
				cell.add(Direction.getRandom());
			}
		}
	}

	public boolean isSolved() {
		Cell start = at(startCell);
		Cell finish = at(finishCell);
		if (start.has(startDirection) && finish.has(finishDirection)) {
			Collection<Cell> connected = connectedToStart();
			return connected.contains(finish);
		}
		return false;
	}

	public ArrayList<Cell> getCells() {
		ArrayList<Cell> cells = new ArrayList<Cell>();
		for (IPoint point : map.keySet()) {
			cells.add(map.get(point));
		}
		return cells;
	}

	public IPoint startPoint() {
		return startCell;
	}

	public IPoint finishPoint() {
		return finishCell;
	}

	public Iterable<Cell> cells() {
		return map.values();
	}

	public Collection<Cell> connectedToStart() {
		Set<Cell> result = Sets.newHashSet();
		Cell c = at(startCell);
		if (c.has(startDirection)) {
			result.add(c);
			recurse(c, result);
		}
		return result;
	}

	private void recurse(Cell c, Set<Cell> result) {
		for (Cell neighbor : c.connectedNeighbors()) {
			if (!result.contains(neighbor)) {
				result.add(neighbor);
				recurse(neighbor, result);
			}
		}
	}
}
