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


import net.luniks.android.inetify.ConnectivityActionReceiver;
import net.luniks.android.inetify.DatabaseAdapter;
import net.luniks.android.inetify.InetifyIntentService;
import net.luniks.android.inetify.TestInfo;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.test.ServiceTestCase;

public class InetifyIntentServiceTest extends ServiceTestCase<InetifyIntentService> {

	public InetifyIntentServiceTest() {
		super(InetifyIntentService.class);
	}
	
	public void testNullIntent() throws Exception {
		
		Intent serviceIntent = new Intent(this.getContext(), InetifyIntentService.class);
		serviceIntent.putExtra(ConnectivityActionReceiver.EXTRA_IS_WIFI_CONNECTED, true);
		
		this.setupService();
		InetifyIntentService serviceToTest = getService();
		
		TestTester tester = new TestTester();
		TestUtils.setFieldValue(serviceToTest, "tester", tester);
		
		DatabaseAdapter databaseAdapter = new TestDatabaseAdapter();
		TestUtils.setFieldValue(serviceToTest, "databaseAdapter", databaseAdapter);
		
		acquireWakeLock();
		
		this.startService(null);
		
		// FIXME How to wait for tester.test() to never get called?
		// TestUtils.waitForTestCount(tester, 0, 1000);
		Thread.sleep(1000);
		
		// When receiving a null intent, the service should ignore it and stop itself
		assertEquals(0, tester.testCount());
		
		TestUtils.waitForStaticFieldNull(InetifyIntentService.class, "wakeLock", 1000);
		
		assertFalse(this.getService().stopService(serviceIntent));
	}

	public void testNotNullIntent() throws Exception {
		
		Intent serviceIntent = new Intent(this.getContext(), InetifyIntentService.class);
		serviceIntent.putExtra(ConnectivityActionReceiver.EXTRA_IS_WIFI_CONNECTED, true);
		
		this.setupService();
		InetifyIntentService serviceToTest = getService();
		
		TestTester tester = new TestTester();
		TestUtils.setFieldValue(serviceToTest, "tester", tester);
		
		DatabaseAdapter databaseAdapter = new TestDatabaseAdapter();
		TestUtils.setFieldValue(serviceToTest, "databaseAdapter", databaseAdapter);
		
		acquireWakeLock();
		
		this.startService(serviceIntent);
		
		TestUtils.waitForTestCount(tester, 1, 1000);
		
		// Service should call Tester.test()
		assertEquals(1, tester.testCount());
		
		// Service should stop itself when the test is done 
		tester.done();
		
		TestUtils.waitForStaticFieldNull(InetifyIntentService.class, "wakeLock", 1000);
		
		assertFalse(this.getService().stopService(serviceIntent));
	}
	
	public void testWifiNotConnected() throws Exception {
		
		Intent serviceIntent = new Intent(this.getContext(), InetifyIntentService.class);
		serviceIntent.putExtra(ConnectivityActionReceiver.EXTRA_IS_WIFI_CONNECTED, false);
		
		this.setupService();
		InetifyIntentService serviceToTest = getService();
		
		TestTester tester = new TestTester();
		TestUtils.setFieldValue(serviceToTest, "tester", tester);
		
		DatabaseAdapter databaseAdapter = new TestDatabaseAdapter();
		TestUtils.setFieldValue(serviceToTest, "databaseAdapter", databaseAdapter);
		
		acquireWakeLock();
		
		this.startService(serviceIntent);
		
		// FIXME How to wait for tester.test() to never get called?
		// TestUtils.waitForTestCount(tester, 0, 1000);
		Thread.sleep(1000);
		
		// When wifi said it disconnected, the service should run a test anyway and check the
		// actual state of wifi connectivity, see InetifyIntentService.test()
		assertEquals(1, tester.testCount());
		
		// Service should stop itself when the test is done 
		tester.done();
		
		TestUtils.waitForStaticFieldNull(InetifyIntentService.class, "wakeLock", 1000);
		
		assertFalse(this.getService().stopService(serviceIntent));
	}
	
	public void testWifiIgnored() throws Exception {
		
		Intent serviceIntent = new Intent(this.getContext(), InetifyIntentService.class);
		serviceIntent.putExtra(ConnectivityActionReceiver.EXTRA_IS_WIFI_CONNECTED, true);
		
		this.setupService();
		InetifyIntentService serviceToTest = getService();
		
		TestTester tester = new TestTester();
		TestUtils.setFieldValue(serviceToTest, "tester", tester);
		
		DatabaseAdapter databaseAdapter = new TestDatabaseAdapter();
		databaseAdapter.addIgnoredWifi(tester.getWifiInfo().getBSSID(), tester.getWifiInfo().getSSID());
		TestUtils.setFieldValue(serviceToTest, "databaseAdapter", databaseAdapter);
		
		acquireWakeLock();
		
		this.startService(serviceIntent);
		
		// FIXME How to wait for tester.test() to never get called?
		// TestUtils.waitForTestCount(tester, 0, 1000);
		Thread.sleep(1000);
		
		// When Wifi is connected but ignored, the service should just skip the test and stop itself
		assertEquals(0, tester.testCount());
		
		TestUtils.waitForStaticFieldNull(InetifyIntentService.class, "wakeLock", 1000);
		
		assertFalse(this.getService().stopService(serviceIntent));
	}
	
	public void testWifiNotIgnored() throws Exception {
		
		Intent serviceIntent = new Intent(this.getContext(), InetifyIntentService.class);
		serviceIntent.putExtra(ConnectivityActionReceiver.EXTRA_IS_WIFI_CONNECTED, true);
		
		this.setupService();
		InetifyIntentService serviceToTest = getService();
		
		TestTester tester = new TestTester();
		TestInfo info = new TestInfo();
		info.setExtra("testWifiNotIgnored()");
		tester.setInfo(info);
		TestUtils.setFieldValue(serviceToTest, "tester", tester);
		
		DatabaseAdapter databaseAdapter = new TestDatabaseAdapter();
		databaseAdapter.addIgnoredWifi("NotIgnoredBSSID", "NotIgnoredSSID");
		TestUtils.setFieldValue(serviceToTest, "databaseAdapter", databaseAdapter);
		
		acquireWakeLock();
		
		this.startService(serviceIntent);
		
		TestUtils.waitForTestCount(tester, 1, 1000);
		
		// When Wifi is connected and not ignored, the service should call Tester.test()
		assertEquals(1, tester.testCount());
		
		tester.done();
		
		assertEquals("testWifiNotIgnored()", databaseAdapter.fetchTestResult().getExtra());
		
		TestUtils.waitForStaticFieldNull(InetifyIntentService.class, "wakeLock", 1000);
		
		assertFalse(this.getService().stopService(serviceIntent));
	}
	
	public void testTestThrowsException() throws Exception {
		
		Intent serviceIntent = new Intent("net.luniks.android.inetify.InetifyTestService");
		serviceIntent.putExtra(ConnectivityActionReceiver.EXTRA_IS_WIFI_CONNECTED, true);
		
		this.setupService();
		InetifyIntentService serviceToTest = getService();
		
		TestTester tester = new TestTester();
		TestUtils.setFieldValue(serviceToTest, "tester", tester);
		
		DatabaseAdapter databaseAdapter = new TestDatabaseAdapter();
		TestUtils.setFieldValue(serviceToTest, "databaseAdapter", databaseAdapter);
		
		acquireWakeLock();
		
		this.startService(serviceIntent);
		
		TestUtils.waitForTestCount(tester, 1, 10000);
		
		// Service should call Tester.test()
		assertEquals(1, tester.testCount());
		
		// Service should stop itself when the test threw an exception
		tester.throwException();
		
		TestUtils.waitForStaticFieldNull(InetifyIntentService.class, "wakeLock", 1000);
		
		assertFalse(this.getService().stopService(serviceIntent));
	}
	
	public void testDestroyed() throws Exception {
		
		Intent serviceIntent = new Intent(this.getContext(), InetifyIntentService.class);
		serviceIntent.putExtra(ConnectivityActionReceiver.EXTRA_IS_WIFI_CONNECTED, true);
		
		this.setupService();
		InetifyIntentService serviceToTest = getService();
		
		TestTester tester = new TestTester();
		TestUtils.setFieldValue(serviceToTest, "tester", tester);
		
		DatabaseAdapter databaseAdapter = new TestDatabaseAdapter();
		// To open the database
		databaseAdapter.isIgnoredWifi("TestBSSID");
		TestUtils.setFieldValue(serviceToTest, "databaseAdapter", databaseAdapter);
		
		assertTrue(databaseAdapter.isOpen());
		
		acquireWakeLock();
		
		this.startService(serviceIntent);
		
		TestUtils.waitForTestCount(tester, 1, 1000);
		
		// Service should call Tester.test()
		assertEquals(1, tester.testCount());
		
		// The service should cancel the test when it is killed
		shutdownService();
		
		assertTrue(tester.cancelled());
		assertFalse(databaseAdapter.isOpen());
		
		TestUtils.waitForStaticFieldNull(InetifyIntentService.class, "wakeLock", 1000);
		
		assertFalse(this.getService().stopService(serviceIntent));
	}
	
	public void testCancelWhileBusyAndStartNext() throws Exception {
		
		Intent serviceIntent = new Intent(this.getContext(), InetifyIntentService.class);
		serviceIntent.putExtra(ConnectivityActionReceiver.EXTRA_IS_WIFI_CONNECTED, true);
		
		this.setupService();
		InetifyIntentService serviceToTest = getService();
		
		TestTester tester = new TestTester();
		TestUtils.setFieldValue(serviceToTest, "tester", tester);
		
		DatabaseAdapter databaseAdapter = new TestDatabaseAdapter();
		TestUtils.setFieldValue(serviceToTest, "databaseAdapter", databaseAdapter);
		
		acquireWakeLock();
		
		this.startService(serviceIntent);
		
		TestUtils.waitForTestCount(tester, 1, 1000);
		
		// Service should call Tester.test()
		assertEquals(1, tester.testCount());
		
		// Fails assertion
		// startService(serviceIntent);
		
		// Does not cause onStart or onStartService to be called
		// this.getContext().startService(serviceIntent);
		
		// Only way to call onStartCommand()?
		// Queue up a second task
		serviceToTest.onStartCommand(serviceIntent, 0, 0);
		
		Thread.sleep(100);
		
		WakeLock wakeLock = (WakeLock)TestUtils.getStaticFieldValue(InetifyIntentService.class, "wakeLock");
		
		assertNotNull(wakeLock);
		assertTrue(wakeLock.isHeld());
		
		TestUtils.waitForTestCount(tester, 2, 1000);
		
		// First task should have been cancelled
		assertTrue(0 < tester.cancelCount());
		
		// The second task should have been started
		assertEquals(2, tester.testCount());
		
		// Let the second task complete
		tester.done();
		
		TestUtils.waitForStaticFieldNull(InetifyIntentService.class, "wakeLock", 1000);
		
		assertFalse(this.getService().stopService(serviceIntent));
	}
	
	private void acquireWakeLock() throws Exception {
		PowerManager powerManager = (PowerManager)this.getContext().getSystemService(Context.POWER_SERVICE);
		WakeLock wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, 
				InetifyIntentService.WAKE_LOCK_TAG);
		wakeLock.acquire();
		TestUtils.setStaticFieldValue(InetifyIntentService.class, "wakeLock", wakeLock);
	}

}
