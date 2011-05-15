package net.luniks.android.impl;

import net.luniks.android.interfaces.IWifiInfo;
import net.luniks.android.interfaces.IWifiManager;
import android.net.wifi.WifiManager;

/**
 * Implementation of IWifiManager.
 * @see android.net.wifi.WifiManager
 * 
 * @author dode@luniks.net
 */
public class WifiManagerImpl implements IWifiManager {
	
	private final WifiManager wifiManager;
	
	public WifiManagerImpl(final WifiManager wifiManager) {
		this.wifiManager = wifiManager;
	}

	public IWifiInfo getConnectionInfo() {
		return new WifiInfoImpl(wifiManager.getConnectionInfo());
	}

}
