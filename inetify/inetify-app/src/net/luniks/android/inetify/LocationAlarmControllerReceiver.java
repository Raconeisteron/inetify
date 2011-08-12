package net.luniks.android.inetify;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.util.Log;

/**
 * Broadcast receiver that receives certain intents and resets the location alarm,
 * and disables LocationAlarmReceiver when the battery is low.
 * 
 * @author torsten.roemer@luniks.net
 */
public class LocationAlarmControllerReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(final Context context, final Intent intent) {
		
		if(intent != null) {
			String action = intent.getAction();
			
			Alarm alarm = new LocationAlarm(context);
			
			if(action.equals(Intent.ACTION_BOOT_COMPLETED)) {
				alarm.reset();
			}
			else if(action.equals(Intent.ACTION_AIRPLANE_MODE_CHANGED)) {
				alarm.reset();
			}
			else if(action.equals(Intent.ACTION_BATTERY_LOW)) {
				setLocationAlarmReceiverEnabled(context, false);
			}
			else if(action.equals(Intent.ACTION_BATTERY_OKAY)) {
				setLocationAlarmReceiverEnabled(context, true);
			}
		}
	}
	
	/**
	 * Enables LocationAlarmReceiver if the given boolean is true, disables it otherwise.
	 * @param context
	 * @param enabled
	 */
	private void setLocationAlarmReceiverEnabled(final Context context, final boolean enabled) {
		
		PackageManager packageManager = context.getPackageManager();
		ComponentName locationAlarmReceiver = new ComponentName(context, LocationAlarmReceiver.class);
		
		packageManager.setComponentEnabledSetting(locationAlarmReceiver, 
				enabled ? PackageManager.COMPONENT_ENABLED_STATE_DEFAULT : PackageManager.COMPONENT_ENABLED_STATE_DISABLED,  
				PackageManager.DONT_KILL_APP);
		
		Log.d(Inetify.LOG_TAG, String.format("Set LocationAlarmReceiver enabled: %s", enabled));
	}
}
