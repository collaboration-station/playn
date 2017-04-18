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
import static com.google.common.base.Preconditions.checkState;
import static playn.core.PlayN.log;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import react.RFuture;
import react.RPromise;
import react.Slot;
import react.UnitSlot;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Handler;
import android.util.Log;
import edu.bsu.issgame.core.net.MessageIOPump;
import edu.bsu.issgame.core.net.NetworkInterface;
import edu.bsu.issgame.core.net.client.Client;
import edu.bsu.issgame.core.net.server.ClientHandler;
import edu.bsu.issgame.core.net.server.ExpeditionNameGenerator;
import edu.bsu.issgame.core.net.server.Server;

public class BluetoothNetworkInterface extends NetworkInterface {
	public static final int DISCOVERABLE_AS_SERVER_REQUEST_CODE = 4721;
	public static final int DISCOVERABLE_DURATION = 120;
	public static final UUID ISSGAME_UUID = UUID
			.fromString("7ca74c03-1618-468e-ac00-4299ff3280d1");
	private static final String SDP_NAME = "issgame";

	private final BluetoothAdapter adapter;
	private final IssGameActivity activity;
	private ExecutorService executorService = Executors.newCachedThreadPool();
	private BroadcastReceiver receiver;
	private Server server;
	private Client client;
	private MessageIOPump pump;
	private RPromise<Boolean> bluetoothAcceptedPromise;

	public BluetoothNetworkInterface(BluetoothAdapter adapter,
			IssGameActivity activity) {
		this.adapter = checkNotNull(adapter);
		this.activity = checkNotNull(activity);
	}

	@Override
	public RFuture<Boolean> startGameService() {
		checkState(adapter != null, "Adapter is not set.");
		checkState(bluetoothAcceptedPromise == null,
				"An old promise still exists.");
		Log.d(IssGameActivity.TAG, "Start game service request received.");
		bluetoothAcceptedPromise = RPromise.create();
		new Handler(activity.getMainLooper())
				.post(new BluetoothPopupAndStart());
		return bluetoothAcceptedPromise;
	}

	@Override
	public void discoverGameService() {
		checkState(receiver == null,
				"Receiver already created by an earlier call.");
		receiver = new BroadcastReceiver() {
			public void onReceive(Context context, Intent intent) {
				String action = intent.getAction();
				debug("Received intent: " + action);
				if (BluetoothDevice.ACTION_FOUND.equals(action)) {
					BluetoothDevice device = intent
							.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
					debug("Found device " + device.getName());
					if (activity.bluetoothNameManager().isHostingDevice(device)) {
						adapter.cancelDiscovery();
						connectTo(device);
						unregisterReceiver();
					}
				}
			}
		};
		IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
		activity.registerReceiver(receiver, filter);
		adapter.startDiscovery();
	}

	private void unregisterReceiver() {
		debug("Unregistering broadcast receiver");
		activity.unregisterReceiver(receiver);
		receiver = null;
	}

	private void connectTo(final BluetoothDevice device) {
		debug("Creating client connector to " + device.getName());
		ClientConnector clientConnector = new ClientConnector(device,
				executorService, getVersionCode());
		clientConnector.onSuccess(new Slot<Client>() {
			@Override
			public void onEmit(Client theNewClient) {
				checkState(client == null);
				client = theNewClient;
				clientCreated.emit(theNewClient);
			}
		});
		clientConnector.onFailure(new UnitSlot() {
			@Override
			public void onEmit() {
				Log.e(IssGameActivity.TAG,
						"Failed to connect to " + device.getName()
								+ ". Giving up.");
				networkError.emit("Failed to connect.");
				shutdown();
				
			}
		});
		executorService.execute(clientConnector);
	}

	@Override
	public void shutdown() {
		debug("Shutting down network adapter; cancelling bluetooth discovery");
		adapter.cancelDiscovery();
		activity.bluetoothNameManager().ensureAdapterNameDoesNotEndInSuffix();
		if (receiver != null) {
			unregisterReceiver();
		}
		if (server != null) {
			log().debug("Shutting down server.");
			server.shutdown();
			server.broadcastNewsOfMyDemise();
			server = null;
		}
		if (client != null) {
			log().debug("Shutting down client");
			client.cancel();
			client = null;
		}
		if (pump != null) {
			log().debug("Stopping message pump for local client");
			pump.stop();
		}
		shutdownAllTasksAndResetExecutorService();
	}

	private void shutdownAllTasksAndResetExecutorService() {
		executorService.shutdown();
		try {
			boolean terminated = executorService.awaitTermination(5000,
					TimeUnit.MILLISECONDS);
			if (terminated) {
				debug("Executor service shut down.");
			} else {
				debug("Timeout while awaiting executor service shutting down.");
			}
		} catch (InterruptedException e) {
			debug("Interrupted while awaiting termination.");
		}
		executorService = Executors.newCachedThreadPool();
	}

	public void spawnServer() {
		try {
			server = new Server(executorService,
					new BluetoothConnectionAccepter(adapter
							.listenUsingRfcommWithServiceRecord(SDP_NAME,
									ISSGAME_UUID)),
					ExpeditionNameGenerator.instance().generate(), getVersionCode());
			executorService.execute(server);
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
		checkNotNull(server);
		startLocalClientTasks(server);
	}

	private int getVersionCode() {
		try {
			return activity.getPackageManager().getPackageInfo(activity.getPackageName(), 0).versionCode;
		} catch (NameNotFoundException e) {
			throw new IllegalStateException("This should never happen.");
		}
	}

	private void startLocalClientTasks(Server server) {
		try {
			checkState(client == null);
			pump = new MessageIOPump();
			ClientHandler handler = new ClientHandler(pump.a,
					server.expeditionName, getVersionCode());
			server.runClientHandler(handler);
			client = Client.withIO(pump.b).withVersionCode(getVersionCode());
			executorService.execute(client);
			emitClientStarted(client);
		} catch (IOException ioe) {
			throw new RuntimeException(ioe);
		}
	}

	private void debug(String mesg) {
		Log.d(IssGameActivity.TAG, mesg);
	}

	protected void emitClientStarted(Client client) {
		clientCreated.emit(client);
	}

	private final class BluetoothPopupAndStart implements Runnable {

		@Override
		public void run() {
			activity.bluetoothNameManager().appendHostSuffix();
			initiateBluetoothDiscoverability();
		}

		private void initiateBluetoothDiscoverability() {
			Intent discoverableIntent = new Intent(
					BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
			discoverableIntent.putExtra(
					BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION,
					DISCOVERABLE_DURATION);
			activity.startActivityForResult(discoverableIntent,
					DISCOVERABLE_AS_SERVER_REQUEST_CODE);
		}
	}

	public void handleDiscoverableResult(int resultCode) {
		if (resultCode == Activity.RESULT_CANCELED) {
			bluetoothAcceptedPromise.succeed(false);
		} else {
			bluetoothAcceptedPromise.succeed(true);
			spawnServer();
		}
		bluetoothAcceptedPromise = null;
	};

}
