package net.luniks.android.inetify;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

/**
 * Main activity of the app, providing a possibility to manually test internet connectivity
 * and a menu.
 * 
 * @author dode@luniks.net
 */
public class Inetify extends Activity {
	
	/** Tag used for logging */
	public static final String LOG_TAG = "Inetify";
	
	/** Number of retries to test internet connectivity */
	private static final int TEST_RETRIES = 1;
	
	/** Title key used for SimpleAdapter */
	private static final String KEY_TITLE = "title";
	
	/** Summary key used for SimpleAdapter */
	private static final String KEY_SUMMARY = "summary";
	
	/** FIXME Index of the list item to test internet connectivity */
	private static final int INDEX_TEST = 0;
	
	/** FIXME Index of the list item to show the settings */
	private static final int INDEX_SETTINGS = 1;
	
	/** FIXME Index of the list item to show the help */
	private static final int INDEX_HELP = 2;
	
	/** Shared preferences */
	private SharedPreferences sharedPreferences;
	
	/** Helper */
	private InetifyHelper helper;

	/** 
	 * Loads the preferences and sets the default notification tone.
	 * {@inheritDoc}
	 */
	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		PreferenceManager.setDefaultValues(this, R.xml.settings, false);
		sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
		helper = new InetifyHelper(this, sharedPreferences, 
				(ConnectivityManager)getSystemService(CONNECTIVITY_SERVICE), 
				(WifiManager)getSystemService(WIFI_SERVICE));
		
		setDefaultTone();
		
		this.setContentView(R.layout.main);
		
		List<Map<String, String>> listViewData = buildListViewData();
		
		SimpleAdapter simpleAdapter = new SimpleAdapter(this, listViewData, android.R.layout.simple_list_item_2, 
				new String[] { KEY_TITLE, KEY_SUMMARY },
				new int[] { android.R.id.text1, android.R.id.text2 });
		
		ListView listViewMain = (ListView)findViewById(R.id.listview_main);
		listViewMain.setAdapter(simpleAdapter);
		listViewMain.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			public void onItemClick(final AdapterView<?> parent, final View view, final int position, final long id) {
				if(position == INDEX_TEST) {
					runTest();
				}
				if(position == INDEX_SETTINGS) {
					showSettings();
				}
				if(position == INDEX_HELP) {
					showHelp();
				}
			}
		});
	}
	
	/**
	 * Returns a list of maps used as data given to SimpleAdapter.
	 * @return List<Map<String, String>>
	 */
	private List<Map<String, String>> buildListViewData() {
		List<Map<String, String>> list = new ArrayList<Map<String, String>>();
		
		Map<String, String> mapTest = new HashMap<String, String>();
		mapTest.put(KEY_TITLE, getString(R.string.main_title_test));
		mapTest.put(KEY_SUMMARY, getString(R.string.main_summary_test));
		list.add(INDEX_TEST, mapTest);
		
		Map<String, String> mapSettings = new HashMap<String, String>();
		mapSettings.put(KEY_TITLE, getString(R.string.main_title_settings));
		mapSettings.put(KEY_SUMMARY, getString(R.string.main_summary_settings));
		list.add(INDEX_SETTINGS, mapSettings);
		
		Map<String, String> mapHelp = new HashMap<String, String>();
		mapHelp.put(KEY_TITLE, getString(R.string.main_title_help));
		mapHelp.put(KEY_SUMMARY, getString(R.string.main_summary_help));
		list.add(INDEX_HELP, mapHelp);
		
		return list;
	}
	
	/**
	 * Sets DEFAULT_NOTIFICATION_URI if the notification tone in in the preferences
	 * is null (first installation, data deleted).
	 */
	private void setDefaultTone() {
		// Is there really no other way to set the default tone, i.e. in XML?
		String tone = sharedPreferences.getString("settings_tone", null);
		if(tone == null) {
			sharedPreferences.edit().putString("settings_tone", android.provider.Settings.System.DEFAULT_NOTIFICATION_URI.toString()).commit();
		}
	}
	
	/**
	 * Test internet connectivity.
	 */
	private void runTest() {
		new TestTask().execute(new Void[0]);
	}
	
	/**
	 * Show settings.
	 */
	private void showSettings() {
		Intent launchPreferencesIntent = new Intent().setClass(this, Settings.class);
		startActivity(launchPreferencesIntent);
	}
	
	/**
	 * Show help.
	 */
	private void showHelp() {
		Intent launchHelpIntent = new Intent().setClass(this, Help.class);
		startActivity(launchHelpIntent);
	}
	
	/**
	 * Displays the given TestInfo in the InfoDetail view.
	 * @param info
	 */
	private void showInfoDetail(final TestInfo info) {
		Intent infoDetailIntent = new Intent().setClass(Inetify.this, InfoDetail.class);
		infoDetailIntent.putExtra(InfoDetail.EXTRA_TEST_INFO, info);
		Inetify.this.startActivity(infoDetailIntent);
	}
	
	/**
	 * AsyncTask showing a progress dialog while it is testing internet connectivity,
	 * and then displaying the information and status.
	 * 
	 * @author dode@luniks.net
	 */
    private class TestTask extends AsyncTask<Void, Void, TestInfo> {
    	
    	private ProgressDialog dialog = ProgressDialog.show(Inetify.this, "", Inetify.this.getString(R.string.main_testing), true);

    	/** {@inheritDoc} */
		@Override
		protected void onPreExecute() {
			dialog.show();
		}

		/** {@inheritDoc} */
		@Override
		protected TestInfo doInBackground(final Void... arg) {
			return helper.getTestInfo(TEST_RETRIES);
		}
		
		/** {@inheritDoc} */
		@Override
	    protected void onPostExecute(final TestInfo info) {
			dialog.cancel();
			showInfoDetail(info);
	    }
		
    }
	
}
