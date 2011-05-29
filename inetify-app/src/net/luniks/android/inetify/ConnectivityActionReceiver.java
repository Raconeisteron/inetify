package net.luniks.android.inetify;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.preference.PreferenceManager;

/**
 * BroadcastReceiver that receives android.net.conn.CONNECTIVITY_CHANGE and
 * android.net.wifi.STATE_CHANGE intents and starts the InetifyIntentService when
 * Wifi connects or disconnects.
 * 
 * @author dode@luniks.net
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
		boolean enabled  = sharedPreferences.getBoolean("settings_enabled", false);
		if(enabled) {
			if(intent.getAction().equals(WifiManager.NETWORK_STATE_CHANGED_ACTION)) {
				NetworkInfo networkInfo = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
				if(networkInfo.isConnected()) {
					// Log.d(Inetify.LOG_TAG, String.valueOf(networkInfo));
					startService(context, true);
				}
			} else if(intent.getAction().equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
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
		Intent serviceIntent = new Intent("net.luniks.android.inetify.InetifyIntentService");
		serviceIntent.putExtra(EXTRA_IS_WIFI_CONNECTED, isWifiConnected);
		context.startService(serviceIntent);
	}

}
