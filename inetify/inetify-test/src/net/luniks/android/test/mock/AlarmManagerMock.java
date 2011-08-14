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
