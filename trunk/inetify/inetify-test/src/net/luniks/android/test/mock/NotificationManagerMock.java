package net.luniks.android.test.mock;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.luniks.android.interfaces.INotificationManager;
import android.app.Notification;

public class NotificationManagerMock implements INotificationManager {
	
	private List<Integer> cancelledIds = new ArrayList<Integer>();
	private Map<Integer, Notification> notifications = new HashMap<Integer, Notification>();

	public void cancel(final int id) {
		this.cancelledIds.add(id);
	}

	public void notify(final int id, final Notification notification) {
		this.notifications.put(id, notification);
	}
	
	public void reset() {
		cancelledIds.clear();
		notifications.clear();
	}

	public List<Integer> getCancelledIds() {
		return cancelledIds;
	}

	public Map<Integer, Notification> getNotifications() {
		return notifications;
	}

}
