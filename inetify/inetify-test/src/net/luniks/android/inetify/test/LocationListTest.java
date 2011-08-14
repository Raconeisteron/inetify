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
import net.luniks.android.inetify.Dialogs.InputDialog;
import net.luniks.android.inetify.LocationList;
import net.luniks.android.inetify.R;
import net.luniks.android.interfaces.IWifiInfo;
import net.luniks.android.interfaces.IWifiManager;
import net.luniks.android.test.mock.WifiInfoMock;
import net.luniks.android.test.mock.WifiManagerMock;
import android.app.AlertDialog;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.test.ActivityInstrumentationTestCase2;
import android.widget.ListView;
import android.widget.TwoLineListItem;


public class LocationListTest extends ActivityInstrumentationTestCase2<LocationList> {
	
	public LocationListTest() {
		super("net.luniks.android.inetify", LocationList.class);
	}
	
	public void setUp() throws Exception {
		super.setUp();
		this.getInstrumentation().getTargetContext().deleteDatabase("inetifydb");
		this.getInstrumentation().getTargetContext().deleteDatabase("inetifydb-journal");
	}
	
	public void testListEmptyWifiDisconnected() throws Exception {
		
		LocationList activity = this.getActivity();
		IWifiInfo wifiInfo = new WifiInfoMock().setBSSID("TestBSSID").setSSID("TestSSID");
		IWifiManager wifiManager = new WifiManagerMock(wifiInfo);
		TestUtils.setFieldValue(activity, "wifiManager", wifiManager);
		
		Intent intent = new Intent(WifiManager.NETWORK_STATE_CHANGED_ACTION);
		NetworkInfo networkInfo = TestUtils.createNetworkInfo(ConnectivityManager.TYPE_WIFI, false);
		intent.putExtra(WifiManager.EXTRA_NETWORK_INFO, networkInfo);
		this.getActivity().sendBroadcast(intent);
		
		final ListView listView = (ListView)activity.findViewById(android.R.id.list);
		
		TwoLineListItem headerItem = (TwoLineListItem)TestUtils.selectAndFindListViewChildAt(activity, listView, 0, 3000);
		
		assertFalse(headerItem.isEnabled());
		assertEquals(activity.getString(R.string.locationlist_add_wifi_location), headerItem.getText1().getText());
		assertEquals(activity.getString(R.string.wifi_disconnected), headerItem.getText2().getText());
		
		activity.finish();
	}
	
	public void testListEmptyWifiConnected() throws Exception {
		
		LocationList activity = this.getActivity();
		IWifiInfo wifiInfo = new WifiInfoMock().setBSSID("TestBSSID").setSSID("TestSSID");
		IWifiManager wifiManager = new WifiManagerMock(wifiInfo);
		TestUtils.setFieldValue(activity, "wifiManager", wifiManager);
		
		Intent intent = new Intent(WifiManager.NETWORK_STATE_CHANGED_ACTION);
		NetworkInfo networkInfo = TestUtils.createNetworkInfo(ConnectivityManager.TYPE_WIFI, true);
		intent.putExtra(WifiManager.EXTRA_NETWORK_INFO, networkInfo);
		this.getActivity().sendBroadcast(intent);
		
		// FIXME Wait for condition with timeout
		Thread.sleep(1000);
		
		final ListView listView = (ListView)activity.findViewById(android.R.id.list);
		
		TwoLineListItem headerItem = (TwoLineListItem)TestUtils.selectAndFindListViewChildAt(activity, listView, 0, 3000);
		
		assertTrue(headerItem.isEnabled());
		assertEquals(activity.getString(R.string.locationlist_add_wifi_location), headerItem.getText1().getText());
		assertEquals(activity.getString(R.string.locationlist_add_location_of_wifi, "TestSSID"), headerItem.getText2().getText());
		
		activity.finish();
	}
	
	public void testListEmptyWifiConnectedWifiInfoNull() throws Exception {
		
		LocationList activity = this.getActivity();
		IWifiManager wifiManager = new WifiManagerMock(null);
		TestUtils.setFieldValue(activity, "wifiManager", wifiManager);
		
		Intent intent = new Intent(WifiManager.NETWORK_STATE_CHANGED_ACTION);
		NetworkInfo networkInfo = TestUtils.createNetworkInfo(ConnectivityManager.TYPE_WIFI, true);
		intent.putExtra(WifiManager.EXTRA_NETWORK_INFO, networkInfo);
		this.getActivity().sendBroadcast(intent);
		
		// FIXME Wait for condition with timeout
		Thread.sleep(1000);
		
		final ListView listView = (ListView)activity.findViewById(android.R.id.list);
		
		TwoLineListItem headerItem = (TwoLineListItem)TestUtils.selectAndFindListViewChildAt(activity, listView, 0, 3000);
		
		assertFalse(headerItem.isEnabled());
		assertEquals(activity.getString(R.string.locationlist_add_wifi_location), headerItem.getText1().getText());
		assertEquals(activity.getString(R.string.wifi_disconnected), headerItem.getText2().getText());
		
		activity.finish();
	}
	
	public void testAddWifiDisconnected() throws Exception {
		
		LocationList activity = this.getActivity();
		IWifiInfo wifiInfo = new WifiInfoMock().setBSSID("TestBSSID").setSSID("TestSSID");
		IWifiManager wifiManager = new WifiManagerMock(wifiInfo);
		TestUtils.setFieldValue(activity, "wifiManager", wifiManager);
		
		Intent intent = new Intent(WifiManager.NETWORK_STATE_CHANGED_ACTION);
		NetworkInfo networkInfo = TestUtils.createNetworkInfo(ConnectivityManager.TYPE_WIFI, false);
		intent.putExtra(WifiManager.EXTRA_NETWORK_INFO, networkInfo);
		this.getActivity().sendBroadcast(intent);
		
		// FIXME Wait for condition with timeout
		Thread.sleep(1000);
		
		Intent intentAddLocation = new Intent();
		intentAddLocation.setAction(LocationList.ADD_LOCATION_ACTION);
		intentAddLocation.putExtra(LocationList.EXTRA_LOCATION, new Location(LocationManager.GPS_PROVIDER));
		activity.sendBroadcast(intentAddLocation);
		
		final ListView listView = (ListView)activity.findViewById(android.R.id.list);
		
		// As "gimmick", it is possible to add a location even if Wifi is not connected
		TestUtils.waitForItemCount(listView, 2, 10000);
		
		final TwoLineListItem addedItem = (TwoLineListItem)TestUtils.selectAndFindListViewChildAt(activity, listView, 1, 3000);
		
		assertTrue(addedItem.isEnabled());
		assertEquals(activity.getString(R.string.disconnected), addedItem.getText1().getText());
		// TODO Not sure what to show here with this "hidden feature"
		// assertEquals("?", addedItem.getText2().getText());
		
		activity.finish();
	}
	
	public void testAddWifiConnected() throws Exception {
		
		LocationList activity = this.getActivity();
		IWifiInfo wifiInfo = new WifiInfoMock().setBSSID("TestBSSID").setSSID("TestSSID");
		IWifiManager wifiManager = new WifiManagerMock(wifiInfo);
		TestUtils.setFieldValue(activity, "wifiManager", wifiManager);
		
		Intent intent = new Intent(WifiManager.NETWORK_STATE_CHANGED_ACTION);
		NetworkInfo networkInfo = TestUtils.createNetworkInfo(ConnectivityManager.TYPE_WIFI, true);
		intent.putExtra(WifiManager.EXTRA_NETWORK_INFO, networkInfo);
		this.getActivity().sendBroadcast(intent);
		
		// FIXME Wait for condition with timeout
		Thread.sleep(1000);
		
		Intent intentAddLocation = new Intent();
		intentAddLocation.setAction(LocationList.ADD_LOCATION_ACTION);
		intentAddLocation.putExtra(LocationList.EXTRA_LOCATION, new Location(LocationManager.GPS_PROVIDER));
		activity.sendBroadcast(intentAddLocation);
		
		final ListView listView = (ListView)activity.findViewById(android.R.id.list);
		
		TestUtils.waitForItemCount(listView, 2, 10000);
		
		final TwoLineListItem addedItem = (TwoLineListItem)TestUtils.selectAndFindListViewChildAt(activity, listView, 1, 3000);
		
		assertTrue(addedItem.isEnabled());
		assertEquals("TestSSID", addedItem.getText1().getText());
		assertEquals("TestBSSID", addedItem.getText2().getText());
				
		activity.finish();
	}
	
	public void testListPopulated() throws InterruptedException {
		
		insertTestData();
		
		LocationList activity = this.getActivity();
		
		final ListView listView = (ListView)activity.findViewById(android.R.id.list);
		
		TwoLineListItem listItem1 = (TwoLineListItem)TestUtils.selectAndFindListViewChildAt(activity, listView, 1, 3000);
		TwoLineListItem listItem2 = (TwoLineListItem)TestUtils.selectAndFindListViewChildAt(activity, listView, 2, 3000);
		TwoLineListItem listItem3 = (TwoLineListItem)TestUtils.selectAndFindListViewChildAt(activity, listView, 3, 3000);
		TwoLineListItem listItem4 = (TwoLineListItem)TestUtils.selectAndFindListViewChildAt(activity, listView, 4, 3000);
		
		assertTrue(listItem1.isEnabled());
		assertEquals("Celsten", listItem1.getText1().getText());
		assertEquals("00:21:29:A2:48:80", listItem1.getText2().getText());

		assertTrue(listItem2.isEnabled());
		assertEquals("Test1", listItem2.getText1().getText());
		assertEquals("00:11:22:33:44:55", listItem2.getText2().getText());

		assertTrue(listItem3.isEnabled());
		assertEquals("Test2", listItem3.getText1().getText());
		assertEquals("00:66:77:88:99:00", listItem3.getText2().getText());
		
		assertTrue(listItem4.isEnabled());
		assertEquals("Test3", listItem4.getText1().getText());
		assertEquals("00:99:11:22:33:44", listItem4.getText2().getText());
		
		activity.finish();
	}
	
	public void testDelete() throws InterruptedException {
		
		insertTestData();
		
		LocationList activity = this.getActivity();
		
		final ListView listView = (ListView)activity.findViewById(android.R.id.list);
		
		final TwoLineListItem lastItem = (TwoLineListItem)TestUtils.selectAndFindListViewChildAt(activity, listView, 4, 3000);
		
		TestUtils.performLongClickOnUIThread(activity, lastItem);
		
		AlertDialog contextDialog = (AlertDialog)TestUtils.waitForCurrentDialogShowing(activity, 10000);
		
		TestUtils.performItemClickOnUIThread(activity, contextDialog.getListView(), null, 1);
		
		TestUtils.waitForDialogNotShowing(contextDialog, 10000);
		
		AlertDialog confirmDialog = (AlertDialog)TestUtils.waitForCurrentDialogShowing(activity, 10000);
		
		TestUtils.performClickOnUIThread(activity, confirmDialog.getButton(AlertDialog.BUTTON_POSITIVE));
		
		TestUtils.waitForDialogNotShowing(confirmDialog, 10000);
		
		TestUtils.waitForItemCount(listView, 4, 10000);
		
		TwoLineListItem listItem1 = (TwoLineListItem)TestUtils.selectAndFindListViewChildAt(activity, listView, 1, 3000);
		TwoLineListItem listItem2 = (TwoLineListItem)TestUtils.selectAndFindListViewChildAt(activity, listView, 2, 3000);
		TwoLineListItem listItem3 = (TwoLineListItem)TestUtils.selectAndFindListViewChildAt(activity, listView, 3, 3000);
		
		assertEquals("Celsten", listItem1.getText1().getText());
		assertEquals("00:21:29:A2:48:80", listItem1.getText2().getText());

		assertEquals("Test1", listItem2.getText1().getText());
		assertEquals("00:11:22:33:44:55", listItem2.getText2().getText());

		assertEquals("Test2", listItem3.getText1().getText());
		assertEquals("00:66:77:88:99:00", listItem3.getText2().getText());
		
		activity.finish();
	}
	
	public void testDeleteCancel() throws InterruptedException {
		
		insertTestData();
		
		LocationList activity = this.getActivity();
		
		final ListView listView = (ListView)activity.findViewById(android.R.id.list);
		
		final TwoLineListItem firstItem = (TwoLineListItem)TestUtils.selectAndFindListViewChildAt(activity, listView, 1, 3000);
		
		TestUtils.performLongClickOnUIThread(activity, firstItem);
		
		AlertDialog contextDialog = (AlertDialog)TestUtils.waitForCurrentDialogShowing(activity, 10000);
		
		TestUtils.performItemClickOnUIThread(activity, contextDialog.getListView(), null, 1);
		
		TestUtils.waitForDialogNotShowing(contextDialog, 10000);
		
		AlertDialog confirmDialog = (AlertDialog)TestUtils.waitForCurrentDialogShowing(activity, 10000);
		
		TestUtils.performClickOnUIThread(activity, confirmDialog.getButton(AlertDialog.BUTTON_NEGATIVE));
		
		TestUtils.waitForDialogNotShowing(confirmDialog, 10000);
		
		TestUtils.waitForItemCount(listView, 5, 10000);
		
		TwoLineListItem listItem1 = (TwoLineListItem)TestUtils.selectAndFindListViewChildAt(activity, listView, 1, 3000);
		TwoLineListItem listItem2 = (TwoLineListItem)TestUtils.selectAndFindListViewChildAt(activity, listView, 2, 3000);
		TwoLineListItem listItem3 = (TwoLineListItem)TestUtils.selectAndFindListViewChildAt(activity, listView, 3, 3000);
		TwoLineListItem listItem4 = (TwoLineListItem)TestUtils.selectAndFindListViewChildAt(activity, listView, 4, 3000);
		
		assertTrue(listItem1.isEnabled());
		assertEquals("Celsten", listItem1.getText1().getText());
		assertEquals("00:21:29:A2:48:80", listItem1.getText2().getText());

		assertTrue(listItem2.isEnabled());
		assertEquals("Test1", listItem2.getText1().getText());
		assertEquals("00:11:22:33:44:55", listItem2.getText2().getText());

		assertTrue(listItem3.isEnabled());
		assertEquals("Test2", listItem3.getText1().getText());
		assertEquals("00:66:77:88:99:00", listItem3.getText2().getText());
		
		assertTrue(listItem4.isEnabled());
		assertEquals("Test3", listItem4.getText1().getText());
		assertEquals("00:99:11:22:33:44", listItem4.getText2().getText());
		
		activity.finish();
	}
	
	public void testRename() throws InterruptedException {
		
		insertTestData();
		
		LocationList activity = this.getActivity();
		
		final ListView listView = (ListView)activity.findViewById(android.R.id.list);
		
		final TwoLineListItem firstItem = (TwoLineListItem)TestUtils.selectAndFindListViewChildAt(activity, listView, 1, 3000);
		
		TestUtils.performLongClickOnUIThread(activity, firstItem);
		
		AlertDialog contextDialog = (AlertDialog)TestUtils.waitForCurrentDialogShowing(activity, 10000);
		
		TestUtils.performItemClickOnUIThread(activity, contextDialog.getListView(), null, 0);
		
		TestUtils.waitForDialogNotShowing(contextDialog, 10000);
		
		InputDialog confirmDialog = (InputDialog)TestUtils.waitForCurrentDialogShowing(activity, 10000);
		
		confirmDialog.setInputText("Dode's Wifi");
		
		TestUtils.performClickOnUIThread(activity, confirmDialog.getButton(AlertDialog.BUTTON_POSITIVE));
		
		TestUtils.waitForDialogNotShowing(confirmDialog, 10000);
		
		TestUtils.waitForItemCount(listView, 5, 10000);
		
		TwoLineListItem listItem1 = (TwoLineListItem)TestUtils.selectAndFindListViewChildAt(activity, listView, 1, 3000);
		TwoLineListItem listItem2 = (TwoLineListItem)TestUtils.selectAndFindListViewChildAt(activity, listView, 2, 3000);
		TwoLineListItem listItem3 = (TwoLineListItem)TestUtils.selectAndFindListViewChildAt(activity, listView, 3, 3000);
		TwoLineListItem listItem4 = (TwoLineListItem)TestUtils.selectAndFindListViewChildAt(activity, listView, 4, 3000);
		
		assertTrue(listItem1.isEnabled());
		assertEquals("Dode's Wifi", listItem1.getText1().getText());
		assertEquals("00:21:29:A2:48:80", listItem1.getText2().getText());
		
		assertEquals("Test1", listItem2.getText1().getText());
		assertEquals("00:11:22:33:44:55", listItem2.getText2().getText());

		assertEquals("Test2", listItem3.getText1().getText());
		assertEquals("00:66:77:88:99:00", listItem3.getText2().getText());
		
		assertEquals("Test3", listItem4.getText1().getText());
		assertEquals("00:99:11:22:33:44", listItem4.getText2().getText());
		
		activity.finish();
	}
	
	public void testRenameCancel() throws InterruptedException {
		
		insertTestData();
		
		LocationList activity = this.getActivity();
		
		final ListView listView = (ListView)activity.findViewById(android.R.id.list);
		
		final TwoLineListItem firstItem = (TwoLineListItem)TestUtils.selectAndFindListViewChildAt(activity, listView, 1, 3000);
		
		TestUtils.performLongClickOnUIThread(activity, firstItem);
		
		AlertDialog contextDialog = (AlertDialog)TestUtils.waitForCurrentDialogShowing(activity, 10000);
		
		TestUtils.performItemClickOnUIThread(activity, contextDialog.getListView(), null, 0);
		
		TestUtils.waitForDialogNotShowing(contextDialog, 10000);
		
		InputDialog confirmDialog = (InputDialog)TestUtils.waitForCurrentDialogShowing(activity, 10000);
		
		confirmDialog.setInputText("Dode's Wifi");
		
		TestUtils.performClickOnUIThread(activity, confirmDialog.getButton(AlertDialog.BUTTON_NEGATIVE));
		
		TestUtils.waitForDialogNotShowing(confirmDialog, 10000);
		
		TestUtils.waitForItemCount(listView, 5, 10000);
		
		TwoLineListItem listItem1 = (TwoLineListItem)TestUtils.selectAndFindListViewChildAt(activity, listView, 1, 3000);
		TwoLineListItem listItem2 = (TwoLineListItem)TestUtils.selectAndFindListViewChildAt(activity, listView, 2, 3000);
		TwoLineListItem listItem3 = (TwoLineListItem)TestUtils.selectAndFindListViewChildAt(activity, listView, 3, 3000);
		TwoLineListItem listItem4 = (TwoLineListItem)TestUtils.selectAndFindListViewChildAt(activity, listView, 4, 3000);
		
		assertTrue(listItem1.isEnabled());
		assertEquals("Celsten", listItem1.getText1().getText());
		assertEquals("00:21:29:A2:48:80", listItem1.getText2().getText());
		
		assertEquals("Test1", listItem2.getText1().getText());
		assertEquals("00:11:22:33:44:55", listItem2.getText2().getText());

		assertEquals("Test2", listItem3.getText1().getText());
		assertEquals("00:66:77:88:99:00", listItem3.getText2().getText());
		
		assertEquals("Test3", listItem4.getText1().getText());
		assertEquals("00:99:11:22:33:44", listItem4.getText2().getText());
		
		activity.finish();
	}
	
	private void insertTestData() {
		DatabaseAdapter databaseAdapter = new DatabaseAdapterImpl(this.getInstrumentation().getTargetContext());
		databaseAdapter.addLocation("00:21:29:A2:48:80", "Celsten", "Celsten", TestUtils.createLocation(0.1, 0.1, 10));
		databaseAdapter.addLocation("00:11:22:33:44:55", "TestSSID1", "Test1", TestUtils.createLocation(0.2, 0.2, 20));
		databaseAdapter.addLocation("00:66:77:88:99:00", "TestSSID2", "Test2", TestUtils.createLocation(0.3, 0.3, 30));
		databaseAdapter.addLocation("00:99:11:22:33:44", "TestSSID3", "Test3", TestUtils.createLocation(0.4, 0.4, 40));
		databaseAdapter.close();
	}
	
}
