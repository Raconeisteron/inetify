package net.luniks.android.inetify.test;

import net.luniks.android.inetify.Help;
import net.luniks.android.inetify.Inetify;
import net.luniks.android.inetify.InfoDetail;
import net.luniks.android.inetify.R;
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
	
	public void testSetDefaultTone() throws Exception {
		
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(activity);
		
		activity.finish();
		// Seems it is needed to really destroy the activity so onCreate() is called on getActivity()
		this.tearDown();
		
		preferences.edit().putString("settings_tone", null).commit();
		
		activity = this.getActivity();
		
		assertEquals(android.provider.Settings.System.DEFAULT_NOTIFICATION_URI.toString(), preferences.getString("settings_tone", null));
	}

	public void testHello() {
		
		TextView textViewName = (TextView)activity.findViewById(R.id.textview_name);
		
		assertEquals(activity.getString(R.string.hello), (String)textViewName.getText());
		
		Drawable icon = activity.getResources().getDrawable(R.drawable.icon);
		// How to test a drawable for equality?
		// assertEquals(icon, textViewName.getCompoundDrawables()[0]);
	}
	
	public void testTest() {
		
		ListView listView = (ListView)activity.findViewById(R.id.listview_main);
		
		TwoLineListItem listItemTest = (TwoLineListItem)listView.getChildAt(0);
		
		// Why isClickable() == false?
		// assertTrue(manualTest.isClickable());
		assertTrue(listItemTest.isEnabled());
		
		assertEquals(activity.getString(R.string.main_title_test), listItemTest.getText1().getText());
		assertEquals(activity.getString(R.string.main_summary_test), listItemTest.getText2().getText());
		
	}
	
	public void testSettings() {
		
		ListView listView = (ListView)activity.findViewById(R.id.listview_main);
		
		TwoLineListItem listItemSettings = (TwoLineListItem)listView.getChildAt(1);
		
		// Why isClickable() == false?
		// assertTrue(manualTest.isClickable());
		assertTrue(listItemSettings.isEnabled());
		
		assertEquals(activity.getString(R.string.main_title_settings), listItemSettings.getText1().getText());
		assertEquals(activity.getString(R.string.main_summary_settings), listItemSettings.getText2().getText());
		
	}
	
	public void testHelp() {
		
		ListView listView = (ListView)activity.findViewById(R.id.listview_main);
		
		TwoLineListItem listItemHelp = (TwoLineListItem)listView.getChildAt(2);
		
		// Why isClickable() == false?
		// assertTrue(manualTest.isClickable());
		assertTrue(listItemHelp.isEnabled());
		
		assertEquals(activity.getString(R.string.main_title_help), listItemHelp.getText1().getText());
		assertEquals(activity.getString(R.string.main_summary_help), listItemHelp.getText2().getText());
		
	}
	
	// @UiThreadTest
	public void testClickTest() throws InterruptedException {
		
		TestTester tester = new TestTester();
		activity.setTester(tester);
		
		final ListView listView = (ListView)activity.findViewById(R.id.listview_main);
		
		final TwoLineListItem listItemTest = (TwoLineListItem)listView.getChildAt(0);
		
		ActivityMonitor monitor = new ActivityMonitor(InfoDetail.class.getName(), null, false);
		this.getInstrumentation().addMonitor(monitor);
		
		Runnable clickTest = new Runnable() {
			public void run() {
				listView.performItemClick(listItemTest, 0, 0);
			}
		};
		activity.runOnUiThread(clickTest);
		
		TestUtils.waitForTestCount(tester, 1, 10000);
		
		tester.done();
		
		Activity infoDetail = monitor.waitForActivityWithTimeout(10000);
		
		assertEquals(1, monitor.getHits());
		
		infoDetail.finish();
		
	}
	
	// @UiThreadTest
	public void testClickSettings() {
		
		final ListView listView = (ListView)activity.findViewById(R.id.listview_main);
		
		final TwoLineListItem listItemSettings = (TwoLineListItem)listView.getChildAt(1);
		
		ActivityMonitor monitor = new ActivityMonitor(net.luniks.android.inetify.Settings.class.getName(), null, false);
		this.getInstrumentation().addMonitor(monitor);
		
		Runnable clickTest = new Runnable() {
			public void run() {
				listView.performItemClick(listItemSettings, 1, 1);
			}
		};
		activity.runOnUiThread(clickTest);
		
		Activity settings = monitor.waitForActivityWithTimeout(10000);
		
		assertEquals(1, monitor.getHits());
		
		settings.finish();
		
	}
	
	// @UiThreadTest
	public void testClickHelp() {
		
		final ListView listView = (ListView)activity.findViewById(R.id.listview_main);
		
		final TwoLineListItem listItemHelp = (TwoLineListItem)listView.getChildAt(2);
		
		ActivityMonitor monitor = new ActivityMonitor(Help.class.getName(), null, false);
		this.getInstrumentation().addMonitor(monitor);
		
		Runnable clickTest = new Runnable() {
			public void run() {
				listView.performItemClick(listItemHelp, 2, 2);
			}
		};
		activity.runOnUiThread(clickTest);
		
		Activity help = monitor.waitForActivityWithTimeout(10000);
		
		assertEquals(1, monitor.getHits());
		
		help.finish();
		
	}

}
