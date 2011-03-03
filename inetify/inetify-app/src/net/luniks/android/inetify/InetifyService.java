package net.luniks.android.inetify;

import java.net.InetAddress;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.preference.PreferenceManager;

public class InetifyService extends Service {
	
	private static final int NOTIFICATION_ID = 1;
	private static final int REACHABLE_TIMEOUT = 10000;
	
	private final IBinder binder = new LocalBinder();
	private final Handler handler = new Handler();
	
	private NotificationManager notificationManager;
	private ConnectivityManager connectivityManager;
	private WifiManager wifiManager;
	
	private Thread uglyThreadForNow;

	@Override
	public void onCreate() {
		
		notificationManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
		connectivityManager = (ConnectivityManager)getSystemService(CONNECTIVITY_SERVICE);
		wifiManager = (WifiManager)getSystemService(WIFI_SERVICE);
		
		run();
	}

	@Override
	public IBinder onBind(final Intent intent) {
		return binder;
	}

	@Override
	public void onDestroy() {
		
		uglyThreadForNow.interrupt();
		
		super.onDestroy();
	}
	
	public void run() {
		
		uglyThreadForNow = new Thread() {
			public void run() {
				while(! uglyThreadForNow.isInterrupted()) {
					
					final boolean shouldNotify = shouldNotify();
					
					handler.post(new Runnable() {
						public void run() {
							inetify(shouldNotify);
						}
					});
					
					try {
						Thread.sleep(60000);
					} catch (InterruptedException e) {
						// Ignore
					}
				}
			}
		};
		uglyThreadForNow.start();
	}
	
	private boolean shouldNotify() {
		
		boolean notify = false;
		if(hasWifiConnection()) {
			notify =! hasInetConnection();
		}
		return notify;
	}
	
	private boolean hasWifiConnection() {
		
		NetworkInfo networkInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		return networkInfo.isConnected();
	}
	
	private boolean hasInetConnection() {
		
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this.getApplicationContext());
		String server = prefs.getString("settings_server", null);
		
		try {
			InetAddress inetAddress = InetAddress.getByName(server);
			return inetAddress.isReachable(REACHABLE_TIMEOUT);
		} catch(Exception e) {
			return false;
		}
	}
    
    private void inetify(final boolean notify) {
    	
    	if(notify) {
	        CharSequence contentTitle = getText(R.string.notification_title);
	        CharSequence contentText = getText(R.string.notification_text);
	
	        Notification notification = new Notification(R.drawable.icon, contentTitle, System.currentTimeMillis());
	        notification.defaults |= Notification.DEFAULT_SOUND;
	        notification.defaults |= Notification.DEFAULT_LIGHTS;
	        notification.flags |= Notification.FLAG_SHOW_LIGHTS;
	        notification.flags |= Notification.FLAG_AUTO_CANCEL;
	        notification.flags |= Notification.FLAG_ONLY_ALERT_ONCE;
	
	        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, new Intent(), 0);
	        notification.setLatestEventInfo(this, getText(R.string.service_label), contentText, contentIntent);

        	notificationManager.notify(NOTIFICATION_ID, notification);
    	} else {
    		notificationManager.cancel(NOTIFICATION_ID);
    	}
    }
    
    public class LocalBinder extends Binder {
    	InetifyService getService() {
            return InetifyService.this;
        }
    }

}
