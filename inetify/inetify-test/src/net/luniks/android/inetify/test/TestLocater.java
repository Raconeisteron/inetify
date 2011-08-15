/*
 * Copyright 2011 Torsten Römer
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.luniks.android.inetify.test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import net.luniks.android.inetify.Locater;
import android.location.Location;

public class TestLocater implements Locater {
	
	private LocaterLocationListener listener;
	
	private AtomicBoolean running = new AtomicBoolean(false);
	private AtomicBoolean started = new AtomicBoolean(false);
	private List<CallToStart> callsToStart = new ArrayList<CallToStart>();
	
	public void updateLocation(final Location location) {
		if(listener != null) {
			listener.onLocationChanged(location);
		}
	}
	
	public boolean isRunning() {
		return running.get();
	}
	
	public boolean wasStarted() {
		return started.get();
	}
	
	public List<CallToStart> getCallsToStart() {
		return callsToStart;
	}

	public void start(final LocaterLocationListener listener, final long maxAge, final int minAccuracy, boolean useGPS) {
		running.set(true);
		started.set(true);
		callsToStart.add(new CallToStart(maxAge, minAccuracy, useGPS));
		this.listener = listener;
	}

	// TODO Implement when needed
	public void stop() {
		running.set(false);
	}

	// TODO Implement when needed
	public Location getBestLastKnownLocation(final long maxAge) {
		return null;
	}

	// TODO Implement when needed
	public boolean isAccurateEnough(final Location location, final int meters) {
		return true;
	}

	// TODO Implement when needed
	public boolean isProviderEnabled(String provider) {
		return true;
	}
	
	static class CallToStart {
		
		private long maxAge;
		private int minAccuracy;
		private boolean useGPS;
		
		public CallToStart(final long maxAge, final int minAccuracy, final boolean useGPS) {
			this.maxAge = maxAge;
			this.minAccuracy = minAccuracy;
			this.useGPS = useGPS;
		}

		public long getMaxAge() {
			return maxAge;
		}

		public int getMinAccuracy() {
			return minAccuracy;
		}

		public boolean isUseGPS() {
			return useGPS;
		}
		
	}

}
