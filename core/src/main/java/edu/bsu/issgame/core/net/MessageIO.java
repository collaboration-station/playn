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
import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;

public interface MessageIO {

	Message read() throws IOException, ClassNotFoundException;

	MessageIO send(Message message) throws IOException;

	void cancel();
	
	boolean isNetworked();

	public static final class SocketAdapter implements MessageIO {

		public static SocketAdapter inServerMode(Socket socket)
				throws IOException {
			return new SocketAdapter(socket, Mode.INPUT_FIRST);
		}

		public static SocketAdapter inClientMode(Socket socket)
				throws IOException {
			return new SocketAdapter(socket, Mode.OUTPUT_FIRST);
		}

		/**
		 * Specifies whether the input or output should be configured first.
		 * This is critical, because of the fact that objectinputstream, to be
		 * instantiated, has to read header information from objectoutputstreams
		 * on the other side.
		 * 
		 * @see http://stackoverflow.com/a/5658109/176007
		 */
		private enum Mode {
			INPUT_FIRST, OUTPUT_FIRST;
		}

		private final ObjectInput in;
		private final ObjectOutput out;
		private final Socket socket;

		private SocketAdapter(Socket socket, Mode mode) throws IOException {
			this.socket = checkNotNull(socket);
			InputStream in = socket.getInputStream();
			OutputStream out = socket.getOutputStream();

			if (mode == Mode.INPUT_FIRST) {
				this.in = new ObjectInputStream(in);
				this.out = new ObjectOutputStream(out);
			} else {
				this.out = new ObjectOutputStream(out);
				this.in = new ObjectInputStream(in);
			}
		}

		@Override
		public Message read() throws IOException, ClassNotFoundException {
			return (Message) in.readObject();
		}

		@Override
		public MessageIO send(Message message) throws IOException {
			checkNotNull(message);
			out.writeObject(message);
			out.flush();
			return this;
		}

		@Override
		public void cancel() {
			try {
				socket.close();
			} catch (IOException e) {
				log().warn("Problem closing socket: " + e.getMessage());
				e.printStackTrace();
			}
		}

		@Override
		public boolean isNetworked() {
			return true;
		}
	}
}
