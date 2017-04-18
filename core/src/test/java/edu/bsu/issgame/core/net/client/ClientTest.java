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
package edu.bsu.issgame.core.net.client;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.io.IOException;
import java.util.UUID;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import react.Slot;
import react.UnitSlot;

import com.google.common.collect.ImmutableList;

import edu.bsu.issgame.core.Country;
import edu.bsu.issgame.core.PlayerMinigameMap;
import edu.bsu.issgame.core.mission.Exposition;
import edu.bsu.issgame.core.net.ClientId;
import edu.bsu.issgame.core.net.HeadlessTestCase;
import edu.bsu.issgame.core.net.Message;
import edu.bsu.issgame.core.net.Message.Introduce;
import edu.bsu.issgame.core.net.Message.RequestStartGame;
import edu.bsu.issgame.core.net.Message.VersionMismatch;
import edu.bsu.issgame.core.net.TestClientIO;

public class ClientTest extends HeadlessTestCase {

	private static final int MATCHED_VERSIONCODE = -1;
	private static final int MISMATCH_VERSIONCODE = 1;
	private TestClientIO clientIO = new TestClientIO();
	private ClientId clientId = new ClientId(Country.USA, UUID.randomUUID());
	private Client client = Client.withIO(clientIO).withVersionCode(MATCHED_VERSIONCODE);
	@Mock
	private Slot<Iterable<ClientId>> clientListSlot;
	private boolean versionMismatch;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
		versionMismatch = false;
	}

	@Test
	public void testReceive_hello_sendsIntroduce() throws IOException {
		whenReceivingHello();
		thenTheClientSendsA(Introduce.class);
	}

	private void whenReceivingHello() throws IOException {
		if (versionMismatch) {
			client.receive(new Message.Hello("DummyExpeditionName", MISMATCH_VERSIONCODE));
		} else {
			client.receive(new Message.Hello("DummyExpeditionName", MATCHED_VERSIONCODE));
		}
	}

	private <T extends Message> void thenTheClientSendsA(Class<T> clazz)
			throws IOException {
		ArgumentCaptor<Message> argumentCaptor = ArgumentCaptor
				.forClass(Message.class);
		verify(clientIO.out).writeObject(argumentCaptor.capture());
		assertEquals(clazz, argumentCaptor.getValue().getClass());
	}

	@Test
	public void testReceive_HelloWithVersionMismatch_sendsVersionMismatch()
			throws IOException {
		givenAVersionMismatch();
		whenReceivingHello();
		thenTheClientSendsA(VersionMismatch.class);
	}

	private void givenAVersionMismatch() {
		versionMismatch = true;
	}

	@Test
	public void testReceive_welcome_emitsSignal() throws IOException {
		client.onClientsUpdated().connect(clientListSlot);
		whenReceivingWelcome();
		thenClientEmitsClientConnectionSignal();
	}

	private void whenReceivingWelcome() throws IOException {
		client.receive(new Message.Welcome(ImmutableList.of(clientId)));
	}

	// Need to suppress warnings here because of the nested generics of the
	// matcher.
	@SuppressWarnings("unchecked")
	private void thenClientEmitsClientConnectionSignal() {
		verify(clientListSlot).onEmit(Matchers.any(Iterable.class));
	}

	@Test
	public void testRequestStartGame_sendsRequest() throws IOException {
		whenTheClientRequestsTheStartOfAGame();
		thenTheClientSendsA(RequestStartGame.class);
	}

	private void whenTheClientRequestsTheStartOfAGame() throws IOException {
		client.requestStartGame();
	}

	@Test
	public void testReceive_startGame_emitsSignal() throws IOException {
		UnitSlot slot = mock(UnitSlot.class);
		client.onGameStarted().connect(slot);
		client.receive(new Message.StartMission(mock(Exposition.class)));
		verify(slot).onEmit();
	}

	@Test
	public void testReceive_serverGoingDown_emitsSignal() throws IOException {
		UnitSlot slot = mock(UnitSlot.class);
		client.onServerDown().connect(slot);
		client.receive(new Message.ServerGoingDown());
		verify(slot).onEmit();
	}

	@Test(expected = IllegalArgumentException.class)
	public void testRequestStartScenario_withoutMappingClientsToGames_throwsException()
			throws IOException {
		givenAClientInAOnePlayerGame();
		client.requestStartScenario(new PlayerMinigameMap());
	}

	private void givenAClientInAOnePlayerGame() throws IOException {
		Message.Welcome welcomeSelf = new Message.Welcome(
				ImmutableList.of(clientId));
		client.receive(welcomeSelf);
		Message.StartMission startMessage = new Message.StartMission(
				mock(Exposition.class));
		client.receive(startMessage);
	}
}
