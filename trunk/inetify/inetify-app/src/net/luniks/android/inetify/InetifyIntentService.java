package net.luniks.android.inetify;

import java.util.concurrent.atomic.AtomicBoolean;

import net.luniks.android.impl.ConnectivityManagerImpl;
import net.luniks.android.impl.WifiManagerImpl;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;

public class InetifyIntentService extends IntentService {
	
	/** Delay before starting to test internet connectivity */
	public static final int TEST_DELAY_MILLIS = 10000;
	
	/** Id of the OK notification */
	private static final int NOTIFICATION_ID_OK = 1;
	
	/** Id of the Not OK notification */
	private static final int NOTIFICATION_ID_NOK = 2;
	
	/** Number of retries to test internet connectivity */
	private static final int TEST_RETRIES = 3;
	
	/** Busy flag */
	private final AtomicBoolean busy = new AtomicBoolean(false);
	
	/** UI thread handler */
	private Handler handler;
	
	/** Notification manager */
	private NotificationManager notificationManager;
	
	/** Shared preferences */
	private SharedPreferences sharedPreferences;
	
	/** Tester */
	private InetifyTester tester;
	
	/** Constructor */
	public InetifyIntentService() {
		super("InetifyIntentService");
	}

	@Override
	public void onCreate() {
		super.onCreate();
		this.handler = new Handler();
		notificationManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
		sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
		if(tester == null) {
			tester = new InetifyTesterImpl(this, sharedPreferences, 
					new ConnectivityManagerImpl((ConnectivityManager)getSystemService(CONNECTIVITY_SERVICE)), 
					new WifiManagerImpl((WifiManager)getSystemService(WIFI_SERVICE)),
					new TitleVerifierImpl());
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		tester.cancel();
		busy.set(false);
	}

	@Override
	protected void onHandleIntent(final Intent intent) {
		
		if(intent == null) {
			Log.d(Inetify.LOG_TAG, "Received a null intent, ignoring");
			return;
		}
		
		if(busy.get()) {
			Log.d(Inetify.LOG_TAG, String.format("Received intent while busy, ignoring: %s", String.valueOf(intent)));
			return;
		}
		
		Log.d(Inetify.LOG_TAG, "Setting busy flag to true");
		busy.set(true);
		try {
			InetifyRunner runner = new InetifyRunner(null);
			
			boolean isWifiConnected = intent.getBooleanExtra(ConnectivityActionReceiver.EXTRA_IS_WIFI_CONNECTED, false);
			if(isWifiConnected) {
				Log.d(Inetify.LOG_TAG, "Wifi is connected, testing internet connectivity");
				TestInfo info = tester.test(TEST_RETRIES, TEST_DELAY_MILLIS, true);
				runner = new InetifyRunner(info);
			}
			
			handler.post(runner);
		}
		catch(Exception e) {
			e.printStackTrace();
		} finally {
			Log.d(Inetify.LOG_TAG, "Setting busy flag to false");
			busy.set(false);
		}
	}
	
	/**
	 * Replaces the InetifyTester instance used by the service with the given one -
	 * intended to use for unit tests.
	 * @param tester
	 */
	public void setTester(final InetifyTester tester) {
		this.tester = tester;
	}
	
	/**
	 * Returns true if this service is currently processing an intent, false otherwise.
	 * @return boolean true if processing, false otherwise
	 */
	public boolean isBusy() {
		return busy.get();
	}
	
	/**
	 * Handles notifications based on the given info. Must be called from the UI thread.
	 * @param info
	 */
	private void inetify(final TestInfo info) {
		
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

		Intent infoDetailIntent = new Intent().setClass(InetifyIntentService.this, InfoDetail.class);
		infoDetailIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		infoDetailIntent.putExtra(InfoDetail.EXTRA_TEST_INFO, info);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, infoDetailIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        notification.setLatestEventInfo(this, getText(R.string.service_label), contentText, contentIntent);

        Log.d(Inetify.LOG_TAG, String.format("Issuing notification: %s", String.valueOf(info)));
    	notificationManager.notify(notificationId, notification);
	}
	
	/**
	 * Runnable that calls inetify(TestInfo) with the given TestInfo.
	 * @author dode@luniks.net
	 */
	private class InetifyRunner implements Runnable {
		
		private final TestInfo info;
		
		public InetifyRunner(final TestInfo info) {
			this.info = info;
		}
		
		public void run() {
			inetify(info);
		}		
	}

}
