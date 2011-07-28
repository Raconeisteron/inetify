package net.luniks.android.interfaces;

import java.util.List;

import android.location.Location;
import android.location.LocationListener;

public interface ILocationManager {
	
	List<String> getAllProviders();
	
	boolean isProviderEnabled(String provider);
	
	Location getLastKnownLocation(String provider);
	
	void requestLocationUpdates(String provider, long minTime, float minDistance, LocationListener listener);
	
	void removeUpdates(LocationListener listener);

}
