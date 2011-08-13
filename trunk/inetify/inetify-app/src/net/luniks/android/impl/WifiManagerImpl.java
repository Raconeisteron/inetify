/*
 * Copyright 2011 Torsten Römer
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
