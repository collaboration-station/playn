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

import static playn.core.PlayN.graphics;
import playn.core.Image;
import playn.core.ImageLayer;
import tripleplay.ui.Constraints;
import tripleplay.ui.Group;
import tripleplay.ui.Label;
import tripleplay.ui.Root;
import tripleplay.ui.Shim;
import tripleplay.ui.Style;
import tripleplay.ui.layout.AxisLayout;
import edu.bsu.issgame.core.mission.Exposition;

public class MissionConclusionScreen extends AbstractGameScreen {

	public MissionConclusionScreen(AbstractGameScreen previous,
			Exposition conclusion) {
		super(previous);
		addBackground();
		Group successTextGroup = new Group(AxisLayout.horizontal())
				.add(new Shim(graphics().screenWidth() / 6, 0))
				.add(new Label(conclusion.asText()).addStyles(
						Style.TEXT_WRAP.is(true),
						Style.TEXT_EFFECT.vectorOutline,
						Style.HIGHLIGHT.is(Palette.DARK_BLUE.color),
						Style.OUTLINE_WIDTH.is(5f)))
				.setConstraint(Constraints.fixedWidth(graphics().width() * .8f))
				.add(new Shim(graphics().screenWidth() / 6, 0));
		Root root = iface
				.createRoot(AxisLayout.vertical(), CustomStyleSheet.instance(),
						layer).setSize(graphics().width(), graphics().height())
				.add(successTextGroup);
		root.add(makeReturnToEarthButton());
	}

	private void addBackground() {
		Image bgImage = GameImage.WIN_FINAL_SCREEN.image;
		Image scaledBg = scale.getBackgroundSubImage(bgImage);
		ImageLayer bg = graphics().createImageLayer(scaledBg);
		bg.setWidth(graphics().width());
		bg.setHeight(graphics().height());
		layer.add(bg);
	}

}
