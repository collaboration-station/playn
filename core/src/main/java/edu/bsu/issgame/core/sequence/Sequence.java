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

import static playn.core.PlayN.random;

import java.util.ArrayList;
import java.util.List;

import tripleplay.util.Bag;

public class Sequence {

	private final Bag<SequenceItemType> valueBag = new Bag<SequenceItemType>();
	private final List<SequenceItemType> sequenceList;
	private int pointer = 0;

	public Sequence() {
		sequenceList = new ArrayList<SequenceItemType>();
		fillValueBag();
	}

	private void fillValueBag() {
		for (SequenceItemType type : SequenceItemType.values()) {
			for (int i = 0; i < 2; i++) {
				valueBag.add(type);
			}
		}
	}

	public Sequence addItem() {
		if (valueBag.isEmpty()) {
			fillValueBag();
		}
		int randomIndex = (int) (random() * valueBag.size());
		SequenceItemType randomItem = valueBag.removeAt(randomIndex);
		sequenceList.add(randomItem);
		return this;
	}

	public boolean isClickCorrect(SequenceButton button) {
		if (button.getButtonType().equals(sequenceList.get(pointer))) {
			return true;
		} else {
			return false;
		}
	}

	public void reset() {
		sequenceList.clear();
	}

	public boolean isComplete() {
		if (pointer == sequenceList.size()) {
			return true;
		} else {
			return false;
		}
	}

	public void incrementPointer() {
		pointer++;
	}

	public void resetPointer() {
		pointer = 0;
	}

	public List<SequenceItemType> getSequence() {
		return sequenceList;
	}

	public int getLength() {
		return sequenceList.size();
	}

}
