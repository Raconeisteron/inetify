package net.luniks.android.test.mock;

import net.luniks.android.interfaces.IWifiInfo;
import net.luniks.android.interfaces.IWifiManager;
import android.net.wifi.WifiManager;

public class WifiManagerMock implements IWifiManager {
	
	private IWifiInfo wifiInfo;
	private int wifiState = WifiManager.WIFI_STATE_ENABLED;
	
	public WifiManagerMock(final IWifiInfo wifiInfo) {
		this.wifiInfo = wifiInfo;
	}
	
	public void setWifiInfo(final IWifiInfo wifiInfo) {
		this.wifiInfo = wifiInfo;
	}

	public void setWifiState(final int wifiState) {
		this.wifiState = wifiState;
	}

	public IWifiInfo getWifiInfo() {
		return wifiInfo;
	}
	
	public IWifiInfo getConnectionInfo() {
		return wifiInfo;
	}
	
	public int getWifiState() {
		return wifiState;
	}

}
