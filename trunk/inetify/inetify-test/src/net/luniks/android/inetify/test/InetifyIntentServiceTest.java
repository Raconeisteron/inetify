package net.luniks.android.inetify.test;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import net.luniks.android.inetify.ConnectivityActionReceiver;
import net.luniks.android.inetify.InetifyIntentService;
import net.luniks.android.inetify.TestInfo;
import net.luniks.android.inetify.Tester;
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
	
	public void testNullIntent() throws InterruptedException {
		
		this.setupService();
		InetifyIntentService serviceToTest = getService();
		
		TestTester tester = new TestTester();
		serviceToTest.setTester(tester);
		
		startService(null);
		
		// When receiving a null intent, the service should ignore it and virtually immediately become not busy
		assertEquals(0, tester.testCount());
		
		waitForServiceBusy(serviceToTest, false, 1000);
		
		assertFalse(serviceToTest.isBusy());
		
	}

	public void testWifiNotConnected() throws InterruptedException {
		
		boolean wifiConnected = false;
		
		Intent serviceIntent = new Intent("net.luniks.android.inetify.InetifyTestService");
		serviceIntent.putExtra(ConnectivityActionReceiver.EXTRA_IS_WIFI_CONNECTED, wifiConnected);
		
		this.setupService();
		InetifyIntentService serviceToTest = getService();
		
		TestTester tester = new TestTester();
		serviceToTest.setTester(tester);
		
		startService(serviceIntent);
		
		// When Wifi is not connected, the service should do nothing and virtually immediately become not busy
		assertEquals(0, tester.testCount());
		
		waitForServiceBusy(serviceToTest, false, 1000);
		
		assertFalse(serviceToTest.isBusy());
		
	}
	
	public void testWifiConnected() throws InterruptedException {
		
		boolean wifiConnected = true;
		
		Intent serviceIntent = new Intent("net.luniks.android.inetify.InetifyTestService");
		serviceIntent.putExtra(ConnectivityActionReceiver.EXTRA_IS_WIFI_CONNECTED, wifiConnected);
		
		this.setupService();
		InetifyIntentService serviceToTest = getService();
		
		TestTester tester = new TestTester();
		serviceToTest.setTester(tester);
		
		startService(serviceIntent);
		
		// Wait for the service's background thread to start working
		waitForServiceBusy(serviceToTest, true, 3000);
		
		// When Wifi is connected, the service should test internet connectivity using the tester
		assertEquals(1, tester.testCount());
		assertTrue(serviceToTest.isBusy());
		
		// Simulate that the tester is done testing
		tester.done();
		
		waitForServiceBusy(serviceToTest, false, 1000);
		
		assertFalse(serviceToTest.isBusy());
		
	}
	
	public void testDestroyed() throws InterruptedException {

		boolean wifiConnected = true;
		
		Intent serviceIntent = new Intent("net.luniks.android.inetify.InetifyTestService");
		serviceIntent.putExtra(ConnectivityActionReceiver.EXTRA_IS_WIFI_CONNECTED, wifiConnected);
		
		this.setupService();
		InetifyIntentService serviceToTest = getService();
		
		TestTester tester = new TestTester();
		serviceToTest.setTester(tester);
		
		startService(serviceIntent);
		
		// Wait for the service's background thread to start working
		waitForServiceBusy(serviceToTest, true, 3000);
		
		// When Wifi is connected, the service should test internet connectivity using the tester
		assertEquals(1, tester.testCount());
		assertTrue(serviceToTest.isBusy());
		
		// Simulate that the service is killed
		shutdownService();
		
		assertTrue(tester.cancelled());
		
		waitForServiceBusy(serviceToTest, false, 1000);
		
		assertFalse(serviceToTest.isBusy());
		
	}
	
	public void testIgnoreWhileBusy() throws InterruptedException {

		boolean wifiConnected = true;
		
		Intent serviceIntent = new Intent("net.luniks.android.inetify.InetifyTestService");
		serviceIntent.putExtra(ConnectivityActionReceiver.EXTRA_IS_WIFI_CONNECTED, wifiConnected);
		
		this.setupService();
		InetifyIntentService serviceToTest = getService();
		
		TestTester tester = new TestTester();
		serviceToTest.setTester(tester);
		
		startService(serviceIntent);
		
		// Wait for the service's background thread to start working
		waitForServiceBusy(serviceToTest, true, 3000);
		
		// When Wifi is connected, the service should test internet connectivity using the tester
		assertEquals(1, tester.testCount());
		assertTrue(serviceToTest.isBusy());
		
		// Fails assertion
		// startService(serviceIntent);
		
		// Does not cause onStart or onStartService to be called
		// this.getContext().startService(serviceIntent);
		
		// Only way to call onStartCommand()?
		// Queue up a second task
		serviceToTest.onStartCommand(serviceIntent, 0, 0);
		
		// First task is still busy
		assertEquals(1, tester.testCount());
		
		// Let the first task complete
		tester.done();
		
		// The second task should have been dropped
		assertEquals(1, tester.testCount());
		
		waitForServiceBusy(serviceToTest, false, 1000);
		
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
	
	private class TestTester implements Tester {
		
		private TestInfo info = null;
		private AtomicBoolean done = new AtomicBoolean(false);
		private AtomicInteger testCount = new AtomicInteger(0);
		private AtomicBoolean cancelled = new AtomicBoolean(false);
		
		public void setInfo(final TestInfo info) {
			this.info = info;
		}

		public TestInfo test(int retries, long delay, boolean wifiOnly) {
			done.set(false);
			testCount.incrementAndGet();
			cancelled.set(false);
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
		
		public int testCount() {
			return testCount.get();
		}
		
		public boolean cancelled() {
			return cancelled.get();
		}
		
	}

}
