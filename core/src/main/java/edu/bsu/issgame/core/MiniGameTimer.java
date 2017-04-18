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
import react.Signal;
import react.SignalView;
import react.Value;
import react.ValueView;
import tripleplay.util.Timer;
import tripleplay.util.Timer.Handle;

public final class MiniGameTimer {

	public static MiniGameTimer initialTime(int time) {
		return new MiniGameTimer(time);
	}

	private final Value<Integer> timeRemaining;
	private final Signal<MiniGameTimer> onTimeOut = Signal.create();
	private final Timer timer = new Timer();
	private final Runnable countDownByOneSecond = new Runnable() {
		@Override
		public void run() {
			int oldTime = timeRemaining.get();
			int newTime = oldTime - 1;
			timeRemaining.update(newTime);
			if (newTime == 0) {
				onTimeOut.emit(MiniGameTimer.this);
				stop();
			}
		}
	};
	private Handle handle;

	private MiniGameTimer(int initialValue) {
		timeRemaining = Value.create(initialValue);
	}

	public ValueView<Integer> timeRemaining() {
		return timeRemaining;
	}

	public SignalView<MiniGameTimer> onTimeOut() {
		return onTimeOut;
	}

	public void update() {
		timer.update();
	}

	public void start() {
		handle = timer.every(1000, countDownByOneSecond);
	}

	public void stop() {
		checkNotNull(handle, "Timer was never started.");
		handle.cancel();
	}
}
