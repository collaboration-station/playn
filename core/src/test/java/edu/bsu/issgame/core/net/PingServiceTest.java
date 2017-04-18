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

import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

import react.UnitSlot;
import edu.bsu.issgame.core.net.Message.Ping;
import edu.bsu.issgame.core.net.Message.PingAck;

public class PingServiceTest {

	private static final int TEST_TIME_TO_LIVE_MS = 1;
	private MessageIO io;
	private PingService.Server server;
	private UnitSlot onFailureSlot;
	private PingService.Client client;

	@Before
	public void setUp() {
		io = mock(MessageIO.class);
	}
	
	@Test
	public void testServerEmitsPing() throws IOException {
		givenANewServer();
		server.run();
		verify(io).send(any(Message.Ping.class));
	}

	private void givenANewServer() {
		server = PingService.createServer(io).acknowledgmentWindow(TEST_TIME_TO_LIVE_MS);
	}

	@Test
	public void testServerEmitsFailureOnNoPingAcknowledgment() {
		givenANewServer();
		givenAFailureSlotConnection();
		server.run();
		sleep(5);
		verify(onFailureSlot).onEmit();
	}

	private void givenAFailureSlotConnection() {
		onFailureSlot = mock(UnitSlot.class);
		server.onFailure(onFailureSlot);
	}

	private void sleep(int ms) {
		try {
			Thread.sleep(ms);
		} catch (InterruptedException ie) {
			fail(); // Should never happen
		}
	}

	@Test
	public void testClientAcknowledgesPing() throws IOException {
		givenANewClient();
		Ping.instance().accept(client.visitor);
		verify(io).send(any(PingAck.class));
	}

	private void givenANewClient() {
		client = PingService.createClient(io).timeToLive(TEST_TIME_TO_LIVE_MS);
	}

	@Test
	public void testClientEmitsFailureWhenNotReceivingPing() {
		givenANewClient();
		givenAClientFailureSlot();
		whenTheClientRunsForSomeTime(TEST_TIME_TO_LIVE_MS * 100);
		verify(onFailureSlot).onEmit();
	}

	private void givenAClientFailureSlot() {
		onFailureSlot = mock(UnitSlot.class);
		client.onFailure(onFailureSlot);		
	}

	private void whenTheClientRunsForSomeTime(int milliseconds) {
		Thread thread = new Thread(client);
		thread.start();
		sleep(milliseconds);
		thread.interrupt();		
	}
	
	@Test
	public void testClientStop_doesNotSendPing() throws IOException {
		givenANewClient();
		client.stop();
		Message ping = Message.Ping.instance();
		ping.accept(client.visitor);
		verifyNoMoreInteractions(io);
	}
	
	@Test
	public void testClientStop_doesNotReportPingFailure() throws IOException {
		givenANewClient();
		givenAClientFailureSlot();
		client.stop();
		whenTheClientRunsForSomeTime(TEST_TIME_TO_LIVE_MS * 10);
		verifyNoMoreInteractions(onFailureSlot);
	}
}
