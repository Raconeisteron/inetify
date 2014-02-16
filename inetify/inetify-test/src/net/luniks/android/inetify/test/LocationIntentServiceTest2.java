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

import net.luniks.android.inetify.DatabaseAdapter;
import net.luniks.android.inetify.LocationIntentService;
import net.luniks.android.inetify.Notifier;
import net.luniks.android.inetify.Settings;
import net.luniks.android.inetify.WifiLocation;
import net.luniks.android.interfaces.IConnectivityManager;
import net.luniks.android.interfaces.IWifiManager;
import android.content.SharedPreferences;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.preference.PreferenceManager;
import android.test.AndroidTestCase;

public class LocationIntentServiceTest2 extends AndroidTestCase {
	
	private SharedPreferences sharedPreferences;
	
	public void setUp() throws Exception {
		super.setUp();
		
		sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this.getContext());
	}
	
	public void testNullLocation() throws Exception {
		
		LocationIntentService service = new LocationIntentService();
		
		// Avoid NPEs because onCreate() is not called
		TestUtils.setFieldValue(service, "databaseAdapter", new TestDatabaseAdapter());
		TestUtils.setFieldValue(service, "locater", new TestLocater());
		
		service.onLocationChanged(null);
	}
	
	public void testLocationNearNothingEnabled() throws Exception {
		
		sharedPreferences.edit().putBoolean(Settings.LOCATION_AUTO_WIFI, false).commit();
		sharedPreferences.edit().putBoolean(Settings.LOCATION_CHECK, false).commit();
		sharedPreferences.edit().putString(Settings.LOCATION_MAX_DISTANCE, "500").commit();
		
		LocationIntentService service = new LocationIntentService();
		
		WifiLocation nearestLocation = new WifiLocation();
		nearestLocation.setBSSID("TestBSSID");
		nearestLocation.setDistance(100);
		
		TestDatabaseAdapter databaseAdapter = new TestDatabaseAdapter();
		databaseAdapter.setNearestLocation(nearestLocation);
		
		TestNotifier notifier = new TestNotifier();
		
		TestWifiManager wifiManager = new TestWifiManager();
		wifiManager.setWifiState(WifiManager.WIFI_STATE_DISABLED);
		
		TestConnectivityManager connectivityManager = new TestConnectivityManager();
		TestNetworkInfo networkInfo = new TestNetworkInfo();
		networkInfo.setType(ConnectivityManager.TYPE_WIFI);
		networkInfo.setIsConnectedOrConnecting(false);
		connectivityManager.setNetworkInfo(networkInfo);
		
		setDependencies(service, sharedPreferences, databaseAdapter, notifier, wifiManager, connectivityManager);
		
		service.onLocationChanged(new Location("network"));
		
		assertEquals(0, notifier.getLocatifyCallCount());
		assertEquals(0, wifiManager.getSetWifiEnabledCallCount());
		assertEquals(WifiManager.WIFI_STATE_DISABLED, wifiManager.getWifiState());
	}
	
	public void testLocationNearBothEnabledWifiNotConnectedOrConnecting() throws Exception {
		
		sharedPreferences.edit().putBoolean(Settings.LOCATION_AUTO_WIFI, true).commit();
		sharedPreferences.edit().putBoolean(Settings.LOCATION_CHECK, true).commit();
		sharedPreferences.edit().putString(Settings.LOCATION_MAX_DISTANCE, "500").commit();
		
		LocationIntentService service = new LocationIntentService();
		
		WifiLocation nearestLocation = new WifiLocation();
		nearestLocation.setBSSID("TestBSSID");
		nearestLocation.setDistance(100);
		
		TestDatabaseAdapter databaseAdapter = new TestDatabaseAdapter();
		databaseAdapter.setNearestLocation(nearestLocation);
		
		TestNotifier notifier = new TestNotifier();
		
		TestWifiManager wifiManager = new TestWifiManager();
		wifiManager.setWifiState(WifiManager.WIFI_STATE_DISABLED);
		
		TestConnectivityManager connectivityManager = new TestConnectivityManager();
		TestNetworkInfo networkInfo = new TestNetworkInfo();
		networkInfo.setType(ConnectivityManager.TYPE_WIFI);
		networkInfo.setIsConnectedOrConnecting(false);
		connectivityManager.setNetworkInfo(networkInfo);
		
		setDependencies(service, sharedPreferences, databaseAdapter, notifier, wifiManager, connectivityManager);
		
		service.onLocationChanged(new Location("network"));
		
		assertEquals(1, notifier.getLocatifyCallCount());
		assertEquals(1, wifiManager.getSetWifiEnabledCallCount());
		assertEquals(WifiManager.WIFI_STATE_ENABLED, wifiManager.getWifiState());
	}
	
	public void testLocationNearBothEnabledWifiConnectedOrConnecting() throws Exception {
		
		sharedPreferences.edit().putBoolean(Settings.LOCATION_AUTO_WIFI, true).commit();
		sharedPreferences.edit().putBoolean(Settings.LOCATION_CHECK, true).commit();
		sharedPreferences.edit().putString(Settings.LOCATION_MAX_DISTANCE, "500").commit();

		LocationIntentService service = new LocationIntentService();
		
		WifiLocation nearestLocation = new WifiLocation();
		nearestLocation.setBSSID("TestBSSID");
		nearestLocation.setDistance(100);
		
		TestDatabaseAdapter databaseAdapter = new TestDatabaseAdapter();
		databaseAdapter.setNearestLocation(nearestLocation);
		
		TestNotifier notifier = new TestNotifier();
		
		TestWifiManager wifiManager = new TestWifiManager();
		wifiManager.setWifiState(WifiManager.WIFI_STATE_ENABLED);
		
		TestConnectivityManager connectivityManager = new TestConnectivityManager();
		TestNetworkInfo networkInfo = new TestNetworkInfo();
		networkInfo.setType(ConnectivityManager.TYPE_WIFI);
		networkInfo.setIsConnectedOrConnecting(true);
		connectivityManager.setNetworkInfo(networkInfo);
		
		setDependencies(service, sharedPreferences, databaseAdapter, notifier, wifiManager, connectivityManager);
		
		service.onLocationChanged(new Location("network"));
		
		// Wifi should not be enabled if it is already enabled
		assertEquals(0, notifier.getLocatifyCallCount());
		// Notification should not be issued if wifi is connected
		assertEquals(0, wifiManager.getSetWifiEnabledCallCount());
		assertEquals(WifiManager.WIFI_STATE_ENABLED, wifiManager.getWifiState());
	}
	
	public void testLocationFarNothingEnabledWifiDisabled() throws Exception {
		
		sharedPreferences.edit().putBoolean(Settings.LOCATION_AUTO_WIFI, false).commit();
		sharedPreferences.edit().putBoolean(Settings.LOCATION_CHECK, false).commit();
		sharedPreferences.edit().putString(Settings.LOCATION_MAX_DISTANCE, "500").commit();

		LocationIntentService service = new LocationIntentService();
		
		WifiLocation nearestLocation = new WifiLocation();
		nearestLocation.setBSSID("TestBSSID");
		nearestLocation.setDistance(1000);
		
		TestDatabaseAdapter databaseAdapter = new TestDatabaseAdapter();
		databaseAdapter.setNearestLocation(nearestLocation);
		
		TestNotifier notifier = new TestNotifier();
		
		TestWifiManager wifiManager = new TestWifiManager();
		wifiManager.setWifiState(WifiManager.WIFI_STATE_DISABLED);
		
		setDependencies(service, sharedPreferences, databaseAdapter, notifier, wifiManager, null);
		
		service.onLocationChanged(new Location("network"));
		
		assertEquals(1, notifier.getLocatifyCallCount());
		assertEquals(0, wifiManager.getSetWifiEnabledCallCount());
		assertEquals(WifiManager.WIFI_STATE_DISABLED, wifiManager.getWifiState());
	}
	
	public void testLocationFarBothEnabledWifiEnabling() throws Exception {
		
		sharedPreferences.edit().putBoolean(Settings.LOCATION_AUTO_WIFI, true).commit();
		sharedPreferences.edit().putBoolean(Settings.LOCATION_CHECK, true).commit();
		sharedPreferences.edit().putString(Settings.LOCATION_MAX_DISTANCE, "500").commit();

		LocationIntentService service = new LocationIntentService();
		
		WifiLocation nearestLocation = new WifiLocation();
		nearestLocation.setBSSID("TestBSSID");
		nearestLocation.setDistance(1000);
		
		TestDatabaseAdapter databaseAdapter = new TestDatabaseAdapter();
		databaseAdapter.setNearestLocation(nearestLocation);
		
		TestNotifier notifier = new TestNotifier();
		
		TestWifiManager wifiManager = new TestWifiManager();
		wifiManager.setWifiState(WifiManager.WIFI_STATE_ENABLING);
		
		setDependencies(service, sharedPreferences, databaseAdapter, notifier, wifiManager, null);
		
		service.onLocationChanged(new Location("network"));
		
		assertEquals(1, notifier.getLocatifyCallCount());
		assertEquals(0, wifiManager.getSetWifiEnabledCallCount());
		assertEquals(WifiManager.WIFI_STATE_ENABLING, wifiManager.getWifiState());
	}
	
	public void testLocationFarBothEnabledWifiConnectedOrConnecting() throws Exception {
		
		sharedPreferences.edit().putBoolean(Settings.LOCATION_AUTO_WIFI, true).commit();
		sharedPreferences.edit().putBoolean(Settings.LOCATION_CHECK, true).commit();
		sharedPreferences.edit().putString(Settings.LOCATION_MAX_DISTANCE, "500").commit();

		LocationIntentService service = new LocationIntentService();
		
		WifiLocation nearestLocation = new WifiLocation();
		nearestLocation.setBSSID("TestBSSID");
		nearestLocation.setDistance(1000);
		
		TestDatabaseAdapter databaseAdapter = new TestDatabaseAdapter();
		databaseAdapter.setNearestLocation(nearestLocation);
		
		TestNotifier notifier = new TestNotifier();
		
		TestWifiManager wifiManager = new TestWifiManager();
		wifiManager.setWifiState(WifiManager.WIFI_STATE_ENABLED);
		
		TestConnectivityManager connectivityManager = new TestConnectivityManager();
		TestNetworkInfo networkInfo = new TestNetworkInfo();
		networkInfo.setType(ConnectivityManager.TYPE_WIFI);
		networkInfo.setIsConnectedOrConnecting(true);
		connectivityManager.setNetworkInfo(networkInfo);
		
		setDependencies(service, sharedPreferences, databaseAdapter, notifier, wifiManager, connectivityManager);
		
		service.onLocationChanged(new Location("network"));
		
		assertEquals(1, notifier.getLocatifyCallCount());
		assertEquals(0, wifiManager.getSetWifiEnabledCallCount());
		assertEquals(WifiManager.WIFI_STATE_ENABLED, wifiManager.getWifiState());
	}
	
	public void testLocationFarBothEnabledMobileConnectedOrConnecting() throws Exception {
		
		sharedPreferences.edit().putBoolean(Settings.LOCATION_AUTO_WIFI, true).commit();
		sharedPreferences.edit().putBoolean(Settings.LOCATION_CHECK, true).commit();
		sharedPreferences.edit().putString(Settings.LOCATION_MAX_DISTANCE, "500").commit();

		LocationIntentService service = new LocationIntentService();
		
		WifiLocation nearestLocation = new WifiLocation();
		nearestLocation.setBSSID("TestBSSID");
		nearestLocation.setDistance(1000);
		
		TestDatabaseAdapter databaseAdapter = new TestDatabaseAdapter();
		databaseAdapter.setNearestLocation(nearestLocation);
		
		TestNotifier notifier = new TestNotifier();
		
		TestWifiManager wifiManager = new TestWifiManager();
		wifiManager.setWifiState(WifiManager.WIFI_STATE_ENABLED);
		
		TestConnectivityManager connectivityManager = new TestConnectivityManager();
		TestNetworkInfo networkInfo = new TestNetworkInfo();
		networkInfo.setType(ConnectivityManager.TYPE_MOBILE);
		networkInfo.setIsConnectedOrConnecting(true);
		connectivityManager.setNetworkInfo(networkInfo);
		
		setDependencies(service, sharedPreferences, databaseAdapter, notifier, wifiManager, connectivityManager);
		
		service.onLocationChanged(new Location("network"));
		
		assertEquals(1, notifier.getLocatifyCallCount());
		assertEquals(1, wifiManager.getSetWifiEnabledCallCount());
		assertEquals(WifiManager.WIFI_STATE_DISABLED, wifiManager.getWifiState());
	}
	
	public void testLocationFarBothEnabledWifiEnabledNotConnectedOrConnecting() throws Exception {
		
		sharedPreferences.edit().putBoolean(Settings.LOCATION_AUTO_WIFI, true).commit();
		sharedPreferences.edit().putBoolean(Settings.LOCATION_CHECK, true).commit();
		sharedPreferences.edit().putString(Settings.LOCATION_MAX_DISTANCE, "500").commit();

		LocationIntentService service = new LocationIntentService();
		
		WifiLocation nearestLocation = new WifiLocation();
		nearestLocation.setBSSID("TestBSSID");
		nearestLocation.setDistance(1000);
		
		TestDatabaseAdapter databaseAdapter = new TestDatabaseAdapter();
		databaseAdapter.setNearestLocation(nearestLocation);
		
		TestNotifier notifier = new TestNotifier();
		
		TestWifiManager wifiManager = new TestWifiManager();
		wifiManager.setWifiState(WifiManager.WIFI_STATE_ENABLED);
		
		TestConnectivityManager connectivityManager = new TestConnectivityManager();
		TestNetworkInfo networkInfo = new TestNetworkInfo();
		networkInfo.setType(ConnectivityManager.TYPE_WIFI);
		networkInfo.setIsConnectedOrConnecting(false);
		connectivityManager.setNetworkInfo(networkInfo);
		
		setDependencies(service, sharedPreferences, databaseAdapter, notifier, wifiManager, connectivityManager);
		
		service.onLocationChanged(new Location("network"));
		
		assertEquals(1, notifier.getLocatifyCallCount());
		assertEquals(1, wifiManager.getSetWifiEnabledCallCount());
		assertEquals(WifiManager.WIFI_STATE_DISABLED, wifiManager.getWifiState());
	}
	
	public void testNoDuplicateNotification() throws Exception {
		
		sharedPreferences.edit().putBoolean(Settings.LOCATION_AUTO_WIFI, true).commit();
		sharedPreferences.edit().putBoolean(Settings.LOCATION_CHECK, true).commit();
		sharedPreferences.edit().putString(Settings.LOCATION_MAX_DISTANCE, "500").commit();

		LocationIntentService service = new LocationIntentService();
		
		WifiLocation nearestLocation = new WifiLocation();
		nearestLocation.setBSSID("TestBSSID");
		nearestLocation.setDistance(100);
		
		TestDatabaseAdapter databaseAdapter = new TestDatabaseAdapter();
		databaseAdapter.setNearestLocation(nearestLocation);
		
		TestNotifier notifier = new TestNotifier();
		
		TestWifiManager wifiManager = new TestWifiManager();
		wifiManager.setWifiState(WifiManager.WIFI_STATE_DISABLED);
		
		TestConnectivityManager connectivityManager = new TestConnectivityManager();
		TestNetworkInfo networkInfo = new TestNetworkInfo();
		networkInfo.setType(ConnectivityManager.TYPE_WIFI);
		networkInfo.setIsConnectedOrConnecting(false);
		connectivityManager.setNetworkInfo(networkInfo);
		
		setDependencies(service, sharedPreferences, databaseAdapter, notifier, wifiManager, connectivityManager);
		
		service.onLocationChanged(new Location("network"));
		
		assertEquals(1, notifier.getLocatifyCallCount());
		assertEquals(1, wifiManager.getSetWifiEnabledCallCount());
		assertEquals(WifiManager.WIFI_STATE_ENABLED, wifiManager.getWifiState());
		
		// Wifi is connected
		networkInfo.setIsConnectedOrConnecting(true);
		
		service.onLocationChanged(new Location("network"));
		
		// Inetify should not issue another notification
		assertEquals(1, notifier.getLocatifyCallCount());
		
		WifiLocation nearestLocationTooFar = new WifiLocation();
		nearestLocationTooFar.setBSSID("TestBSSID");
		nearestLocationTooFar.setDistance(1000);
		
		databaseAdapter.setNearestLocation(nearestLocationTooFar);
		service.onLocationChanged(new Location("network"));
		
		// Existing notification is cleared when too far away from any wifi location
		assertEquals(2, notifier.getLocatifyCallCount());
		
		// Wifi disconnects
		networkInfo.setIsConnectedOrConnecting(false);
		
		databaseAdapter.setNearestLocation(nearestLocation);
		service.onLocationChanged(new Location("network"));
		
		// Wifi connects again
		networkInfo.setIsConnectedOrConnecting(true);

		assertEquals(3, notifier.getLocatifyCallCount());
	}
	
	public void testNotifyOtherLocation() throws Exception {
		
		sharedPreferences.edit().putBoolean(Settings.LOCATION_AUTO_WIFI, true).commit();
		sharedPreferences.edit().putBoolean(Settings.LOCATION_CHECK, true).commit();
		sharedPreferences.edit().putString(Settings.LOCATION_MAX_DISTANCE, "500").commit();

		LocationIntentService service = new LocationIntentService();
		
		WifiLocation nearestLocation = new WifiLocation();
		nearestLocation.setBSSID("TestBSSID");
		nearestLocation.setDistance(100);
		
		TestDatabaseAdapter databaseAdapter = new TestDatabaseAdapter();
		databaseAdapter.setNearestLocation(nearestLocation);
		
		TestNotifier notifier = new TestNotifier();
		
		TestWifiManager wifiManager = new TestWifiManager();
		wifiManager.setWifiState(WifiManager.WIFI_STATE_DISABLED);
		
		TestConnectivityManager connectivityManager = new TestConnectivityManager();
		TestNetworkInfo networkInfo = new TestNetworkInfo();
		networkInfo.setType(ConnectivityManager.TYPE_WIFI);
		networkInfo.setIsConnectedOrConnecting(false);
		connectivityManager.setNetworkInfo(networkInfo);
		
		setDependencies(service, sharedPreferences, databaseAdapter, notifier, wifiManager, connectivityManager);
		
		service.onLocationChanged(new Location("network"));
		
		assertEquals(1, notifier.getLocatifyCallCount());
		assertEquals(1, wifiManager.getSetWifiEnabledCallCount());
		assertEquals(WifiManager.WIFI_STATE_ENABLED, wifiManager.getWifiState());
		
		WifiLocation otherNearestLocation = new WifiLocation();
		otherNearestLocation.setBSSID("OtherTestBSSID");
		otherNearestLocation.setDistance(100);
		
		databaseAdapter.setNearestLocation(otherNearestLocation);
		
		service.onLocationChanged(new Location("network"));
		
		// Wifi should not be enabled if it is already enabled
		assertEquals(1, wifiManager.getSetWifiEnabledCallCount());
		// Notification should be issued if wifi is enabled but not connected
		assertEquals(2, notifier.getLocatifyCallCount());
	}
	
	public void testDeactivateWifiOnlyOnce() throws Exception {
		
		sharedPreferences.edit().putBoolean(Settings.LOCATION_AUTO_WIFI, true).commit();
		sharedPreferences.edit().putBoolean(Settings.LOCATION_CHECK, true).commit();
		sharedPreferences.edit().putString(Settings.LOCATION_MAX_DISTANCE, "500").commit();

		LocationIntentService service = new LocationIntentService();
		
		WifiLocation nearestLocation = new WifiLocation();
		nearestLocation.setBSSID("TestBSSID");
		nearestLocation.setDistance(1000);
		
		TestDatabaseAdapter databaseAdapter = new TestDatabaseAdapter();
		databaseAdapter.setNearestLocation(nearestLocation);
		
		TestNotifier notifier = new TestNotifier();
		
		TestWifiManager wifiManager = new TestWifiManager();
		wifiManager.setWifiState(WifiManager.WIFI_STATE_ENABLED);
		
		TestConnectivityManager connectivityManager = new TestConnectivityManager();
		TestNetworkInfo networkInfo = new TestNetworkInfo();
		networkInfo.setType(ConnectivityManager.TYPE_WIFI);
		networkInfo.setIsConnectedOrConnecting(false);
		connectivityManager.setNetworkInfo(networkInfo);
		
		setDependencies(service, sharedPreferences, databaseAdapter, notifier, wifiManager, connectivityManager);
		
		service.onLocationChanged(new Location("network"));
		
		assertEquals(1, notifier.getLocatifyCallCount());
		assertEquals(1, wifiManager.getSetWifiEnabledCallCount());
		assertEquals(WifiManager.WIFI_STATE_DISABLED, wifiManager.getWifiState());
		
		// User enables Wifi after Inetify disabled it
		wifiManager.setWifiState(WifiManager.WIFI_STATE_ENABLED);
		
		// Wifi is connnecting or connected
		networkInfo.setIsConnectedOrConnecting(true);
		
		service.onLocationChanged(new Location("network"));
		
		// Inetify should only disable Wifi again if it is not connecting or connected
		assertEquals(2, notifier.getLocatifyCallCount());
		assertEquals(1, wifiManager.getSetWifiEnabledCallCount());
		assertEquals(WifiManager.WIFI_STATE_ENABLED, wifiManager.getWifiState());
		
		WifiLocation otherNearestLocation = new WifiLocation();
		otherNearestLocation.setBSSID("OtherTestBSSID");
		otherNearestLocation.setDistance(100);
		
		databaseAdapter.setNearestLocation(otherNearestLocation);
		
		service.onLocationChanged(new Location("network"));

		// Wifi should not be enabled and no notification issued if it is already enabled
		assertEquals(2, notifier.getLocatifyCallCount());
		assertEquals(1, wifiManager.getSetWifiEnabledCallCount());
		assertEquals(WifiManager.WIFI_STATE_ENABLED, wifiManager.getWifiState());
		
		// Wifi is disconnected
		networkInfo.setIsConnectedOrConnecting(false);
		
		databaseAdapter.setNearestLocation(nearestLocation);
		
		service.onLocationChanged(new Location("network"));
		
		// Inetify should again disable Wifi
		assertEquals(3, notifier.getLocatifyCallCount());
		assertEquals(2, wifiManager.getSetWifiEnabledCallCount());
		assertEquals(WifiManager.WIFI_STATE_DISABLED, wifiManager.getWifiState());
	}
	
	private void setDependencies(final LocationIntentService service,
			final SharedPreferences sharedPreferences,
			final DatabaseAdapter databaseAdapter,
			final Notifier notifier, 
			final IWifiManager wifiManager,
			final IConnectivityManager connectivityManager) throws Exception {
		TestUtils.setFieldValue(service, "sharedPreferences", sharedPreferences);
		TestUtils.setFieldValue(service, "databaseAdapter", databaseAdapter);
		TestUtils.setFieldValue(service, "notifier", notifier);
		TestUtils.setFieldValue(service, "wifiManager", wifiManager);
		TestUtils.setFieldValue(service, "connectivityManager", connectivityManager);
		TestUtils.setFieldValue(service, "locater", new TestLocater());
	}

}
