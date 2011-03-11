package net.luniks.android.inetify;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;

public class InetifyService extends Service {
	
	private static final int NOTIFICATION_ID_OK = 1;
	private static final int NOTIFICATION_ID_NOK = 2;
	
	private static final int TEST_DELAY_MILLIS = 10000;
	
	private NotificationManager notificationManager;
	private SharedPreferences sharedPreferences;

	@Override
	public void onCreate() {
		notificationManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
		sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this.getApplicationContext());
	}

	@Override
	public IBinder onBind(final Intent intent) {
		return null;
	}
	
	@Override
	public int onStartCommand(final Intent intent, final int flags, final int startId) {		
		String server = sharedPreferences.getString("settings_server", null);
		String title = sharedPreferences.getString("settings_title", null);
		new TestAndInetifyTask().execute(server, title);
		return START_STICKY;
	}
    
    private void inetify(final boolean haveInternet) {
    	
    	boolean tone = sharedPreferences.getBoolean("settings_tone", true);
    	boolean light = sharedPreferences.getBoolean("settings_light", true);
    	
    	int notificationId = NOTIFICATION_ID_OK;
        CharSequence contentTitle = getText(R.string.notification_ok_title);
        CharSequence contentText = getText(R.string.notification_ok_text);
        int icon = R.drawable.emo_im_happy;
        if(! haveInternet) {
        	notificationId = NOTIFICATION_ID_NOK;
            contentTitle = getText(R.string.notification_nok_title);
            contentText = getText(R.string.notification_nok_text);
            icon = R.drawable.emo_im_sad;
        }
        
        Notification notification = new Notification(icon, contentTitle, System.currentTimeMillis());
        
        notification.flags |= Notification.FLAG_ONLY_ALERT_ONCE;
        
        if(tone) {
        	notification.defaults |= Notification.DEFAULT_SOUND;
        }
        if(light) {
        	notification.defaults |= Notification.DEFAULT_LIGHTS;
        	notification.flags |= Notification.FLAG_SHOW_LIGHTS;
        }
        
        notification.flags |= Notification.FLAG_AUTO_CANCEL;

        // TODO Start browser when user clicks notification
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, new Intent(), 0);
        notification.setLatestEventInfo(this, getText(R.string.service_label), contentText, contentIntent);

    	notificationManager.notify(notificationId, notification);
    }
    
    private class TestAndInetifyTask extends AsyncTask<String, Void, Boolean> {

		@Override
		protected Boolean doInBackground(String... args) {
			Log.d(Inetify.LOG_TAG, String.format("TestAndInetifyTask started, sleeping for %s ms", TEST_DELAY_MILLIS));
			try {
				Thread.sleep(TEST_DELAY_MILLIS);
			} catch (InterruptedException e) {
				// Ignore
			}
			Log.d(Inetify.LOG_TAG, String.format("Testing internet connectivity with site %s and title %s", args[0], args[1]));
			return ConnectivityUtil.haveInternet(args[0], args[1]);
		}
		
		@Override
	    protected void onPostExecute(Boolean haveInternet) {
			Log.d(Inetify.LOG_TAG, String.format("Internet connectivity: %s", haveInternet));
			inetify(haveInternet);
	        stopSelf();
	    }
		
    }

}
