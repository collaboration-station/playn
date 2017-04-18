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
package edu.bsu.issgame.core.mission;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.Serializable;

import com.google.common.base.MoreObjects;

import edu.bsu.issgame.core.Score;
import edu.bsu.issgame.core.assetmgt.Jukebox.Track;

public class Scenario implements Serializable {
	private static final long serialVersionUID = -3721190772280769703L;

	public static Builder withExposition(Exposition exposition) {
		return new Builder(exposition);
	}

	public static final class Builder {
		private final Exposition exposition;
		private Score goal;
		private Track track = arbitraryTrack();

		private Builder(Exposition exposition) {
			this.exposition = checkNotNull(exposition);
		}

		private Track arbitraryTrack() {
			return Track.MINIGAME_MUSIC_1;
		}

		public Builder andTrack(Track track) {
			this.track = checkNotNull(track);
			return this;
		}

		public Scenario andGoal(Score goal) {
			this.goal = checkNotNull(goal);
			return new Scenario(this);
		}
	}

	public final Exposition exposition;
	public final Score goal;
	public final Track track;

	private Scenario(Builder builder) {
		this.exposition = builder.exposition;
		this.track = builder.track;
		this.goal = Score.copy(builder.goal);
	}

	@Override
	public String toString() {
		return MoreObjects.toStringHelper(this).add("exposition", exposition)
				.toString();
	}

	public Result evaluate(Score score) {
		if (score.meetsOrExceeds(goal)) {
			return new Result.Success();
		} else {
			return new Result.Failure();
		}
	}
}
