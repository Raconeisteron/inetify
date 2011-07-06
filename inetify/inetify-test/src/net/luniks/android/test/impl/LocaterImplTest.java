package net.luniks.android.test.impl;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import net.luniks.android.inetify.Locater.LocaterLocationListener;
import net.luniks.android.inetify.LocaterImpl;
import android.location.Location;
import android.os.Looper;
import android.test.AndroidTestCase;

public class LocaterImplTest extends AndroidTestCase {

	public void testGetLocation() throws InterruptedException {
		
		final Map<Long, Location> locations = new ConcurrentHashMap<Long, Location>();
		
		final LocaterImpl locater = new LocaterImpl(this.getContext());
		final LocaterLocationListener listener = new LocaterLocationListener() {
			
			public void onNewLocation(Location location) {
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
