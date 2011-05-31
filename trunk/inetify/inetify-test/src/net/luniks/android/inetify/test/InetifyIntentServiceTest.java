package net.luniks.android.inetify.test;


import net.luniks.android.inetify.ConnectivityActionReceiver;
import net.luniks.android.inetify.InetifyIntentService;
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
		serviceIntent.putExtra(ConnectivityActionReceiver.EXTRA_IS_WIFI_CONNECTED, true);
		
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
		serviceIntent.putExtra(ConnectivityActionReceiver.EXTRA_IS_WIFI_CONNECTED, true);
		
		this.setupService();
		InetifyIntentService serviceToTest = getService();
		
		TestTester tester = new TestTester();
		serviceToTest.setTester(tester);
		
		this.startService(serviceIntent);
		
		TestUtils.waitForTestCount(tester, 1, 1000);
		
		// Service should call Tester.test()
		assertEquals(1, tester.testCount());
		
		// Service should stop itself when the test is done 
		tester.done();
		
		assertFalse(this.getService().stopService(serviceIntent));
		
	}
	
	public void testWifiNotConnected() throws InterruptedException {
		
		Intent serviceIntent = new Intent("net.luniks.android.inetify.InetifyTestService");
		serviceIntent.putExtra(ConnectivityActionReceiver.EXTRA_IS_WIFI_CONNECTED, false);
		
		this.setupService();
		InetifyIntentService serviceToTest = getService();
		
		TestTester tester = new TestTester();
		serviceToTest.setTester(tester);
		
		this.startService(serviceIntent);
		
		// When Wifi is not connected, the service should just skip the test, cancel notifications and stop itself
		assertEquals(0, tester.testCount());
		
		assertFalse(this.getService().stopService(serviceIntent));
		
	}
	
	public void testTestThrowsException() throws InterruptedException {
		
		Intent serviceIntent = new Intent("net.luniks.android.inetify.InetifyTestService");
		serviceIntent.putExtra(ConnectivityActionReceiver.EXTRA_IS_WIFI_CONNECTED, true);
		
		this.setupService();
		InetifyIntentService serviceToTest = getService();
		
		TestTester tester = new TestTester();
		serviceToTest.setTester(tester);
		
		this.startService(serviceIntent);
		
		TestUtils.waitForTestCount(tester, 1, 1000);
		
		// Service should call Tester.test()
		assertEquals(1, tester.testCount());
		
		// Service should stop itself when the test threw an exception
		tester.throwException();
		
		assertFalse(this.getService().stopService(serviceIntent));
		
	}
	
	public void testDestroyed() throws InterruptedException {
		
		Intent serviceIntent = new Intent("net.luniks.android.inetify.InetifyTestService");
		serviceIntent.putExtra(ConnectivityActionReceiver.EXTRA_IS_WIFI_CONNECTED, true);
		
		this.setupService();
		InetifyIntentService serviceToTest = getService();
		
		TestTester tester = new TestTester();
		serviceToTest.setTester(tester);
		
		this.startService(serviceIntent);
		
		TestUtils.waitForTestCount(tester, 1, 1000);
		
		// Service should call Tester.test()
		assertEquals(1, tester.testCount());
		
		// The service should cancel the test when it is killed
		shutdownService();
		
		assertTrue(tester.cancelled());
		
		assertFalse(this.getService().stopService(serviceIntent));
		
	}
	
	public void testCancelWhileBusyAndStartNext() throws InterruptedException {
		
		Intent serviceIntent = new Intent("net.luniks.android.inetify.InetifyTestService");
		serviceIntent.putExtra(ConnectivityActionReceiver.EXTRA_IS_WIFI_CONNECTED, true);
		
		this.setupService();
		InetifyIntentService serviceToTest = getService();
		
		TestTester tester = new TestTester();
		serviceToTest.setTester(tester);
		
		this.startService(serviceIntent);
		
		TestUtils.waitForTestCount(tester, 1, 1000);
		
		// Service should call Tester.test()
		assertEquals(1, tester.testCount());
		
		// Fails assertion
		// startService(serviceIntent);
		
		// Does not cause onStart or onStartService to be called
		// this.getContext().startService(serviceIntent);
		
		// Only way to call onStartCommand()?
		// Queue up a second task
		serviceToTest.onStartCommand(serviceIntent, 0, 0);
		
		TestUtils.waitForTestCount(tester, 2, 1000);
		
		// First task should have been cancelled
		assertEquals(1, tester.cancelCount());
		
		// The second task should have been started
		assertEquals(2, tester.testCount());
		
		// Let the second task complete
		tester.done();
		
		assertFalse(this.getService().stopService(serviceIntent));
		
	}

}
