package net.luniks.android.inetify;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;

/**
 * Service testing internet connectivity and showing a notification.
 * 
 * @author dode@luniks.net
 */
public class InetifyService extends Service {
	
	/** Id of the OK notification */
	private static final int NOTIFICATION_ID_OK = 1;
	
	/** Id of the Not OK notification */
	private static final int NOTIFICATION_ID_NOK = 2;
	
	/** Delay before starting to test internet connectivity */
	private static final int TEST_DELAY_MILLIS = 10000;
	
	/** Number of retries to test internet connectivity */
	private static final int TEST_RETRIES = 3;
	
	/** Notification manager */
	private NotificationManager notificationManager;
	
	/** Shared preferences */
	private SharedPreferences sharedPreferences;
	
	/** Helper */
	private InetifyHelper helper;

	/** 
	 * Gets the notification manager and loads the preferences.
	 * {@inheritDoc}
	 */
	@Override
	public void onCreate() {
		notificationManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
		sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
		helper = new InetifyHelper(this, sharedPreferences, new TitleVerifierImpl());
	}

	/** {@inheritDoc} */
	@Override
	public IBinder onBind(final Intent intent) {
		return null;
	}
	
	/**
	 * Executes the TestAndInetifyTask.
	 * {@inheritDoc} 
	 */
	@Override
	public int onStartCommand(final Intent intent, final int flags, final int startId) {
		cancelNotifications();
		
		boolean isWifiConnected = intent.getBooleanExtra(ConnectivityActionReceiver.EXTRA_IS_WIFI_CONNECTED, false);
		if(isWifiConnected) {
			Log.d(Inetify.LOG_TAG, "Wifi is connected, starting task to test internet connectivity");
			new TestAndInetifyTask().execute();
		}
		return START_NOT_STICKY;
	}
	
	/**
	 * Cancels any notifications
	 */
	private void cancelNotifications() {
		Log.d(Inetify.LOG_TAG, "Cancelling notifications");
		notificationManager.cancel(NOTIFICATION_ID_OK);
		notificationManager.cancel(NOTIFICATION_ID_NOK);
	}
    
	/**
	 * Gives an OK notification if the given boolean is true, a Not OK notification otherwise. 
	 * @param info
	 */
    private void inetify(final TestInfo info) {
    	
    	boolean onlyNotOK = sharedPreferences.getBoolean("settings_only_nok", false);
    	String tone = sharedPreferences.getString("settings_tone", null);
    	boolean light = sharedPreferences.getBoolean("settings_light", true);
    	
    	if(info.getIsExpectedTitle() && onlyNotOK) {
    		return;
    	}
    	
    	int notificationId = NOTIFICATION_ID_OK;
        CharSequence contentTitle = getText(R.string.notification_ok_title);
        CharSequence contentText = getText(R.string.notification_ok_text);
        int icon = R.drawable.notification_ok;
        
        if(! info.getIsExpectedTitle()) {
        	notificationId = NOTIFICATION_ID_NOK;
            contentTitle = getText(R.string.notification_nok_title);
            contentText = getText(R.string.notification_nok_text);
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

		Intent infoDetailIntent = new Intent().setClass(InetifyService.this, InfoDetail.class);
		infoDetailIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		infoDetailIntent.putExtra(InfoDetail.EXTRA_TEST_INFO, info);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, infoDetailIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        notification.setLatestEventInfo(this, getText(R.string.service_label), contentText, contentIntent);

        Log.d(Inetify.LOG_TAG, String.format("Issuing notification: %s", String.valueOf(info)));
    	notificationManager.notify(notificationId, notification);
    }
    
    /**
     * AsyncTask that sleeps for TEST_DELAY_MILLIS, then tests internet connectivity and
     * gives a notification depending on the result, and then stops this service.
     * 
     * @author dode@luniks.net
     */
    private class TestAndInetifyTask extends AsyncTask<Void, Void, TestInfo> {

    	/** {@inheritDoc} */
		@Override
		protected TestInfo doInBackground(final Void... args) {			
			return helper.getTestInfo(TEST_RETRIES, TEST_DELAY_MILLIS, true);
		}
		
		/** {@inheritDoc} */
		@Override
	    protected void onPostExecute(final TestInfo info) {
			if(info != null) {			
				inetify(info);
			} else {
				cancelNotifications();
			}
	    }
		
    }

}
