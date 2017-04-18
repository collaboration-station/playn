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
import static playn.core.PlayN.log;
import playn.core.CanvasImage;
import playn.core.Font;
import playn.core.ImageLayer;
import playn.core.TextFormat;
import playn.core.TextLayout;
import react.RFuture;
import react.RPromise;
import tripleplay.game.AnimScreen;
import tripleplay.util.Colors;

public final class Popup {

	private static final int POPUP_TIME = 750;
	private static final int FADE_TIME = 500;
	public static final int ANIMATION_TIME = POPUP_TIME + FADE_TIME;
	
	public static final class Builder {

		private final AnimScreen screen;

		private Builder(AnimScreen screen) {
			this.screen = checkNotNull(screen);
		}

		public RFuture<Void> show(String text) {
			log().debug("Popup: " + text);
			final RPromise<Void> promise = RPromise.create();
			final ImageLayer popupLayer = createPopupLayerAtBottomOfScreen(text);
			screen.layer.add(popupLayer);
			screen.anim.tweenY(popupLayer)//
					.to(graphics().height() * 0.9f)//
					.in(POPUP_TIME)//
					.easeOut()//
					.then()//
					.tweenAlpha(popupLayer)//
					.to(0)//
					.in(FADE_TIME)//
					.then()//
					.action(new Runnable() {
						@Override
						public void run() {
							screen.layer.remove(popupLayer);
							promise.succeed(null);
						}
					});
			return promise;
		}

		private ImageLayer createPopupLayerAtBottomOfScreen(String text) {
			CanvasImage image = createPopupImage(text);
			ImageLayer popupLayer = graphics().createImageLayer(image);
			popupLayer.setOrigin(popupLayer.width() / 2,
					popupLayer.height() / 2);
			popupLayer.setTranslation(graphics().width() / 2, graphics()
					.height());
			return popupLayer;
		}

		private CanvasImage createPopupImage(String text) {
			Font font = GameFont.TITLE.font;
			TextFormat format = new TextFormat().withFont(font);
			TextLayout layout = graphics().layoutText(text, format);
			CanvasImage image = graphics().createImage(
					(int) Math.ceil(layout.width()),
					(int) Math.ceil(layout.height()));
			image.canvas().setFillColor(Colors.WHITE);
			image.canvas().fillText(layout, 0, 0);
			return image;
		}
	}

	public static final Builder on(AnimScreen screen) {
		return new Builder(screen);
	}

}
