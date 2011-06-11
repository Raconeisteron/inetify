package net.luniks.android.interfaces;

/**
 * Interface for a wrapper for WifiManager, to allow mocking.
 * @see android.net.wifi.WifiManager
 * 
 * @author torsten.roemer@luniks.net
 */
public interface IWifiManager {

	IWifiInfo getConnectionInfo();

}
