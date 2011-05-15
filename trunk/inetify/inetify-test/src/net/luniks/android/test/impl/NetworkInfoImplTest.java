package net.luniks.android.test.impl;

import net.luniks.android.impl.NetworkInfoImpl;
import net.luniks.android.interfaces.INetworkInfo;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.test.AndroidTestCase;

public class NetworkInfoImplTest extends AndroidTestCase {
	
	public void testNetworkInfoImpl() {
		
		NetworkInfo real = ((ConnectivityManager)this.getContext().getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
		if(real == null) {
			fail("ConnectivityManager.getActiveNetworkInfo() is null, cannot run NetworkInfoImplTest");
		}
		
		INetworkInfo wrapped = new NetworkInfoImpl(real);
		
		assertEquals(real.getType(), wrapped.getType());
		assertEquals(real.getTypeName(), wrapped.getTypeName());
		assertEquals(real.getSubtypeName(), wrapped.getSubtypeName());
		assertEquals(real.isConnected(), wrapped.isConnected());
		
	}

}
