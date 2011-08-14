package net.luniks.android.test.mock;

import net.luniks.android.interfaces.IAlarmManager;
import android.app.PendingIntent;

public class AlarmManagerMock implements IAlarmManager {
	
	private int type;
	private long triggerAtTime;
	private long interval;
	private PendingIntent operation;
	
	private boolean cancelled;
	private PendingIntent cancelledOperation;

	public void setInexactRepeating(final int type, final long triggerAtTime,
			final long interval, final PendingIntent operation) {
		this.type = type;
		this.triggerAtTime = triggerAtTime;
		this.interval = interval;
		this.operation = operation;
	}

	public void cancel(final PendingIntent operation) {
		this.cancelled = true;
		this.cancelledOperation = operation;
	}

	public int getType() {
		return type;
	}

	public long getTriggerAtTime() {
		return triggerAtTime;
	}

	public long getInterval() {
		return interval;
	}

	public PendingIntent getOperation() {
		return operation;
	}

	public boolean isCancelled() {
		return cancelled;
	}

	public PendingIntent getCancelledOperation() {
		return cancelledOperation;
	}

}
