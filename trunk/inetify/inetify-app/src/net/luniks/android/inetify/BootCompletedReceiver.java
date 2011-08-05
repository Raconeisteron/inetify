package net.luniks.android.inetify;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;

public class BootCompletedReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(final Context context, final Intent intent) {
		
		// TODO Does it make sense to use a Handler here?
		Handler handler = new Handler(context.getMainLooper());
		Alarm alarm = new CheckLocationAlarm(context);
		handler.post(alarm);

	}

}
