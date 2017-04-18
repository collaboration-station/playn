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

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.io.Serializable;
import java.util.UUID;

import com.google.common.base.MoreObjects;
import com.google.common.collect.ImmutableList;

import edu.bsu.issgame.core.PlayerMinigameMap;
import edu.bsu.issgame.core.Score;
import edu.bsu.issgame.core.Scoreboard;
import edu.bsu.issgame.core.mission.Exposition;
import edu.bsu.issgame.core.mission.Scenario;

public interface Message extends Serializable {

	public enum LogVerbosityLevel {
		TRACE, DEBUG
	};

	void accept(Visitor visitor, Object... args);

	LogVerbosityLevel logVerbosityLevel();

	public interface Visitor {
		void visit(Hello hello, Object... args);

		void visit(Introduce introduce, Object... args);

		void visit(VersionMismatch versionMismatch, Object... args);

		void visit(Welcome welcome, Object... args);

		void visit(StartMission startGame, Object... args);

		void visit(RequestStartGame request, Object... args);

		void visit(RequestStartScenario request, Object... args);

		void visit(StartScenario startScenario, Object... args);

		void visit(ReportScore reportScore, Object... args);

		void visit(ScenarioFinishedWithSuccess scenarioFinished, Object... args);

		void visit(ServerGoingDown serverGoingDown, Object... args);

		void visit(ScenarioFinishedSuccesfullyEndingMission finished,
				Object... args);

		void visit(ScenarioFinishedInFailure failure, Object... args);

		void visit(AdvanceToScenarioSetup advanceToScenarioSetup,
				Object... args);

		void visit(RequestAdvanceToScenarioSetup requestAdvanceToScenarioSetup,
				Object... args);

		void visit(Ping ping, Object... args);

		void visit(PingAck pingAck, Object... args);

		void visit(RequestStartMission requestStartMission, Object... args);

		public static abstract class Adapter implements Visitor {
			@Override
			public void visit(Hello hello, Object... args) {
			}

			@Override
			public void visit(Introduce introduce, Object... args) {
			}

			@Override
			public void visit(VersionMismatch versionMismatch, Object... args) {
			}

			@Override
			public void visit(Welcome welcome, Object... args) {
			}

			@Override
			public void visit(StartMission startGame, Object... args) {
			}

			@Override
			public void visit(RequestStartGame request, Object... args) {
			}

			@Override
			public void visit(
					RequestAdvanceToScenarioSetup requestAdvanceToScenarioSetup,
					Object... args) {
			}

			@Override
			public void visit(AdvanceToScenarioSetup advanceToScenarioSetup,
					Object... args) {
			}

			@Override
			public void visit(RequestStartScenario request, Object... args) {
			}

			@Override
			public void visit(StartScenario startScenario, Object... args) {
			}

			@Override
			public void visit(ReportScore reportScore, Object... args) {
			}

			@Override
			public void visit(ScenarioFinishedWithSuccess scenarioFinished,
					Object... args) {
			}

			@Override
			public void visit(ServerGoingDown serverGoingDown, Object... args) {
			}

			@Override
			public void visit(
					ScenarioFinishedSuccesfullyEndingMission finished,
					Object... args) {
			}

			@Override
			public void visit(ScenarioFinishedInFailure failure, Object... args) {
			}

			@Override
			public void visit(Ping ping, Object... args) {
			}

			@Override
			public void visit(PingAck pingAck, Object... args) {
			}

			@Override
			public void visit(RequestStartMission requestStartMission,
					Object... args) {
			}
		}
	}

	public static abstract class AbstractMessage implements Message {
		private static final long serialVersionUID = 6344952606000815190L;

		@Override
		public LogVerbosityLevel logVerbosityLevel() {
			return LogVerbosityLevel.DEBUG;
		}
	}

	public static final class Hello extends AbstractMessage {
		private static final long serialVersionUID = 5563820521241405842L;

		public final String expeditionName;
		public final int verisonCode;

		public Hello(String expeditionName, int versionCode) {
			this.expeditionName = checkNotNull(expeditionName);
			this.verisonCode = versionCode;
		}

		@Override
		public void accept(Visitor visitor, Object... args) {
			visitor.visit(this, args);
		}

		@Override
		public String toString() {
			return MoreObjects.toStringHelper(this)//
					.add("expeditionName", expeditionName)//
					.toString();
		}
	}

	public static final class Introduce extends AbstractMessage {
		private static final long serialVersionUID = -3252581050101527692L;

		public final UUID uuid;

		public Introduce(UUID uuid) {
			this.uuid = checkNotNull(uuid);
		}

		@Override
		public void accept(Visitor visitor, Object... args) {
			visitor.visit(this, args);
		}

		@Override
		public String toString() {
			return MoreObjects.toStringHelper(this)//
					.add("uuid", uuid)//
					.toString();
		}
	}

	public static final class VersionMismatch extends AbstractMessage {
		private static final long serialVersionUID = -511714198979169124L;

		@Override
		public void accept(Visitor visitor, Object... args) {
			visitor.visit(this, args);
		}

	}

	public static final class Welcome extends AbstractMessage {
		private static final long serialVersionUID = 4629788549787392045L;

		public final ImmutableList<ClientId> clients;

		public Welcome(Iterable<ClientId> clients) {
			this.clients = ImmutableList.copyOf(clients);
		}

		@Override
		public void accept(Visitor visitor, Object... args) {
			visitor.visit(this, args);
		}

		@Override
		public String toString() {
			return MoreObjects.toStringHelper(this)//
					.add("clients", clients)//
					.toString();
		}
	}

	public static final class StartMission extends AbstractMessage {
		private static final long serialVersionUID = -1513919655753316333L;

		public final Exposition introduction;

		public StartMission(Exposition introduction) {
			this.introduction = checkNotNull(introduction);
		}

		@Override
		public void accept(Visitor visitor, Object... args) {
			visitor.visit(this, args);
		}
	}

	public static final class RequestStartGame extends AbstractMessage {
		private static final long serialVersionUID = -6423844681458844975L;

		@Override
		public void accept(Visitor visitor, Object... args) {
			visitor.visit(this, args);
		}

	}

	public static final class RequestStartScenario extends AbstractMessage {
		private static final long serialVersionUID = 2562400251080392883L;
		public final PlayerMinigameMap map;

		public RequestStartScenario(PlayerMinigameMap map) {
			this.map = checkNotNull(map);
		}

		@Override
		public void accept(Visitor visitor, Object... args) {
			visitor.visit(this, args);
		}
	}

	public static final class StartScenario extends AbstractMessage {
		private static final long serialVersionUID = 5256854726561931878L;
		public final PlayerMinigameMap map;

		public StartScenario(RequestStartScenario request) {
			checkNotNull(request);
			checkArgument(request.map.size() > 0);
			this.map = request.map;
		}

		@Override
		public void accept(Visitor visitor, Object... args) {
			visitor.visit(this, args);
		}
	}

	public static final class ReportScore extends AbstractMessage {
		private static final long serialVersionUID = -2654391454852309532L;

		public final Score score;

		public ReportScore(Score score) {
			this.score = score;
		}

		@Override
		public void accept(Visitor visitor, Object... args) {
			visitor.visit(this, args);
		}

		@Override
		public String toString() {
			return MoreObjects.toStringHelper(this)//
					.add("score", score)//
					.toString();
		}
	}

	public static final class ScenarioFinishedWithSuccess extends
			AbstractMessage {
		private static final long serialVersionUID = 3084487025554519557L;

		public final Scoreboard scoreboard;
		public final Scenario nextScenario;

		public ScenarioFinishedWithSuccess(Scoreboard scoreboard,
				Scenario nextScenarioOrNull) {
			this.scoreboard = scoreboard;
			this.nextScenario = nextScenarioOrNull;
		}

		@Override
		public void accept(Visitor visitor, Object... args) {
			visitor.visit(this, args);
		}

		@Override
		public String toString() {
			return MoreObjects.toStringHelper(this)//
					.add("scoreboard", scoreboard)//
					.toString();
		}
	}

	public static final class ServerGoingDown extends AbstractMessage {
		private static final long serialVersionUID = -3663667177449713032L;

		@Override
		public void accept(Visitor visitor, Object... args) {
			visitor.visit(this, args);
		}
	}

	public static class ScenarioFinishedSuccesfullyEndingMission extends
			AbstractMessage {
		private static final long serialVersionUID = 4813639313371168110L;

		public final Scoreboard scoreboard;
		public final Exposition conclusion;

		public ScenarioFinishedSuccesfullyEndingMission(Scoreboard scoreboard,
				Exposition conclusion) {
			this.scoreboard = checkNotNull(scoreboard);
			this.conclusion = checkNotNull(conclusion);
		}

		@Override
		public void accept(Visitor visitor, Object... args) {
			visitor.visit(this, args);
		}

	}

	public static class ScenarioFinishedInFailure extends AbstractMessage {
		private static final long serialVersionUID = -2629073572021476562L;

		public final Scoreboard scoreboard;

		public ScenarioFinishedInFailure(Scoreboard scoreboard) {
			this.scoreboard = scoreboard;
		}

		@Override
		public void accept(Visitor visitor, Object... args) {
			visitor.visit(this, args);
		}

	}

	public static class AdvanceToScenarioSetup extends AbstractMessage {
		private static final long serialVersionUID = 604743738708138625L;

		public final Scenario scenario;

		public AdvanceToScenarioSetup(Scenario scenario) {
			this.scenario = checkNotNull(scenario);
		}

		@Override
		public void accept(Visitor visitor, Object... args) {
			visitor.visit(this, args);
		}
	}

	public static class RequestAdvanceToScenarioSetup extends AbstractMessage {
		private static final long serialVersionUID = -1498997600871505715L;

		@Override
		public void accept(Visitor visitor, Object... args) {
			visitor.visit(this, args);
		}
	}

	public static class Ping extends AbstractMessage {
		private static final long serialVersionUID = 1597341118791556045L;

		private static final Ping INSTANCE = new Ping();

		public static Message instance() {
			return INSTANCE;
		}

		@Override
		public void accept(Visitor visitor, Object... args) {
			visitor.visit(this, args);
		}

		@Override
		public LogVerbosityLevel logVerbosityLevel() {
			return LogVerbosityLevel.TRACE;
		}
	}

	public static class PingAck extends AbstractMessage {
		private static final long serialVersionUID = -6410482104397228003L;

		private static final PingAck INSTANCE = new PingAck();

		public static PingAck instance() {
			return INSTANCE;
		}

		@Override
		public void accept(Visitor visitor, Object... args) {
			visitor.visit(this, args);
		}

		@Override
		public LogVerbosityLevel logVerbosityLevel() {
			return LogVerbosityLevel.TRACE;
		}

	}

	public static class RequestStartMission extends AbstractMessage {
		private static final long serialVersionUID = -3813500642388555871L;
		private static final RequestStartMission INSTANCE = new RequestStartMission();

		public static RequestStartMission instance() {
			return INSTANCE;
		}

		@Override
		public void accept(Visitor visitor, Object... args) {
			visitor.visit(this, args);
		}

	}

}
