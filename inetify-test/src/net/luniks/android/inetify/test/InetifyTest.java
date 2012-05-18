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

import net.luniks.android.inetify.Help;
import net.luniks.android.inetify.IgnoreList;
import net.luniks.android.inetify.Inetify;
import net.luniks.android.inetify.InfoDetail;
import net.luniks.android.inetify.LocationList;
import net.luniks.android.inetify.R;
import net.luniks.android.inetify.Settings;
import android.app.Activity;
import android.app.Instrumentation.ActivityMonitor;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.preference.PreferenceManager;
import android.test.ActivityInstrumentationTestCase2;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TwoLineListItem;

public class InetifyTest extends ActivityInstrumentationTestCase2<Inetify> {

	private Inetify activity;

	public InetifyTest() {
		super("net.luniks.android.inetify", Inetify.class);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		activity = this.getActivity();
	}
	
	// Inetify does not set default tone at the moment, it is just silent
	public void ignoreTestSetDefaultTone() throws Exception {
		
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(activity);
		
		activity.finish();
		// Seems it is needed to really destroy the activity so onCreate() is called on getActivity()
		this.tearDown();
		
		preferences.edit().putString(Settings.TONE, null).commit();
		
		activity = this.getActivity();
		
		assertEquals(android.provider.Settings.System.DEFAULT_NOTIFICATION_URI.toString(), preferences.getString(Settings.TONE, null));
	}

	public void testHello() {
		
		TextView textViewName = (TextView)activity.findViewById(R.id.textview_name);
		
		assertEquals(activity.getString(R.string.hello), (String)textViewName.getText());
		
		Drawable icon = activity.getResources().getDrawable(R.drawable.icon);
		
		// How to test a drawable for equality?
		// assertEquals(icon, textViewName.getCompoundDrawables()[0]);
		assertNotNull(icon);
		assertNotNull(textViewName.getCompoundDrawables()[0]);
	}
	
	public void testTest() throws InterruptedException {
		
		ListView listView = (ListView)activity.findViewById(R.id.listview_main);
		
		TwoLineListItem listItemTest = (TwoLineListItem)TestUtils.selectAndFindListViewChildAt(activity, listView, 0, 3000);
		
		assertTrue(listItemTest.isEnabled());
		
		assertEquals(activity.getString(R.string.main_title_test), listItemTest.getText1().getText());
		assertEquals(activity.getString(R.string.main_summary_test), listItemTest.getText2().getText());
		
	}
	
	public void testSettings() throws InterruptedException {
		
		ListView listView = (ListView)activity.findViewById(R.id.listview_main);
		
		TwoLineListItem listItemSettings = (TwoLineListItem)TestUtils.selectAndFindListViewChildAt(activity, listView, 1, 3000);
		
		assertTrue(listItemSettings.isEnabled());
		
		assertEquals(activity.getString(R.string.main_title_settings), listItemSettings.getText1().getText());
		assertEquals(activity.getString(R.string.main_summary_settings), listItemSettings.getText2().getText());
		
	}
	
	public void testIgnoreList() throws InterruptedException {
		
		ListView listView = (ListView)activity.findViewById(R.id.listview_main);
		
		TwoLineListItem listItemHelp = (TwoLineListItem)TestUtils.selectAndFindListViewChildAt(activity, listView, 2, 3000);
		
		assertTrue(listItemHelp.isEnabled());
		
		assertEquals(activity.getString(R.string.main_title_ignorelist), listItemHelp.getText1().getText());
		assertEquals(activity.getString(R.string.main_summary_ignorelist), listItemHelp.getText2().getText());
		
	}
	
	public void testLocationList() throws InterruptedException {
		
		ListView listView = (ListView)activity.findViewById(R.id.listview_main);
		
		TwoLineListItem listItemHelp = (TwoLineListItem)TestUtils.selectAndFindListViewChildAt(activity, listView, 3, 3000);
		
		assertTrue(listItemHelp.isEnabled());
		
		assertEquals(activity.getString(R.string.main_title_locationlist), listItemHelp.getText1().getText());
		assertEquals(activity.getString(R.string.main_summary_locationlist), listItemHelp.getText2().getText());
		
	}
	
	public void testHelp() throws InterruptedException {
		
		ListView listView = (ListView)activity.findViewById(R.id.listview_main);
		
		TwoLineListItem listItemHelp = (TwoLineListItem)TestUtils.selectAndFindListViewChildAt(activity, listView, 4, 3000);
		
		assertTrue(listItemHelp.isEnabled());
		
		assertEquals(activity.getString(R.string.main_title_help), listItemHelp.getText1().getText());
		assertEquals(activity.getString(R.string.main_summary_help), listItemHelp.getText2().getText());
		
	}
	
	public void testClickTest() throws Exception {
		
		TestTester tester = new TestTester();
		Object testTask = TestUtils.getFieldValue(activity, "testTask");
		TestUtils.setFieldValue(testTask, "tester", tester);
		
		final ListView listView = (ListView)activity.findViewById(R.id.listview_main);
		
		final TwoLineListItem listItemTest = (TwoLineListItem)TestUtils.selectAndFindListViewChildAt(activity, listView, 0, 3000);
		
		ActivityMonitor monitor = new ActivityMonitor(InfoDetail.class.getName(), null, false);
		this.getInstrumentation().addMonitor(monitor);
		
		Runnable click = new Runnable() {
			public void run() {
				listView.performItemClick(listItemTest, 0, 0);
			}
		};
		activity.runOnUiThread(click);
		
		TestUtils.waitForTestCount(tester, 1, 10000);
		
		tester.done();
		
		Activity infoDetail = monitor.waitForActivityWithTimeout(10000);
		
		assertEquals(1, monitor.getHits());
		
		this.getInstrumentation().removeMonitor(monitor);
		
		infoDetail.finish();
		
	}
	
	public void testClickSettings() throws InterruptedException {
		
		final ListView listView = (ListView)activity.findViewById(R.id.listview_main);
		
		final TwoLineListItem listItemSettings = (TwoLineListItem)TestUtils.selectAndFindListViewChildAt(activity, listView, 1, 3000);
		
		ActivityMonitor monitor = new ActivityMonitor(net.luniks.android.inetify.Settings.class.getName(), null, false);
		this.getInstrumentation().addMonitor(monitor);
		
		Runnable click = new Runnable() {
			public void run() {
				listView.performItemClick(listItemSettings, 1, 1);
			}
		};
		activity.runOnUiThread(click);
		
		Activity settings = monitor.waitForActivityWithTimeout(10000);
		
		assertEquals(1, monitor.getHits());
		
		this.getInstrumentation().removeMonitor(monitor);
		
		settings.finish();
		
	}
	
	public void testClickIgnoreList() throws InterruptedException {
		
		final ListView listView = (ListView)activity.findViewById(R.id.listview_main);
		
		final TwoLineListItem listItemIgnoreList = (TwoLineListItem)TestUtils.selectAndFindListViewChildAt(activity, listView, 2, 3000);
		
		ActivityMonitor monitor = new ActivityMonitor(IgnoreList.class.getName(), null, false);
		this.getInstrumentation().addMonitor(monitor);
		
		Runnable click = new Runnable() {
			public void run() {
				listView.performItemClick(listItemIgnoreList, 2, 2);
			}
		};
		activity.runOnUiThread(click);
		
		Activity ignoreList = monitor.waitForActivityWithTimeout(10000);
		
		assertEquals(1, monitor.getHits());
		
		this.getInstrumentation().removeMonitor(monitor);
		
		ignoreList.finish();
		
	}
	
	public void testClickLocationList() throws InterruptedException {
		
		final ListView listView = (ListView)activity.findViewById(R.id.listview_main);
		
		final TwoLineListItem listLocationList = (TwoLineListItem)TestUtils.selectAndFindListViewChildAt(activity, listView, 3, 3000);
		
		ActivityMonitor monitor = new ActivityMonitor(LocationList.class.getName(), null, false);
		this.getInstrumentation().addMonitor(monitor);
		
		Runnable click = new Runnable() {
			public void run() {
				listView.performItemClick(listLocationList, 3, 3);
			}
		};
		activity.runOnUiThread(click);
		
		Activity locationList = monitor.waitForActivityWithTimeout(10000);
		
		assertEquals(1, monitor.getHits());
		
		this.getInstrumentation().removeMonitor(monitor);
		
		locationList.finish();
		
	}
	
	public void testClickHelp() throws InterruptedException {
		
		final ListView listView = (ListView)activity.findViewById(R.id.listview_main);
		
		final TwoLineListItem listItemHelp = (TwoLineListItem)TestUtils.selectAndFindListViewChildAt(activity, listView, 4, 3000);
		
		ActivityMonitor monitor = new ActivityMonitor(Help.class.getName(), null, false);
		this.getInstrumentation().addMonitor(monitor);
		
		Runnable click = new Runnable() {
			public void run() {
				listView.performItemClick(listItemHelp, 4, 4);
			}
		};
		activity.runOnUiThread(click);
		
		Activity help = monitor.waitForActivityWithTimeout(10000);
		
		assertEquals(1, monitor.getHits());
		
		this.getInstrumentation().removeMonitor(monitor);
		
		help.finish();
		
	}

}
