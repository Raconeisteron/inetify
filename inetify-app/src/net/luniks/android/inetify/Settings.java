/*
 * Copyright 2011 Torsten Römer
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.luniks.android.inetify;

import android.os.Bundle;
import android.preference.PreferenceActivity;

/**
 * Activity displaying the settings view.
 * 
 * @author torsten.roemer@luniks.net
 */
public class Settings extends PreferenceActivity {
	
	public static final String INTERNET_CHECK = "settings_enabled";
	public static final String INTERNET_ONLY_NOK = "settings_only_nok";
	public static final String INTERNET_SERVER = "settings_server";
	public static final String INTERNET_TITLE = "settings_title";
	public static final String LOCATION_CHECK = "settings_wifi_location_enabled";
	public static final String LOCATION_AUTO_WIFI = "settings_auto_wifi";
	public static final String LOCATION_USE_GPS = "settings_use_gps";
	public static final String LOCATION_MAX_DISTANCE = "settings_max_distance";
	public static final String LOCATION_CHECK_INTERVAL = "settings_check_interval";
	public static final String TONE = "settings_tone";
	public static final String LIGHT = "settings_light";

	/**
	 * Creates the activity and adds the preferences to the view.
	 */
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        addPreferencesFromResource(R.xml.settings);
    }

}
