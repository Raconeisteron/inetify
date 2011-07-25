package net.luniks.android.inetify.test;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import net.luniks.android.inetify.Locater.LocaterLocationListener;
import net.luniks.android.inetify.LocaterImpl;
import net.luniks.android.test.mock.LocationManagerMock;
import android.location.Location;
import android.test.AndroidTestCase;

public class LocaterImplTest extends AndroidTestCase {
	
	private static final long MAX_AGE = 60 * 1000;
	
	public void testGetBestLastKnownLocationAll() {
		
		LocationManagerMock locationManager = new LocationManagerMock();
		
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
			
			assertEquals(mostAccurateMostRecent, bestLastKnownLocation);
		}
		
	}
	
	public void testGetBestLastKnownLocationSameAccuracy() {
		
		LocationManagerMock locationManager = new LocationManagerMock();
		
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
			
			assertEquals(mostAccurateMostRecent, bestLastKnownLocation);
		}
		
	}
	
	public void testGetBestLastKnownLocationSameTime() {
		
		LocationManagerMock locationManager = new LocationManagerMock();
		
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
			
			assertEquals(mostAccurateMostRecent, bestLastKnownLocation);
		}
		
	}

	public void testGetBestLastKnownTooOld() {
		
		LocationManagerMock locationManager = new LocationManagerMock();
		
		long time = System.currentTimeMillis();
		
		Location mostAccurateTooOld = new Location("A");
		mostAccurateTooOld.setTime(time - 2000);
		mostAccurateTooOld.setAccuracy(10);
		locationManager.addLastKnownLocation("A", mostAccurateTooOld);
		
		Location lessAccurateEvenOlder = new Location("B");
		lessAccurateEvenOlder.setTime(time - 3000);
		lessAccurateEvenOlder.setAccuracy(20);
		locationManager.addLastKnownLocation("B", lessAccurateEvenOlder);
		
		final LocaterImpl locater = new LocaterImpl(locationManager);
		
		for(int i = 0; i < 1000; i++) {
			Location bestLastKnownLocation = locater.getBestLastKnownLocation(1000);
			
			assertNull(bestLastKnownLocation);
		}
		
	}
	
	public void testLocaterLastKnownRecent() {
		
		final Map<Long, Location> locations = new ConcurrentHashMap<Long, Location>();
		
		LocationManagerMock locationManager = new LocationManagerMock();
		
		long time = System.currentTimeMillis();
		
		Location recent = new Location("A");
		recent.setTime(time - 55 * 1000);
		recent.setAccuracy(10);
		locationManager.addLastKnownLocation("A", recent);
		
		final LocaterLocationListener listener = new LocaterLocationListener() {
			
			public void onLocationChanged(Location location) {
				locations.put(location.getTime(), location);
			}
		};
		
		final LocaterImpl locater = new LocaterImpl(locationManager);
		
		locater.start(listener);
		
		assertEquals(1, locations.size());
		
	}
	
	public void testLocaterLastKnownTooOld() {
		
		final Map<Long, Location> locations = new ConcurrentHashMap<Long, Location>();
		
		LocationManagerMock locationManager = new LocationManagerMock();
		
		long time = System.currentTimeMillis();
		
		Location tooOld = new Location("A");
		tooOld.setTime(time - 65 * 1000);
		tooOld.setAccuracy(10);
		locationManager.addLastKnownLocation("A", tooOld);
		
		final LocaterLocationListener listener = new LocaterLocationListener() {
			
			public void onLocationChanged(Location location) {
				locations.put(location.getTime(), location);
			}
		};
		
		final LocaterImpl locater = new LocaterImpl(locationManager);
		
		locater.start(listener);
		
		assertEquals(0, locations.size());
		
	}
	
}
