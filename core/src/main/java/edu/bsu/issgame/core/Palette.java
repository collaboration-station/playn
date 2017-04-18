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

import playn.core.Color;

public enum Palette {
	LIGHT_BLUE(26, 121, 130), //
	DARK_BLUE(12, 26, 54), //
	TAN(196, 170, 77), //
	BROWN(143, 64, 4), //
	ORANGE(245, 117, 5);

	public final int color;

	private Palette(int red, int green, int blue) {
		this.color = Color.rgb(red, green, blue);
	}
}
