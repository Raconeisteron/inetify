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

	public boolean setWifiEnabled(boolean enabled) {
		return true;
	}

}
