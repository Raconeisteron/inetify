package net.luniks.android.inetify;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
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
	
	private static final int REQUEST_CODE_PREFERENCES = 1;

	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		PreferenceManager.setDefaultValues(this, R.xml.settings, false);
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
			test();
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
	
	// TODO Real implementation and something that looks nice
	private void test() {
		new TestTask().execute(new Void[0]);
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
			SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(Inetify.this.getApplicationContext());
			
			String server = sharedPreferences.getString("settings_server", null);
			WifiInfo wifiInfo = wifiManager.getConnectionInfo();
			
			boolean hasWifiConnection = ConnectivityUtil.hasWifiConnection(connectivityManager);
			boolean isReachable = ConnectivityUtil.isReachable(server);
			boolean isLoadable = ConnectivityUtil.isLoadable(server);
			
			StringBuffer message = new StringBuffer();
			message.append("Test Result\n\n");
			message.append(String.format("Wifi is connected: %s\n", hasWifiConnection));
			message.append(String.format("SSID is: %s\n", wifiInfo.getSSID()));
			message.append(String.format("ICMP %s: %s\n", server, isReachable));
			message.append(String.format("HTTP %s: %s\n", server, isLoadable));
			
			return message.toString();
		}
		
		@Override
	    protected void onPostExecute(String message) {
			dialog.cancel();
			TextView textView = (TextView) findViewById(R.id.textview);
			textView.setText(message.toString(), BufferType.NORMAL);
	    }
		
    }
	
}
