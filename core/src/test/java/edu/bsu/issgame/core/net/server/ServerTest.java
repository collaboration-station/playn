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

import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.junit.After;
import org.junit.Before;

import react.UnitSlot;

import com.google.common.collect.Lists;

import edu.bsu.issgame.core.net.HeadlessTestCase;
import edu.bsu.issgame.core.net.MessageIO;
import edu.bsu.issgame.core.net.NetworkInterface;

public final class ServerTest extends HeadlessTestCase {

	private static final int TEST_PORT = NetworkInterface.PORT;
	private static final int TEST_VERSIONCODE = 0;
	private ExecutorService executorService;
	private Server server;
	private List<MessageIO> ioList = Lists.newArrayList();
	private List<UUID> uuidList = Lists.newArrayList();

	private CountDownLatch shutdownLatch;

	@Before
	public void setUp() throws IOException {
		final CountDownLatch startupLatch = new CountDownLatch(1);
		executorService = Executors.newCachedThreadPool();
		server = new Server(executorService, new SocketConnectionAccepter(
				TEST_PORT), "DummyExpeditionName", TEST_VERSIONCODE);
		server.onServerStart().connect(new UnitSlot() {
			@Override
			public void onEmit() {
				startupLatch.countDown();
			}
		});
		executorService.execute(server);
		try {
			startupLatch.await();
		} catch (InterruptedException e) {
			throw new RuntimeException("Nobody should interrupt me.");
		}

		shutdownLatch = new CountDownLatch(1);
		server.onServerStop().connect(new UnitSlot() {
			@Override
			public void onEmit() {
				shutdownLatch.countDown();
			}
		});
	}

	@After
	public void tearDown() {
		if (!server.wasShutdownRequested()) {
			server.shutdown();
			ioList.clear();
			uuidList.clear();
		}
		executorService.shutdown();
		try {
			shutdownLatch.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}