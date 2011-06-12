package net.luniks.android.inetify;

import android.database.Cursor;

/**
 * Interface for a database adapter, basically a helper for the SQLite database.
 * 
 * @author torsten.roemer@luniks.net
 */
public interface DatabaseAdapter {

	/**
	 * Adds the given BSSID and SSID as ignored Wifi network to the database.
	 * If an entry with the same BSSID exists it will be replaced.
	 * @param bssid
	 * @param ssid
	 * @return boolean true if successfully added, false otherwise
	 */
	boolean addIgnoredWifi(String bssid, String ssid);
	
	/**
	 * Returns true if the given SSID is an ignored Wifi network, false otherwise. 
	 * @param ssid
	 * @return boolean true if ignored, false otherwise
	 */
	boolean isIgnoredWifi(String ssid);
	
	/**
	 * Deletes the entrie(s) matching the given SSID as ignored Wifi networks from the database.
	 * @param ssid
	 * @return boolean true if one or more entries deleted, false otherwise
	 */
	boolean deleteIgnoredWifi(String ssid);
	
	/**
	 * Returns a cursor to all ignored Wifi networks in the database.
	 * @return Cursor all ignored Wifi networks
	 */
	Cursor fetchIgnoredWifis();
	
	/**
	 * Effectively closes the database.
	 */
	void close();
	
	/**
	 * Returns true if the database is open, false otherwise.
	 * @return boolean true if the database is open, false otherwise
	 */
	boolean isOpen();
	
}
