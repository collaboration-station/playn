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
package edu.bsu.issgame.screenscaling;

import pythagoras.i.Dimension;

public enum TestSizes {

	small(426,320), normal(470,320), large(640,480), test(600,400), xlarge(960,720), veryWide(700,300), veryTall(600,500);
	
	private Dimension d;
	
	TestSizes(int x, int y)
	{
		d= new Dimension(x,y);
	}
	
	public Dimension getSize()
	{
		return d;
	}
}
