package net.luniks.android.test.impl;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import net.luniks.android.impl.LocationManagerImpl;
import net.luniks.android.inetify.Locater.LocaterLocationListener;
import net.luniks.android.inetify.LocaterImpl;
import net.luniks.android.test.mock.LocationManagerMock;
import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.os.Looper;
import android.test.AndroidTestCase;

public class LocaterImplTest extends AndroidTestCase {
	
	private static final long MAX_AGE = 60 * 1000;
	
	public void testGetBestLastKnownLocationAll() {
		
		LocationManagerMock locationManager = new LocationManagerMock();
		
		long bestTime = System.currentTimeMillis();
		
		Location mostAccurateMostRecent = new Location("A");
		mostAccurateMostRecent.setTime(bestTime);
		mostAccurateMostRecent.setAccuracy(10);
		locationManager.addLastKnownLocation("A", mostAccurateMostRecent);
		
		Location mostAccurateLessRecent = new Location("B");
		mostAccurateLessRecent.setTime(bestTime - 30 * 1000);
		mostAccurateLessRecent.setAccuracy(10);
		locationManager.addLastKnownLocation("B", mostAccurateLessRecent);
		
		Location lessAccurateMostRecent = new Location("C");
		lessAccurateMostRecent.setTime(bestTime);
		lessAccurateMostRecent.setAccuracy(20);
		locationManager.addLastKnownLocation("C", lessAccurateMostRecent);
		
		Location lessAccuratelessRecent = new Location("D");
		lessAccuratelessRecent.setTime(bestTime - 30 * 1000);
		lessAccuratelessRecent.setAccuracy(20);
		locationManager.addLastKnownLocation("D", lessAccuratelessRecent);
		
		Location mostAccurateTooOld = new Location("E");
		mostAccurateTooOld.setTime(bestTime - 2 * MAX_AGE);
		mostAccurateTooOld.setAccuracy(10);
		locationManager.addLastKnownLocation("E", mostAccurateTooOld);
		
		final LocaterImpl locater = new LocaterImpl(locationManager);
		
		Location bestLastKnownLocation = locater.getBestLastKnownLocation(MAX_AGE);
		
		assertEquals(mostAccurateMostRecent, bestLastKnownLocation);
		
	}
	
	public void testGetBestLastKnownLocationSameAccuracy() {
		
		LocationManagerMock locationManager = new LocationManagerMock();
		
		long bestTime = System.currentTimeMillis();
		
		Location mostAccurateMostRecent = new Location("A");
		mostAccurateMostRecent.setTime(bestTime);
		mostAccurateMostRecent.setAccuracy(10);
		locationManager.addLastKnownLocation("A", mostAccurateMostRecent);
		
		Location mostAccurateLessRecent = new Location("B");
		mostAccurateLessRecent.setTime(bestTime - 30 * 1000);
		mostAccurateLessRecent.setAccuracy(10);
		locationManager.addLastKnownLocation("B", mostAccurateLessRecent);
		
		final LocaterImpl locater = new LocaterImpl(locationManager);
		
		for(int i = 0; i < 100; i++) {
			Location bestLastKnownLocation = locater.getBestLastKnownLocation(MAX_AGE);
			
			assertEquals(mostAccurateMostRecent, bestLastKnownLocation);
		}
		
	}
	
	public void testGetBestLastKnownLocationSameTime() {
		
		LocationManagerMock locationManager = new LocationManagerMock();
		
		long bestTime = System.currentTimeMillis();
		
		Location mostAccurateMostRecent = new Location("A");
		mostAccurateMostRecent.setTime(bestTime);
		mostAccurateMostRecent.setAccuracy(10);
		locationManager.addLastKnownLocation("A", mostAccurateMostRecent);
		
		Location lessAccurateMostRecent = new Location("B");
		lessAccurateMostRecent.setTime(bestTime);
		lessAccurateMostRecent.setAccuracy(20);
		locationManager.addLastKnownLocation("B", lessAccurateMostRecent);
		
		final LocaterImpl locater = new LocaterImpl(locationManager);
		
		Location bestLastKnownLocation = locater.getBestLastKnownLocation(MAX_AGE);
		
		assertEquals(mostAccurateMostRecent, bestLastKnownLocation);
		
	}

	public void testGetLocation() throws InterruptedException {
		
		final Map<Long, Location> locations = new ConcurrentHashMap<Long, Location>();
		
		LocationManager locationManager = (LocationManager)this.getContext().getSystemService(Context.LOCATION_SERVICE);
		final LocaterImpl locater = new LocaterImpl(new LocationManagerImpl(locationManager));
		final LocaterLocationListener listener = new LocaterLocationListener() {
			
			public void onLocationChanged(Location location) {
				locations.put(location.getTime(), location);
			}
		};
	
		Thread t = new Thread() {
			public void run() {
				Looper.prepare();
				
				locater.start(listener);
				
				Looper.loop();
			}
		};
		t.start();
		
		Thread.sleep(3000);
		
		locater.stop();
		
		assertTrue(locations.size() > 0);
		
	}
	
}
