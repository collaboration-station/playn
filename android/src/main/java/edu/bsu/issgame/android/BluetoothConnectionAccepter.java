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

import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.util.Log;
import edu.bsu.issgame.core.net.server.ClientHandler;
import edu.bsu.issgame.core.net.server.ConnectionAccepter;
import edu.bsu.issgame.core.net.server.Server;

public class BluetoothConnectionAccepter implements ConnectionAccepter {

	private final BluetoothServerSocket serverSocket;

	public BluetoothConnectionAccepter(BluetoothServerSocket serverSocket) {
		this.serverSocket = checkNotNull(serverSocket);
	}

	@Override
	public ClientHandler acceptClientConnection(Server server) throws IOException {
		Log.d(IssGameActivity.TAG, "Accepting bluetooth socket connection");
		BluetoothSocket socket = serverSocket.accept();
		Log.d(IssGameActivity.TAG,
				"Bluetooth connection established, creating client handler");
		return new ClientHandler(BluetoothMessageIO.inServerMode(socket), server.expeditionName, server.versionCode);
	}

	@Override
	public void shutdown() throws IOException {
		Log.d(IssGameActivity.TAG, "Closing bluetooth server socket");
		serverSocket.close();
	}

}
