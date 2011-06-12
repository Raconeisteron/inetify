package net.luniks.android.inetify.test;

import net.luniks.android.inetify.DatabaseAdapter;
import android.app.Activity;
import android.app.Instrumentation.ActivityMonitor;
import android.view.View;
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
	
	public static View selectAndFindListViewChildAt(final Activity activity, final ListView listView, final int position, final long timeout) throws InterruptedException {
		activity.runOnUiThread(new Runnable() {
			public void run() {
				listView.setSelection(position);
			}
		});
		long start = System.currentTimeMillis();
		while(listView.getLastVisiblePosition() < position) {
			Thread.sleep(50);
			long now = System.currentTimeMillis();
			if(now - start > timeout) {
				return null;
			}
		}
		int firstVisiblePosition = listView.getFirstVisiblePosition();
		return listView.getChildAt(position - firstVisiblePosition);
	}

}
