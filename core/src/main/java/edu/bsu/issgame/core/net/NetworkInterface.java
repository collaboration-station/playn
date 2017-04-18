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
package edu.bsu.issgame.core.net;

import react.RFuture;
import react.Signal;
import react.SignalView;
import edu.bsu.issgame.core.net.client.Client;

public abstract class NetworkInterface {

	public static final int PORT = 8910;

	protected Signal<Client> clientCreated = Signal.create();
	protected Signal<String> networkError = Signal.create();

	public SignalView<Client> onClientCreated() {
		return clientCreated;
	}
	
	public SignalView<String> onNetworkError() {
		return networkError;
	}

	public abstract RFuture<Boolean> startGameService();
	public abstract void discoverGameService();

	public abstract void shutdown();
}
