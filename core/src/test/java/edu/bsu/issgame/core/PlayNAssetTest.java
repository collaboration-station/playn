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

import static org.junit.Assert.*;
import static playn.core.PlayN.assets;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Properties;

import org.junit.Test;

import edu.bsu.issgame.core.net.HeadlessTestCase;

public class PlayNAssetTest extends HeadlessTestCase {

	private static final String TEXT_FILE_PATH = "text/ui.txt";

	@Test
	public void testAssetsGetTextIntoProperties_hasAtLeastTwoEntries() {
		try {
			String assetText = assets().getTextSync(TEXT_FILE_PATH);
			InputStream stream = new ByteArrayInputStream(
					assetText.getBytes("UTF-8"));
			Properties properties = new Properties();
			properties.load(stream);
			assertTrue(properties.size() > 1);
		} catch (Exception e) {
			fail("Failed to load " + TEXT_FILE_PATH);
		}
	}
}
