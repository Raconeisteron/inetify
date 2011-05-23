package net.luniks.android.inetify.test;

import net.luniks.android.inetify.NotifierImpl;
import net.luniks.android.inetify.R;
import net.luniks.android.inetify.TestInfo;
import net.luniks.android.test.mock.NotificationManagerMock;
import android.app.Notification;
import android.test.AndroidTestCase;

public class NotifierImplTest extends AndroidTestCase {
	
	public void testInfoNull() {
		
		NotificationManagerMock notificationManager = new NotificationManagerMock();
		
		NotifierImpl notifier = new NotifierImpl(getContext(), notificationManager);
		
		notifier.inetify(null);
		
		assertEquals(1, notificationManager.getCancelledIds().size());
		assertTrue(notificationManager.getCancelledIds().contains(NotifierImpl.NOTIFICATION_ID));
		
		assertEquals(0, notificationManager.getNotifications().size());
	}
	
	public void testInfoIsExpectedTitle() {
		
		NotificationManagerMock notificationManager = new NotificationManagerMock();
		
		NotifierImpl notifier = new NotifierImpl(getContext(), notificationManager);
		
		TestInfo info = new TestInfo();
		info.setIsExpectedTitle(true);
		
		notifier.inetify(info);
		
		assertEquals(0, notificationManager.getCancelledIds().size());
		assertFalse(notificationManager.getCancelledIds().contains(NotifierImpl.NOTIFICATION_ID));
		
		assertEquals(1, notificationManager.getNotifications().size());
		assertTrue(notificationManager.getNotifications().containsKey(NotifierImpl.NOTIFICATION_ID));
		
		Notification notification = notificationManager.getNotifications().get(NotifierImpl.NOTIFICATION_ID);
		
		assertEquals(R.drawable.notification_ok, notification.icon);
	}
	
	public void testInfoIsNotExpectedTitle() {
		
		NotificationManagerMock notificationManager = new NotificationManagerMock();
		
		NotifierImpl notifier = new NotifierImpl(getContext(), notificationManager);
		
		TestInfo info = new TestInfo();
		info.setIsExpectedTitle(false);
		
		notifier.inetify(info);
		
		assertEquals(0, notificationManager.getCancelledIds().size());
		assertFalse(notificationManager.getCancelledIds().contains(NotifierImpl.NOTIFICATION_ID));
		
		assertEquals(1, notificationManager.getNotifications().size());
		assertTrue(notificationManager.getNotifications().containsKey(NotifierImpl.NOTIFICATION_ID));
		
		Notification notification = notificationManager.getNotifications().get(NotifierImpl.NOTIFICATION_ID);
		
		assertEquals(R.drawable.notification_nok, notification.icon);
	}

}
