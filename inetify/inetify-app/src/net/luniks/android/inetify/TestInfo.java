package net.luniks.android.inetify;

public class TestInfo {
	
	private String type;
	private String extra;
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
