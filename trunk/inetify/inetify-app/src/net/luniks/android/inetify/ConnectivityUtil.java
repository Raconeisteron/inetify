package net.luniks.android.inetify;

import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.URL;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class ConnectivityUtil {
	
	private static final int TIMEOUT = 5000;
	
	private ConnectivityUtil() {
		// Utility class
	}
	
	public static boolean shouldNotify(final ConnectivityManager connectivityManager, final String server) {
		boolean notify = false;
		if(hasWifiConnection(connectivityManager)) {
			notify = ConnectivityUtil.isReachable(server) || ConnectivityUtil.isLoadable(server);
		}
		return notify;
	}
	
	public static boolean hasWifiConnection(final ConnectivityManager connectivityManager) {
		NetworkInfo networkInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		return networkInfo.isConnected();
	}
	
	public static boolean isReachable(final String server) {
		try {
			InetAddress inetAddress = InetAddress.getByName(server);
			return inetAddress.isReachable(TIMEOUT);
		} catch(Exception e) {
			return false;
		}
	}
	
	public static boolean isLoadable(final String server) {
		try {
			URL url = new URL(String.format("http://%s", server));
			HttpURLConnection connection = (HttpURLConnection)url.openConnection();
			connection.setRequestProperty("Connection", "close");
			connection.setConnectTimeout(TIMEOUT);
			connection.connect();
			return connection.getResponseCode() == 200;
		} catch(Exception e) {
			return false;
		}
	}

}
