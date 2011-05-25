package net.luniks.android.impl;

import net.luniks.android.interfaces.INotificationManager;
import android.app.Notification;
import android.app.NotificationManager;

/**
 * Implementation of INotificationManager.
 * @see android.app.NotificationManager
 * 
 * @author dode@luniks.net
 */
public class NotificationManagerImpl implements INotificationManager {
	
	private final NotificationManager notificationManager;
	
	public NotificationManagerImpl(final NotificationManager notificationManager) {
		this.notificationManager = notificationManager;
	}

	public void cancel(final int id) {
		this.notificationManager.cancel(id);
	}

	public void notify(final int id, final Notification notification) {
		this.notificationManager.notify(id, notification);
	}

}
