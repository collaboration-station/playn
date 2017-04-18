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

public class RoleBasedExposition implements Exposition {
	private static final long serialVersionUID = 7254529621130956563L;

	public static Builder forCrew(String crewText) {
		return new Builder(crewText);
	}

	public static final class Builder {
		private final String crewText;
		private String commanderText;

		public Builder(String crewText) {
			this.crewText = crewText;
		}

		public RoleBasedExposition andCommanderText(String commanderText) {
			this.commanderText = checkNotNull(commanderText);
			return new RoleBasedExposition(this);
		}
	}

	private final String crewText;
	private final String commanderText;

	private RoleBasedExposition(Builder importer) {
		this.crewText = importer.crewText;
		this.commanderText = importer.commanderText;
	}

	@Override
	public String asText() {
		return crewText;
	}

	@Override
	public String commanderText() {
		return commanderText;
	}
}
