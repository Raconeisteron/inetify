package net.luniks.android.inetify;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;
import android.util.Log;

/**
 * Broadcast receiver that gets intents sent to it from an alarm and starts
 * an IntentService to check the location and give a notification about the
 * nearest Wifi location and enables or disables Wifi.
 * 
 * @author torsten.roemer@luniks.net
 */
public class LocationAlarmReceiver extends BroadcastReceiver {
	
	/** This should be way enough for the service to become available?  */
	private static final long WAKE_LOCK_TIMEOUT = 10 * 1000;

	/**
	 * Starts LocationIntentService.
	 */
	@Override
	public void onReceive(final Context context, final Intent intent) {
		
		Log.d(Inetify.LOG_TAG, String.format("Received alarm"));
		
		if(LocationIntentService.wakeLock == null || ! LocationIntentService.wakeLock.isHeld()) {
			PowerManager powerManager = (PowerManager)context.getSystemService(Context.POWER_SERVICE);
			LocationIntentService.wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "LocationAlarmReceiver");
			
			// http://code.google.com/p/android/issues/detail?id=14184 ?
			LocationIntentService.wakeLock.setReferenceCounted(false);
			LocationIntentService.wakeLock.acquire(WAKE_LOCK_TIMEOUT);
			
			Log.d(Inetify.LOG_TAG, String.format("Acquired wake lock"));
		}
		
		Intent serviceIntent = new Intent(context, LocationIntentService.class);
		context.startService(serviceIntent);
	}

}
