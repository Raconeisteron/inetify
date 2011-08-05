package net.luniks.android.inetify;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import net.luniks.android.interfaces.IConnectivityManager;
import net.luniks.android.interfaces.INetworkInfo;
import net.luniks.android.interfaces.IWifiInfo;
import net.luniks.android.interfaces.IWifiManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.preference.PreferenceManager;
import android.util.Log;

/**
 * Tester implementation.
 * 
 * @author torsten.roemer@luniks.net
 */
public class TesterImpl implements Tester {
	
	/** Shared preferences */
	private final SharedPreferences sharedPreferences;
	
	/** Connectivity manager */
	private final IConnectivityManager connectivityManager;
	
	/** Wifi manager */
	private final IWifiManager wifiManager;
	
	/** Title verifier */
	private final TitleVerifier titleVerifier;
	
	/** Flag to cancel the test */
	private final AtomicBoolean cancelled = new AtomicBoolean(false);
	
	/** Thread that runs the test */
	private CountDownLatch countDownLatch;
	
	/**
	 * Constructs a tester instance using the given Context, IConnectivityManager, IWifiManager and TitleVerifier.
	 * @param context
	 * @param connectivityManager
	 * @param wifiManager
	 * @param titleVerifier
	 */
	public TesterImpl(final Context context,
			final IConnectivityManager connectivityManager, final IWifiManager wifiManager,
			final TitleVerifier titleVerifier) {
		
		this.sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
		this.connectivityManager = connectivityManager;
		this.wifiManager = wifiManager;
		this.titleVerifier = titleVerifier;
	}

	/**
	 * Gets network and Wifi info and tests if the internet site in the settings has
	 * the expected title and returns and instance of TestInfo.
	 * @return instance of TestInfo containing the test results
	 */
	public TestInfo testSimple() {
		
		String server = getSettingsServer();
		String title = getSettingsTitle();
		
		String pageTitle = "";
		boolean isExpectedTitle = false;
		String exception = null;
				
		try {
			Log.d(Inetify.LOG_TAG, String.format("Manual internet connectivity test"));
			pageTitle = titleVerifier.getPageTitle(server);
			isExpectedTitle = titleVerifier.isExpectedTitle(title, pageTitle);
			
			Log.d(Inetify.LOG_TAG, String.format("Internet connectivity is OK: %s", isExpectedTitle));				
		} catch(Exception e) {
			Log.d(Inetify.LOG_TAG, String.format("Internet connectivity test failed with: %s", e.getMessage()));
			exception = e.getLocalizedMessage();
		}
		
		return buildTestInfo(pageTitle, isExpectedTitle, exception);
	}
	
	/**
	 * FIXME Replace Thread.sleep() with a Timer or so
	 * Gets network and Wifi info and tests if the internet site in the settings has
	 * the expected title and returns and instance of TestInfo. Aborts testing and
	 * returns null if Wifi disconnects during testing.
	 * @param retries number of test retries
	 * @param delay before each test attempt in milliseconds
	 * @return instance of TestInfo containing the test results
	 */
	public TestInfo testWifi(final int retries, final long delay) {
		
		if(titleVerifier == null) {
			return null;
		}
		
		cancelled.set(false);
		
		String server = getSettingsServer();
		String title = getSettingsTitle();
		
		String pageTitle = "";
		boolean isExpectedTitle = false;
		String exception = null;
		
		for(int i = 0; i < retries && ! isExpectedTitle; i++) {
			try {
				
				Log.d(Inetify.LOG_TAG, String.format("Sleeping %s ms before testing internet connectivity", delay));
				try {
					countDownLatch = new CountDownLatch(1);
					countDownLatch.await(delay, TimeUnit.MILLISECONDS);
				} catch (InterruptedException e) {
					Log.d(Inetify.LOG_TAG, String.format("Cancelled during sleep(), aborting"));
					return null;
				}
				
				if(cancelledOrNoWifiConnection()) {
					Log.d(Inetify.LOG_TAG, "Cancelling internet connectivity test");
					return null;
				}
				
				Log.d(Inetify.LOG_TAG, String.format("Testing internet connectivity, try %s of %s", i + 1, retries));
				pageTitle = titleVerifier.getPageTitle(server);
				isExpectedTitle = titleVerifier.isExpectedTitle(title, pageTitle);
				
				Log.d(Inetify.LOG_TAG, String.format("Internet connectivity is OK: %s", isExpectedTitle));
				exception = null;
				
			} catch(Exception e) {
				Log.d(Inetify.LOG_TAG, String.format("Internet connectivity test failed with: %s", e.getMessage()));
				exception = e.getLocalizedMessage();
			}
		}

		if(cancelledOrNoWifiConnection()) {
			Log.d(Inetify.LOG_TAG, "Cancelling internet connectivity test");
			return null;
		}
		
		return buildTestInfo(pageTitle, isExpectedTitle, exception);	
	}
	
	/**
	 * Returns the server set in the settings.
	 * @return String server setting
	 */
	private String getSettingsServer() {
		return sharedPreferences.getString("settings_server", null);
	}
	
	/**
	 * Returns the title set in the settings.
	 * @return String title setting
	 */
	private String getSettingsTitle() {
		return sharedPreferences.getString("settings_title", null);
	}
	
	/**
	 * Builds a TestInfo instance from NetworkInfo and WifiInfo, and the given test results. 
	 * @param pageTitle page title found
	 * @param isExpectedTitle if pageTitle was the expected title
	 * @param exception exception message or null if there was no exception 
	 * @return TestInfo instance
	 */
	private TestInfo buildTestInfo(final String pageTitle, final boolean isExpectedTitle, final String exception) {
		
		String server = getSettingsServer();
		String title = getSettingsTitle();
		
		INetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
		IWifiInfo wifiInfo = wifiManager.getConnectionInfo();
		
		int type = -1;
		String typeName = null;
		String extra = null;
		String extra2 = null;
		
		if(networkInfo != null) {
			type = networkInfo.getType();
			typeName = networkInfo.getTypeName();
			if(networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
				extra = wifiInfo.getSSID();
				extra2 = wifiInfo.getBSSID();
			} else if((networkInfo.getType() == ConnectivityManager.TYPE_MOBILE)) {
				extra = networkInfo.getSubtypeName();
			}
		}
		
		TestInfo info = new TestInfo();
		info.setTimestamp(System.currentTimeMillis());
		info.setType(type);
		info.setTypeName(typeName);
		info.setExtra(extra);
		info.setExtra2(extra2);
		info.setSite(server);
		info.setTitle(title);
		info.setPageTitle(pageTitle);
		info.setIsExpectedTitle(isExpectedTitle);
		info.setException(exception);
		
		return info;
	}
	
	/**
	 * Returns true if the test was cancelled or if there was no Wifi connection.
	 * @return true if the test should be cancelled, false otherwise
	 */
	private boolean cancelledOrNoWifiConnection() {
		
		if(cancelled.get()) {
			Log.d(Inetify.LOG_TAG, String.format("Cancelled"));
			return true;
		}
		
		if(! isWifiConnected()) {
			Log.d(Inetify.LOG_TAG, "No Wifi connection");
			return true;
		}
		
		return false;
	}
	
	/**
	 * Cancels an ongoing test.
	 */
	public void cancel() {
		this.cancelled.set(true);
		if(countDownLatch != null) {
			countDownLatch.countDown();
		}
	}
	
	/**
	 * Returns true if there currently is a Wifi connection, false otherwise.
	 * @return boolean true if Wifi is connected, false otherwise
	 */
    public boolean isWifiConnected() {
    	INetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
    	
    	if(networkInfo != null && networkInfo.getType() == ConnectivityManager.TYPE_WIFI && networkInfo.isConnected()) {
    		// if(wifiInfo != null && wifiInfo.getSSID() != null) {
    			return true;
    		// }
    	}
    	return false;
    }

	/**
	 * Returns the current WifiInfo.
	 * @return IWifiInfo 
	 */
	public IWifiInfo getWifiInfo() {
		return wifiManager.getConnectionInfo();
	}
	
}
