package net.luniks.android.inetify.test;

import java.util.concurrent.atomic.AtomicBoolean;

import net.luniks.android.inetify.ConnectivityActionReceiver;
import net.luniks.android.inetify.InetifyIntentService;
import net.luniks.android.inetify.InetifyTester;
import net.luniks.android.inetify.TestInfo;
import android.content.Intent;
import android.test.ServiceTestCase;

public class InetifyIntentServiceTest extends ServiceTestCase<InetifyIntentService> {

	public InetifyIntentServiceTest() {
		super(InetifyIntentService.class);
	}
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
	}

	public void testWifiNotConnected() throws InterruptedException {
		
		boolean wifiConnected = false;
		
		Intent serviceIntent = new Intent("net.luniks.android.inetify.InetifyTestService");
		serviceIntent.putExtra(ConnectivityActionReceiver.EXTRA_IS_WIFI_CONNECTED, wifiConnected);
		
		this.setupService();
		InetifyIntentService serviceToTest = getService();
		
		TestInetifyTester tester = new TestInetifyTester();
		serviceToTest.setTester(tester);
		
		startService(serviceIntent);
		
		// When Wifi is not connected, the service should do nothing and virtually immediately become not busy
		assertFalse(tester.testCalled());
		assertFalse(serviceToTest.isBusy());
		
	}
	
	public void testWifiConnected() throws InterruptedException {
		
		boolean wifiConnected = true;
		
		Intent serviceIntent = new Intent("net.luniks.android.inetify.InetifyTestService");
		serviceIntent.putExtra(ConnectivityActionReceiver.EXTRA_IS_WIFI_CONNECTED, wifiConnected);
		
		this.setupService();
		InetifyIntentService serviceToTest = getService();
		
		TestInetifyTester tester = new TestInetifyTester();
		serviceToTest.setTester(tester);
		
		startService(serviceIntent);
		
		// Wait for the service's background thread to start working
		waitForServiceBusy(serviceToTest, true, 3000);
		
		// When Wifi is connected, the service should test internet connectivity using the tester
		assertTrue(tester.testCalled());
		assertTrue(serviceToTest.isBusy());
		
		// Simulate that the tester is done testing
		tester.done();
		
		assertFalse(serviceToTest.isBusy());
		
	}
	
	public void testDestroyed() throws InterruptedException {

		boolean wifiConnected = true;
		
		Intent serviceIntent = new Intent("net.luniks.android.inetify.InetifyTestService");
		serviceIntent.putExtra(ConnectivityActionReceiver.EXTRA_IS_WIFI_CONNECTED, wifiConnected);
		
		this.setupService();
		InetifyIntentService serviceToTest = getService();
		
		TestInetifyTester tester = new TestInetifyTester();
		serviceToTest.setTester(tester);
		
		startService(serviceIntent);
		
		// Wait for the service's background thread to start working
		waitForServiceBusy(serviceToTest, true, 3000);
		
		// When Wifi is connected, the service should test internet connectivity using the tester
		assertTrue(tester.testCalled());
		assertTrue(serviceToTest.isBusy());
		
		// Simulate that the service is killed
		shutdownService();
		
		assertTrue(tester.cancelled());
		assertFalse(serviceToTest.isBusy());
		
	}
	
	private boolean waitForServiceBusy(final InetifyIntentService service, final boolean expected, final long timeout) throws InterruptedException {
		long start = System.currentTimeMillis();
		while(expected != service.isBusy()) {
			Thread.sleep(50);
			long now = System.currentTimeMillis();
			if(now - start > timeout) {
				return false;
			}
		}
		return true;
	}
	
	private class TestInetifyTester implements InetifyTester {
		
		private TestInfo info = null;
		private AtomicBoolean done = new AtomicBoolean(false);
		private AtomicBoolean testCalled = new AtomicBoolean(false);
		private AtomicBoolean cancelled = new AtomicBoolean(false);
		
		public void setInfo(final TestInfo info) {
			this.info = info;
		}

		public TestInfo test(int retries, long delay, boolean wifiOnly) {
			testCalled.set(true);
			while(! done.get() && ! cancelled.get()) {
				try {
					Thread.sleep(50);
				} catch (InterruptedException e) {
					// Ignore
				}
			}
			
			return info;
		}

		public void cancel() {
			cancelled.set(true);
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				// Ignore
			}
		}

		public boolean isWifiConnected() {
			return false;
		}
		
		public void done() {
			this.done.set(true);
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				// Ignore
			}
		}
		
		public boolean testCalled() {
			return testCalled.get();
		}
		
		public boolean cancelled() {
			return cancelled.get();
		}
		
	}

}
