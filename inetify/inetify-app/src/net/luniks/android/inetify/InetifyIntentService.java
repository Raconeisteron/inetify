package net.luniks.android.inetify;

import java.util.concurrent.atomic.AtomicBoolean;

import net.luniks.android.impl.ConnectivityManagerImpl;
import net.luniks.android.impl.NotificationManagerImpl;
import net.luniks.android.impl.WifiManagerImpl;
import android.app.IntentService;
import android.app.NotificationManager;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.util.Log;

public class InetifyIntentService extends IntentService {
	
	/** Delay before starting to test internet connectivity */
	public static final int TEST_DELAY_MILLIS = 10000;
	
	/** Number of retries to test internet connectivity */
	private static final int TEST_RETRIES = 3;
	
	/** Busy flag */
	private final AtomicBoolean busy = new AtomicBoolean(false);
	
	/** UI thread handler */
	private Handler handler;
	
	/** Tester */
	private Tester tester;
	
	/** Notifier */
	private Notifier notifier;
	
	/** Constructor */
	public InetifyIntentService() {
		super("InetifyIntentService");
		this.setIntentRedelivery(true);
	}

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
	}

	/**
	 * Need to override to prevent intents from getting queued up when the service is busy.
	 * NOTE: ServiceTestCase and pre 1.5 API calls onStart()!
	 * @see android.app.IntentService#onStartCommand(android.content.Intent, int, int)
	 */
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		if(busy.get()) {
			Log.d(Inetify.LOG_TAG, "Received intent while busy, ignoring");
			return START_NOT_STICKY;
		}
		return super.onStartCommand(intent, flags, startId);
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
	 * Sets the Tester instance used by the service - intended for unit tests.
	 * @param tester
	 */
	public void setTester(final Tester tester) {
		this.tester = tester;
	}
	
	/**
	 * Sets the Notifier instance used by the service - intended for unit tests.
	 * @param notifier
	 */
	public void setNotifier(final Notifier notifier) {
		this.notifier = notifier;
	}
	
	/**
	 * Returns true if this service is currently processing an intent, false otherwise.
	 * @return boolean true if processing, false otherwise
	 */
	public boolean isBusy() {
		return busy.get();
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
			notifier.inetify(info);
		}		
	}

}
