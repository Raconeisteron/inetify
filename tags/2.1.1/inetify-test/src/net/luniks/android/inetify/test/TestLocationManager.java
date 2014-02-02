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
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import net.luniks.android.interfaces.ILocationManager;
import android.location.Location;
import android.location.LocationListener;

/**
 * 
 * @author torsten.roemer@luniks.net
 */
public class TestLocationManager implements ILocationManager {
	
	private ConcurrentHashMap<String, TestProvider> providers = 
		new ConcurrentHashMap<String, TestProvider>();
	
	public TestLocationManager() {
		providers.put("gps", new TestProvider());
		providers.put("network", new TestProvider());
		providers.put("passive", new TestProvider());
	}
	
	public void setAllProvidersEnabled(final boolean enabled) {
		for(Map.Entry<String, TestProvider> entry : providers.entrySet()) {
			entry.getValue().setEnabled(enabled);
		}
	}

	public List<String> getAllProviders() {
		return new ArrayList<String>(providers.keySet());
	}

	public boolean isProviderEnabled(final String provider) {
		return providers.get(provider).isEnabled();
	}

	public Location getLastKnownLocation(final String provider) {
		return new Location(provider);
	}

	public void requestLocationUpdates(final String provider, final long minTime,
			float minDistance, LocationListener listener) {
		// TODO Auto-generated method stub

	}

	public void removeUpdates(final LocationListener listener) {
		// TODO Auto-generated method stub

	}
	
	private static class TestProvider {
			
		private boolean enabled;
		
		public TestProvider() {
			this.enabled = true;
		}

		public boolean isEnabled() {
			return enabled;
		}
		public void setEnabled(final boolean enabled) {
			this.enabled = enabled;
		}
	}

}
