package net.luniks.android.impl;

import net.luniks.android.interfaces.IAlarmManager;
import android.app.AlarmManager;
import android.app.PendingIntent;

/**
 * Implementation of IAlarmManager.
 * @see android.app.AlarmManager
 * 
 * @author torsten.roemer@luniks.net
 */
public class AlarmManagerImpl implements IAlarmManager {
	
	private final AlarmManager alarmManager;
	
	public AlarmManagerImpl(final AlarmManager alarmManager) {
		this.alarmManager = alarmManager;
	}

	public void setInexactRepeating(final int type,
			final long triggerAtTime, final long interval,
			final PendingIntent operation) {
		alarmManager.setInexactRepeating(type, triggerAtTime, interval, operation);
	}

	public void cancel(final PendingIntent operation) {
		alarmManager.cancel(operation);
	}

}
