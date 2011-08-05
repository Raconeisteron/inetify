package net.luniks.android.inetify.test;

import net.luniks.android.inetify.NotifierImpl;
import net.luniks.android.inetify.R;
import net.luniks.android.inetify.TestInfo;
import net.luniks.android.test.mock.NotificationManagerMock;
import android.app.Notification;
import android.app.PendingIntent;
import android.preference.PreferenceManager;
import android.test.AndroidTestCase;

public class NotifierImplTest extends AndroidTestCase {

	public void testInfoNull() {
		
		NotificationManagerMock notificationManager = new NotificationManagerMock();
		
		NotifierImpl notifier = new NotifierImpl(getContext(), notificationManager);
		
		notifier.inetify(null);
		
		assertEquals(1, notificationManager.getCancelledIds().size());
		assertTrue(notificationManager.getCancelledIds().contains(NotifierImpl.INETIFY_NOTIFICATION_ID));
		
		assertEquals(0, notificationManager.getNotifications().size());
	}
	
	public void testInfoIsExpectedTitleSettingsOnlyNotOK() {
		
		PreferenceManager.getDefaultSharedPreferences(getContext()).edit().putBoolean("settings_only_nok", true).commit();
		
		NotificationManagerMock notificationManager = new NotificationManagerMock();
		
		NotifierImpl notifier = new NotifierImpl(getContext(), notificationManager);
		
		TestInfo info = new TestInfo();
		info.setIsExpectedTitle(true);
		
		notifier.inetify(info);
		
		assertEquals(1, notificationManager.getCancelledIds().size());
		assertTrue(notificationManager.getCancelledIds().contains(NotifierImpl.INETIFY_NOTIFICATION_ID));
		
		assertEquals(0, notificationManager.getNotifications().size());
		
	}
	
	public void testContentIntent() {
		
		PreferenceManager.getDefaultSharedPreferences(getContext()).edit().putBoolean("settings_only_nok", false).commit();
		
		NotificationManagerMock notificationManager = new NotificationManagerMock();
		
		NotifierImpl notifier = new NotifierImpl(getContext(), notificationManager);
		
		TestInfo info = new TestInfo();
		info.setIsExpectedTitle(true);
		
		notifier.inetify(info);
		
		assertEquals(1, notificationManager.getNotifications().size());
		
		Notification notification = notificationManager.getNotifications().get(NotifierImpl.INETIFY_NOTIFICATION_ID);
		
		PendingIntent contentIntent = notification.contentIntent;
		
		// TODO More assertions, how?
		assertNotNull(contentIntent);
		
	}
	
	public void testNoToneNoLight() {
		
		PreferenceManager.getDefaultSharedPreferences(getContext()).edit().putBoolean("settings_only_nok", false).commit();
		
		// No notification tone, no LED
		PreferenceManager.getDefaultSharedPreferences(getContext()).edit().putString("settings_tone", "").commit();
		PreferenceManager.getDefaultSharedPreferences(getContext()).edit().putBoolean("settings_light", false).commit();
		
		NotificationManagerMock notificationManager = new NotificationManagerMock();
		
		NotifierImpl notifier = new NotifierImpl(getContext(), notificationManager);
		
		TestInfo info = new TestInfo();
		info.setIsExpectedTitle(true);
		
		notifier.inetify(info);
		
		assertEquals(1, notificationManager.getNotifications().size());
		
		Notification notification = notificationManager.getNotifications().get(NotifierImpl.INETIFY_NOTIFICATION_ID);
		
		assertFalse(Notification.DEFAULT_LIGHTS == (Notification.DEFAULT_LIGHTS & notification.flags));
		assertFalse(Notification.FLAG_SHOW_LIGHTS == (Notification.FLAG_SHOW_LIGHTS & notification.flags));
		assertNull(notification.sound);
		
		assertTrue(Notification.FLAG_ONLY_ALERT_ONCE == (notification.flags & Notification.FLAG_ONLY_ALERT_ONCE));
		assertTrue(Notification.FLAG_AUTO_CANCEL == (notification.flags & Notification.FLAG_AUTO_CANCEL));
		
	}
	
	public void testDefaultToneAndLight() {
		
		PreferenceManager.getDefaultSharedPreferences(getContext()).edit().putBoolean("settings_only_nok", false).commit();
		
		// Default notification tone and LED
		String defaultTone = android.provider.Settings.System.DEFAULT_NOTIFICATION_URI.toString();
		PreferenceManager.getDefaultSharedPreferences(getContext()).edit().putString("settings_tone", defaultTone).commit();
		PreferenceManager.getDefaultSharedPreferences(getContext()).edit().putBoolean("settings_light", true).commit();
		
		NotificationManagerMock notificationManager = new NotificationManagerMock();
		
		NotifierImpl notifier = new NotifierImpl(getContext(), notificationManager);
		
		TestInfo info = new TestInfo();
		info.setIsExpectedTitle(true);
		
		notifier.inetify(info);
		
		assertEquals(1, notificationManager.getNotifications().size());
		
		Notification notification = notificationManager.getNotifications().get(NotifierImpl.INETIFY_NOTIFICATION_ID);
		
		assertTrue(Notification.DEFAULT_LIGHTS == (notification.defaults & Notification.DEFAULT_LIGHTS));
		assertTrue(Notification.FLAG_SHOW_LIGHTS == (notification.flags & Notification.FLAG_SHOW_LIGHTS));
		assertEquals(android.provider.Settings.System.DEFAULT_NOTIFICATION_URI, notification.sound);
		
		assertTrue(Notification.FLAG_ONLY_ALERT_ONCE == (notification.flags & Notification.FLAG_ONLY_ALERT_ONCE));
		assertTrue(Notification.FLAG_AUTO_CANCEL == (notification.flags & Notification.FLAG_AUTO_CANCEL));
		
	}
	
	public void testInfoIsExpectedTitle() {
		
		PreferenceManager.getDefaultSharedPreferences(getContext()).edit().putBoolean("settings_only_nok", false).commit();
		
		NotificationManagerMock notificationManager = new NotificationManagerMock();
		
		NotifierImpl notifier = new NotifierImpl(getContext(), notificationManager);
		
		TestInfo info = new TestInfo();
		info.setIsExpectedTitle(true);
		
		notifier.inetify(info);
		
		assertEquals(0, notificationManager.getCancelledIds().size());
		assertFalse(notificationManager.getCancelledIds().contains(NotifierImpl.INETIFY_NOTIFICATION_ID));
		
		assertEquals(1, notificationManager.getNotifications().size());
		assertTrue(notificationManager.getNotifications().containsKey(NotifierImpl.INETIFY_NOTIFICATION_ID));
		
		Notification notification = notificationManager.getNotifications().get(NotifierImpl.INETIFY_NOTIFICATION_ID);
		
		assertEquals(R.drawable.notification_ok, notification.icon);
		assertEquals(getContext().getString(R.string.notification_ok_title), notification.tickerText.toString());
		
	}
	
	public void testInfoIsNotExpectedTitle() {
		
		NotificationManagerMock notificationManager = new NotificationManagerMock();
		
		NotifierImpl notifier = new NotifierImpl(getContext(), notificationManager);
		
		TestInfo info = new TestInfo();
		info.setIsExpectedTitle(false);
		
		notifier.inetify(info);
		
		assertEquals(0, notificationManager.getCancelledIds().size());
		assertFalse(notificationManager.getCancelledIds().contains(NotifierImpl.INETIFY_NOTIFICATION_ID));
		
		assertEquals(1, notificationManager.getNotifications().size());
		assertTrue(notificationManager.getNotifications().containsKey(NotifierImpl.INETIFY_NOTIFICATION_ID));
		
		Notification notification = notificationManager.getNotifications().get(NotifierImpl.INETIFY_NOTIFICATION_ID);
		
		assertEquals(R.drawable.notification_nok, notification.icon);
		assertEquals(getContext().getString(R.string.notification_nok_title), notification.tickerText.toString());
		
	}

}
