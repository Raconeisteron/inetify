package net.luniks.android.inetify;

import java.net.InetAddress;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.TextView.BufferType;
import android.widget.Toast;

public class Inetify extends Activity {
	private static final int REQUEST_CODE_PREFERENCES = 1;

	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		PreferenceManager.setDefaultValues(this, R.xml.settings, false);

		this.setContentView(R.layout.main);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main, menu);

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
		case R.id.refresh:
			simpleTest();
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
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == REQUEST_CODE_PREFERENCES) {
			// Restart timer/reregister for WiFi connect notifications...
			Toast.makeText(this, "Settings saved", Toast.LENGTH_SHORT).show();
		}
	}
	
	/**
	 * Just for fun for now...
	 */
	private void simpleTest() {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this.getApplicationContext());
		String server = prefs.getString("settings_server", "www.google.com");

		try {
			InetAddress inetAddress = InetAddress.getByName(server);
			boolean reachable = inetAddress.isReachable(3000);
			
			StringBuffer message = new StringBuffer();
			message.append(String.format("Server %s reachable: %s", server, reachable));
			
			TextView textView = (TextView) findViewById(R.id.textview);
			textView.setText(message.toString(), BufferType.NORMAL);
		} catch (Exception e) {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage(String.format("Failed to inetify: %s", e.getMessage()));
			builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					dialog.cancel();
				}
			});
			AlertDialog alert = builder.create();
			alert.show();
		}
	}
}
