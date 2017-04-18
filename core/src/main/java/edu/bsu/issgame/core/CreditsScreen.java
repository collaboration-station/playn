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

import java.util.List;

import playn.core.Image;
import react.Slot;
import tripleplay.game.trans.FadeTransition;
import tripleplay.ui.Background;
import tripleplay.ui.Button;
import tripleplay.ui.Element;
import tripleplay.ui.Group;
import tripleplay.ui.Icons;
import tripleplay.ui.Label;
import tripleplay.ui.Root;
import tripleplay.ui.Style;
import tripleplay.ui.layout.AbsoluteLayout;
import tripleplay.ui.layout.AxisLayout;
import tripleplay.ui.layout.TableLayout;
import tripleplay.ui.util.BoxPoint;

import com.google.common.collect.ImmutableList;

import edu.bsu.issgame.core.assetmgt.LoadableImage;

public class CreditsScreen extends AbstractGameScreen {

	private static final float TEAM_HEIGHT_PERCENT = 0.40f;
	private static final float PARTNER_HEIGHT_PERCENT = 0.25f;
	private static final float LOGO_Y_PERCENT_OF_SCREEN = 0.75f;

	private static final String[] BSU_THANKS = {
			"Produced by Ball State University", //
			" ", //
			"Special Thanks To", "Computer Science Department",
			"Provost Immersive Learning Initiative",
			"Charles W. Brown Planetarium", };
	private static final String[] TCM_THANKS = {
			"Created in collaboration with",
			"The Children's Museum of Indianapolis",//
			"with special thanks to", //
			"Cathy Hamaker and Despi Ross", //
			" ", //
			"Thanks also to", "Storer Elementary School" };

	private static final List<String> TEAM = ImmutableList.copyOf(new String[] {
			"Kelly Blair",//
			"Paul Gestwicki",//
			"Cou'l Greer",//
			"Alex Knight",//
			"Carisa Lovell",//
			"Cole Ludwig",//
			"Kait Mahl",//
			"Justin Prather",//
			"Zach Sexton",//
			"Kaleb Stumbaugh",//
			"Nicholas Wolfe" });
	private static final BoxPoint TOP_CENTER = new BoxPoint(0.5f, 0);

	private final Root root;
	private final Group smsGroup;
	private final Group bsuGroup;
	private final Group tcmGroup;

	private final Slot<Button> removeCreditsScreenAction = new Slot<Button>() {
		@Override
		public void onEmit(Button event) {
			screenStack.replace(new WelcomeScreen(CreditsScreen.this), new FadeTransition(screenStack));
		}
	};

	protected CreditsScreen(AbstractGameScreen previous) {
		super(previous);
		smsGroup = makeCreditsGroup();
		bsuGroup = makeBsuGroup();
		tcmGroup = makeTcmGroup();
		root = makeRoot()
				.add(smsGroup.setConstraint(AbsoluteLayout.uniform(TOP_CENTER)))
				.add(bsuGroup.setConstraint(AbsoluteLayout.uniform(TOP_CENTER))
						.setVisible(false))
				.add(tcmGroup.setConstraint(AbsoluteLayout.uniform(TOP_CENTER))
						.setVisible(false));
		addLogos();
		addBackButton();
	}

	private Root makeRoot() {
		return iface
				.createRoot(new AbsoluteLayout(), CustomStyleSheet.instance(),
						layer)//
				.setSize(graphics().width(), graphics().height())
				.addStyles(
						Style.BACKGROUND.is(Background.image(LoadableImage.CREDITS_BG.loadAsync())));
	}

	private Group makeCreditsGroup() {
		checkState(TEAM.size() % 2 == 1, "Odd team size");
		final int vgap = (int) -percentOfScreenHeight(0.01f);
		Group group = new Group(AxisLayout.vertical().gap(vgap))//
				.add(makeCreditsLabel("SPACE MONKEY STUDIO"));
		Group table = makeTable(vgap);
		group.add(table);
		group.add(makeCreditsLabel(TEAM.get(TEAM.size() - 1)));
		return group;
	}

	private Group makeTable(int vgap) {
		Group table = new Group(new TableLayout(2)//
				.gaps(vgap, 0));
		for (int i = 0; i < TEAM.size() / 2; i++) {
			table.add(makeCreditsLabel(TEAM.get(i)));
			int oppositeMemberIndex = i + TEAM.size() / 2;
			table.add(makeCreditsLabel(TEAM.get(oppositeMemberIndex)));
		}
		return table;
	}

	private Element<?> makeCreditsLabel(String text) {
		return new Label(text).addStyles(Style.FONT.is(GameFont.CREDITS.font));
	}

	private Group makeBsuGroup() {
		return makeTextGroup(BSU_THANKS);
	}

	private Group makeTextGroup(String[] text) {
		Group group = new Group(AxisLayout.vertical().gap(
				(int) -percentOfScreenHeight(0.01f)));
		for (String s : text) {
			group.add(makeCreditsLabel(s));
		}
		return group;
	}

	private Group makeTcmGroup() {
		return makeTextGroup(TCM_THANKS);
	}

	private void addLogos() {
		Button smsButton = makeButton(LoadableImage.SMS_LOGO.loadSync(), true);
		Button bsuButton = makeButton(LoadableImage.BSU_LOGO.loadSync(), false);
		Button tcmButton = makeButton(LoadableImage.TCM_LOGO.loadSync(), false);

		smsButton.onClick(new Slot<Button>() {
			@Override
			public void onEmit(Button event) {
				showOnly(smsGroup);
			}
		});
		bsuButton.onClick(new Slot<Button>() {
			@Override
			public void onEmit(Button event) {
				showOnly(bsuGroup);
			}
		});
		tcmButton.onClick(new Slot<Button>() {
			@Override
			public void onEmit(Button event) {
				showOnly(tcmGroup);
			}
		});

		final float y = percentOfScreenHeight(LOGO_Y_PERCENT_OF_SCREEN);
		root.add(AbsoluteLayout.centerAt(smsButton, graphics().width() * 0.50f,
				y));
		root.add(AbsoluteLayout.centerAt(bsuButton, graphics().width() * 0.20f,
				y));
		root.add(AbsoluteLayout.centerAt(tcmButton, graphics().width() * 0.80f,
				y));
	}

	private Button makeButton(Image image, boolean big) {
		final float percent = big ? TEAM_HEIGHT_PERCENT
				: PARTNER_HEIGHT_PERCENT;
		return new Button(Icons.scaled(Icons.image(image),
				percentOfScreenHeight(percent) / image.height()))//
				.addStyles(Style.BACKGROUND.is(Background.blank()));
	}

	private void showOnly(Group groupToShow) {
		for (Group group : new Group[] { tcmGroup, bsuGroup, smsGroup }) {
			group.setVisible(group == groupToShow);
		}
	}

	private void addBackButton() {
		Button backButton = new Button("Back")
				.onClick(removeCreditsScreenAction);
		root.add(backButton.setConstraint(AbsoluteLayout.uniform(BoxPoint.BL)));
	}

}
