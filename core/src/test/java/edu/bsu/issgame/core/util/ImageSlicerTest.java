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
package edu.bsu.issgame.core.util;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static playn.core.PlayN.graphics;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import playn.core.CanvasImage;
import tripleplay.util.Colors;
import edu.bsu.issgame.core.net.HeadlessTestCase;

public class ImageSlicerTest extends HeadlessTestCase {

	private static final float EPSILON = 0.00001f;
	private static final int TEST_IMAGE_SIZE = 10;

	private CanvasImage image;
	private List<CanvasImage> images;
	private int[] sourcePixelData;
	private int[] targetPixelData;

	@Before
	public void setUp() {
		image = graphics().createImage(TEST_IMAGE_SIZE, TEST_IMAGE_SIZE);
		fillImageWithTwoByTwoCheckerboard();
	}

	private void fillImageWithTwoByTwoCheckerboard() {
		image.canvas().setFillColor(Colors.CYAN);
		image.canvas().fillRect(0, 0, TEST_IMAGE_SIZE / 2, TEST_IMAGE_SIZE / 2);
		image.canvas().fillRect(TEST_IMAGE_SIZE / 2, TEST_IMAGE_SIZE / 2,
				TEST_IMAGE_SIZE / 2, TEST_IMAGE_SIZE / 2);
	}

	@Test
	public void testSlice_1_returnsOneImage() {
		whenSlicingTheTestImageInto(1);
		assertEquals(1, images.size());
	}

	private void whenSlicingTheTestImageInto(int slicesAlongAnAxis) {
		images = ImageSlicer.cuts(image).intoSlicesAlongAxis(slicesAlongAnAxis, slicesAlongAnAxis);
	}

	@Test
	public void testSlice_1_resultIsSameWidth() {
		whenSlicingTheTestImageInto(1);
		assertEquals(TEST_IMAGE_SIZE, images.get(0).width(), EPSILON);
	}

	@Test
	public void testSlice_2_returnsFourImages() {
		whenSlicingTheTestImageInto(2);
		assertEquals(4, images.size());
	}

	@Test
	public void testSlice_2_resultIsHalfWidth() {
		whenSlicingTheTestImageInto(2);
		assertEquals(TEST_IMAGE_SIZE / 2, images.get(0).width(), EPSILON);
	}

	@Test
	public void testSlice_1_resultPixelsMatchSourcePixels() {
		whenSlicingTheTestImageInto(1);
		readSourceData(TEST_IMAGE_SIZE);
		readTargetData(TEST_IMAGE_SIZE);
		assertArrayEquals(sourcePixelData, targetPixelData);
	}

	private void readSourceData(int size) {
		sourcePixelData = new int[size * size];
		image.getRgb(0, 0, size, size, sourcePixelData, 0, size);
	}

	private void readTargetData(int size) {
		targetPixelData = new int[size * size];
		image.getRgb(0, 0, size, size, targetPixelData, 0, size);
	}

	@Test
	public void testSlice_2_upperLeftMatch() {
		whenSlicingTheTestImageInto(2);
		final int sliceSize = TEST_IMAGE_SIZE / 2;
		readSourceData(sliceSize);
		readTargetData(sliceSize);
		assertArrayEquals(sourcePixelData, targetPixelData);
	}
}
