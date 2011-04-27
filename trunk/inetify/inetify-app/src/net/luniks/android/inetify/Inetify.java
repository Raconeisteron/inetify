package net.luniks.android.inetify;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.TextView.BufferType;

/**
 * Main activity of the app, providing a possibility to manually test internet connectivity
 * and a menu.
 * 
 * @author dode@luniks.net
 */
public class Inetify extends Activity {
	
	/** Tag used for logging */
	public static final String LOG_TAG = "Inetify";
	
	/** Request code for result activity of the settings menu item */
	private static final int REQUEST_CODE_PREFERENCES = 1;
	
	/** Request code for result activity of the help menu item */
	private static final int REQUEST_CODE_HELP = 2;
	
	/** Number of retries to test internet connectivity */
	private static final int TEST_RETRIES = 1;
	
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
		helper = new InetifyHelper(this, sharedPreferences);
		
		setDefaultTone();
		
		this.setContentView(R.layout.main);
	}
	
	/**
	 * Method called by the "Test Internet Connectivity" button, executing the TestTask.
	 * @param view
	 */
	public void test(final View view) {
		new TestTask().execute(new Void[0]);
	}

	/** {@inheritDoc} */
	@Override
	public boolean onCreateOptionsMenu(final Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main, menu);
		return true;
	}

	/** {@inheritDoc} */
	@Override
	public boolean onOptionsItemSelected(final MenuItem item) {
		switch (item.getItemId()) {
		
		case R.id.help:
			Intent launchHelpIntent = new Intent().setClass(this, Help.class);
			startActivityForResult(launchHelpIntent, REQUEST_CODE_HELP);
			return true;

		case R.id.settings:
			Intent launchPreferencesIntent = new Intent().setClass(this, Settings.class);
			startActivityForResult(launchPreferencesIntent, REQUEST_CODE_PREFERENCES);
			return true;

		default:
			break;
		}

		return false;
	}

	/** {@inheritDoc} */
	@Override
	protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == REQUEST_CODE_PREFERENCES) {
			// Do something when settings were saved?
		}
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
	 * Displays the given TestInfo in the main view.
	 * @param info
	 */
	private void showTestInfo(final TestInfo info) {
		
		TextView textViewConnection = (TextView)this.findViewById(R.id.textview_connection);
		TextView textViewInfo = (TextView)this.findViewById(R.id.textview_info);
		
		textViewConnection.setText(helper.getConnectionString(info), BufferType.NORMAL);
		textViewInfo.setText(helper.getInfoString(info), BufferType.NORMAL);
		
		textViewInfo.setOnClickListener(new ShowInfoDetailOnClickListener(info.getIsExpectedTitle(), helper.getInfoDetailString(info)));
	}
	
	/**
	 * AsyncTask showing a progress dialog while it is testing internet connectivity,
	 * and then displaying the information and status.
	 * 
	 * @author dode@luniks.net
	 */
    private class TestTask extends AsyncTask<Void, Void, TestInfo> {
    	
    	private ProgressDialog dialog = ProgressDialog.show(Inetify.this, "", Inetify.this.getString(R.string.inetify_testing), true);

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
			showTestInfo(info);
	    }
		
    }
    
    /**
     * Implementation of OnClickListener that starts the InfoDetail activity.
     * 
     * @author dode@luniks.net
     */
    private class ShowInfoDetailOnClickListener implements OnClickListener {
    	
    	private final boolean isExpectedTitle;
    	private final String text;
    	
    	/**
    	 * The ShowInfoDetailOnClickListener will pass the given details to the InfoDetail activity.
    	 * @param isExpectedTitle
    	 * @param text
    	 */
    	public ShowInfoDetailOnClickListener(final boolean isExpectedTitle, final String text) {
    		this.isExpectedTitle = isExpectedTitle;
    		this.text = text;
    	}

		/** {@inheritDoc} */
		public void onClick(final View v) {
			Intent infoDetailIntent = new Intent().setClass(Inetify.this, InfoDetail.class);
			infoDetailIntent.putExtra(InfoDetail.KEY_IS_EXPECTED_TITLE, isExpectedTitle);
			infoDetailIntent.putExtra(InfoDetail.KEY_TEXT, text);
			Inetify.this.startActivity(infoDetailIntent);
		}
    	
    }
	
}
