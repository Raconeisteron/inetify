package net.luniks.android.inetify;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

/**
 * Implementation of Locater.
 * 
 * @author torsten.roemer@luniks.net
 */
public class LocaterImpl implements Locater {
	
	/** Maximum age of a (last known) location */
	private static final long MAX_AGE = 60 * 1000;

	/** LocationManager instance */
	private final LocationManager locationManager;
	
	/** LocationListener instance */
	private LocationListener locationListener;
	
	/**
	 * Creates an instance using the given context.
	 * @param context
	 */
	public LocaterImpl(final Context context) {
		this.locationManager = (LocationManager)context.getSystemService(Context.LOCATION_SERVICE);
	}
	
	/**
	 * Starts listening for location updates using the given listener, using GPS and NETWORK provider,
	 * and first considering lastKnownLocations of both providers before forwarding the Location
	 * passed in LocationListener.onNewLocation().
	 */
	public void start(final LocaterLocationListener listener) {
		
		Location lastKnownLocationNetwork = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
		if(isNotNullAndNotTooOld(lastKnownLocationNetwork)) {
			listener.onNewLocation(lastKnownLocationNetwork);
		}
		
		Location lastKnownLocationGPS = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		if(isNotNullAndNotTooOld(lastKnownLocationGPS)) {
			listener.onNewLocation(lastKnownLocationGPS);
		}
		
		locationListener = new LocationListener() {
			
			public void onLocationChanged(Location location) {
				listener.onNewLocation(location);
			}

			public void onProviderDisabled(String provider) {
			}

			public void onProviderEnabled(String provider) {
			}

			public void onStatusChanged(String provider, int status, Bundle extras) {
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
		}
	}
	
	/**
	 * Returns true if the given location has at least the given accuracy, false otherwise.
	 * @param location Location
	 * @param accuracy in meters
	 * @return boolean true if the location has at least the given accuracy
	 */
	public boolean isAccurateEnough(final Location location, final Accuracy accuracy) {
		
		// TODO Good idea?
		if(! location.hasAccuracy()) {
			return false;
		}
		
		return location.getAccuracy() <= accuracy.getMeters();
	}
	
	/**
	 * Returns true if the given location is not null and not older than
	 * MAX_AGE, false otherwise.
	 * @param location
	 * @return boolean true if the given location is not null and not older than 
	 * MAX_AGE
	 */
	private boolean isNotNullAndNotTooOld(final Location location) {
		if(location == null) {
			return false;
		}
		
		if(System.currentTimeMillis() - location.getTime() > MAX_AGE) {
			return false;
		}
		
		return true;
	}

}
