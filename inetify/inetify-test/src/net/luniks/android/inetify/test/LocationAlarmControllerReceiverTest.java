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

import net.luniks.android.inetify.LocationAlarmControllerReceiver;
import net.luniks.android.inetify.LocationAlarmReceiver;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.test.AndroidTestCase;

public class LocationAlarmControllerReceiverTest extends AndroidTestCase {
	
	public void testBootCompleted() throws Exception {
		
		LocationAlarmControllerReceiver receiver = new LocationAlarmControllerReceiver();
		
		TestAlarm alarm = new TestAlarm();
		TestUtils.setFieldValue(receiver, "alarm", alarm);
		
		receiver.onReceive(this.getContext(), new Intent(Intent.ACTION_BOOT_COMPLETED));
		receiver.onReceive(this.getContext(), new Intent("some.other.ACTION"));
		
		assertEquals(1, alarm.getResetCalledCount());
		
	}
	
	public void testAirplaneModeChanged() throws Exception {
		
		LocationAlarmControllerReceiver receiver = new LocationAlarmControllerReceiver();
		
		TestAlarm alarm = new TestAlarm();
		TestUtils.setFieldValue(receiver, "alarm", alarm);
		
		receiver.onReceive(this.getContext(), new Intent(Intent.ACTION_AIRPLANE_MODE_CHANGED));
		receiver.onReceive(this.getContext(), new Intent("some.other.ACTION"));
		
		assertEquals(1, alarm.getResetCalledCount());
		
	}
	
	public void testBatteryLow() throws Exception {
		
		LocationAlarmControllerReceiver receiver = new LocationAlarmControllerReceiver();
		
		TestAlarm alarm = new TestAlarm();
		TestUtils.setFieldValue(receiver, "alarm", alarm);
		
		PackageManager packageManager = this.getContext().getPackageManager();
		ComponentName locationAlarmReceiver = new ComponentName(this.getContext(), LocationAlarmReceiver.class);
		
		packageManager.setComponentEnabledSetting(locationAlarmReceiver, 
				PackageManager.COMPONENT_ENABLED_STATE_DEFAULT, PackageManager.DONT_KILL_APP);
		
		packageManager.getComponentEnabledSetting(locationAlarmReceiver);
		
		assertEquals(PackageManager.COMPONENT_ENABLED_STATE_DEFAULT, 
				packageManager.getComponentEnabledSetting(locationAlarmReceiver));
		
		receiver.onReceive(this.getContext(), new Intent(Intent.ACTION_BATTERY_LOW));
		receiver.onReceive(this.getContext(), new Intent("some.other.ACTION"));
		
		assertEquals(0, alarm.getResetCalledCount());
		
		assertEquals(PackageManager.COMPONENT_ENABLED_STATE_DISABLED, 
				packageManager.getComponentEnabledSetting(locationAlarmReceiver));
		
	}

	public void testBatteryOkay() throws Exception {
		
		LocationAlarmControllerReceiver receiver = new LocationAlarmControllerReceiver();
		
		TestAlarm alarm = new TestAlarm();
		TestUtils.setFieldValue(receiver, "alarm", alarm);
		
		PackageManager packageManager = this.getContext().getPackageManager();
		ComponentName locationAlarmReceiver = new ComponentName(this.getContext(), LocationAlarmReceiver.class);
		
		packageManager.setComponentEnabledSetting(locationAlarmReceiver, 
				PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
		
		packageManager.getComponentEnabledSetting(locationAlarmReceiver);
		
		assertEquals(PackageManager.COMPONENT_ENABLED_STATE_DISABLED, 
				packageManager.getComponentEnabledSetting(locationAlarmReceiver));
		
		receiver.onReceive(this.getContext(), new Intent(Intent.ACTION_BATTERY_OKAY));
		receiver.onReceive(this.getContext(), new Intent("some.other.ACTION"));
		
		assertEquals(0, alarm.getResetCalledCount());
		
		assertEquals(PackageManager.COMPONENT_ENABLED_STATE_DEFAULT, 
				packageManager.getComponentEnabledSetting(locationAlarmReceiver));
		
	}
	
}
