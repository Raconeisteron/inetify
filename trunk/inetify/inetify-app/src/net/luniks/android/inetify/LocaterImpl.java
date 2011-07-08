package net.luniks.android.inetify;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

public class LocaterImpl implements Locater {
	
	private static final long MAX_AGE = 60 * 1000;

	private final LocationManager locationManager;
	
	private LocationListener locationListener;
	
	public LocaterImpl(final Context context) {
		this.locationManager = (LocationManager)context.getSystemService(Context.LOCATION_SERVICE);
	}
	
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
	
	public void stop() {
		if(locationListener != null) {
			locationManager.removeUpdates(locationListener);
		}
	}
	
	private boolean isNotNullAndNotTooOld(final Location location) {
		if(location == null) {
			return false;
		}
		
		if(System.currentTimeMillis() - location.getTime() > MAX_AGE) {
			return false;
		}
		
		return true;
	}

	public boolean isAccurateEnough(final Location location, final Accuracy accuracy) {
		
		// TODO Good idea?
		if(! location.hasAccuracy()) {
			return false;
		}
		
		return location.getAccuracy() <= accuracy.getMeters();
	}

}
