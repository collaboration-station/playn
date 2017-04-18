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

import static org.mockito.Mockito.mock;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

public final class TestClientIO implements MessageIO {
	public final ObjectOutput out = mock(ObjectOutput.class);
	public final ObjectInput in = mock(ObjectInput.class);

	@Override
	public Message read() throws IOException, ClassNotFoundException {
		return (Message) in.readObject();
	}

	@Override
	public MessageIO send(Message message) throws IOException {
		out.writeObject(message);
		out.flush();
		return this;
	}

	@Override
	public void cancel() {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean isNetworked() {
		// Pretend I am networked because I am for testing.
		return true;
	}
}
