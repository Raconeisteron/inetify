package net.luniks.android.inetify;

import java.util.Date;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Parcelable to hold the results of testing internet connectivity.
 * 
 * @author dode@luniks.net
 */
public class TestInfo implements Parcelable {
	
	public static final Parcelable.Creator<TestInfo> CREATOR = new TestInfoCreator();
	
	/**
	 * Default constructor
	 */
	public TestInfo() {
	}
	
	/**
	 * Constructor required by Parcelable
	 * @param source
	 */
	public TestInfo(final Parcel source) {
		timestamp = source.readLong();
		type = source.readInt();
		typeName = source.readString();
		extra = source.readString();
		extra2 = source.readString();
		site = source.readString();
		title = source.readString();
		pageTitle = source.readString();
		boolean[] val = new boolean[1];
		source.readBooleanArray(val);
		isExpectedTitle = val[0];
		exception = source.readString();		
	}
	
	/** Timestamp when the test was done */
	private long timestamp;
	
	/** Type of the data connection, i.e. ConnectivityManager.TYPE_WIFI */
	private int type;
	
	/** Type of the data connection, i.e. "WIFI" or "mobile" */
	private String typeName;
	
	/** The SSID of a Wifi connection or the subtype of a mobile connection, i.e. "UMTS" */
	private String extra;
	
	/** The BSSID of a Wifi connection */
	private String extra2;
	
	/** The internet site used for testing */
	private String site;
	
	/** The expected title */
	private String title;
	
	/** The title of the welcome page of the internet site */
	private String pageTitle;
	
	/** True if pageTitle contains the expected title */
	private boolean isExpectedTitle;
	
	/** If an exception occurred, null otherwise */
	private String exception;
	
	public long getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(final long timestamp) {
		this.timestamp = timestamp;
	}
	public int getType() {
		return type;
	}
	public void setType(final int type) {
		this.type = type;
	}
	public String getTypeName() {
		return typeName;
	}
	public void setTypeName(final String typeName) {
		this.typeName = typeName;
	}
	public String getExtra() {
		return extra;
	}
	public void setExtra(final String extra) {
		this.extra = extra;
	}
	public String getExtra2() {
		return extra2;
	}
	public void setExtra2(final String extra2) {
		this.extra2 = extra2;
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
	public String getException() {
		return exception;
	}
	public void setException(final String exception) {
		this.exception = exception;
	}
	
	/**
	 * String representation of this TestInfo instance.
	 * @return string representation
	 */
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("TestInfo [ timestamp = ").append(new Date(timestamp));
		buffer.append(", type = ").append(type);
		buffer.append(", typeName = ").append(typeName);
		buffer.append(", extra = ").append(extra);
		buffer.append(", extra2 = ").append(extra2);
		buffer.append(", site = ").append(site);
		buffer.append(", title = ").append(title);
		buffer.append(", pageTitle = ").append(pageTitle);
		buffer.append(", exception = ").append(exception);
		buffer.append(", expectedTitle = ").append(isExpectedTitle);
		buffer.append(" ]");
		return buffer.toString();
	}
	
	public int describeContents() {
		return 0;
	}
	
	public void writeToParcel(final Parcel dest, final int flags) {
		dest.writeLong(timestamp);
		dest.writeInt(type);
		dest.writeString(typeName);
		dest.writeString(extra);
		dest.writeString(extra2);
		dest.writeString(site);
		dest.writeString(title);
		dest.writeString(pageTitle);
		dest.writeBooleanArray(new boolean[] {isExpectedTitle});
		dest.writeString(exception);
	}
	
	private static class TestInfoCreator implements Parcelable.Creator<TestInfo> {
	      public TestInfo createFromParcel(final Parcel source) {
	            return new TestInfo(source);
	      }
	      public TestInfo[] newArray(final int size) {
	            return new TestInfo[size];
	      }
	}

}
