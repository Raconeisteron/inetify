package net.luniks.android.interfaces;

import android.app.PendingIntent;

public interface IAlarmManager {

	void setInexactRepeating(int type,
			long triggerAtTime, long interval,
			PendingIntent operation);
	
	void cancel(PendingIntent operation);

}
