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

import static com.google.common.base.Preconditions.checkState;
import static playn.core.PlayN.graphics;
import static playn.core.PlayN.log;
import static tripleplay.ui.layout.TableLayout.COL;

import java.io.IOException;
import java.util.List;

import playn.core.Color;
import playn.core.Image;
import react.Slot;
import react.Value;
import tripleplay.ui.Background;
import tripleplay.ui.Button;
import tripleplay.ui.Constraints;
import tripleplay.ui.Element;
import tripleplay.ui.Group;
import tripleplay.ui.Icon;
import tripleplay.ui.Label;
import tripleplay.ui.Menu;
import tripleplay.ui.MenuHost;
import tripleplay.ui.MenuItem;
import tripleplay.ui.Root;
import tripleplay.ui.Shim;
import tripleplay.ui.Style;
import tripleplay.ui.Styles;
import tripleplay.ui.layout.AxisLayout;
import tripleplay.ui.layout.TableLayout;
import tripleplay.util.Colors;

import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableBiMap;
import com.google.common.collect.ImmutableMap;

import edu.bsu.issgame.core.assetmgt.Jukebox;
import edu.bsu.issgame.core.assetmgt.Jukebox.Track;
import edu.bsu.issgame.core.net.ClientId;

public class TaskAssignmentScreen extends AbstractGameScreen {

	private static final ImmutableMap<MinigameCategory, Integer> COLOR_MAP = ImmutableMap
			.of(MinigameCategory.SCIENCE, Color.rgb(61, 82, 66),//
					MinigameCategory.MAINTENANCE, Color.rgb(170, 96, 39));
	private static final BiMap<MinigameType, String> MINIGAME_NAME_MAP = ImmutableBiMap
			.of(MinigameType.MEMORY, "Memory Experiment", //
					MinigameType.PATTERN_REPEAT, "Repeat the Pattern",//
					MinigameType.SLIDING_PUZZLE, "Unscramble the Picture",//
					MinigameType.TILE_ROTATION, "Complete the Circuit");
	private final Button beginButton;

	private final Slot<Button> beginAction = new Slot<Button>() {
		@Override
		public void onEmit(Button event) {
			beginButton.setEnabled(false);
			try {
				client.requestStartScenario(map);
			} catch (IOException e) {
				log().error("IO Exception sending request: " + e.getMessage());
				e.printStackTrace();
			}
			startTheCommandersGame();
		}

		private void startTheCommandersGame() {
			GamePrepScreen gamePrepScreen = new GamePrepScreen(
					TaskAssignmentScreen.this, map);
			screenStack.replace(gamePrepScreen, screenStack.slide());
		}
	};

	private final Root root;
	private final MenuHost menuHost;
	private final PlayerMinigameMap map = new PlayerMinigameMap();
	private final int COLUMN_GAP = (int) percentOfScreenHeight(0.05f);
	private final int ROW_GAP = (int) percentOfScreenHeight(0.04f);
	private final Value<Feedback> feedback = Value
			.create(Feedback.NOT_EVERYONE_HAS_TASK);

	public TaskAssignmentScreen(AbstractGameScreen previous) {
		super(previous);
		checkState(isCommander,
				"Only the one hosting the game is expected to be the commander.");
		Image bgImage = GameImage.TASK_ASSIGNMENT_BACKGROUND.image;
		Image scaledBg = scale.getBackgroundSubImage(bgImage);
		root = iface
				.createRoot(AxisLayout.vertical(), CustomStyleSheet.instance(),
						layer).setSize(graphics().width(), graphics().height())
				.addStyles(Style.BACKGROUND.is(Background.image(scaledBg)));
		menuHost = new MenuHost(iface, root);
		root.add(//
				new Label("Talk With Your Crew About This Mission"),//
				new Shim(0, percentOfScreenHeight(0.035f)),//
				createButtonAndCountryLabelLayout(),//
				new Shim(0, percentOfScreenHeight(0.035f)),//
				createFeedbackLabel(),//
				beginButton = new Button("Begin")//
						.onClick(beginAction)//
						.setEnabled(false));
	}

	private Label createFeedbackLabel() {
		final Label label = new Label(feedback.get().asText);
		feedback.connect(new Slot<Feedback>() {
			@Override
			public void onEmit(final Feedback newFeedback) {
				final float durationMS = 500f;
				anim.tween(TextAnimator.collapse(label.text))
						.from(1f)
						.to(0f)
						.in(durationMS)
						.then()
						.tween(TextAnimator.expand(label.text).toTargetText(
								newFeedback.asText))//
						.from(0f)//
						.to(1f)//
						.in(durationMS);
			}
		});
		return label;
	}

	private Group createButtonAndCountryLabelLayout() {
		Group countryLableAndSelectionButtonLayout = new Group(//
				new TableLayout(COL.alignLeft(), COL.alignRight())//
						.gaps(ROW_GAP, COLUMN_GAP));//
		populateTableWithCountriesAndButtons(countryLableAndSelectionButtonLayout);
		return countryLableAndSelectionButtonLayout;
	}

	private void populateTableWithCountriesAndButtons(Group countryTableLayout) {
		CountryLabelFactory countryLabelFactory = CountryLabelFactory
				.instance();
		List<ClientId> connectedClients = client.getConnectedClients();
		for (ClientId clientId : connectedClients) {
			countryTableLayout.add(createCountryLabel(countryLabelFactory,
					clientId));//
			countryTableLayout.add(new TaskButton(clientId)
					.setConstraint(Constraints
							.fixedWidth(graphics().width() * .50f)));
		}
	}

	private Element<?> createCountryLabel(
			CountryLabelFactory countryLabelFactory, ClientId clientId) {
		return countryLabelFactory.createCountryLabel(clientId.country);
	}

	public final class TaskButton extends Button {
		public TaskButton(final ClientId clientId) {
			super("Select a task >");
			onClick(new Slot<Button>() {
				@Override
				public void onEmit(Button self) {
					MenuHost.Pop pop = new MenuHost.Pop(self, createMenu());
					pop.menu.itemTriggered().connect(new Slot<MenuItem>() {
						@Override
						public void onEmit(MenuItem event) {
							final String selection = event.text.get();
							final Icon iconSelect = event.icon.get();
							text.update(selection);
							icon.update(iconSelect);
							MinigameType type = MINIGAME_NAME_MAP.inverse()
									.get(selection);
							addStyles(makeStylesFor(type.category));
							map.put(clientId, type);
							updateFeedback();
							beginButton.setEnabled(feedback.get().canProceed);
						}

						private void updateFeedback() {
							if (allPlayersHaveATask()) {
								updateFeedbackForAllPlayersHavingATask();
							} else {
								feedback.update(Feedback.NOT_EVERYONE_HAS_TASK);
							}
						}

						private void updateFeedbackForAllPlayersHavingATask() {
							checkState(allPlayersHaveATask());
							if (!bothScienceAndMaintenanceAreSelected()) {
								feedback.update(Feedback.NEED_BOTH_SCIENCE_AND_MAINTENANCE);
							} else {
								feedback.update(Feedback.READY);
							}
						}
					});
					menuHost.popup(pop);
				}
			});
		}

		private boolean allPlayersHaveATask() {
			boolean readyToStart = client.getConnectedClients().size() == map
					.size();
			return readyToStart;
		}

		private boolean bothScienceAndMaintenanceAreSelected() {
			boolean hasScience = false;
			boolean hasMaintenance = false;

			for (MinigameType game : map.values()) {
				if (game.category.equals(MinigameCategory.SCIENCE)) {
					hasScience = true;
				} else if (game.category.equals(MinigameCategory.MAINTENANCE)) {
					hasMaintenance = true;
				}
			}
			return hasScience && hasMaintenance;
		}

		private Menu createMenu() {
			Menu menu = new Menu(AxisLayout.vertical().offStretch().gap(3));
			for (String item : MINIGAME_NAME_MAP.values()) {
				MinigameType type = MINIGAME_NAME_MAP.inverse().get(item);
				Icon icon = MinigameCategoryIconFactory.instance()
						.lookupIconFor(type.category);
				MenuItem menuItem = new MenuItem(item, icon)//
						.addStyles(makeStylesFor(type.category));
				menu.add(menuItem);
			}
			return menu;
		}

		private Styles makeStylesFor(MinigameCategory category) {
			return Styles.make(Style.BACKGROUND.is(CustomStyleSheet
					.makeButtonBackground(COLOR_MAP.get(category))),
					Style.COLOR.is(Colors.WHITE));
		}

	}

	private enum Feedback {
		NOT_EVERYONE_HAS_TASK(false, "Assign tasks to each crew member"), //
		NEED_BOTH_SCIENCE_AND_MAINTENANCE(false,
				"You need both science and maintenance tasks"), //
		READY(true, "Ready!");

		public final boolean canProceed;
		public final String asText;

		private Feedback(boolean canProceed, String asText) {
			this.canProceed = canProceed;
			this.asText = asText;
		}
	}

	@Override
	public void wasShown() {
		super.wasShown();
		if (isCommander) {
			Jukebox.instance().loop(Track.GAME_SELECTION_MUSIC);
		}
	}

}
