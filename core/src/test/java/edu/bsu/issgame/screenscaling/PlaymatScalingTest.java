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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import edu.bsu.issgame.core.ScreenScale;
import edu.bsu.issgame.core.net.HeadlessTestCase;

public class PlaymatScalingTest extends HeadlessTestCase {

	private static final float EPSILON = 0.01f;

	public boolean isAlmostEqual(double d1, double d2) {
		if (d1 == d2) {
			return true;
		}
		if (d1 > d2) {
			if (d1 * 0.99 < d2) {
				return true;
			} else {
				return false;
			}
		} else {
			if (d2 * 0.99 < d1) {
				return true;
			} else {
				return false;
			}
		}
	}

	// TODO run a loop for each of the test sizes
	public double getRatio(int width, int height) {
		return ((double) (width)) / ((double) (height));
	}

	@Test
	public void testThatPlayMatIsSmallerThanScreen() {
		ScreenScale scale = new ScreenScale();
		assertTrue(scale.screenSize.width >= scale.playmatRect.width);
		assertTrue(scale.screenSize.height >= scale.playmatRect.height);
	}

	@Test
	public void testThatPlayMatMaintainsAspectRatio() {
		ScreenScale scale = new ScreenScale();
		double originalRatio = ScreenScale.originalScale;
		double playmatRatio = getRatio(scale.playmatSize.width,
				scale.playmatSize.height);

		System.out.println("Test original ratio: " + originalRatio);
		System.out.println("Test playmat ratio: " + playmatRatio);
		assertEquals(originalRatio, playmatRatio, EPSILON);

	}

	@Test
	public void testThatPlayMatIsCentered() {
		ScreenScale scale = new ScreenScale();
		int playmatX = scale.playmatRect.x;
		int playmatY = scale.playmatRect.y;
		int playmatWidth = scale.playmatRect.width;
		int playmatHeight = scale.playmatRect.height;
		int screenWidth = scale.screenSize.width;
		int screenHeight = scale.screenSize.height;

		int approximateScreenWidth = playmatX * 2 + playmatWidth;
		int approximateScreenHeight = playmatY * 2 + playmatHeight;

		assertTrue(approximateScreenWidth == screenWidth);
		assertTrue(approximateScreenHeight == screenHeight);

	}

	@Test
	public void testThatPlayMatIsOnlyCroppedOnOneAxis() {
		ScreenScale scale = new ScreenScale();

		int screenWidth = scale.screenSize.width;
		int screenHeight = scale.screenSize.height;

		int playmatHeight = scale.playmatSize.height;
		int playmatWidth = scale.playmatSize.width;

		if (getRatio(screenWidth, screenHeight) == getRatio(playmatWidth,
				playmatHeight)) {
			assertTrue(screenWidth == playmatWidth);
			assertTrue(screenHeight == playmatHeight);
		} else {
			assertTrue(screenWidth == playmatWidth
					|| screenHeight == playmatHeight);
			assertFalse(screenWidth == playmatWidth
					&& screenHeight == playmatHeight);
		}
	}
}
