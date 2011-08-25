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
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.PowerManager;
import android.preference.PreferenceManager;

/**
 * BroadcastReceiver that receives android.net.conn.CONNECTIVITY_CHANGE and
 * android.net.wifi.STATE_CHANGE intents and starts the InetifyIntentService when
 * Wifi connects or disconnects.
 * 
 * @author torsten.roemer@luniks.net
 */
public class ConnectivityActionReceiver extends BroadcastReceiver {
	
	/** Lookup key for a boolean that provides extra information if wifi is connected or not */
	public static final String EXTRA_IS_WIFI_CONNECTED = "isWifiConnected";

	/**
	 * Checks if Wifi connected or disconnected and then starts InetifyIntentService,
	 * passing an intent with EXTRA_IS_WIFI_CONNECTED, indicating if Wifi is connected or not.
	 */
	@Override
	public void onReceive(final Context context, final Intent intent) {
		
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
		boolean enabled  = sharedPreferences.getBoolean(Settings.INTERNET_CHECK, false);
		
		if(intent != null && intent.getAction() != null && enabled) {
			String action = intent.getAction();
			
			if(action.equals(WifiManager.NETWORK_STATE_CHANGED_ACTION)) {
				// FIXME This intent seems to be broadcasted before the Wifi connection setup is completely finished,
				// i.e. DHCP configuration is not completely done and ConnectivityManager still reports a mobile data
				// connection, at least sometimes.
				NetworkInfo networkInfo = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
				if(networkInfo.isConnected()) {
					// Log.d(Inetify.LOG_TAG, String.valueOf(networkInfo));
					startService(context, true);
				}
			} else if(action.equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
				NetworkInfo networkInfo = intent.getParcelableExtra(ConnectivityManager.EXTRA_NETWORK_INFO);
				if(networkInfo.getType() == ConnectivityManager.TYPE_WIFI && ! networkInfo.isConnected()) {
					// Log.d(Inetify.LOG_TAG, String.valueOf(networkInfo));
					startService(context, false);
				}
			}
		}
	}
	
	/**
	 * Starts InetifyIntentService, passing an intent with EXTRA_IS_WIFI_CONNECTED
	 * @param isWifiConnected
	 */
	private void startService(final Context context, final boolean isWifiConnected) {
		Intent serviceIntent = new Intent(context, InetifyIntentService.class);
		serviceIntent.putExtra(EXTRA_IS_WIFI_CONNECTED, isWifiConnected);
		context.startService(serviceIntent);
		
		if(InetifyIntentService.wakeLock == null) {
			PowerManager powerManager = (PowerManager)context.getSystemService(Context.POWER_SERVICE);
			InetifyIntentService.wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, 
					InetifyIntentService.WAKE_LOCK_TAG);
		}
		if(! InetifyIntentService.wakeLock.isHeld()) {
			InetifyIntentService.wakeLock.acquire();
			
			// Log.d(Inetify.LOG_TAG, String.format("Acquired wake lock"));
		}
	}

}
