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

import net.luniks.android.inetify.WifiLocation;
import android.location.Location;
import android.test.AndroidTestCase;

public class WifiLocationTest extends AndroidTestCase {
	
	public void testSetGet() {
		Location location = new Location("WifiLocation");
	
		WifiLocation wifiLocation = getAllSet(location);
		
		assertEquals("TestBSSID", wifiLocation.getBSSID());
		assertEquals("TestSSID", wifiLocation.getSSID());
		assertEquals("TestName", wifiLocation.getName());
		assertEquals(333f, wifiLocation.getDistance());
		assertEquals(location, wifiLocation.getLocation());
		
	}
	
	public void testToString() {
		
		Location location = new Location("WifiLocation");
		
		WifiLocation wifiLocation = getAllSet(location);
		
		String string = String.valueOf(wifiLocation);
		
		assertTrue(string.contains("BSSID = TestBSSID"));
		assertTrue(string.contains("SSID = TestSSID"));
		assertTrue(string.contains("name = TestName"));
		assertTrue(string.contains("distance = 333"));
		assertTrue(string.contains("location = " + location.toString()));
		
	}
	
	private WifiLocation getAllSet(final Location location) {
		WifiLocation wifiLocation = new WifiLocation();
		
		wifiLocation.setBSSID("TestBSSID");
		wifiLocation.setSSID("TestSSID");
		wifiLocation.setName("TestName");
		wifiLocation.setDistance(333);
		wifiLocation.setLocation(location);
		
		return wifiLocation;
	}

}
