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
import net.luniks.android.inetify.Locater;
import net.luniks.android.inetify.LocationIntentService;
import net.luniks.android.inetify.Settings;
import net.luniks.android.interfaces.ILocationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Looper;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.preference.PreferenceManager;
import android.test.ServiceTestCase;

public class LocationIntentServiceTest extends ServiceTestCase<LocationIntentService> {
	
	private SharedPreferences sharedPreferences;
	
	public LocationIntentServiceTest() {
		super(LocationIntentService.class);
	}
	
	public void setUp() throws Exception {
		super.setUp();
		
		sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this.getContext());
	}
	
	public void testProviderEnabledHasLocationsNullIntent() throws Exception {
		
		Intent serviceIntent = new Intent(this.getContext(), LocationIntentService.class);
		
		this.setupService();
		LocationIntentService serviceToTest = getService();
		
		// At least one location provider enabled
		TestLocationManager locationManager = new TestLocationManager();
		locationManager.setAllProvidersEnabled(true);
		
		// At least one location in database
		DatabaseAdapter databaseAdapter = new TestDatabaseAdapter();
		databaseAdapter.addLocation("TestBSSID", "TestSSID", "TestName", new Location(Locater.PROVIDER_DATABASE));
		
		TestLocater locater = new TestLocater();
		
		setDependencies(serviceToTest, locationManager, databaseAdapter, locater);
		
		new ServiceStarter(null).start();
		
		acquireWakeLock();
		
		Thread.sleep(500);
		
		// When receiving a null intent, the service should do nothing and stop itself
		assertFalse(locater.wasStarted());
		
		TestUtils.waitForStaticFieldNull(LocationIntentService.class, "wakeLock", 1000);
		
		assertFalse(this.getService().stopService(serviceIntent));
	}
	
	public void testNoProviderEnabledHasLocationsIntent() throws Exception {
		
		Intent serviceIntent = new Intent(this.getContext(), LocationIntentService.class);
		
		this.setupService();
		LocationIntentService serviceToTest = getService();
		
		// No location provider enabled
		TestLocationManager locationManager = new TestLocationManager();
		locationManager.setAllProvidersEnabled(false);
		
		// At least one location in database
		DatabaseAdapter databaseAdapter = new TestDatabaseAdapter();
		databaseAdapter.addLocation("TestBSSID", "TestSSID", "TestName", new Location(Locater.PROVIDER_DATABASE));
		
		TestLocater locater = new TestLocater();
		
		setDependencies(serviceToTest, locationManager, databaseAdapter, locater);
		
		new ServiceStarter(serviceIntent).start();
		
		acquireWakeLock();
		
		Thread.sleep(500);
		
		// When no location provider is enabled, the service should do nothing and stop itself
		assertFalse(locater.wasStarted());
		
		TestUtils.waitForStaticFieldNull(LocationIntentService.class, "wakeLock", 1000);
		
		assertFalse(this.getService().stopService(serviceIntent));
	}
	
	public void testProviderEnabledHasNoLocationsIntent() throws Exception {
		
		Intent serviceIntent = new Intent(this.getContext(), LocationIntentService.class);
		
		this.setupService();
		LocationIntentService serviceToTest = getService();
		
		// At least one location provider enabled
		TestLocationManager locationManager = new TestLocationManager();
		locationManager.setAllProvidersEnabled(true);
		
		// No location in the database
		TestDatabaseAdapter databaseAdapter = new TestDatabaseAdapter();
		databaseAdapter.clearLocations();
		
		TestLocater locater = new TestLocater();
		
		setDependencies(serviceToTest, locationManager, databaseAdapter, locater);
		
		acquireWakeLock();
		
		new ServiceStarter(serviceIntent).start();
		
		Thread.sleep(500);
		
		// When no location is in the database, the service should do nothing and stop itself
		assertFalse(locater.wasStarted());
		
		TestUtils.waitForStaticFieldNull(LocationIntentService.class, "wakeLock", 1000);
		
		assertFalse(this.getService().stopService(serviceIntent));
	}
	
	public void testNoLocationNotUseGPS() throws Exception {
		
		sharedPreferences.edit().putBoolean(Settings.LOCATION_USE_GPS, false).commit();
		
		Intent serviceIntent = new Intent(this.getContext(), LocationIntentService.class);
		
		this.setupService();
		LocationIntentService serviceToTest = getService();
		
		// At least one location provider enabled
		TestLocationManager locationManager = new TestLocationManager();
		locationManager.setAllProvidersEnabled(true);
		
		// At least one location in the database
		TestDatabaseAdapter databaseAdapter = new TestDatabaseAdapter();
		databaseAdapter.addLocation("TestBSSID", "TestSSID", "TestName", new Location(Locater.PROVIDER_DATABASE));
		
		final TestLocater locater = new TestLocater();
		
		setDependencies(serviceToTest, locationManager, databaseAdapter, locater);
		setGetLocationTimeout(100);
		
		acquireWakeLock();
		
		new ServiceStarter(serviceIntent).start();
		
		// Service should start the locater twice with 100 millisecond timeout each
		Thread.sleep(500);
		
		// After the timeout, the locater should have been stopped
		assertFalse(locater.isRunning());
		
		assertEquals(2, locater.getCallsToStart().size());
		
		assertEquals(60 * 1000, locater.getCallsToStart().get(0).getMaxAge());
		assertEquals(100, locater.getCallsToStart().get(0).getMinAccuracy());
		assertEquals(false, locater.getCallsToStart().get(0).isUseGPS());
		
		assertEquals(60 * 1000, locater.getCallsToStart().get(1).getMaxAge());
		assertEquals(5000, locater.getCallsToStart().get(1).getMinAccuracy());
		assertEquals(false, locater.getCallsToStart().get(1).isUseGPS());
		
		TestUtils.waitForStaticFieldNull(LocationIntentService.class, "wakeLock", 1000);
		
		assertFalse(this.getService().stopService(serviceIntent));
	}
	
	public void testNoLocationUseGPS() throws Exception {
		
		sharedPreferences.edit().putBoolean(Settings.LOCATION_USE_GPS, true).commit();
		
		Intent serviceIntent = new Intent(this.getContext(), LocationIntentService.class);
		
		this.setupService();
		LocationIntentService serviceToTest = getService();
		
		// At least one location provider enabled
		TestLocationManager locationManager = new TestLocationManager();
		locationManager.setAllProvidersEnabled(true);
		
		// At least one location in the database
		TestDatabaseAdapter databaseAdapter = new TestDatabaseAdapter();
		databaseAdapter.addLocation("TestBSSID", "TestSSID", "TestName", new Location(Locater.PROVIDER_DATABASE));
		
		final TestLocater locater = new TestLocater();
		
		setDependencies(serviceToTest, locationManager, databaseAdapter, locater);
		setGetLocationTimeout(100);
		
		acquireWakeLock();
		
		new ServiceStarter(serviceIntent).start();
		
		// Service should start the locater twice with 100 millisecond timeout each
		Thread.sleep(500);
		
		// After the timeout, the locater should have been stopped
		assertFalse(locater.isRunning());
		
		assertEquals(2, locater.getCallsToStart().size());
		
		// Even if GPS is enabled, the service should first see if it can get
		// an accurate location from the network (probably succeeds only if Wifi is enabled)
		assertEquals(60 * 1000, locater.getCallsToStart().get(0).getMaxAge());
		assertEquals(100, locater.getCallsToStart().get(0).getMinAccuracy());
		assertEquals(false, locater.getCallsToStart().get(0).isUseGPS());
		
		// If GPS is used, the service should not accept locations with coarse accuracy
		// from the network when GPS does not find a location because if it does, 
		// it might consider a Wifi location as near that it didn't when it found 
		// accurate locations using GPS 
		assertEquals(60 * 1000, locater.getCallsToStart().get(1).getMaxAge());
		assertEquals(100, locater.getCallsToStart().get(1).getMinAccuracy());
		assertEquals(true, locater.getCallsToStart().get(1).isUseGPS());
		
		TestUtils.waitForStaticFieldNull(LocationIntentService.class, "wakeLock", 1000);
		
		assertFalse(this.getService().stopService(serviceIntent));
	}
	
	public void testFineLocation() throws Exception {
		
		sharedPreferences.edit().putBoolean(Settings.LOCATION_USE_GPS, false).commit();
		
		Intent serviceIntent = new Intent(this.getContext(), LocationIntentService.class);
		
		this.setupService();
		LocationIntentService serviceToTest = getService();
		
		// At least one location provider enabled
		TestLocationManager locationManager = new TestLocationManager();
		locationManager.setAllProvidersEnabled(true);
		
		// At least one location in the database
		TestDatabaseAdapter databaseAdapter = new TestDatabaseAdapter();
		databaseAdapter.addLocation("TestBSSID", "TestSSID", "TestName", new Location(Locater.PROVIDER_DATABASE));
		// Service should not fail if nearest location is null
		databaseAdapter.setNearestLocation(null);
		
		final TestLocater locater = new TestLocater();
		
		setDependencies(serviceToTest, locationManager, databaseAdapter, locater);
		setGetLocationTimeout(100);
		
		acquireWakeLock();
		
		new ServiceStarter(serviceIntent).start();
		
		// Wait for the locater to be started for the first time
		while(! locater.wasStarted()) {
			Thread.sleep(10);
		}
		
		// Pass a location with an accuracy better than 100 m
		Location location = new Location("network");
		location.setAccuracy(33);
		serviceToTest.onLocationChanged(location);
		
		// Service should stop the locater and not start it for the second run
		Thread.sleep(500);
		
		// After the timeout, the locater should have been stopped
		assertFalse(locater.isRunning());
		
		assertEquals(1, locater.getCallsToStart().size());
		
		assertEquals(60 * 1000, locater.getCallsToStart().get(0).getMaxAge());
		assertEquals(100, locater.getCallsToStart().get(0).getMinAccuracy());
		assertEquals(false, locater.getCallsToStart().get(0).isUseGPS());
		
		TestUtils.waitForStaticFieldNull(LocationIntentService.class, "wakeLock", 1000);
		
		assertFalse(this.getService().stopService(serviceIntent));
	}
	
	public void testRunOnlyOnceReleaseWakeLock() throws Exception {
		
		sharedPreferences.edit().putBoolean(Settings.LOCATION_USE_GPS, false).commit();
		
		Intent serviceIntent = new Intent(this.getContext(), LocationIntentService.class);
		
		this.setupService();
		LocationIntentService serviceToTest = getService();
		
		// At least one location provider enabled
		TestLocationManager locationManager = new TestLocationManager();
		locationManager.setAllProvidersEnabled(true);
		
		// At least one location in the database
		TestDatabaseAdapter databaseAdapter = new TestDatabaseAdapter();
		databaseAdapter.addLocation("TestBSSID", "TestSSID", "TestName", new Location(Locater.PROVIDER_DATABASE));
		
		final TestLocater locater = new TestLocater();
		
		setDependencies(serviceToTest, locationManager, databaseAdapter, locater);
		setGetLocationTimeout(100);
		
		acquireWakeLock();
		
		new ServiceStarter(serviceIntent).start();
		
		// Service should start the locater twice with 100 millisecond timeout each
		Thread.sleep(500);
		
		// After the timeout, the locater should have been stopped
		assertFalse(locater.isRunning());
		
		assertEquals(2, locater.getCallsToStart().size());
		
		assertEquals(60 * 1000, locater.getCallsToStart().get(0).getMaxAge());
		assertEquals(100, locater.getCallsToStart().get(0).getMinAccuracy());
		assertEquals(false, locater.getCallsToStart().get(0).isUseGPS());
		
		assertEquals(60 * 1000, locater.getCallsToStart().get(1).getMaxAge());
		assertEquals(5000, locater.getCallsToStart().get(1).getMinAccuracy());
		assertEquals(false, locater.getCallsToStart().get(1).isUseGPS());
		
		// Send a second intent to the service - it should be ignored
		serviceToTest.onStartCommand(serviceIntent, 0, 0);
		
		assertFalse(locater.isRunning());
		
		assertEquals(2, locater.getCallsToStart().size());
		
		TestUtils.waitForStaticFieldNull(LocationIntentService.class, "wakeLock", 1000);
		
		assertFalse(this.getService().stopService(serviceIntent));
	}
	
	private void acquireWakeLock() throws Exception {
		PowerManager powerManager = (PowerManager)this.getContext().getSystemService(Context.POWER_SERVICE);
		WakeLock wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, 
				LocationIntentService.WAKE_LOCK_TAG);
		wakeLock.acquire();
		TestUtils.setStaticFieldValue(LocationIntentService.class, "wakeLock", wakeLock);
	}
	
	private void setDependencies(final LocationIntentService service, 
			final ILocationManager locationManager, 
			final DatabaseAdapter databaseAdapter, 
			final Locater locater) throws Exception {
		TestUtils.setFieldValue(service, "locationManager", locationManager);
		TestUtils.setFieldValue(service, "databaseAdapter", databaseAdapter);
		TestUtils.setFieldValue(service, "locater", locater);
	}
	
	private void setGetLocationTimeout(final long timeout) throws Exception {
		TestUtils.setStaticFieldValue(LocationIntentService.class, "GET_LOCATION_TIMEOUT", timeout);
		TestUtils.setStaticFieldValue(LocationIntentService.class, "GET_LOCATION_TIMEOUT_GPS", timeout);
	}
	
	/*
	 * It is necessary to start the service on another thread than the test's thread,
	 * because the locater inside it is started on that thread as well - so they would
	 * block each other. 
	 */
	private class ServiceStarter extends Thread {
		
		private Intent serviceIntent;
		
		public ServiceStarter(final Intent serviceIntent) {
			this.serviceIntent = serviceIntent;
		}
		
		public void run() {
			Looper.prepare();
			LocationIntentServiceTest.this.startService(serviceIntent);
			Looper.loop();
		}
	}

}
