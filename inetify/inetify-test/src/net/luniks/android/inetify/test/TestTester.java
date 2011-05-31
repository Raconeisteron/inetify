package net.luniks.android.inetify.test;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import net.luniks.android.inetify.TestInfo;
import net.luniks.android.inetify.Tester;

class TestTester implements Tester {
	
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