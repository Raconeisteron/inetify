package net.luniks.android.interfaces;

/**
 * Interface for a wrapper for WifiManager, to allow mocking.
 * @see android.net.wifi.WifiManager
 * 
 * @author dode@luniks.net
 */
public interface IWifiManager {

	IWifiInfo getConnectionInfo();

}
