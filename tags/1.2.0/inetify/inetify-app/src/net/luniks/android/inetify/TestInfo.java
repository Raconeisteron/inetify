package net.luniks.android.inetify;

/**
 * Bean to hold the results of testing internet connectivity.
 * 
 * @author dode@luniks.net
 */
public class TestInfo {
	
	/** Type of the data connection, i.e. "WIFI" or "mobile" */
	private String type;
	
	/** The SSID of a Wifi connection or the subtype of a mobile connection, i.e. "UMTS" */
	private String extra;
	
	/** The internet site used for testing */
	private String site;
	
	/** The expected title */
	private String title;
	
	/** The title of the welcome page of the internet site */
	private String pageTitle;
	
	/** True if pageTitle contains the expected title */
	private boolean isExpectedTitle;
	
	/** If an exception occurred, null otherwise */
	private Exception exception;
	
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getExtra() {
		return extra;
	}
	public void setExtra(final String extra) {
		this.extra = extra;
	}
	public String getSite() {
		return site;
	}
	public void setSite(final String site) {
		this.site = site;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(final String title) {
		this.title = title;
	}
	public String getPageTitle() {
		return pageTitle;
	}
	public void setPageTitle(final String pageTitle) {
		this.pageTitle = pageTitle;
	}
	public boolean getIsExpectedTitle() {
		return isExpectedTitle;
	}
	public void setIsExpectedTitle(final boolean isExpectedTitle) {
		this.isExpectedTitle = isExpectedTitle;
	}
	public Exception getException() {
		return exception;
	}
	public void setException(final Exception exception) {
		this.exception = exception;
	}

}
