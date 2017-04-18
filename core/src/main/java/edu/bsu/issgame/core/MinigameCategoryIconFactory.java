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

import static playn.core.PlayN.graphics;
import playn.core.Image;
import tripleplay.ui.Icon;
import tripleplay.ui.Icons;

import com.google.common.collect.ImmutableMap;

public final class MinigameCategoryIconFactory {

	private static final MinigameCategoryIconFactory INSTANCE = new MinigameCategoryIconFactory();
	
	private static final float SIZE_FOR_WHICH_ICON_WAS_DESIGNED = 800;
	private static final float SCALE = graphics().width() / SIZE_FOR_WHICH_ICON_WAS_DESIGNED;

	public static MinigameCategoryIconFactory instance() {
		return INSTANCE;
	}

	private static final ImmutableMap<MinigameCategory, Icon> MAP = ImmutableMap
			.of(MinigameCategory.SCIENCE,
					makeIcon(GameImage.SCIENCE_ICON.image),//
					MinigameCategory.MAINTENANCE,
					makeIcon(GameImage.MAINTAINENCE_ICON.image));

	private static Icon makeIcon(Image image) {
		Icon unscaled = Icons.image(image);
		return Icons.scaled(unscaled, SCALE);
	}

	private MinigameCategoryIconFactory() {
	}

	public Icon lookupIconFor(MinigameCategory category) {
		return MAP.get(category);
	}

}
