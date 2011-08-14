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
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.preference.PreferenceManager;
import android.test.AndroidTestCase;

public class ConnectivityActionReceiverTest extends AndroidTestCase {
	
	private ConnectivityActionReceiver receiver;
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		
		receiver = new ConnectivityActionReceiver();
	}
	
	public void testNullIntent() {
				
		TestContext testContext = new TestContext(this.getContext());
		
		receiver.onReceive(testContext, null);
		
		assertEquals(0, testContext.getStartServiceCount());
	}
	
	public void testOtherIntent() {
				
		TestContext testContext = new TestContext(this.getContext());
		
		receiver.onReceive(testContext, new Intent());
		
		assertEquals(0, testContext.getStartServiceCount());
	}

	public void testWifiConnected() throws Exception {
		
		PreferenceManager.getDefaultSharedPreferences(getContext()).edit().putBoolean("settings_enabled", true).commit();
		
		NetworkInfo mobileDisconnected = TestUtils.createNetworkInfo(ConnectivityManager.TYPE_MOBILE, false);
		NetworkInfo wifiConnected = TestUtils.createNetworkInfo(ConnectivityManager.TYPE_WIFI, true);
		
		// Mobile disconnected
		Intent connectivityActionMobileDisconnect = new Intent(ConnectivityManager.CONNECTIVITY_ACTION);
		connectivityActionMobileDisconnect.putExtra(ConnectivityManager.EXTRA_NETWORK_INFO, mobileDisconnected);
		
		// Wifi connected
		Intent wifiActionConnect = new Intent(WifiManager.NETWORK_STATE_CHANGED_ACTION);
		wifiActionConnect.putExtra(WifiManager.EXTRA_NETWORK_INFO, wifiConnected);
		
		// CONNECTIVITY_ACTION Wifi connected 1st intent
		Intent connectivityActionWifiConnect1 = new Intent(ConnectivityManager.CONNECTIVITY_ACTION);
		connectivityActionWifiConnect1.putExtra(ConnectivityManager.EXTRA_NETWORK_INFO, wifiConnected);
		
		// CONNECTIVITY_ACTION Wifi connected 2nd intent - why two intents with similar NetworkInfo inside?
		Intent connectivityActionWifiConnect2 = new Intent(ConnectivityManager.CONNECTIVITY_ACTION);
		connectivityActionWifiConnect2.putExtra(ConnectivityManager.EXTRA_NETWORK_INFO, wifiConnected);
		
		TestContext testContext = new TestContext(getContext());
		
		receiver.onReceive(testContext, connectivityActionMobileDisconnect);
		receiver.onReceive(testContext, wifiActionConnect);
		receiver.onReceive(testContext, connectivityActionWifiConnect1);
		receiver.onReceive(testContext, connectivityActionWifiConnect2);
		
		assertEquals(1, testContext.getStartServiceCount());
		
		Intent startServiceIntent = testContext.getStartServiceIntent();
		assertNotNull(startServiceIntent);
		
		boolean connected = startServiceIntent.getBooleanExtra(ConnectivityActionReceiver.EXTRA_IS_WIFI_CONNECTED, false);
		assertTrue(connected);
		
	}
	
	public void testWifiDisconnected() throws Exception {
		
		PreferenceManager.getDefaultSharedPreferences(getContext()).edit().putBoolean("settings_enabled", true).commit();
		
		NetworkInfo wifiDisconnected = TestUtils.createNetworkInfo(ConnectivityManager.TYPE_WIFI, false);
		NetworkInfo mobileConnected = TestUtils.createNetworkInfo(ConnectivityManager.TYPE_MOBILE, true);
		
		// Wifi disconnected
		Intent connectivityActionWifiDisconnects = new Intent(ConnectivityManager.CONNECTIVITY_ACTION);
		connectivityActionWifiDisconnects.putExtra(ConnectivityManager.EXTRA_NETWORK_INFO, wifiDisconnected);
		
		// Mobile connected
		Intent connectivityActionMobileConnects = new Intent(ConnectivityManager.CONNECTIVITY_ACTION);
		connectivityActionMobileConnects.putExtra(ConnectivityManager.EXTRA_NETWORK_INFO, mobileConnected);
		
		TestContext testContext = new TestContext(getContext());
		
		receiver.onReceive(testContext, connectivityActionWifiDisconnects);
		receiver.onReceive(testContext, connectivityActionMobileConnects);
		
		assertEquals(1, testContext.getStartServiceCount());
		
		Intent startServiceIntent = testContext.getStartServiceIntent();
		assertNotNull(startServiceIntent);
		
		boolean connected = startServiceIntent.getBooleanExtra(ConnectivityActionReceiver.EXTRA_IS_WIFI_CONNECTED, true);
		assertFalse(connected);
		
	}
	
	public void testWifiDisconnectedSettingsDisabled() throws Exception {
		
		PreferenceManager.getDefaultSharedPreferences(getContext()).edit().putBoolean("settings_enabled", false).commit();
		
		NetworkInfo wifiDisconnected = TestUtils.createNetworkInfo(ConnectivityManager.TYPE_WIFI, false);
		NetworkInfo mobileConnected = TestUtils.createNetworkInfo(ConnectivityManager.TYPE_MOBILE, true);
		
		// Wifi disconnected
		Intent connectivityActionWifiDisconnects = new Intent(ConnectivityManager.CONNECTIVITY_ACTION);
		connectivityActionWifiDisconnects.putExtra(ConnectivityManager.EXTRA_NETWORK_INFO, wifiDisconnected);
		
		// Mobile connected
		Intent connectivityActionMobileConnects = new Intent(ConnectivityManager.CONNECTIVITY_ACTION);
		connectivityActionMobileConnects.putExtra(ConnectivityManager.EXTRA_NETWORK_INFO, mobileConnected);
		
		TestContext testContext = new TestContext(getContext());
		
		receiver.onReceive(testContext, connectivityActionWifiDisconnects);
		receiver.onReceive(testContext, connectivityActionMobileConnects);
		
		assertEquals(0, testContext.getStartServiceCount());
		
	}

}
