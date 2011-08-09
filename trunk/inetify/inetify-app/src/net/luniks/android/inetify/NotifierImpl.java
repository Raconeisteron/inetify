package net.luniks.android.inetify;

import net.luniks.android.interfaces.INotificationManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.util.Log;

/**
 * Notifier implementation.
 * 
 * @author torsten.roemer@luniks.net
 */
public class NotifierImpl implements Notifier {
	
	/** Id of the notification for internet connectivity test */
	public static final int INETIFY_NOTIFICATION_ID = 1;
	
	/** Id of the notification for Wifi location */
	public static final int LOCATIFY_NOTIFICATION_ID = 2;
	
	/** Application Context */
	private final Context context;
	
	/** Shared preferences */
	private final SharedPreferences sharedPreferences;
	
	/** Notification manager */
	private final INotificationManager notificationManager;
	
	/**
	 * Constructs an instance using the given Context and INotificationManager.
	 * @param context
	 * @param notificationManager
	 */
	public NotifierImpl(final Context context, final INotificationManager notificationManager) {
		this.context = context;
		this.sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
		this.notificationManager = notificationManager;
	}

	/**
	 * Creates an "internet connectivity test" notification based on the given TestInfo,
	 * cancels an existing notification if info is null.
	 * @param info test info
	 */
	public void inetify(final TestInfo info) {
		
		if(info == null) {
			Log.d(Inetify.LOG_TAG, "Cancelling notification");
			notificationManager.cancel(INETIFY_NOTIFICATION_ID);
			return;
		}
		
    	boolean onlyNotOK = sharedPreferences.getBoolean("settings_only_nok", false);

    	if(info.getIsExpectedTitle() && onlyNotOK) {
			Log.d(Inetify.LOG_TAG, "Cancelling notification");
			notificationManager.cancel(INETIFY_NOTIFICATION_ID);
    		return;
    	}
    	
        String tickerText = context.getString(R.string.notification_ok_title);
        String contentText = context.getString(R.string.notification_ok_text);
        int icon = R.drawable.notification_ok;
        
        if(! info.getIsExpectedTitle()) {
            tickerText = context.getString(R.string.notification_nok_title);
            contentText = context.getString(R.string.notification_nok_text);
            icon = R.drawable.notification_nok;
        }

		Intent intent = new Intent().setClass(context, InfoDetail.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.putExtra(InfoDetail.EXTRA_TEST_INFO, info);
		
		Notification notification = createNotification(icon, tickerText, tickerText, contentText, intent);

        Log.d(Inetify.LOG_TAG, String.format("Issuing notification: %s", String.valueOf(info)));
    	notificationManager.notify(INETIFY_NOTIFICATION_ID, notification);
    	
	}

	/**
	 * Creates a "nearest Wifi" notification using the given location and Wifi location.
	 * @param location current location
	 * @param nearestLocation nearest Wifi location
	 */
	public void locatify(final Location location, final WifiLocation nearestLocation) {
		
    	int icon = R.drawable.notification_ok;

    	String tickerText = String.format("Nearest Wifi: %s", nearestLocation.getName());
        String contentText = String.format("Distance: %s m - Accuracy: %s m", 
        		Math.round(nearestLocation.getDistance()), Math.round(location.getAccuracy()));

		/*
        Intent intent = new Intent().setClass(context, LocationMapView.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.setAction(LocationMapView.SHOW_LOCATION_ACTION);
		intent.putExtra(LocationList.EXTRA_LOCATION, location);
		*/
        Intent intent = new Intent();

		Notification notification = createNotification(icon, tickerText, tickerText, contentText, intent);

        Log.d(Inetify.LOG_TAG, String.format("Issuing notification: %s", contentText));
    	notificationManager.notify(LOCATIFY_NOTIFICATION_ID, notification);
	}
	
	/**
	 * Creates a notification using the given details and returns it.
	 * @param icon
	 * @param tickerText
	 * @param contentTitle
	 * @param contentText
	 * @param intent
	 * @return notification
	 */
	private Notification createNotification(final int icon, final String tickerText, 
			final String contentTitle, final String contentText, final Intent intent) {
		
    	String tone = sharedPreferences.getString("settings_tone", null);
    	boolean light = sharedPreferences.getBoolean("settings_light", true);
		
        Notification notification = new Notification(icon, tickerText, System.currentTimeMillis());
        notification.flags |= Notification.FLAG_ONLY_ALERT_ONCE;
        notification.flags |= Notification.FLAG_AUTO_CANCEL;
        
        if(! (tone.length() == 0)) {
        	notification.sound = Uri.parse(tone);
        }
        if(light) {
        	notification.defaults |= Notification.DEFAULT_LIGHTS;
        	notification.flags |= Notification.FLAG_SHOW_LIGHTS;
        }

        PendingIntent contentIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        notification.setLatestEventInfo(context, tickerText, contentText, contentIntent);
        
        return notification;
	}

}
