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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import playn.core.Image;
import tripleplay.ui.Background;
import tripleplay.ui.Constraints;
import tripleplay.ui.Group;
import tripleplay.ui.Label;
import tripleplay.ui.Shim;
import tripleplay.ui.Style;
import tripleplay.ui.layout.AxisLayout;

public class ScenarioFailureScreen extends AbstractGameScreen {

	Image loseImageWhichShouldEventuallyBeSetByHowYouLose = getLoseImage();

	public ScenarioFailureScreen(AbstractGameScreen previous) {
		super(previous);
		Group failureTextGroup = new Group(AxisLayout.horizontal())
		.add(new Shim(graphics().screenWidth()/6, 0))		
		.add(new Label(
						"Oh, No! You didn’t keep the Space Station well maintained. Your crew will be returned to Earth.").addStyles(
						Style.TEXT_WRAP.is(true),
						Style.TEXT_EFFECT.vectorOutline,
						Style.HIGHLIGHT.is(Palette.DARK_BLUE.color),
						Style.OUTLINE_WIDTH.is(5f)))//
				.setConstraint(Constraints.fixedWidth(graphics().width() * .8f))
				.add(new Shim(graphics().screenWidth()/6, 0));
		Image scaledBg = scale
				.getBackgroundSubImage(loseImageWhichShouldEventuallyBeSetByHowYouLose);

		iface.createRoot(AxisLayout.vertical(), CustomStyleSheet.instance(),
				layer).setSize(graphics().width(), graphics().height())
				.add(failureTextGroup)
				.addStyles(Style.BACKGROUND.is(Background.image(scaledBg)))//
				.add(new Shim(0f, percentOfScreenHeight(0.15f)))//
				.add(makeReturnToEarthButton());

	}

	private Image getLoseImage() {
		List<Image> listOfLoseBackgrounds = new ArrayList<Image>();
		listOfLoseBackgrounds.add(GameImage.LOSE_FINAL_SCREEN.image);
		listOfLoseBackgrounds.add(GameImage.LOSE_MAINTAINANCE_SCREEN.image);
		listOfLoseBackgrounds.add(GameImage.LOSE_SCIENCE_SCREEN.image);
		Collections.shuffle(listOfLoseBackgrounds);
		return listOfLoseBackgrounds.get(0);
	}
}
