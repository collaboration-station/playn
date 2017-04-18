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

import static com.google.common.base.Preconditions.checkArgument;
import static playn.core.PlayN.graphics;
import playn.core.Font;
import playn.core.Font.Style;

public enum GameFont {
	TITLE(FontInfo.PLAY, 0.08f), //
	PLAIN(FontInfo.PLAY, 0.06f), //
	CREDITS(FontInfo.PLAY, 0.04f), //
	BOLD_HUGE(FontInfo.PLAY_BOLD, 0.20f);

	private final FontInfo info;
	public final Font font;

	private GameFont(FontInfo info, float percentOfScreenHeight) {
		this.info = info;
		this.font = percentOfScreenHeight(percentOfScreenHeight);
	}

	private final Font percentOfScreenHeight(float percent) {
		checkArgument(percent >= 0);
		// Note that we always create the font in PLAIN style since the
		// automatic creation of variants creates ugly fonts, as described at
		// https://code.google.com/p/playn/wiki/CustomFonts
		return graphics().createFont(info.name, Style.PLAIN,
				graphics().height() * percent);
	}
}
