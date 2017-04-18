package edu.bsu.issgame.core.util;

import static playn.core.PlayN.graphics;
import playn.core.Image;
import tripleplay.ui.Icon;
import tripleplay.ui.Icons;

public final class IconScaler {

	public static IconScaler instance() {
		return INSTANCE;
	}

	private static final IconScaler INSTANCE = new IconScaler();
	private static final float SIZE_FOR_WHICH_ICON_WAS_DESIGNED = 800;
	private static final float SCALE = graphics().width()
			/ SIZE_FOR_WHICH_ICON_WAS_DESIGNED;

	private IconScaler() {
	}

	public Icon makeScaledIconFor(Image image) {
		Icon unscaled = Icons.image(image);
		return Icons.scaled(unscaled, SCALE);
	}

	public Icon makeIconScaledToPercentOfScreenHeight(float percent, Image image) {
		Icon unscaled = Icons.image(image);
		float scale = (graphics().height() * percent) /  image.height();
		return Icons.scaled(unscaled, scale);
	}
}
