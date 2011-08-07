package net.luniks.android.inetify;

import android.location.Location;

public class WifiLocation {
	
	private String bssid;
	private String ssid;
	private String name;
	private Location location;
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
}
