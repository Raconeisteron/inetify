package net.luniks.android.impl;

import net.luniks.android.interfaces.IWifiInfo;
import android.net.wifi.WifiInfo;

/**
 * Implementation of IWifiInfo.
 * @see android.net.wifi.WifiInfo
 * 
 * @author dode@luniks.net
 */
public class WifiInfoImpl implements IWifiInfo {
	
	private final WifiInfo wifiInfo;

	public WifiInfoImpl(final WifiInfo wifiInfo) {
		this.wifiInfo = wifiInfo;
	}

	public String getSSID() {
		return wifiInfo.getSSID();
	}
	
	public WifiInfo getWifiInfo() {
		return wifiInfo;
	}

}
