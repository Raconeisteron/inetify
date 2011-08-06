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
	 * Creates the notification using the given INotificationManager, based on the
	 * given TestInfo. Cancels existing notifications if info is null.
	 * @param info
	 */
	public void inetify(final TestInfo info) {
		
		if(info == null) {
			Log.d(Inetify.LOG_TAG, "Cancelling notification");
			notificationManager.cancel(INETIFY_NOTIFICATION_ID);
			return;
		}
		
    	boolean onlyNotOK = sharedPreferences.getBoolean("settings_only_nok", false);
    	String tone = sharedPreferences.getString("settings_tone", null);
    	boolean light = sharedPreferences.getBoolean("settings_light", true);
    	
    	if(info.getIsExpectedTitle() && onlyNotOK) {
			Log.d(Inetify.LOG_TAG, "Cancelling notification");
			notificationManager.cancel(INETIFY_NOTIFICATION_ID);
    		return;
    	}
    	
        CharSequence contentTitle = context.getText(R.string.notification_ok_title);
        CharSequence contentText = context.getText(R.string.notification_ok_text);
        int icon = R.drawable.notification_ok;
        
        if(! info.getIsExpectedTitle()) {
            contentTitle = context.getText(R.string.notification_nok_title);
            contentText = context.getText(R.string.notification_nok_text);
            icon = R.drawable.notification_nok;
        }
        
        Notification notification = new Notification(icon, contentTitle, System.currentTimeMillis());
        notification.flags |= Notification.FLAG_ONLY_ALERT_ONCE;
        notification.flags |= Notification.FLAG_AUTO_CANCEL;
        
        if(! (tone.length() == 0)) {
        	notification.sound = Uri.parse(tone);
        }
        if(light) {
        	notification.defaults |= Notification.DEFAULT_LIGHTS;
        	notification.flags |= Notification.FLAG_SHOW_LIGHTS;
        }

		Intent infoDetailIntent = new Intent().setClass(context, InfoDetail.class);
		infoDetailIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		infoDetailIntent.putExtra(InfoDetail.EXTRA_TEST_INFO, info);
        PendingIntent contentIntent = PendingIntent.getActivity(context, 0, infoDetailIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        notification.setLatestEventInfo(context, context.getText(R.string.service_label), contentText, contentIntent);

        Log.d(Inetify.LOG_TAG, String.format("Issuing notification: %s", String.valueOf(info)));
    	notificationManager.notify(INETIFY_NOTIFICATION_ID, notification);
    	
	}

	// FIXME Code duplication, use Handler to call these methods?
	// TODO Cancel notification if location is null? How to avoid redundant/unwanted recurring notifications?
	public void locatify(final Location location, final WifiLocation nearestLocation) {
    	String tone = sharedPreferences.getString("settings_tone", null);
    	boolean light = sharedPreferences.getBoolean("settings_light", true);
		
    	int icon = R.drawable.notification_ok;
        Notification notification = new Notification(icon, "Found location", System.currentTimeMillis());
        notification.flags |= Notification.FLAG_ONLY_ALERT_ONCE;
        notification.flags |= Notification.FLAG_AUTO_CANCEL;
        
        if(! (tone.length() == 0)) {
        	notification.sound = Uri.parse(tone);
        }
        if(light) {
        	notification.defaults |= Notification.DEFAULT_LIGHTS;
        	notification.flags |= Notification.FLAG_SHOW_LIGHTS;
        }
        
        String text = String.format("Nearest Location: %s (%s m/%s m)", nearestLocation.getName(), 
        		Math.round(nearestLocation.getDistance()), Math.round(location.getAccuracy()));

		Intent intent = new Intent().setClass(context, LocationMapView.class);
		intent.setAction(LocationMapView.SHOW_LOCATION_ACTION);
		intent.putExtra(LocationList.EXTRA_LOCATION, location);
        PendingIntent contentIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        notification.setLatestEventInfo(context, context.getText(R.string.service_label), text, contentIntent);

        Log.d(Inetify.LOG_TAG, String.format("Issuing notification: %s", text));
    	notificationManager.notify(LOCATIFY_NOTIFICATION_ID, notification);
	}

}
