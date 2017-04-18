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

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import edu.bsu.issgame.core.util.CircularByteBuffer;

public final class MessageIOPump {

	public final PumpingIO a;
	public final PumpingIO b;
	private final CircularByteBuffer c1;
	private final CircularByteBuffer c2;

	public MessageIOPump() throws IOException {
		c1 = new CircularByteBuffer(4096);
		c2 = new CircularByteBuffer(4096);

		a = new PumpingIO();
		b = new PumpingIO();

		a.out = new ObjectOutputStream(c1.getOutputStream());
		b.out = new ObjectOutputStream(c2.getOutputStream());

		a.in = new ObjectInputStream(c2.getInputStream());
		b.in = new ObjectInputStream(c1.getInputStream());
	}

	public final class PumpingIO implements MessageIO {

		private Thread readingThread;
		public ObjectInputStream in;
		public ObjectOutputStream out;

		@Override
		public Message read() throws IOException, ClassNotFoundException {
			readingThread = Thread.currentThread();
			return (Message) in.readObject();
		}

		@Override
		public MessageIO send(Message message) throws IOException {
			out.writeObject(message);
			out.flush();
			return this;
		}

		private void interruptReading() {
			if (readingThread != null) {
				readingThread.interrupt();
			}
		}

		@Override
		public void cancel() {
			interruptReading();
		}

		@Override
		public boolean isNetworked() {
			return false;
		}
	}

	public void stop() {
		a.cancel();
		b.cancel();
	}

}
