package net.luniks.android.inetify;

public interface InetifyTester {

	/**
	 * Gets network and Wifi info and tests if the internet site in the settings has
	 * the expected title and returns and instance of TestInfo. Aborts testing and
	 * returns null if onlyWifi is true and Wifi disconnects during testing.
	 * @param retries number of test retries
	 * @param delay before each test attempt in milliseconds
	 * @param wifiOnly abort test if Wifi is not connected
	 * @return instance of TestInfo containing the test results
	 */
	TestInfo test(final int retries, final long delay, final boolean wifiOnly);
	
	/**
	 * Cancels an ongoing test
	 */
	void cancel();

	/**
	 * Returns true if there currently is a Wifi connection, false otherwise.
	 * @return boolean true if Wifi is connected, false otherwise
	 */
	boolean isWifiConnected();

}