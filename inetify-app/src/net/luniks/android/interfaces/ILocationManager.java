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
package net.luniks.android.interfaces;

import java.util.List;

import android.location.Location;
import android.location.LocationListener;

/**
 * Interface for a wrapper for LocationManager, to allow mocking.
 * @see android.location.LocationManager
 * 
 * @author torsten.roemer@luniks.net
 */
public interface ILocationManager {
	
	List<String> getAllProviders();
	
	boolean isProviderEnabled(String provider);
	
	Location getLastKnownLocation(String provider);
	
	void requestLocationUpdates(String provider, long minTime, float minDistance, LocationListener listener);
	
	void removeUpdates(LocationListener listener);

}
