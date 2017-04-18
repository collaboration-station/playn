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
package edu.bsu.issgame.android;

import static com.google.common.base.Preconditions.checkNotNull;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.util.Log;

public final class BluetoothNameManager {

	private static final String SUFFIX = "-ISSGame";

	private final BluetoothAdapter adapter;

	public BluetoothNameManager(BluetoothAdapter adapter) {
		this.adapter = checkNotNull(adapter);
	}

	public void ensureAdapterNameDoesNotEndInSuffix() {
		String name = adapter.getName();
		int indexOfSuffix = name.indexOf(SUFFIX);
		if (indexOfSuffix != -1) {
			String newName = name.substring(0, indexOfSuffix);
			updateName(newName);
		}
	}

	private void updateName(String newName) {
		checkNotNull(newName);
		adapter.setName(newName);
		Log.d(IssGameActivity.TAG, "Updated bluetooth name to " + newName);
	}

	public void appendHostSuffix() {
		updateName(adapter.getName() + SUFFIX);
	}

	public boolean isHostingDevice(BluetoothDevice device) {
		if (device.getName() == null) {
			Log.d(IssGameActivity.TAG,
					"Null device name is unexpected; considering as not host.");
			return false;
		}
		return device.getName().endsWith(SUFFIX);
	}

}
