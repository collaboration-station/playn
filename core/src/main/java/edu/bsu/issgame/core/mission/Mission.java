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
package edu.bsu.issgame.core.mission;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

import edu.bsu.issgame.core.Score;

public class Mission {

	public interface Result {
		public interface Visitor {
			void visit(Failure failure);

			void visit(MissionSuccess success);

			void visit(ScenarioSuccess success);
		}

		void accept(Visitor visitor);

		public static final class Failure implements Result {
			@Override
			public void accept(Visitor visitor) {
				visitor.visit(this);
			}
		}

		public static final class MissionSuccess implements Result {
			@Override
			public void accept(Visitor visitor) {
				visitor.visit(this);
			}
		}

		public static final class ScenarioSuccess implements Result {
			@Override
			public void accept(Visitor visitor) {
				visitor.visit(this);
			}
		}

	}

	public static Builder createWithIntroduction(Exposition introduction) {
		return new Builder(introduction);
	}

	public static final class Builder {
		private final Exposition introduction;
		private final List<Scenario> scenarios = Lists.newArrayList();
		private Exposition conclusion;

		private Builder(Exposition intro) {
			this.introduction = checkNotNull(intro);
		}

		public Builder andScenario(Scenario scenario) {
			scenarios.add(checkNotNull(scenario));
			return this;
		}

		public Builder andScenarios(Scenario... scenarios) {
			for (Scenario scenario : scenarios) {
				andScenario(scenario);
			}
			return this;
		}

		public Mission andConclusion(Exposition conclusion) {
			this.conclusion = checkNotNull(conclusion);
			return new Mission(this);
		}
	}

	private final ImmutableList<Scenario> scenarios;
	private int scenarioIndex = 0;
	private boolean isComplete = false;
	public final Exposition introduction;
	public final Exposition conclusion;

	private Mission(Builder importer) {
		this.introduction = importer.introduction;
		this.scenarios = ImmutableList.copyOf(importer.scenarios);
		this.conclusion = importer.conclusion;
	}

	public Scenario scenario() {
		if (isComplete) {
			throw new IllegalStateException();
		} else {
			return scenarios.get(scenarioIndex);
		}
	}

	public Result evaluateCurrentScenario(Score score) {
		if (score.meetsOrExceeds(scenario().goal)) {
			scenarioIndex++;
			if (scenarioIndex >= scenarios.size()) {
				isComplete = true;
				return new Result.MissionSuccess();
			} else {
				return new Result.ScenarioSuccess();
			}
		} else {
			return new Result.Failure();
		}
	}
}
