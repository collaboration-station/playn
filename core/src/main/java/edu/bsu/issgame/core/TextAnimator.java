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

import react.Value;
import tripleplay.anim.Animation;

public final class TextAnimator implements Animation.Value {

	public static TextAnimator collapse(Value<String> value) {
		return new TextAnimator(value, value.get());
	}

	public static Builder expand(Value<String> value) {
		return new Builder(value);
	}

	public static final class Builder {
		private final Value<String> value;

		private Builder(Value<String> value) {
			this.value = value;
		}

		public TextAnimator toTargetText(String targetText) {
			return new TextAnimator(value, targetText);
		}
	}

	private static final String SPACE_SO_THAT_LABEL_KEEPS_VERTICAL_SIZE = " ";
	private final String fullText;
	private final Value<String> stringValue;

	private TextAnimator(Value<String> value, String targetText) {
		this.stringValue = value;
		this.fullText = targetText;
	}

	@Override
	public float initial() {
		return 1.0f;
	}

	@Override
	public void set(float value) {
		final int numberOfCharacters = (int) (fullText.length() * value);
		String newText = fullText.substring(fullText.length()
				- numberOfCharacters);
		if (newText.isEmpty()) {
			newText = SPACE_SO_THAT_LABEL_KEEPS_VERTICAL_SIZE;
		}
		stringValue.update(newText);
	}

}
