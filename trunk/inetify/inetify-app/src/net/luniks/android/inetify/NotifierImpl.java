package net.luniks.android.inetify;

import net.luniks.android.interfaces.INotificationManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.util.Log;

public class NotifierImpl implements Notifier {
	
	/** Id of the OK notification */
	private static final int NOTIFICATION_ID_OK = 1;
	
	/** Id of the Not OK notification */
	private static final int NOTIFICATION_ID_NOK = 2;
	
	/** Application Context */
	private final Context context;
	
	/** Shared preferences */
	private final SharedPreferences sharedPreferences;
	
	/** Notification manager */
	private final INotificationManager notificationManager;
	
	public NotifierImpl(final Context context, final INotificationManager notificationManager) {
		this.context = context;
		this.sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
		this.notificationManager = notificationManager;
	}

	public void inetify(final TestInfo info) {
		
		if(info == null) {
			Log.d(Inetify.LOG_TAG, "Cancelling notifications");
			notificationManager.cancel(NOTIFICATION_ID_OK);
			notificationManager.cancel(NOTIFICATION_ID_NOK);
			return;
		}
		
    	boolean onlyNotOK = sharedPreferences.getBoolean("settings_only_nok", false);
    	String tone = sharedPreferences.getString("settings_tone", null);
    	boolean light = sharedPreferences.getBoolean("settings_light", true);
    	
    	if(info.getIsExpectedTitle() && onlyNotOK) {
    		return;
    	}
    	
    	int notificationId = NOTIFICATION_ID_OK;
        CharSequence contentTitle = context.getText(R.string.notification_ok_title);
        CharSequence contentText = context.getText(R.string.notification_ok_text);
        int icon = R.drawable.notification_ok;
        
        if(! info.getIsExpectedTitle()) {
        	notificationId = NOTIFICATION_ID_NOK;
            contentTitle = context.getText(R.string.notification_nok_title);
            contentText = context.getText(R.string.notification_nok_text);
            icon = R.drawable.notification_nok;
        }
        
        Notification notification = new Notification(icon, contentTitle, System.currentTimeMillis());
        notification.flags |= Notification.FLAG_ONLY_ALERT_ONCE;
        
        if(! (tone.length() == 0)) {
        	notification.sound = Uri.parse(tone);
        }
        if(light) {
        	notification.defaults |= Notification.DEFAULT_LIGHTS;
        	notification.flags |= Notification.FLAG_SHOW_LIGHTS;
        }
        
        notification.flags |= Notification.FLAG_AUTO_CANCEL;

		Intent infoDetailIntent = new Intent().setClass(context, InfoDetail.class);
		infoDetailIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		infoDetailIntent.putExtra(InfoDetail.EXTRA_TEST_INFO, info);
        PendingIntent contentIntent = PendingIntent.getActivity(context, 0, infoDetailIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        notification.setLatestEventInfo(context, context.getText(R.string.service_label), contentText, contentIntent);

        Log.d(Inetify.LOG_TAG, String.format("Issuing notification: %s", String.valueOf(info)));
    	notificationManager.notify(notificationId, notification);
    	
	}

}