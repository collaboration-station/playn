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
package edu.bsu.issgame.core.net;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.Serializable;
import java.util.UUID;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;

import edu.bsu.issgame.core.Country;

public class ClientId implements Serializable, Comparable<ClientId>, Cloneable {
	private static final long serialVersionUID = 7631331277732293666L;

	public final Country country;
	public final UUID uuid;

	public ClientId(Country country, UUID uuid) {
		this.country = checkNotNull(country);
		this.uuid = checkNotNull(uuid);
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(uuid);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null)
			return false;
		else if (obj instanceof ClientId) {
			ClientId other = (ClientId) obj;
			return Objects.equal(this.uuid, other.uuid);
		} else
			return false;
	}

	@Override
	public String toString() {
		return MoreObjects.toStringHelper(this)//
				.add("name", country)//
				.toString();
	}

	@Override
	public int compareTo(ClientId o) {
		return this.country.compareTo(o.country);
	}

	@Override
	public Object clone() throws CloneNotSupportedException {
		return new ClientId(country, uuid);
	}

}
