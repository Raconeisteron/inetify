package net.luniks.android.inetify;

import net.luniks.android.impl.ConnectivityManagerImpl;
import net.luniks.android.impl.NotificationManagerImpl;
import net.luniks.android.impl.WifiManagerImpl;
import net.luniks.android.interfaces.IWifiInfo;
import android.app.IntentService;
import android.app.NotificationManager;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.util.Log;

/**
 * IntentService that is started by ConnectivityActionReceiver when Wifi connects
 * or disconnects, performs the internet connectivity test and creates or cancels
 * the notifications. If the service receives an intent while is busy testing internet
 * connectivity it cancels the test and starts a new test run. 
 * 
 * @author dode@luniks.net
 */
public class InetifyIntentService extends IntentService {
	
	/** Delay before/between each (re)try to test internet connectivity */
	public static final int TEST_DELAY_MILLIS = 10000;
	
	/** Number of retries to test internet connectivity */
	private static final int TEST_RETRIES = 3;
	
	/** UI thread handler */
	private Handler handler;
	
	/** Tester */
	private Tester tester;
	
	/** Notifier */
	private Notifier notifier;
	
	/** Database adapter */
	private DatabaseAdapter databaseAdapter;
	
	/** Constructor */
	public InetifyIntentService() {
		super("InetifyIntentService");
		this.setIntentRedelivery(true);
	}

	/**
	 * Performs initialization.
	 */
	@Override
	public void onCreate() {
		super.onCreate();
		this.handler = new Handler();
		if(tester == null) {
			tester = new TesterImpl(this,
					new ConnectivityManagerImpl((ConnectivityManager)getSystemService(CONNECTIVITY_SERVICE)), 
					new WifiManagerImpl((WifiManager)getSystemService(WIFI_SERVICE)),
					new TitleVerifierImpl());
		}
		if(notifier == null) {
			notifier = new NotifierImpl(this,
					new NotificationManagerImpl((NotificationManager)getSystemService(NOTIFICATION_SERVICE)));
		}
		if(databaseAdapter == null) {
			databaseAdapter = new DatabaseAdapterImpl(this);
		}
	}

	/**
	 * Overridden to cancel a possibly ongoing internet connectivity test so the next
	 * one can be started instead.
	 * NOTE: ServiceTestCase and pre 1.5 API calls onStart()!
	 * @see android.app.IntentService#onStartCommand(android.content.Intent, int, int)
	 */
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		cancelTester();
		return super.onStartCommand(intent, flags, startId);
	}

	/**
	 * Cancels a possibly ongoing internet connectivity test.
	 */
	@Override
	public void onDestroy() {
		cancelTester();
		super.onDestroy();
	}

	/**
	 * Runs an internet connectivity test for each received intent, skips the test if
	 * the extra EXTRA_IS_WIFI_CONNECTED is false.
	 */
	@Override
	protected void onHandleIntent(final Intent intent) {
		
		if(intent == null) {
			Log.d(Inetify.LOG_TAG, "Received a null intent, ignoring");
			return;
		}
		
		try {
			boolean isWifiConnected = intent.getBooleanExtra(ConnectivityActionReceiver.EXTRA_IS_WIFI_CONNECTED, false);

			if(isWifiConnected) {
				IWifiInfo wifiInfo = tester.getWifiInfo();
				if(wifiInfo != null && databaseAdapter.isIgnoredWifi(wifiInfo.getSSID())) {
					Log.d(Inetify.LOG_TAG, String.format("Wifi %s is connected but ignored, skipping test", wifiInfo.getSSID()));
					return;
				} else {
					Log.d(Inetify.LOG_TAG, "Wifi is connected, running test");
					TestInfo info = tester.test(TEST_RETRIES, TEST_DELAY_MILLIS, true);
					handler.post(new InetifyRunner(info));
				}
			} else {
				Log.d(Inetify.LOG_TAG, "Wifi is not connected, skipping test");
				handler.post(new InetifyRunner(null));
			}
			
		} catch(Exception e) {
			Log.w(Inetify.LOG_TAG, String.format("Test threw exception: %s", e.getMessage()));
		}
	}
	
	/**
	 * Cancelling the tester, catching any exception it may throw.
	 */
	private void cancelTester() {
		try {
			tester.cancel();
		} catch(Exception e) {
			Log.w(Inetify.LOG_TAG, String.format("Cancelling test threw exception: %s", e.getMessage()));
		}
	}
	
	/**
	 * Sets the Tester implementation used by the service - intended for unit tests.
	 * @param tester
	 */
	public void setTester(final Tester tester) {
		this.tester = tester;
	}
	
	/**
	 * Sets the Notifier implementation used by the service - intended for unit tests.
	 * @param notifier
	 */
	public void setNotifier(final Notifier notifier) {
		this.notifier = notifier;
	}
	
	/**
	 * Sets the DatabaseAdapter implementation used by the service - intended for unit tests.
	 * @param databaseAdapter
	 */
	public void setDatabaseAdapter(final DatabaseAdapter databaseAdapter) {
		this.databaseAdapter = databaseAdapter;
	}
	
	/**
	 * Runnable that calls inetify(TestInfo) with the given TestInfo.
	 * A null TestInfo causes any existing notification to be cancelled.
	 * @author dode@luniks.net
	 */
	private class InetifyRunner implements Runnable {
		
		private final TestInfo info;
		
		public InetifyRunner(final TestInfo info) {
			this.info = info;
		}
		
		public void run() {
			Log.d(Inetify.LOG_TAG, String.format("Inetifying with info: %s", String.valueOf(info)));
			notifier.inetify(info);
		}		
	}

}
