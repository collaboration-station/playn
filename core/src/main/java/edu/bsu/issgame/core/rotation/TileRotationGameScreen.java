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
import static playn.core.PlayN.graphics;
import static playn.core.PlayN.pointer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import playn.core.Image;
import playn.core.ImageLayer;
import playn.core.Layer;
import playn.core.Layer.HasSize;
import playn.core.Pointer;
import playn.core.Pointer.Event;
import pythagoras.f.Dimension;
import pythagoras.f.MathUtil;
import pythagoras.i.Point;
import react.Slot;
import tripleplay.util.Input.Registration;
import tripleplay.util.PointerInput;

import com.google.common.collect.Lists;

import edu.bsu.issgame.core.AbstractGameScreen;
import edu.bsu.issgame.core.CommonGameScreenUI;
import edu.bsu.issgame.core.GameImage;
import edu.bsu.issgame.core.GameSound;
import edu.bsu.issgame.core.MinigameType;
import edu.bsu.issgame.core.rotation.Grid.Cell;

public class TileRotationGameScreen extends CommonGameScreenUI {

	private static final int POINTS_PER_BOARD = 10;
	private static final float ROTATION_DURATION_MS = 250f;
	private static final Dimension TILE_SIZE = new Dimension(
			graphics().width() * 0.10f, //
			graphics().width() * 0.10f);
	private static final float GUTTER = TILE_SIZE.width * 0.06f;
	private static final float X_MARGIN = graphics().width() * 0.3f;
	private static final float Y_MARGIN = graphics().height() * 0.2f;
	private static final pythagoras.i.Dimension GRID_SIZE = new pythagoras.i.Dimension(
			4, 4);

	private static final Runnable INPUT_ENABLER = new Runnable() {
		@Override
		public void run() {
			pointer().setEnabled(true);
		}
	};
	private static final Runnable INPUT_DISABLER = new Runnable() {
		@Override
		public void run() {
			pointer().setEnabled(false);
		}
	};

	private List<Layer> startAndEndlayers = new ArrayList<Layer>();
	private List<Tile> tiles = new ArrayList<Tile>();
	private Layer endTile;
	private Layer endTileComplete;
	private Grid grid;
	private final PointerInput pointerInput = new PointerInput();
	private final Runnable solutionChecker = new Runnable() {
		@Override
		public void run() {
			checkIfSolved();
		}
	};
	private final Runnable imageUpdater = new Runnable() {
		private boolean solved;
		private Collection<Cell> cellsConnectedToStart;

		@Override
		public void run() {
			solved = grid.isSolved();
			updateEndBlockLayer();
			cellsConnectedToStart = grid.connectedToStart();
			for (Tile tile : tiles) {
				updateImageFor(tile);
			}
		}

		private void updateEndBlockLayer() {
			endTile.setVisible(!solved);
			endTileComplete.setVisible(solved);
		}

		private void updateImageFor(Tile tile) {
			if (cellsConnectedToStart.contains(tile.cell)) {
				if (solved) {
					tile.useCircuitImage();
				} else {
					tile.useGlowImage();
				}
			} else {
				tile.useNormalImage();
			}
		}
	};
	private final List<Registration> pointerRegistrations = Lists
			.newArrayList();

	public TileRotationGameScreen(AbstractGameScreen previous) {
		super(previous);
		setBackground(GameImage.TILE_ROTATION_BACKGROUND.image);
		pointer().setListener(pointerInput.plistener);
		initGrid();
		addStartAndEndPoints();
		addTiles();
		imageUpdater.run();
	}

	private void addStartAndEndPoints() {
		addEndPoint();
		addStartPoint();
	}

	private void addEndPoint() {
		ImageLayer end = createEndTile(false);
		ImageLayer endComplete = createEndTile(true);
		int deltaRowEnd = grid.finishDirection.xMod;
		int deltaColEnd = grid.finishDirection.yMod;
		Point endLocation = getTileLocation(grid.finishPoint().x()
				+ deltaColEnd, grid.finishPoint().y() + deltaRowEnd);
		endTile = end;
		layer.addAt(end, endLocation.x, endLocation.y);
		startAndEndlayers.add(end);
		anim.setVisible(endComplete, false);
		layer.addAt(endComplete, endLocation.x, endLocation.y);
		startAndEndlayers.add(endComplete);
		endTileComplete = endComplete;
	}

	private ImageLayer createEndTile(boolean isComplete) {
		ImageLayer layer;
		if (isComplete) {
			layer = graphics().createImageLayer(
					GameImage.FINISH_ROTATION_COMPLETE.image);
		} else {
			layer = graphics()
					.createImageLayer(GameImage.FINISH_ROTATION.image);
		}
		layer.setSize(TILE_SIZE.width, TILE_SIZE.height);
		layer.setOrigin(layer.width() / 2, layer.height() / 2);
		return layer;
	}

	private void addStartPoint() {
		ImageLayer start = createStartTile();
		int deltaRowStart = grid.startDirection.xMod;
		int deltaColStart = grid.startDirection.yMod;
		Point startLocation = getTileLocation(grid.startPoint().x()
				+ deltaColStart, grid.startPoint().y() + deltaRowStart);
		layer.addAt(start, startLocation.x, startLocation.y);
		startAndEndlayers.add(start);
	}

	private ImageLayer createStartTile() {
		ImageLayer layer = graphics().createImageLayer(
				GameImage.START_ROTATION.image);
		layer.setSize(TILE_SIZE.width, TILE_SIZE.height);
		layer.setOrigin(layer.width() / 2, layer.height() / 2);
		return layer;
	}

	private static Point getTileLocation(int row, int col) {
		Point p = new Point();
		int x = (int) (X_MARGIN + col * (TILE_SIZE.width + GUTTER) + TILE_SIZE.width / 2);
		int y = (int) (Y_MARGIN + row * (TILE_SIZE.height + GUTTER) + TILE_SIZE.height / 2);
		p.setLocation(x, y);
		return p;
	}

	private void initGrid() {
		grid = Grid.create(GRID_SIZE).withStart(Direction.WEST).of(0, 0)
				.andFinish(Direction.EAST)
				.of(GRID_SIZE.width - 1, GRID_SIZE.height - 1);
		PathGenerator gen = PathGenerator.create();
		Path path = gen.generatePathFor(grid);
		grid.layPath(path);
		grid.addFillCells();
		GridRandomizer.instance().randomize(grid);
	}

	private void addTiles() {
		for (int col = 0; col < grid.width(); col++) {
			for (int row = 0; row < grid.height(); row++) {
				final Cell cell = (grid.at(col, row));
				final Tile tile = new Tile(cell);
				Layer.HasSize tileLayer = (HasSize) tile.layer;
				pointerRegistrations.add(pointerInput.register(tileLayer,
						new Pointer.Adapter() {
							@Override
							public void onPointerStart(Event event) {
								GameSound.ZAP.sound.play();
								cell.turnRight();
							}
						}));
				cell.onTurnRight().connect(new TileRotator(tileLayer));
				float x = X_MARGIN + col * (TILE_SIZE.width + GUTTER)
						+ TILE_SIZE.width / 2;
				float y = Y_MARGIN + row * (TILE_SIZE.height + GUTTER)
						+ TILE_SIZE.height / 2;
				layer.addAt(tileLayer, x, y);
				tiles.add(tile);
			}
		}
	}

	private final class Tile {
		final Cell cell;
		final ImageLayer layer;
		private final Image tileImage;
		private final Image glowTileImage;
		private final Image finishedTileImage;

		private Tile(Cell cell) {
			this.cell = cell;
			TileOrientation orientation = new TileOrientation(cell.directions());
			tileImage = TileImageBundle.NORMAL.imageFor(orientation);
			glowTileImage = TileImageBundle.GLOW.imageFor(orientation);
			finishedTileImage = TileImageBundle.CIRCUIT.imageFor(orientation);
			layer = graphics().createImageLayer(tileImage);
			layer.setSize(TILE_SIZE.width, TILE_SIZE.height);
			layer.setOrigin(layer.width() / 2, layer.height() / 2);
			layer.setRotation((float) (orientation.rightTurns * (Math.PI / 2)));
		}

		public void useCircuitImage() {
			layer.setImage(finishedTileImage);
		}

		public void useGlowImage() {
			layer.setImage(glowTileImage);
		}

		public void useNormalImage() {
			layer.setImage(tileImage);
		}
	}

	private final class TileRotator extends Slot<Cell> {
		private final Layer tileLayer;

		public TileRotator(Layer layer) {
			this.tileLayer = checkNotNull(layer);
		}

		@Override
		public void onEmit(Cell event) {
			anim.action(INPUT_DISABLER)//
					.then()//
					.tweenRotation(tileLayer)//
					.to(tileLayer.rotation() + MathUtil.HALF_PI)//
					.in(ROTATION_DURATION_MS)//
					.easeInOut()//
					.then()//
					.action(imageUpdater)//
					.then()//
					.action(solutionChecker);
		}
	}

	private void checkIfSolved() {
		if (grid.isSolved()) {
			incrementScore();
			fadeOutAndResetBoard();
		} else {
			INPUT_ENABLER.run();
		}
	}

	private void incrementScore() {
		int amountToAdd = POINTS_PER_BOARD;
		score.update(score.get().addMaintenance(amountToAdd));
	}

	private void fadeOutAndResetBoard() {
		GameSound.SUCCESS.sound.play();
		final Collection<Cell> solutionCells = grid.connectedToStart();
		final List<Tile> solutionTiles = Lists.newArrayList();
		final List<Tile> quickFadeTiles = Lists.newArrayList();
		for (Tile tile : tiles) {
			if (solutionCells.contains(tile.cell)) {
				solutionTiles.add(tile);
			} else {
				quickFadeTiles.add(tile);
			}
		}
		animateNonSuccessTiles(quickFadeTiles);
		animateSuccessTilesThenResetBoard(solutionTiles);
	}

	private void animateNonSuccessTiles(List<Tile> quickFadeTiles) {
		for (Tile t : quickFadeTiles) {
			quickFade(t);
		}
	}

	private void animateSuccessTilesThenResetBoard(List<Tile> solutionTiles) {
		final float tileZoomMs = 250f;
		final float delayAfterZoomMs = 250f;
		final float successFadeMs = 1000f;
		final float delayBeforeReset = tileZoomMs + delayAfterZoomMs
				+ successFadeMs;
		for (Tile t : solutionTiles) {
			anim.tweenScale(t.layer)//
					.to(1.1f)//
					.in(tileZoomMs)//
					.easeIn()//
					.then()//
					.delay(delayAfterZoomMs)//
					.then()//
					.tweenAlpha(t.layer)//
					.to(0)//
					.in(successFadeMs);
		}
		anim.delay(delayBeforeReset)//
				.then().action(new Runnable() {
					@Override
					public void run() {
						resetBoard();
					}
				});
	}

	private void quickFade(Tile t) {
		anim.tweenAlpha(t.layer)//
				.to(0)//
				.in(250f)//
				.easeOut();
	}

	private void resetBoard() {
		for (Layer layerToRemove : startAndEndlayers) {
			layer.remove(layerToRemove);
		}
		startAndEndlayers.clear();
		for (Tile tile : tiles) {
			layer.remove(tile.layer);
		}
		tiles.clear();
		initGrid();
		addStartAndEndPoints();
		addTiles();
		imageUpdater.run();
		INPUT_ENABLER.run();
	}

	@Override
	protected void setMinigameType() {
		thisMinigame = MinigameType.TILE_ROTATION;
	}

	@Override
	public void wasHidden() {
		super.wasHidden();
		for (Registration registration : pointerRegistrations) {
			registration.cancel();
		}
		pointer().setListener(null);
	}

}
