package net.luniks.android.test.mock;

import net.luniks.android.interfaces.IWifiInfo;
import net.luniks.android.interfaces.IWifiManager;

public class WifiManagerMock implements IWifiManager {
	
	private IWifiInfo wifiInfo;
	
	public WifiManagerMock(IWifiInfo wifiInfo) {
		this.wifiInfo = wifiInfo;
	}

	public IWifiInfo getWifiInfo() {
		return wifiInfo;
	}

	public void setWifiInfo(IWifiInfo wifiInfo) {
		this.wifiInfo = wifiInfo;
	}

	public IWifiInfo getConnectionInfo() {
		return wifiInfo;
	}

}
