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
import static playn.core.PlayN.log;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.StreamCorruptedException;

import android.bluetooth.BluetoothSocket;
import android.util.Log;
import edu.bsu.issgame.core.net.Message;
import edu.bsu.issgame.core.net.MessageIO;

public class BluetoothMessageIO implements MessageIO {

	public static BluetoothMessageIO inServerMode(BluetoothSocket socket)
			throws IOException {
		return new BluetoothMessageIO(socket, Mode.INPUT_FIRST);
	}

	public static BluetoothMessageIO inClientMode(BluetoothSocket socket)
			throws IOException {
		return new BluetoothMessageIO(socket, Mode.OUTPUT_FIRST);
	}

	/**
	 * Specifies whether the input or output should be configured first. This is
	 * critical, because of the fact that objectinputstream, to be instantiated,
	 * has to read header information from objectoutputstreams on the other
	 * side.
	 * 
	 * @see http://stackoverflow.com/a/5658109/176007
	 */
	private enum Mode {
		INPUT_FIRST, OUTPUT_FIRST;
	}

	private final ObjectInput in;
	private final ObjectOutput out;
	private final BluetoothSocket socket;

	public BluetoothMessageIO(BluetoothSocket socket, Mode mode)
			throws IOException {
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
		try {
			out.writeObject(message);
			out.flush();
		} catch (StreamCorruptedException e) {
			// This is expected when one side of the socket dies, e.g. bluetooth
			// turned off.
			log().warn(
					"Could not send message because one end presumably died: "
							+ message);
		}
		return this;
	}

	@Override
	public void cancel() {
		Log.d(IssGameActivity.TAG, "Cancelling bluetooth message io");
		try {
			socket.close();
		} catch (IOException e) {
			Log.w(IssGameActivity.TAG,
					"Problem closing bluetooth socket: " + e.getMessage());
			e.printStackTrace();
		}
	}

	@Override
	public boolean isNetworked() {
		return true;
	}

}
