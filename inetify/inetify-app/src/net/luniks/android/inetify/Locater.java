package net.luniks.android.inetify;

import android.location.Location;

/**
 * Interface for a class providing location updates using a LocaterLocationListener. 
 * @author torsten.roemer@luniks.net
 */
public interface Locater {
	
	/** Provider used for locations coming from the database */
	public static final String PROVIDER_DATABASE = "database";

	/**
	 * First checks for last known locations and if there was none that satisfied the given criteria,
	 * starts listening for location updates using the given listener, using GPS or not. Stops itself
	 * when it found a location that satisfied the given criteria.
	 * @param listener
	 * @param maxAge
	 * @param minAccuracy
	 * @param useGPS
	 */
	void start(LocaterLocationListener listener, long maxAge, int minAccuracy, boolean useGPS);
	
	/**
	 * Stops listening for location updates.
	 */
	void stop();
	
	/**
	 * Returns true if checking for last known locations or listening for location updates,
	 * false otherwise.
	 * @return boolean
	 */
	boolean isRunning();
	
	/**
	 * Returns the best last known location that is not older than maxAge.
	 * @param maxAge
	 * @return Location
	 */
	Location getBestLastKnownLocation(long maxAge);

	/**
	 * Returns true if the given location has at least the given accuracy, false otherwise.
	 * @param location Location
	 * @param accuracy in meters
	 * @return boolean true if the location has at least the given accuracy
	 */
	boolean isAccurateEnough(Location location, int accuracy);
	
	/**
	 * Returns true if the given provider is enabled, false otherwise.
	 * @param provider Provider
	 * @return boolean true if enabled, false otherwise
	 */
	boolean isProviderEnabled(String provider);
	
	/**
	 * Listener used by Locater implementations.
	 * 
	 * @author torsten.roemer@luniks.net
	 */
	public interface LocaterLocationListener {
		
		/**
		 * Called when location updates occur, passing in the new location.
		 * @param location
		 */
		void onLocationChanged(Location location);
		
	}
	
}

