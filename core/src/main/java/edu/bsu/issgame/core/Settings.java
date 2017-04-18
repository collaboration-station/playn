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

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Map;

import com.google.common.base.Objects;
import com.google.common.collect.Maps;

public final class Settings {

	public static final class Key<T> {

		public static <T> Key<T> create(String name) {
			return new Key<T>(name);
		}

		private final String name;

		public Key(String name) {
			this.name = checkNotNull(name);
		}

		@Override
		public int hashCode() {
			return Objects.hashCode(name);
		}

		@Override
		public boolean equals(Object obj) {
			if (obj instanceof Key) {
				// We can ignore the types as long as the names are unique.
				@SuppressWarnings("rawtypes")
				Key other = (Key) obj;
				return Objects.equal(this.name, other.name);
			}
			return false;
		}

	}

	public static final Key<Integer> MINIGAME_DURATION = Key
			.create("minigame.duration");
	public static final Key<Boolean> BOARD_RANDOMIZATION = Key
			.create("board.randomization");
	public static final Key<Integer> POINTS_PER_COMPLETED_MEMORY_BOARD = Key
			.create("points.memory.board.completion");
	public static final Key<Integer> POINTS_PER_MATCH = Key
			.create("points.memory.match");
	public static final Key<Boolean> LOG_TRACE_MESSAGES = Key.create("log.trace");
	
	public static Settings createDefaults() {
		Settings settings = new Settings();
		settings.set(MINIGAME_DURATION).to(60);
		settings.set(BOARD_RANDOMIZATION).to(true);
		settings.set(POINTS_PER_COMPLETED_MEMORY_BOARD).to(10);
		settings.set(POINTS_PER_MATCH).to(4);
		settings.set(LOG_TRACE_MESSAGES).to(false);
		return settings;
	}

	private static final Map<Key<?>, Object> map = Maps.newHashMap();

	private Settings() {
	}

	public <T> T get(Key<T> key) {
		checkThatKeyIsInMap(key);
		// We ensure that only the right type of values is put into the private
		// map, so this is a safe warning suppression.
		@SuppressWarnings("unchecked")
		T value = (T) map.get(key);
		return value;
	}

	private void checkThatKeyIsInMap(Key<?> key) {
		checkArgument(map.containsKey(key), "Missing key: " + key);
	}

	public <T> SettingsUpdater<T> set(Key<T> key) {
		return new SettingsUpdater<T>(key);
	}

	public final class SettingsUpdater<T> {
		private final Key<T> key;

		private SettingsUpdater(Key<T> key) {
			this.key = checkNotNull(key);
		}

		public Settings to(T value) {
			map.put(key, value);
			return Settings.this;
		}
	}

	public static final Settings SETTINGS = Settings.createDefaults();
}
