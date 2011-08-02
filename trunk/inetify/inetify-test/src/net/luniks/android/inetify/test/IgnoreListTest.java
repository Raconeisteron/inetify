package net.luniks.android.inetify.test;

import net.luniks.android.inetify.DatabaseAdapter;
import net.luniks.android.inetify.DatabaseAdapterImpl;
import net.luniks.android.inetify.IgnoreList;
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


public class IgnoreListTest extends ActivityInstrumentationTestCase2<IgnoreList> {
	
	public IgnoreListTest() {
		super("net.luniks.android.inetify", IgnoreList.class);
	}
	
	public void setUp() throws Exception {
		super.setUp();
		this.getInstrumentation().getTargetContext().deleteDatabase("inetifydb");
		this.getInstrumentation().getTargetContext().deleteDatabase("inetifydb-journal");
	}
	
	public void testListEmptyWifiDisconnected() throws Exception {
		
		IgnoreList activity = this.getActivity();
		IWifiInfo wifiInfo = new WifiInfoMock().setBSSID("TestBSSID").setSSID("TestSSID");
		IWifiManager wifiManager = new WifiManagerMock(wifiInfo);
		TestUtils.setFieldValue(activity, "wifiManager", wifiManager);
		
		Intent intent = new Intent(WifiManager.NETWORK_STATE_CHANGED_ACTION);
		NetworkInfo networkInfo = TestUtils.createNetworkInfo(ConnectivityManager.TYPE_WIFI, false);
		intent.putExtra(WifiManager.EXTRA_NETWORK_INFO, networkInfo);
		this.getActivity().sendBroadcast(intent);
		
		final ListView listView = (ListView)activity.findViewById(android.R.id.list);
		
		TwoLineListItem headerView = (TwoLineListItem)TestUtils.selectAndFindListViewChildAt(activity, listView, 0, 3000);
		
		assertFalse(headerView.isEnabled());
		assertEquals(activity.getString(R.string.ignorelist_add_ignored_wifi), headerView.getText1().getText());
		assertEquals(activity.getString(R.string.wifi_disconnected), headerView.getText2().getText());
		
		activity.finish();
	}
	
	public void testListEmptyWifiConnected() throws Exception {
		
		IgnoreList activity = this.getActivity();
		IWifiInfo wifiInfo = new WifiInfoMock().setBSSID("TestBSSID").setSSID("TestSSID");
		IWifiManager wifiManager = new WifiManagerMock(wifiInfo);
		TestUtils.setFieldValue(activity, "wifiManager", wifiManager);
		
		final Intent intent = new Intent(WifiManager.NETWORK_STATE_CHANGED_ACTION);
		NetworkInfo networkInfo = TestUtils.createNetworkInfo(ConnectivityManager.TYPE_WIFI, true);
		intent.putExtra(WifiManager.EXTRA_NETWORK_INFO, networkInfo);
		
		this.getActivity().sendBroadcast(intent);
		
		// FIXME Wait for condition with timeout
		Thread.sleep(1000);
		
		final ListView listView = (ListView)activity.findViewById(android.R.id.list);
		
		TwoLineListItem headerItem = (TwoLineListItem)TestUtils.selectAndFindListViewChildAt(activity, listView, 0, 3000);
		
		assertTrue(headerItem.isEnabled());
		assertEquals(activity.getString(R.string.ignorelist_add_ignored_wifi), headerItem.getText1().getText());
		assertEquals(activity.getString(R.string.ignorelist_ignore_wifi, "TestSSID"), headerItem.getText2().getText());
		
		activity.finish();
	}
	
	public void testListEmptyWifiConnectedWifiInfoNull() throws Exception {
		
		IgnoreList activity = this.getActivity();
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
		assertEquals(activity.getString(R.string.ignorelist_add_ignored_wifi), headerItem.getText1().getText());
		assertEquals(activity.getString(R.string.wifi_disconnected), headerItem.getText2().getText());
		
		activity.finish();
	}
	
	public void testAddWifiDisconnected() throws Exception {
		
		IgnoreList activity = this.getActivity();
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
		
		// A not connected Wifi is not added to the ignore list...
		TestUtils.waitForItemCount(listView, 1, 10000);
		
		activity.finish();
	}
	
	public void testAddWifiConnected() throws Exception {
		
		IgnoreList activity = this.getActivity();
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
		
		TestUtils.performItemClickOnUIThread(activity, listView, null, 0);
		
		TestUtils.waitForItemCount(listView, 2, 10000);
		
		final TwoLineListItem addedItem = (TwoLineListItem)TestUtils.selectAndFindListViewChildAt(activity, listView, 1, 3000);
		
		assertTrue(addedItem.isEnabled());
		assertEquals("TestSSID", addedItem.getText1().getText());
		assertEquals("TestBSSID", addedItem.getText2().getText());
				
		activity.finish();
	}
	
	public void testListPopulated() throws InterruptedException {
		
		insertTestData();
		
		IgnoreList activity = this.getActivity();
		
		final ListView listView = (ListView)activity.findViewById(android.R.id.list);
		
		TwoLineListItem listItem1 = (TwoLineListItem)TestUtils.selectAndFindListViewChildAt(activity, listView, 1, 3000);
		TwoLineListItem listItem2 = (TwoLineListItem)TestUtils.selectAndFindListViewChildAt(activity, listView, 2, 3000);
		TwoLineListItem listItem3 = (TwoLineListItem)TestUtils.selectAndFindListViewChildAt(activity, listView, 3, 3000);
		
		assertTrue(listItem1.isEnabled());
		assertEquals("Celsten", listItem1.getText1().getText());
		assertEquals("00:21:29:A2:48:80", listItem1.getText2().getText());

		assertTrue(listItem2.isEnabled());
		assertEquals("TestSSID1", listItem2.getText1().getText());
		assertEquals("00:11:22:33:44:55", listItem2.getText2().getText());

		assertTrue(listItem3.isEnabled());
		assertEquals("TestSSID2", listItem3.getText1().getText());
		assertEquals("00:66:77:88:99:00", listItem3.getText2().getText());
		
		activity.finish();
	}
	
	public void testDelete() throws InterruptedException {
		
		insertTestData();
		
		IgnoreList activity = this.getActivity();
		
		final ListView listView = (ListView)activity.findViewById(android.R.id.list);
		
		final TwoLineListItem firstItem = (TwoLineListItem)TestUtils.selectAndFindListViewChildAt(activity, listView, 1, 3000);
		
		TestUtils.performLongClickOnUIThread(activity, firstItem);
		
		AlertDialog contextDialog = (AlertDialog)TestUtils.waitForCurrentDialogShowing(activity, 10000);
		
		TestUtils.performItemClickOnUIThread(activity, contextDialog.getListView(), null, 0);
		
		TestUtils.waitForDialogNotShowing(contextDialog, 10000);
		
		AlertDialog confirmDialog = (AlertDialog)TestUtils.waitForCurrentDialogShowing(activity, 10000);
		
		TestUtils.performClickOnUIThread(activity, confirmDialog.getButton(AlertDialog.BUTTON_POSITIVE));
		
		TestUtils.waitForDialogNotShowing(confirmDialog, 10000);
		
		TestUtils.waitForItemCount(listView, 3, 10000);
		
		TwoLineListItem listItem1 = (TwoLineListItem)TestUtils.selectAndFindListViewChildAt(activity, listView, 1, 3000);
		TwoLineListItem listItem2 = (TwoLineListItem)TestUtils.selectAndFindListViewChildAt(activity, listView, 2, 3000);
		
		assertEquals("TestSSID1", listItem1.getText1().getText());
		assertEquals("00:11:22:33:44:55", listItem1.getText2().getText());

		assertEquals("TestSSID2", listItem2.getText1().getText());
		assertEquals("00:66:77:88:99:00", listItem2.getText2().getText());
		
		activity.finish();
	}
	
	public void testDeleteCancel() throws InterruptedException {
		
		insertTestData();
		
		IgnoreList activity = this.getActivity();
		
		final ListView listView = (ListView)activity.findViewById(android.R.id.list);
		
		final TwoLineListItem firstItem = (TwoLineListItem)TestUtils.selectAndFindListViewChildAt(activity, listView, 1, 3000);
		
		TestUtils.performLongClickOnUIThread(activity, firstItem);
		
		AlertDialog contextDialog = (AlertDialog)TestUtils.waitForCurrentDialogShowing(activity, 10000);
		
		TestUtils.performItemClickOnUIThread(activity, contextDialog.getListView(), null, 0);
		
		TestUtils.waitForDialogNotShowing(contextDialog, 10000);
		
		AlertDialog confirmDialog = (AlertDialog)TestUtils.waitForCurrentDialogShowing(activity, 10000);
		
		TestUtils.performClickOnUIThread(activity, confirmDialog.getButton(AlertDialog.BUTTON_NEGATIVE));
		
		TestUtils.waitForDialogNotShowing(confirmDialog, 10000);
		
		TestUtils.waitForItemCount(listView, 4, 10000);
		
		TwoLineListItem listItem1 = (TwoLineListItem)TestUtils.selectAndFindListViewChildAt(activity, listView, 1, 3000);
		TwoLineListItem listItem2 = (TwoLineListItem)TestUtils.selectAndFindListViewChildAt(activity, listView, 2, 3000);
		TwoLineListItem listItem3 = (TwoLineListItem)TestUtils.selectAndFindListViewChildAt(activity, listView, 3, 3000);
		
		assertTrue(listItem1.isEnabled());
		assertEquals("Celsten", listItem1.getText1().getText());
		assertEquals("00:21:29:A2:48:80", listItem1.getText2().getText());

		assertTrue(listItem2.isEnabled());
		assertEquals("TestSSID1", listItem2.getText1().getText());
		assertEquals("00:11:22:33:44:55", listItem2.getText2().getText());

		assertTrue(listItem3.isEnabled());
		assertEquals("TestSSID2", listItem3.getText1().getText());
		assertEquals("00:66:77:88:99:00", listItem3.getText2().getText());
		
		activity.finish();
	}
	
	private void insertTestData() {
		DatabaseAdapter databaseAdapter = new DatabaseAdapterImpl(this.getInstrumentation().getTargetContext());
		databaseAdapter.addIgnoredWifi("00:21:29:A2:48:80", "Celsten");
		databaseAdapter.addIgnoredWifi("00:11:22:33:44:55", "TestSSID1");
		databaseAdapter.addIgnoredWifi("00:66:77:88:99:00", "TestSSID2");
		databaseAdapter.close();
	}

}
