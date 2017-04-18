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
import static com.google.common.base.Preconditions.checkState;
import static playn.core.PlayN.graphics;
import static playn.core.PlayN.log;

import java.util.List;

import playn.core.CanvasImage;
import playn.core.Font;
import playn.core.Image;
import playn.core.ImageLayer;
import playn.core.Layer;
import playn.core.TextFormat;
import playn.core.TextLayout;
import pythagoras.f.IPoint;
import react.Connection;
import react.Slot;
import react.UnitSlot;
import react.Value;
import tripleplay.anim.AnimGroup;
import tripleplay.anim.Animation;
import tripleplay.game.ScreenStack;
import tripleplay.game.UIAnimScreen;
import tripleplay.game.trans.SlideTransition;
import tripleplay.ui.Button;
import tripleplay.util.Colors;

import com.google.common.collect.Lists;

import edu.bsu.issgame.core.assetmgt.Jukebox;
import edu.bsu.issgame.core.mission.Scenario;
import edu.bsu.issgame.core.net.ClientId;
import edu.bsu.issgame.core.net.NetworkInterface;
import edu.bsu.issgame.core.net.client.Client;

public abstract class AbstractGameScreen extends UIAnimScreen {

	protected Client client;
	protected ScreenStack screenStack;
	protected NetworkInterface net;
	protected Settings settings;
	protected ScreenScale scale;
	protected boolean isCommander;
	protected ClientId clientIdForThisPlayer;
	protected Integer timeRemaining;
	protected Scenario scenario;
	protected Value<Country> country;
	private FlagSystem flagSystem = new FlagSystem(this);
	protected PlayerMinigameMap miniGameMap;
	private List<Connection> connectionsToDisableOnRemoved = Lists
			.newArrayList();

	protected AbstractGameScreen(AbstractGameScreen previous) {
		if (previous != null) {
			this.scenario = previous.scenario;
			this.client = previous.client;
			this.screenStack = previous.screenStack;
			this.net = previous.net;
			this.settings = previous.settings;
			this.scale = previous.scale;
			this.isCommander = previous.isCommander;
			this.country = previous.country;
			this.miniGameMap = previous.miniGameMap;
		} else {
			this.settings = Settings.SETTINGS;
			this.scale = new ScreenScale();
			this.isCommander = false;
			this.client = null;
			this.country = Value.create(null);
			this.miniGameMap = null;
		}
		if (client != null) {
			watchForServerGoingDown();
		}
	}

	private void watchForServerGoingDown() {
		Connection onServerDown = client.onServerDown().connect(new UnitSlot() {
			@Override
			public void onEmit() {
				log().debug("Server went down");
				client.cancel();
				net.shutdown();
				Popup.on(AbstractGameScreen.this)
						.show(isCommander ? "Your Crew is lost in space!"
								: "Your Commander is lost in space!")//
						.onSuccess(new UnitSlot() {
							@Override
							public void onEmit() {
								screenStack.replace(new RoleSelectScreen(
										AbstractGameScreen.this),
										new SlideTransition(screenStack)
												.right());
							}
						});
			}
		});
		connectionsToDisableOnRemoved.add(onServerDown);
	}

	protected void enableFlagAssignmentAnimation() {
		Connection countryConnection = flagSystem
				.configureAnimateFlagOnAssignment(country);
		connectionsToDisableOnRemoved.add(countryConnection);
	}

	@Override
	public void wasShown() {
		super.wasShown();
		if (isThereACountryAssigned()) {
			flagSystem.showFlagUnlessAnimationIsConfigured(country.get());
		}
	}

	private boolean isThereACountryAssigned() {
		return country.get() != null;
	}

	@Override
	public void wasRemoved() {
		super.wasRemoved();
		for (Connection c : connectionsToDisableOnRemoved) {
			c.disconnect();
		}
	}

	@Override
	public void update(int deltaMS) {
		super.update(deltaMS);
		Jukebox.instance().update(deltaMS);
	}

	public ScreenScale getScreenScale() {
		return scale;
	}

	protected final float percentOfScreenHeight(float f) {
		return f * graphics().height();
	}

	protected final float percentOfScreenWidth(float f) {
		return f * graphics().width();
	}

	public ScreenStack getScreenstack() {
		return screenStack;
	}

	protected ClientId getClientIdForThisPlayer() {
		if (client == null) {
			return null;
		}
		for (ClientId clientId : client.getConnectedClients()) {
			if (client.uuid.equals(clientId.uuid)) {
				clientIdForThisPlayer = clientId;
			} else {
				log().debug(
						"Could not find clientId uuid matching this client uuid");
			}
		}
		return clientIdForThisPlayer;
	}

	protected Button makeReturnToEarthButton() {
		return new Button("Return to Earth").onClick(new Slot<Button>() {
			@Override
			public void onEmit(Button event) {
				net.shutdown();
				screenStack.replace(new WelcomeScreen(AbstractGameScreen.this));
			}
		});
	}

	private static final class FlagSystem {
		private static final float FLAG_CENTER_PERCENT_OF_SCREEN_WIDTH = 0.055f;
		private static final float FLAG_CENTER_PERCENT_OF_SCREEN_HEIGHT = 0.07f;
		private static final IPoint FLAG_POSITION = new pythagoras.f.Point(
				graphics().width() * FLAG_CENTER_PERCENT_OF_SCREEN_WIDTH,
				graphics().height() * FLAG_CENTER_PERCENT_OF_SCREEN_HEIGHT);
		private static final float FLAG_SIZE_PERCENT_OF_SCREEN_WIDTH = 0.10f;
		private static final float BIG_FLAG_SIZE_PERCENT_OF_SCREEN = 0.85f;

		private boolean flagAssignmentAnimation = false;
		private final UIAnimScreen screen;
		private Country country;

		public FlagSystem(UIAnimScreen screen) {
			this.screen = checkNotNull(screen);
		}

		public void showFlagUnlessAnimationIsConfigured(Country country) {
			if (!flagAssignmentAnimation) {
				addCountryFlagLayer(country);
			}
		}

		public Connection configureAnimateFlagOnAssignment(
				Value<Country> countryValue) {
			return countryValue.connect(new Slot<Country>() {
				@Override
				public void onEmit(Country assignedCountry) {
					if (assignedCountry != null) {
						animateCountryFlagLayer(assignedCountry);
					}
				}
			});
		}

		private Layer animateCountryFlagLayer(Country country) {
			this.country = checkNotNull(country);
			ImageLayer flagLayer = createCountryFlagLayer(country);
			flagLayer.setScale(bigFlagScale(flagLayer.image()));
			flagLayer.setTranslation(graphics().width() / 2, graphics()
					.height() / 2);
			screen.layer.add(flagLayer);
			animateToCorner(flagLayer);
			return flagLayer;
		}

		private float bigFlagScale(Image image) {
			float scale = (graphics().width() * BIG_FLAG_SIZE_PERCENT_OF_SCREEN)
					/ image.width();
			return scale;
		}

		private void animateToCorner(ImageLayer flagLayer) {
			Font font = GameFont.BOLD_HUGE.font;
			TextFormat format = new TextFormat().withFont(font).withAntialias(
					true);
			TextLayout layout = graphics().layoutText(country.asText, format);
			final ImageLayer textLayer = createTextLayer(layout,
					colorFor(country));
			textLayer.setTranslation(graphics().width() / 2, graphics()
					.height() / 2);

			textLayer.setAlpha(0);
			screen.anim.delay(100f)//
					.then()//
					.action(new Runnable() {
						@Override
						public void run() {
							screen.layer.add(textLayer);
						}
					})//
					.then()//
					.tweenAlpha(textLayer)//
					.to(1)//
					.in(250f)//
					.easeIn()//
					.then()//
					.delay(750f)//
					.then()//
					.tweenAlpha(textLayer)//
					.to(0)//
					.in(250f)//
					.easeOut()//
					.then()//
					.action(new Runnable() {
						@Override
						public void run() {
							screen.layer.remove(textLayer);
						}
					})//
					.then()//
					.add(createFlagToCornerAnimation(flagLayer));
		}

		private int colorFor(Country country) {
			switch (country) {
			case CANADA:
				return Palette.LIGHT_BLUE.color;
			case USA:
				return Palette.LIGHT_BLUE.color;
			case CHINA:
				return Colors.WHITE;
			case EU:
				return Colors.WHITE;
			case JAPAN:
				return Palette.LIGHT_BLUE.color;
			default:
				log().warn("No color mapping for " + country);
				return Palette.TAN.color;
			}
		}

		private Animation createFlagToCornerAnimation(ImageLayer flagLayer) {
			final int duration = 750;
			AnimGroup group = new AnimGroup();
			group.tweenScale(flagLayer)//
					.to(flagScale(flagLayer.image()))//
					.in(duration)//
					.easeIn();
			group.tweenTranslation(flagLayer)//
					.to(FLAG_POSITION.x(), FLAG_POSITION.y())//
					.in(duration)//
					.easeIn();
			return group.toAnim();
		}

		protected ImageLayer createTextLayer(TextLayout layout, int color) {
			CanvasImage image = graphics().createImage(
					(int) Math.ceil(layout.width()),
					(int) Math.ceil(layout.height()));
			image.canvas().setFillColor(color);
			image.canvas().fillText(layout, 0, 0);
			ImageLayer layer = graphics().createImageLayer(image);
			layer.setOrigin(layer.width() / 2, layer.height() / 2);
			return layer;
		}

		private void addCountryFlagLayer(Country country) {
			checkState(country != null, "Country not specified.");
			Layer.HasSize flagLayer = createCountryFlagLayer(country);
			screen.layer.addAt(flagLayer, FLAG_POSITION.x(), FLAG_POSITION.y());
		}

		private ImageLayer createCountryFlagLayer(Country country) {
			Image image = CountryFlagImage.getCountryImage(country);
			ImageLayer layer = graphics().createImageLayer(image);
			layer.setOrigin(layer.width() / 2, layer.height() / 2);
			layer.setScale(flagScale(image));
			return layer;
		}

		private float flagScale(Image image) {
			float scale = (graphics().width() * FLAG_SIZE_PERCENT_OF_SCREEN_WIDTH)
					/ image.width();
			return scale;
		}
	}
}
