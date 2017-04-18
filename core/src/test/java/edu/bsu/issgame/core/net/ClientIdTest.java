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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.UUID;

import org.junit.Test;

import edu.bsu.issgame.core.Country;

public class ClientIdTest {

	private ClientId kirk = new ClientId(Country.USA, UUID.randomUUID());
	private ClientId kirk2 = new ClientId(Country.USA, UUID.randomUUID());

	@Test
	public void testEquals_identityEquality() {
		assertTrue(kirk.equals(kirk));
	}

	@Test
	public void testCreate_differentUUID() {
		assertFalse(kirk.uuid.equals(kirk2.uuid));
	}

	@Test
	public void testEquals_sameNameDifferentUUID_false() {
		assertFalse(kirk.equals(kirk2));
	}
	
	@Test
	public void testClone_cloneIsEqualToSource() {
		ClientId clone = null;
		try {
			clone = (ClientId)kirk.clone();
		} catch (CloneNotSupportedException e) {
			fail();
		}
		assertTrue(clone.equals(kirk));
	}
}
