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
package edu.bsu.issgame.core.sequence;

import static playn.core.PlayN.graphics;
import playn.core.ImageLayer;
import react.RFuture;
import react.RPromise;
import tripleplay.anim.Animator;

public class SequenceButton {
	private static final float BUTTON_DIAMETER_PERCENT_OF_SCREEN_WIDTH = 0.2f;
	private final SequenceItemType buttonType;
	private ImageLayer layer;
	private boolean active = false;
	private boolean pressed = false;

	public SequenceButton(SequenceItemType buttonType) {
		this.buttonType = buttonType;
		final float size = graphics().width()
				* BUTTON_DIAMETER_PERCENT_OF_SCREEN_WIDTH;
		layer = graphics().createImageLayer(buttonType.unclickedInactiveImage);
		layer.setSize(size, size);
		layer.setOrigin(size / 2, size / 2);
	}

	public void setActive() {
		active = true;
		updateImage();
	}

	private void updateImage() {
		if (active) {
			layer.setImage(pressed ? buttonType.clickedActiveImage
					: buttonType.unclickedActiveImage);
		} else {
			layer.setImage(pressed ? buttonType.clickedInactiveImage
					: buttonType.unclickedInactiveImage);
		}
	}

	public void setInactive() {
		active = false;
		updateImage();
	}

	public ImageLayer getImageLayer() {
		return layer;
	}

	public SequenceItemType getButtonType() {
		return buttonType;
	}

	public void setPressed() {
		pressed = true;
		updateImage();
	}

	public void setUnpressed() {
		pressed = false;
		updateImage();
	}

	public RFuture<SequenceButton> animateButton(Animator anim) {
		float duration = 1000f;
		RPromise<SequenceButton> promise = RPromise.create();
		anim.tweenScaleXY(layer)//
				.to(2f, 2f)//
				.in(duration)//
				.easeIn()//
				.then()//
				.tweenScaleXY(layer)//
				.to(1f, 1f)//
				.in(duration)//
				.easeOut();
		promise.isComplete();
		return promise;
	}
}
