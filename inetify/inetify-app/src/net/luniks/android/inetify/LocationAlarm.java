package net.luniks.android.inetify;

import net.luniks.android.impl.AlarmManagerImpl;
import net.luniks.android.interfaces.IAlarmManager;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.util.Log;

/**
 * Class that sets or cancels an alarm, that triggers a location check, depending on
 * some conditions.
 * 
 * @author torsten.roemer@luniks.net
 */
public class LocationAlarm implements Alarm {
	
	/** Alarm will not be triggered before this delay after it was reset */
	private static final long TRIGGER_DELAY = 3 * 60 * 1000;
	
	/** Application Context */
	private final Context context;
	
	/** Shared preferences */
	private final SharedPreferences sharedPreferences;
	
	/** Alarm manager */
	private final IAlarmManager alarmManager;
	
	/** The operation executed by this alarm */
	private final PendingIntent operation;
	
	/**
	 * Creates an instance using the given context.
	 * @param context
	 */
	public LocationAlarm(final Context context) {
		this.context = context;
		this.sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
		this.alarmManager = new AlarmManagerImpl((AlarmManager)context.getSystemService(Context.ALARM_SERVICE));
		
		Intent checkLocationIntent = new Intent(context, LocationAlarmReceiver.class);
		this.operation = PendingIntent.getBroadcast(context, 0, checkLocationIntent, 0);
	}

	/**
	 * Sets or cancels the alarm depending on some conditions.
	 */
	public void reset() {
		boolean autoWifi  = sharedPreferences.getBoolean("settings_auto_wifi", false);
		boolean notification  = sharedPreferences.getBoolean("settings_wifi_location_enabled", false);
		
		long interval = getIntervalSetting();
		boolean airplaneModeOn = isAirplaneModeOn();
		
		if((autoWifi || notification) && ! airplaneModeOn) {
			
			alarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
					SystemClock.elapsedRealtime() + TRIGGER_DELAY, interval, operation);
			
			Log.d(Inetify.LOG_TAG, String.format("Alarm set"));
		} else {
			alarmManager.cancel(operation);
			
			Log.d(Inetify.LOG_TAG, String.format("Alarm cancelled"));
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
     * Returns true if airplane mode is on, false otherwise.
     * @return boolean true if airplane mode is on
     */
    private boolean isAirplaneModeOn() {
    	int airplaneModeOn = Settings.System.getInt(context.getContentResolver(), Settings.System.AIRPLANE_MODE_ON, 0);
        return airplaneModeOn != 0;
    }

}
