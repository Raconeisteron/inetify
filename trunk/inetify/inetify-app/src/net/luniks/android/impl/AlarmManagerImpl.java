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
