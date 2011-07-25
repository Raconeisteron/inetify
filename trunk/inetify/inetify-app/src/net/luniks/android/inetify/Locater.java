package net.luniks.android.inetify;

import android.location.Location;

/**
 * Interface for a class providing location updates using a LocaterLocationListener. 
 * @author torsten.roemer@luniks.net
 */
public interface Locater {

	/**
	 * Starts listening for location updates using the given listener.
	 * @param listener
	 */
	void start(LocaterLocationListener listener);
	
	/**
	 * Stops listening for location updates.
	 */
	void stop();
	
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
	boolean isAccurateEnough(Location location, Accuracy accuracy);
	
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
	
	/**
	 * Enum for accuracy levels.
	 * @author torsten.roemer@luniks.net
	 */
	public static enum Accuracy {
		
		/**
		 * Fine accuracy.
		 */
		FINE(100),
		
		/**
		 * Coarse accuracy.
		 */
		COARSE(1500);
		
		int meters;
		
		Accuracy(final int meters) {
			this.meters = meters;
		}
		
		/**
		 * Returns the accuracy level in meters.
		 * @return int meters
		 */
		public int getMeters() {
			return meters;
		}
	}
	
}

