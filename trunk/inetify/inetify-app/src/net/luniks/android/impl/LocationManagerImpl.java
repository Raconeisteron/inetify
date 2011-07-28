package net.luniks.android.impl;

import java.util.List;

import net.luniks.android.interfaces.ILocationManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;

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
