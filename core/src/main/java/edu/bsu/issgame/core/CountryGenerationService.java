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
package edu.bsu.issgame.core;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.google.common.collect.Lists;

public class CountryGenerationService {

	private static final CountryGenerationService INSTANCE = new CountryGenerationService();

	public static CountryGenerationService instance() {
		return INSTANCE;
	}

	private final List<Country> countries;
	private int index = 0;

	private CountryGenerationService() {
		countries = createListOfCountriesInAListImplementationThatSupportsRemoval();
		Collections.shuffle(countries);
	}

	private List<Country> createListOfCountriesInAListImplementationThatSupportsRemoval() {
		return Lists.newArrayList(Arrays.asList(Country.values()));
	}

	public Country next() {
		return countries.get((index++) % countries.size());
	}
}
