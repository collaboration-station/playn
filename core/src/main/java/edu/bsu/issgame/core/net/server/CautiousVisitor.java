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

import static playn.core.PlayN.log;
import edu.bsu.issgame.core.net.Message;
import edu.bsu.issgame.core.net.Message.AdvanceToScenarioSetup;
import edu.bsu.issgame.core.net.Message.Hello;
import edu.bsu.issgame.core.net.Message.Introduce;
import edu.bsu.issgame.core.net.Message.Ping;
import edu.bsu.issgame.core.net.Message.PingAck;
import edu.bsu.issgame.core.net.Message.ReportScore;
import edu.bsu.issgame.core.net.Message.RequestAdvanceToScenarioSetup;
import edu.bsu.issgame.core.net.Message.RequestStartGame;
import edu.bsu.issgame.core.net.Message.RequestStartMission;
import edu.bsu.issgame.core.net.Message.RequestStartScenario;
import edu.bsu.issgame.core.net.Message.ScenarioFinishedInFailure;
import edu.bsu.issgame.core.net.Message.ScenarioFinishedSuccesfullyEndingMission;
import edu.bsu.issgame.core.net.Message.ScenarioFinishedWithSuccess;
import edu.bsu.issgame.core.net.Message.ServerGoingDown;
import edu.bsu.issgame.core.net.Message.StartMission;
import edu.bsu.issgame.core.net.Message.StartScenario;
import edu.bsu.issgame.core.net.Message.VersionMismatch;
import edu.bsu.issgame.core.net.Message.Welcome;

public class CautiousVisitor implements Message.Visitor {

	@Override
	public void visit(Hello hello, Object... args) {
		warn(hello);
	}

	@Override
	public void visit(Introduce introduce, Object... args) {
		warn(introduce);
	}

	@Override
	public void visit(VersionMismatch versionMismatch, Object... args) {
		warn(versionMismatch);
	}
	
	@Override
	public void visit(Welcome welcome, Object... args) {
		warn(welcome);
	}

	@Override
	public void visit(StartMission startGame, Object... args) {
		warn(startGame);
	}

	@Override
	public void visit(RequestStartGame request, Object... args) {
		warn(request);
	}

	@Override
	public void visit(RequestStartScenario request, Object... args) {
		warn(request);
	}

	@Override
	public void visit(StartScenario startScenario, Object... args) {
		warn(startScenario);
	}

	@Override
	public void visit(ReportScore reportScore, Object... args) {
		warn(reportScore);
	}

	@Override
	public void visit(ScenarioFinishedWithSuccess scenarioFinished,
			Object... args) {
		warn(scenarioFinished);
	}

	@Override
	public void visit(ServerGoingDown serverGoingDown, Object... args) {
		warn(serverGoingDown);
	}

	@Override
	public void visit(ScenarioFinishedSuccesfullyEndingMission finished,
			Object... args) {
		warn(finished);
	}

	@Override
	public void visit(ScenarioFinishedInFailure failure, Object... args) {
		warn(failure);
	}

	@Override
	public void visit(AdvanceToScenarioSetup advanceToScenarioSetup,
			Object... args) {
		warn(advanceToScenarioSetup);
	}

	@Override
	public void visit(
			RequestAdvanceToScenarioSetup requestAdvanceToScenarioSetup,
			Object... args) {
		warn(requestAdvanceToScenarioSetup);
	}

	@Override
	public void visit(Ping ping, Object... args) {
		warn(ping);
	}

	@Override
	public void visit(PingAck pingAck, Object... args) {
	}

	@Override
	public void visit(RequestStartMission requestStartMission, Object... args) {
		warn(requestStartMission);
	}

	private static void warn(Message message) {
		log().warn("Unexpected message: " + message);
	}
}
