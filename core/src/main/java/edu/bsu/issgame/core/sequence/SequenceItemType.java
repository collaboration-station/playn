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
package edu.bsu.issgame.core.sequence;

import static playn.core.PlayN.assets;
import edu.bsu.issgame.core.GameSound;
import playn.core.Image;

public enum SequenceItemType {
	BIOLOGY("images/simon_button_biology", GameSound.SEQUENCE_BIOLOGY), //
	EXERCISE("images/simon_button_exercise", GameSound.SEQUENCE_EXERCISE), //
	SLEEP("images/simon_button_sleep", GameSound.SEQUENCE_SLEEP), //
	PHYSICS("images/simon_button_physics", GameSound.SEQUENCE_PHYSICS);

	public final Image unclickedActiveImage;
	public final Image clickedActiveImage;
	public final Image unclickedInactiveImage;
	public final Image clickedInactiveImage;
	public final GameSound sfx;

	private SequenceItemType(String path, GameSound sfx) {
		this.unclickedActiveImage = assets().getImage(path + ".png");
		this.clickedActiveImage = assets().getImage(path + "_2.png");
		this.unclickedInactiveImage = assets().getImage(path + "-inactive.png");
		this.clickedInactiveImage = assets().getImage(path + "_2-inactive.png");
		this.sfx = sfx;
	}
}
