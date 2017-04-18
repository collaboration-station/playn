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

import static com.google.common.base.Preconditions.checkState;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import tripleplay.sound.Playable;
import edu.bsu.issgame.core.assetmgt.Jukebox.Track;
import edu.bsu.issgame.core.net.HeadlessTestCase;

public class JukeboxTest extends HeadlessTestCase {

	private static final int FIRST_INDEX = 0;
	private static final int SECOND_INDEX = 1;

	private Jukebox jukebox;
	private Playable firstPlayable;

	@Before
	public void setUp() {
		jukebox = Jukebox.create();
	}

	@Test
	public void testIsPlaying_initial_isFalse() {
		assertFalse(jukebox.isPlaying());
	}

	@Test
	public void testIsPlaying_afterLoop_isTrue() {
		whenATrackIsLooped();
		assertTrue(jukebox.isPlaying());
	}

	private void whenATrackIsLooped() {
		jukebox.loop(Track.values()[0]);
		firstPlayable = jukebox.currentPlayable();
	}

	@Test
	public void testPlayableIsPlaying_afterLoop() {
		whenATrackIsLooped();
		thenThePlayableIsPlaying();
	}

	private void thenThePlayableIsPlaying() {
		assertTrue(firstPlayable.isPlaying());
	}

	@Test
	public void testOldLoopIsStoppedWhenStartingNewLoop() {
		givenATrackIsLooped();
		whenANewTrackIsLooped();
		thenTheOriginalTrackHasStopped();
	}

	private void givenATrackIsLooped() {
		jukebox.loop(Track.values()[FIRST_INDEX]);
		firstPlayable = jukebox.currentPlayable();
	}

	private void whenANewTrackIsLooped() {
		checkState(firstPlayable!=null, "I assume that there already was something playing.");
		jukebox.loop(Track.values()[SECOND_INDEX]);
	}

	private void thenTheOriginalTrackHasStopped() {
		assertFalse(firstPlayable.isPlaying());
	}

	@Test
	public void testOldLoopIsStoppedWhenStartingANewNonloopedTrack() {
		givenATrackIsLooped();
		whenANewTrackIsPlayedOnce();
		thenTheOriginalTrackHasStopped();
	}

	private void whenANewTrackIsPlayedOnce() {
		jukebox.playOnce(Track.values()[SECOND_INDEX]);
	}

	@Test
	public void testIsPlaying_afterStop_isFalse() {
		givenATrackIsLooped();
		jukebox.stop();
		assertFalse(jukebox.isPlaying());
	}
}
