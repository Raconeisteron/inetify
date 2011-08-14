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
package net.luniks.android.inetify.test;

import net.luniks.android.inetify.LocationAlarm;
import net.luniks.android.inetify.LocationAlarmReceiver;
import net.luniks.android.test.mock.AlarmManagerMock;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.test.AndroidTestCase;

public class LocationAlarmTest extends AndroidTestCase {
	
	private SharedPreferences sharedPreferences;
	private PendingIntent operation;
	
	public void setUp() throws Exception {
		super.setUp();
		sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this.getContext());
		Intent checkLocationIntent = new Intent(this.getContext(), LocationAlarmReceiver.class);
		this.operation = PendingIntent.getBroadcast(this.getContext(), 0, checkLocationIntent, 0);
	}
	
	// Auto Wifi and notification enabled, airplane mode off
	public void testAutoWifiAndNotification() throws Exception {
		
		LocationAlarm alarm = new LocationAlarm(this.getContext());
		
		AlarmManagerMock alarmManager = new AlarmManagerMock();
		TestUtils.setFieldValue(alarm, "alarmManager", alarmManager);
		
		setSettings(true, true, "15");
		
		// How to set airplane mode?
		assertFalse("Test assumes that airplane mode is off", isAirplaneModeOn());
		
		alarm.reset();
		
		assertAlarmSet(alarmManager, AlarmManager.INTERVAL_FIFTEEN_MINUTES);
	}
	
	// Auto Wifi enabled, notification disabled, airplane mode off
	public void testAutoWifi() throws Exception {
		
		LocationAlarm alarm = new LocationAlarm(this.getContext());
		
		AlarmManagerMock alarmManager = new AlarmManagerMock();
		TestUtils.setFieldValue(alarm, "alarmManager", alarmManager);
		
		setSettings(true, false, "30");
		
		// How to set airplane mode?
		assertFalse("Test assumes that airplane mode is off", isAirplaneModeOn());
		
		alarm.reset();
		
		assertAlarmSet(alarmManager, AlarmManager.INTERVAL_HALF_HOUR);
	}
	
	// Auto Wifi disabled, notification enabled, airplane mode off
	public void testNotification() throws Exception {
		
		LocationAlarm alarm = new LocationAlarm(this.getContext());
		
		AlarmManagerMock alarmManager = new AlarmManagerMock();
		TestUtils.setFieldValue(alarm, "alarmManager", alarmManager);
		
		setSettings(false, true, "60");
		
		// How to set airplane mode?
		assertFalse("Test assumes that airplane mode is off", isAirplaneModeOn());
		
		alarm.reset();
		
		assertAlarmSet(alarmManager, AlarmManager.INTERVAL_HOUR);
	}
	
	// Auto Wifi and notification disabled, airplane mode off
	public void testNone() throws Exception {
		
		LocationAlarm alarm = new LocationAlarm(this.getContext());
		
		AlarmManagerMock alarmManager = new AlarmManagerMock();
		TestUtils.setFieldValue(alarm, "alarmManager", alarmManager);
		
		setSettings(false, false, "15");
		
		// How to set airplane mode?
		assertFalse("Test assumes that airplane mode is off", isAirplaneModeOn());
		
		alarm.reset();
		
		assertAlarmCancelled(alarmManager);
	}
	
	// Interval null, should default to INTERVAL_FIFTEEN_MINUTES
	public void testIntervalNull() throws Exception {
		
		LocationAlarm alarm = new LocationAlarm(this.getContext());
		
		AlarmManagerMock alarmManager = new AlarmManagerMock();
		TestUtils.setFieldValue(alarm, "alarmManager", alarmManager);
		
		setSettings(true, true, null);
		
		// How to set airplane mode?
		assertFalse("Test assumes that airplane mode is off", isAirplaneModeOn());
		
		alarm.reset();
		
		assertAlarmSet(alarmManager, AlarmManager.INTERVAL_FIFTEEN_MINUTES);
	}
	
	// Interval invalid, should default to INTERVAL_FIFTEEN_MINUTES
	public void testIntervalInvalid() throws Exception {
		
		LocationAlarm alarm = new LocationAlarm(this.getContext());
		
		AlarmManagerMock alarmManager = new AlarmManagerMock();
		TestUtils.setFieldValue(alarm, "alarmManager", alarmManager);
		
		setSettings(true, true, "invalid");
		
		// How to set airplane mode?
		assertFalse("Test assumes that airplane mode is off", isAirplaneModeOn());
		
		alarm.reset();
		
		assertAlarmSet(alarmManager, AlarmManager.INTERVAL_FIFTEEN_MINUTES);
	}
	
    /**
     * Returns true if airplane mode is on, false otherwise.
     * @return boolean true if airplane mode is on
     */
    private boolean isAirplaneModeOn() {
    	int airplaneModeOn = Settings.System.getInt(this.getContext().getContentResolver(),
    			Settings.System.AIRPLANE_MODE_ON, 0);
        return airplaneModeOn != 0;
    }
    
    private void setSettings(final boolean autoWifi, final boolean notification, final String interval) {
		sharedPreferences.edit().putBoolean("settings_auto_wifi", autoWifi).commit();
		sharedPreferences.edit().putBoolean("settings_wifi_location_enabled", notification).commit();
		sharedPreferences.edit().putString("settings_check_interval", interval).commit();
    }
    
    private void assertAlarmSet(final AlarmManagerMock alarmManager, final long interval) {
		assertFalse(alarmManager.isCancelled());
		assertEquals(AlarmManager.ELAPSED_REALTIME_WAKEUP, alarmManager.getType());
		assertTrue(SystemClock.elapsedRealtime() < alarmManager.getTriggerAtTime());
		assertTrue(SystemClock.elapsedRealtime() + LocationAlarm.TRIGGER_DELAY >= alarmManager.getTriggerAtTime());
		assertEquals(interval, alarmManager.getInterval());
		assertEquals(operation, alarmManager.getOperation());
    }
    
    private void assertAlarmCancelled(final AlarmManagerMock alarmManager) {
		assertTrue(alarmManager.isCancelled());
		assertEquals(operation, alarmManager.getCancelledOperation());
    }

}
