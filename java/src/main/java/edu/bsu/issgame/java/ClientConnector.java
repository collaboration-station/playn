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
package edu.bsu.issgame.java;

import static com.google.common.base.Preconditions.checkNotNull;
import static playn.core.PlayN.log;

import java.io.IOException;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import react.Signal;
import react.SignalView;
import edu.bsu.issgame.core.net.MessageIO;
import edu.bsu.issgame.core.net.NetworkInterface;
import edu.bsu.issgame.core.net.client.Client;

public final class ClientConnector implements Runnable {

	public static ClientConnector connectingTo(InetAddress serverAddress) {
		return new ClientConnector(serverAddress);
	}

	private static final int TIMEOUT_MS = 500;

	private final InetAddress serverAddress;
	private final Signal<Client> clientConnected = Signal.create();
	private int maxConnectRetries = 5;
	private int delayBetweenConnectRetriesMS = 500;

	private ClientConnector(InetAddress serverAddress) {
		this.serverAddress = checkNotNull(serverAddress);
	}

	public SignalView<Client> onClientStarted() {
		return clientConnected;
	}

	@Override
	public void run() {
		log().debug("Client connector running, will connect to " //
				+ serverAddress + ":" + NetworkInterface.PORT);
		InetSocketAddress address = new InetSocketAddress(serverAddress,
				NetworkInterface.PORT);
		Client client = null;
		try {
			Socket socket = connectTo(address);
			MessageIO messageIO = MessageIO.SocketAdapter.inClientMode(socket);
			client = Client.withIO(messageIO).withVersionCode(JavaNetworkInterface.versionCode);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		ExecutorService executorService = Executors.newCachedThreadPool();
		clientConnected.emit(client);
		executorService.execute(client);
		client.startPingClient(executorService);
	}

	private Socket connectTo(InetSocketAddress address) throws IOException {
		int tries = 0;
		while (tries < maxConnectRetries) {
			try {
				Socket socket = new Socket();
				socket.bind(null);
				log().debug("Trying to connect");
				socket.connect(address, TIMEOUT_MS);
				log().debug("Connected to " + serverAddress);
				return socket;
			} catch (SocketTimeoutException e) {
				log().debug("Socket timeout.");
			} catch (ConnectException e) {
				log().debug("Connect exception.");
			} finally {
				tries++;
				if (tries < maxConnectRetries) {
					try {
						Thread.sleep(delayBetweenConnectRetriesMS);
					} catch (InterruptedException e) {
						// Nothing special to do here.
					}
				}
			}
		}
		throw new IOException("Failed to connect after " + maxConnectRetries
				+ "; giving up.");
	}
}
