package net.luniks.android.inetify.test;

import net.luniks.android.inetify.ConnectivityActionReceiver;
import net.luniks.android.inetify.InetifyService;
import android.content.Intent;
import android.os.Looper;
import android.test.ServiceTestCase;

// FIXME Better ways to wait for the service to be started/stopped
public class InetifyServiceTest extends ServiceTestCase<InetifyService> {

	public InetifyServiceTest() {
		super(InetifyService.class);
	}
	
	public void testOnBind() {
		
		InetifyService inetifyService = new InetifyService();
		
		assertNull(inetifyService.onBind(new Intent()));
		
	}
	
	/*
	 * TODO: Test that the AsyncTask is not busy/cancelled
	 */
	public void testStartStopWifiNotConnected() throws InterruptedException {
		
		boolean wifiConnected = false;
		
		Intent serviceIntent = new Intent("net.luniks.android.inetify.InetifyService");
		serviceIntent.putExtra(ConnectivityActionReceiver.EXTRA_IS_WIFI_CONNECTED, wifiConnected);
		
		final AsyncTaskServiceTestThread serviceTestThread = new AsyncTaskServiceTestThread(serviceIntent);
		
		// Assert that service was started
		serviceTestThread.runAfterOnPreExecute = new Runnable() {
			public void run() {
				assertTrue(serviceTestThread.serviceToTest.isStarted());
			}
		};
		
		// Start the service
		serviceTestThread.start();
		
		// Wait a bit for the thread to start the service
		Thread.sleep(1000);
		
		// When Wifi is not connected, the service should do nothing and virtually immediately stop itself
		// this.shutdownService();
		
		assertFalse(serviceTestThread.serviceToTest.isStarted());

	}

	/*
	 * TODO: Test that the AsyncTask is not busy/cancelled
	 */
	public void testStartStopWifiConnected() throws InterruptedException {
		
		boolean wifiConnected = true;
		
		Intent serviceIntent = new Intent("net.luniks.android.inetify.InetifyService");
		serviceIntent.putExtra(ConnectivityActionReceiver.EXTRA_IS_WIFI_CONNECTED, wifiConnected);
		
		final AsyncTaskServiceTestThread serviceTestThread = new AsyncTaskServiceTestThread(serviceIntent);
		
		// Assert that service was started
		serviceTestThread.runAfterOnPreExecute = new Runnable() {
			public void run() {
				assertTrue(serviceTestThread.serviceToTest.isStarted());
			}
		};
		
		// Start the service
		serviceTestThread.start();
		
		// Wait a bit for the thread to start the service
		Thread.sleep(1000);
		
		// When Wifi is connected, the service should first sleep for TEST_DELAY_MILLIS
		assertTrue(serviceTestThread.serviceToTest.isStarted());
		
		// FIXME Make sure that this also cancels the AsyncTask
		this.shutdownService();
		
		assertFalse(serviceTestThread.serviceToTest.isStarted());

	}

	/*
	 * TODO: Test that the AsyncTask is not busy/cancelled
	 */
	public void testStopsItselfWhenWifiNotConnected() throws Exception {
		
		boolean wifiConnected = false;
		
		Intent serviceIntent = new Intent("net.luniks.android.inetify.InetifyService");
		serviceIntent.putExtra(ConnectivityActionReceiver.EXTRA_IS_WIFI_CONNECTED, wifiConnected);
		
		final AsyncTaskServiceTestThread serviceTestThread = new AsyncTaskServiceTestThread(serviceIntent);
		
		// Assert that service was started
		serviceTestThread.runAfterOnPreExecute = new Runnable() {
			public void run() {
				assertTrue(serviceTestThread.serviceToTest.isStarted());
			}
		};
		
		// Start the service
		serviceTestThread.start();
		
		// When Wifi is not connected, the service should do nothing and virtually immediately stop itself
		Thread.sleep(1000);
		
		// Assert that the service stopped itself
		assertFalse(serviceTestThread.serviceToTest.isStarted());
		
	}

	/*
	 * TODO: Test that the AsyncTask is not busy/cancelled
	 */
	public void testStopsItselfWhenWifiIsConnectedButDisconnects() throws Exception {
		
		boolean wifiConnected = true;
		
		Intent serviceIntent = new Intent("net.luniks.android.inetify.InetifyService");
		serviceIntent.putExtra(ConnectivityActionReceiver.EXTRA_IS_WIFI_CONNECTED, wifiConnected);
		
		final AsyncTaskServiceTestThread serviceTestThread = new AsyncTaskServiceTestThread(serviceIntent);
		
		// Assert that service was started
		serviceTestThread.runAfterOnPreExecute = new Runnable() {
			public void run() {
				assertTrue(serviceTestThread.serviceToTest.isStarted());
			}
		};
		
		// Start the service
		serviceTestThread.start();
		
		// When Wifi is connected but disconnects some seconds after, the service should realize that
		// after TEST_DELAY_MILLIS and stop itself
		Thread.sleep(InetifyService.TEST_DELAY_MILLIS + 1000);
		
		// Assert that the service stopped itself
		assertFalse(serviceTestThread.serviceToTest.isStarted());
		
	}
	
	private class AsyncTaskServiceTestThread extends Thread {

		private final Intent serviceIntent;
		
		private Runnable runAfterOnPreExecute;
		private InetifyService serviceToTest;
		
		public AsyncTaskServiceTestThread(final Intent serviceIntent) {
			this.serviceIntent = serviceIntent;
		}
		
		public void run() {
			Looper.prepare();
			
			startService(serviceIntent);
			
			serviceToTest = getService();

			if(runAfterOnPreExecute != null) {
				runAfterOnPreExecute.run();
			}
		    
		    Looper.loop();
		}
		
	}

}
