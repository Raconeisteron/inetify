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
package net.luniks.android.impl;

import net.luniks.android.interfaces.INetworkInfo;
import android.net.NetworkInfo;

/**
 * Implementation of INetworkInfo.
 * @see android.net.NetworkInfo
 * 
 * @author torsten.roemer@luniks.net
 */
public class NetworkInfoImpl implements INetworkInfo {
	
	private final NetworkInfo networkInfo;

	private NetworkInfoImpl(NetworkInfo networkInfo) {
		this.networkInfo = networkInfo;
	}
	
	public static NetworkInfoImpl getInstance(final NetworkInfo networkInfo) {
		if(networkInfo == null) {
			return null;
		}
		return new NetworkInfoImpl(networkInfo);
	}

	public String getTypeName() {
		return networkInfo.getTypeName();
	}

	public int getType() {
		return networkInfo.getType();
	}

	public String getSubtypeName() {
		return networkInfo.getSubtypeName();
	}

	public boolean isConnectedOrConnecting() {
		return networkInfo.isConnectedOrConnecting();
	}
	
	public NetworkInfo getNetworkInfo() {
		return networkInfo;
	}

}
