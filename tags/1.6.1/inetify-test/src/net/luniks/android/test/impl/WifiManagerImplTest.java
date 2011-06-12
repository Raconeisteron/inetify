package net.luniks.android.test.impl;

import net.luniks.android.impl.WifiInfoImpl;
import net.luniks.android.impl.WifiManagerImpl;
import net.luniks.android.interfaces.IWifiManager;
import android.content.Context;
import android.net.wifi.WifiManager;
import android.test.AndroidTestCase;

public class WifiManagerImplTest extends AndroidTestCase {

	public void testWifiManagerImplTest() {
		
		WifiManager real = ((WifiManager)this.getContext().getSystemService(Context.WIFI_SERVICE));
		
		IWifiManager wrapper = new WifiManagerImpl(real);
		
		// FIXME Not so clean...
		assertEquals(String.valueOf(real.getConnectionInfo()), String.valueOf(((WifiInfoImpl)wrapper.getConnectionInfo()).getWifiInfo()));
		
	}
	
}
