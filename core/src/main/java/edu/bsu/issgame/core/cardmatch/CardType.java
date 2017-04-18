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
package edu.bsu.issgame.core.cardmatch;

import static playn.core.PlayN.assets;
import playn.core.Image;

public enum CardType {
	BUTTERFLY("butterfly_carddesign.png"), //
	FISH("fish_carddesign.png"), //
	FLY("fly_carddesign.png"), //
	SPIDER("spider_carddesign.png"), //
	ANT("ant_carddesign.png"), //
	PLANTS("plants_carddesign.png"), //
	GLOVE_SYRINGE("glove_syringe_carddesign.png"), //
	SQUID("squid_carddesign.png"), //
	SLUG("slug_carddesign.png"), //
	MOUSE("mouse_carddesign.png");

	public final Image image;

	private CardType(String path) {
		this.image = assets().getImage("images/cards/" + path);
	}

}
