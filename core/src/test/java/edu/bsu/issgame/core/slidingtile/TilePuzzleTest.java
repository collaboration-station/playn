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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.util.Set;

import org.junit.Test;
import org.mockito.Matchers;

import pythagoras.i.IPoint;
import pythagoras.i.Point;
import react.ValueView;
import react.ValueView.Listener;
import edu.bsu.issgame.core.slidingtile.TilePuzzle.Move;

public class TilePuzzleTest {

	private TilePuzzle puzzle;

	@Test
	public void testGetHole_2x1puzzle_onTheRight() {
		givenA2x1PuzzleWithTheHoleOnTheRight();
		IPoint actual = puzzle.getHole();
		IPoint expected = new Point(1, 0);
		assertEquals(expected, actual);
	}

	private void givenA2x1PuzzleWithTheHoleOnTheRight() {
		puzzle = TilePuzzle.columns(2).rows(1).withHoleAt(1, 0).build();
	}

	@Test
	public void testGetHole_2x1puzzle_onTheLeft() {
		givenA2x1PuzzleWithTheHoleOnTheLeft();
		IPoint actual = puzzle.getHole();
		IPoint expected = new Point(0, 0);
		assertEquals(expected, actual);
	}

	private void givenA2x1PuzzleWithTheHoleOnTheLeft() {
		puzzle = TilePuzzle.columns(2).rows(1).withHoleAt(0, 0).build();
	}

	@Test
	public void testGetMoves_2x1puzzle_oneMove() {
		givenA2x1PuzzleWithTheHoleOnTheRight();
		assertNumberOfPossibleMoves(1);
	}

	private void assertNumberOfPossibleMoves(int expectedNumberOfMoves) {
		Set<Move> moves = puzzle.getPossibleMoves();
		assertEquals(expectedNumberOfMoves, moves.size());
	}

	@Test
	public void testGetMoves_3x3puzzleHoleInCenter_fourMoves() {
		puzzle = TilePuzzle.columns(3).rows(3).withHoleAt(1, 1).build();
		assertNumberOfPossibleMoves(4);
	}

	@Test
	public void testMove_2x1puzzleHoleOnRight_holeIsNowOnLeft() {
		givenA2x1PuzzleWithTheHoleOnTheRight();
		whenWeMakeTheFirstPossibleMove();
		thenTheHoleIsAt(new Point(0, 0));
	}

	@Test
	public void testThatSolvedTileIsSolved() {
		givenA2x1PuzzleWithTheHoleOnTheRight();
		assertTrue(puzzle.getTiles().get(0).isInProperPosition());
	}

	@Test
	public void testThatUnsolvedTileIsNotSolved() {
		givenA2x1PuzzleWithTheHoleOnTheRight();
		whenWeMakeTheFirstPossibleMove();
		assertTrue(!puzzle.getTiles().get(0).isInProperPosition());
	}

	@Test
	public void testThatSolvedPuzzleIsSolved() {
		givenA2x1PuzzleWithTheHoleOnTheRight();
		assertTrue(puzzle.isSolved());
	}

	@Test
	public void testThatUnsolvedPuzzleIsNotSolved() {
		givenA2x1PuzzleWithTheHoleOnTheRight();
		whenWeMakeTheFirstPossibleMove();
		assertTrue(!puzzle.isSolved());
	}

	private void whenWeMakeTheFirstPossibleMove() {
		puzzle.getPossibleMoves().iterator().next().make();
	}

	private void thenTheHoleIsAt(IPoint expected) {
		IPoint actual = puzzle.getHole();
		assertEquals(expected, actual);
	}

	@Test
	public void testMove_2x1puzzleHoleOnLeft_holeIsNowOnRight() {
		givenA2x1PuzzleWithTheHoleOnTheLeft();
		whenWeMakeTheFirstPossibleMove();
		thenTheHoleIsAt(new Point(1, 0));
	}

	@Test
	public void testGetTiles_2x1puzzleHoleOnRight_tileIsOnTheLeft() {
		givenA2x1PuzzleWithTheHoleOnTheRight();
		Tile tile = puzzle.getTiles().iterator().next();
		assertEquals(new Point(0, 0), tile.position().get());
	}

	@Test
	public void testGetTiles_2x1puzzleHoleOnLeft_tileIsOnTheRight() {
		givenA2x1PuzzleWithTheHoleOnTheLeft();
		Tile tile = puzzle.getTiles().iterator().next();
		assertEquals(new Point(1, 0), tile.position().get());
	}

	@Test
	public void testMove_2x1puzzleHoleOnRight_tileMovesToTheRight() {
		givenA2x1PuzzleWithTheHoleOnTheRight();
		whenWeMakeTheFirstPossibleMove();
		Tile tile = puzzle.getTiles().iterator().next();
		assertEquals(new Point(1, 0), tile.position().get());
	}

	@Test
	public void testMove_tileEmitsSignal() {
		ValueView.Listener<IPoint> listener = createMockListenerIgnoringWarnings();
		givenA2x1PuzzleWithTheHoleOnTheLeft();
		Tile tile = puzzle.getTiles().iterator().next();
		tile.position().connect(listener);
		whenWeMakeTheFirstPossibleMove();
		verify(listener).onChange(Matchers.any(Point.class),
				Matchers.any(Point.class));
	}
	
	@Test
	public void testThatPuzzleCloneCreatesAPuzzle()
	{
		givenA2x1PuzzleWithTheHoleOnTheRight();
		TilePuzzle clone = puzzle.clone();
		assertTrue(clone!=null);
	}
	
	@Test
	public void testThatPuzzleCloneIsNotEqualToOriginal()
	{
		givenA2x1PuzzleWithTheHoleOnTheRight();
		TilePuzzle clone = puzzle.clone();
		assertTrue(clone!=puzzle);
	}
	
	@Test
	public void testThatPuzzleClonesTilesAreNotEqualToOriginal()
	{
		givenA2x1PuzzleWithTheHoleOnTheRight();
		TilePuzzle clone = puzzle.clone();
		assertTrue(clone.getTile(new Point(0,0))!=puzzle.getTile(new Point(0,0)));
	}
	
	@Test
	public void testThatPuzzleTilesAreEqualToOriginal()
	{
		givenA2x1PuzzleWithTheHoleOnTheRight();
		assertTrue(puzzle.getTiles().get(0)==puzzle.getTiles().get(0));
	}
	
	@Test
	public void testTileEqualsMethodReturnsTrueIfTwoTilesAreEqual()
	{
		givenA2x1PuzzleWithTheHoleOnTheRight();
		Tile other = puzzle.getTile(new Point(0,0));
		Tile tile = puzzle.getTile(new Point(0,0)).clone();
		assertTrue(tile.equals(other));
	}
	
	@Test
	public void testTileEqualsMethodReturnsFalseIfTwoTilesAreUnequal()
	{
		puzzle = TilePuzzle.columns(2).rows(2).withHoleAt(1, 1).build();
		Tile tile = puzzle.getTile(new Point(0,0));
		Tile other = puzzle.getTile(new Point(0,1));
		assertTrue(!tile.equals(other));
	}
	
	@Test
	public void testThatPuzzleClonesTilesHaveTheSameValue()
	{
		givenA2x1PuzzleWithTheHoleOnTheRight();
		TilePuzzle clone = puzzle.clone();
		assertTrue(clone.getTile(new Point(0,0)).equals(puzzle.getTile(new Point(0,0))));
	}
	@Test
	public void testThatNumberOfTilesOutOfPlaceInUnshuffledPuzzleIsZero()
	{
		givenA2x1PuzzleWithTheHoleOnTheRight();
		assertTrue(puzzle.checkNumberOfTilesOutOfPlace()==0);
	}
	public void testthatNumberOfTilesOutOfPlaceInSolvedPuzzleIsZero()
	{
		givenA2x1PuzzleWithTheHoleOnTheRight();
		puzzle.getRandomMove().make();
		puzzle.getRandomMove().make();
		assertTrue(puzzle.checkNumberOfTilesOutOfPlace()==0);
	}
	@Test
	public void testThatNumberOfTilesOutOfPlaceInSlidPuzzleIsOne()
	{
		givenA2x1PuzzleWithTheHoleOnTheRight();
		puzzle.getRandomMove().make();
		assertTrue(puzzle.checkNumberOfTilesOutOfPlace()==1);
	}
	private Listener<IPoint> createMockListenerIgnoringWarnings() {
		@SuppressWarnings("unchecked")
		ValueView.Listener<IPoint> listener = mock(ValueView.Listener.class);
		return listener;
	}

}
