package net.luniks.android.inetify;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.wifi.WifiManager;
import android.preference.PreferenceManager;

/**
 * Broadcast receiver that receives certain events and tells CheckLocationAlarm
 * to set or cancel the alarm triggering location checks.
 * 
 * @author torsten.roemer@luniks.net
 */
public class CheckLocationAlarmControllerReceiver extends BroadcastReceiver {
	
	/** Shared preferences key used to store the battery low state */
	private static final String SHARED_PREFERENCES_BATTERY_LOW = "battery_low";

	/**
	 * Calls CheckLocationAlarm.update(), which checks all the relevant conditions.
	 * TODO Not sure if this is the best way of setting/cancelling an alarm depending
	 * on many intents/conditions.
	 */
	@Override
	public void onReceive(final Context context, final Intent intent) {
		
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
		
		if(intent != null) {
			String action = intent.getAction();
			
			if(action.equals(Intent.ACTION_BATTERY_LOW)) {
				sharedPreferences.edit().putBoolean(SHARED_PREFERENCES_BATTERY_LOW, true).commit();
			}
			else if(action.equals(Intent.ACTION_BATTERY_OKAY)) {
				sharedPreferences.edit().putBoolean(SHARED_PREFERENCES_BATTERY_LOW, false).commit();
			}
			else if(action.equals(WifiManager.WIFI_STATE_CHANGED_ACTION)) {
				int state = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, WifiManager.WIFI_STATE_ENABLED);
				if(state == WifiManager.WIFI_STATE_DISABLING || state == WifiManager.WIFI_STATE_ENABLING) {
					return;
				}
			}
			
			Alarm alarm = new CheckLocationAlarm(context);
			alarm.update();
		}
	}
}
