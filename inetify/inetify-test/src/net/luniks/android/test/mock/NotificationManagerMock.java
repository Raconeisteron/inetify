package net.luniks.android.test.mock;

import java.util.ArrayList;
import java.util.List;

import net.luniks.android.interfaces.INotificationManager;
import android.app.Notification;

public class NotificationManagerMock implements INotificationManager {
	
	private List<Integer> cancelledIds = new ArrayList<Integer>();
	private List<TestNotification> notifications = new ArrayList<TestNotification>();

	public void cancel(int id) {
		this.cancelledIds.add(id);
	}

	public void notify(int id, Notification notification) {
		this.notifications.add(new TestNotification(id, notification));
	}
	
	public void reset() {
		cancelledIds.clear();
		notifications.clear();
	}

	public List<Integer> getCancelledIds() {
		return cancelledIds;
	}

	public List<TestNotification> getNotifications() {
		return notifications;
	}
	
	public static class TestNotification {
		
		private final int id;
		private final Notification notification;
		
		public TestNotification(int id, Notification notification) {
			this.id = id;
			this.notification = notification;
		}
		
		public int getId() {
			return id;
		}
		public Notification getNotification() {
			return notification;
		}
		
	}

}
