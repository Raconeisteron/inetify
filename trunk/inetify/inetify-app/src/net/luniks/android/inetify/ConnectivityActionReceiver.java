package net.luniks.android.inetify;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;
import android.util.Log;

/**
 * BroadcastReceiver that receives android.net.conn.CONNECTIVITY_CHANGE and
 * android.net.wifi.STATE_CHANGE intents.
 * 
 * @author torsten.roemer@luniks.net
 */
public class ConnectivityActionReceiver extends BroadcastReceiver {
	
	/** Lookup key for a boolean that provides extra information if wifi is connected or not */
	public static final String EXTRA_IS_WIFI_CONNECTED = "isWifiConnected";

	/**
	 * Checks if Wifi connected or disconnected and calls startService() and setCheckLocationAlarm()
	 */
	@Override
	public void onReceive(final Context context, final Intent intent) {
		if(intent != null && intent.getAction().equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
			NetworkInfo networkInfo = intent.getParcelableExtra(ConnectivityManager.EXTRA_NETWORK_INFO);
			if(networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
				Log.d(Inetify.LOG_TAG, String.valueOf(networkInfo));
				startService(context, networkInfo.isConnected());
				setCheckLocationAlarm(context);
			}
		}
	}
	
	/**
	 * Starts InetifyIntentService, passing an intent with EXTRA_IS_WIFI_CONNECTED
	 * @param context
	 * @param isWifiConnected
	 */
	private void startService(final Context context, final boolean isWifiConnected) {
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
		boolean enabled  = sharedPreferences.getBoolean("settings_enabled", false);
		if(enabled) {
			Intent serviceIntent = new Intent("net.luniks.android.inetify.InetifyIntentService");
			serviceIntent.putExtra(EXTRA_IS_WIFI_CONNECTED, isWifiConnected);
			context.startService(serviceIntent);
		}
	}
	
	/**
	 * Tells the check location alarm to update itself.
	 * @param context
	 */
	private void setCheckLocationAlarm(final Context context) {
		Alarm alarm = new CheckLocationAlarm(context);
		alarm.update();
	}

}
