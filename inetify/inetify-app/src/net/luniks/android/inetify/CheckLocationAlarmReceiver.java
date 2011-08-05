package net.luniks.android.inetify;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class CheckLocationAlarmReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(final Context context, final Intent intent) {
		
		Log.d(Inetify.LOG_TAG, String.format("Received alarm"));
		
		Intent serviceIntent = new Intent("net.luniks.android.inetify.CheckLocationIntentService");
		context.startService(serviceIntent);
	}

}
