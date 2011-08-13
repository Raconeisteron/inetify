/*
 * Copyright 2011 Torsten Römer
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.luniks.android.inetify;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

/**
 * TitleVerifier implementation.
 * 
 * @author torsten.roemer@luniks.net
 */
public final class TitleVerifierImpl implements TitleVerifier {
	
	/** Connect timeout */
	private static final int TIMEOUT = 3000;
	
	/** Protocol */
	private static final String PROTOCOL = "://";
	
	/** Protocol HTTP */
	private static final String PROTOCOL_HTTP = "http://";
	
	/**
	 * Returns true if the given pageTitle contains the given title, case insensitive.
	 * @param title (part of) the expected title
	 * @param pageTitle page title
	 * @return boolean true if pageTitle contains title
	 */
	public boolean isExpectedTitle(final String title, final String pageTitle) {
		if(title == null || title.length() == 0 || pageTitle == null || pageTitle.length() == 0) {
			return false;
		}
		return pageTitle.toUpperCase().contains(title.toUpperCase());
	}

	/**
	 * Returns the page title of the welcome page of the given internet server
	 * @param server internet server
	 * @return String page title
	 * @throws Exception if some error occurs
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
	
	/**
	 * Adds protocol "http://" to the given url if it doesn't appear to have
	 * a protocol, and returns it
	 * @param url
	 * @return url
	 */
	public static String addProtocol(final String url) {
		if(! url.contains(PROTOCOL)) {
			return String.format("%s%s", PROTOCOL_HTTP, url);
		}
		return url;
	}

}
