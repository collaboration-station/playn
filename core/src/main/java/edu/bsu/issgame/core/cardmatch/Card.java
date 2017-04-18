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
package edu.bsu.issgame.core.cardmatch;

import static com.google.common.base.Preconditions.checkNotNull;
import static playn.core.PlayN.graphics;
import playn.core.Image;
import playn.core.ImageLayer;
import playn.core.Layer;
import pythagoras.f.IDimension;
import react.RFuture;
import react.RPromise;
import react.Slot;
import react.Value;
import react.ValueView;
import tripleplay.anim.Animator;

import com.google.common.base.MoreObjects;

import edu.bsu.issgame.core.GameImage;
import edu.bsu.issgame.core.GameSound;

public class Card {
	public static final int FLIP_TIME_MS = 300;
	private static final float EPSILON = 0.0001f;

	private final Animator anim;
	private ImageLayer imageLayer;
	private final CardType cardType;
	private Value<Facing> cardface = Value.create(Facing.DOWN);
	public final Layer.HasSize layer;
	private boolean isCardInputAllowed = true;

	public Card(CardType card, Animator anim, IDimension size) {
		this.cardType = checkNotNull(card);
		this.anim = checkNotNull(anim);
		imageLayer = createFaceDownCardLayer(size);
		layer = imageLayer;
		imageLayer.setOrigin(imageLayer.width() / 2, imageLayer.height() / 2);
	}

	private ImageLayer createFaceDownCardLayer(IDimension size) {
		ImageLayer imageLayer = graphics().createImageLayer(
				GameImage.CARD_BACK.image);
		imageLayer.setSize(size.width(), size.height());
		return imageLayer;
	}

	public RFuture<Card> flip() {
		playFlipSound();
		if (cardface.get().equals(Facing.DOWN)) {
			return animateFlipUp(0);
		} else {
			return animateFlipDown(0);
		}
	}

	public RFuture<Card> flip(int delay) {
		playFlipSound();
		if (cardface.get().equals(Facing.DOWN)) {
			return animateFlipUp(delay);
		} else {
			return animateFlipDown(delay);
		}
	}

	private RFuture<Card> animateFlipUp(int delay) {
		this.setInputDisabled();
		return animate(cardType.image, delay).onSuccess(new Slot<Card>() {
			@Override
			public void onEmit(Card event) {
				cardface.update(Facing.UP);
				event.setInputEnabled();
			}
		});
	}

	private RFuture<Card> animate(final Image nextImage, int delay) {
		final RPromise<Card> promise = RPromise.create();
		anim.delay(delay)//
				.then()//
				.tweenScaleX(imageLayer)//
				.to(EPSILON)//
				.in(FLIP_TIME_MS / 2f)//
				.easeIn()//
				.then()//
				.action(new Runnable() {
					@Override
					public void run() {
						imageLayer.setImage(nextImage);
					}
				})//
				.then()//
				.tweenScaleX(imageLayer)//
				.to(1f)//
				.easeOut()//
				.in(FLIP_TIME_MS / 2f)//
				.then()//
				.action(new Runnable() {
					@Override
					public void run() {
						promise.succeed(Card.this);
					}
				});
		return promise;
	}

	private RFuture<Card> animateFlipDown(int delay) {

		this.setInputDisabled();
		return animate(GameImage.CARD_BACK.image, delay).onSuccess(
				new Slot<Card>() {
					@Override
					public void onEmit(Card event) {
						cardface.update(Facing.DOWN);
						event.setInputEnabled();
					}
				});
	}

	public void playFlipSound() {
		GameSound.CARD_FLIP.sound.play();
	}

	public boolean isMatch(Card cardToMatch) {
		if (this == cardToMatch) {
			return false;
		}
		return cardType.equals(cardToMatch.cardType);
	}

	public ValueView<Facing> facing() {
		return cardface;
	}

	@Override
	public String toString() {
		return MoreObjects.toStringHelper(this)//
				.add("cardType", cardType)//
				.toString();
	}

	public boolean isCardInputAllowed() {
		return isCardInputAllowed;
	}

	public void setInputDisabled() {
		isCardInputAllowed = false;
	}

	public void setInputEnabled() {
		isCardInputAllowed = true;
	}
}
