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
package edu.bsu.issgame.java;

import static playn.core.PlayN.log;
import static playn.core.PlayN.platform;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;

import playn.core.PlayN;
import playn.java.JavaGraphics;
import playn.java.JavaPlatform;
import pythagoras.i.Dimension;
import react.RFuture;
import react.RPromise;
import react.Slot;
import react.UnitSlot;
import edu.bsu.issgame.core.FontInfo;
import edu.bsu.issgame.core.IssGame;
import edu.bsu.issgame.core.net.MessageIO;
import edu.bsu.issgame.core.net.NetworkInterface;
import edu.bsu.issgame.core.net.client.Client;
import edu.bsu.issgame.core.net.server.Server;
import edu.bsu.issgame.core.net.server.SocketConnectionAccepter;

public final class IssGameJava {

	private static final float GALAXY_TAB_3_ASPECT_RATIO = 16f / 9f;
	private static final Dimension APPROXIMATE_GALAXY_TAB_3_PHYSICAL_SIZE = new Dimension(
			750, (int) (1 / GALAXY_TAB_3_ASPECT_RATIO * 750));

	private static final String SIZE_OPTION = "size";
	private static final String SIZE_OPTION_DEFAULT = fromDimension(APPROXIMATE_GALAXY_TAB_3_PHYSICAL_SIZE);
	private static final String VERSIONCODE_OPTION = "versionCode";
	private static final String VERSIONCODE_OPTION_DEFAULT = "0";
	private static JavaPlatform.Config config = new JavaPlatform.Config();

	private static JavaNetworkInterface javaNet;
	
	private static String fromDimension(Dimension dimension) {
		return dimension.width + "x" + dimension.height;
	}

	public static void main(String[] args) {
		javaNet = new JavaNetworkInterface();
		parseCommandLineOptons(args);
		JavaPlatform.register(config);
		registerFonts();
		PlayN.run(new IssGame(javaNet));
	}

	private static void parseCommandLineOptons(String[] args) {
		Options options = createCommandLineOptions();
		CommandLineParser parser = new PosixParser();
		try {
			CommandLine commandLine = parser.parse(options, args);
			processSizeOption(commandLine);
			processVersionCodeOption(commandLine);
		} catch (ParseException e) {
			e.printStackTrace();
			return;
		} catch (NumberFormatException e) {
			log().error("Invalid size option, use WxH format (e.g. 640x480)");
			return;
		}
	}

	private static Options createCommandLineOptions() {
		// Apache CLI forces the use of a static call to create() here.
		@SuppressWarnings("static-access")
		Option sizeOption = OptionBuilder.withLongOpt(SIZE_OPTION)//
				.withDescription("Specify the size of the display in pixels")//
				.hasArg()//
				.withArgName("res")//
				.create();
		@SuppressWarnings("static-access")
		Option versionOption = OptionBuilder.withLongOpt(VERSIONCODE_OPTION)//
				.withDescription("Specify android manifest version code number.")//
				.hasArg()//
				.withArgName("vCode")//
				.create();
		return new Options().addOption(sizeOption).addOption(versionOption);
	}

	private static void processSizeOption(CommandLine commandLine) {
		String sizeOptionValue = commandLine.getOptionValue(SIZE_OPTION,
				SIZE_OPTION_DEFAULT);
		int separatorIndex = sizeOptionValue.indexOf('x');
		config.width = Integer.parseInt(sizeOptionValue.substring(0,
				separatorIndex));
		config.height = Integer.parseInt(sizeOptionValue
				.substring(separatorIndex + 1));
	}
	
	private static void processVersionCodeOption(CommandLine commandLine) {
		String versionCodeOptionValue = commandLine.getOptionValue(VERSIONCODE_OPTION, VERSIONCODE_OPTION_DEFAULT);
		JavaNetworkInterface.versionCode = Integer.parseInt(versionCodeOptionValue);
	}

	private static void registerFonts() {
		JavaGraphics g = (JavaGraphics) platform().graphics();
		for (FontInfo info : FontInfo.values()) {
			g.registerFont(info.name, info.path);
		}
	}
}

final class JavaNetworkInterface extends NetworkInterface {

	protected static int versionCode = -1; 
	private final ExecutorService executorService = Executors
			.newCachedThreadPool();

	private Server server;

	@Override
	public RFuture<Boolean> startGameService() {
		final RPromise<Boolean> promise = RPromise.create();
		try {
			server = new Server(executorService, new SocketConnectionAccepter(
					NetworkInterface.PORT), "Expedition Java", versionCode);
		} catch (IOException e) {
			e.printStackTrace();
		}
		server.onServerStart().connect(new UnitSlot() {
			@Override
			public void onEmit() {
				promise.succeed(true);
				startClientAsync();
			}

			private void startClientAsync() {
				executorService.execute(new Runnable() {
					@Override
					public void run() {
						try {
							log().debug("Starting up the local client.");
							InetAddress localhost = InetAddress.getLocalHost();
							Socket socket = new Socket(localhost,
									NetworkInterface.PORT);
							MessageIO io = MessageIO.SocketAdapter
									.inClientMode(socket);
							Client client = Client.withIO(io).withVersionCode(versionCode);
							clientCreated.emit(client);
							executorService.execute(client);
						} catch (IOException e) {
							throw new RuntimeException(e);
						}
					}
				});
			}
		});
		executorService.execute(server);
		return promise;
	}

	@Override
	public void discoverGameService() {
		try {
			final ClientConnector clientConnector = ClientConnector
					.connectingTo(InetAddress.getLocalHost());
			clientConnector.onClientStarted().connect(new Slot<Client>() {
				@Override
				public void onEmit(Client newClient) {
					clientCreated.emit(newClient);
				}
			});
			executorService.execute(clientConnector);
		} catch (UnknownHostException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void shutdown() {
		if (server != null) {
			server.shutdown();
		}
		executorService.shutdown();
	}

}