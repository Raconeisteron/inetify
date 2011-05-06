package net.luniks.android.inetify;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.TwoLineListItem;

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
	
	/** Index of the list item showing the timestamp */
	private static final int INDEX_TIMESTAMP = 0;
	
	/** Index of the list item showing connection info */
	private static final int INDEX_CONNECTION = 1;
	
	/** Index of the list item showing the internet site */
	private static final int INDEX_INTERNETSITE = 2;
	
	/** Index of the list item showing the expected title */
	private static final int INDEX_EXPECTEDTITLE = 3;
	
	/** Index of the list item showing the found title */
	private static final int INDEX_FOUNDTITLE = 4;
	
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
		int colorResId = textViewInfodetail.getTextColors().getDefaultColor();
		
		if(info != null) {
			if(info.getIsExpectedTitle()) {
				drawableResid = R.drawable.icon_ok;
				textResid = R.string.infodetail_ok;
				colorResId = R.color.green_ok;
			} else {
				drawableResid = R.drawable.icon_nok;
				textResid = R.string.infodetail_nok;
				colorResId = R.color.red_nok;
			}
		}
		
		textViewInfodetail.setCompoundDrawablesWithIntrinsicBounds(drawableResid, 0, 0, 0);
		textViewInfodetail.setText(textResid);
		textViewInfodetail.setTextColor(getResources().getColor(colorResId));
		
		if(info == null) {
			return;
		}
		
		List<Map<String, String>> listViewData = buildListViewData(info);
		
		SimpleAdapter simpleAdapter = new SimpleAdapterSomeItemsDisabled(this, listViewData, android.R.layout.simple_list_item_2, 
				new String[] { KEY_PROP, KEY_VALUE },
				new int[] { android.R.id.text1, android.R.id.text2 });
		
		ListView listViewInfodetail = (ListView)findViewById(R.id.listview_infodetail);
		listViewInfodetail.setAdapter(simpleAdapter);
		listViewInfodetail.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			public void onItemClick(final AdapterView<?> parent, final View view, final int position, final long id) {
				if(position == INDEX_CONNECTION) {
					openWirelessSettings();
				}
				if(position == INDEX_INTERNETSITE) {
					openInBrowser(info.getSite());
				}
			}
		});
	}
	
	/**
	 * Opens the given site in the browser.
	 * @param site
	 */
	private void openInBrowser(final String site) {
		Uri uri = null;
		try {
			uri = Uri.parse(ConnectivityUtil.addProtocol(site));
		} catch(Exception e) {
			// TODO Show dialog with error message
		}
		Intent intent = new Intent(Intent.ACTION_VIEW, uri);
		startActivity(intent);
	}
	
	/**
	 * Opens the wireless settings.
	 */
	private void openWirelessSettings() {
		Intent intent = new Intent(android.provider.Settings.ACTION_WIRELESS_SETTINGS );
		startActivity(intent);
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
		list.add(INDEX_TIMESTAMP, mapTimestamp);
		
		Map<String, String> mapConnection = new HashMap<String, String>();
		mapConnection.put(KEY_PROP, getString(R.string.infodetail_prop_connection));
		mapConnection.put(KEY_VALUE, getString(R.string.infodetail_value_connection, info.getType(), info.getExtra()));
		list.add(INDEX_CONNECTION, mapConnection);
		
		Map<String, String> mapInternetsite = new HashMap<String, String>();
		mapInternetsite.put(KEY_PROP, getString(R.string.infodetail_prop_internetsite));
		mapInternetsite.put(KEY_VALUE, getString(R.string.infodetail_value_internetsite, info.getSite()));
		list.add(INDEX_INTERNETSITE, mapInternetsite);
		
		Map<String, String> mapExpectedtitle = new HashMap<String, String>();
		mapExpectedtitle.put(KEY_PROP, getString(R.string.infodetail_prop_expectedtitle));
		mapExpectedtitle.put(KEY_VALUE, getString(R.string.infodetail_value_expectedtitle, info.getTitle()));
		list.add(INDEX_EXPECTEDTITLE, mapExpectedtitle);
		
		Map<String, String> mapFoundtitle = new HashMap<String, String>();
		mapFoundtitle.put(KEY_PROP, getString(R.string.infodetail_prop_foundtitle));
		mapFoundtitle.put(KEY_VALUE, getString(R.string.infodetail_value_foundtitle, info.getPageTitle()));
		if(info.getException() != null) {
			mapFoundtitle.put(KEY_VALUE, getString(R.string.infodetail_value_exception, info.getException()));			
		}
		list.add(INDEX_FOUNDTITLE, mapFoundtitle);
		
		return list;
	}
	
	/**
	 * Returns the given timestamp formatted as date and time for the default locale.
	 * @param timestamp timestamp to format
	 * @return String timestamp formatted as date and time
	 */
	private String getDateTimeString(final long timestamp) {
		Date date = new Date(timestamp);
		String dateString = DateFormat.getLongDateFormat(this).format(date);
		String timeString = DateFormat.getTimeFormat(this).format(date);
		return String.format("%s, %s", dateString, timeString);
	}
	
	/**
	 * Subclass of SimpleAdapter disabling some items in the ItemList.
	 * 
	 * @author dode@luniks.net
	 */
	private static class SimpleAdapterSomeItemsDisabled extends SimpleAdapter {

		public SimpleAdapterSomeItemsDisabled(final Context context, final List<? extends Map<String, ?>> data, 
				final int resource, final String[] from, final int[] to) {
			super(context, data, resource, from, to);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view = super.getView(position, convertView, parent);
			if(view instanceof TwoLineListItem) {
				TwoLineListItem item = (TwoLineListItem)view;
				if(! (position == INDEX_CONNECTION || position == INDEX_INTERNETSITE)) {
					item.getText1().setTextColor(Color.LTGRAY);
					item.setEnabled(false);
					item.setClickable(false);
				}
			}
			return view;
		}
		
	}

}
