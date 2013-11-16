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

import net.luniks.android.inetify.Inetify;
import net.luniks.android.inetify.R;
import net.luniks.android.inetify.Settings;
import android.app.Instrumentation.ActivityMonitor;
import android.test.ActivityInstrumentationTestCase2;

public class MenuTest extends ActivityInstrumentationTestCase2<Inetify> {
	
	public MenuTest() {
		super("net.luniks.android.inetify", Inetify.class);
	}

	public void testSettingsMenuItem() {
		
		ActivityMonitor settingsActivityMonitor = new ActivityMonitor(Settings.class.getName(), null, true);
		this.getInstrumentation().addMonitor(settingsActivityMonitor);
		
		Inetify activity = this.getActivity();
		
		boolean menuItemSettingsCalled = this.getInstrumentation().invokeMenuActionSync(activity, R.id.settings, 0);
		
		assertTrue(menuItemSettingsCalled);
		
		int settingsActivityHitCount = settingsActivityMonitor.getHits();
		
		assertEquals(1, settingsActivityHitCount);
		
		this.getInstrumentation().removeMonitor(settingsActivityMonitor);
	
	}
	
}

