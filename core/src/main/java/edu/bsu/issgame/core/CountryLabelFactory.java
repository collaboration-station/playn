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

import playn.core.Image;
import tripleplay.ui.Icon;
import tripleplay.ui.Label;
import edu.bsu.issgame.core.util.IconScaler;

public class CountryLabelFactory {

	private static final CountryLabelFactory INSTANCE = new CountryLabelFactory();

	public static CountryLabelFactory instance() {
		return INSTANCE;
	}

	private CountryLabelFactory() {
	}

	public Label createCountryLabel(Country country) {
		Image image = CountryFlagImage.getCountryImage(country);
		Icon icon = IconScaler.instance().makeIconScaledToPercentOfScreenHeight(0.10f, image);
		return new Label(country.asText, icon);
	}
}
