/*
 * Copyright 2011 Torsten Römer
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
	
	public static final String ACTION_LOCATION_ALARM = "net.luniks.android.inetify.action.LOCATION_ALARM";

	/**
	 * Starts LocationIntentService.
	 */
	@Override
	public void onReceive(final Context context, final Intent intent) {
		
		if(intent != null && intent.getAction() != null) {
			String action = intent.getAction();
			
			if(action.equals(ACTION_LOCATION_ALARM)) {
				
				Log.d(Inetify.LOG_TAG, String.format("Received alarm"));
				
				Intent serviceIntent = new Intent(context, LocationIntentService.class);
				context.startService(serviceIntent);
				
				if(LocationIntentService.wakeLock == null || ! LocationIntentService.wakeLock.isHeld()) {
					PowerManager powerManager = (PowerManager)context.getSystemService(Context.POWER_SERVICE);
					LocationIntentService.wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, 
							LocationIntentService.WAKE_LOCK_TAG);
					LocationIntentService.wakeLock.acquire();
					
					Log.d(Inetify.LOG_TAG, String.format("Acquired wake lock"));
				}
			}
		}
	}

}
