package net.luniks.android.inetify;

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
 * @author dode@luniks.net
 */
public class TesterImpl implements Tester {

	/** Application context */
	private final Context context;
	
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
	private Thread testThread;
	
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
		
		this.context = context;
		this.sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
		this.connectivityManager = connectivityManager;
		this.wifiManager = wifiManager;
		this.titleVerifier = titleVerifier;
	}
	
	/**
	 * FIXME Make smaller, replace Thread.sleep() with a Timer or so
	 * Gets network and Wifi info and tests if the internet site in the settings has
	 * the expected title and returns and instance of TestInfo. Aborts testing and
	 * returns null if onlyWifi is true and Wifi disconnects during testing.
	 * @param retries number of test retries
	 * @param delay before each test attempt in milliseconds
	 * @param wifiOnly abort test if Wifi is not connected
	 * @return instance of TestInfo containing the test results
	 */
	public TestInfo test(final int retries, final long delay, final boolean wifiOnly) {
		
		testThread = Thread.currentThread();
		cancelled.set(false);
		
		String server = sharedPreferences.getString("settings_server", null);
		String title = sharedPreferences.getString("settings_title", null);
		
		String pageTitle = "";
		boolean isExpectedTitle = false;
		String exception = null;
		
		for(int i = 0; i < retries && ! isExpectedTitle; i++) {
			try {
				
				Log.d(Inetify.LOG_TAG, String.format("Sleeping %s ms before testing internet connectivity", delay));
				try {
					Thread.sleep(delay);
				} catch (InterruptedException e) {
					Log.d(Inetify.LOG_TAG, String.format("Cancelled during sleep(), aborting"));
					return null;
				}
				
				if(cancelledOrNotWifi(wifiOnly)) {
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
		
		INetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
		IWifiInfo wifiInfo = wifiManager.getConnectionInfo();
		
		String type = null;
		String extra = null;
		
		if(networkInfo != null) {
			type = networkInfo.getTypeName();
			if(networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
				extra = wifiInfo.getSSID();
			} else {
				extra = networkInfo.getSubtypeName();
			}
		}
		
		String notConnected = context.getString(R.string.tester_not_connected);
		if(type == null) {
			type = notConnected;
		}
		if(extra == null) {
			extra = notConnected;
		}
		
		TestInfo info = new TestInfo();
		info.setTimestamp(System.currentTimeMillis());
		info.setType(type);
		info.setExtra(extra);
		info.setSite(server);
		info.setTitle(title);
		info.setPageTitle(pageTitle);
		info.setIsExpectedTitle(isExpectedTitle);
		info.setException(exception);
		
		if(cancelledOrNotWifi(wifiOnly)) {
			Log.d(Inetify.LOG_TAG, "Cancelling internet connectivity test");
			return null;
		}
		
		return info;	
	}
	
	/**
	 * Returns true if the test was cancelled, or if there was no Wifi
	 * connection and the given wifiOnly is true.
	 * @param wifiOnly if true, this method returns true if there is no
	 *        Wifi connection
	 * @return true if the test should be cancelled, false otherwise
	 */
	private boolean cancelledOrNotWifi(final boolean wifiOnly) {
		
		if(cancelled.get()) {
			Log.d(Inetify.LOG_TAG, String.format("Cancelled"));
			return true;
		}
		
		if(wifiOnly && ! isWifiConnected()) {
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
		if(testThread != null) {
			testThread.interrupt();
		}
	}
	
	/**
	 * Returns true if Wifi is connected, false otherwise.
	 * @return true if Wifi is connected, false otherwise.
	 */
    public boolean isWifiConnected() {
    	INetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
    	IWifiInfo wifiInfo = wifiManager.getConnectionInfo();
    	
    	if(networkInfo != null && networkInfo.getType() == ConnectivityManager.TYPE_WIFI && networkInfo.isConnected()) {
    		if(wifiInfo != null && wifiInfo.getSSID() != null) {
    			return true;
    		}
    	}    	
    	return false;
    }
	
}
