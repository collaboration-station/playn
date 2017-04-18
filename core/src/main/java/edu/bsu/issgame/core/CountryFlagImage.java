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

import static playn.core.PlayN.assets;
import playn.core.Image;

public enum CountryFlagImage {
	USA("images/usa_flag.png"),
	CHINA("images/china_flag.png"),//
	CANADA("images/canada_flag.png"),//
	EU("images/european_union_flag.png"),//
	JAPAN("images/japan_flag.png");
	
	public final Image image;
	
	private CountryFlagImage(String path){
		this.image = assets().getImage(path);
	}
	
	public static Image getCountryImage(Country country)
	{
		Image image=null;
		switch (country) {
		case USA:
			image = CountryFlagImage.USA.image;
			break;
		case CHINA:
			image =CountryFlagImage.CHINA.image;
			break;
		case CANADA:
			image =CountryFlagImage.CANADA.image;
			break;
		case EU:
			image = CountryFlagImage.EU.image;
			break;
		case JAPAN:
			image = CountryFlagImage.JAPAN.image;
			break;
		}
		
		return image;
	}
	
	

}
