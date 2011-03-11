package net.luniks.android.inetify;

import java.io.IOException;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
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
import android.widget.TextView;
import android.widget.TextView.BufferType;

public class Inetify extends Activity {
	
	public static final String LOG_TAG = "Inetify";
	
	private static final int REQUEST_CODE_PREFERENCES = 1;
	
	private SharedPreferences sharedPreferences;

	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		PreferenceManager.setDefaultValues(this, R.xml.settings, false);
		sharedPreferences = PreferenceManager.getDefaultSharedPreferences(Inetify.this.getApplicationContext());
		
		this.setContentView(R.layout.main);
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
		case R.id.refresh:
			new TestTask().execute(new Void[0]);
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
	
    private class TestTask extends AsyncTask<Void, Void, String> {
    	
    	ProgressDialog dialog = ProgressDialog.show(Inetify.this, "", "Testing, please wait...", true);

		@Override
		protected void onPreExecute() {
			dialog.show();
		}

		@Override
		protected String doInBackground(Void... arg) {
			ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(CONNECTIVITY_SERVICE);
			WifiManager wifiManager =  (WifiManager)getSystemService(Context.WIFI_SERVICE);
			NetworkInfo networkInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
			WifiInfo wifiInfo = wifiManager.getConnectionInfo();
			
			String server = sharedPreferences.getString("settings_server", null);
			String title = sharedPreferences.getString("settings_title", null);
			
			StringBuffer message = new StringBuffer();
			message.append("Test Result\n\n");
			message.append(String.format("Wifi is connected: %s\n", networkInfo.isConnected()));
			message.append(String.format("SSID is: %s\n", wifiInfo.getSSID()));
			
			try {
				String pageTitle = ConnectivityUtil.getPageTitle(server);
				boolean isExpectedTitle = ConnectivityUtil.isExpectedTitle(title, pageTitle);
				
				message.append(String.format("Page title of %s is: %s\n", server, pageTitle));
				message.append(String.format("Is expected title: %s\n", isExpectedTitle));
			} catch(IOException e) {
				message.append("\n");
				message.append(String.format("Could not load page %s: %s\n", server, e.getMessage()));
			}
			
			return message.toString();
		}
		
		@Override
	    protected void onPostExecute(String message) {
			dialog.cancel();
			TextView textView = (TextView)findViewById(R.id.textview);
			textView.setText(message.toString(), BufferType.NORMAL);
	    }
		
    }
	
}
