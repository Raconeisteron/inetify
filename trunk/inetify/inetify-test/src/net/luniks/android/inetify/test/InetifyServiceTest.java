package net.luniks.android.inetify.test;

import net.luniks.android.inetify.ConnectivityActionReceiver;
import net.luniks.android.inetify.InetifyService;
import android.content.Intent;
import android.test.ServiceTestCase;

public class InetifyServiceTest extends ServiceTestCase<InetifyService> {

	public InetifyServiceTest() {
		super(InetifyService.class);
	}

	public void testStartStop() throws InterruptedException {
		
		Intent serviceIntent = new Intent("net.luniks.android.inetify.InetifyService");
		serviceIntent.putExtra(ConnectivityActionReceiver.EXTRA_IS_WIFI_CONNECTED, true);
		
		this.startService(serviceIntent);
		
		InetifyService inetifyService = this.getService();
		
		assertTrue(inetifyService.isStarted());
		
		this.shutdownService();
		
		assertFalse(inetifyService.isStarted());
	}

}
