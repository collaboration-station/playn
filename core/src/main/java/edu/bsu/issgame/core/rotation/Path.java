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

import static com.google.common.base.Preconditions.checkState;

import java.util.Iterator;
import java.util.List;

import pythagoras.i.IPoint;
import pythagoras.i.Point;
import pythagoras.i.Points;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import com.google.common.collect.Lists;

import edu.bsu.issgame.core.rotation.Path.Node;

public class Path implements Iterable<Node> {

	public final class Node {

		private Direction directionToNext;
		private Direction directionFromPrevious;
		private final IPoint location;

		private Node(IPoint location) {
			this.location = location;
		}

		public boolean hasNext() {
			return directionToNext != null;
		}

		public Direction directionToNext() {
			checkState(directionToNext != null);
			return directionToNext;
		}

		public IPoint location() {
			return location;
		}

		public boolean hasPrevious() {
			return directionFromPrevious != null;
		}

		public Direction directionFromPrevious() {
			checkState(directionFromPrevious != null);
			return directionFromPrevious;
		}

		@Override
		public int hashCode() {
			return Objects.hashCode(location);
		}

		@Override
		public boolean equals(Object obj) {
			if (obj == null)
				return false;
			if (obj instanceof Node) {
				Node other = (Node) obj;
				return Objects.equal(this.location, other.location);
			} else
				return false;
		}

		@Override
		public String toString() {
			return MoreObjects.toStringHelper(this)//
					.add("location", location)//
					.add("directionToNext", directionToNext)//
					.add("directionFromPrevious", directionFromPrevious)//
					.toString();

		}
	}

	public static Path create() {
		return new Path();
	}

	private final List<Node> list = Lists.newArrayList();

	private Path() {
	}

	public Path add(int x, int y) {
		if (list.isEmpty()) {
			addFirstNode(x, y);
		} else {
			addNonFirstNode(x, y);
		}
		return this;
	}

	private void addFirstNode(Node n) {
		list.add(n);
	}

	private void addFirstNode(int x, int y) {
		addFirstNode(new Node(new Point(x, y)));
	}

	private void addNonFirstNode(final int x, final int y) {
		Point newPoint = new Point(x, y);
		if (!isOrthogonalToLastPoint(newPoint))
			;
		// checkArgument(isOrthogonalToLastPoint(newPoint));
		Node prev = list.get(list.size() - 1);
		IPoint prevLocation = prev.location;
		IPoint toNext = new Point(x - prevLocation.x(), y - prevLocation.y());
		prev.directionToNext = Direction.fromOffset(toNext);
		Node newNode = new Node(newPoint);
		newNode.directionFromPrevious = prev.directionToNext.opposite();
		list.add(newNode);
	}

	// TODO fix
	public boolean isOrthogonalToLastPoint(Point newPoint) {
		checkState(!list.isEmpty(), "Path is empty, cannot compare.");
		IPoint lastPoint = end().location;
		return Points.manhattanDistance(lastPoint.x(), newPoint.x,
				lastPoint.y(), newPoint.y) == 1;
	}

	public Node start() {
		return list.get(0);
	}

	public Node end() {
		return list.get(list.size() - 1);
	}

	public Node getNode(int i) {
		return list.get(i);
	}

	public int size() {
		return list.size();
	}

	@Override
	public Iterator<Node> iterator() {
		return list.iterator();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null)
			return false;
		if (obj == this)
			return true;
		if (obj instanceof Path) {
			Path other = (Path) obj;
			return this.list.equals(other.list);
		}
		return false;
	}

}