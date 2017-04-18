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
package edu.bsu.issgame.core.intro;

import edu.bsu.issgame.core.assetmgt.LoadableImage;

public enum IntroScreenInformation {
	SCREEN_ONE(
			LoadableImage.INTRO_1,
			"In 1998, The International Space Station (ISS) was launched into Earth's orbit."), //
	SCREEN_TWO(
			LoadableImage.INTRO_2,
			"The ISS is a place where crew members live and complete many different scientific experiments."), //
	SCREEN_THREE(
			LoadableImage.INTRO_3,
			"In order to keep everyone on board safe, crew members must participate in routine maintenance."), //
	SCREEN_FOUR(
			LoadableImage.INTRO_4,
			"Now it's your turn to be an astronaut aboard the ISS and collaborate with your crew to make your expedition a success!");

	public final LoadableImage loadableImage;
	public final String text;

	private IntroScreenInformation(LoadableImage loadable, String informationText) {
		this.loadableImage = loadable;
		this.text = informationText;
	}

}
