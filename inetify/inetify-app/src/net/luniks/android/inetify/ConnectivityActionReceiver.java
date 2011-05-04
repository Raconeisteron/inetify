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
 * BroadcastReceiver that receives android.net.conn.CONNECTIVITY_CHANGE intents and
 * starts the InetifyService when a Wifi connection is established.
 * 
 * @author dode@luniks.net
 */
public class ConnectivityActionReceiver extends BroadcastReceiver {
	
	/** Lookup key for a boolean that provides extra information if wifi is connected or not */
	public static final String EXTRA_IS_WIFI_CONNECTED = "isWifiConnected";

	/** {@inheritDoc} */
	@Override
	public void onReceive(final Context context, final Intent intent) {
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
		boolean enabled  = sharedPreferences.getBoolean("settings_enabled", false);
		if(enabled) {
			if(intent.getAction().equals(WifiManager.NETWORK_STATE_CHANGED_ACTION)) {
				NetworkInfo networkInfo = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
				if(networkInfo.isConnected()) {
					startService(context, true);
				}
			} else if(intent.getAction().equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
				NetworkInfo networkInfo = intent.getParcelableExtra(ConnectivityManager.EXTRA_NETWORK_INFO);
				if(networkInfo.getType() == ConnectivityManager.TYPE_WIFI && ! networkInfo.isConnected()) {
					startService(context, false);
				}
			}
		}
	}
	
	/**
	 * Starts InetifyService, passing an intent with EXTRA_IS_WIFI_CONNECTED
	 * @param isWifiConnected
	 */
	private void startService(final Context context, final boolean isWifiConnected) {
		Intent serviceIntent = new Intent("net.luniks.android.inetify.InetifyService");
		serviceIntent.putExtra(EXTRA_IS_WIFI_CONNECTED, isWifiConnected);
		context.startService(serviceIntent);
	}

}
