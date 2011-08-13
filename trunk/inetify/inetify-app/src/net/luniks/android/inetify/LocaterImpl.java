package net.luniks.android.inetify;

import java.util.List;

import net.luniks.android.interfaces.ILocationManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

/**
 * Implementation of Locater.
 * 
 * @author torsten.roemer@luniks.net
 */
public class LocaterImpl implements Locater {
	
	/** LocationManager instance */
	private final ILocationManager locationManager;
	
	/** LocationListener instance */
	private LocationListener locationListener;
	
	/**
	 * Creates an instance using the given ILocationManager implementation.
	 * @param locationManager
	 */
	public LocaterImpl(final ILocationManager locationManager) {
		this.locationManager = locationManager;
	}
	
	/**
	 * First checks for last known locations and if there was none that satisfied the given criteria,
	 * starts listening for location updates using the given listener, using GPS or not. Stops itself
	 * when it found a location that satisfied the given criteria.
	 * @param listener
	 * @param maxAge
	 * @param minAccuracy
	 * @param useGPS
	 */
	public synchronized void start(final LocaterLocationListener listener,
			final long maxAge, final int minAccuracy, final boolean useGPS) {
		
		Log.d(Inetify.LOG_TAG, String.format("Locater started with maxAge: %s, minAccuracy: %s, useGPS: %s", 
				maxAge, minAccuracy, useGPS));
		
		Location bestLastKnownLocation = this.getBestLastKnownLocation(maxAge);
		if(bestLastKnownLocation != null && bestLastKnownLocation.getAccuracy() <= minAccuracy) {
			
			Log.d(Inetify.LOG_TAG, String.format("Locater bestLastKnownLocation %s", bestLastKnownLocation));
			
			listener.onLocationChanged(bestLastKnownLocation);
			
			if(minAccuracy < Integer.MAX_VALUE) {
				Log.d(Inetify.LOG_TAG, "Not listening for location updates as a last known location was sufficient");
				
				return;
			}
		}
		
		if(locationListener != null) {	
			locationManager.removeUpdates(locationListener);
		}
		
		locationListener = new LocationListener() {
			
			public void onLocationChanged(final Location location) {
				if(location != null && location.getAccuracy() <= minAccuracy) {
					
					Log.d(Inetify.LOG_TAG, String.format("Locater onLocationChanged: %s", location));
					
					listener.onLocationChanged(location);
					
					if(minAccuracy < Integer.MAX_VALUE) {
						stop();
					}
				}
			}

			public void onProviderDisabled(final String provider) {
			}

			public void onProviderEnabled(final String provider) {
			}

			public void onStatusChanged(final String provider, final int status, final Bundle extras) {
			}
		};
		
		// The passive provider doesn't seem to make much sense here - it merely duplicates location updates
		if(useGPS) {
			locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
		}
		locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
	}
	
	/**
	 * Stops listening for location updates.
	 */
	public synchronized void stop() {
		if(locationListener != null) {	
			locationManager.removeUpdates(locationListener);
			
			Log.d(Inetify.LOG_TAG, "Locater stopped");
		}
	}
	
	/**
	 * Returns the best last known location that is not older than maxAge.
	 * @param maxAge
	 * @return Location
	 */
	public Location getBestLastKnownLocation(final long maxAge) {
		List<String> allProviders = locationManager.getAllProviders();
		Location bestLocation = null;
		long bestTime = System.currentTimeMillis() - maxAge;
		float bestAccuracy = Float.MAX_VALUE;
		for(String provider : allProviders) {
			Location location = locationManager.getLastKnownLocation(provider);			
			if(location != null) {
				long time = location.getTime();
				float accuracy = location.getAccuracy();
				if(accuracy < bestAccuracy && time >= bestTime) {
					bestLocation = location;
					bestTime = time;
					bestAccuracy = accuracy;
				} else if(time > bestTime) {
					bestLocation = location;
					bestTime = time;
					bestAccuracy = accuracy;
				}
			}
		}
		return bestLocation;
	}
	
	/**
	 * Returns true if the given location has at least the given accuracy, false otherwise.
	 * @param location Location
	 * @param accuracy in meters
	 * @return boolean true if the location has at least the given accuracy
	 */
	public boolean isAccurateEnough(final Location location, final int accuracy) {
		if(location == null) {
			return false;
		}
		
		// TODO Good idea?
		if(! location.hasAccuracy()) {
			return true;
		}
		
		return location.getAccuracy() <= accuracy;
	}
	
	/**
	 * Returns true if the given provider is enabled, false otherwise.
	 * @param provider Provider
	 * @return boolean true if enabled, false otherwise
	 */
	public boolean isProviderEnabled(final String provider) {
		return locationManager.isProviderEnabled(provider);
	}

}
