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

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;
import java.util.Random;

import pythagoras.i.IPoint;

import com.google.common.collect.Lists;

public class PathGenerator {

	public static PathGenerator create() {
		return new PathGenerator();
	}

	private static final Random random = new Random();

	private List<IPoint> list = Lists.newArrayList();
	private boolean foundAPathFromStartToFinish = false;
	private Grid grid;

	private PathGenerator() {
	}

	public Path generatePathFor(Grid grid) {
		this.grid = checkNotNull(grid);
		do {
			list.clear();
			list.add(grid.startPoint());
			foundAPathFromStartToFinish = tryToMakeAPath();
		} while (!foundAPathFromStartToFinish);
		Path result = buildPathFromList();
		cleanUp();
		return result;
	}

	private boolean tryToMakeAPath() {
		while (true) {
			IPoint lastThing = list.get(list.size() - 1);
			Direction d = randomDirection();
			IPoint neighbor = d.of(lastThing);
			if (isLegalMove(neighbor)) {
				list.add(neighbor);
				if (neighbor.equals(grid.finishPoint())) {
					return true;
				}
			} else {
				return false;
			}
		}
	}
	
	private static Direction randomDirection() {
		int index = random.nextInt(Direction.values().length);
		return Direction.values()[index];
	}

	private boolean isLegalMove(IPoint neighbor) {
		return grid.isOnGrid(neighbor) && !list.contains(neighbor);
	}

	private Path buildPathFromList() {
		Path result = Path.create();
		for (IPoint p : list)
			result.add(p.x(), p.y());
		return result;
	}

	private void cleanUp() {
		list.clear();
		foundAPathFromStartToFinish = false;
		grid = null;
	}
}
