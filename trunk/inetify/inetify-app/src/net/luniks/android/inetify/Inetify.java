package net.luniks.android.inetify;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.luniks.android.impl.ConnectivityManagerImpl;
import net.luniks.android.impl.WifiManagerImpl;
import android.app.Activity;
import android.app.Dialog;
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
 * Main activity of the app, providing a possibility to manually test internet connectivity,
 * go to the settings and display a help text.
 * 
 * @author torsten.roemer@luniks.net
 */
public class Inetify extends Activity {
	
	/** Tag used for logging */
	public static final String LOG_TAG = "Inetify";
	
    /** Request code for the settings activity */
    private static final int REQUEST_CODE_PREFERENCES = 0;
	
	/** Id of the progress dialog */
	private static final int ID_PROGRESS_DIALOG = 0;
	
	/** Title key used for SimpleAdapter */
	private static final String KEY_TITLE = "title";
	
	/** Summary key used for SimpleAdapter */
	private static final String KEY_SUMMARY = "summary";
	
	/** Index of the list item to test internet connectivity */
	private static final int INDEX_TEST = 0;
	
	/** Index of the list item to show the settings */
	private static final int INDEX_SETTINGS = 1;
	
	/** Index of the list item to show the ignore list */
	private static final int INDEX_IGNORELIST = 2;
	
	/** Index of the list item to show the location list */
	private static final int INDEX_LOCATIONLIST = 3;
	
	/** Index of the list item to show the help */
	private static final int INDEX_HELP = 4;
	
	/** Shared preferences */
	private SharedPreferences sharedPreferences;
	
	/** TestTask - retained through config changes */
	private TestTask testTask;
	
	/**
	 * Retains the tester AsyncTask before a config change occurs.
	 */
	@Override
	public Object onRetainNonConfigurationInstance() {
		testTask.setActivity(null);
		return testTask;
	}

	/** 
	 * Performs initialization, sets the default notification tone and populates the view.
	 */
	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		this.setContentView(R.layout.main);
		
		PreferenceManager.setDefaultValues(this, R.xml.settings, false);
		sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
		
		Object retained = this.getLastNonConfigurationInstance();
		if(retained == null) {
			testTask = new TestTask(this);
		} else {
			testTask = (TestTask)retained;
			testTask.setActivity(this);
		}
		
		setDefaultTone();
		
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
				if(position == INDEX_IGNORELIST) {
					showIgnoreList();
				}
				if(position == INDEX_LOCATIONLIST) {
					showLocationList();
				}
				if(position == INDEX_HELP) {
					showHelp();
				}
			}
		});
		
		if(savedInstanceState == null) {
    		Alarm alarm = new LocationAlarm(this);
    		alarm.reset();
		}
	}
	
	/**
	 * Creates the dialogs managed by this activity.
	 */
	@Override
	protected Dialog onCreateDialog(final int id) {
		if(id == ID_PROGRESS_DIALOG) {
			ProgressDialog dialog = new ProgressDialog(this);
			dialog.setTitle(Inetify.this.getString(R.string.main_testing_title));
			dialog.setMessage(Inetify.this.getString(R.string.main_testing_message));
			dialog.setIndeterminate(true);
			dialog.setCancelable(false);
			return dialog;
		}
		return super.onCreateDialog(id);
	}
	
	/**
	 * Sets the alarm after leaving the settings.
	 */
    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
            super.onActivityResult(requestCode, resultCode, data);

            if (requestCode == REQUEST_CODE_PREFERENCES) {		
        		Alarm alarm = new LocationAlarm(this);
        		alarm.reset();
            }
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
		
		Map<String, String> mapIgnorelist = new HashMap<String, String>();
		mapIgnorelist.put(KEY_TITLE, getString(R.string.main_title_ignorelist));
		mapIgnorelist.put(KEY_SUMMARY, getString(R.string.main_summary_ignorelist));
		list.add(INDEX_IGNORELIST, mapIgnorelist);
		
		Map<String, String> mapLocationlist = new HashMap<String, String>();
		mapLocationlist.put(KEY_TITLE, getString(R.string.main_title_locationlist));
		mapLocationlist.put(KEY_SUMMARY, getString(R.string.main_summary_locationlist));
		list.add(INDEX_LOCATIONLIST, mapLocationlist);
		
		Map<String, String> mapHelp = new HashMap<String, String>();
		mapHelp.put(KEY_TITLE, getString(R.string.main_title_help));
		mapHelp.put(KEY_SUMMARY, getString(R.string.main_summary_help));
		list.add(INDEX_HELP, mapHelp);
		
		return list;
	}
	
	/**
	 * Sets DEFAULT_NOTIFICATION_URI if the notification tone in in the preferences
	 * is null (first installation or data deleted).
	 */
	private void setDefaultTone() {
		// Is there really no other way to set the default tone, i.e. in XML?
		String tone = sharedPreferences.getString("settings_tone", null);
		if(tone == null) {
			sharedPreferences.edit().putString("settings_tone", android.provider.Settings.System.DEFAULT_NOTIFICATION_URI.toString()).commit();
		}
	}
	
	/**
	 * Test internet connectivity using an AsyncTask.
	 */
	private void runTest() {
		if(testTask.getStatus() == AsyncTask.Status.FINISHED) {
			testTask.setActivity(null);
			testTask = new TestTask(this);
		}
		testTask.execute(new Void[0]);
	}
	
	/**
	 * Shows the settings.
	 */
	private void showSettings() {
		Intent launchPreferencesIntent = new Intent().setClass(this, Settings.class);
		startActivityForResult(launchPreferencesIntent, REQUEST_CODE_PREFERENCES);
	}
	
	/**
	 * Shows the ignore list.
	 */
	private void showIgnoreList() {
		Intent showIgnoreListIntent = new Intent().setClass(this, IgnoreList.class);
		startActivity(showIgnoreListIntent);
	}
	
	/**
	 * Shows the location list.
	 */
	private void showLocationList() {
		Intent showLocationListIntent = new Intent().setClass(this, LocationList.class);
		startActivity(showLocationListIntent);
	}
	
	/**
	 * Shows the help.
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
		startActivity(infoDetailIntent);
	}
	
	/**
	 * AsyncTask showing a progress dialog while it is testing internet connectivity,
	 * and then displaying the results.
	 * 
	 * @author torsten.roemer@luniks.net
	 */
    private static class TestTask extends AsyncTask<Void, Void, TestInfo> {
    	
    	private Tester tester;
    	private Inetify activity;
    	
    	private TestTask(final Inetify activity) {
    		this.activity = activity;
    		this.tester = new TesterImpl(activity,
					new ConnectivityManagerImpl((ConnectivityManager)activity.getSystemService(CONNECTIVITY_SERVICE)), 
					new WifiManagerImpl((WifiManager)activity.getSystemService(WIFI_SERVICE)),
					new TitleVerifierImpl());
    	}
    	
    	private void setActivity(final Inetify activity) {
    		this.activity = activity;
    	}

    	/**
    	 * Shows the progress dialog.
    	 */
		@Override
		protected void onPreExecute() {
			activity.showDialog(ID_PROGRESS_DIALOG);
		}

		/**
		 * Runs the internet connectivity test in the background.
		 */
		@Override
		protected TestInfo doInBackground(final Void... arg) {
			return tester.testSimple();
		}
		
		/**
		 * Cancels the progress dialog, and calls showInfoDetail(TestInfo) with
		 * the TestInfo returned by doInBackground().
		 */
		@Override
	    protected void onPostExecute(final TestInfo info) {
			Dialogs.dismissDialogSafely(activity, ID_PROGRESS_DIALOG);
			// http://code.google.com/p/android/issues/detail?id=4266
			Dialogs.removeDialogSafely(activity, ID_PROGRESS_DIALOG);
			activity.showInfoDetail(info);
	    }
		
    }
	
}
