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

import java.util.Map;

import playn.core.Key;
import playn.core.Keyboard.Event;
import tripleplay.game.Screen;

import com.google.common.collect.Maps;

import edu.bsu.issgame.core.cardmatch.MemoryBoardScreen;
import edu.bsu.issgame.core.rotation.TileRotationGameScreen;
import edu.bsu.issgame.core.sequence.SequenceMatchScreen;
import edu.bsu.issgame.core.slidingtile.SlidingTilePuzzleScreen;

public class DebugMode extends playn.core.Keyboard.Adapter {

	private static final String ACTIVATION_MESSAGE = "Debug mode activated";

	private final AbstractGameScreen initialScreen;
	private final Map<playn.core.Key, DebugOption> map = Maps.newHashMap();

	public DebugMode(final AbstractGameScreen screen) {
		this.initialScreen = checkNotNull(screen);
		createDebugSettingsMap();
		createNonSettingDebugOptions();
		Popup.on(currentScreen()).show(ACTIVATION_MESSAGE);
	}

	private void createDebugSettingsMap() {
		changeSetting(Settings.MINIGAME_DURATION).to(5)
				.andDisplayMessage("Reducing minigame duration")//
				.whenPressing(Key.K5);
		changeSetting(Settings.BOARD_RANDOMIZATION).to(false)
				.andDisplayMessage("Disabling board randomization")//
				.whenPressing(Key.O);
		changeSetting(Settings.LOG_TRACE_MESSAGES).to(true)
				.andDisplayMessage("Enabling trace logging")//
				.whenPressing(Key.T);
	}

	@Override
	public void onKeyDown(Event event) {
		DebugOption option = map.get(event.key());
		if (option != null) {
			option.apply();
		}
	}

	private void createNonSettingDebugOptions() {
		map.put(Key.K1, new NonSettingDebugOption("Adding some points") {
			@Override
			protected void onTriggered() {
				Screen s = currentScreen();
				if (s instanceof CommonGameScreenUI) {
					CommonGameScreenUI gameScreen = (CommonGameScreenUI) s;
					gameScreen.addPointsInDebugMode();
				}
			}
		});
		map.put(Key.S, new NonSettingDebugOption("Jumping to sequence match") {
			@Override
			protected void onTriggered() {

				currentScreen().screenStack.push(new SequenceMatchScreen(
						currentScreen()));
			}
		});
		map.put(Key.R, new NonSettingDebugOption("Jumping to TileRotation") {
			@Override
			protected void onTriggered() {
				currentScreen().screenStack.push(new TileRotationGameScreen(
						currentScreen()));
			}
		});
		map.put(Key.P, new NonSettingDebugOption("Stopping ping client") {
			@Override
			protected void onTriggered() {
				currentScreen().client.stopPingClient();
			}
		});
		map.put(Key.D,
				new NonSettingDebugOption("Entering Sliding Tile Puzzle") {
					@Override
					protected void onTriggered() {
						currentScreen().screenStack
								.push(new SlidingTilePuzzleScreen(
										currentScreen()));
					}
				});
		map.put(Key.M, new NonSettingDebugOption("Jumping to Memory") {
			@Override
			protected void onTriggered() {
				currentScreen().screenStack
						.push(new MemoryBoardScreen.Builder()
								.fromPreviousScreen(currentScreen()).build());
			}
		});
	}

	private <T> DebugOptionBuilder<T> changeSetting(Settings.Key<T> key) {
		return new DebugOptionBuilder<T>(key);
	}

	private abstract class NonSettingDebugOption implements DebugOption {

		private final String message;

		public NonSettingDebugOption(String message) {
			this.message = message;
		}

		@Override
		public final void apply() {
			Popup.on(currentScreen()).show(message);
			onTriggered();
		}

		protected abstract void onTriggered();

		@Override
		public void whenPressing(Key key) {
			throw new IllegalStateException(
					"Non-setting debug options don't use this method");
		}

	}

	private final class DebugOptionBuilder<T> {
		private T value;
		private final Settings.Key<T> key;

		private DebugOptionBuilder(Settings.Key<T> key) {
			this.key = checkNotNull(key);
		}

		public DebugOptionBuilder<T> to(T value) {
			this.value = value;
			return this;
		}

		public SettingsOption<T> andDisplayMessage(String message) {
			SettingsOption<T> opt = new SettingsOption<T>();
			opt.key = key;
			opt.value = value;
			opt.description = message;
			return opt;
		}
	}

	private interface DebugOption {
		public void apply();

		public void whenPressing(Key key);
	}

	private AbstractGameScreen currentScreen() {
		return (AbstractGameScreen) initialScreen.screenStack.top();
	}

	private final class SettingsOption<T> implements DebugOption {
		private String description;
		private Settings.Key<T> key;
		private T value;

		@Override
		public void apply() {
			Popup.on(currentScreen()).show(description);
			initialScreen.settings.set(key).to(value);
		}

		@Override
		public void whenPressing(Key key) {
			map.put(key, this);
		}
	}

}
