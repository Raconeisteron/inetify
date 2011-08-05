package net.luniks.android.inetify;

import android.location.Location;

public class WifiLocation {
	
	private String name;
	private String ssid;
	private Location location;
	private float distance;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getSSID() {
		return ssid;
	}
	public void setSSID(String ssid) {
		this.ssid = ssid;
	}
	public Location getLocation() {
		return location;
	}
	public void setLocation(Location location) {
		this.location = location;
	}
	public float getDistance() {
		return distance;
	}
	public void setDistance(float distance) {
		this.distance = distance;
	}
}
