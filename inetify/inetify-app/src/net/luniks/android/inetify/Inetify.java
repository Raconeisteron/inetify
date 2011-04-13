package net.luniks.android.inetify;

import java.io.IOException;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.TextView.BufferType;

public class Inetify extends Activity {
	
	public static final String LOG_TAG = "Inetify";
	
	private static final int REQUEST_CODE_PREFERENCES = 1;
	private static final int REQUEST_CODE_HELP = 2;
	
	private SharedPreferences sharedPreferences;

	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		PreferenceManager.setDefaultValues(this, R.xml.settings, false);
		sharedPreferences = PreferenceManager.getDefaultSharedPreferences(Inetify.this.getApplicationContext());
		
		setDefaultTone();
		
		this.setContentView(R.layout.main);
	}
	
	public void test(final View view) {
		new TestTask().execute(new Void[0]);
	}

	@Override
	public boolean onCreateOptionsMenu(final Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main, menu);
		return true;
	}

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

	@Override
	protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == REQUEST_CODE_PREFERENCES) {
			// Do something when settings were saved?
		}
	}
	
	private void setDefaultTone() {
		// Is there really no other way to set the default tone, i.e. in XML?
		String tone = sharedPreferences.getString("settings_tone", null);
		if(tone == null) {
			sharedPreferences.edit().putString("settings_tone", android.provider.Settings.System.DEFAULT_NOTIFICATION_URI.toString()).commit();
		}
	}
	
	private TestInfo getTestInfo() {
		ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
		WifiManager wifiManager =  (WifiManager)getSystemService(Context.WIFI_SERVICE);
		WifiInfo wifiInfo = wifiManager.getConnectionInfo();
		
		String server = sharedPreferences.getString("settings_server", null);
		String title = sharedPreferences.getString("settings_title", null);
		
		String type = null;
		String extra = null;
		if(networkInfo != null) {
			type = networkInfo.getTypeName();
			if(networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
				extra = wifiInfo.getSSID();
			} else {
				extra = networkInfo.getSubtypeName();
			}
		}

		try {
			String pageTitle = ConnectivityUtil.getPageTitle(server);
			boolean isExpectedTitle = ConnectivityUtil.isExpectedTitle(title, pageTitle);
			
			TestInfo info = new TestInfo();
			info.setType(type);
			info.setExtra(extra);
			info.setSite(server);
			info.setTitle(title);
			info.setPageTitle(pageTitle);
			info.setExpectedTitle(isExpectedTitle);
			
			return info;
			
		} catch (IOException e) {
			return new TestInfo(e);
		}
		
	}
	
	private void showTestInfo(final TestInfo info) {
		
		TextView textViewConnection = (TextView)this.findViewById(R.id.textview_connection);
		TextView textViewInfo = (TextView)this.findViewById(R.id.textview_info);
		
		if(info.getException() != null) {
			showErrorDialog(info.getException());
		}
		
		textViewConnection.setText(getConnectionString(info), BufferType.NORMAL);
		textViewInfo.setText(getInfoString(info), BufferType.NORMAL);
		
	}
	
	private String getConnectionString(final TestInfo info) {
		return String.format("Internet connectivity via %s (%s) seems:", 
				info.getType(), info.getExtra());
	}
	
	private String getInfoString(final TestInfo info) {
		if(info.isExpectedTitle()) {
			return "OK";
		} else {
			return "Not OK";
		}
	}
	
	/**
	 * Shows a dialog displaying the message of the given exception
	 * @param exception
	 */
	private void showErrorDialog(final Exception exception) {
		AlertDialog.Builder alert = new AlertDialog.Builder(Inetify.this);

		alert.setCancelable(false);
		alert.setTitle("Error");
		alert.setMessage(String.format("Error testing connectivity:\n%s", exception.getMessage()));
		
		alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			public void onClick(final DialogInterface dialog, final int whichButton) {
				dialog.dismiss();
			}
		});
		
		alert.show();		
	}
	
    private class TestTask extends AsyncTask<Void, Void, TestInfo> {
    	
    	ProgressDialog dialog = ProgressDialog.show(Inetify.this, "", "Testing, please wait...", true);

		@Override
		protected void onPreExecute() {
			dialog.show();
		}

		@Override
		protected TestInfo doInBackground(Void... arg) {
			return getTestInfo();
		}
		
		@Override
	    protected void onPostExecute(TestInfo info) {
			dialog.cancel();
			showTestInfo(info);
	    }
		
    }
	
}
