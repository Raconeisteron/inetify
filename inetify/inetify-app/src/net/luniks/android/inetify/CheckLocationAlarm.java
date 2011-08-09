package net.luniks.android.inetify;

import net.luniks.android.impl.AlarmManagerImpl;
import net.luniks.android.impl.WifiManagerImpl;
import net.luniks.android.interfaces.IAlarmManager;
import net.luniks.android.interfaces.IWifiManager;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.wifi.WifiManager;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

public class CheckLocationAlarm implements Alarm {
	
	/** Application Context */
	private final Context context;
	
	/** Shared preferences */
	private final SharedPreferences sharedPreferences;
	
	/** Wifi manager */
	private final IWifiManager wifiManager;
	
	/** Alarm manager */
	private final IAlarmManager alarmManager;
	
	/** The operation executed by this alarm */
	private final PendingIntent operation;
	
	public CheckLocationAlarm(final Context context) {
		this.context = context;
		this.sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
		this.wifiManager = new WifiManagerImpl((WifiManager)context.getSystemService(Context.WIFI_SERVICE));
		this.alarmManager = new AlarmManagerImpl((AlarmManager)context.getSystemService(Context.ALARM_SERVICE));
		
		Intent checkLocationIntent = new Intent(context, CheckLocationAlarmReceiver.class);
		this.operation = PendingIntent.getBroadcast(context, 0, checkLocationIntent, 0);
	}

	public void update() {
		boolean settingEnabled  = sharedPreferences.getBoolean("settings_wifi_location_enabled", false);
		boolean settingOnlyIfWifiDisabled  = sharedPreferences.getBoolean("settings_only_if_wifi_disabled", false);
		
		long interval = getIntervalSetting();
		boolean wifiEnabled = isWifiEnabled();
		boolean airplaneModeOn = isAirplaneModeOn();
		boolean batteryLow = isBatteryLow();
		
		if(settingEnabled && ! airplaneModeOn && ! batteryLow && 
	      (! wifiEnabled || (! settingOnlyIfWifiDisabled == wifiEnabled))) {
			
			alarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
					SystemClock.elapsedRealtime() + 60 * 1000, interval, operation);
			
			Log.d(Inetify.LOG_TAG, String.format("Alarm set"));
			// TODO Remove
			Toast.makeText(context, "Alarm set", Toast.LENGTH_LONG).show();
		} else {
			alarmManager.cancel(operation);
			
			Log.d(Inetify.LOG_TAG, String.format("Alarm cancelled"));
			// TODO Remove
			Toast.makeText(context, "Alarm cancelled", Toast.LENGTH_LONG).show();
		}
	}
	
	/**
	 * Maps the "settings_check_interval" setting to a AlarmManager.INTERVAL_* value.
	 * FIXME Just can't come up with something better than this
	 * @return long AlarmManager.INTERVAL_* value
	 */
	private long getIntervalSetting() {
		String setting = sharedPreferences.getString("settings_check_interval", null);
		if(setting == null) return AlarmManager.INTERVAL_FIFTEEN_MINUTES;
		if(setting.equals("30")) return AlarmManager.INTERVAL_HALF_HOUR;
		if(setting.equals("60")) return AlarmManager.INTERVAL_HOUR;
		return AlarmManager.INTERVAL_FIFTEEN_MINUTES;
	}
	
	/**
	 * Returns true if Wifi is enabled, false otherwise.
	 * @return boolean true if Wifi is enabled
	 */
    private boolean isWifiEnabled() {
    	int wifiState = wifiManager.getWifiState();
    	return wifiState == WifiManager.WIFI_STATE_ENABLED;
    }
    
    /**
     * Returns true if airplane mode is on, false otherwise.
     * @return boolean true if airplane mode is on
     */
    private boolean isAirplaneModeOn() {
    	int airplaneModeOn = Settings.System.getInt(context.getContentResolver(), Settings.System.AIRPLANE_MODE_ON, 0);
        return airplaneModeOn != 0;
    }

    /**
     * Returns true if the battery is low, false otherwise.
     * @return boolean true if the battery is low
     */
    private boolean isBatteryLow() {
    	return sharedPreferences.getBoolean("battery_low", false);
    }

}
