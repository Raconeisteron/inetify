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

import java.util.ArrayList;
import java.util.List;

import net.luniks.android.inetify.WifiStateReceiver;
import net.luniks.android.inetify.WifiStateReceiver.WifiStateListener;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.test.AndroidTestCase;

public class WifiStateReceiverTest extends AndroidTestCase {
	
	public void testNull() {
		
		final List<Boolean> stateChanges = new ArrayList<Boolean>();
		
		WifiStateListener listener = new WifiStateListener() {

			public void onWifiStateChanged(boolean connected) {
				stateChanges.add(connected);
			}
			
		};
		
		WifiStateReceiver receiver = new WifiStateReceiver(listener);
		
		receiver.onReceive(null, null);
		
		assertEquals(0, stateChanges.size());
		
	}
	
	public void testOtherAction() throws Exception {
		
		final List<Boolean> stateChanges = new ArrayList<Boolean>();
		
		WifiStateListener listener = new WifiStateListener() {

			public void onWifiStateChanged(boolean connected) {
				stateChanges.add(connected);
			}
			
		};
		
		WifiStateReceiver receiver = new WifiStateReceiver(listener);
		
		Intent intent = new Intent(WifiManager.NETWORK_IDS_CHANGED_ACTION);
		NetworkInfo networkInfo = TestUtils.createNetworkInfo(ConnectivityManager.TYPE_WIFI, true);
		intent.putExtra(WifiManager.EXTRA_NETWORK_INFO, networkInfo);
		
		receiver.onReceive(null, intent);
		
		assertEquals(0, stateChanges.size());
		
	}
	
	public void testNoNetworkInfoExtra() throws Exception {
		
		final List<Boolean> stateChanges = new ArrayList<Boolean>();
		
		WifiStateListener listener = new WifiStateListener() {

			public void onWifiStateChanged(boolean connected) {
				stateChanges.add(connected);
			}
			
		};
		
		WifiStateReceiver receiver = new WifiStateReceiver(listener);
		
		Intent intent = new Intent(WifiManager.NETWORK_IDS_CHANGED_ACTION);
		
		receiver.onReceive(null, intent);
		
		assertEquals(0, stateChanges.size());
		
	}
	
	public void testNotConnected() throws Exception {
		
		final List<Boolean> stateChanges = new ArrayList<Boolean>();
		
		WifiStateListener listener = new WifiStateListener() {

			public void onWifiStateChanged(boolean connected) {
				stateChanges.add(connected);
			}
			
		};
		
		WifiStateReceiver receiver = new WifiStateReceiver(listener);
		
		Intent intent = new Intent(WifiManager.NETWORK_STATE_CHANGED_ACTION);
		NetworkInfo networkInfo = TestUtils.createNetworkInfo(ConnectivityManager.TYPE_WIFI, false);
		intent.putExtra(WifiManager.EXTRA_NETWORK_INFO, networkInfo);
		
		receiver.onReceive(null, intent);
		
		assertEquals(1, stateChanges.size());
		assertFalse(stateChanges.get(0));
		
	}
	
	public void testConnected() throws Exception {
		
		final List<Boolean> stateChanges = new ArrayList<Boolean>();
		
		WifiStateListener listener = new WifiStateListener() {

			public void onWifiStateChanged(boolean connected) {
				stateChanges.add(connected);
			}
			
		};
		
		WifiStateReceiver receiver = new WifiStateReceiver(listener);
		
		Intent intent = new Intent(WifiManager.NETWORK_STATE_CHANGED_ACTION);
		NetworkInfo networkInfo = TestUtils.createNetworkInfo(ConnectivityManager.TYPE_WIFI, true);
		intent.putExtra(WifiManager.EXTRA_NETWORK_INFO, networkInfo);
		
		receiver.onReceive(null, intent);
		
		assertEquals(1, stateChanges.size());
		assertTrue(stateChanges.get(0));
		
	}

}
