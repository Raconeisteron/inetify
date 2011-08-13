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
