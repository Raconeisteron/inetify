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
package net.luniks.android.test.mock;

import net.luniks.android.interfaces.IConnectivityManager;
import net.luniks.android.interfaces.INetworkInfo;

public class ConnectivityManagerMock implements IConnectivityManager {
	
	private INetworkInfo networkInfo;
	
	public ConnectivityManagerMock(final INetworkInfo networkInfo) {
		this.networkInfo = networkInfo;
	}
	
	public INetworkInfo getNetworkInfo() {
		return networkInfo;
	}

	public void setNetworkInfo(final INetworkInfo networkInfo) {
		this.networkInfo = networkInfo;
	}

	public INetworkInfo getActiveNetworkInfo() {
		return networkInfo;
	}

}
