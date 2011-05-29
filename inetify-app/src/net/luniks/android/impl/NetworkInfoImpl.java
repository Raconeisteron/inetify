package net.luniks.android.impl;

import net.luniks.android.interfaces.INetworkInfo;
import android.net.NetworkInfo;

/**
 * Implementation of INetworkInfo.
 * @see android.net.NetworkInfo
 * 
 * @author dode@luniks.net
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

	public boolean isConnected() {
		return networkInfo.isConnected();
	}
	
	public NetworkInfo getNetworkInfo() {
		return networkInfo;
	}

}
