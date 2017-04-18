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

import java.io.IOException;
import java.util.List;

import playn.core.Image;
import pythagoras.f.Point;
import react.Connection;
import react.Slot;
import react.Value;
import react.ValueView.Listener;
import tripleplay.ui.Background;
import tripleplay.ui.Icon;
import tripleplay.ui.Label;
import tripleplay.ui.Root;
import tripleplay.ui.Style;
import tripleplay.ui.layout.AbsoluteLayout;
import tripleplay.util.Colors;

import com.google.common.collect.Lists;

import edu.bsu.issgame.core.assetmgt.Jukebox;
import edu.bsu.issgame.core.mission.Scenario;
import edu.bsu.issgame.core.net.Message;
import edu.bsu.issgame.core.net.Message.ScenarioFinishedSuccesfullyEndingMission;

public class CommonGameScreenUI extends AbstractGameScreen {

	private int duration = settings.get(Settings.MINIGAME_DURATION);
	private final int DOWN_SHIFT = getScreenScale().getScaledScreenSize().height / 55;
	private Root root;
	protected MiniGameTimer timer = MiniGameTimer.initialTime(duration);
	protected Value<Score> score = Value.create(Score.create());
	private List<Connection> connectionsToDisconnectOnHidden = Lists
			.newArrayList();
	protected MinigameType thisMinigame;

	protected CommonGameScreenUI(AbstractGameScreen previous) {
		super(previous);
		if (previous.timeRemaining != null) {
			this.duration = previous.timeRemaining;
		}
		setMinigameType();
		initRoot();
		configureEndGameOnTimeOut();
		configureShowScoreScreenAfterReceivingServerMessage();
	}

	protected void setMinigameType() {
	}

	protected void setBackground(Image bgImage) {
		checkNotNull(root, "Initialize root before calling this method.");
		Image scaledBg = scale.getBackgroundSubImage(bgImage);
		root.addStyles(Style.BACKGROUND.is(Background.image(scaledBg)));
	}

	protected void initRoot() {
		final Label timeRemainingLabel = createTimeRemainingLabel();
		final Label scoreLabel = createScoreLabel();
		root = iface.createRoot(new AbsoluteLayout(),//
				CustomStyleSheet.instance(), layer)//
				.setSize(graphics().width(), graphics().height());
		root.add(AbsoluteLayout.at(timeRemainingLabel, //
				new Point(getScreenScale().getScaledPosition().x
						+ (getScreenScale().getScaledScreenSize().width / 10),
						DOWN_SHIFT + getScreenScale().getScaledPosition().y)));
		root.add(AbsoluteLayout
				.at(scoreLabel, //
						new Point(
								getScreenScale().getScaledPosition().x
										+ (getScreenScale()
												.getScaledScreenSize().width * 2 / 3),
								DOWN_SHIFT
										+ getScreenScale().getScaledPosition().y)));
	}

	protected Label createTimeRemainingLabel() {
		timer = MiniGameTimer.initialTime(duration);
		final Label label = new Label(UIProperties.instance().getProperty(
				"label.timeRemaining")
				+ " " + timer.timeRemaining().get())//
				.addStyles(Style.COLOR.is(Colors.WHITE));
		timer.timeRemaining().connect(new Listener<Integer>() {
			@Override
			public void onChange(Integer value, Integer oldValue) {
				label.text.update(UIProperties.instance().getProperty(
						"label.timeRemaining")
						+ timer.timeRemaining().get());
			}
		});
		return label;
	}

	protected Label createScoreLabel() {
		final Label label = new Label(UIProperties.instance().getProperty(
				"label.score")
				+ " " + scoreText(), getIcon())//
				.addStyles(Style.COLOR.is(Colors.WHITE));
		score.connect(new Listener<Score>() {
			@Override
			public void onChange(Score value, Score oldValue) {
				label.text.update(UIProperties.instance().getProperty(
						"label.score")
						+ " " + scoreText());
			}
		});
		return label;
	}

	private Icon getIcon() {
		MinigameCategoryIconFactory factory = MinigameCategoryIconFactory
				.instance();
		return factory.lookupIconFor(thisMinigame.category);
	}

	protected int scoreText() {
		switch (thisMinigame.category) {
		case SCIENCE:
			return score.get().science;
		case MAINTENANCE:
			return score.get().maintenance;
		default:
			throw new IllegalStateException("Unrecognized category: "
					+ thisMinigame.category);
		}
	}

	private void configureEndGameOnTimeOut() {
		Connection onTimeOutConnection = timer.onTimeOut().connect(
				new Slot<MiniGameTimer>() {
					@Override
					public void onEmit(MiniGameTimer event) {
						handleScenarioEnd();
					}
				});
		connectionsToDisconnectOnHidden.add(onTimeOutConnection);
	}

	@Override
	public void wasHidden() {
		super.wasHidden();
		for (Connection c : connectionsToDisconnectOnHidden) {
			c.disconnect();
		}
	}

	protected void handleScenarioEnd() {
		try {
			client.reportScore(score.get());
		} catch (IOException ioe) {
			log().error(ioe.getMessage());
			ioe.printStackTrace();
		}
		log().warn(
				"Should probably disable input while we await server notification.");
	}

	private void configureShowScoreScreenAfterReceivingServerMessage() {
		if (client != null) {
			Connection onScenarioFinishedConnection = client
					.onScenarioFinishedWithSuccess().connect(
							new Slot<Message.ScenarioFinishedWithSuccess>() {
								@Override
								public void onEmit(
										Message.ScenarioFinishedWithSuccess message) {
									CommonGameScreenUI.this.scenario = message.nextScenario;
									pushScoreScreenForScenarioSuccess(
											message.scoreboard,
											message.nextScenario);
								}
							});
			connectionsToDisconnectOnHidden.add(onScenarioFinishedConnection);
			Connection onScenarioFailed = client.onScenarioFailed().connect(
					new Slot<Scoreboard>() {
						@Override
						public void onEmit(Scoreboard scoreboard) {
							screenStack.replace(ScoreScreen
									.makeFailureScoreScreen(
											CommonGameScreenUI.this, scoreboard));
						}
					});
			connectionsToDisconnectOnHidden.add(onScenarioFailed);
			Connection onMissionCompleteConnection = client
					.onMissionComplete()
					.connect(
							new Slot<ScenarioFinishedSuccesfullyEndingMission>() {
								@Override
								public void onEmit(
										ScenarioFinishedSuccesfullyEndingMission event) {
									AbstractGameScreen screen = ScoreScreen
											.makeMissionSuccessScoreScreen(
													CommonGameScreenUI.this,
													event.scoreboard,
													event.conclusion);
									screenStack.replace(screen,
											screenStack.slide());
								}
							});
			connectionsToDisconnectOnHidden.add(onMissionCompleteConnection);
		}
	}

	private void pushScoreScreenForScenarioSuccess(Scoreboard scoreboard,
			Scenario nextScenario) {
		screenStack.replace(ScoreScreen.makeSuccessScoreScreen(this,
				scoreboard, nextScenario), //
				screenStack.slide());
	}

	protected void addPointsInDebugMode() {
		score.update(Score.maintenance(100).science(100));
	}

	@Override
	public void wasShown() {
		super.wasShown();
		timer.start();
		startMusic();
	}

	@Override
	public void update(int delta) {
		super.update(delta);
		timer.update();
		timeRemaining = timer.timeRemaining().get();
	}

	private void startMusic() {
		if (isCommander) {
			Jukebox.instance().loop(scenario.track);
		}
	}
}
