package net.luniks.android.inetify;

/**
 * Interface for a class fetching and comparing the title of the welcome page
 * of an internet server with an expected title.
 * 
 * @author dode@luniks.net
 */
public interface TitleVerifier {

	/**
	 * Returns true if the given pageTitle contains the given title, case insensitive.
	 * @param title (part of) the expected title
	 * @param pageTitle page title
	 * @return boolean true if pageTitle contains title
	 */
	boolean isExpectedTitle(final String title, final String pageTitle);

	/**
	 * Returns the page title of the welcome page of the given internet server
	 * @param server internet server
	 * @return String page title
	 * @throws Exception if some error occurs
	 */
	String getPageTitle(final String server) throws Exception;

}