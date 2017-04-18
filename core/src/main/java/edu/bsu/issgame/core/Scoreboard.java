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
import java.util.Map;
import java.util.Set;

import com.google.common.base.MoreObjects;
import com.google.common.collect.Maps;

import edu.bsu.issgame.core.net.ClientId;

public final class Scoreboard implements Serializable {
	private static final long serialVersionUID = 1574525983563451095L;

	private Map<ClientId, Score> map = Maps.newHashMap();

	public Scoreboard() {
	}

	public Scoreboard(Map<ClientId, Score> map) {
		this.map.putAll(map);
	}

	public int size() {
		return map.size();
	}

	public Set<ClientId> keySet() {
		return map.keySet();
	}

	public Score get(ClientId clientId) {
		return map.get(clientId);
	}

	public Score sum() {
		Score sum = Score.ZERO;
		for (Score s : map.values()) {
			sum = sum.add(s);
		}
		return sum;
	}

	public Set<Map.Entry<ClientId, Score>> entries() {
		return map.entrySet();
	}

	public Score put(ClientId id, Score score) {
		return map.put(id, score);
	}

	public void clear() {
		map.clear();
	}

	@Override
	public String toString() {
		return MoreObjects.toStringHelper(this)//
				.add("map", map)//
				.toString();
	}

}
