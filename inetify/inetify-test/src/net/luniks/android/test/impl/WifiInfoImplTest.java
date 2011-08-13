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
package net.luniks.android.test.impl;

import net.luniks.android.impl.WifiInfoImpl;
import net.luniks.android.interfaces.IWifiInfo;
import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.test.AndroidTestCase;

public class WifiInfoImplTest extends AndroidTestCase {
	
	public void testGetInstanceNull() {
		
		IWifiInfo wrapped = WifiInfoImpl.getInstance(null);
		
		assertNull(wrapped);
		
	}
	
	public void testWifiInfoImpl() {
		WifiInfo real = ((WifiManager)this.getContext().getSystemService(Context.WIFI_SERVICE)).getConnectionInfo();
		if(real == null) {
			fail("WifiManager.getConnectionInfo() is null, cannot run WifiInfoImplTest");
		}
		
		IWifiInfo wrapped = WifiInfoImpl.getInstance(real);
		
		assertEquals(real.getSSID(), wrapped.getSSID());
		assertEquals(real.getBSSID(), wrapped.getBSSID());
	}

}
