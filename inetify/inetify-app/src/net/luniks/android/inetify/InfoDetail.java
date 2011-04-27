package net.luniks.android.inetify;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Activity that shows detailed information about the status of internet connectivity.
 * 
 * @author dode
 */
public class InfoDetail extends Activity {

	private static final String KEY_PROP = "prop";
	private static final String KEY_VALUE = "value";
	
	public static final String EXTRA_TEST_INFO = "extraTestInfo";
	
	/** Shared preferences */
	private SharedPreferences sharedPreferences;

	/** {@inheritDoc} */
	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

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
		
		SimpleAdapter simpleAdapter = new TestInfoAdapter(this, listViewData, android.R.layout.simple_list_item_2, 
				new String[] { KEY_PROP, KEY_VALUE },
				new int[] { android.R.id.text1, android.R.id.text2 }, info);
		
		ListView listViewInfodetail = (ListView)findViewById(R.id.listview_infodetail);
		listViewInfodetail.setAdapter(simpleAdapter);
		listViewInfodetail.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				if(position == 3) {
					updateSettingsPageTitle(info.getPageTitle());
				}
			}
		});


	}
	
	private void updateSettingsPageTitle(final String newTitle) {
		String text = getString(R.string.infodetail_updated_settings, newTitle);
		sharedPreferences.edit().putString("settings_title", newTitle).commit();
		Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
	}
	
	private List<Map<String, String>> buildListViewData(final TestInfo info) {
		List<Map<String, String>> list = new ArrayList<Map<String, String>>();
		
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
		if(! info.getIsExpectedTitle()) {
			mapFoundtitle.put(KEY_VALUE, getString(R.string.infodetail_value_foundtitlenotmatch, info.getPageTitle()));
		}
		if(info.getException() != null) {
			mapFoundtitle.put(KEY_VALUE, getString(R.string.infodetail_value_exception, info.getException()));			
		}
		list.add(mapFoundtitle);
		
		return list;
	}
	
	private class TestInfoAdapter extends SimpleAdapter {
		
		private final TestInfo info;

		public TestInfoAdapter(final Context context, final List<? extends Map<String, ?>> data, 
				final int resource, final String[] from, final int[] to, final TestInfo info) {
			super(context, data, resource, from, to);
			this.info = info;
		}

		@Override
		public boolean areAllItemsEnabled() {
			return false;
		}

		@Override
		public boolean isEnabled(int position) {
			if(position == 3 && ! info.getIsExpectedTitle() && info.getException() == null) {
				return true;
			}
			return false;
		}
		
	}

}
