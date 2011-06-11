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
