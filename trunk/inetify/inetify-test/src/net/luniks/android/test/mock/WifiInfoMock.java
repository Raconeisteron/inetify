package net.luniks.android.test.mock;

import net.luniks.android.interfaces.IWifiInfo;

public class WifiInfoMock implements IWifiInfo {
	
	private String ssid;
	private String bssid;

	public String getSSID() {
		return ssid;
	}

	public void setSSID(String ssid) {
		this.ssid = ssid;
	}

	public String getBSSID() {
		return bssid;
	}
	
	public void setBSSID(String bssid) {
		this.bssid = bssid;
	}

}
