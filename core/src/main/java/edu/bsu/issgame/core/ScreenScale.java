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
package edu.bsu.issgame.core;

import pythagoras.i.Dimension;
import pythagoras.i.Point;
import pythagoras.i.Rectangle;
import playn.core.Image;

import playn.core.PlayN;

public class ScreenScale {

	// this is the screen size we designed for, which will be used to guide the
	// scaling
	private static final Dimension originalSize = new Dimension(1920, 1280);
	// 1.5
	public static final double originalScale = getRatio(originalSize);

	private Dimension testDimension;
	// this is the screen size of the device you are using
	public final Dimension screenSize = getScreenSize();
	public final double screenRatio = ((double) (screenSize.width()))
			/ ((double) (screenSize.height()));
	public final Dimension playmatSize = getPlaymatSize();
	public final Rectangle playmatRect = getPlaymatRect();
	public final Rectangle uncroppedBackgroundRect = getUncroppedBackgroundRect();
	public final Rectangle backgroundRect = getCroppedBackgroundRect();

	private static final boolean isScaled = true;
	private static final boolean isBackgroundScaled = true;

	public void setTestDimension(Dimension d) {
		testDimension = d;
	}

	public Image getBackgroundSubImage(Image originalImage) {
		float yScale = originalImage.height() / uncroppedBackgroundRect.height;
		float xScale = originalImage.width() / uncroppedBackgroundRect.width;

		int scaledHeight = (int) (backgroundRect.height * yScale);
		int scaledWidth = (int) (backgroundRect.width * xScale);
		int scaledX = (int) (backgroundRect.x * xScale);
		int scaledY = (int) (backgroundRect.y * yScale);

		Image subImage = originalImage.subImage(scaledX, scaledY, scaledWidth,
				scaledHeight);
		return subImage;
	}

	public Dimension getScaledScreenSize() {
		if (isScaled) {
			return playmatSize;
		} else
			return screenSize;
	}

	public Point getScaledPosition() {
		if (isScaled) {
			return playmatRect.location();
		}
		return new Point(0, 0);
	}

	public Point getBackgroundPosition() {
		if (isBackgroundScaled) {
			return backgroundRect.location();
		} else
			return new Point(0, 0);
	}

	public Dimension getBackgroundSize() {
		if (isBackgroundScaled) {
			return backgroundRect.size();
		} else
			return screenSize;
	}

	/**
	 * The scale is not the original scale, because it is cropped. The size
	 * should equal the screen size
	 */
	private Rectangle getCroppedBackgroundRect() {

		int uncroppedBackgroundWidth = uncroppedBackgroundRect.width;
		int uncroppedBackgroundHeight = uncroppedBackgroundRect.height;

		int backgroundX = Math
				.abs((screenSize.width - uncroppedBackgroundWidth) / 2);
		int backgroundY = Math
				.abs((screenSize.height - uncroppedBackgroundHeight) / 2);
		int croppedBackgroundWidth = uncroppedBackgroundWidth
				- (backgroundX * 2);
		int croppedBackgroundHeight = uncroppedBackgroundHeight
				- (backgroundY * 2);

		Rectangle croppedBackgroundRect = new Rectangle(backgroundX,
				backgroundY, croppedBackgroundWidth, croppedBackgroundHeight);
		return croppedBackgroundRect;
	}

	/**
	 * This method returns a rectangle that is the same scale as the original
	 * scale, and is close to the screen size
	 */
	private Rectangle getUncroppedBackgroundRect() {
		Rectangle uncroppedBackgroundRect = new Rectangle();
		int uncroppedBackgroundWidth;
		int uncroppedBackgroundHeight;

		// the sign is reversed from getScaledSize, BUT the contents are not
		if (screenRatio <= originalScale) {
			uncroppedBackgroundWidth = (int) (screenSize.height * originalScale);
			uncroppedBackgroundHeight = screenSize.height;
		} else {
			uncroppedBackgroundWidth = screenSize.width;
			uncroppedBackgroundHeight = (int) (screenSize.width / originalScale);
		}
		uncroppedBackgroundRect.setBounds(0, 0, uncroppedBackgroundWidth,
				uncroppedBackgroundHeight);
		return uncroppedBackgroundRect;
	}

	private static double getRatio(Dimension dimension) {
		return ((double) (dimension.width())) / ((double) (dimension.height()));
	}

	/**
	 * Returns the size of the screen, whether on android devices or on java.
	 * Must call setJavaDimension() in the java implementation for this to
	 * return the correct value in java.
	 */
	private Dimension getScreenSize() {
		Dimension size = new Dimension(PlayN.graphics().width(), PlayN
				.graphics().height());
		if (testDimension != null)
			return testDimension;
		else if (size.height != 0 && size.width != 0)
			return size;
		// this needs to be entered manually, it is the screen size you set java
		// to run on
		else
			return new Dimension(640, 480);
	}

	private Rectangle getPlaymatRect() {
		Point playmatPosition = getPlaymatPosition();
		Rectangle playmatRect = new Rectangle(playmatPosition.x,
				playmatPosition.y, playmatSize.width, playmatSize.height);
		return playmatRect;
	}

	/**
	 * Determines the position of the playmat window. The window should be small
	 * enough to fit inside the screen, so this centers it
	 */
	private Point getPlaymatPosition() {
		int playmatX = (screenSize.width - playmatSize.width) / 2;
		int playmatY = (screenSize.height - playmatSize.height) / 2;

		Point playmatPosition = new Point(playmatX, playmatY);
		return playmatPosition;
	}

	/**
	 * Carves out a window of the screen that fits the original scale. This
	 * should be small enough to fit inside the screen.
	 */
	private Dimension getPlaymatSize() {
		double screenScale = getRatio(screenSize);
		int playmatWidth;
		int playmatHeight;

		Dimension playmatSize;

		if (screenScale >= originalScale) {
			playmatWidth = (int) (screenSize.height * originalScale);
			playmatHeight = screenSize.height;
		} else {
			playmatWidth = screenSize.width;
			playmatHeight = (int) (screenSize.width / originalScale);
		}
		playmatSize = new Dimension(playmatWidth, playmatHeight);
		return playmatSize;
	}

}
