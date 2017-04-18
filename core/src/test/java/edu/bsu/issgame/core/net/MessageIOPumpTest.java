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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.junit.Test;

public class MessageIOPumpTest {

	@Test
	public void testSend() throws IOException, ClassNotFoundException {
		TestMessage message = new TestMessage();
		MessageIOPump pump = new MessageIOPump();
		pump.a.send(message);
		Message received = pump.b.read();
		assertEquals(message, received);
	}

	@Test
	public void testStop_interruptsReader() throws IOException,
			InterruptedException {
		final MessageIOPump pump = new MessageIOPump();
		MessageReader reader = new MessageReader(pump);
		ExecutorService service = Executors.newCachedThreadPool();
		service.execute(reader);
		sleepABitSoTheReadHappens();
		pump.stop();
		service.shutdown();
		boolean terminated = service.awaitTermination(1, TimeUnit.SECONDS);
		assertTrue("Timeout while awaiting executor service shutdown",
				terminated);
		assertTrue(reader.wasInterruptedByException);
	}

	private void sleepABitSoTheReadHappens() {
		try {
			Thread.sleep(100);
		} catch (InterruptedException ie) {
			fail();
		}
	}

	private class MessageReader implements Runnable {
		private final MessageIOPump pump;
		boolean wasInterruptedByException = false;

		public MessageReader(MessageIOPump pump) {
			this.pump = pump;
		}

		public void run() {
			try {
				pump.b.read();
			} catch (IOException e) {
				wasInterruptedByException = true;
			} catch (ClassNotFoundException e) {
				fail(e.getMessage());
			}
		}
	}

	private static class TestMessage extends Message.AbstractMessage {
		private static final long serialVersionUID = -1608919046377594850L;

		@Override
		public void accept(Visitor visitor, Object... args) {
			throw new UnsupportedOperationException();
		}

		@Override
		public int hashCode() {
			return 4720;
		}

		@Override
		public boolean equals(Object obj) {
			return (obj instanceof TestMessage);
		}
	}
}
