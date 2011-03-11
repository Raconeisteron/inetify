package net.luniks.android.inetify;

import java.io.IOException;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class ConnectivityUtil {
	
	private static final int TIMEOUT = 3000;
	private static final int RETRIES = 3;
	private static final String PROTOCOL_HTTP = "http://";
	
	private ConnectivityUtil() {
		// Utility class
	}
	
	public static boolean haveInternet(final String server, final String title) {
		boolean internet = false;
		for(int i = 0; i < RETRIES && ! internet; i++) {
			try {
				String pageTitle = getPageTitle(server);
				internet = isExpectedTitle(title, pageTitle);
			} catch (IOException e) {
				internet = false;
			}
		}
		return internet;
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