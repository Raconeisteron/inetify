package net.luniks.android.inetify;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;

public class PostingBroadcastReceiver extends BroadcastReceiver {
	
	private final Runnable runnable;
	private final Handler handler;
	
	public PostingBroadcastReceiver(final Runnable runnable, final Handler handler) {
		this.runnable = runnable;
		this.handler = handler;
	}

	@Override
	public void onReceive(final Context context, final Intent intent) {
		if(intent == null) {
			return;
		}
		handler.post(runnable);
	}

}
