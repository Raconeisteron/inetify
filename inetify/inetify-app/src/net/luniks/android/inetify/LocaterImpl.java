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
	
	/** Maximum age of a (last known) location (60 seconds) */
	private long maxAge = 60 * 1000;
	
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
	 * Sets the maximum age in milliseconds a (last known) location can have
	 * to be accepted, default is 60 seconds.
	 * @param maxAge 
	 */
	public void setMaxAge(final long maxAge) {
		this.maxAge = maxAge;
	}
	
	/**
	 * Starts listening for location updates using the given listener.
	 * @param listener
	 */
	public void start(final LocaterLocationListener listener) {
		
		Log.d(Inetify.LOG_TAG, "Locater started");
		
		Location bestLastKnownLocation = this.getBestLastKnownLocation(maxAge);
		if(bestLastKnownLocation != null) {
			
			Log.d(Inetify.LOG_TAG, String.format("Locater bestLastKnownLocation %s", bestLastKnownLocation));
			
			listener.onLocationChanged(bestLastKnownLocation);
		}
		
		locationListener = new LocationListener() {
			
			public void onLocationChanged(final Location location) {
				if(location != null) {
					
					Log.d(Inetify.LOG_TAG, String.format("Locater onLocationChanged: %s", location));
					
					listener.onLocationChanged(location);
				}
			}

			public void onProviderDisabled(final String provider) {
			}

			public void onProviderEnabled(final String provider) {
			}

			public void onStatusChanged(final String provider, final int status, final Bundle extras) {
			}
		};
		
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
		locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
		
	}
	
	/**
	 * Stops listening for location updates.
	 */
	public void stop() {
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
	public boolean isAccurateEnough(final Location location, final Accuracy accuracy) {
		if(location == null) {
			return false;
		}
		
		// TODO Good idea?
		if(! location.hasAccuracy()) {
			return false;
		}
		
		return location.getAccuracy() <= accuracy.getMeters();
	}

}
