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
package net.luniks.android.inetify.test;

import net.luniks.android.interfaces.IWifiInfo;
import net.luniks.android.interfaces.IWifiManager;
import android.net.wifi.WifiManager;

public class TestWifiManager implements IWifiManager {
	
	private int wifiState = WifiManager.WIFI_STATE_DISABLED;
	private int setWifiEnabledCallCount = 0;
	
	public void setWifiState(final int wifiState) {
		this.wifiState = wifiState;
	}
	
	public int getSetWifiEnabledCallCount() {
		return setWifiEnabledCallCount;
	}

	public IWifiInfo getConnectionInfo() {
		// TODO Auto-generated method stub
		return null;
	}

	public int getWifiState() {
		return wifiState;
	}

	public boolean setWifiEnabled(boolean enabled) {
		setWifiEnabledCallCount++;
		this.wifiState = enabled ? WifiManager.WIFI_STATE_ENABLED : WifiManager.WIFI_STATE_DISABLED;
		return true;
	}

}
