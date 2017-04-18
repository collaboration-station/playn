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

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Properties;

public class UIProperties extends Properties {

	private static final String TEXT_FILE_PATH = "text/ui.txt";
	private static final long serialVersionUID = 1L;
	private static final UIProperties INSTANCE = new UIProperties();
	private static String assetText;
	private static InputStream stream;

	private UIProperties() {
		try {
			assetText = assets().getTextSync(TEXT_FILE_PATH);
			stream = new ByteArrayInputStream(assetText.getBytes("UTF-8"));
			load(stream);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static UIProperties instance() {
		return INSTANCE;
	}
}
