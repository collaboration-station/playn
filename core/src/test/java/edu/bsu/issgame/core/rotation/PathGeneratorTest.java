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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import pythagoras.i.Dimension;

import com.google.common.collect.Lists;

import edu.bsu.issgame.core.net.HeadlessTestCase;

public class PathGeneratorTest extends HeadlessTestCase {

	private Grid grid;
	private PathGenerator generator;

	@Before
	public void setup() {
		generator = PathGenerator.create();
	}

	@Test
	public void testGeneratedPathCompletesThePuzzle() {
		givenA3x3GridWithNorthwestStartAndSoutheastFinish();
		Path path = generator.generatePathFor(grid);
		grid.layPath(path);
		assertTrue(grid.isSolved());
	}

	private void givenA3x3GridWithNorthwestStartAndSoutheastFinish() {
		grid = Grid.create(new Dimension(3, 3))//
				.withStart(Direction.WEST).of(0, 0)//
				.andFinish(Direction.EAST).of(2, 2);
	}

	@Test
	public void testGeneratedPathsAreGeneratedRandomly() {
		final int numberOfPaths = 10;
		givenA3x3GridWithNorthwestStartAndSoutheastFinish();
		List<Path> pathList = generatePaths(numberOfPaths);
		assertFalse(areAllPathsTheSame(pathList));
	}

	private boolean areAllPathsTheSame(List<Path> pathList) {
		for (int i = 0; i < pathList.size() - 1; i++) {
			for (int j = 1; j < pathList.size(); j++) {
				Path path1 = pathList.get(i);
				Path path2 = pathList.get(j);
				if (!path1.equals(path2)) {
					return false;
				}
			}
		}
		return true;
	}

	private List<Path> generatePaths(int numberOfPaths) {
		List<Path> pathList = Lists.newArrayList();
		for (int i = 0; i < numberOfPaths; i++) {
			pathList.add(generator.generatePathFor(grid));
		}
		return pathList;
	}
}
