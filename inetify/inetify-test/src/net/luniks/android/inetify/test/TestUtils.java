/*
 * Copyright 2011 Torsten Römer
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.luniks.android.inetify.test;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;

import net.luniks.android.inetify.DatabaseAdapter;
import android.app.Activity;
import android.app.Dialog;
import android.app.Instrumentation.ActivityMonitor;
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
	
	public static Dialog waitForCurrentDialogShowing(final Activity activity, final long timeout) throws InterruptedException {
		long start = System.currentTimeMillis();
		try {
			Field currentDialogField = activity.getClass().getDeclaredField("currentDialog");
			currentDialogField.setAccessible(true);
			Dialog dialog = null;
			while(dialog == null || ! dialog.isShowing()) {
				dialog = (Dialog)currentDialogField.get(activity);
				Thread.sleep(50);
				long now = System.currentTimeMillis();
				if(now - start > timeout) {
					throw new InterruptedException(String.format("Timeout exceeded while waiting for dialog showing"));
				}
			}
			return dialog;
		} catch(Exception e) {
			throw new InterruptedException(String.format("Exception while waiting for dialog showing"));
		}
	}
	
	public static Dialog waitForDialogNotShowing(final Dialog dialog, final long timeout) throws InterruptedException {
		long start = System.currentTimeMillis();
		while(dialog.isShowing()) {
			Thread.sleep(50);
			long now = System.currentTimeMillis();
			if(now - start > timeout) {
				throw new InterruptedException(String.format("Timeout exceeded while waiting for dialog not showing"));
			}
		}
		return dialog;
	}
	
	public static void waitForStaticFieldNull(@SuppressWarnings("rawtypes") final Class clazz, final String name, final long timeout) throws Exception {
		long start = System.currentTimeMillis();
		while(! (getStaticFieldValue(clazz, name) == null)) {
			Thread.sleep(50);
			long now = System.currentTimeMillis();
			if(now - start > timeout) {
				throw new InterruptedException(String.format("Timeout exceeded while waiting for field %s to become null", name));
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
	
	public static void performClickOnUIThread(final Activity activity, final View item) {
		Runnable click = new Runnable() {
			public void run() {
				item.performClick();
			}
		};
		activity.runOnUiThread(click);
	}
	
	public static void performLongClickOnUIThread(final Activity activity, final View item) {
		Runnable click = new Runnable() {
			public void run() {
				item.performLongClick();
			}
		};
		activity.runOnUiThread(click);
	}
	
	public static void performItemClickOnUIThread(final Activity activity, final ListView listView, final View view, final int position) {
		Runnable click = new Runnable() {
			public void run() {
				listView.performItemClick(view, position, 0);
			}
		};
		activity.runOnUiThread(click);
	}
	
	public static Location createLocation(final double latitude, final double longitude, final float accuracy) {
		Location location = new Location("TestProvider");
		location.setLatitude(latitude);
		location.setLongitude(longitude);
		location.setAccuracy(accuracy);
		return location;
	}
	
	public static NetworkInfo createNetworkInfo(final int type, final boolean connected) throws Exception {
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
	
	public static void setFieldValue(final Object object, final String name, final Object value) throws Exception {
		Field field = object.getClass().getDeclaredField(name);
		field.setAccessible(true);
		field.set(object, value);
	}
	
	public static Object getFieldValue(final Object object, final String name) throws Exception {
		Field field = object.getClass().getDeclaredField(name);
		field.setAccessible(true);
		return field.get(object);
	}
	
	public static void setStaticFieldValue(@SuppressWarnings("rawtypes") final Class clazz, 
			final String name, final Object value) throws Exception {
		Field field = clazz.getDeclaredField(name);
		field.setAccessible(true);
		// There seems to be no field "modifiers" in Android/DalvikVM?
		// Field modifiersField = Field.class.getDeclaredField("modifiers");
	    // modifiersField.setAccessible(true);
	    // modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);
		field.set(null, value);
	}
	
	public static Object getStaticFieldValue(@SuppressWarnings("rawtypes") final Class clazz, 
			final String name) throws Exception {
		Field field = clazz.getDeclaredField(name);
		field.setAccessible(true);
		return field.get(null);
	}

}
