package net.luniks.android.impl;

import net.luniks.android.interfaces.IConnectivityManager;
import net.luniks.android.interfaces.INetworkInfo;
import android.net.ConnectivityManager;

/**
 * Implementation of IConnectivityManager.
 * @see android.net.ConnectivityManager
 * 
 * @author torsten.roemer@luniks.net
 */
public class ConnectivityManagerImpl implements IConnectivityManager {
	
	private final ConnectivityManager connectivityManager;
	
	public ConnectivityManagerImpl(final ConnectivityManager connectivityManager) {
		this.connectivityManager = connectivityManager;
	}

	/**
	 * Returns the wrapped active NetworkInfo from the wrapped ConnectivityManager,
	 * null if it was null.
	 */
	public INetworkInfo getActiveNetworkInfo() {
		return NetworkInfoImpl.getInstance(connectivityManager.getActiveNetworkInfo());
	}

}
