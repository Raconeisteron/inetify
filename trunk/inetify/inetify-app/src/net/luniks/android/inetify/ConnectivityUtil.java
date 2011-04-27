package net.luniks.android.inetify;

import java.io.IOException;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

/**
 * Utility class providing some methods to test internet connectivity.
 * 
 * @author dode@luniks.net
 */
public final class ConnectivityUtil {
	
	/** Connect timeout */
	private static final int TIMEOUT = 3000;
	
	/** HTTP protocol */
	private static final String PROTOCOL_HTTP = "http://";
	
	private ConnectivityUtil() {
		// Utility class
	}
	
	/**
	 * Returns true if the given pageTitle contains the given title, case insensitive.
	 * @param title (part of) the expected title
	 * @param pageTitle page title
	 * @return boolean true if pageTitle contains title
	 */
	public static boolean isExpectedTitle(final String title, final String pageTitle) {
		if(title == null || title.length() == 0 || pageTitle == null || pageTitle.length() == 0) {
			return false;
		}
		return pageTitle.toUpperCase().contains(title.toUpperCase());
	}

	/**
	 * Returns the page title of the welcome page of the given internet server
	 * @param server internet server
	 * @return String page title
	 * @throws IOException if a connection error occurs
	 */
	public static String getPageTitle(final String server) throws IOException {
		String page = server;
		if(! server.startsWith(PROTOCOL_HTTP)) {
			page = String.format("%s%s", PROTOCOL_HTTP, server);
		}
		// Sometimes, this fails with "Connection reset by peer". Maybe this helps?
		System.setProperty("http.keepAlive", "false");
		Connection connection = Jsoup.connect(page);
		connection.timeout(TIMEOUT);
		Document document = connection.get();
		return document.title();
	}

}
