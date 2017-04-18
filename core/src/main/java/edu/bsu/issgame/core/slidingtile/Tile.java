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
package edu.bsu.issgame.core.slidingtile;

import com.google.common.base.MoreObjects;

import pythagoras.i.IPoint;
import pythagoras.i.Point;
import react.Value;
import react.ValueView;

public class Tile {

	private Value<Point> position = Value.create(null);
	public final Value<Point> initialPosition;

	public Tile(Point initialPosition) {
		this.position.update(initialPosition);
		this.initialPosition = Value.create(initialPosition);
	}

	private Tile(Point initialPosition, Point position)
	{
		this.position.update(position);
		this.initialPosition= Value.create(initialPosition);
	}
	
	public ValueView<? extends IPoint> position() {
		return position;
	}

	public Tile setPosition(Point tileNewLocation) {
		this.position.update(tileNewLocation);
		return this;
	}

	public boolean isInProperPosition() {
		if (position.equals(initialPosition)) {
			return true;
		} else {
			return false;
		}
	}
	
	public Tile clone()
	{
		return new Tile(initialPosition.get(), position.get());
	}

	@Override
	public String toString() {
		return MoreObjects.toStringHelper(this).add("position", position)
				.toString();
	}
	
	public boolean equals(Tile other)
	{
		if(this==other)
		{
			return true;
		}
		if(!other.initialPosition.get().equals(this.initialPosition.get()))
		{
			return false;
		}
		if(other.position().get().equals(this.position().get()))
		{
			return true;
		}
		return false;
		
	}

}
