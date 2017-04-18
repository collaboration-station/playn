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
import playn.core.Font;
import tripleplay.ui.Background;
import tripleplay.ui.Button;
import tripleplay.ui.Field;
import tripleplay.ui.Label;
import tripleplay.ui.Menu;
import tripleplay.ui.MenuItem;
import tripleplay.ui.SimpleStyles;
import tripleplay.ui.Style;
import tripleplay.ui.Style.Mode;
import tripleplay.ui.Stylesheet;
import tripleplay.util.Colors;

public final class CustomStyleSheet {

	private static final Font PLAIN_FONT = GameFont.PLAIN.font;
	private static final float BORDER_WIDTH = percentOfScreenHeight(0.002f);
	private static final float CORNER_RADIUS = percentOfScreenHeight(0.004f);
	private static final float INSETS_TOP = percentOfScreenHeight(0.004f);
	private static final float INSETS_LEFT = percentOfScreenHeight(0.008f);
	private static final float INSETS_BOTTOM = percentOfScreenHeight(0.004f);
	private static final float INSETS_RIGHT = percentOfScreenHeight(0.008f);
	private static final float TEXT_OUTLINE_WIDTH = percentOfScreenHeight(.01f);
	private static final Background BUTTON_ROUND_RECT = makeButtonBackground(Palette.DARK_BLUE.color);
	private static final Background BUTTON_ROUND_RECT_SELECTED = Background
			.roundRect(Palette.ORANGE.color, CORNER_RADIUS, Palette.TAN.color,
					BORDER_WIDTH)//
			.inset(INSETS_TOP, INSETS_RIGHT, INSETS_BOTTOM, INSETS_LEFT);
	private static final Background BUTTON_DISABLED = Background.roundRect(
			Colors.GRAY, CORNER_RADIUS, Colors.LIGHT_GRAY, BORDER_WIDTH)//
			.inset(INSETS_TOP, INSETS_RIGHT, INSETS_BOTTOM, INSETS_LEFT);
	private static final Background MENU_ITEM_BACKGROUND = Background
			.roundRect(Palette.TAN.color, CORNER_RADIUS)//
			.inset(INSETS_TOP, INSETS_RIGHT, INSETS_BOTTOM, INSETS_LEFT);
	private static final Background MENU_ITEM_SELECTED_BACKGROUND = Background
			.roundRect(Palette.LIGHT_BLUE.color, CORNER_RADIUS).inset(
					INSETS_TOP, INSETS_RIGHT, INSETS_BOTTOM, INSETS_LEFT);
	private static final Background MENU_BACKGROUND = Background.roundRect(
			Palette.TAN.color, CORNER_RADIUS, Palette.DARK_BLUE.color,
			BORDER_WIDTH)//
			.inset(INSETS_TOP, INSETS_RIGHT, INSETS_BOTTOM, INSETS_LEFT);

	private static float percentOfScreenHeight(float percent) {
		return graphics().height() * percent;
	}

	/**
	 * The shared stylesheet instance. See the SimpleStyles source code for an
	 * example of how to build a custom stylesheet.
	 *
	 * @see <a
	 *      href="https://github.com/threerings/tripleplay/blob/master/core/src/main/java/tripleplay/ui/SimpleStyles.java">TriplePlay
	 *      SimpleStyles</a>
	 */
	private static final Stylesheet INSTANCE = SimpleStyles
			.newSheetBuilder()
			.add(Button.class, Mode.DEFAULT, Style.FONT.is(PLAIN_FONT),//
					Style.COLOR.is(Palette.TAN.color),//
					Style.BACKGROUND.is(BUTTON_ROUND_RECT))
			.add(Button.class, Mode.SELECTED,
					Style.ACTION_SOUND.is(GameSound.BUTTON_CLICK.sound),
					Style.FONT.is(PLAIN_FONT),//
					Style.COLOR.is(Palette.TAN.color),//
					Style.BACKGROUND.is(BUTTON_ROUND_RECT_SELECTED))
			.add(Button.class, Mode.DISABLED,
					Style.COLOR.is(Colors.LIGHT_GRAY),
					Style.BACKGROUND.is(BUTTON_DISABLED))
			.add(Label.class,
					Style.FONT.is(PLAIN_FONT),//
					Style.COLOR.is(Palette.TAN.color),
					Style.TEXT_EFFECT.vectorOutline,
					Style.HIGHLIGHT.is(Palette.DARK_BLUE.color), Style.OUTLINE_WIDTH.is(TEXT_OUTLINE_WIDTH))
			.add(Field.class, Style.FONT.is(PLAIN_FONT))
			.add(MenuItem.class, Style.FONT.is(PLAIN_FONT),
					Style.COLOR.is(Palette.DARK_BLUE.color),
					Style.BACKGROUND.is(MENU_ITEM_BACKGROUND),
					Style.HALIGN.left)
			.add(MenuItem.class, Mode.SELECTED,
					Style.ACTION_SOUND.is(GameSound.BUTTON_CLICK.sound),
					Style.BACKGROUND.is(MENU_ITEM_SELECTED_BACKGROUND),
					Style.HALIGN.left)
			.add(Menu.class, Style.BACKGROUND.is(MENU_BACKGROUND)).create();

	public static Stylesheet instance() {
		return INSTANCE;
	}

	public static Background makeButtonBackground(int buttonColor) {
		return Background.roundRect(buttonColor, CORNER_RADIUS,
				Palette.TAN.color, BORDER_WIDTH)//
				.inset(INSETS_TOP, INSETS_RIGHT, INSETS_BOTTOM, INSETS_LEFT);
	}
}
