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

import static com.google.common.base.Preconditions.checkNotNull;
import static playn.core.PlayN.graphics;

import java.util.List;

import playn.core.CanvasImage;
import playn.core.Image;

import com.google.common.collect.Lists;

public class ImageSlicer {

	
	
	public static ImageSlicer cuts(Image image) {
		return new ImageSlicer(image);
	}

	private final Image source;

	private ImageSlicer(Image image) {
		this.source = checkNotNull(image);
	}

	public List<CanvasImage> intoSlicesAlongAxis(int cols, int rows) {
		final int sliceWidth = (int) (source.width() / cols);
		final int sliceHeight = (int) (source.height() / rows);
		final int[] pixelData = new int[sliceWidth * sliceHeight];
		final List<CanvasImage> list = Lists.newArrayList();
		for (int y = 0; y< rows; y++) {
			for (int x= 0; x < cols; x++) {
				CanvasImage image = graphics().createImage(sliceWidth,
						sliceHeight);
				source.getRgb(x * sliceWidth, y * sliceHeight, sliceWidth,
						sliceHeight, pixelData, 0, sliceWidth);
				image.setRgb(0, 0, sliceWidth, sliceHeight, pixelData, 0,
						sliceWidth);
				list.add(image);
			}
		}
		return list;
	}

}
