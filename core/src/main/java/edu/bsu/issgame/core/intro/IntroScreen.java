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

import static playn.core.PlayN.graphics;
import static playn.core.PlayN.log;
import playn.core.Image;
import playn.core.ImageLayer;
import react.Slot;
import tripleplay.ui.Button;
import tripleplay.ui.Label;
import tripleplay.ui.Root;
import tripleplay.ui.Shim;
import tripleplay.ui.Style;
import tripleplay.ui.layout.AxisLayout;

import com.google.common.collect.ImmutableList;

import edu.bsu.issgame.core.AbstractGameScreen;
import edu.bsu.issgame.core.CustomStyleSheet;
import edu.bsu.issgame.core.Palette;
import edu.bsu.issgame.core.WelcomeScreen;

public class IntroScreen extends AbstractGameScreen {
	
	private int counter = 0;
	private ImmutableList<IntroScreenInformation> list = ImmutableList
			.copyOf(IntroScreenInformation.values());
	private Root root;
	private Label informationLabel;
	private ImageLayer bgLayer;
	private final Slot<Button> goToNextScreen = new Slot<Button>() {
		@Override
		public void onEmit(Button event) {
			log().debug(
					"the next button has been pressed, counter at " + counter);
			counter++;
			if (counter < list.size()) {
				removeBackgroundImageTextAndButton();
				configureScreen();
			} else {
				screenStack.replace(new WelcomeScreen(IntroScreen.this));
			}
		}
	};
	private Button nextButton = new Button("Next").onClick(goToNextScreen);

	public IntroScreen(AbstractGameScreen previous) {
		super(previous);
		configureScreen();
	}

	private void configureScreen() {
		initBackground();
		initText();
		placeButtonAndText();
	}

	private void initBackground() {
		Image backgroundImage = list.get(counter).loadableImage.loadSync();
		Image scaledBackgroundImage = scale
				.getBackgroundSubImage(backgroundImage);
		bgLayer = graphics().createImageLayer(scaledBackgroundImage);
		bgLayer.setAlpha(0.5f);
		bgLayer.setWidth(graphics().width());
		bgLayer.setHeight(graphics().height());
		layer.add(bgLayer);
	}

	private void initText() {
		informationLabel = new Label(list.get(counter).text)//
				.addStyles(Style.COLOR.is(Palette.TAN.color),
						Style.TEXT_WRAP.on);
	}

	private void placeButtonAndText() {
		root = iface
				.createRoot(AxisLayout.vertical(),//
						CustomStyleSheet.instance(), layer)
				.setSize(graphics().width(), graphics().height())
				.add(informationLabel)
				.add(new Shim((graphics().width() / 5) * 2, (graphics()
						.height() / 5) * 2))//
				.add(nextButton);
	}

	private void removeBackgroundImageTextAndButton() {
		layer.remove(bgLayer);
		root.remove(informationLabel);
		root.remove(nextButton);
	}
}
