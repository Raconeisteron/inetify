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
import android.content.Intent;
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
		
		this.startService(null);
		
		// FIXME How to wait for tester.test() to never get called?
		// TestUtils.waitForTestCount(tester, 0, 1000);
		Thread.sleep(1000);
		
		// When receiving a null intent, the service should ignore it and stop itself
		assertEquals(0, tester.testCount());
		
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
		
		this.startService(serviceIntent);
		
		TestUtils.waitForTestCount(tester, 1, 1000);
		
		// Service should call Tester.test()
		assertEquals(1, tester.testCount());
		
		// Service should stop itself when the test is done 
		tester.done();
		
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
		
		// FIXME How to wait for tester.test() to never get called?
		// TestUtils.waitForTestCount(tester, 0, 1000);
		Thread.sleep(1000);
		
		// When Wifi is not connected, the service should just skip the test, cancel notifications and stop itself
		assertEquals(0, tester.testCount());
		
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
		
		this.startService(serviceIntent);
		
		// FIXME How to wait for tester.test() to never get called?
		// TestUtils.waitForTestCount(tester, 0, 1000);
		Thread.sleep(1000);
		
		// When Wifi is connected but ignored, the service should just skip the test and stop itself
		assertEquals(0, tester.testCount());
		
		assertFalse(this.getService().stopService(serviceIntent));
	}
	
	public void testWifiNotIgnored() throws Exception {
		
		Intent serviceIntent = new Intent(this.getContext(), InetifyIntentService.class);
		serviceIntent.putExtra(ConnectivityActionReceiver.EXTRA_IS_WIFI_CONNECTED, true);
		
		this.setupService();
		InetifyIntentService serviceToTest = getService();
		
		TestTester tester = new TestTester();
		TestUtils.setFieldValue(serviceToTest, "tester", tester);
		
		DatabaseAdapter databaseAdapter = new TestDatabaseAdapter();
		databaseAdapter.addIgnoredWifi("NotIgnoredBSSID", "NotIgnoredSSID");
		TestUtils.setFieldValue(serviceToTest, "databaseAdapter", databaseAdapter);
		
		this.startService(serviceIntent);
		
		TestUtils.waitForTestCount(tester, 1, 1000);
		
		// When Wifi is connected and not ignored, the service should call Tester.test()
		assertEquals(1, tester.testCount());
		
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
		
		this.startService(serviceIntent);
		
		TestUtils.waitForTestCount(tester, 1, 1000);
		
		// Service should call Tester.test()
		assertEquals(1, tester.testCount());
		
		// Service should stop itself when the test threw an exception
		tester.throwException();
		
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
		
		this.startService(serviceIntent);
		
		TestUtils.waitForTestCount(tester, 1, 1000);
		
		// Service should call Tester.test()
		assertEquals(1, tester.testCount());
		
		// The service should cancel the test when it is killed
		shutdownService();
		
		assertTrue(tester.cancelled());
		assertFalse(databaseAdapter.isOpen());
		
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
		
		TestUtils.waitForTestCount(tester, 2, 1000);
		
		// First task should have been cancelled
		assertEquals(1, tester.cancelCount());
		
		// The second task should have been started
		assertEquals(2, tester.testCount());
		
		// Let the second task complete
		tester.done();
		
		assertFalse(this.getService().stopService(serviceIntent));
	}

}