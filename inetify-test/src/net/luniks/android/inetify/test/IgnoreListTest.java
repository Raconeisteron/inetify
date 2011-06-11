package net.luniks.android.inetify.test;

import net.luniks.android.inetify.DatabaseAdapter;
import net.luniks.android.inetify.DatabaseAdapterImpl;
import net.luniks.android.inetify.IgnoreList;
import net.luniks.android.inetify.R;
import android.test.ActivityInstrumentationTestCase2;
import android.widget.ListView;
import android.widget.TextView;
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
	
	public void testListEmpty() {
		
		IgnoreList activity = this.getActivity();
		
		TextView textViewName = (TextView)activity.findViewById(android.R.id.empty);
		
		assertEquals(activity.getString(R.string.ignorelist_empty), textViewName.getText().toString());
		
		ListView listView = (ListView)activity.findViewById(android.R.id.list);
		
		assertEquals(0, listView.getChildCount());
		
		activity.finish();
	}
	
	public void testListPopulated() {
		
		insertTestData();
		
		IgnoreList activity = this.getActivity();
		
		final ListView listView = (ListView)activity.findViewById(android.R.id.list);
		
		assertEquals(3, listView.getChildCount());
		
		TwoLineListItem listItem0 = (TwoLineListItem)listView.getChildAt(0);
		TwoLineListItem listItem1 = (TwoLineListItem)listView.getChildAt(1);
		TwoLineListItem listItem2 = (TwoLineListItem)listView.getChildAt(2);
		
		// Why isClickable() == false?
		// assertTrue(listItem0.isClickable());
		assertTrue(listItem0.isEnabled());
		assertEquals("Celsten", listItem0.getText1().getText());
		assertEquals("00:21:29:A2:48:80", listItem0.getText2().getText());

		assertTrue(listItem1.isEnabled());
		assertEquals("TestSSID1", listItem1.getText1().getText());
		assertEquals("00:11:22:33:44:55", listItem1.getText2().getText());

		assertTrue(listItem2.isEnabled());
		assertEquals("TestSSID2", listItem2.getText1().getText());
		assertEquals("00:66:77:88:99:00", listItem2.getText2().getText());
		
		activity.finish();
	}
	
	public void testDelete() throws InterruptedException {
		
		insertTestData();
		
		IgnoreList activity = this.getActivity();
		
		// TODO How to test dialogs?
		activity.setSkipConfirmDeleteDialog(true);
		
		final ListView listView = (ListView)activity.findViewById(android.R.id.list);
		
		final TwoLineListItem firstItem = (TwoLineListItem)listView.getChildAt(0);
		
		assertEquals(3, listView.getChildCount());
		
		Runnable click = new Runnable() {
			public void run() {
				// TODO Long click?
				listView.performItemClick(firstItem, 0, 0);
			}
		};
		activity.runOnUiThread(click);
		
		TestUtils.waitForChildCount(listView, 2, 10000);
		
		assertEquals(2, listView.getChildCount());
		
		TwoLineListItem listItem0 = (TwoLineListItem)listView.getChildAt(0);
		TwoLineListItem listItem1 = (TwoLineListItem)listView.getChildAt(1);
		
		assertEquals("TestSSID1", listItem0.getText1().getText());
		assertEquals("00:11:22:33:44:55", listItem0.getText2().getText());

		assertEquals("TestSSID2", listItem1.getText1().getText());
		assertEquals("00:66:77:88:99:00", listItem1.getText2().getText());
		
		activity.finish();
	}
	
	private void insertTestData() {
		DatabaseAdapter databaseAdapter = new DatabaseAdapterImpl(this.getInstrumentation().getTargetContext());
		databaseAdapter.addIgnoredWifi("00:21:29:A2:48:80", "Celsten");
		databaseAdapter.addIgnoredWifi("00:11:22:33:44:55", "TestSSID1");
		databaseAdapter.addIgnoredWifi("00:66:77:88:99:00", "TestSSID2");
	}
	
}
