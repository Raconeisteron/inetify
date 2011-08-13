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
package net.luniks.android.inetify;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

/**
 * BroadcastReceiver that considers WifiManager.NETWORK_STATE_CHANGED_ACTION intents
 * and calls onWifiStateChanged(boolean) of its WifiStateListener when Wifi state changes.
 * 
 * @author torsten.roemer@luniks.net
 */
public class WifiStateReceiver extends BroadcastReceiver {
	
	private final WifiStateListener listener;
	
	/**
	 * Creates an instance using the given WifiStateListener.
	 * @param runner
	 */
	public WifiStateReceiver(final WifiStateListener listener) {
		this.listener = listener;
	}

	@Override
	public void onReceive(final Context context, final Intent intent) {
		if(intent != null) {
			if(intent.getAction().equals(WifiManager.NETWORK_STATE_CHANGED_ACTION)) {
				NetworkInfo networkInfo = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
				if(networkInfo != null) {
					if(networkInfo.isConnected()) {
						Log.d(Inetify.LOG_TAG, "Wifi is connected");
						listener.onWifiStateChanged(true);
					} else {
						Log.d(Inetify.LOG_TAG, "Wifi is not connected");
						listener.onWifiStateChanged(false);
					}
				}
			}
		}
	}
	
	/**
	 * Listener whose onWifiStateChanged(boolean) is called with true when Wifi
	 * connected and false when it disconnected.
	 * @author torsten.roemer@luniks.net
	 */
	public static interface WifiStateListener {
		
		void onWifiStateChanged(boolean connected);
		
	}

}
