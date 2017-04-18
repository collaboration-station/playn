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
package edu.bsu.issgame.core.slidingtile;

import edu.bsu.issgame.core.GameImage;
import playn.core.Image;

public enum PuzzleViewData {
	
	ROBOT(GameImage.SLIDING_PUZZLE_ROBOT.image, "Robonaut"),
	ORBIT_PATH(GameImage.SLIDING_IMAGE_ORBITPATH.image, "ISS Orbit Path"),
	ASTRONAUT(GameImage.SLIDING_PUZZLE_ASTRONAUT.image, "Astronaut Outside Station");
	
	public final Image image;
	public final String description;
	
	private PuzzleViewData (Image image, String description){
		this.image=image;
		this.description = description;
	}
}
