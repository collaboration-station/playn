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

import edu.bsu.issgame.core.Score;

public class ScoreScaler {
	private final int numberOfPlayers;
	private boolean scienceEmpasis;

	public ScoreScaler(int numberOfPlayers) {
		this.numberOfPlayers = numberOfPlayers;
	}
	
	public Score scale(int pointsPerPlayer){
		int sciencePoints = pointsPerPlayer*(numberOfPlayers/2);
		int maintenancePoints = pointsPerPlayer*(numberOfPlayers/2);
		if(isOddNumberOfPlayers()){
			if(scienceEmpasis){
				sciencePoints+=pointsPerPlayer;
			} else {
				maintenancePoints+=pointsPerPlayer;
			}
		}
		return Score.maintenance(maintenancePoints).science(sciencePoints);
	}

	private boolean isOddNumberOfPlayers() {
		return numberOfPlayers%2 == 1;
	}

	public ScoreScaler withScienceEmphasis() {
		scienceEmpasis = true;
		return this;
	}

	public ScoreScaler withMaintenanceEmphasis() {
		scienceEmpasis = false;
		return this;
	}

}
