package net.luniks.android.test.mock;

import net.luniks.android.interfaces.IWifiInfo;

public class WifiInfoMock implements IWifiInfo {
	
	private String ssid;
	private String macAddress;

	public String getSSID() {
		return ssid;
	}

	public void setSSID(String ssid) {
		this.ssid = ssid;
	}

	public String getMacAddress() {
		return macAddress;
	}
	
	public void setMacAddress(String macAddress) {
		this.macAddress = macAddress;
	}

}
