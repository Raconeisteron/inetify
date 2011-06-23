package net.luniks.android.interfaces;

/**
 * Interface for a wrapper for ConnectivityManager, to allow mocking.
 * @see android.net.ConnectivityManager
 * 
 * @author torsten.roemer@luniks.net
 */
public interface IConnectivityManager {

	INetworkInfo getActiveNetworkInfo();

}
