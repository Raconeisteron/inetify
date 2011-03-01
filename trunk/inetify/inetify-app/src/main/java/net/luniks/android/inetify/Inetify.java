package net.luniks.android.inetify;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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
			Toast.makeText(this, "Refresh", Toast.LENGTH_SHORT).show();
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
			Toast.makeText(this, "Settings saved", Toast.LENGTH_SHORT).show();
			// Reconnect to other BPE...
		}
	}
}
