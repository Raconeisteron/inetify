/*
 * Copyright 2011 Torsten Römer
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.luniks.android.inetify.test;

import net.luniks.android.inetify.NotifierImpl;
import net.luniks.android.inetify.R;
import net.luniks.android.inetify.WifiLocation;
import net.luniks.android.test.mock.NotificationManagerMock;
import android.app.Notification;
import android.app.PendingIntent;
import android.location.Location;
import android.test.AndroidTestCase;

public class NotifierImplLocatifyTest extends AndroidTestCase {

	public void testBothNull() {
		
		NotificationManagerMock notificationManager = new NotificationManagerMock();
		
		NotifierImpl notifier = new NotifierImpl(getContext(), notificationManager);
		
		notifier.locatify(null, null);
		
		assertEquals(1, notificationManager.getCancelledIds().size());
		assertTrue(notificationManager.getCancelledIds().contains(NotifierImpl.LOCATIFY_NOTIFICATION_ID));
		
		assertEquals(0, notificationManager.getNotifications().size());
	}
	
	public void testLocationNull() {
		
		NotificationManagerMock notificationManager = new NotificationManagerMock();
		
		NotifierImpl notifier = new NotifierImpl(getContext(), notificationManager);
		
		notifier.locatify(null, new WifiLocation());
		
		assertEquals(1, notificationManager.getCancelledIds().size());
		assertTrue(notificationManager.getCancelledIds().contains(NotifierImpl.LOCATIFY_NOTIFICATION_ID));
		
		assertEquals(0, notificationManager.getNotifications().size());
	}
	
	public void testNearestLocationNull() {
		
		NotificationManagerMock notificationManager = new NotificationManagerMock();
		
		NotifierImpl notifier = new NotifierImpl(getContext(), notificationManager);
		
		notifier.locatify(new Location("Test"), null);
		
		assertEquals(1, notificationManager.getCancelledIds().size());
		assertTrue(notificationManager.getCancelledIds().contains(NotifierImpl.LOCATIFY_NOTIFICATION_ID));
		
		assertEquals(0, notificationManager.getNotifications().size());
	}
	
	public void testNotification() {
		
		NotificationManagerMock notificationManager = new NotificationManagerMock();
		
		NotifierImpl notifier = new NotifierImpl(getContext(), notificationManager);
		
		Location location = new Location("Test");
		location.setAccuracy(333.3f);
		
		WifiLocation nearestLocation = new WifiLocation();
		nearestLocation.setName("TestNearest");
		nearestLocation.setDistance(33.3f);
		
		notifier.locatify(location, nearestLocation);
		
		assertEquals(0, notificationManager.getCancelledIds().size());
		
		assertEquals(1, notificationManager.getNotifications().size());
		assertTrue(notificationManager.getNotifications().containsKey(NotifierImpl.LOCATIFY_NOTIFICATION_ID));
		
		Notification notification = notificationManager.getNotifications().get(NotifierImpl.LOCATIFY_NOTIFICATION_ID);
		
		String tickerText = this.getContext().getString(R.string.notification_next_wifi_title, 
				nearestLocation.getName());
		
		assertEquals(R.drawable.notification, notification.icon);
		assertEquals(tickerText, notification.tickerText.toString());
		
		PendingIntent contentIntent = notification.contentIntent;
		
		// TODO More assertions, how?
		assertNotNull(contentIntent);
	}

}
