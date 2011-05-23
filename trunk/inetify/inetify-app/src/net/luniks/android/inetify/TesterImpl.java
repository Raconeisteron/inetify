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
 * Class providing methods to get internet connectivity test information.
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
	
	// FIXME Just for testing
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
	
	/* (non-Javadoc)
	 * @see net.luniks.android.inetify.Tester#getTestInfo(int, long, boolean)
	 */
	// FIXME Replace Thread.sleep() with a Timer or so, add test for cancelling retries
	public TestInfo test(final int retries, final long delay, final boolean wifiOnly) {
		
		testThread = Thread.currentThread();
		
		cancelled.set(false);
		
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
		
		String notConnected = context.getString(R.string.tester_not_connected);
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
				if(cancelled.get()) {
					Log.d(Inetify.LOG_TAG, String.format("Cancelled, aborting"));
					return null;
				}
				Log.d(Inetify.LOG_TAG, String.format("Sleeping %s ms before testing internet connectivity", delay));
				try {
					Thread.sleep(delay);
				} catch (InterruptedException e) {
					Log.d(Inetify.LOG_TAG, String.format("Cancelled during sleep(), aborting"));
					return null;
				}
				if(wifiOnly && ! isWifiConnected()) {
					Log.d(Inetify.LOG_TAG, "Aborting internet connectivity test as there is no Wifi connection anymore");
					return null;
				}
				Log.d(Inetify.LOG_TAG, String.format("Testing internet connectivity, try %s of %s", i + 1, retries));
				pageTitle = titleVerifier.getPageTitle(server);
				isExpectedTitle = titleVerifier.isExpectedTitle(title, pageTitle);
				Log.d(Inetify.LOG_TAG, String.format("Internet connectivity was %s", isExpectedTitle));
				info.setException(null);
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
	
    /* (non-Javadoc)
	 * @see net.luniks.android.inetify.Tester#isWifiConnected()
	 */	
	public void cancel() {
		this.cancelled.set(true);
		if(testThread != null) {
			testThread.interrupt();
		}
	}
	
    /* (non-Javadoc)
	 * @see net.luniks.android.inetify.Tester#isWifiConnected()
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
