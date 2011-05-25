package net.luniks.android.inetify;

import android.os.Bundle;
import android.preference.PreferenceActivity;

/**
 * Activity displaying the settings view.
 * 
 * @author dode@luniks.net
 */
public class Settings extends PreferenceActivity {

	/**
	 * Creates the activity and adds the preferences to the view.
	 */
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        addPreferencesFromResource(R.xml.settings);
    }

}
