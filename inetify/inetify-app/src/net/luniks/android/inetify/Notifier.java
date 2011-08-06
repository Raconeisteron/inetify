package net.luniks.android.inetify;

import android.location.Location;

/**
 * Interface for a class creating notifications based on the TestInfo given to inetify(TestInfo).
 * 
 * @author torsten.roemer@luniks.net
 */
public interface Notifier {
	
	/**
	 * Creates the notification using the given INotificationManager, based on the
	 * given TestInfo. Cancels existing notifications if info is null.
	 * @param info
	 */
	void inetify(TestInfo info);

	void locatify(Location location, WifiLocation nearestlocation);

}
