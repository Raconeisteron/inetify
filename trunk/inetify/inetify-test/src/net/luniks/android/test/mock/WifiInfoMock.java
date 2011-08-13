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
import android.net.wifi.SupplicantState;

public class WifiInfoMock implements IWifiInfo {
	
	private String ssid;
	private String bssid;
	private SupplicantState supplicantState;

	public String getSSID() {
		return ssid;
	}

	public WifiInfoMock setSSID(final String ssid) {
		this.ssid = ssid;
		return this;
	}

	public String getBSSID() {
		return bssid;
	}
	
	public WifiInfoMock setBSSID(final String bssid) {
		this.bssid = bssid;
		return this;
	}

	public SupplicantState getSupplicantState() {
		return supplicantState;
	}

	public void setSupplicantState(final SupplicantState supplicantState) {
		this.supplicantState = supplicantState;
	}

}
