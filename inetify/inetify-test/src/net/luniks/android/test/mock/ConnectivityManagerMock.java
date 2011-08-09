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
