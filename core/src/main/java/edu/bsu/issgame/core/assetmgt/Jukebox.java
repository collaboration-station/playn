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
package edu.bsu.issgame.core.assetmgt;

import static com.google.common.base.Preconditions.checkNotNull;
import static playn.core.PlayN.log;
import tripleplay.sound.Playable;
import tripleplay.sound.SoundBoard;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.MoreObjects;

public final class Jukebox {

	public static enum Track {
		WELCOME_MUSIC("music_welcome"), //
		FANFARE("fanfare"), //
		SADFARE("sadfare"), //
		MINIGAME_MUSIC_1("minigame_song_1"), //
		MINIGAME_MUSIC_2("minigame_song_2"), //
		MINIGAME_MUSIC_3("minigame_song_3"), //
		GAME_SELECTION_MUSIC("game_selection"), //
		PREP_MUSIC("prepscreen1");

		private final String path;

		private Track(String partialPath) {
			this.path = "music/" + partialPath;
		}
	}

	private static final Jukebox INSTANCE = create();
	private final SoundBoard soundBoard = new SoundBoard();
	private State state;

	public static Jukebox instance() {
		return INSTANCE;
	}

	public static Jukebox create() {
		return new Jukebox();
	}

	private Jukebox() {
		setState(notPlaying);
	}

	public void loop(Track track) {
		state.loop(track);
	}

	public void update(final int deltaMS) {
		soundBoard.update(deltaMS);
	}

	public boolean isPlaying() {
		return state.isPlaying();
	}

	@VisibleForTesting
	Playable currentPlayable() {
		return state.currentPlayable();
	}

	public void playOnce(Track track) {
		state.playOnce(track);
	}

	public void stop() {
		state.stop();
	}

	private void setState(State state) {
		if (this.state != null) {
			this.state.onExit();
		}
		this.state = checkNotNull(state);
		this.state.onEnter();
	}

	private interface State {
		boolean isPlaying();

		@VisibleForTesting
		Playable currentPlayable();

		void stop();

		void loop(Track track);

		void playOnce(Track track);

		void onEnter();

		void onExit();
	}

	private abstract class AbstractState implements State {
		@Override
		public void loop(Track track) {
			log().debug("Starting loop " + track.path);
			Playable p = soundBoard.getLoop(track.path);
			setState(new PlayingState(p));
		}

		@Override
		public void playOnce(Track track) {
			log().debug("Playing once " + track.path);
			Playable p = soundBoard.getClip(track.path);
			setState(new PlayingState(p));
		}

		@Override
		public void onEnter() {
		}

		@Override
		public void onExit() {
		}
	}

	private final State notPlaying = new AbstractState() {

		@Override
		public boolean isPlaying() {
			return false;
		}

		@Override
		public Playable currentPlayable() {
			throw new IllegalStateException();
		}

		@Override
		public void stop() {
			throw new IllegalStateException();
		}

		@Override
		public String toString() {
			return "NotPlayingState";
		}
	};

	private final class PlayingState extends AbstractState {

		private final Playable currentPlayable;

		PlayingState(Playable playable) {
			this.currentPlayable = checkNotNull(playable);
		}

		@Override
		public boolean isPlaying() {
			return true;
		}

		@Override
		public Playable currentPlayable() {
			return currentPlayable;
		}

		@Override
		public void stop() {
			setState(notPlaying);
		}

		@Override
		public void onEnter() {
			currentPlayable.play();
		}

		@Override
		public void onExit() {
			currentPlayable.stop();
			currentPlayable.release();
		}

		@Override
		public String toString() {
			return MoreObjects.toStringHelper(this)
					.add("currentPlayable", currentPlayable).toString();
		}
	}

}
