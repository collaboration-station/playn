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

import java.util.List;
import java.util.Queue;

import com.google.common.collect.Lists;

public class PuzzleFactory {

	private final Queue<Integer> numberOfMovesQueue = Lists.newLinkedList();
	private final Queue<PuzzleDifficulty> puzzleDifficultyQueue = Lists.newLinkedList();
	private final List<PuzzleViewData> puzzleImages = Lists.newArrayList();

	public PuzzleFactory() {
		fillImageList();
		initPuzzleDifficultyQueue();
		initNumberOfMovesQueue();
	}
	
	private void fillImageList() {
		for (PuzzleViewData puzzleImage : PuzzleViewData.values()) {
			puzzleImages.add(puzzleImage);
		}
	}

	private void initPuzzleDifficultyQueue() {
		puzzleDifficultyQueue.add(PuzzleDifficulty.EASY);
		puzzleDifficultyQueue.add(PuzzleDifficulty.EASY);
		puzzleDifficultyQueue.add(PuzzleDifficulty.EASY);
		puzzleDifficultyQueue.add(PuzzleDifficulty.MEDIUM);
		puzzleDifficultyQueue.add(PuzzleDifficulty.MEDIUM);
		puzzleDifficultyQueue.add(PuzzleDifficulty.MEDIUM);
		puzzleDifficultyQueue.add(PuzzleDifficulty.MEDIUM);
		puzzleDifficultyQueue.add(PuzzleDifficulty.HARD);
		puzzleDifficultyQueue.add(PuzzleDifficulty.HARD);
		puzzleDifficultyQueue.add(PuzzleDifficulty.HARD);
		puzzleDifficultyQueue.add(PuzzleDifficulty.EXTREME);
	}
	
	private void initNumberOfMovesQueue() {
		numberOfMovesQueue.add(2);
		numberOfMovesQueue.add(3);
		numberOfMovesQueue.add(3);
		numberOfMovesQueue.add(4);
		numberOfMovesQueue.add(4);
		numberOfMovesQueue.add(5);
		numberOfMovesQueue.add(5);
		numberOfMovesQueue.add(2);
		numberOfMovesQueue.add(4);
		numberOfMovesQueue.add(5);
	}

	private PuzzleDifficulty getDifficulty() {
		if (puzzleDifficultyQueue.size() == 1) {
			return puzzleDifficultyQueue.peek();
		} else {
			return puzzleDifficultyQueue.remove();
		}
	}

	public PuzzleBundle nextPuzzle() {
		int index = (int) (Math.random() * puzzleImages.size());
		PuzzleViewData puzzleImage =  puzzleImages.remove(index);
		if (puzzleImages.isEmpty()) {
			fillImageList();
		}
		return new PuzzleBundle(puzzleImage, getDifficulty(), getNumberOfMoves());
	}

	private int getNumberOfMoves() {
		int numberOfMoves;
		if (numberOfMovesQueue.size() > 1) {
			numberOfMoves = numberOfMovesQueue.remove();
		} else if (numberOfMovesQueue.size() == 1) {
			numberOfMoves = numberOfMovesQueue.peek();
		} else {
			numberOfMoves = 0;
		}
		return numberOfMoves;
	}
}
