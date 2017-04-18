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

import static com.google.common.base.Preconditions.checkNotNull;
import static playn.core.PlayN.log;

import java.io.EOFException;
import java.io.IOException;
import java.net.Socket;

import com.google.common.base.MoreObjects;

import react.Signal;
import react.SignalView;
import edu.bsu.issgame.core.CountryGenerationService;
import edu.bsu.issgame.core.Settings;
import edu.bsu.issgame.core.net.ClientId;
import edu.bsu.issgame.core.net.Message;
import edu.bsu.issgame.core.net.Message.Introduce;
import edu.bsu.issgame.core.net.Message.LogVerbosityLevel;
import edu.bsu.issgame.core.net.MessageIO;
import edu.bsu.issgame.core.util.BlockingReadOperationInterruptedException;

public class ClientHandler implements Runnable {

	final MessageIO io;
	private ClientId clientId;
	private final String expeditionName;
	private final int versionCode;

	private Signal<MessageReceivedEvent> received = Signal.create();

	public ClientHandler(Socket socket, String expeditionName, int versionCode)
			throws IOException {
		io = MessageIO.SocketAdapter.inServerMode(socket);
		this.expeditionName = checkNotNull(expeditionName);
		this.versionCode = versionCode;
	}

	public ClientHandler(MessageIO io, String expeditionName, int versionCode) {
		this.io = checkNotNull(io);
		this.expeditionName = checkNotNull(expeditionName);
		this.versionCode = versionCode;
	}

	public SignalView<MessageReceivedEvent> onReceived() {
		return received;
	}

	public ClientId id() {
		checkNotNull(clientId,
				"Should not be called before introduction is made.");
		return clientId;
	}

	@Override
	public void run() {
		final String indicator = "(" + io.getClass().getSimpleName() + ")";
		log().debug("Client handler started " + indicator);
		sayHelloAndAwaitIntroduction();
		try {
			while (true) {
				Message message = readMessage();
				received.emit(new MessageReceivedEvent(this, message));
			}
		} catch (EOFException e) {
			log().debug("Stream finished.");
		} catch (BlockingReadOperationInterruptedException e) {
			log().debug(
					"Blocking read was interrupted; probably local client cancellation.");
		} catch (IOException e) {
			log().warn("Client handler problem: " + e.getMessage() + " IOException");
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			log().warn("Client handler problem: " + e.getMessage() + " CNFException");
			e.printStackTrace();
		}
		log().debug("Client handler finished " + indicator);
	}

	private Message readMessage() throws IOException, ClassNotFoundException {
		Message message = io.read();
		if (shouldLog(message)) {
			log().debug("Client handler received " + message);
		}
		return message;
	}
	
	private boolean shouldLog(Message message) {
		return Settings.SETTINGS.get(Settings.LOG_TRACE_MESSAGES)
				|| message.logVerbosityLevel() == LogVerbosityLevel.DEBUG;
	}

	private void sayHelloAndAwaitIntroduction() {
		try {
			log().debug("Saying hello to new client.");
			io.send(new Message.Hello(expeditionName, versionCode));
			Message message = readMessage();
			message.accept(introductionHandler);
			received.emit(new MessageReceivedEvent(this, message));
		} catch (IOException ioe) {
			ioe.printStackTrace();
		} catch (ClassNotFoundException cnfe) {
			cnfe.printStackTrace();
		}
	}

	private Message.Visitor introductionHandler = new CautiousVisitor() {
		@Override
		public void visit(Introduce introduce, Object... args) {
			clientId = new ClientId(CountryGenerationService.instance().next(),
					introduce.uuid);
		}
	};

	@Override
	public String toString() {
		return MoreObjects.toStringHelper(this)
				.add("clientId", clientId)
				.toString();
	}

	public void cancel() {
		cancelTheIoHandlerWhichShouldStopTheRunMethod();
	}

	private void cancelTheIoHandlerWhichShouldStopTheRunMethod() {
		io.cancel();
	}

}