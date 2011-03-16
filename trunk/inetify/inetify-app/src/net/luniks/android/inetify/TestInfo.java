package net.luniks.android.inetify;

public class TestInfo {
	
	private boolean isWifiConnected;
	private String wifiSSID;
	private String site;
	private String title;
	private String pageTitle;
	private boolean isExpectedTitle;
	private Exception exception;
	
	public TestInfo() {
	}
	
	public TestInfo(final Exception exception) {
		this.exception = exception;
	}
	
	public boolean isWifiConnected() {
		return isWifiConnected;
	}
	public void setWifiConnected(final boolean isWifiConnected) {
		this.isWifiConnected = isWifiConnected;
	}
	public String getWifiSSID() {
		return wifiSSID;
	}
	public void setWifiSSID(final String wifiSSID) {
		this.wifiSSID = wifiSSID;
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
	public boolean isExpectedTitle() {
		return isExpectedTitle;
	}
	public void setExpectedTitle(final boolean isExpectedTitle) {
		this.isExpectedTitle = isExpectedTitle;
	}
	public Exception getException() {
		return exception;
	}
	public void setException(final Exception exception) {
		this.exception = exception;
	}

}
