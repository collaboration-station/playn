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

import java.io.Serializable;

import react.Value;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;

public class Score implements Serializable, Comparable<Score> {
	private static final long serialVersionUID = -5692707529021126256L;

	public static final Score ZERO = maintenance(0).science(0);

	public static Score create() {
		return ZERO;
	}

	public static Score copy(Score goal) {
		return goal;
	}

	public static Builder maintenance(int i) {
		return new Builder(i);
	}

	public static final class Builder {
		private final int maintenance;
		private int science;

		private Builder(int maintenance) {
			this.maintenance = maintenance;
		}

		public Score science(int science) {
			this.science = science;
			return new Score(this);
		}
	}

	public final int science;
	public final int maintenance;

	private Score(Builder importer) {
		this.science = importer.science;
		this.maintenance = importer.maintenance;
	}

	public boolean meetsOrExceeds(Score threshold) {
		return this.science >= threshold.science
				&& this.maintenance >= threshold.maintenance;
	}

	public Score addScience(int points) {
		return maintenance(maintenance).science(science + points);
	}

	public Score addMaintenance(int amountToAdd) {
		return maintenance(maintenance + amountToAdd).science(science);
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(maintenance, science);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null)
			return false;
		else if (obj instanceof Score) {
			Score other = (Score) obj;
			return Objects.equal(maintenance, other.maintenance)
					&& Objects.equal(science, other.science);
		} else {
			return false;
		}
	}

	@Override
	public String toString() {
		return MoreObjects.toStringHelper(this)//
				.add("maintanence", maintenance)//
				.add("science", science)//
				.toString();
	}

	public static final class ScoreValue extends Value<Integer> {
		public ScoreValue(Integer value) {
			super(value);
		}

		public ScoreValue add(int points) {
			update(get() + points);
			return this;
		}
	}

	@Override
	public int compareTo(Score other) {
		int maintenanceDifference = this.maintenance - other.maintenance;
		if (maintenanceDifference != 0)
			return maintenanceDifference;
		else {
			return this.science - other.science;
		}
	}

	public Score add(Score s) {
		return maintenance(this.maintenance + s.maintenance)//
				.science(this.science + s.science);
	}
}
