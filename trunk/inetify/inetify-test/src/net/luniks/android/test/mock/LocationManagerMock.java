package net.luniks.android.test.mock;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.luniks.android.interfaces.ILocationManager;
import android.location.Location;
import android.location.LocationListener;

public class LocationManagerMock implements ILocationManager {
	
	private List<String> allProviders = new ArrayList<String>();
	private Map<String, Location> lastKnownLocations = new HashMap<String, Location>();
	private Map<String, LocationListener> listeners = new HashMap<String, LocationListener>();
	
	public LocationManagerMock() {
		this.addProvider("A");
		this.addProvider("B");
		this.addProvider("C");
		this.addProvider("D");
		this.addProvider("E");
		this.addProvider("F");
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
