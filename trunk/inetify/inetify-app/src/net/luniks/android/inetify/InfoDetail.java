package net.luniks.android.inetify;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

/**
 * Activity that shows detailed information about the status of internet connectivity.
 * 
 * @author dode@luniks.net
 */
public class InfoDetail extends Activity {

	/** Prop key used for SimpleAdapter */
	private static final String KEY_PROP = "prop";
	
	/** Value key used for SimpleAdapter */
	private static final String KEY_VALUE = "value";
	
	/** Lookup key used for the parcelable extra used to pass a TestInfo instance */
	public static final String EXTRA_TEST_INFO = "extraTestInfo";

	/** {@inheritDoc} */
	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		this.setContentView(R.layout.infodetail);

		Bundle extras = this.getIntent().getExtras();
		final TestInfo info = extras.getParcelable(EXTRA_TEST_INFO);
		
		TextView textViewInfodetail = (TextView)findViewById(R.id.textview_infodetail);
		int drawableResid = R.drawable.icon;
		int textResid = R.string.infodetail_na;
		
		if(info != null) {
			if(info.getIsExpectedTitle()) {
				drawableResid = R.drawable.icon_ok;
				textResid = R.string.inetify_info_string_ok;
			} else {
				drawableResid = R.drawable.icon_nok;
				textResid = R.string.inetify_info_string_nok;
			}
		}
		
		textViewInfodetail.setCompoundDrawablesWithIntrinsicBounds(drawableResid, 0, 0, 0);
		textViewInfodetail.setText(textResid);
		
		if(info == null) {
			return;
		}
		
		List<Map<String, String>> listViewData = buildListViewData(info);
		
		SimpleAdapter simpleAdapter = new SimpleAdapterAllItemsDisabled(this, listViewData, android.R.layout.simple_list_item_2, 
				new String[] { KEY_PROP, KEY_VALUE },
				new int[] { android.R.id.text1, android.R.id.text2 });
		
		ListView listViewInfodetail = (ListView)findViewById(R.id.listview_infodetail);
		listViewInfodetail.setAdapter(simpleAdapter);
	}
	
	/**
	 * Returns a list of maps used as data given to SimpleAdapter, created from the given TestInfo instance.
	 * @param info
	 * @return List<Map<String, String>>
	 */
	private List<Map<String, String>> buildListViewData(final TestInfo info) {
		List<Map<String, String>> list = new ArrayList<Map<String, String>>();
		
		Map<String, String> mapTimestamp = new HashMap<String, String>();
		mapTimestamp.put(KEY_PROP, getString(R.string.infodetail_prop_timestamp));
		mapTimestamp.put(KEY_VALUE, getString(R.string.infodetail_value_timestamp, getDateTimeString(info.getTimestamp())));
		list.add(mapTimestamp);
		
		Map<String, String> mapConnection = new HashMap<String, String>();
		mapConnection.put(KEY_PROP, getString(R.string.infodetail_prop_connection));
		mapConnection.put(KEY_VALUE, getString(R.string.infodetail_value_connection, info.getType(), info.getExtra()));
		list.add(mapConnection);
		
		Map<String, String> mapInternetsite = new HashMap<String, String>();
		mapInternetsite.put(KEY_PROP, getString(R.string.infodetail_prop_internetsite));
		mapInternetsite.put(KEY_VALUE, getString(R.string.infodetail_value_internetsite, info.getSite()));
		list.add(mapInternetsite);
		
		Map<String, String> mapExpectedtitle = new HashMap<String, String>();
		mapExpectedtitle.put(KEY_PROP, getString(R.string.infodetail_prop_expectedtitle));
		mapExpectedtitle.put(KEY_VALUE, getString(R.string.infodetail_value_expectedtitle, info.getTitle()));
		list.add(mapExpectedtitle);
		
		Map<String, String> mapFoundtitle = new HashMap<String, String>();
		mapFoundtitle.put(KEY_PROP, getString(R.string.infodetail_prop_foundtitle));
		mapFoundtitle.put(KEY_VALUE, getString(R.string.infodetail_value_foundtitle, info.getPageTitle()));
		if(info.getException() != null) {
			mapFoundtitle.put(KEY_VALUE, getString(R.string.infodetail_value_exception, info.getException()));			
		}
		list.add(mapFoundtitle);
		
		return list;
	}
	
	/**
	 * Returns the given timestamp formatted as date and time for the default locale.
	 * @param timestamp timestamp to format
	 * @return String timestamp formatted as date and time
	 */
	private String getDateTimeString(long timestamp) {
		Date date = new Date(timestamp);
		String dateString = DateFormat.getLongDateFormat(this).format(date);
		String timeString = DateFormat.getTimeFormat(this).format(date);
		return String.format("%s, %s", dateString, timeString);
	}
	
	/**
	 * Subclass of SimpleAdapter disabling all items in the ItemList.
	 * 
	 * @author dode@luniks.net
	 */
	private class SimpleAdapterAllItemsDisabled extends SimpleAdapter {

		public SimpleAdapterAllItemsDisabled(final Context context, final List<? extends Map<String, ?>> data, 
				final int resource, final String[] from, final int[] to) {
			super(context, data, resource, from, to);
		}

		@Override
		public boolean areAllItemsEnabled() {
			return false;
		}

		@Override
		public boolean isEnabled(int position) {
			return false;
		}
		
	}

}
