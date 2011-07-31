package net.luniks.android.test.impl;

import net.luniks.android.impl.LocationManagerImpl;
import net.luniks.android.interfaces.ILocationManager;
import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.test.AndroidTestCase;

public class LocationManagerImplTest extends AndroidTestCase {
	
	private LocationManager real;
	
	public void setUp() {
		real = (LocationManager)this.getContext().getSystemService(Context.LOCATION_SERVICE);
		removeTestProviders();
	}

	public void tearDown() {
		removeTestProviders();
	}
	
	public void testProviders() {
				
		ILocationManager wrapper = new LocationManagerImpl(real);
		
		assertEquals(real.getAllProviders(), wrapper.getAllProviders());
	}
	
	public void testIsProviderEnabled() {
				
		ILocationManager wrapper = new LocationManagerImpl(real);
		
		real.addTestProvider("enabled", false, false, false, false, false, false, false, Criteria.POWER_LOW, Criteria.ACCURACY_FINE);
		real.setTestProviderEnabled("enabled", true);
		
		real.addTestProvider("disabled", false, false, false, false, false, false, false, Criteria.POWER_LOW, Criteria.ACCURACY_FINE);
		real.setTestProviderEnabled("disabled", false);
		
		assertTrue(wrapper.isProviderEnabled("enabled"));
		assertFalse(wrapper.isProviderEnabled("disabled"));

	}
	
	public void testGetLastKnownLocation() {
				
		ILocationManager wrapper = new LocationManagerImpl(real);
		
		real.addTestProvider("test", false, false, false, false, false, false, false, Criteria.POWER_LOW, Criteria.ACCURACY_FINE);
		real.setTestProviderEnabled("test", true);
		
		// TODO Why is it necessary to register a listener to get a last known location?
		TestLocationListener listener = new TestLocationListener();
		wrapper.requestLocationUpdates("test", 0, 0, listener);
		
		Location location = new Location("test");
		location.setLatitude(1.23);
        location.setLongitude(4.56);
        location.setAccuracy(10);
        location.setTime(System.currentTimeMillis());
		real.setTestProviderLocation("test", location);
		
		Location lastKnown = wrapper.getLastKnownLocation("test");
		
		assertNotNull(lastKnown);
		assertEquals(location.getLatitude(), lastKnown.getLatitude());
		assertEquals(location.getLongitude(), lastKnown.getLongitude());

	}
	
	// FIXME LocationListener.getLastLocation() never gets called
	public void ignoreTestRequestLocationUpdates() throws InterruptedException {
		
		ILocationManager wrapper = new LocationManagerImpl(real);
		
		real.addTestProvider("test", false, false, false, false, false, false, false, Criteria.POWER_LOW, Criteria.ACCURACY_FINE);
		real.setTestProviderEnabled("test", true);
		
		TestLocationListener listener = new TestLocationListener();
		wrapper.requestLocationUpdates("test", 0, 0, listener);
				
		Location location = new Location("test");
		location.setLatitude(1.23);
        location.setLongitude(4.56);
        location.setAccuracy(10);
        location.setTime(System.currentTimeMillis());
		real.setTestProviderLocation("test", location);
		
		Thread.sleep(1000);
		
		Location lastLocation = listener.getLastLocation();
		
		assertNotNull(lastLocation);
		assertEquals(location.getLatitude(), lastLocation.getLatitude());
		assertEquals(location.getLongitude(), lastLocation.getLongitude());

	}
	
	private void removeTestProviders() {
		try {
			real.removeTestProvider("test");
			real.removeTestProvider("enabled");
			real.removeTestProvider("disabled");
		} catch(Exception e) {
			// Ignore
		}
	}
	
	public static class TestLocationListener implements LocationListener {
		
		private Location lastLocation;
		
		public void onStatusChanged(String provider, int status, Bundle extras) {
		}
		
		public void onProviderEnabled(String provider) {
		}
		
		public void onProviderDisabled(String provider) {
		}
		
		public void onLocationChanged(Location location) {
			this.lastLocation = location;
		}
		
		public Location getLastLocation() {
			return lastLocation;
		}
	};
}
