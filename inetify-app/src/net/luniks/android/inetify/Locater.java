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
package net.luniks.android.inetify;

import android.location.Location;

/**
 * Interface for a class providing location updates using a LocaterLocationListener. 
 * @author torsten.roemer@luniks.net
 */
public interface Locater {
	
	/** Provider used for locations coming from the database */
	public static final String PROVIDER_DATABASE = "database";

	/**
	 * First checks for last known locations and if there was none that satisfied the given criteria,
	 * starts listening for location updates using the given listener, using GPS or not. Stops itself
	 * when it found a location that satisfied the given criteria.
	 * @param listener
	 * @param maxAge
	 * @param minAccuracy
	 * @param useGPS
	 */
	void start(LocaterLocationListener listener, long maxAge, int minAccuracy, boolean useGPS);
	
	/**
	 * Stops listening for location updates.
	 */
	void stop();
	
	/**
	 * Returns the best last known location that is not older than maxAge.
	 * @param maxAge
	 * @return Location
	 */
	Location getBestLastKnownLocation(long maxAge);

	/**
	 * Returns true if the given location has at least the given accuracy, false otherwise.
	 * @param location Location
	 * @param accuracy in meters
	 * @return boolean true if the location has at least the given accuracy
	 */
	boolean isAccurateEnough(Location location, int accuracy);
	
	/**
	 * Returns true if the given provider is enabled, false otherwise.
	 * @param provider Provider
	 * @return boolean true if enabled, false otherwise
	 */
	boolean isProviderEnabled(String provider);
	
	/**
	 * Listener used by Locater implementations.
	 * 
	 * @author torsten.roemer@luniks.net
	 */
	public interface LocaterLocationListener {
		
		/**
		 * Called when location updates occur, passing in the new location.
		 * @param location
		 */
		void onLocationChanged(Location location);
		
	}
	
}

