package net.luniks.android.inetify.test;

import net.luniks.android.inetify.DatabaseAdapter;
import net.luniks.android.inetify.DatabaseAdapterImpl;
import net.luniks.android.inetify.IgnoreList;
import net.luniks.android.inetify.R;
import android.content.Intent;
import android.net.ConnectivityManager;
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
	
	public void testListEmptyWifiDisconnected() throws InterruptedException {
		
		IgnoreList activity = this.getActivity();
		TestTester tester = new TestTester();
		tester.setWifiConnected(false);
		activity.setTester(tester);
		
		this.getActivity().sendBroadcast(new Intent(ConnectivityManager.CONNECTIVITY_ACTION));
		
		final ListView listView = (ListView)activity.findViewById(android.R.id.list);
		
		TwoLineListItem headerView = (TwoLineListItem)TestUtils.selectAndFindListViewChildAt(activity, listView, 0, 3000);
		
		assertFalse(headerView.isEnabled());
		assertEquals(activity.getString(R.string.ignorelist_add_ignored_wifi), headerView.getText1().getText());
		assertEquals(activity.getString(R.string.ignorelist_wifi_disconnected), headerView.getText2().getText());
		
		activity.finish();
	}
	
	public void testListEmptyWifiConnected() throws InterruptedException {
		
		IgnoreList activity = this.getActivity();
		TestTester tester = new TestTester();
		tester.setWifiConnected(true);
		activity.setTester(tester);
		
		this.getActivity().sendBroadcast(new Intent(WifiManager.WIFI_STATE_CHANGED_ACTION));
		
		// TODO Wait for condition with timeout
		Thread.sleep(1000);
		
		final ListView listView = (ListView)activity.findViewById(android.R.id.list);
		
		TwoLineListItem headerItem = (TwoLineListItem)TestUtils.selectAndFindListViewChildAt(activity, listView, 0, 3000);
		
		assertTrue(headerItem.isEnabled());
		assertEquals(activity.getString(R.string.ignorelist_add_ignored_wifi), headerItem.getText1().getText());
		assertEquals(activity.getString(R.string.ignorelist_ignore_wifi, "TesterSSID"), headerItem.getText2().getText());
		
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
		
		// TODO How to test dialogs?
		activity.setSkipConfirmDeleteDialog(true);
		
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
		databaseAdapter.addIgnoredWifi("00:21:29:A2:48:80", "Celsten");
		databaseAdapter.addIgnoredWifi("00:11:22:33:44:55", "TestSSID1");
		databaseAdapter.addIgnoredWifi("00:66:77:88:99:00", "TestSSID2");
	}
	
}
