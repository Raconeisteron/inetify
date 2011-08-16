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
package net.luniks.android.test.mock;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.luniks.android.interfaces.ILocationManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;

public class LocationManagerMock implements ILocationManager {
	
	private List<String> allProviders = new ArrayList<String>();
	private Map<String, Location> lastKnownLocations = new HashMap<String, Location>();
	private Map<String, LocationListener> listeners = new HashMap<String, LocationListener>();
	
	public LocationManagerMock(final boolean realProviders) {
		if(realProviders) {
			this.addProvider(LocationManager.GPS_PROVIDER);
			this.addProvider(LocationManager.NETWORK_PROVIDER);
			this.addProvider("passive");
		} else {
			this.addProvider("A");
			this.addProvider("B");
			this.addProvider("C");
			this.addProvider("D");
			this.addProvider("E");
			this.addProvider("F");			
		}
	}
	
	public void addProvider(final String provider) {
		allProviders.add(provider);
	}
	
	public void addLastKnownLocation(final String provider, final Location location) {
		lastKnownLocations.put(provider, location);
	}
	
	public void updateLocation(final Location location) {
		LocationListener listener = listeners.get(location.getProvider());
		if(listener != null) {
			listeners.get(location.getProvider()).onLocationChanged(location);
		}
	}
	
	public boolean areListenersRegistered() {
		return listeners.size() > 0;
	}

	public List<String> getAllProviders() {;
		Collections.shuffle(allProviders);
		return allProviders;
	}
	
	// TODO Implement
	public boolean isProviderEnabled(final String provider) {
		return true;
	}

	public Location getLastKnownLocation(final String provider) {
		return lastKnownLocations.get(provider);
	}

	public void requestLocationUpdates(final String provider, final long minTime,
			final float minDistance, final LocationListener listener) {
		listeners.put(provider, listener);
	}

	public void removeUpdates(final LocationListener listener) {
		listeners.clear();
	}

}
