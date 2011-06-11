package net.luniks.android.impl;

import net.luniks.android.interfaces.IWifiInfo;
import android.net.wifi.WifiInfo;

/**
 * Implementation of IWifiInfo.
 * @see android.net.wifi.WifiInfo
 * 
 * @author torsten.roemer@luniks.net
 */
public class WifiInfoImpl implements IWifiInfo {
	
	private final WifiInfo wifiInfo;

	private WifiInfoImpl(final WifiInfo wifiInfo) {
		this.wifiInfo = wifiInfo;
	}
	
	public static WifiInfoImpl getInstance(final WifiInfo wifiInfo) {
		if(wifiInfo == null) {
			return null;
		}
		return new WifiInfoImpl(wifiInfo);
	}

	public String getSSID() {
		return wifiInfo.getSSID();
	}
	
	public String getBSSID() {
		return wifiInfo.getBSSID();
	}
	
	public WifiInfo getWifiInfo() {
		return wifiInfo;
	}

}
