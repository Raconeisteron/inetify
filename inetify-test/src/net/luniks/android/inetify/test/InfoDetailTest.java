package net.luniks.android.inetify.test;

import net.luniks.android.inetify.DatabaseAdapter;
import net.luniks.android.inetify.DatabaseAdapterImpl;
import net.luniks.android.inetify.InfoDetail;
import net.luniks.android.inetify.R;
import net.luniks.android.inetify.TestInfo;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.test.ActivityInstrumentationTestCase2;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TwoLineListItem;


public class InfoDetailTest extends ActivityInstrumentationTestCase2<InfoDetail> {
	
	public InfoDetailTest() {
		super("net.luniks.android.inetify", InfoDetail.class);
	}
	
	public void setUp() throws Exception {
		super.setUp();
		this.getInstrumentation().getTargetContext().deleteDatabase("inetifydb");
		this.getInstrumentation().getTargetContext().deleteDatabase("inetifydb-journal");
	}
	
	public void testTestInfoNull() {
		
		TestInfo info = null;
		
		InfoDetail activity = this.getActivity(info);
		
		TextView textViewName = (TextView)activity.findViewById(R.id.textview_infodetail);
		
		assertEquals(activity.getString(R.string.infodetail_na), (String)textViewName.getText());
		
		Drawable icon = activity.getResources().getDrawable(R.drawable.icon);
		
		// How to test a drawable for equality?
		// assertEquals(icon, textViewName.getCompoundDrawables()[0]);
		assertNotNull(icon);
		assertNotNull(textViewName.getCompoundDrawables()[0]);
		
		ListView listView = (ListView)activity.findViewById(R.id.listview_infodetail);
		
		assertEquals(0, listView.getChildCount());
		
		activity.finish();
		
	}
	
	public void testTestInfoOK() {
		
		TestInfo info = getTestInfo();
		
		InfoDetail activity = this.getActivity(info);
		
		TextView textViewName = (TextView)activity.findViewById(R.id.textview_infodetail);
		
		assertEquals(activity.getString(R.string.infodetail_ok), (String)textViewName.getText());
		
		Drawable icon = activity.getResources().getDrawable(R.drawable.icon_ok);
		
		// How to test a drawable for equality?
		// assertEquals(icon, textViewName.getCompoundDrawables()[0]);
		assertNotNull(icon);
		assertNotNull(textViewName.getCompoundDrawables()[0]);
		
		ListView listView = (ListView)activity.findViewById(R.id.listview_infodetail);
		
		assertEquals(6, listView.getChildCount());
		
		assertListItems(activity, listView, info, false);
		
		activity.finish();
		
	}
	
	public void testTestIgnoreIfWifi() {
		
		TestInfo info = getTestInfo();
		
		InfoDetail activity = this.getActivity(info);
		
		ListView listView = (ListView)activity.findViewById(R.id.listview_infodetail);
		
		assertEquals(6, listView.getChildCount());
		
		assertListItems(activity, listView, info, false);
		
		activity.finish();
		
	}
	
	public void testTestIgnoreIfWifiAlreadyIgnored() throws Exception {
		
		TestInfo info = getTestInfo();
		
		// Need to use the real database here, since it is to late to call
		// InfoDetail.setDatabaseAdapter() after this.getActivity() and before it is not possible
		DatabaseAdapter databaseAdapter = new DatabaseAdapterImpl(this.getInstrumentation().getTargetContext());
		databaseAdapter.addIgnoredWifi(info.getExtra2(), info.getExtra());
		
		InfoDetail activity = this.getActivity(info);
		
		ListView listView = (ListView)activity.findViewById(R.id.listview_infodetail);
		
		assertEquals(6, listView.getChildCount());
		
		assertListItems(activity, listView, info, true);
		
		activity.finish();
		
	}
	
	public void testTestIgnoreIfNotWifi() {
		
		TestInfo info = getTestInfo();
		info.setType(ConnectivityManager.TYPE_MOBILE);
		info.setTypeName("TestMobile");
		
		InfoDetail activity = this.getActivity(info);
		
		ListView listView = (ListView)activity.findViewById(R.id.listview_infodetail);
		
		assertEquals(5, listView.getChildCount());
		
		assertListItems(activity, listView, info, false);
		
		activity.finish();
		
	}
	
	public void testTestInfoNotOKException() {
		
		TestInfo info = getTestInfo();
		info.setIsExpectedTitle(false);
		info.setException("TestException");
		
		InfoDetail activity = this.getActivity(info);
		
		TextView textViewName = (TextView)activity.findViewById(R.id.textview_infodetail);
		
		assertEquals(activity.getString(R.string.infodetail_nok), (String)textViewName.getText());
		
		Drawable icon = activity.getResources().getDrawable(R.drawable.icon_nok);
		
		// How to test a drawable for equality?
		// assertEquals(icon, textViewName.getCompoundDrawables()[0]);
		assertNotNull(icon);
		assertNotNull(textViewName.getCompoundDrawables()[0]);
		
		ListView listView = (ListView)activity.findViewById(R.id.listview_infodetail);
		
		assertEquals(6, listView.getChildCount());
		
		assertListItems(activity, listView, info, false);
		
		activity.finish();
		
	}
	
	public void testTestInfoInvalidInternetSite() {
		
		TestInfo info = getTestInfo();
		info.setIsExpectedTitle(false);
		info.setSite("invalid://TestSite");
		
		InfoDetail activity = this.getActivity(info);
		
		final ListView listView = (ListView)activity.findViewById(R.id.listview_infodetail);
		
		final TwoLineListItem listItemSettings = (TwoLineListItem)listView.getChildAt(2);
		
		Runnable click = new Runnable() {
			public void run() {
				listView.performItemClick(listItemSettings, 2, 2);
			}
		};
		activity.runOnUiThread(click);
		
		// TODO Assert on AlertDialog
		
		activity.finish();
		
	}
	
	public void testClickIgnore() throws InterruptedException {
		
		TestInfo info = getTestInfo();
		DatabaseAdapter databaseAdapter = new TestDatabaseAdapter();
		
		InfoDetail activity = this.getActivity(info);
		
		activity.setDatabaseAdapter(databaseAdapter);
		
		final ListView listView = (ListView)activity.findViewById(R.id.listview_infodetail);
		
		final TwoLineListItem listItemIgnore = (TwoLineListItem)listView.getChildAt(5);
		
		assertEquals(activity.getString(R.string.infodetail_value_ignore, info.getExtra()), listItemIgnore.getText2().getText());
		
		Runnable click = new Runnable() {
			public void run() {
				listView.performItemClick(listItemIgnore, 5, 5);
			}
		};
		activity.runOnUiThread(click);
		
		TestUtils.waitForIgnoredWifi(databaseAdapter, info.getExtra(), 10000);
		
		assertEquals(activity.getString(R.string.infodetail_value_ignored, info.getExtra()), listItemIgnore.getText2().getText());
		
		assertTrue(databaseAdapter.isIgnoredWifi(info.getExtra()));
		
		activity.finish();
		
	}
	
	private InfoDetail getActivity(final TestInfo info) {
		
		Intent infoDetailIntent = new Intent(InfoDetail.class.getName());
		infoDetailIntent.putExtra(InfoDetail.EXTRA_TEST_INFO, info);
		
		this.setActivityIntent(infoDetailIntent);
		
		return this.getActivity();
	}
	
	private TestInfo getTestInfo() {
		
		TestInfo info = new TestInfo();
		info.setIsExpectedTitle(true);
		info.setType(ConnectivityManager.TYPE_WIFI);
		info.setTypeName("TestWifi");
		info.setExtra("TestExtra");
		info.setExtra2("TestExtra2");
		info.setSite("TestSite");
		info.setTitle("TestTitle");
		info.setPageTitle("TestPageTitle");
		info.setTimestamp(1234567890L);
		
		return info;
	}
	
	private void assertListItems(final InfoDetail activity, final ListView listView, final TestInfo info, final boolean ignored) {
		
		TwoLineListItem listItem0 = (TwoLineListItem)listView.getChildAt(0);
		TwoLineListItem listItem1 = (TwoLineListItem)listView.getChildAt(1);
		TwoLineListItem listItem2 = (TwoLineListItem)listView.getChildAt(2);
		TwoLineListItem listItem3 = (TwoLineListItem)listView.getChildAt(3);
		TwoLineListItem listItem4 = (TwoLineListItem)listView.getChildAt(4);
		TwoLineListItem listItem5 = (TwoLineListItem)listView.getChildAt(5);
		
		// Why isClickable() == false?
		// assertTrue(listItem0.isClickable());
		assertFalse(listItem0.isEnabled());
		assertEquals(activity.getString(R.string.infodetail_prop_timestamp), listItem0.getText1().getText());
		assertEquals(activity.getString(R.string.infodetail_value_timestamp, activity.getDateTimeString(info.getTimestamp())), listItem0.getText2().getText());

		assertTrue(listItem1.isEnabled());
		assertEquals(activity.getString(R.string.infodetail_prop_connection), listItem1.getText1().getText());
		assertEquals(activity.getString(R.string.infodetail_value_connection, info.getTypeName(), info.getExtra()), listItem1.getText2().getText());

		assertTrue(listItem2.isEnabled());
		assertEquals(activity.getString(R.string.infodetail_prop_internetsite), listItem2.getText1().getText());
		assertEquals(activity.getString(R.string.infodetail_value_internetsite, info.getSite()), listItem2.getText2().getText());

		assertFalse(listItem3.isEnabled());
		assertEquals(activity.getString(R.string.infodetail_prop_expectedtitle), listItem3.getText1().getText());
		assertEquals(activity.getString(R.string.infodetail_value_expectedtitle, info.getTitle()), listItem3.getText2().getText());

		assertFalse(listItem4.isEnabled());
		assertEquals(activity.getString(R.string.infodetail_prop_foundtitle), listItem4.getText1().getText());
		if(info.getException() == null) {
			assertEquals(activity.getString(R.string.infodetail_value_foundtitle, info.getPageTitle()), listItem4.getText2().getText());
		} else {
			assertEquals(activity.getString(R.string.infodetail_value_exception, info.getException()), listItem4.getText2().getText());
		}
		
		if(info.getType() == ConnectivityManager.TYPE_WIFI) {
			assertTrue(listItem5.isEnabled());
			assertEquals(activity.getString(R.string.infodetail_prop_ignore), listItem5.getText1().getText());
			if(ignored) {
				assertEquals(activity.getString(R.string.infodetail_value_ignored, info.getExtra()), listItem5.getText2().getText());
			} else {
				assertEquals(activity.getString(R.string.infodetail_value_ignore, info.getExtra()), listItem5.getText2().getText());
			}
		}
		
	}
	
}
