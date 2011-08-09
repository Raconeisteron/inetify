package net.luniks.android.inetify;

import android.location.Location;

/**
 * Interface for a class creating notifications.
 * 
 * @author torsten.roemer@luniks.net
 */
public interface Notifier {
	
	/**
	 * Creates an "internet connectivity test" notification based on the given TestInfo,
	 * cancels an existing notification if info is null.
	 * @param info test info
	 */
	void inetify(TestInfo info);

	/**
	 * Creates a "nearest Wifi" notification using the given location and Wifi location.
	 * @param location current location
	 * @param nearestLocation nearest Wifi location
	 */
	void locatify(Location location, WifiLocation nearestlocation);

}
