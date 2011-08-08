package net.luniks.android.inetify;

import net.luniks.android.impl.AlarmManagerImpl;
import net.luniks.android.impl.ConnectivityManagerImpl;
import net.luniks.android.interfaces.IAlarmManager;
import net.luniks.android.interfaces.IConnectivityManager;
import net.luniks.android.interfaces.INetworkInfo;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

public class CheckLocationAlarm implements Alarm {
	
	/** Application Context */
	private final Context context;
	
	/** Shared preferences */
	private final SharedPreferences sharedPreferences;
	
	/** Connectivity manager */
	private final IConnectivityManager connectivityManager;
	
	/** Alarm manager */
	private final IAlarmManager alarmManager;
	
	/** The operation executed by this alarm */
	private final PendingIntent operation;
	
	public CheckLocationAlarm(final Context context) {
		this.context = context;
		this.sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
		this.connectivityManager = new ConnectivityManagerImpl((ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE));
		this.alarmManager = new AlarmManagerImpl((AlarmManager)context.getSystemService(Context.ALARM_SERVICE));
		
		Intent checkLocationIntent = new Intent(context, CheckLocationAlarmReceiver.class);
		this.operation = PendingIntent.getBroadcast(context, 0, checkLocationIntent, 0);
	}

	public void update() {
		boolean settingEnabled  = sharedPreferences.getBoolean("settings_wifi_location_enabled", false);
		boolean settingOnlyIfNoWifi  = sharedPreferences.getBoolean("settings_only_if_no_wifi", false);
		String settingInterval = sharedPreferences.getString("settings_check_interval", null);
		long interval = mapIntervalSetting(settingInterval);
		
		boolean wifiNotConnected = ! isWifiConnected();
		boolean locationsPresent = true;
		
		if(settingEnabled && locationsPresent && (wifiNotConnected || (settingOnlyIfNoWifi == wifiNotConnected))) {
			alarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
					SystemClock.elapsedRealtime() + 60 * 1000, interval,
					operation);
			
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
	 * Maps the given string from the settings to a AlarmManager.INTERVAL_* value.
	 * FIXME Just can't come up with something better than this
	 * @param setting String from settings to map
	 * @return long AlarmManager.INTERVAL_* value
	 */
	private long mapIntervalSetting(final String setting) {
		if(setting == null) return AlarmManager.INTERVAL_FIFTEEN_MINUTES;
		if(setting.equals("30")) return AlarmManager.INTERVAL_HALF_HOUR;
		if(setting.equals("60")) return AlarmManager.INTERVAL_HOUR;
		return AlarmManager.INTERVAL_FIFTEEN_MINUTES;
	}
	
	/**
	 * Returns true if there currently is a Wifi connection, false otherwise.
	 * @return boolean true if Wifi is connected, false otherwise
	 */
    private boolean isWifiConnected() {
    	INetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
    	
    	if(networkInfo != null && networkInfo.getType() == ConnectivityManager.TYPE_WIFI && networkInfo.isConnected()) {
    		return true;
    	}
    	return false;
    }

}
