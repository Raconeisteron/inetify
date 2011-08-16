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
package net.luniks.android.inetify;

import net.luniks.android.interfaces.IWifiInfo;

/**
 * Interface for a class to testing internet connectivity.
 * 
 * @author torsten.roemer@luniks.net
 */
public interface Tester {

	/**
	 * Constructs a tester instance using the given Context, IConnectivityManager, IWifiManager and TitleVerifier.
	 * @param context
	 * @param connectivityManager
	 * @param wifiManager
	 * @param titleVerifier
	 */
	TestInfo testSimple();

	/**
	 * Gets network and Wifi info and tests if the internet site in the settings has
	 * the expected title and returns and instance of TestInfo. Aborts testing and
	 * returns null if Wifi disconnects during testing.
	 * @param retries number of test retries
	 * @param delay before each test attempt in milliseconds
	 * @return instance of TestInfo containing the test results
	 */
	TestInfo testWifi(final int retries, final long delay);
	
	/**
	 * Cancels an ongoing test.
	 */
	void cancel();

	/**
	 * Returns true if there currently is a Wifi connection/connecting, false otherwise.
	 * @return boolean true if Wifi is connected or connecting, false otherwise
	 */
	boolean isWifiConnectedOrConnecting();

	/**
	 * Returns the current WifiInfo.
	 * @return IWifiInfo 
	 */
	IWifiInfo getWifiInfo();

}