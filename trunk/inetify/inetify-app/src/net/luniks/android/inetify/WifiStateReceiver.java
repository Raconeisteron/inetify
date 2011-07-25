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
	public WifiStateReceiver(final WifiStateListener runnable) {
		this.listener = runnable;
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
