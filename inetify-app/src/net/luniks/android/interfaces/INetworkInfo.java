package net.luniks.android.interfaces;

/**
 * Interface for a wrapper for NetworkInfo, to allow mocking.
 * @see android.net.NetworkInfo
 * 
 * @author torsten.roemer@luniks.net
 */
public interface INetworkInfo {

	String getTypeName();

	int getType();

	String getSubtypeName();

	boolean isConnected();

}
