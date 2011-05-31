package net.luniks.android.inetify.test;

import android.app.Instrumentation.ActivityMonitor;

public class TestUtils {
	
	public static void waitForHitCount(final ActivityMonitor monitor, final int expectedCount, final long timeout) throws InterruptedException {
		long start = System.currentTimeMillis();
		while(monitor.getHits() < expectedCount) {
			Thread.sleep(50);
			long now = System.currentTimeMillis();
			if(now - start > timeout) {
				return;
			}
		}
	}
	
	public static void waitForTestCount(final TestTester tester, final int expectedCount, final long timeout) throws InterruptedException {
		long start = System.currentTimeMillis();
		while(tester.testCount() < expectedCount) {
			Thread.sleep(50);
			long now = System.currentTimeMillis();
			if(now - start > timeout) {
				return;
			}
		}
	}

}
