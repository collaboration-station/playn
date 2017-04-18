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
import static playn.core.PlayN.log;
import static tripleplay.ui.layout.TableLayout.COL;

import java.io.IOException;
import java.util.Map;

import playn.core.Image;
import playn.core.ImageLayer;
import react.Connection;
import react.Slot;
import react.UnitSlot;
import tripleplay.ui.Button;
import tripleplay.ui.Group;
import tripleplay.ui.Label;
import tripleplay.ui.Root;
import tripleplay.ui.Style;
import tripleplay.ui.layout.AxisLayout;
import tripleplay.ui.layout.TableLayout;
import tripleplay.util.Colors;
import edu.bsu.issgame.core.assetmgt.Jukebox;
import edu.bsu.issgame.core.assetmgt.Jukebox.Track;
import edu.bsu.issgame.core.mission.Exposition;
import edu.bsu.issgame.core.mission.Scenario;
import edu.bsu.issgame.core.net.ClientId;

public final class ScoreScreen extends AbstractGameScreen {

	public static ScoreScreen makeSuccessScoreScreen(
			AbstractGameScreen previous, Scoreboard scoreboard, Scenario next) {
		return new ScoreScreen(previous, scoreboard, next);
	}

	public static ScoreScreen makeFailureScoreScreen(
			AbstractGameScreen previous, Scoreboard scoreboard) {
		return new ScoreScreen(previous, scoreboard, true);
	}

	public static AbstractGameScreen makeMissionSuccessScoreScreen(
			CommonGameScreenUI previous, Scoreboard scoreboard,
			Exposition conclusion) {
		return new ScoreScreen(previous, scoreboard, conclusion);
	}

	private static final String SCREEN_TITLE = "Results";
	private final int COLUMN_GAP = (int) percentOfScreenHeight(0.05f);
	private final int ROW_GAP = (int) percentOfScreenHeight(0.00f);
	private Connection connection;
	private final boolean isFailure;
	private final Scoreboard scoreboard;
	private Exposition conclusion;
	private Group scoreTable = new Group(new TableLayout(COL.alignLeft(),
			COL.alignRight(), COL.alignRight(), COL.alignRight(),
			COL.alignRight()).gaps(ROW_GAP, COLUMN_GAP));//
	private CountryLabelFactory countryLabelFactory = CountryLabelFactory
			.instance();

	private ScoreScreen(AbstractGameScreen previous, Scoreboard scoreboard,
			Exposition conclusion) {
		super(previous);
		this.isFailure = false;
		this.scoreboard = checkNotNull(scoreboard);
		this.conclusion = checkNotNull(conclusion);
		configureBackground();
		constructUI();
	}

	private ScoreScreen(AbstractGameScreen previous, Scoreboard scoreboard,
			boolean isFailure) {
		super(previous);
		this.isFailure = isFailure;
		this.scoreboard = checkNotNull(scoreboard);
		configureBackground();
		constructUI();
		initMusic();
	}

	private ScoreScreen(AbstractGameScreen previous, Scoreboard scoreboard,
			final Scenario nextScenario) {
		this(previous, scoreboard, false);
		connection = client.onGameStarted().connect(new UnitSlot() {
			@Override
			public void onEmit() {
				if (isCommander) {
					screenStack.replace(new TaskAssignmentScreen(
							ScoreScreen.this), screenStack.slide());
				} else {
					screenStack.replace(new ScenarioInformationScreen(
							ScoreScreen.this, nextScenario), //
							screenStack.slide());
				}
				connection.disconnect();
			}
		});
	}

	private void constructUI() {
		Root root = iface
				.createRoot(
						AxisLayout.vertical().gap(
								(int) percentOfScreenHeight(0.02f)),
						CustomStyleSheet.instance(), layer)
				.setSize(graphics().width(), graphics().height())
				.add(new Label(SCREEN_TITLE)//
						.addStyles(Style.FONT.is(GameFont.TITLE.font),
								Style.COLOR.is(Colors.WHITE)));
		addScoresToTable();
		addTotalsToScoreTable();
		if (isFailure)
			root.add(new Label("The Mission Failed..."));
		if (!isFailure)
			root.add(new Label("The Mission Was A Success!"));
		root.add(scoreTable);
		addButtonIfHostingOrFailureOrMissionSuccess(root);
	}

	private void addTotalsToScoreTable() {
		scoreTable.add(new Label("ISS TOTAL: "));//
		makeScoreGroup(scoreboard.sum());//
		scoreTable.add(new Label("ISS GOAL: "));//
		makeScoreGroup(scenario.goal);
	}

	private void addScoresToTable() {
		for (Map.Entry<ClientId, Score> entry : scoreboard.entries()) {
			scoreTable.add(countryLabelFactory.createCountryLabel(entry
					.getKey().country));
			makeScoreGroup(entry.getValue());
		}
	}

	private void addButtonIfHostingOrFailureOrMissionSuccess(Root root) {
		if (isCommander || isFailure || conclusion != null) {
			root.add(makeButton("Continue").onClick(new Slot<Button>() {
				@Override
				public void onEmit(Button event) {
					try {
						if (isFailure) {
							screenStack.push(new ScenarioFailureScreen(
									ScoreScreen.this));
						} else if (conclusion != null) {
							screenStack.push(new MissionConclusionScreen(
									ScoreScreen.this, conclusion));
						} else {
							client.requestStartGame();
						}
					} catch (IOException ioe) {
						log().error(ioe.getMessage());
						ioe.printStackTrace();
					}
				}
			}));
		}
	}

	private void configureBackground() {
		Image backgroundImage = GameImage.SCORE_SCREEN_BACKGROUND.image;
		Image scaledBackgroundImage = scale
				.getBackgroundSubImage(backgroundImage);
		ImageLayer bgLayer = graphics().createImageLayer(scaledBackgroundImage);
		bgLayer.setAlpha(0.5f);
		bgLayer.setWidth(graphics().width());
		bgLayer.setHeight(graphics().height());
		layer.add(bgLayer);
	}

	private void makeScoreGroup(Score value) {
		MinigameCategoryIconFactory iconFactory = MinigameCategoryIconFactory
				.instance();
		scoreTable
				.add(new Label(value.maintenance + ""))
				.add(new Label(iconFactory
						.lookupIconFor(MinigameCategory.MAINTENANCE)))
				.add(new Label(value.science + ""))
				.add(new Label(iconFactory
						.lookupIconFor(MinigameCategory.SCIENCE)));
	}

	private Button makeButton(String text) {
		return new Button(text);
	}

	private void initMusic() {
		if (isCommander) {
			Track track = isFailure ? Track.SADFARE : Track.FANFARE;
			Jukebox.instance().playOnce(track);
		}
	}

}
