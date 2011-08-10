package net.luniks.android.inetify;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;
import android.util.Log;

/**
 * Broadcast receiver that gets intents sent to from an alarm using a pending intent,
 * starting an IntentService to check the location and give a notification about the
 * nearest Wifi location.
 * 
 * @author torsten.roemer@luniks.net
 */
public class CheckLocationAlarmReceiver extends BroadcastReceiver {

	/**
	 * Starts CheckLocationIntentService.
	 */
	@Override
	public void onReceive(final Context context, final Intent intent) {
		
		Log.d(Inetify.LOG_TAG, String.format("Received alarm"));
		
		PowerManager powerManager = (PowerManager)context.getSystemService(Context.POWER_SERVICE);
		CheckLocationIntentService.wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "CheckLocationAlarmReceiver");
		CheckLocationIntentService.wakeLock.acquire();
		
		Log.d(Inetify.LOG_TAG, String.format("Acquired wake lock"));
		
		Intent serviceIntent = new Intent("net.luniks.android.inetify.CheckLocationIntentService");
		context.startService(serviceIntent);
	}

}
