package net.luniks.android.test.mock;

import net.luniks.android.interfaces.IWifiInfo;
import android.net.wifi.SupplicantState;

public class WifiInfoMock implements IWifiInfo {
	
	private String ssid;
	private String bssid;
	private SupplicantState supplicantState;

	public String getSSID() {
		return ssid;
	}

	public WifiInfoMock setSSID(final String ssid) {
		this.ssid = ssid;
		return this;
	}

	public String getBSSID() {
		return bssid;
	}
	
	public WifiInfoMock setBSSID(final String bssid) {
		this.bssid = bssid;
		return this;
	}

	public SupplicantState getSupplicantState() {
		return supplicantState;
	}

	public void setSupplicantState(final SupplicantState supplicantState) {
		this.supplicantState = supplicantState;
	}

}
