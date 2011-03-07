package net.luniks.android.inetify;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.IBinder;
import android.preference.PreferenceManager;

public class InetifyService extends Service {
	
	private static final int NOTIFICATION_ID = 1;
	
	private NotificationManager notificationManager;
	private ConnectivityManager connectivityManager;
	private SharedPreferences sharedPreferences;

	@Override
	public void onCreate() {
		notificationManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
		connectivityManager = (ConnectivityManager)getSystemService(CONNECTIVITY_SERVICE);
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
    
    private void inetify() {
    	
    	boolean tone = sharedPreferences.getBoolean("settings_tone", true);
    	boolean light = sharedPreferences.getBoolean("settings_light", true);
    	
        CharSequence contentTitle = getText(R.string.notification_title);
        CharSequence contentText = getText(R.string.notification_text);

        Notification notification = new Notification(R.drawable.icon, contentTitle, System.currentTimeMillis());
        
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

    	notificationManager.notify(NOTIFICATION_ID, notification);
    }
    
    private class TestAndInetifyTask extends AsyncTask<String, Void, Boolean> {

		@Override
		protected Boolean doInBackground(String... args) {
			return ConnectivityUtil.shouldNotify(connectivityManager, args[0], args[1]);
		}
		
		@Override
	    protected void onPostExecute(Boolean shouldNotify) {
			if(shouldNotify) {
				inetify();
			}
	        stopSelf();
	    }
		
    }

}
