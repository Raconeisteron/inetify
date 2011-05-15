package net.luniks.android.inetify;

import net.luniks.android.interfaces.IConnectivityManager;
import net.luniks.android.interfaces.INetworkInfo;
import net.luniks.android.interfaces.IWifiInfo;
import net.luniks.android.interfaces.IWifiManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.util.Log;

/**
 * Class to help with getting internet connectivity test information.
 * 
 * @author dode@luniks.net
 */
public class InetifyHelper {

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
	
	/**
	 * Constructs a helper instance using the given Context, SharedPreferences and TitleVerifier.
	 * @param context
	 * @param sharedPreferences
	 * @param titleVerifier
	 */
	public InetifyHelper(final Context context, final SharedPreferences sharedPreferences, 
			final IConnectivityManager connectivityManager, final IWifiManager wifiManager,
			final TitleVerifier titleVerifier) {
		
		this.context = context;
		this.sharedPreferences = sharedPreferences;
		this.connectivityManager = connectivityManager;
		this.wifiManager = wifiManager;
		this.titleVerifier = titleVerifier;
	}
	
	/**
	 * Gets network and Wifi info and tests if the internet site in the settings has
	 * the expected title and returns and instance of TestInfo. Aborts testing and
	 * returns null if onlyWifi is true and Wifi disconnects during testing.
	 * @param retries number of test retries
	 * @param delay before each test attempt in milliseconds
	 * @param wifiOnly abort test if Wifi is not connected
	 * @return instance of TestInfo containing the test results
	 */
	public TestInfo getTestInfo(final int retries, final long delay, final boolean wifiOnly) {
		INetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
		IWifiInfo wifiInfo = wifiManager.getConnectionInfo();
		
		String server = sharedPreferences.getString("settings_server", null);
		String title = sharedPreferences.getString("settings_title", null);
		
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
		
		String notConnected = context.getString(R.string.helper_not_connected);
		if(type == null) {
			type = notConnected;
		}
		if(extra == null) {
			extra = notConnected;
		}
		
		String pageTitle = "";
		boolean isExpectedTitle = false;
			
		TestInfo info = new TestInfo();
		info.setTimestamp(System.currentTimeMillis());
		info.setType(type);
		info.setExtra(extra);
		info.setSite(server);
		info.setTitle(title);
		
		for(int i = 0; i < retries && ! isExpectedTitle; i++) {
			try {
				Log.d(Inetify.LOG_TAG, String.format("Sleeping %s ms before testing internet connectivity", delay));
				Thread.sleep(delay);
				if(wifiOnly && ! isWifiConnected()) {
					Log.d(Inetify.LOG_TAG, "Aborting internet connectivity test as there is no Wifi connection anymore");
					return null;
				}
				Log.d(Inetify.LOG_TAG, String.format("Testing internet connectivity, try %s of %s", i + 1, retries));
				pageTitle = titleVerifier.getPageTitle(server);
				isExpectedTitle = titleVerifier.isExpectedTitle(title, pageTitle);
				Log.d(Inetify.LOG_TAG, String.format("Internet connectivity was %s", isExpectedTitle));
				info.setException(null);
			} catch(InterruptedException e) {
				info.setException(e.getLocalizedMessage());
				break;
			} catch(Exception e) {
				Log.d(Inetify.LOG_TAG, String.format("Internet connectivity test failed with %s", e.getMessage()));
				info.setException(e.getLocalizedMessage());
			}
		}
		
		info.setPageTitle(pageTitle);
		info.setIsExpectedTitle(isExpectedTitle);
		
		if(wifiOnly && ! isWifiConnected()) {
			Log.d(Inetify.LOG_TAG, "Aborting internet connectivity test as there is no Wifi connection anymore");
			return null;
		}
		
		return info;	
	}
	
    /**
     * Returns true if there currently is a Wifi connection, false otherwise.
     * @return boolean true if Wifi is connected, false otherwise
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
