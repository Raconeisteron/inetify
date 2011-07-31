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
