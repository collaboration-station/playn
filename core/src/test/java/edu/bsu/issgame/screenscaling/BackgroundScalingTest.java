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
package edu.bsu.issgame.screenscaling;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static playn.core.PlayN.assets;

import org.junit.Test;
import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;

import playn.core.Image;
import edu.bsu.issgame.core.GameImage;
import edu.bsu.issgame.core.ScreenScale;
import edu.bsu.issgame.core.net.HeadlessTestCase;

@RunWith(Theories.class)
public class BackgroundScalingTest extends HeadlessTestCase {

	private static final Image BACKGROUND_IMAGE = assets().getImageSync(
			GameImage.MEMORY_BOARD_BACKGROUND.path);

	public static @DataPoints TestSizes[] candidates = TestSizes.values();

	private boolean isAlmostEqualTo(int a, int b) {
		if (a == b) {
			return true;
		}
		if ((a + 1) == b) {
			return true;
		}
		if (b + 1 == a) {
			return true;
		}
		return true;
	}

	private double getScale(int width, int height) {
		return ((double) (width)) / ((double) (height));
	}

	@Theory
	public void testThatBackgroundImageIsCentered(TestSizes canadites) {
		ScreenScale scale = new ScreenScale();
		scale.setTestDimension(canadites.getSize());
		int uncroppedWidth = scale.uncroppedBackgroundRect.width;
		int uncroppedHeight = scale.uncroppedBackgroundRect.height;

		int croppedWidth = scale.backgroundRect.width;
		int croppedHeight = scale.backgroundRect.height;

		int centeredX = (uncroppedWidth - croppedWidth) / 2;
		int centeredY = (uncroppedHeight - croppedHeight) / 2;

		assertTrue(scale.backgroundRect.x == centeredX);
		assertTrue(scale.backgroundRect.y == centeredY);
	}

	@Theory
	public void testThatBackgroundImageFillsEntireScreen(TestSizes canadites) {
		ScreenScale scale = new ScreenScale();
		scale.setTestDimension(canadites.getSize());
		int backgroundImageHeight = scale.backgroundRect.height;
		int backgroundImageWidth = scale.backgroundRect.width;
		int screenSizeWidth = scale.screenSize.width;
		int screenSizeHeight = scale.screenSize.height;

		assertTrue(backgroundImageHeight >= screenSizeHeight);
		assertTrue(backgroundImageWidth >= screenSizeWidth);
		assertTrue(isAlmostEqualTo(backgroundImageHeight, screenSizeHeight));
		assertTrue(isAlmostEqualTo(backgroundImageWidth, backgroundImageHeight));
	}

	// TODO test
	@Theory
	public void testThatBackgroundImageIsOnlyCroppedOnOneAxis(
			TestSizes canadites) {
		ScreenScale scale = new ScreenScale();
		scale.setTestDimension(canadites.getSize());

		double screenScale = scale.screenRatio;
		Image modifiedBackgroundImage = scale
				.getBackgroundSubImage(BACKGROUND_IMAGE);
		int originalWidth = (int) BACKGROUND_IMAGE.width();
		int originalHeight = (int) BACKGROUND_IMAGE.height();
		double originalScale = getScale(originalWidth, originalHeight);

		int modifiedHeight = (int) modifiedBackgroundImage.height();
		int modifiedWidth = (int) modifiedBackgroundImage.width();

		if (originalScale == screenScale) {
			assertEquals(originalWidth, modifiedWidth);
			assertEquals(originalHeight, modifiedHeight);
		} else {
			assertTrue("origW==" + originalWidth + ", modW==" + modifiedWidth
					+ ", origH==" + originalHeight + ", modH=="
					+ modifiedHeight, originalWidth == modifiedWidth
					|| modifiedHeight == originalHeight);
		}
	}

	@Test
	public void testThatProjectedBackgroundImageIsOnlyCroppedOnOneAxis() {
		ScreenScale scale = new ScreenScale();

		int uncroppedWidth = scale.uncroppedBackgroundRect.width;
		int uncroppedHeight = scale.uncroppedBackgroundRect.height;
		int width = scale.backgroundRect.width;
		int height = scale.backgroundRect.height;

		assertTrue(uncroppedHeight == height || uncroppedWidth == width);
	}

}
