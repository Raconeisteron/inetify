package net.luniks.android.inetify;

import java.io.IOException;
import java.util.Date;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.text.format.DateFormat;

/**
 * Class to help with getting and formatting internet connectivity test information.
 * 
 * @author dode@luniks.net
 */
public class InetifyHelper {

	/** Application context */
	private final Context context;
	
	/** Shared preferences */
	private SharedPreferences sharedPreferences;
	
	/**
	 * Constructs a helper instance using the given Context and SharedPreferences.
	 * @param context
	 * @param sharedPreferences
	 */
	public InetifyHelper(final Context context, final SharedPreferences sharedPreferences) {
		this.context = context;
		this.sharedPreferences = sharedPreferences;
	}
	
	/**
	 * Gets network and Wifi info and tests if the internet site in the settings has
	 * the expected title and returns and instance of TestInfo.
	 * @param retries number of test retries
	 * @return instance of TestInfo containing the test results
	 */
	public TestInfo getTestInfo(final int retries) {
		ConnectivityManager connectivityManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
		WifiManager wifiManager =  (WifiManager)context.getSystemService(Context.WIFI_SERVICE);
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
	
	/**
	 * Returns the given date and the time formatted for the default locale.
	 * @param date date to format
	 * @return String formatted date and time
	 */
	public String getDateTimeString(final Date date) {
		String dateString = DateFormat.getMediumDateFormat(context).format(date);
		String timeString = DateFormat.getTimeFormat(context).format(date);
		return String.format("%s, %s", dateString, timeString);
	}
	
}
