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

import static com.google.common.base.Preconditions.checkNotNull;

public enum FontInfo {
	PLAY("fonts/Play-Regular.ttf", "Play"), //
	PLAY_BOLD("fonts/Play-Bold.ttf", "Play-Bold");

	public final String name;
	public final String path;

	private FontInfo(String path, String name) {
		this.path = checkNotNull(path);
		this.name = checkNotNull(name);
	}
}