package net.luniks.android.inetify;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;

/**
 * A generic BroadcastReceiver that adds the given Runnable on the message queue
 * using the given handler.
 * 
 * @author torsten.roemer@luniks.net
 */
public class PostingBroadcastReceiver extends BroadcastReceiver {
	
	private final Runnable runnable;
	private final Handler handler;
	
	/**
	 * Creates an instance using the given Runnable and Handler.
	 * 
	 * @param runnable
	 * @param handler
	 */
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
