package net.luniks.android.impl;

import net.luniks.android.interfaces.IWifiInfo;
import net.luniks.android.interfaces.IWifiManager;
import android.net.wifi.WifiManager;

/**
 * Implementation of IWifiManager.
 * @see android.net.wifi.WifiManager
 * 
 * @author torsten.roemer@luniks.net
 */
public class WifiManagerImpl implements IWifiManager {
	
	private final WifiManager wifiManager;
	
	public WifiManagerImpl(final WifiManager wifiManager) {
		this.wifiManager = wifiManager;
	}
	
	/**
	 * Returns the wrapped WifiInfo from the wrapped WifiManager,
	 * null if it was null.
	 */
	public IWifiInfo getConnectionInfo() {
		return WifiInfoImpl.getInstance(wifiManager.getConnectionInfo());
	}
	
	/**
	 * Returns the Wifi state
	 * @return int
	 */
	public int getWifiState() {
		return wifiManager.getWifiState();
	}
	
	/**
	 * Enables Wifi if the given boolean is true, disables it otherwise.
	 * @param enabled
	 * @return boolean
	 */
	public boolean setWifiEnabled(final boolean enabled) {
		return wifiManager.setWifiEnabled(enabled);
	}

}
