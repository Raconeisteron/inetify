package net.luniks.android.interfaces;

/**
 * Interface for a wrapper for ConnectivityManager, to allow mocking.
 * @see android.net.ConnectivityManager
 * 
 * @author dode@luniks.net
 */
public interface IConnectivityManager {

	INetworkInfo getActiveNetworkInfo();

}
