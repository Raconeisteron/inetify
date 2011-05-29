package net.luniks.android.test.mock;

import net.luniks.android.interfaces.IWifiInfo;

public class WifiInfoMock implements IWifiInfo {
	
	private String ssid;

	public String getSSID() {
		return ssid;
	}

	public void setSSID(String ssid) {
		this.ssid = ssid;
	}

}
