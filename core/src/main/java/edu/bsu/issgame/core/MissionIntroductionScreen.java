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
import static playn.core.PlayN.graphics;

import java.io.IOException;

import playn.core.Image;
import playn.core.ImageLayer;
import react.Connection;
import react.Slot;
import tripleplay.ui.Button;
import tripleplay.ui.Constraints;
import tripleplay.ui.Group;
import tripleplay.ui.Label;
import tripleplay.ui.Root;
import tripleplay.ui.Shim;
import tripleplay.ui.Style;
import tripleplay.ui.layout.AxisLayout;
import tripleplay.util.Colors;
import edu.bsu.issgame.core.mission.Exposition;
import edu.bsu.issgame.core.mission.Scenario;

public class MissionIntroductionScreen extends AbstractGameScreen {

	private Connection onScenarioStartConnection;

	public MissionIntroductionScreen(AbstractGameScreen previous,
			Exposition introduction) {
		super(previous);
		addBackground();
		Root root = iface
				.createRoot(AxisLayout.vertical(), CustomStyleSheet.instance(),
						layer)//
				.setSize(graphics().width(), graphics().height())
				.add(new Label(isCommander ? introduction.commanderText()
						: introduction.asText())//
						.setConstraint(
								Constraints
										.fixedWidth(graphics().width() * 0.8f))//
						.addStyles(Style.TEXT_WRAP.on,
								Style.COLOR.is(Colors.WHITE)));
		if (isCommander) {
			MinigameCategoryIconFactory iconFactory = MinigameCategoryIconFactory
					.instance();
			root.add(new Shim(0, graphics().screenHeight() / 30));
			final Group group = new Group(AxisLayout.horizontal());
			group.add(new Label("Science", iconFactory
					.lookupIconFor(MinigameCategory.SCIENCE)));
			group.add(new Shim(graphics().screenWidth() / 10, 0));
			group.add(new Label("Maintenence", iconFactory
					.lookupIconFor(MinigameCategory.MAINTENANCE)));
			root.add(group);
			root.add(new Shim(0, graphics().screenHeight() / 20));
			root.add(new Button("OK").setConstraint(
					Constraints.fixedWidth(graphics().width() * 0.2f)).onClick(
					new Slot<Button>() {
						@Override
						public void onEmit(Button event) {
							try {
								client.advanceToScenarioSetup();
							} catch (IOException e) {
								throw new RuntimeException(e);
							}
						}
					}));
		}
	}

	private void addBackground() {
		Image bgImage = GameImage.SCENARIO_INFORMATION_BACKGROUND.image;
		Image scaledBg = scale.getBackgroundSubImage(bgImage);
		ImageLayer bg = graphics().createImageLayer(scaledBg);
		bg.setWidth(graphics().width());
		bg.setHeight(graphics().height());
		layer.add(bg);
	}

	@Override
	public void wasShown() {
		super.wasShown();
		onScenarioStartConnection = client.onScenarioSetup().connect(
				new Slot<Scenario>() {
					@Override
					public void onEmit(Scenario scenario) {
						MissionIntroductionScreen.this.scenario = checkNotNull(scenario);
						AbstractGameScreen screen = null;
						if (isCommander) {
							screen = new TaskAssignmentScreen(
									MissionIntroductionScreen.this);
						} else {
							screen = new ScenarioInformationScreen(
									MissionIntroductionScreen.this, scenario);
						}
						screenStack.replace(screen, screenStack.slide());
						onScenarioStartConnection.disconnect();
						onScenarioStartConnection = null;
					}
				});
	}
}