package net.luniks.android.inetify.test;

import net.luniks.android.inetify.DatabaseAdapter;
import net.luniks.android.inetify.DatabaseAdapterImpl;
import net.luniks.android.inetify.LocationList;
import net.luniks.android.inetify.R;
import android.test.ActivityInstrumentationTestCase2;
import android.test.UiThreadTest;
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
	
	@UiThreadTest
	public void testListEmptyWifiDisconnected() throws InterruptedException {
		
		LocationList activity = this.getActivity();
		TestTester tester = new TestTester();
		tester.setWifiConnected(false);
		activity.setTester(tester);
		
		this.getInstrumentation().callActivityOnPause(activity);
		this.getInstrumentation().callActivityOnResume(activity);
		
		final ListView listView = (ListView)activity.findViewById(android.R.id.list);
		
		TwoLineListItem headerItem = (TwoLineListItem)TestUtils.selectAndFindListViewChildAt(activity, listView, 0, 3000);
		
		assertFalse(headerItem.isEnabled());
		assertEquals(activity.getString(R.string.locationlist_add_wifi_location), headerItem.getText1().getText());
		assertEquals(activity.getString(R.string.locationlist_wifi_disconnected), headerItem.getText2().getText());
		
		activity.finish();
	}
	
	public void testListEmptyWifiConnected() throws InterruptedException {
		
		LocationList activity = this.getActivity();
		TestTester tester = new TestTester();
		tester.setWifiConnected(true);
		activity.setTester(tester);
		
		final ListView listView = (ListView)activity.findViewById(android.R.id.list);
		
		TwoLineListItem headerItem = (TwoLineListItem)TestUtils.selectAndFindListViewChildAt(activity, listView, 0, 3000);
		
		assertTrue(headerItem.isEnabled());
		assertEquals(activity.getString(R.string.locationlist_add_wifi_location), headerItem.getText1().getText());
		assertEquals(activity.getString(R.string.locationlist_add_location_of_wifi, "Celsten"), headerItem.getText2().getText());
		
		activity.finish();
	}
	
	public void testListPopulated() throws InterruptedException {
		
		insertTestData();
		
		LocationList activity = this.getActivity();
		activity.setTester(new TestTester());
		
		final ListView listView = (ListView)activity.findViewById(android.R.id.list);
		
		TwoLineListItem headerItem = (TwoLineListItem)TestUtils.selectAndFindListViewChildAt(activity, listView, 0, 3000);
		TwoLineListItem listItem1 = (TwoLineListItem)TestUtils.selectAndFindListViewChildAt(activity, listView, 1, 3000);
		TwoLineListItem listItem2 = (TwoLineListItem)TestUtils.selectAndFindListViewChildAt(activity, listView, 2, 3000);
		TwoLineListItem listItem3 = (TwoLineListItem)TestUtils.selectAndFindListViewChildAt(activity, listView, 3, 3000);
		
		assertTrue(headerItem.isEnabled());
		assertEquals(activity.getString(R.string.locationlist_add_wifi_location), headerItem.getText1().getText());
		assertEquals(activity.getString(R.string.locationlist_add_location_of_wifi, "Celsten"), headerItem.getText2().getText());
		
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
		
		// TODO How to test dialogs?
		activity.setSkipConfirmDeleteDialog(true);
		
		final ListView listView = (ListView)activity.findViewById(android.R.id.list);
		
		final TwoLineListItem firstItem = (TwoLineListItem)TestUtils.selectAndFindListViewChildAt(activity, listView, 0, 3000);
		
		Runnable click = new Runnable() {
			public void run() {
				// TODO Long click?
				listView.performItemClick(firstItem, 0, 0);
			}
		};
		activity.runOnUiThread(click);
		
		TestUtils.waitForItemCount(listView, 2, 10000);
		
		TwoLineListItem listItem0 = (TwoLineListItem)TestUtils.selectAndFindListViewChildAt(activity, listView, 0, 3000);
		TwoLineListItem listItem1 = (TwoLineListItem)TestUtils.selectAndFindListViewChildAt(activity, listView, 1, 3000);
		
		assertEquals("TestSSID1", listItem0.getText1().getText());
		assertEquals("00:11:22:33:44:55", listItem0.getText2().getText());

		assertEquals("TestSSID2", listItem1.getText1().getText());
		assertEquals("00:66:77:88:99:00", listItem1.getText2().getText());
		
		activity.finish();
	}
	
	private void insertTestData() {
		DatabaseAdapter databaseAdapter = new DatabaseAdapterImpl(this.getInstrumentation().getTargetContext());
		databaseAdapter.addLocation("00:21:29:A2:48:80", "Celsten", TestUtils.getLocation(0.1, 0.1, 10));
		databaseAdapter.addLocation("00:11:22:33:44:55", "TestSSID1", TestUtils.getLocation(0.2, 0.2, 20));
		databaseAdapter.addLocation("00:66:77:88:99:00", "TestSSID2", TestUtils.getLocation(0.3, 0.3, 30));
	}
	
}
