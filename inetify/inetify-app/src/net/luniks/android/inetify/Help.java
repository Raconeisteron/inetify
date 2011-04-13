package net.luniks.android.inetify;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;

public class Help extends Activity {
	
	private SharedPreferences sharedPreferences;

	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		PreferenceManager.setDefaultValues(this, R.xml.settings, false);
		sharedPreferences = PreferenceManager.getDefaultSharedPreferences(Help.this.getApplicationContext());
		
		this.setContentView(R.layout.help);
	}
	
}
