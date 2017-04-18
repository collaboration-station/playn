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
import playn.android.AndroidGraphics;
import playn.android.GameActivity;
import playn.core.Font;
import playn.core.PlayN;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;
import edu.bsu.issgame.core.FontInfo;
import edu.bsu.issgame.core.IssGame;

public final class IssGameActivity extends GameActivity {

	private static final int REQUEST_ENABLE_BT = 4720;
	public static final String TAG = IssGameActivity.class.getSimpleName();

	private BluetoothNameManager nameManager;
	private BluetoothAdapter bluetoothAdapter;
	private final BluetoothNetworkInterface net = new BluetoothNetworkInterface(
			BluetoothAdapter.getDefaultAdapter(), this);
	private BroadcastReceiver receiver;

	@Override
	public void onCreate(Bundle savedState) {
		super.onCreate(savedState);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		if (bluetoothAdapter == null) {
			throw new IllegalStateException(
					"We don't support Bluetooth not being supported.");
		}
		if (!bluetoothAdapter.isEnabled()) {
			Intent enableBtIntent = new Intent(
					BluetoothAdapter.ACTION_REQUEST_ENABLE);
			startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
		}
		nameManager = new BluetoothNameManager(bluetoothAdapter);
		nameManager.ensureAdapterNameDoesNotEndInSuffix();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		debug("Request code==" + requestCode);
		if (requestCode == REQUEST_ENABLE_BT) {
			if (resultCode == RESULT_OK) {
				debug("Bluetooth is now enabled.");
			} else {
				debug("Problem enabling bluetooth!");
			}
		}
		if (requestCode == BluetoothNetworkInterface.DISCOVERABLE_AS_SERVER_REQUEST_CODE) {
			debug("Got bluetooth discovery response");
			net.handleDiscoverableResult(resultCode);
		}
	}

	protected BluetoothNameManager bluetoothNameManager() {
		return nameManager;
	}

	private void debug(String message) {
		Log.d(TAG, message);
	}

	@Override
	public void main() {
		registerFonts();
		PlayN.run(new IssGame(net));
	}

	private void registerFonts() {
		AndroidGraphics g = platform().graphics();
		for (FontInfo info : FontInfo.values()) {
			g.registerFont(info.path, info.name, Font.Style.PLAIN);
		}
	}

	@Override
	protected void onStop() {
		Log.d(TAG, "onStop");
		net.shutdown();
		finish();
		super.onStop();
	}

	@Override
	protected void onDestroy() {
		if (receiver != null) {
			unregisterReceiver(receiver);
		}
		super.onDestroy();
	}

	@Override
	public Intent registerReceiver(BroadcastReceiver receiver,
			IntentFilter filter) {
		this.receiver = checkNotNull(receiver);
		return super.registerReceiver(receiver, filter);
	}
}
