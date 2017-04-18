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

import static playn.core.PlayN.graphics;
import static playn.core.PlayN.pointer;

import java.util.ArrayList;
import java.util.List;

import playn.core.CanvasImage;
import playn.core.Image;
import playn.core.ImageLayer;
import playn.core.Pointer;
import playn.core.Pointer.Event;
import pythagoras.i.Dimension;
import pythagoras.i.Point;
import pythagoras.i.Rectangle;
import react.RFuture;
import react.RPromise;
import react.Slot;
import tripleplay.anim.Animation.Delay;
import tripleplay.ui.Button;
import tripleplay.ui.Style;
import tripleplay.ui.layout.AbsoluteLayout;
import tripleplay.ui.util.BoxPoint;
import tripleplay.util.PointerInput;
import edu.bsu.issgame.core.AbstractGameScreen;
import edu.bsu.issgame.core.CommonGameScreenUI;
import edu.bsu.issgame.core.CustomStyleSheet;
import edu.bsu.issgame.core.GameFont;
import edu.bsu.issgame.core.GameImage;
import edu.bsu.issgame.core.GameSound;
import edu.bsu.issgame.core.MinigameType;
import edu.bsu.issgame.core.slidingtile.TilePuzzle.Move;
import edu.bsu.issgame.core.util.ImageSlicer;

public class SlidingTilePuzzleScreen extends CommonGameScreenUI {

	private static final double X_MARGIN = 0.1;
	private static final double Y_MARGIN = 0.1;

	private static final double X_MARGIN_CELL = 0.15;
	private static final double Y_MARGIN_CELL = 0.15;

	private static final float ANIMATION_MS = 100f;
	private static final float LONG_ANIMATION_MS = 500f;
	private static final float SOLVE_TRANSITION_MS = 1000f;
	private static final float PUZZLE_IMAGE_DELAY = 500f;
	private static final float PUZZLE_IMAGE_FADE = 500f;

	private final Rectangle regionOfInterest;
	private TilePuzzle puzzleModel;
	private TilePuzzle clonePuzzle;
	private PuzzleDifficulty difficulty;
	private final PuzzleFactory puzzleFactory = new PuzzleFactory();
	private ResetButton resetButton;
	private CanvasImage holeImage;
	private Image puzzleImage;
	private PuzzleBundle puzzle;

	private PointerInput pInput = new PointerInput();
	private List<Move> shuffleMoves = new ArrayList<Move>();
	private List<TileLayer> tileLayers = new ArrayList<TileLayer>();
	private List<TileLayer> cloneList = new ArrayList<TileLayer>();

	public SlidingTilePuzzleScreen(AbstractGameScreen previous) {
		super(previous);
		pointer().setListener(pInput.plistener);
		regionOfInterest = getRegionOfInterest();
		initializePuzzle();
		showOriginalPuzzleImage();
		addResetButton();
		setBackground(GameImage.SLIDING_PUZZLE_BACKGROUND.image);
		shortDelayThenShufflePuzzle();
	}

	@Override
	protected void setMinigameType() {
		thisMinigame = MinigameType.SLIDING_PUZZLE;
	}

	private Rectangle getRegionOfInterest() {
		Rectangle ROI = new Rectangle();

		int marginX = (int) (scale.playmatSize.width * (X_MARGIN));
		int marginY = (int) (scale.playmatSize.height * (Y_MARGIN));

		int estimatedScoreBufferY = (int) (scale.screenSize.height * 0.05);

		int x = scale.playmatRect.x + marginX;
		int y = scale.playmatRect.y + marginY + estimatedScoreBufferY;

		int width = (int) (scale.playmatSize.width - (marginX * 2));
		int height = (int) (scale.playmatSize.height - (marginY * 2) + estimatedScoreBufferY);

		ROI.setBounds(x, y, width, height);
		System.out.print("ROI" + ROI);
		return ROI;
	}

	private Dimension getCellSize() {
		Dimension cellSize = new Dimension(regionOfInterest.width
				/ puzzleModel.cols, regionOfInterest.height()
				/ puzzleModel.rows);
		return cellSize;
	}

	private Dimension getImageSize() {
		Dimension imageSize;

		int cellSizeX = getCellSize().width;
		int cellSizeY = getCellSize().height;

		imageSize = new Dimension(
				(int) (cellSizeX - calculateXMarginCell() * 2),
				(int) (cellSizeY - (calculateYMarginCell() * 2)));
		return imageSize;
	}

	private int calculateXMarginCell() {
		int cellMarginX = (int) (getCellSize().width * X_MARGIN_CELL / puzzleModel.cols);
		return cellMarginX;
	}

	private int calculateYMarginCell() {
		int cellMarginY = (int) (getCellSize().height * Y_MARGIN_CELL / puzzleModel.rows);
		return cellMarginY;
	}

	private void initializePuzzle() {
		puzzle = puzzleFactory.nextPuzzle();
		difficulty = puzzle.difficulty;
		Point puzzleSize = difficulty.puzzleSize;
		puzzleImage = puzzle.image.image;
		List<CanvasImage> puzzlePieceImages = ImageSlicer.cuts(puzzleImage)
				.intoSlicesAlongAxis(puzzleSize.x, puzzleSize.y);
		puzzleModel = TilePuzzle.columns(puzzleSize.x).rows(puzzleSize.y)
				.withHoleAt(puzzleSize.x - 1, puzzleSize.y - 1).build();
		ArrayList<TileLayer> unshuffledLayer = new ArrayList<TileLayer>();
		for (int n = 0; n < puzzlePieceImages.size(); n++) {
			CanvasImage image = puzzlePieceImages.get(n);
			Tile tile;
			if (n < puzzleModel.getHoleIndex()) {
				tile = puzzleModel.getTiles().get(n);
			} else if (n == puzzleModel.getHoleIndex()) {
				tile = null;
				holeImage = puzzlePieceImages.get(n);
			} else {
				tile = puzzleModel.getTiles().get(n - 1);
			}
			if (tile != null) {
				TileLayer tileLayer = new TileLayer(tile, image);
				unshuffledLayer.add(tileLayer);
			}
		}
		for (TileLayer t : unshuffledLayer) {
			t.addToScreen();
		}
		clonePuzzle = puzzleModel.clone();
	}

	private void showOriginalPuzzleImage() {
		final ImageLayer originalImage = graphics().createImageLayer(
				puzzleImage);
		originalImage.setSize(regionOfInterest.width, regionOfInterest.height);
		layer.addAt(originalImage, getRegionOfInterest().x,
				getRegionOfInterest().y);
		anim.delay(PUZZLE_IMAGE_DELAY)//
				.then()//
				.tweenAlpha(originalImage)//
				.to(0)//
				.in(PUZZLE_IMAGE_FADE)//
				.then()//
				.action(new Runnable() {
					@Override
					public void run() {
						layer.remove(originalImage);
					}
				});
	}

	private void shortDelayThenShufflePuzzle() {
		shuffleMoves.add(puzzleModel.getRandomMove());
		anim.delay(1000).then().action(new Runnable() {
			@Override
			public void run() {
				shufflePuzzle(puzzle.numberOfMoves);
			}
		});

	}

	private void shufflePuzzle(final int numberOfMoves) {
		int shuffleDelay = 100;
		Move randomMove = puzzleModel.getRandomMove();
		Move lastMove = shuffleMoves.get(shuffleMoves.size() - 1);
		while (randomMove.direction == lastMove.direction.opposite()) {
			randomMove = puzzleModel.getRandomMove();
		}
		for (final TileLayer tileLayer : tileLayers) {
			if (randomMove.tile.equals(tileLayer.tile)) {
				final Move moveToMake = randomMove;
				anim.action(new Runnable() {
					@Override
					public void run() {
						if (makeMove(tileLayer.tile)) {
							tileLayer.animateLayer(ANIMATION_MS);
							shuffleMoves.add(moveToMake);
						} else {
							shufflePuzzle(numberOfMoves);
						}
					}
				})//
						.then()//
						.delay(shuffleDelay)//
						.then()//
						.action(new Runnable() {
							@Override
							public void run() {
								if (numberOfMoves > 0)
									shufflePuzzle(numberOfMoves - 1);
							}
						});
			}
		}
	}

	private boolean makeMove(Tile tile) {
		for (Move m : puzzleModel.getPossibleMoves()) {
			if (m.isForTile(tile)) {
				m.make();
				return true;
			}
		}
		return false;
	}

	private final class TileLayer {
		public final ImageLayer tileLayer;
		public final Tile tile;

		private boolean inputEnabled;

		public TileLayer(final Tile tile, Image image) {
			this.tile = tile;
			tileLayer = graphics().createImageLayer(image);
			tileLayer.setSize(getImageSize().width, getImageSize().height);
			pInput.register(tileLayer, new Pointer.Adapter() {
				@Override
				public void onPointerStart(Event event) {
					if (inputEnabled) {
						makeMove(tile);
						animateLayer(ANIMATION_MS);
						handleSolvedPuzzle();
					}
				}
			});
		}

		public void addToScreen(Point p) {
			if (!tileLayers.contains(this)) {
				tileLayers.add(this);
				layer.addAt(tileLayer, getDestination().x, getDestination().y);
			}
		}

		public void addToScreen() {
			addToScreen(new Point(0, 0));
		}

		public void setInputEnabled(boolean inputEnabled) {
			this.inputEnabled = inputEnabled;
		}

		private RFuture<Void> animateLayer(float duration) {
			final RPromise<Void> promise = RPromise.create();
			GameSound.TILE_SLIDE.sound.play();
			setInputEnabled(false);
			anim.tweenTranslation(tileLayer)//
					.to(getDestination().x, getDestination().y)//
					.in(duration)//
					.easeInOut()//
					.then()//
					.action(new Runnable() {
						@Override
						public void run() {
							setInputEnabled(true);
							promise.succeed(null);
						}
					});
			return promise;
		}

		public Point getDestination() {
			int cellMarginX = calculateXMarginCell();
			int cellMarginY = calculateYMarginCell();
			final int destinationX = regionOfInterest.x + cellMarginX
					+ tile.position().get().x() * getCellSize().width;
			final int destinationY = regionOfInterest.y + cellMarginY
					+ tile.position().get().y() * getCellSize().height;

			return new Point(destinationX, destinationY);
		}

		public Delay moveToSolvedPosition() {
			int cellMarginX = calculateXMarginCell();
			int cellMarginY = calculateYMarginCell();
			final int destinationX = regionOfInterest.x + cellMarginX
					* puzzleModel.cols + tile.position().get().x()
					* (getCellSize().width - cellMarginX * 2);
			final int destinationY = regionOfInterest.y + cellMarginY
					* puzzleModel.rows + tile.position().get().y()
					* (getCellSize().height - cellMarginY * 2);
			return anim.tweenTranslation(tileLayer)//
					.to(destinationX, destinationY)//
					.in(LONG_ANIMATION_MS)//
					.easeIn()//
					.then()//
					.delay(SOLVE_TRANSITION_MS);
		}

	}

	private void handleSolvedPuzzle() {
		if (puzzleModel.isSolved()) {
			resetButton.setEnabled(false);
			for (TileLayer layer : tileLayers) {
				layer.setInputEnabled(false);
			}
			GameSound.SLIDING_PUZZLE_COMPLETE.sound.play();
			score.update(score.get().addMaintenance(difficulty.score));
			animateReset();
		}
	}

	private void animateReset() {
		anim.delay(ANIMATION_MS * 2).then()//
				.action(new Runnable() {
					@Override
					public void run() {
						showHole();
					}
				}).then()//
				.delay(ANIMATION_MS * 3).then().action(new Runnable() {
					@Override
					public void run() {
						moveAllTileLayersToSolvedPosition();
					}

				}).then().delay(1000).then().action(new Runnable() {
					@Override
					public void run() {
						setNewBoard();
						resetButton.setEnabled(true);
					}

				});
	}

	private void moveAllTileLayersToSolvedPosition() {
		for (TileLayer layer : tileLayers) {
			layer.setInputEnabled(false);
			anim.add(layer.moveToSolvedPosition());
		}
	}

	private void showHole() {
		TileLayer hole = new TileLayer(new Tile(new Point(puzzleModel.getHole()
				.x(), puzzleModel.getHole().y())), holeImage);
		hole.addToScreen(hole.getDestination());
	}

	private void setNewBoard() {
		clearScreen();
		initializePuzzle();
		showOriginalPuzzleImage();
		shortDelayThenShufflePuzzle();
	}

	private void clearScreen() {
		for (TileLayer tileLayer : tileLayers) {
			layer.remove(tileLayer.tileLayer);
		}
		tileLayers.clear();
		cloneList.clear();
	}

	@SuppressWarnings("unused")
	private TileLayer getTileLayer(Tile tile) {
		for (TileLayer layer : tileLayers) {
			if (layer.tile.equals(tile)) {
				return layer;
			}
		}
		return null;
	}

	private void addResetButton() {
		resetButton = new ResetButton();
		iface.createRoot(new AbsoluteLayout(), CustomStyleSheet.instance(),
				layer)//
				.setSize(graphics().width() * 0.98f,
						graphics().height() * 0.98f)//
				.add(resetButton);
	}

	private void resetBoard() {
		ArrayList<TileLayer> clones = new ArrayList<TileLayer>();
		for (TileLayer layer : tileLayers) {
			clones.add(new TileLayer(clonePuzzle
					.getTile(layer.tile.initialPosition.get()), layer.tileLayer
					.image()));
		}
		clearScreen();
		puzzleModel = clonePuzzle;
		for (TileLayer layer : clones) {
			layer.addToScreen();
		}
		clonePuzzle = puzzleModel.clone();
		showOriginalPuzzleImage();
		shortDelayThenShufflePuzzle();
	}

	private final class ResetButton extends Button {

		public ResetButton() {
			super("Reset");
			setConstraint(AbsoluteLayout.uniform(BoxPoint.BR));
			addStyles(Style.FONT.is(GameFont.PLAIN.font));
			onClick(new Slot<Button>() {
				@Override
				public void onEmit(Button event) {
					resetBoard();
				}
			});
		}
	}

	@Override
	public void wasHidden() {
		super.wasHidden();
		pointer().setListener(null);
	}

	
}
