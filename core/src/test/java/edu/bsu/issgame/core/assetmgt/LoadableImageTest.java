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
package edu.bsu.issgame.core.assetmgt;

import static org.junit.Assert.fail;

import java.util.concurrent.CountDownLatch;

import org.junit.Test;

import edu.bsu.issgame.core.net.HeadlessTestCase;
import playn.core.AssetWatcher;
import react.RPromise;
import react.Slot;
import react.Try;

public class LoadableImageTest extends HeadlessTestCase {

	@Test
	public void testAllIndexedImagesAreLoadable() {
		final CountDownLatch latch = new CountDownLatch(1);
		final RPromise<Void> promise = RPromise.create();
		AssetWatcher watcher = new AssetWatcher(new AssetWatcher.Listener() {
			@Override
			public void error(Throwable arg0) {
				promise.fail(arg0);
			}

			@Override
			public void done() {
				promise.succeed(null);
			}
		});
		for (LoadableImage ii : LoadableImage.values()) {
			watcher.add(ii.loadSync());
		}
		promise.onComplete(new Slot<Try<Void>>() {
			@Override
			public void onEmit(Try<Void> event) {
				latch.countDown();
			}
		});
		promise.onFailure(new Slot<Throwable>() {
			@Override
			public void onEmit(Throwable event) {
				fail(event.getMessage());
			}
		});
		watcher.start();
	}
}
