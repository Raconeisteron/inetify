package net.luniks.android.test.mock;

import net.luniks.android.interfaces.INotificationManager;
import android.app.Notification;

public class NotificationManagerMock implements INotificationManager {
	
	private int cancelledId = -1;
	private int notifiedId = -1;
	private Notification notification = null;

	public void cancel(int id) {
		this.cancelledId = id;
	}

	public void notify(int id, Notification notification) {
		this.notifiedId = id;
		this.notification = notification;
	}

	public int getCancelledId() {
		return cancelledId;
	}

	public int getNotifiedId() {
		return notifiedId;
	}

	public Notification getNotification() {
		return notification;
	}

}
