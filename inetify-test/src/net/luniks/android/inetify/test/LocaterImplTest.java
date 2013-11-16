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

import java.util.Vector;

import net.luniks.android.inetify.Locater;
import net.luniks.android.inetify.Locater.LocaterLocationListener;
import net.luniks.android.inetify.LocaterImpl;
import net.luniks.android.test.mock.LocationManagerMock;
import android.location.Location;
import android.location.LocationManager;
import android.test.AndroidTestCase;

// TODO Test new methods
public class LocaterImplTest extends AndroidTestCase {
	
	private static final long MAX_AGE = 60 * 1000;
	
	public void testGetBestLastKnownLocationAll() {
		
		LocationManagerMock locationManager = new LocationManagerMock(false);
		
		long time = System.currentTimeMillis();
		
		Location mostAccurateMostRecent = new Location("A");
		mostAccurateMostRecent.setTime(time);
		mostAccurateMostRecent.setAccuracy(10);
		locationManager.addLastKnownLocation("A", mostAccurateMostRecent);
		
		Location mostAccurateLessRecent = new Location("B");
		mostAccurateLessRecent.setTime(time - 30 * 1000);
		mostAccurateLessRecent.setAccuracy(10);
		locationManager.addLastKnownLocation("B", mostAccurateLessRecent);
		
		Location lessAccurateMostRecent = new Location("C");
		lessAccurateMostRecent.setTime(time);
		lessAccurateMostRecent.setAccuracy(20);
		locationManager.addLastKnownLocation("C", lessAccurateMostRecent);
		
		Location lessAccuratelessRecent = new Location("D");
		lessAccuratelessRecent.setTime(time - 30 * 1000);
		lessAccuratelessRecent.setAccuracy(20);
		locationManager.addLastKnownLocation("D", lessAccuratelessRecent);
		
		Location mostAccurateTooOld = new Location("E");
		mostAccurateTooOld.setTime(time - 2 * MAX_AGE);
		mostAccurateTooOld.setAccuracy(10);
		locationManager.addLastKnownLocation("E", mostAccurateTooOld);
		
		final LocaterImpl locater = new LocaterImpl(locationManager);
		
		for(int i = 0; i < 1000; i++) {
			Location bestLastKnownLocation = locater.getBestLastKnownLocation(MAX_AGE);
			
			assertEquals(mostAccurateMostRecent.getProvider(), bestLastKnownLocation.getProvider());
		}
		
	}
	
	public void testGetBestLastKnownLocationSameAccuracy() {
		
		LocationManagerMock locationManager = new LocationManagerMock(false);
		
		long time = System.currentTimeMillis();
		
		Location mostAccurateMostRecent = new Location("A");
		mostAccurateMostRecent.setTime(time);
		mostAccurateMostRecent.setAccuracy(10);
		locationManager.addLastKnownLocation("A", mostAccurateMostRecent);
		
		Location mostAccurateLessRecent = new Location("B");
		mostAccurateLessRecent.setTime(time - 30 * 1000);
		mostAccurateLessRecent.setAccuracy(10);
		locationManager.addLastKnownLocation("B", mostAccurateLessRecent);
		
		final LocaterImpl locater = new LocaterImpl(locationManager);
		
		for(int i = 0; i < 1000; i++) {
			Location bestLastKnownLocation = locater.getBestLastKnownLocation(MAX_AGE);
			
			assertEquals(mostAccurateMostRecent.getProvider(), bestLastKnownLocation.getProvider());
		}
		
	}
	
	public void testGetBestLastKnownLocationSameTime() {
		
		LocationManagerMock locationManager = new LocationManagerMock(false);
		
		long time = System.currentTimeMillis();
		
		Location mostAccurateMostRecent = new Location("A");
		mostAccurateMostRecent.setTime(time);
		mostAccurateMostRecent.setAccuracy(10);
		locationManager.addLastKnownLocation("A", mostAccurateMostRecent);
		
		Location lessAccurateMostRecent = new Location("B");
		lessAccurateMostRecent.setTime(time);
		lessAccurateMostRecent.setAccuracy(20);
		locationManager.addLastKnownLocation("B", lessAccurateMostRecent);
		
		final LocaterImpl locater = new LocaterImpl(locationManager);
		
		for(int i = 0; i < 1000; i++) {
			Location bestLastKnownLocation = locater.getBestLastKnownLocation(MAX_AGE);
			
			assertEquals(mostAccurateMostRecent.getProvider(), bestLastKnownLocation.getProvider());
		}
		
	}

	public void testGetBestLastKnownTooOld() {
		
		LocationManagerMock locationManager = new LocationManagerMock(false);
		
		long time = System.currentTimeMillis();
		
		Location accurateRecent = new Location("A");
		accurateRecent.setTime(time);
		accurateRecent.setAccuracy(10);
		locationManager.addLastKnownLocation("A", accurateRecent);
		
		Location accurateTooOld = new Location("B");
		accurateTooOld.setTime(time - 2000);
		accurateTooOld.setAccuracy(10);
		locationManager.addLastKnownLocation("B", accurateTooOld);
		
		Location accurateEvenOlder = new Location("C");
		accurateEvenOlder.setTime(time - 3000);
		accurateEvenOlder.setAccuracy(20);
		locationManager.addLastKnownLocation("C", accurateEvenOlder);
		
		final LocaterImpl locater = new LocaterImpl(locationManager);
		
		for(int i = 0; i < 1000; i++) {
			Location bestLastKnownLocation = locater.getBestLastKnownLocation(1000);
			
			assertEquals(accurateRecent.getProvider(), bestLastKnownLocation.getProvider());
		}
		
	}
	
	public void testLocaterLastKnownRecentAndAccurate() {
		
		final Vector<Location> locations = new Vector<Location>();
		
		LocationManagerMock locationManager = new LocationManagerMock(false);
		
		long time = System.currentTimeMillis();
		
		Location recentAndAccurate = new Location("A");
		recentAndAccurate.setTime(time - 55 * 1000);
		recentAndAccurate.setAccuracy(10);
		locationManager.addLastKnownLocation("A", recentAndAccurate);
		
		final LocaterLocationListener listener = new LocaterLocationListener() {
			
			public void onLocationChanged(Location location) {
				locations.add(location);
			}
		};
		
		final Locater locater = new LocaterImpl(locationManager);
		
		locater.start(listener, 60 * 1000, 100, false);
		
		assertEquals(1, locations.size());
		assertFalse(locationManager.areListenersRegistered());
	}
	
	public void testLocaterLastKnownTooOld() {
		
		final Vector<Location> locations = new Vector<Location>();
		
		LocationManagerMock locationManager = new LocationManagerMock(false);
		
		long time = System.currentTimeMillis();
		
		Location tooOld = new Location("A");
		tooOld.setTime(time - 65 * 1000);
		tooOld.setAccuracy(10);
		locationManager.addLastKnownLocation("A", tooOld);
		
		final LocaterLocationListener listener = new LocaterLocationListener() {
			
			public void onLocationChanged(Location location) {
				locations.add(location);
			}
		};
		
		final Locater locater = new LocaterImpl(locationManager);
		
		locater.start(listener, 60 * 1000, 100, false);
		
		assertEquals(0, locations.size());
		assertTrue(locationManager.areListenersRegistered());
		
		locater.stop();
		
		assertFalse(locationManager.areListenersRegistered());
	}
	
	public void testLocaterLastKnownNotAccurate() {
		
		final Vector<Location> locations = new Vector<Location>();
		
		LocationManagerMock locationManager = new LocationManagerMock(false);
		
		long time = System.currentTimeMillis();
		
		Location recent = new Location("A");
		recent.setTime(time - 55 * 1000);
		recent.setAccuracy(110);
		locationManager.addLastKnownLocation("A", recent);
		
		final LocaterLocationListener listener = new LocaterLocationListener() {
			
			public void onLocationChanged(Location location) {
				locations.add(location);
			}
		};
		
		final LocaterImpl locater = new LocaterImpl(locationManager);
		
		locater.start(listener, 60 * 1000, 100, false);
		
		assertEquals(0, locations.size());
		assertTrue(locationManager.areListenersRegistered());
		
		locater.stop();
		
		assertFalse(locationManager.areListenersRegistered());
	}
	
	public void testLocaterUpdateLocationRecentAndAccurate() {
		
		final Vector<Location> locations = new Vector<Location>();
		
		LocationManagerMock locationManager = new LocationManagerMock(true);
		
		long time = System.currentTimeMillis();
		
		final LocaterLocationListener listener = new LocaterLocationListener() {
			
			public void onLocationChanged(Location location) {
				locations.add(location);
			}
		};
		
		final Locater locater = new LocaterImpl(locationManager);
		
		locater.start(listener, 60 * 1000, 100, false);
		
		Location recentAndAccurate = new Location(LocationManager.NETWORK_PROVIDER);
		recentAndAccurate.setTime(time - 55 * 1000);
		recentAndAccurate.setAccuracy(10);
		locationManager.updateLocation(recentAndAccurate);
		
		assertEquals(1, locations.size());
		assertTrue(locationManager.areListenersRegistered());
		
		locater.stop();
		
		assertFalse(locationManager.areListenersRegistered());
	}
	
	// Locations passed by the LocationManager to onLocationChanged should always be current ones
	public void ignoreTestLocaterUpdateLocationTooOld() {
		
		final Vector<Location> locations = new Vector<Location>();
		
		LocationManagerMock locationManager = new LocationManagerMock(true);
		
		long time = System.currentTimeMillis();
		
		final LocaterLocationListener listener = new LocaterLocationListener() {
			
			public void onLocationChanged(Location location) {
				locations.add(location);
			}
		};
		
		final Locater locater = new LocaterImpl(locationManager);
		
		locater.start(listener, 60 * 1000, 100, false);
		
		Location tooOld = new Location(LocationManager.NETWORK_PROVIDER);
		tooOld.setTime(time - 65 * 1000);
		tooOld.setAccuracy(10);
		locationManager.updateLocation(tooOld);
		
		assertEquals(0, locations.size());
		assertTrue(locationManager.areListenersRegistered());
		
		locater.stop();
		
		assertFalse(locationManager.areListenersRegistered());
	}
	
	public void testLocaterUpdateLocationNotAccurate() {
		
		final Vector<Location> locations = new Vector<Location>();
		
		LocationManagerMock locationManager = new LocationManagerMock(true);
		
		long time = System.currentTimeMillis();
		
		final LocaterLocationListener listener = new LocaterLocationListener() {
			
			public void onLocationChanged(Location location) {
				locations.add(location);
			}
		};
		
		final LocaterImpl locater = new LocaterImpl(locationManager);
		
		locater.start(listener, 60 * 1000, 100, false);
		
		Location notAccurate = new Location(LocationManager.NETWORK_PROVIDER);
		notAccurate.setTime(time - 55 * 1000);
		notAccurate.setAccuracy(110);
		locationManager.updateLocation(notAccurate);
		
		assertEquals(0, locations.size());
		assertTrue(locationManager.areListenersRegistered());
		
		locater.stop();
		
		assertFalse(locationManager.areListenersRegistered());
	}
	
	public void testLocaterUpdateLocationKeepRunning() {
		
		final Vector<Location> locations = new Vector<Location>();
		
		LocationManagerMock locationManager = new LocationManagerMock(true);
		
		long time = System.currentTimeMillis();
		
		final LocaterLocationListener listener = new LocaterLocationListener() {
			
			public void onLocationChanged(Location location) {
				locations.add(location);
			}
		};
		
		final LocaterImpl locater = new LocaterImpl(locationManager);
		
		locater.start(listener, 60 * 1000, Integer.MAX_VALUE, false);
		
		Location notAccurate = new Location(LocationManager.NETWORK_PROVIDER);
		notAccurate.setTime(time - 55 * 1000);
		notAccurate.setAccuracy(1000);
		locationManager.updateLocation(notAccurate);
		
		assertEquals(1, locations.size());
		assertTrue(locationManager.areListenersRegistered());
		
		locater.stop();
		
		assertFalse(locationManager.areListenersRegistered());
	}
	
	public void testOnLocationChangedUseGPS() {
		
		final Vector<Location> locations = new Vector<Location>();
		
		LocationManagerMock locationManager = new LocationManagerMock(true);
		
		final LocaterLocationListener listener = new LocaterLocationListener() {
			
			public void onLocationChanged(Location location) {
				locations.add(location);
			}
		};
		
		final LocaterImpl locater = new LocaterImpl(locationManager);
		
		locater.start(listener, 60 * 1000, 100, false);
		
		Location locationGPS = new Location(LocationManager.GPS_PROVIDER);
		Location locationNetwork = new Location(LocationManager.NETWORK_PROVIDER);
		
		locationManager.updateLocation(locationGPS);
		locationManager.updateLocation(locationNetwork);
		
		assertEquals(1, locations.size());
		assertEquals(locationNetwork, locations.get(0));
		
		locater.stop();
		
		locater.start(listener, 60 * 1000, 100, true);
		
		locationManager.updateLocation(locationGPS);
		locationManager.updateLocation(locationNetwork);
		
		assertEquals(3, locations.size());
		assertEquals(locationNetwork, locations.get(0));
		assertEquals(locationGPS, locations.get(1));
	}
	
	public void testIsAccurateEnoughTrue() {
		
		LocationManagerMock locationManager = new LocationManagerMock(true);
		
		LocaterImpl locater = new LocaterImpl(locationManager);
		
		Location accurateEnough = new Location(LocationManager.GPS_PROVIDER);
		accurateEnough.setAccuracy(90);
		
		assertTrue(locater.isAccurateEnough(accurateEnough, 100));
		
	}
	
	public void testIsAccurateEnoughFalse() {
		
		LocationManagerMock locationManager = new LocationManagerMock(true);
		
		LocaterImpl locater = new LocaterImpl(locationManager);
		
		Location notAccurateEnough = new Location(LocationManager.GPS_PROVIDER);
		notAccurateEnough.setAccuracy(110);
		
		assertFalse(locater.isAccurateEnough(notAccurateEnough, 100));
		
	}
	
	public void testIsAccurateEnoughHasNoAccuracy() {
		
		LocationManagerMock locationManager = new LocationManagerMock(true);
		
		LocaterImpl locater = new LocaterImpl(locationManager);
		
		Location hasNoAccuracy = new Location(LocationManager.GPS_PROVIDER);
	
		assertFalse(hasNoAccuracy.hasAccuracy());
		
		assertTrue(locater.isAccurateEnough(hasNoAccuracy, 100));
		
	}
	
	public void testIsAccurateNull() {
		
		LocationManagerMock locationManager = new LocationManagerMock(true);
		
		LocaterImpl locater = new LocaterImpl(locationManager);
		
		assertFalse(locater.isAccurateEnough(null, 100));
		
	}
	
}
