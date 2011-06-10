package net.luniks.android.inetify.test;

import net.luniks.android.inetify.InfoDetail;
import net.luniks.android.inetify.R;
import net.luniks.android.inetify.TestInfo;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.test.ActivityInstrumentationTestCase2;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TwoLineListItem;


public class InfoDetailTest extends ActivityInstrumentationTestCase2<InfoDetail> {
	
	public InfoDetailTest() {
		super("net.luniks.android.inetify", InfoDetail.class);
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
		
		assertEquals(5, listView.getChildCount());
		
		assertListItems(activity, listView, info);
		
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
		
		assertEquals(5, listView.getChildCount());
		
		assertListItems(activity, listView, info);
		
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
	
	private InfoDetail getActivity(final TestInfo info) {
		
		Intent infoDetailIntent = new Intent(InfoDetail.class.getName());
		infoDetailIntent.putExtra(InfoDetail.EXTRA_TEST_INFO, info);
		
		this.setActivityIntent(infoDetailIntent);
		
		return this.getActivity();
	}
	
	private TestInfo getTestInfo() {
		
		TestInfo info = new TestInfo();
		info.setIsExpectedTitle(true);
		info.setType("TestWifi");
		info.setExtra("TestExtra");
		info.setSite("TestSite");
		info.setTitle("TestTitle");
		info.setPageTitle("TestPageTitle");
		info.setTimestamp(1234567890L);
		
		return info;
	}
	
	private void assertListItems(final InfoDetail activity, final ListView listView, final TestInfo info) {
		
		TwoLineListItem listItem0 = (TwoLineListItem)listView.getChildAt(0);
		TwoLineListItem listItem1 = (TwoLineListItem)listView.getChildAt(1);
		TwoLineListItem listItem2 = (TwoLineListItem)listView.getChildAt(2);
		TwoLineListItem listItem3 = (TwoLineListItem)listView.getChildAt(3);
		TwoLineListItem listItem4 = (TwoLineListItem)listView.getChildAt(4);
		
		// Why isClickable() == false?
		// assertTrue(listItem0.isClickable());
		assertFalse(listItem0.isEnabled());
		assertEquals(activity.getString(R.string.infodetail_prop_timestamp), listItem0.getText1().getText());
		assertEquals(activity.getString(R.string.infodetail_value_timestamp, activity.getDateTimeString(info.getTimestamp())), listItem0.getText2().getText());

		assertTrue(listItem1.isEnabled());
		assertEquals(activity.getString(R.string.infodetail_prop_connection), listItem1.getText1().getText());
		assertEquals(activity.getString(R.string.infodetail_value_connection, info.getType(), info.getExtra()), listItem1.getText2().getText());

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

		
	}
	
}
