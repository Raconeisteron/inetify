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
		
		Intent serviceIntent = new Intent("net.luniks.android.inetify.InetifyTestService");
		
		this.setupService();
		InetifyIntentService serviceToTest = getService();
		
		TestTester tester = new TestTester();
		serviceToTest.setTester(tester);
		
		this.startService(null);
		
		// When receiving a null intent, the service should ignore it and stop itself
		assertEquals(0, tester.testCount());
		
		assertFalse(this.getService().stopService(serviceIntent));
		
	}

	public void testNotNullIntent() throws InterruptedException {
		
		Intent serviceIntent = new Intent("net.luniks.android.inetify.InetifyTestService");
		
		this.setupService();
		InetifyIntentService serviceToTest = getService();
		
		TestTester tester = new TestTester();
		serviceToTest.setTester(tester);
		
		this.startService(serviceIntent);
		
		waitForTestCount(tester, 1, 1000);
		
		// Service should call Tester.test()
		assertEquals(1, tester.testCount());
		
		// Service should stop itself when the test is done 
		tester.done();
		
		assertFalse(this.getService().stopService(serviceIntent));
		
	}
	
	public void testTestThrowsException() throws InterruptedException {
		
		boolean wifiConnected = true;
		
		Intent serviceIntent = new Intent("net.luniks.android.inetify.InetifyTestService");
		serviceIntent.putExtra(ConnectivityActionReceiver.EXTRA_IS_WIFI_CONNECTED, wifiConnected);
		
		this.setupService();
		InetifyIntentService serviceToTest = getService();
		
		TestTester tester = new TestTester();
		serviceToTest.setTester(tester);
		
		this.startService(serviceIntent);
		
		waitForTestCount(tester, 1, 1000);
		
		// Service should call Tester.test()
		assertEquals(1, tester.testCount());
		
		// Service should stop itself when the test threw an exception
		tester.throwException();
		
		assertFalse(this.getService().stopService(serviceIntent));
		
	}
	
	public void testDestroyed() throws InterruptedException {
		
		Intent serviceIntent = new Intent("net.luniks.android.inetify.InetifyTestService");
		
		this.setupService();
		InetifyIntentService serviceToTest = getService();
		
		TestTester tester = new TestTester();
		serviceToTest.setTester(tester);
		
		this.startService(serviceIntent);
		
		waitForTestCount(tester, 1, 1000);
		
		// Service should call Tester.test()
		assertEquals(1, tester.testCount());
		
		// The service should cancel the test when it is killed
		shutdownService();
		
		assertTrue(tester.cancelled());
		
		assertFalse(this.getService().stopService(serviceIntent));
		
	}
	
	public void testCancelWhileBusyAndStartNext() throws InterruptedException {
		
		Intent serviceIntent = new Intent("net.luniks.android.inetify.InetifyTestService");
		
		this.setupService();
		InetifyIntentService serviceToTest = getService();
		
		TestTester tester = new TestTester();
		serviceToTest.setTester(tester);
		
		this.startService(serviceIntent);
		
		waitForTestCount(tester, 1, 1000);
		
		// Service should call Tester.test()
		assertEquals(1, tester.testCount());
		
		// Fails assertion
		// startService(serviceIntent);
		
		// Does not cause onStart or onStartService to be called
		// this.getContext().startService(serviceIntent);
		
		// Only way to call onStartCommand()?
		// Queue up a second task
		serviceToTest.onStartCommand(serviceIntent, 0, 0);
		
		waitForTestCount(tester, 2, 1000);
		
		// First task should have been cancelled
		assertEquals(1, tester.cancelCount());
		
		// The second task should have been started
		assertEquals(2, tester.testCount());
		
		// Let the second task complete
		tester.done();
		
		assertFalse(this.getService().stopService(serviceIntent));
		
	}
	
	private boolean waitForTestCount(final TestTester tester, final int expectedCount, final long timeout) throws InterruptedException {
		long start = System.currentTimeMillis();
		while(tester.testCount() < expectedCount) {
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
		private AtomicInteger cancelCount = new AtomicInteger(0);
		private AtomicBoolean throwException = new AtomicBoolean(false);
		
		public void setInfo(final TestInfo info) {
			this.info = info;
		}

		public TestInfo test(int retries, long delay, boolean wifiOnly) {
			done.set(false);
			testCount.incrementAndGet();
			cancelled.set(false);
			while(! done.get() && ! cancelled.get()) {
				if(throwException.get()) {
					try {
						throw(new RuntimeException("Tester Exception"));
					} finally {
						throwException.set(false);
					}
				}
				try {
					Thread.sleep(50);
				} catch (InterruptedException e) {
					// Ignore
				}
			}
			
			return info;
		}
		
		public boolean isWifiConnected() {
			return false;
		}

		public void cancel() {
			cancelled.set(true);
			cancelCount.incrementAndGet();
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				// Ignore
			}
		}
		
		public void done() {
			this.done.set(true);
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				// Ignore
			}
		}
		
		public void throwException() {
			throwException.set(true);
		}
		
		public int testCount() {
			return testCount.get();
		}
		
		public boolean cancelled() {
			return cancelled.get();
		}
		
		public int cancelCount() {
			return cancelCount.get();
		}
		
	}

}
