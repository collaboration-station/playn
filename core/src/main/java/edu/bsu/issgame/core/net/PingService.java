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

import static com.google.common.base.Preconditions.checkNotNull;
import static playn.core.PlayN.log;

import java.io.IOException;

import react.Connection;
import react.Slot;
import react.UnitSignal;
import react.UnitSlot;
import edu.bsu.issgame.core.net.Message.Ping;
import edu.bsu.issgame.core.net.Message.PingAck;
import edu.bsu.issgame.core.net.server.MessageReceivedEvent;

public class PingService {

	private static final int DEFAULT_SERVER_ACK_WINDOW_MS = 1000;
	private static final int DEFAULT_CLIENT_TIME_TO_LIVE_MS = 2500;

	public static class Server extends Slot<MessageReceivedEvent> implements
			Runnable {
		private final MessageIO io;
		private int timeToLive = DEFAULT_SERVER_ACK_WINDOW_MS;
		private boolean receivedPingAck = false;

		private final UnitSignal onFailure = new UnitSignal();

		private Server(MessageIO io) {
			this.io = checkNotNull(io);
		}

		public void run() {
			do {
				receivedPingAck = false;
				sendPing();
				sleepWhileAwaitingPingAcknowledgement();
			} while (receivedPingAck);
			onFailure.emit();
		}

		private void sendPing() {
			try {
				io.send(Message.Ping.instance());
			} catch (IOException ioe) {
				throw new RuntimeException(ioe);
			}
		}

		private void sleepWhileAwaitingPingAcknowledgement() {
			try {
				Thread.sleep(timeToLive);
			} catch (InterruptedException ie) {
				// Ignore it.
			}
		}

		public Server acknowledgmentWindow(int acknowledgementWindowMS) {
			timeToLive = acknowledgementWindowMS;
			return this;
		}

		public Connection onFailure(UnitSlot slot) {
			return onFailure.connect(slot);
		}

		private Message.Visitor v = new Message.Visitor.Adapter() {

			@Override
			public void visit(Ping ping, Object... args) {
				throw new IllegalStateException(
						"Server should not receive pings.");
			}

			@Override
			public void visit(PingAck pingAck, Object... args) {
				receivedPingAck = true;
			}
		};

		@Override
		public void onEmit(MessageReceivedEvent event) {
			event.message.accept(v);
		}
	}

	public static Server createServer(MessageIO io) {
		return new Server(io);
	}

	public static final class Client implements Runnable {
		private final MessageIO io;
		private int timeToLiveMS = DEFAULT_CLIENT_TIME_TO_LIVE_MS;
		private final UnitSignal onFailure = new UnitSignal();
		private boolean receivedPingWithinLastTimeWindow = false;
		private boolean stopped = false;

		public final Message.Visitor visitor = new Message.Visitor.Adapter() {
			@Override
			public void visit(Ping ping, Object... args) {
				if (!stopped) {
					receivedPingWithinLastTimeWindow = true;
					try {
						io.send(Message.PingAck.instance());
					} catch (IOException e) {
						throw new RuntimeException(e);
					}
				}
			}
		};

		private Client(MessageIO io) {
			this.io = checkNotNull(io);
		}

		public Client timeToLive(int timeToLiveMs) {
			this.timeToLiveMS = timeToLiveMs;
			return this;
		}

		public Connection onFailure(UnitSlot slot) {
			return onFailure.connect(slot);
		}

		@Override
		public void run() {
			do {
				receivedPingWithinLastTimeWindow = false;
				try {
					Thread.sleep(timeToLiveMS);
				} catch (InterruptedException ie) {
					log().warn(
							"Ping service client thread is interrupted: " + ie);
				}
			} while (receivedPingWithinLastTimeWindow && !stopped);
			if (!stopped) {
				onFailure.emit();
			}
		}

		public void stop() {
			stopped = true;
		}
	}

	public static Client createClient(MessageIO io) {
		return new Client(io);
	}
}
