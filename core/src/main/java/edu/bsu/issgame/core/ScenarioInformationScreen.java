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
import playn.core.Image;
import playn.core.ImageLayer;
import react.Connection;
import react.Slot;
import tripleplay.ui.Constraints;
import tripleplay.ui.Group;
import tripleplay.ui.Label;
import tripleplay.ui.Style;
import tripleplay.ui.layout.AxisLayout;
import tripleplay.util.Colors;
import edu.bsu.issgame.core.mission.Scenario;

public final class ScenarioInformationScreen extends AbstractGameScreen {

	private Connection onScenarioStartConnection;

	public ScenarioInformationScreen(AbstractGameScreen previous,
			Scenario scenario) {
		super(previous);
		addBackground();
		MinigameCategoryIconFactory iconFactory = MinigameCategoryIconFactory
				.instance();
		iface.createRoot(AxisLayout.vertical(), CustomStyleSheet.instance(),
				layer)
				.setSize(graphics().width(), graphics().height())
				.add(new Label("Tell Your Commander:"))
				.add(new Label(scenario.exposition.asText())//
						.setConstraint(
								Constraints
										.fixedWidth(graphics().width() * 0.8f))//
						.addStyles(Style.TEXT_WRAP.on,
								Style.COLOR.is(Colors.WHITE)))//
				.add(new Group(AxisLayout.vertical().gap(10))//
						.add(new Label("Need " + scenario.goal.science
								+ " Science Points", iconFactory
								.lookupIconFor(MinigameCategory.SCIENCE))
								.addStyles(Style.ICON_POS.left))
						.add(new Label("Need " + scenario.goal.maintenance
								+ " Maintainance Points", iconFactory
								.lookupIconFor(MinigameCategory.MAINTENANCE))
								.addStyles(Style.ICON_POS.left)));
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
		onScenarioStartConnection = client.onScenarioStarted().connect(
				new Slot<PlayerMinigameMap>() {
					@Override
					public void onEmit(PlayerMinigameMap event) {
						checkNotNull(event);
						screenStack.replace(new GamePrepScreen(
								ScenarioInformationScreen.this, event),
								screenStack.slide());
						onScenarioStartConnection.disconnect();
					}
				});
	}
}
