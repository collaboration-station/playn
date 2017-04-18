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
package edu.bsu.issgame.core.net.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import edu.bsu.issgame.core.net.MessageIO;

public class SocketConnectionAccepter implements ConnectionAccepter {

	private final ServerSocket serverSocket;

	public SocketConnectionAccepter(int port) throws IOException {
		this.serverSocket = new ServerSocket(port);
	}

	@Override
	public ClientHandler acceptClientConnection(Server server)
			throws IOException {
		Socket socket = serverSocket.accept();
		return new ClientHandler(MessageIO.SocketAdapter.inServerMode(socket),
				server.expeditionName, server.versionCode);
	}

	@Override
	public void shutdown() throws IOException {
		serverSocket.close();
	}

}
