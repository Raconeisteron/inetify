package net.luniks.android.impl;

import net.luniks.android.interfaces.IConnectivityManager;
import net.luniks.android.interfaces.INetworkInfo;
import android.net.ConnectivityManager;

/**
 * Implementation of IConnectivityManager.
 * @see android.net.ConnectivityManager
 * 
 * @author dode@luniks.net
 */
public class ConnectivityManagerImpl implements IConnectivityManager {
	
	private final ConnectivityManager connectivityManager;
	
	public ConnectivityManagerImpl(final ConnectivityManager connectivityManager) {
		this.connectivityManager = connectivityManager;
	}

	public INetworkInfo getActiveNetworkInfo() {
		return new NetworkInfoImpl(connectivityManager.getActiveNetworkInfo());
	}

}
