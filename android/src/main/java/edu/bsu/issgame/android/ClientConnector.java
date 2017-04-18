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

import java.io.IOException;
import java.util.concurrent.ExecutorService;

import react.RPromise;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;
import edu.bsu.issgame.core.net.client.Client;

public final class ClientConnector extends RPromise<Client> implements Runnable {

	private final BluetoothDevice device;
	private final ExecutorService executorService;
	private BluetoothSocket clientSocket;
	private final int versionCode;

	public ClientConnector(BluetoothDevice device, ExecutorService executorService, int versionCode) {
		this.device = checkNotNull(device);
		this.executorService = checkNotNull(executorService);
		this.versionCode = versionCode;
	}

	@Override
	public void run() {
		clientSocket = createClientSocket();
		try {
			debug("Attempting to open socket connection to " + device.getName());
			clientSocket.connect();
		} catch (IOException connectException) {
			debug("Failed to connect to " + device.getName(), connectException);
			tryClosingSocket();
			fail(connectException);
			return;
		}
		try {
			debug("Starting client connected to " + device.getName());
			Client client = Client.withIO(BluetoothMessageIO
					.inClientMode(clientSocket)).withVersionCode(versionCode);
			succeed(client);
			executorService.execute(client);
			client.startPingClient(executorService);
		} catch (IOException e) {
			e.printStackTrace();
			fail(e);
			return;
		}
	}

	private BluetoothSocket createClientSocket() {
		try {
			debug("Creating socket to connect to " + device.getName());
			return device
					.createRfcommSocketToServiceRecord(BluetoothNetworkInterface.ISSGAME_UUID);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private void tryClosingSocket() {
		try {
			clientSocket.close();
		} catch (IOException closeException) {
			// Ignore.
		}
	}

	public void cancel() {
		try {
			clientSocket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void debug(String mesg) {
		Log.d(IssGameActivity.TAG, mesg);
	}
	
	private void debug(String mesg, Throwable t) {
		Log.d(IssGameActivity.TAG, mesg, t);
	}

}
