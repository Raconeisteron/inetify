package net.luniks.android.inetify.test;

import net.luniks.android.inetify.DatabaseAdapter;
import net.luniks.android.inetify.DatabaseAdapterImpl;
import net.luniks.android.inetify.LocationList;
import net.luniks.android.inetify.R;
import net.luniks.android.interfaces.IWifiInfo;
import net.luniks.android.interfaces.IWifiManager;
import net.luniks.android.test.mock.WifiInfoMock;
import net.luniks.android.test.mock.WifiManagerMock;
import android.content.Intent;
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
		activity.setWifiManager(wifiManager);
		
		Intent intent = new Intent(WifiManager.NETWORK_STATE_CHANGED_ACTION);
		NetworkInfo networkInfo = TestUtils.createNetworkInfo(this.getActivity(), ConnectivityManager.TYPE_WIFI, false);
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
		activity.setWifiManager(wifiManager);
		
		Intent intent = new Intent(WifiManager.NETWORK_STATE_CHANGED_ACTION);
		NetworkInfo networkInfo = TestUtils.createNetworkInfo(this.getActivity(), ConnectivityManager.TYPE_WIFI, true);
		intent.putExtra(WifiManager.EXTRA_NETWORK_INFO, networkInfo);
		this.getActivity().sendBroadcast(intent);
		
		// TODO Wait for condition with timeout
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
		activity.setWifiManager(wifiManager);
		
		Intent intent = new Intent(WifiManager.NETWORK_STATE_CHANGED_ACTION);
		NetworkInfo networkInfo = TestUtils.createNetworkInfo(this.getActivity(), ConnectivityManager.TYPE_WIFI, true);
		intent.putExtra(WifiManager.EXTRA_NETWORK_INFO, networkInfo);
		this.getActivity().sendBroadcast(intent);
		
		// TODO Wait for condition with timeout
		Thread.sleep(1000);
		
		final ListView listView = (ListView)activity.findViewById(android.R.id.list);
		
		TwoLineListItem headerItem = (TwoLineListItem)TestUtils.selectAndFindListViewChildAt(activity, listView, 0, 3000);
		
		assertFalse(headerItem.isEnabled());
		assertEquals(activity.getString(R.string.locationlist_add_wifi_location), headerItem.getText1().getText());
		assertEquals(activity.getString(R.string.wifi_disconnected), headerItem.getText2().getText());
		
		activity.finish();
	}
	
	public void testListPopulated() throws InterruptedException {
		
		insertTestData();
		
		LocationList activity = this.getActivity();
		
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
		
		LocationList activity = this.getActivity();
		
		// FIXME How to test dialogs?
		// activity.setSkipConfirmDeleteDialog(true);
		
		final ListView listView = (ListView)activity.findViewById(android.R.id.list);
		
		final TwoLineListItem firstItem = (TwoLineListItem)TestUtils.selectAndFindListViewChildAt(activity, listView, 1, 3000);
		
		Runnable click = new Runnable() {
			public void run() {
				firstItem.performLongClick();
			}
		};
		activity.runOnUiThread(click);
		
		TestUtils.waitForItemCount(listView, 3, 10000);
		
		TwoLineListItem listItem1 = (TwoLineListItem)TestUtils.selectAndFindListViewChildAt(activity, listView, 1, 3000);
		TwoLineListItem listItem2 = (TwoLineListItem)TestUtils.selectAndFindListViewChildAt(activity, listView, 2, 3000);
		
		assertEquals("TestSSID1", listItem1.getText1().getText());
		assertEquals("00:11:22:33:44:55", listItem1.getText2().getText());

		assertEquals("TestSSID2", listItem2.getText1().getText());
		assertEquals("00:66:77:88:99:00", listItem2.getText2().getText());
		
		activity.finish();
	}
	
	private void insertTestData() {
		DatabaseAdapter databaseAdapter = new DatabaseAdapterImpl(this.getInstrumentation().getTargetContext());
		databaseAdapter.addLocation("00:21:29:A2:48:80", "Celsten", "Celsten", TestUtils.createLocation(0.1, 0.1, 10));
		databaseAdapter.addLocation("00:11:22:33:44:55", "TestSSID1", "Test1", TestUtils.createLocation(0.2, 0.2, 20));
		databaseAdapter.addLocation("00:66:77:88:99:00", "TestSSID2", "Test2", TestUtils.createLocation(0.3, 0.3, 30));
	}
	
}
