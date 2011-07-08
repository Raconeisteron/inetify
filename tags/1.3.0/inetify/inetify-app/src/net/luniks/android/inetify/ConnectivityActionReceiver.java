package net.luniks.android.inetify;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;

/**
 * BroadcastReceiver that receives android.net.conn.CONNECTIVITY_CHANGE intents and
 * starts the InetifyService when a Wifi connection is established.
 * 
 * @author dode@luniks.net
 */
public class ConnectivityActionReceiver extends BroadcastReceiver {

	/** {@inheritDoc} */
	@Override
	public void onReceive(final Context context, final Intent intent) {
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
		boolean enabled  = sharedPreferences.getBoolean("settings_enabled", false);
		if(enabled) {
			NetworkInfo networkInfo = intent.getParcelableExtra(ConnectivityManager.EXTRA_NETWORK_INFO);
			if(networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
				Intent serviceIntent = new Intent("net.luniks.android.inetify.InetifyService");
				serviceIntent.putExtra(ConnectivityManager.EXTRA_NETWORK_INFO, networkInfo);
				context.startService(serviceIntent);
			}
		}
	}

}