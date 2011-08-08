package net.luniks.android.inetify;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;

// TODO Implement
public class CheckLocationAlarmControllerReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(final Context context, final Intent intent) {
		
		if(intent != null) {
			String action = intent.getAction();
			
			if(action.equals(Intent.ACTION_BOOT_COMPLETED)) {
				// Boot completed
				setCheckLocationAlarm(context);
			}
			if(action.equals(Intent.ACTION_BATTERY_LOW)) {
				// Battery low
			}
			if(action.equals(Intent.ACTION_BATTERY_OKAY)) {
				// Battery okay
			}
			if(action.equals(Intent.ACTION_AIRPLANE_MODE_CHANGED)) {
				// Airplane mode enabled or disabled
				// boolean enabled = intent.getBooleanExtra("state", false);
			}
		}
	}
	
	/**
	 * Tells the check location alarm to update itself.
	 * @param context
	 */
	private void setCheckLocationAlarm(final Context context) {
		// TODO Does it make sense to use a Handler here?
		Handler handler = new Handler(context.getMainLooper());
		Alarm alarm = new CheckLocationAlarm(context);
		handler.post(alarm);
	}

}
