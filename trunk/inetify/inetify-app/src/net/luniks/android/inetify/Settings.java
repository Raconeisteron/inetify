package net.luniks.android.inetify;

import android.os.Bundle;
import android.preference.PreferenceActivity;
/**
 * Activity displaying the settings view.
 * 
 * @author dode@luniks.net
 */
public class Settings extends PreferenceActivity {

	/** {@inheritDoc} */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        addPreferencesFromResource(R.xml.settings);
    }

}
