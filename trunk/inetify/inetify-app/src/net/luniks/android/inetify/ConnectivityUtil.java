package net.luniks.android.inetify;

import java.io.IOException;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class ConnectivityUtil {
	
	private static final int TIMEOUT = 3000;
	private static final int RETRIES = 3;
	private static final String PROTOCOL_HTTP = "http://";
	
	private ConnectivityUtil() {
		// Utility class
	}
	
	public static boolean shouldNotify(final ConnectivityManager connectivityManager, final String server, final String title) {
		boolean notify = false;
		if(hasWifiConnection(connectivityManager)) {
			notify = true;
			for(int i = 0; i < RETRIES && notify; i++) {
				try {
					String pageTitle = getPageTitle(server);
					notify = ! isExpectedTitle(title, pageTitle);
				} catch (IOException e) {
					notify = true;
				}
			}
		}
		return notify;
	}
	
	public static boolean hasWifiConnection(final ConnectivityManager connectivityManager) {
		NetworkInfo networkInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		return networkInfo.isConnected();
	}
	
	public static boolean isExpectedTitle(final String title, final String pageTitle) throws IOException {
		return title.toUpperCase().contains(pageTitle.toUpperCase());
	}

	public static String getPageTitle(final String server) throws IOException {
		String page = server;
		if(! server.startsWith(PROTOCOL_HTTP)) {
			page = String.format("%s%s", PROTOCOL_HTTP, server);
		}
		Connection connection = Jsoup.connect(page);
		connection.timeout(TIMEOUT);
		Document document = connection.get();
		return document.title();
	}

}
