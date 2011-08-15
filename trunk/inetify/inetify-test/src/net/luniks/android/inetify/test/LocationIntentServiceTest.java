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
import net.luniks.android.inetify.Locater;
import net.luniks.android.inetify.LocationIntentService;
import net.luniks.android.interfaces.ILocationManager;
import android.content.Intent;
import android.location.Location;
import android.test.ServiceTestCase;

public class LocationIntentServiceTest extends ServiceTestCase<LocationIntentService> {
	
	public LocationIntentServiceTest() {
		super(LocationIntentService.class);
	}
	
	public void testProviderEnabledHasLocationsNullIntent() throws Exception {
		
		Intent serviceIntent = new Intent("net.luniks.android.inetify.InetifyTestService");
		serviceIntent.putExtra(ConnectivityActionReceiver.EXTRA_IS_WIFI_CONNECTED, true);
		
		this.setupService();
		LocationIntentService serviceToTest = getService();
		
		// At least one location provider enabled
		TestLocationManager locationManager = new TestLocationManager();
		locationManager.setAllProvidersEnabled(true);
		
		// At least one location in database
		DatabaseAdapter databaseAdapter = new TestDatabaseAdapter();
		databaseAdapter.addLocation("TestBSSID", "TestSSID", "TestName", new Location("network"));
		
		TestLocater locater = new TestLocater();
		
		setDependencies(serviceToTest, locationManager, databaseAdapter, locater);
		
		this.startService(null);
		
		Thread.sleep(1000);
		
		// When receiving a null intent, the service should do nothing and stop itself
		assertFalse(locater.wasStarted());
		
		assertFalse(this.getService().stopService(serviceIntent));
	}
	
	public void testNoProviderEnabledHasLocationsIntent() throws Exception {
		
		Intent serviceIntent = new Intent("net.luniks.android.inetify.InetifyTestService");
		serviceIntent.putExtra(ConnectivityActionReceiver.EXTRA_IS_WIFI_CONNECTED, true);
		
		this.setupService();
		LocationIntentService serviceToTest = getService();
		
		// No location provider enabled
		TestLocationManager locationManager = new TestLocationManager();
		locationManager.setAllProvidersEnabled(false);
		
		// At least one location in database
		DatabaseAdapter databaseAdapter = new TestDatabaseAdapter();
		databaseAdapter.addLocation("TestBSSID", "TestSSID", "TestName", new Location("network"));
		
		TestLocater locater = new TestLocater();
		
		setDependencies(serviceToTest, locationManager, databaseAdapter, locater);
		
		this.startService(new Intent(serviceToTest, LocationIntentService.class));
		
		Thread.sleep(1000);
		
		// When no location provider is enabled, the service should do nothing and stop itself
		assertFalse(locater.wasStarted());
		
		assertFalse(this.getService().stopService(serviceIntent));
	}
	
	public void testProviderEnabledHasNoLocationsIntent() throws Exception {
		
		Intent serviceIntent = new Intent("net.luniks.android.inetify.InetifyTestService");
		serviceIntent.putExtra(ConnectivityActionReceiver.EXTRA_IS_WIFI_CONNECTED, true);
		
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
		
		this.startService(new Intent(serviceToTest, LocationIntentService.class));
		
		Thread.sleep(1000);
		
		// When no location is in the database, the service should do nothing and stop itself
		assertFalse(locater.wasStarted());
		
		assertFalse(this.getService().stopService(serviceIntent));
	}
	
	private void setDependencies(final LocationIntentService service, 
			final ILocationManager locationManager, 
			final DatabaseAdapter databaseAdapter, 
			final Locater locater) throws Exception {
		TestUtils.setFieldValue(service, "locationManager", locationManager);
		TestUtils.setFieldValue(service, "databaseAdapter", databaseAdapter);
		TestUtils.setFieldValue(service, "locater", locater);
	}

}
