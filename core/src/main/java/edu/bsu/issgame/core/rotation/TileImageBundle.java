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

import playn.core.Image;
import edu.bsu.issgame.core.GameImage;

enum TileImageBundle {
	NORMAL(GameImage.BAR_WIRE, GameImage.T_WIRE, GameImage.L_WIRE), //
	GLOW(GameImage.BAR_WIRE_GLOW, GameImage.T_WIRE_GLOW,
			GameImage.L_WIRE_GLOW), //
	CIRCUIT(GameImage.BAR_WIRE_CIRCUIT, GameImage.T_WIRE_CIRCUIT,
			GameImage.L_WIRE_CIRCUIT);

	public final Image bar;
	public final Image tee;
	public final Image ell;

	private TileImageBundle(GameImage bar, GameImage tee, GameImage ell) {
		this.bar = bar.image;
		this.tee = tee.image;
		this.ell = ell.image;
	}

	public Image imageFor(TileOrientation orientation) {
		switch (orientation.type) {
		case BAR_TILE:
			return bar;
		case L_TILE:
			return ell;
		case T_TILE:
			return tee;
		}
		throw new IllegalArgumentException();
	}
}