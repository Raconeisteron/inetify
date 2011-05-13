package net.luniks.android.inetify;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

/**
 * Utility class providing some methods to test internet connectivity.
 * 
 * @author dode@luniks.net
 */
public final class TitleVerifierImpl implements TitleVerifier {
	
	/** Connect timeout */
	private static final int TIMEOUT = 3000;
	
	/** Protocol */
	private static final String PROTOCOL = "://";
	
	/** Protocol HTTP */
	private static final String PROTOCOL_HTTP = "http://";
	
	/* 
	 * (non-Javadoc)
	 * @see net.luniks.android.inetify.TitleVerifier#isExpectedTitle(java.lang.String, java.lang.String)
	 */
	public boolean isExpectedTitle(final String title, final String pageTitle) {
		if(title == null || title.length() == 0 || pageTitle == null || pageTitle.length() == 0) {
			return false;
		}
		return pageTitle.toUpperCase().contains(title.toUpperCase());
	}

	/* 
	 * (non-Javadoc)
	 * @see net.luniks.android.inetify.TitleVerifier#getPageTitle(java.lang.String)
	 */
	public String getPageTitle(final String server) throws Exception {
		String url = addProtocol(server);
		// Sometimes, this fails with "Connection reset by peer". Maybe this helps?
		System.setProperty("http.keepAlive", "false");
		Connection connection = Jsoup.connect(url);
		connection.timeout(TIMEOUT);
		Document document = connection.get();
		return document.title();
	}
	
	public static String addProtocol(final String url) {
		if(! url.contains(PROTOCOL)) {
			return String.format("%s%s", PROTOCOL_HTTP, url);
		}
		return url;
	}

}
