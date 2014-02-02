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
package net.luniks.android.test.impl;

import net.luniks.android.impl.AlarmManagerImpl;
import net.luniks.android.interfaces.IAlarmManager;
import android.app.AlarmManager;
import android.content.Context;
import android.test.AndroidTestCase;

public class AlarmManagerImplTest extends AndroidTestCase {
	
	public void testSetInexactRepeating() {
		
		IAlarmManager alarmManager = new AlarmManagerImpl(
				(AlarmManager)this.getContext().getSystemService(Context.ALARM_SERVICE));
		
		// FIXME How to test? This doesn't even fail.
		alarmManager.setInexactRepeating(-1, -1, -1, null);
		
	}
	
	public void testCancel() {
		
		IAlarmManager alarmManager = new AlarmManagerImpl(
				(AlarmManager)this.getContext().getSystemService(Context.ALARM_SERVICE));
		
		// FIXME How to test? This doesn't even fail.
		alarmManager.cancel(null);
		
	}

}
