package net.luniks.android.test.impl;

import net.luniks.android.impl.ConnectivityManagerImpl;
import net.luniks.android.impl.NetworkInfoImpl;
import net.luniks.android.interfaces.IConnectivityManager;
import android.content.Context;
import android.net.ConnectivityManager;
import android.test.AndroidTestCase;

public class ConnectivityManagerImplTest extends AndroidTestCase {

	public void testConnectivityManagerImpl() {
		
		ConnectivityManager real = ((ConnectivityManager)this.getContext().getSystemService(Context.CONNECTIVITY_SERVICE));
		if(real.getActiveNetworkInfo() == null) {
			fail("ConnectivityManager.getActiveNetworkInfo() is null, cannot run NetworkInfoImplTest");
		}
		
		IConnectivityManager wrapper = new ConnectivityManagerImpl(real);
		
		assertNotNull(wrapper);
		
		// FIXME Not so clean...
		assertEquals(String.valueOf(real.getActiveNetworkInfo()), String.valueOf(((NetworkInfoImpl)wrapper.getActiveNetworkInfo()).getNetworkInfo()));
		
	}
	
}
