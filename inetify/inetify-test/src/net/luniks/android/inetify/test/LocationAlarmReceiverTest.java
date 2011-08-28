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

import net.luniks.android.inetify.LocationAlarmReceiver;
import net.luniks.android.inetify.LocationIntentService;
import android.content.Intent;
import android.os.PowerManager.WakeLock;
import android.test.AndroidTestCase;

public class LocationAlarmReceiverTest extends AndroidTestCase {
	
	public void testNullIntent() throws Exception {
		
		LocationAlarmReceiver receiver = new LocationAlarmReceiver();
		
		TestContext testContext = new TestContext(this.getContext());
		
		receiver.onReceive(testContext, null);
		
		assertEquals(0, testContext.getStartServiceCount());
		
		WakeLock wakeLock = (WakeLock)TestUtils.getStaticFieldValue(LocationIntentService.class, "wakeLock");
		
		assertNull(wakeLock);
	}
	
	public void testEmptyIntent() throws Exception {
		
		LocationAlarmReceiver receiver = new LocationAlarmReceiver();
		
		TestContext testContext = new TestContext(this.getContext());
		
		receiver.onReceive(testContext, new Intent());
		
		assertEquals(0, testContext.getStartServiceCount());
		
		WakeLock wakeLock = (WakeLock)TestUtils.getStaticFieldValue(LocationIntentService.class, "wakeLock");
		
		assertNull(wakeLock);
	}
	
	public void testOtherAction() throws Exception {
		
		LocationAlarmReceiver receiver = new LocationAlarmReceiver();
		
		TestContext testContext = new TestContext(this.getContext());
		
		receiver.onReceive(testContext, new Intent("OTHER_ACTION"));
		
		assertEquals(0, testContext.getStartServiceCount());
		
		WakeLock wakeLock = (WakeLock)TestUtils.getStaticFieldValue(LocationIntentService.class, "wakeLock");
		
		assertNull(wakeLock);
	}
	
	public void testLocationAlarm() throws Exception {
		
		LocationAlarmReceiver receiver = new LocationAlarmReceiver();
		
		TestContext testContext = new TestContext(this.getContext());
		
		Intent intent = new Intent(testContext, LocationAlarmReceiver.class);
		intent.setAction(LocationAlarmReceiver.ACTION_LOCATION_ALARM);
		receiver.onReceive(testContext, intent);
		
		assertEquals(1, testContext.getStartServiceCount());
		
		WakeLock wakeLock = (WakeLock)TestUtils.getStaticFieldValue(LocationIntentService.class, "wakeLock");
		
		assertNotNull(wakeLock);
		assertTrue(wakeLock.isHeld());
		
		wakeLock.release();
		TestUtils.setStaticFieldValue(LocationIntentService.class, "wakeLock", null);
	}

}
