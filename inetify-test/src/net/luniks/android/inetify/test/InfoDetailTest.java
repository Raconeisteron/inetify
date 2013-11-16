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

import net.luniks.android.inetify.DatabaseAdapter;
import net.luniks.android.inetify.DatabaseAdapterImpl;
import net.luniks.android.inetify.InfoDetail;
import net.luniks.android.inetify.R;
import net.luniks.android.inetify.TestInfo;
import net.luniks.android.inetify.Utils;
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
	
	public void testTestInfoNull() throws InterruptedException {
		
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
	
	public void testTestInfoOK() throws InterruptedException {
		
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
		
		TestUtils.waitForItemCount(listView, 6, 10000);
		
		assertListItems(activity, listView, info, false);
		
		activity.finish();
		
	}
	
	// No connection at all
	public void testTestInfoNoConnection() throws InterruptedException {
		
		TestInfo info = getTestInfo();
		info.setType(-1);
		info.setTypeName(null);
		info.setExtra(null);
		info.setExtra2(null);
		
		InfoDetail activity = this.getActivity(info);
		
		TextView textViewName = (TextView)activity.findViewById(R.id.textview_infodetail);
		
		assertEquals(activity.getString(R.string.infodetail_ok), (String)textViewName.getText());
		
		Drawable icon = activity.getResources().getDrawable(R.drawable.icon_ok);
		
		// How to test a drawable for equality?
		// assertEquals(icon, textViewName.getCompoundDrawables()[0]);
		assertNotNull(icon);
		assertNotNull(textViewName.getCompoundDrawables()[0]);
		
		ListView listView = (ListView)activity.findViewById(R.id.listview_infodetail);
		
		TestUtils.waitForItemCount(listView, 5, 10000);
		
		assertListItems(activity, listView, info, false);
		
		activity.finish();
		
	}
	
	// Wifi connection but supplicant not connected?
	public void testTestInfoWifiButSSIDNull() throws InterruptedException {
		
		TestInfo info = getTestInfo();
		info.setType(ConnectivityManager.TYPE_WIFI);
		info.setTypeName(null);
		info.setExtra(null);
		info.setExtra2(null);
		
		InfoDetail activity = this.getActivity(info);
		
		TextView textViewName = (TextView)activity.findViewById(R.id.textview_infodetail);
		
		assertEquals(activity.getString(R.string.infodetail_ok), (String)textViewName.getText());
		
		Drawable icon = activity.getResources().getDrawable(R.drawable.icon_ok);
		
		// How to test a drawable for equality?
		// assertEquals(icon, textViewName.getCompoundDrawables()[0]);
		assertNotNull(icon);
		assertNotNull(textViewName.getCompoundDrawables()[0]);
		
		ListView listView = (ListView)activity.findViewById(R.id.listview_infodetail);
		
		TestUtils.waitForItemCount(listView, 5, 10000);
		
		assertListItems(activity, listView, info, false);
		
		activity.finish();
		
	}
	
	public void testTestIgnoreIfWifi() throws InterruptedException {
		
		TestInfo info = getTestInfo();
		
		InfoDetail activity = this.getActivity(info);
		
		ListView listView = (ListView)activity.findViewById(R.id.listview_infodetail);
		
		TestUtils.waitForItemCount(listView, 6, 10000);
		
		assertListItems(activity, listView, info, false);
		
		activity.finish();
		
	}
	
	// Wifi connected but supplicant not?
	public void testTestIgnoreIfWifiButNotConnected() throws InterruptedException {
		
		TestInfo info = getTestInfo();
		info.setTypeName(null);
		info.setExtra(null);
		info.setExtra2(null);
		
		InfoDetail activity = this.getActivity(info);
		
		ListView listView = (ListView)activity.findViewById(R.id.listview_infodetail);
		
		TestUtils.waitForItemCount(listView, 5, 10000);
		
		assertListItems(activity, listView, info, false);
		
		activity.finish();
		
	}
	
	public void testTestIgnoreIfWifiAlreadyIgnored() throws Exception {
		
		TestInfo info = getTestInfo();
		
		// Need to use the real database here, since it is to late to call
		// InfoDetail.setDatabaseAdapter() after this.getActivity() and before it is not possible
		DatabaseAdapter databaseAdapter = new DatabaseAdapterImpl(this.getInstrumentation().getTargetContext());
		databaseAdapter.addIgnoredWifi(info.getExtra2(), info.getExtra());
		databaseAdapter.close();
		
		InfoDetail activity = this.getActivity(info);
		
		ListView listView = (ListView)activity.findViewById(R.id.listview_infodetail);
		
		TestUtils.waitForItemCount(listView, 6, 10000);
		
		assertListItems(activity, listView, info, true);
		
		activity.finish();
		
	}
	
	public void testTestIgnoreIfNotWifi() throws InterruptedException {
		
		TestInfo info = getTestInfo();
		info.setType(ConnectivityManager.TYPE_MOBILE);
		info.setTypeName("TestMobile");
		
		InfoDetail activity = this.getActivity(info);
		
		ListView listView = (ListView)activity.findViewById(R.id.listview_infodetail);
		
		TestUtils.waitForItemCount(listView, 5, 10000);
		
		assertListItems(activity, listView, info, false);
		
		activity.finish();
		
	}
	
	public void testTestInfoNotOKException() throws InterruptedException {
		
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
		
		TestUtils.waitForItemCount(listView, 6, 10000);
		
		assertListItems(activity, listView, info, false);
		
		activity.finish();
		
	}
	
	public void testTestInfoInvalidInternetSite() throws InterruptedException {
		
		TestInfo info = getTestInfo();
		info.setIsExpectedTitle(false);
		info.setSite("invalid://TestSite");
		
		InfoDetail activity = this.getActivity(info);
		
		final ListView listView = (ListView)activity.findViewById(R.id.listview_infodetail);
		
		final TwoLineListItem listItemInternetSite = (TwoLineListItem)TestUtils.selectAndFindListViewChildAt(activity, listView, 3, 3000);
		
		Runnable click = new Runnable() {
			public void run() {
				listView.performItemClick(listItemInternetSite, 3, 3);
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
		
		assertListItems(activity, listView, info, false);
		
		final TwoLineListItem listItemIgnore = (TwoLineListItem)TestUtils.selectAndFindListViewChildAt(activity, listView, 5, 3000);
		
		Runnable click = new Runnable() {
			public void run() {
				listView.performItemClick(listItemIgnore, 5, 5);
			}
		};
		activity.runOnUiThread(click);
		
		TestUtils.waitForIgnoredWifi(databaseAdapter, info.getExtra(), 10000);
		
		assertListItems(activity, listView, info, true);
		
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
	
	private void assertListItems(final InfoDetail activity, final ListView listView, final TestInfo info, final boolean ignored) throws InterruptedException {
		
		TwoLineListItem listItem0 = (TwoLineListItem)TestUtils.selectAndFindListViewChildAt(activity, listView, 0, 5000);		
		assertFalse(listItem0.isEnabled());
		assertFalse(listItem0.getText1().isEnabled());
		assertTrue(listItem0.getText2().isEnabled());
		assertEquals(activity.getString(R.string.infodetail_prop_timestamp), listItem0.getText1().getText());
		assertEquals(activity.getString(R.string.infodetail_value_timestamp, Utils.getDateTimeString(activity, info.getTimestamp())), listItem0.getText2().getText());

		TwoLineListItem listItem1 = (TwoLineListItem)TestUtils.selectAndFindListViewChildAt(activity, listView, 1, 5000);
		assertFalse(listItem1.isEnabled());
		assertFalse(listItem1.getText1().isEnabled());
		assertTrue(listItem1.getText2().isEnabled());
		assertEquals(activity.getString(R.string.infodetail_prop_expectedtitle), listItem1.getText1().getText());
		assertEquals(activity.getString(R.string.infodetail_value_expectedtitle, info.getTitle()), listItem1.getText2().getText());

		TwoLineListItem listItem2 = (TwoLineListItem)TestUtils.selectAndFindListViewChildAt(activity, listView, 2, 5000);
		assertFalse(listItem2.isEnabled());
		assertFalse(listItem2.getText1().isEnabled());
		assertTrue(listItem2.getText2().isEnabled());
		assertEquals(activity.getString(R.string.infodetail_prop_foundtitle), listItem2.getText1().getText());
		if(info.getException() == null) {
			assertEquals(activity.getString(R.string.infodetail_value_foundtitle, info.getPageTitle()), listItem2.getText2().getText());
		} else {
			assertEquals(activity.getString(R.string.infodetail_value_exception, info.getException()), listItem2.getText2().getText());
		}

		TwoLineListItem listItem3 = (TwoLineListItem)TestUtils.selectAndFindListViewChildAt(activity, listView, 3, 5000);
		assertTrue(listItem3.isEnabled());
		assertTrue(listItem3.getText1().isEnabled());
		assertTrue(listItem3.getText2().isEnabled());
		assertEquals(activity.getString(R.string.infodetail_prop_internetsite), listItem3.getText1().getText());
		assertEquals(activity.getString(R.string.infodetail_value_internetsite, info.getSite()), listItem3.getText2().getText());
		
		TwoLineListItem listItem4 = (TwoLineListItem)TestUtils.selectAndFindListViewChildAt(activity, listView, 4, 5000);
		assertTrue(listItem4.isEnabled());
		assertTrue(listItem4.getText1().isEnabled());
		assertTrue(listItem4.getText2().isEnabled());
		assertEquals(activity.getString(R.string.infodetail_prop_connection), listItem4.getText1().getText());
		if(info.getTypeName() != null && info.getExtra() != null) {
			assertEquals(activity.getString(R.string.infodetail_value_connection, info.getNiceTypeName(), info.getExtra()), listItem4.getText2().getText());
		} else {
			assertEquals(activity.getString(R.string.infodetail_value_noconnection), listItem4.getText2().getText());
		}
		
		if(info.getType() == ConnectivityManager.TYPE_WIFI && info.getExtra() != null && info.getExtra2() != null) {
			TwoLineListItem listItem5 = (TwoLineListItem)TestUtils.selectAndFindListViewChildAt(activity, listView, 5, 5000);
			assertTrue(listItem5.isEnabled());
			assertTrue(listItem5.getText1().isEnabled());
			assertTrue(listItem5.getText2().isEnabled());
			assertEquals(activity.getString(R.string.infodetail_prop_ignore), listItem5.getText1().getText());
			if(ignored) {
				assertEquals(activity.getString(R.string.infodetail_value_ignored, info.getExtra()), listItem5.getText2().getText());
			} else {
				assertEquals(activity.getString(R.string.infodetail_value_ignore, info.getExtra()), listItem5.getText2().getText());
			}
		}
	}
	
}
