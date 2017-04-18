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
import static playn.core.PlayN.graphics;
import edu.bsu.issgame.core.assetmgt.Jukebox;
import edu.bsu.issgame.core.assetmgt.Jukebox.Track;
import react.Slot;
import react.Value;
import tripleplay.ui.Constraints;
import tripleplay.ui.Label;
import tripleplay.ui.Style;
import tripleplay.ui.layout.AxisLayout;
import tripleplay.util.Timer;

public class GamePrepScreen extends AbstractGameScreen {

	private final Value<Integer> timeRemaining = Value.create(5);
	private Timer timer;

	public GamePrepScreen(AbstractGameScreen previous,
			final PlayerMinigameMap minigameMap) {
		super(previous);
		this.miniGameMap = checkNotNull(minigameMap);
		final Label counter = new Label("5");
		timeRemaining.connect(new Slot<Integer>() {
			@Override
			public void onEmit(Integer event) {
				counter.text.update(timeRemaining.get().toString());
			}
		});
		iface.createRoot(AxisLayout.vertical(), CustomStyleSheet.instance(),
				layer)//
				.setSize(graphics().width(), graphics().height())
				//
				.add(new Label(miniGameMap.get(getClientIdForThisPlayer())
						.getDescription())//
						.addStyles(Style.TEXT_WRAP.is(true))//
						.setConstraint(
								Constraints
										.fixedWidth(graphics().width() * .8f)))//

				.add(counter);
	}

	@Override
	public void update(int delta) {
		super.update(delta);
		if (timer != null) {
			timer.update();
		}
	}

	@Override
	public void wasShown() {
		super.wasShown();
		timer = new Timer();
		timer.every(1000, new Runnable() {
			@Override
			public void run() {
				timeRemaining.update(timeRemaining.get() - 1);
				if (timeRemaining.get() == 0) {
					timer = null;
					screenStack.replace(
							miniGameMap.get(getClientIdForThisPlayer())
									.getNextScreen(GamePrepScreen.this), //
							screenStack.slide());
				}
			}
		});
		initMusic();
	}

	private void initMusic() {
		if (isCommander) {
			Jukebox.instance().playOnce(Track.PREP_MUSIC);
		}
	}
}
