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
package edu.bsu.issgame.core.util;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.junit.Test;

public class CircularByteBufferTest {

	@Test
	public void testWriteAndRead() throws IOException {
		byte value = 1;
		CircularByteBuffer b = new CircularByteBuffer();
		b.getOutputStream().write(value);
		byte actual = (byte)b.getInputStream().read();
		assertEquals(value, actual);
	}
	
	@Test
	public void testWriteAndReadWithObjectStreams() throws IOException,
			ClassNotFoundException {
		Object value = "Hello";
		CircularByteBuffer b = new CircularByteBuffer();
		ObjectOutputStream out = new ObjectOutputStream(b.getOutputStream());
		out.writeObject(value);
		out.flush();
		ObjectInputStream in = new ObjectInputStream(b.getInputStream());
		Object actual = in.readObject();
		assertEquals(value, actual);
	}

}
