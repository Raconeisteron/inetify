package net.luniks.android.inetify.test;

import net.luniks.android.inetify.DatabaseAdapter;
import android.app.Instrumentation.ActivityMonitor;
import android.widget.ListView;

public class TestUtils {
	
	public static void waitForHitCount(final ActivityMonitor monitor, final int expectedCount, final long timeout) throws InterruptedException {
		long start = System.currentTimeMillis();
		while(monitor.getHits() != expectedCount) {
			Thread.sleep(50);
			long now = System.currentTimeMillis();
			if(now - start > timeout) {
				return;
			}
		}
	}
	
	public static void waitForTestCount(final TestTester tester, final int expectedCount, final long timeout) throws InterruptedException {
		long start = System.currentTimeMillis();
		while(tester.testCount() != expectedCount) {
			Thread.sleep(50);
			long now = System.currentTimeMillis();
			if(now - start > timeout) {
				return;
			}
		}
	}
	
	public static void waitForChildCount(final ListView listView, final int expectedCount, final long timeout) throws InterruptedException {
		long start = System.currentTimeMillis();
		while(listView.getChildCount() != expectedCount) {
			Thread.sleep(50);
			long now = System.currentTimeMillis();
			if(now - start > timeout) {
				return;
			}
		}
	}

	public static void waitForIgnoredWifi(final DatabaseAdapter databaseAdapter, final String bssid, final long timeout) throws InterruptedException {
		long start = System.currentTimeMillis();
		while(! databaseAdapter.isIgnoredWifi(bssid)) {
			Thread.sleep(50);
			long now = System.currentTimeMillis();
			if(now - start > timeout) {
				return;
			}
		}
	}

}
