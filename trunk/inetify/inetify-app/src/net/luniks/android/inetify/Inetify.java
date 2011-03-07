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
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.BufferType;

public class Inetify extends Activity {
	
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
			test();
			return true;
			
		case R.id.autosetup:
			autoSetup();
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
	
	private void autoSetup() {

		AlertDialog.Builder alert = new AlertDialog.Builder(this);

		alert.setTitle("Auto Setup");
		alert.setMessage("Which site would you like to use to test internet connectivity?");

		final EditText input = new EditText(this);
		input.setText(R.string.default_value_server_preference);
		alert.setView(input);

		alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				String server = input.getText().toString();
				new PageTitleTask().execute(server);
			}
		});

		alert.setNegativeButton("Cancel",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						dialog.cancel();
					}
				});

		alert.show();

	}
	
	private void test() {
		new TestTask().execute(new Void[0]);
	}
	
    private class PageTitleTask extends AsyncTask<String, Void, String> {
    	
    	ProgressDialog dialog = ProgressDialog.show(Inetify.this, "", "Setting up...", true);

		@Override
		protected void onPreExecute() {
			dialog.show();
		}

		@Override
		protected String doInBackground(String... args) {
			
			String server = args[0];
			StringBuffer message = new StringBuffer();
			try {
				String pageTitle = ConnectivityUtil.getPageTitle(server);
				message.append(String.format("Setting Internet Site '%s' and Page Title '%s'. Notifications enabled.", server, pageTitle));
				SharedPreferences.Editor editor = sharedPreferences.edit();
				editor.putBoolean("settings_enabled", true);
				editor.putString("settings_server", server);
				editor.putString("settings_title", pageTitle);
				editor.commit();
			} catch(IOException e) {
				message.append(String.format("Could not load page %s: %s", server, e.getMessage()));
			}
			
			return message.toString();
		}
		
		@Override
	    protected void onPostExecute(String message) {
			dialog.cancel();
			AlertDialog.Builder alert = new AlertDialog.Builder(Inetify.this);

			alert.setCancelable(false);
			alert.setTitle("Auto Setup");
			alert.setMessage(message);
			
			alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					dialog.dismiss();
				}
			});
			
			alert.show();
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
			
			String server = sharedPreferences.getString("settings_server", null);
			String title = sharedPreferences.getString("settings_title", null);
			WifiInfo wifiInfo = wifiManager.getConnectionInfo();
			
			boolean hasWifiConnection = ConnectivityUtil.hasWifiConnection(connectivityManager);
			
			StringBuffer message = new StringBuffer();
			message.append("Test Result\n\n");
			message.append(String.format("Wifi is connected: %s\n", hasWifiConnection));
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
			TextView textView = (TextView) findViewById(R.id.textview);
			textView.setText(message.toString(), BufferType.NORMAL);
	    }
		
    }
	
}
