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
package net.luniks.android.impl;

import java.util.List;

import net.luniks.android.interfaces.ILocationManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;

/**
 * Implementation of ILocationManager.
 * @see android.location.LocationManager
 * 
 * @author torsten.roemer@luniks.net
 */
public class LocationManagerImpl implements ILocationManager {
	
	private final LocationManager locationManager;
	
	public LocationManagerImpl(final LocationManager locationManager) {
		this.locationManager = locationManager;
	}

	public List<String> getAllProviders() {
		return locationManager.getAllProviders();
	}
	
	public boolean isProviderEnabled(final String provider) {
		return locationManager.isProviderEnabled(provider);
	}

	public Location getLastKnownLocation(final String provider) {
		return locationManager.getLastKnownLocation(provider);
	}

	public void requestLocationUpdates(final String provider, final long minTime,
			final float minDistance, final LocationListener listener) {
		locationManager.requestLocationUpdates(provider, minTime, minDistance, listener);
	}

	public void removeUpdates(final LocationListener listener) {
		locationManager.removeUpdates(listener);
	}

}
