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

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import net.luniks.android.inetify.TestInfo;
import net.luniks.android.inetify.Tester;
import net.luniks.android.interfaces.IWifiInfo;
import net.luniks.android.test.mock.WifiInfoMock;

public class TestTester implements Tester {
	
	private TestInfo info = null;
	private AtomicBoolean done = new AtomicBoolean(false);
	private AtomicInteger testCount = new AtomicInteger(0);
	private AtomicBoolean cancelled = new AtomicBoolean(false);
	private AtomicInteger cancelCount = new AtomicInteger(0);
	private AtomicBoolean throwException = new AtomicBoolean(false);
	private AtomicBoolean wifiConnected = new AtomicBoolean(true);
	
	public void setInfo(final TestInfo info) {
		this.info = info;
	}
	
	public TestInfo testSimple() {
		testCount.incrementAndGet();
		if(throwException.get()) {
			try {
				throw(new RuntimeException("Tester Exception"));
			} finally {
				throwException.set(false);
			}
		}
		
		return info;
	}

	public TestInfo testWifi(int retries, long delay) {
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
	
	public boolean isWifiConnectedOrConnecting() {
		return wifiConnected.get();
	}
	
	public IWifiInfo getWifiInfo() {
		WifiInfoMock wifiInfo = new WifiInfoMock();
		wifiInfo.setSSID("TesterSSID");
		wifiInfo.setBSSID("TesterBSSID");
		return wifiInfo;
	}
	
	public void setWifiConnected(final boolean connected) {
		this.wifiConnected.set(connected);
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