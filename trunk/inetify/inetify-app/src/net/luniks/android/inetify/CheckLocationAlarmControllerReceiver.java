package net.luniks.android.inetify;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class CheckLocationAlarmControllerReceiver extends BroadcastReceiver {
	
	/** Shared preferences key used to store the battery low state */
	private static final String SHARED_PREFERENCES_BATTERY_LOW = "battery_low";

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
			
			Alarm alarm = new CheckLocationAlarm(context);
			alarm.update();
		}
	}
}
