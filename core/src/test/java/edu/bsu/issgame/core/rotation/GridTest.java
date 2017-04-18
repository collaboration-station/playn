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
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.util.EnumSet;

import org.junit.Test;

import pythagoras.i.Dimension;
import pythagoras.i.Point;
import react.Slot;
import edu.bsu.issgame.core.rotation.Grid.Cell;

public class GridTest {

	private Grid grid;
	private Path path;

	@Test
	public void testHasDirectionFromStart() {
		givenA4x4GridWithStartAtOriginAndFinishOpposite();
		assertTrue(grid.at(0, 0).has(Direction.WEST));
	}

	private void givenA4x4GridWithStartAtOriginAndFinishOpposite() {
		grid = Grid.create(new Dimension(4, 4))//
				.withStart(Direction.WEST).of(0, 0)//
				.andFinish(Direction.EAST).of(3, 3);
	}

	@Test
	public void testHasDirectionToEnd() {
		givenA4x4GridWithStartAtOriginAndFinishOpposite();
		assertTrue(grid.at(3, 3).has(Direction.EAST));
	}

	@Test(expected = IllegalArgumentException.class)
	public void testStartAndEndAreTheSame_throwsException() {
		Grid.create(new Dimension(4, 4))//
				.withStart(Direction.WEST).of(0, 0)//
				.andFinish(Direction.WEST).of(0, 0);
	}

	@Test
	public void testDoesPathConnectStartToEnd_oneElementPath_false() {
		Grid grid = Grid.create(new Dimension(3, 3))//
				.withStart(Direction.WEST).of(0, 0)//
				.andFinish(Direction.EAST).of(0, 2);
		Path path = Path.create()//
				.add(0, 0);
		assertFalse(grid.doesPathConnectStartToEnd(path));
	}

	@Test
	public void testDoesPathConnectStartToEnd_smallGrid_true() {
		givenA1x2GridAndPathDefinedToCrossIt();
		assertTrue(grid.doesPathConnectStartToEnd(path));
	}

	private void givenA1x2GridAndPathDefinedToCrossIt() {
		grid = Grid.create(new Dimension(2, 1))//
				.withStart(Direction.WEST).of(0, 0)//
				.andFinish(Direction.EAST).of(1, 0);
		path = Path.create()//
				.add(0, 0)//
				.add(1, 0);
	}

	@Test
	public void testLayPath_1x2grid_originHasEast() {
		givenA1x2GridAndPathDefinedToCrossIt();
		grid.layPath(path);
		assertTrue(grid.at(0, 0).has(Direction.EAST));
	}

	@Test
	public void testLayPath_1x2grid_rightSideHasWest() {
		givenA1x2GridAndPathDefinedToCrossIt();
		grid.layPath(path);
		assertTrue(grid.at(1, 0).has(Direction.WEST));
	}

	@Test
	public void testIsSolved_notSolved() {
		givenA4x4GridWithStartAtOriginAndFinishOpposite();
		assertFalse(grid.isSolved());
	}

	@Test
	public void testIsSolved_connectionToEndNotTouchingStart_fails() {
		grid = Grid.create(new Dimension(3, 3))//
				.withStart(Direction.WEST).of(0, 0)//
				.andFinish(Direction.EAST).of(2, 2);
		Path pathFromStart = Path.create().add(0, 0)//
				.add(0, 1)//
				.add(1, 1);
		Path pathToEnd = Path.create().add(2, 1).add(2, 2);
		grid.layPath(pathFromStart);
		grid.layPath(pathToEnd);
		grid.at(1, 1).add(Direction.EAST);
		grid.at(2, 1).add(Direction.NORTH);
		assertFalse(grid.isSolved());
	}

	@Test
	public void testIsSolved_1x2grid_solved() {
		givenA1x2GridWithTheSolutionPathLaid();
		assertTrue(grid.isSolved());
	}

	private void givenA1x2GridWithTheSolutionPathLaid() {
		givenA1x2GridAndPathDefinedToCrossIt();
		grid.layPath(path);
	}

	@Test
	public void testIsSolved_3x3_sPath_solved() {
		givenA3x3Grid();
		path = Path.create()//
				.add(0, 0)//
				.add(1, 0)//
				.add(1, 1)//
				.add(1, 2)//
				.add(2, 2);
		grid.layPath(path);
		assertTrue(grid.isSolved());
	}

	private void givenA3x3Grid() {
		grid = Grid.create(new Dimension(3, 3)).withStart(Direction.WEST)
				.of(0, 0)//
				.andFinish(Direction.EAST).of(2, 2);
	}

	@Test
	public void testIsSolved_3x3WithAllConnections_solved() {
		givenA3x3CompleteGrid();
		assertTrue(grid.isSolved());
	}

	private void givenA3x3CompleteGrid() {
		givenA3x3Grid();
		for (Cell cell : grid.cells()) {
			for (Direction d : Direction.values()) {
				cell.add(d);
			}
		}
	}

	@Test
	public void testCell_rotate_northToEast() {
		givenA3x3Grid();
		Cell cell = grid.at(1, 1);
		checkState(cell.directions().isEmpty());
		cell.add(Direction.NORTH);
		cell.turnRight();
		EnumSet<Direction> expected = EnumSet.of(Direction.EAST);
		assertEquals(expected, cell.directions());
	}

	@Test
	public void testCell_rotate_emitsSignal() {
		givenA3x3Grid();
		Cell cell = grid.at(1, 1);
		cell.add(Direction.NORTH);
		Slot<Cell> slot = makeMockSlotIgnoringWarnings();
		cell.onTurnRight().connect(slot);
		cell.turnRight();
		verify(slot).onEmit(cell);
	}

	@SuppressWarnings("unchecked")
	private Slot<Cell> makeMockSlotIgnoringWarnings() {
		return mock(Slot.class);
	}

	@Test
	public void testCells_returnsNineCellsFor3x3Grid() {
		givenA3x3Grid();
		int count = 0;
		// Using 'cell' for iteration, ignoring the fact it's not used.
		for (@SuppressWarnings("unused")
		Cell cell : grid.cells()) {
			count++;
		}
		assertEquals(9, count);
	}

	@Test
	public void testCellEquals_identityEquality() {
		givenA3x3Grid();
		Cell cellA = grid.at(0, 0);
		assertTrue(cellA.equals(cellA));
	}

	@Test
	public void testCellEquals_differentCellsWithSameContentAreUnequal() {
		givenA3x3Grid();
		Cell middleCell = grid.at(1, 1);
		Cell belowMiddleCell = grid.at(1, 2);
		assertFalse(middleCell.equals(belowMiddleCell));
	}

	@Test
	public void testCellIsConnectedTo_notConnectedIsFalse() {
		givenA3x3Grid();
		Cell middleCell = grid.at(1, 1);
		assertFalse(middleCell.isConnectedTo(Direction.WEST));
	}

	@Test
	public void testCellIsConnectedTo_connectedOnGridIsTrue() {
		givenA3x3Grid();
		Cell upperLeft = grid.at(0, 0);
		upperLeft.add(Direction.EAST);
		grid.at(1, 0).add(Direction.WEST);
		assertTrue(upperLeft.isConnectedTo(Direction.EAST));
	}

	@Test
	public void testCellIsConnectedTo_pathOffGrid_false() {
		givenA3x3Grid();
		Cell upperLeft = grid.at(0, 0);
		upperLeft.add(Direction.NORTH);
		assertFalse(upperLeft.isConnectedTo(Direction.NORTH));
	}

	@Test
	public void testCellConnectedNeighbors_noNeighbors_isEmpty() {
		givenA3x3Grid();
		Cell center = grid.at(1, 1);
		assertTrue(center.connectedNeighbors().isEmpty());
	}

	@Test
	public void testCellConnectedNeighbors_oneNeighbors_sizeIsOne() {
		givenA1x2GridWithTheSolutionPathLaid();
		assertEquals(1, grid.at(0, 0).connectedNeighbors().size());
	}

	@Test
	public void testCellConnectedNeighbors_oneNeighbors_neighborIsTheNeighbor() {
		givenA1x2GridWithTheSolutionPathLaid();
		Cell neighbor = grid.at(1, 0);
		assertEquals(neighbor, grid.at(0, 0).connectedNeighbors().iterator()
				.next());
	}

	@Test
	public void testIsOnGrid_offGrid_false() {
		givenA3x3Grid();
		assertFalse(grid.isOnGrid(new Point(-1, -1)));
	}

	@Test(expected = IndexOutOfBoundsException.class)
	public void testAt_offGrid_throwsException() {
		givenA3x3Grid();
		grid.at(-1, -1);
	}

	@Test
	public void testCellIsConnectedToStart_empty_false() {
		givenA3x3Grid();
		for (Cell cell : grid.cells()) {
			assertFalse(cell.isConnectedToStart());
		}
	}

	@Test
	public void testConnectedToStart_emptyGrid_hasOneElement() {
		givenA3x3Grid();
		assertEquals(1, grid.connectedToStart().size());
	}

	@Test
	public void testConnectedToStart_emptyGrid_theOneElementIsTheStart() {
		givenA3x3Grid();
		Cell onlyConnectedCell = grid.connectedToStart().toArray(new Cell[] {})[0];
		assertEquals(grid.at(0, 0), onlyConnectedCell);
	}

	@Test
	public void testConnectedToStart_largePath_sizeIsCorrect() {
		givenA3x3PathWithZigZagSolution();
		assertEquals(5, grid.connectedToStart().size());
	}

	private void givenA3x3PathWithZigZagSolution() {
		givenA3x3Grid();
		Path path = Path.create()//
				.add(0, 0)//
				.add(1, 0)//
				.add(1, 1)//
				.add(1, 2)//
				.add(2, 2);
		grid.layPath(path);
	}

	@Test
	public void testConnectedToStart_pathWithLoop_sizeIsCorrect() {
		givenA3x3PathWithZigZagSolution();
		grid.at(0, 0).add(Direction.SOUTH);
		grid.at(0, 1).add(Direction.NORTH)//
				.add(Direction.EAST);
		grid.at(1, 1).add(Direction.WEST);
		assertEquals(6, grid.connectedToStart().size());
	}
}