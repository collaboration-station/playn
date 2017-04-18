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

import static com.google.common.base.Preconditions.checkNotNull;

import playn.core.Game;
import playn.core.util.Clock;
import tripleplay.game.ScreenStack;
import edu.bsu.issgame.core.net.NetworkInterface;

public class IssGame extends Game.Default {

	private static final int DELAY_BETWEEN_UPDATES = 33;

	private final Clock.Source clock = new Clock.Source(DELAY_BETWEEN_UPDATES);
	private final ScreenStack screenStack = new ScreenStack();
	private final NetworkInterface net;

	
	public IssGame(NetworkInterface net) {
		super(DELAY_BETWEEN_UPDATES);
		this.net = checkNotNull(net);
	}

	@Override
	public void init() {
		screenStack.push(new LoadingScreen(screenStack, net));
	}

	@Override
	public void update(int delta) {
		super.update(delta);
		clock.update(delta);
		screenStack.update(delta);
	}

	@Override
	public void paint(float alpha) {
		super.paint(alpha);
		clock.paint(alpha);
		screenStack.paint(clock);
	}

}
