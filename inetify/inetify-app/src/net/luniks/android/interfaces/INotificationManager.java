package net.luniks.android.interfaces;

import android.app.Notification;

/**
 * Interface for a wrapper for NotificationManager, to allow mocking.
 * @see android.app.NotificationManager
 * 
 * @author dode@luniks.net
 */
public interface INotificationManager {
	
	void cancel(int id);

	void notify(int id, Notification notification);

}
