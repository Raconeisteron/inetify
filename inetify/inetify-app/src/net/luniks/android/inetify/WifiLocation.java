package net.luniks.android.inetify;

import android.location.Location;

/**
 * Bean that wraps a Location and holds some additional data.
 * 
 * @author torsten.roemer@luniks.net
 */
public class WifiLocation {
	
	/** BSSID of the Wifi */
	private String bssid;
	
	/** SSID of the Wifi */
	private String ssid;
	
	/** Name of the Wifi given by the user */
	private String name;
	
	/** Location of the Wifi */
	private Location location;
	
	/** Distance to the current location */
	private float distance;
	
	public String getBSSID() {
		return bssid;
	}
	public void setBSSID(final String bssid) {
		this.bssid = bssid;
	}
	public String getSSID() {
		return ssid;
	}
	public void setSSID(final String ssid) {
		this.ssid = ssid;
	}
	public String getName() {
		return name;
	}
	public void setName(final String name) {
		this.name = name;
	}
	public Location getLocation() {
		return location;
	}
	public void setLocation(final Location location) {
		this.location = location;
	}
	public float getDistance() {
		return distance;
	}
	public void setDistance(final float distance) {
		this.distance = distance;
	}
	
	/**
	 * String representation of this WifiLocation instance.
	 * @return string representation
	 */
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("WifiLocation [ BSSID = ").append(bssid);
		buffer.append(", SSID = ").append(ssid);
		buffer.append(", name = ").append(name);
		buffer.append(", distance = ").append(distance);
		buffer.append(", location = ").append(location);
		buffer.append(" ]");
		return buffer.toString();
	}
}
