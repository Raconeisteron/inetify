package net.luniks.android.inetify.test;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;

import net.luniks.android.inetify.DatabaseAdapter;
import android.app.Activity;
import android.app.Instrumentation.ActivityMonitor;
import android.content.Context;
import android.location.Location;
import android.net.NetworkInfo;
import android.view.View;
import android.widget.ListView;

public class TestUtils {
	
	public static void waitForHitCount(final ActivityMonitor monitor, final int expectedCount, final long timeout) throws InterruptedException {
		long start = System.currentTimeMillis();
		while(monitor.getHits() != expectedCount) {
			Thread.sleep(50);
			long now = System.currentTimeMillis();
			if(now - start > timeout) {
				throw new InterruptedException(String.format("Timeout exceeded while waiting for a hit count of %s", expectedCount));
			}
		}
	}
	
	public static void waitForTestCount(final TestTester tester, final int expectedCount, final long timeout) throws InterruptedException {
		long start = System.currentTimeMillis();
		while(tester.testCount() != expectedCount) {
			Thread.sleep(50);
			long now = System.currentTimeMillis();
			if(now - start > timeout) {
				throw new InterruptedException(String.format("Timeout exceeded while waiting for a test count of %s", expectedCount));
			}
		}
	}
	
	public static void waitForItemCount(final ListView listView, final int expectedCount, final long timeout) throws InterruptedException {
		long start = System.currentTimeMillis();
		while(listView.getAdapter().getCount() != expectedCount) {
			Thread.sleep(50);
			long now = System.currentTimeMillis();
			if(now - start > timeout) {
				throw new InterruptedException(String.format("Timeout exceeded while waiting for an item count of %s", expectedCount));
			}
		}
	}

	public static void waitForIgnoredWifi(final DatabaseAdapter databaseAdapter, final String bssid, final long timeout) throws InterruptedException {
		long start = System.currentTimeMillis();
		while(! databaseAdapter.isIgnoredWifi(bssid)) {
			Thread.sleep(50);
			long now = System.currentTimeMillis();
			if(now - start > timeout) {
				throw new InterruptedException(String.format("Timeout exceeded while waiting for ignored Wifi %s", bssid));
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
		View child = null;
		while(listView.getLastVisiblePosition() < position || child == null) {
			Thread.sleep(50);
			int firstVisiblePosition = listView.getFirstVisiblePosition();
			child = listView.getChildAt(position - firstVisiblePosition);
			long now = System.currentTimeMillis();
			if(now - start > timeout) {
				return null;
			}
		}
		return child;
	}
	
	public static Location createLocation(final double latitude, final double longitude, final float accuracy) {
		Location location = new Location("TestProvider");
		location.setLatitude(latitude);
		location.setLongitude(longitude);
		location.setAccuracy(accuracy);
		return location;
	}
	
	public static NetworkInfo createNetworkInfo(final Context context, final int type, final boolean connected) throws Exception {
		Constructor<NetworkInfo> ctor = NetworkInfo.class.getDeclaredConstructor(int.class);
		ctor.setAccessible(true);
		NetworkInfo networkInfo = ctor.newInstance(0);
		Field typeField = NetworkInfo.class.getDeclaredField("mNetworkType");
		Field connectedField = NetworkInfo.class.getDeclaredField("mState");
		Field detailedStateField = NetworkInfo.class.getDeclaredField("mDetailedState");
		typeField.setAccessible(true);
		connectedField.setAccessible(true);
		detailedStateField.setAccessible(true);
		typeField.setInt(networkInfo, type);
		connectedField.set(networkInfo, connected == true ? NetworkInfo.State.CONNECTED : NetworkInfo.State.DISCONNECTED);
		detailedStateField.set(networkInfo, connected == true ? NetworkInfo.DetailedState.CONNECTED : NetworkInfo.DetailedState.DISCONNECTED);
		return networkInfo;
	}

}
