package net.luniks.android.interfaces;

/**
 * Interface for a wrapper for WifiInfo, to allow mocking.
 * @see android.net.wifi.WifiInfo
 * 
 * @author dode@luniks.net
 */
public interface IWifiInfo {

	String getSSID();

}
