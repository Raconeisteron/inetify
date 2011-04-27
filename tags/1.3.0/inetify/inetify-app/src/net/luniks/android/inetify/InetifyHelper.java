package net.luniks.android.inetify;

import java.io.IOException;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

/**
 * Class to help with getting and formatting internet connectivity test information.
 * 
 * @author dode@luniks.net
 */
public class InetifyHelper {

	/** Application context */
	private final Context context;
	
	/** Shared preferences */
	private final SharedPreferences sharedPreferences;
	
	/** Connectivity manager */
	private final ConnectivityManager connectivityManager;
	
	/** Wifi manager */
	private final WifiManager wifiManager;
	
	/**
	 * Constructs a helper instance using the given Context and SharedPreferences.
	 * @param context
	 * @param sharedPreferences
	 */
	public InetifyHelper(final Context context, final SharedPreferences sharedPreferences, 
			final ConnectivityManager connectivityManager, final WifiManager wifiManager) {
		
		this.context = context;
		this.sharedPreferences = sharedPreferences;
		this.connectivityManager = connectivityManager;
		this.wifiManager = wifiManager;
	}
	
	/**
	 * Gets network and Wifi info and tests if the internet site in the settings has
	 * the expected title and returns and instance of TestInfo.
	 * @param retries number of test retries
	 * @return instance of TestInfo containing the test results
	 */
	public TestInfo getTestInfo(final int retries) {
		NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
		WifiInfo wifiInfo = wifiManager.getConnectionInfo();
		
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
				pageTitle = ConnectivityUtil.getPageTitle(server);
				isExpectedTitle = ConnectivityUtil.isExpectedTitle(title, pageTitle);
				info.setException(null);
			} catch (IOException e) {
				info.setException(e.getLocalizedMessage());
			}
		}
		
		info.setPageTitle(pageTitle);
		info.setIsExpectedTitle(isExpectedTitle);
		
		return info;	
	}
	
    /**
     * Returns true if there currently is a Wifi connection, false otherwise.
     * @return boolean true if Wifi is connected, false otherwise
     */
    public boolean isWifiConnected() {
    	NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
    	WifiInfo wifiInfo = wifiManager.getConnectionInfo();
    	
    	if(networkInfo != null && networkInfo.getType() == ConnectivityManager.TYPE_WIFI && networkInfo.isConnected()) {
    		if(wifiInfo != null && wifiInfo.getSSID() != null) {
    			return true;
    		}
    	}    	
    	return false;
    }
	
	/**
	 * Returns a styled string describing the current data connection from the given TestInfo.
	 * @param info
	 * @return string describing the current data connection
	 */
	public String getConnectionString(final TestInfo info) {
		if(info.getType() != null) {
			return context.getString(R.string.inetify_connection_string, info.getType(), info.getExtra());
		} else {
			return context.getString(R.string.inetify_connection_string_no_connection);
		}
	}
	
	/**
	 * Returns a styled string describing the status of internet connectivity.
	 * @param info
	 * @return string describing the status of internet connectivity
	 */
	public String getInfoString(final TestInfo info) {
		if(info.getIsExpectedTitle()) {
			return context.getString(R.string.inetify_info_string_ok);
		} else {
			return context.getString(R.string.inetify_info_string_nok);
		}
	}
	
}
