package net.luniks.android.inetify;

import android.database.Cursor;

public interface DatabaseAdapter {

	boolean addIgnoredWifi(String bssid, String ssid);
	
	boolean isIgnoredWifi(String bssid);
	
	boolean deleteIgnoredWifi(String bssid);
	
	Cursor fetchIgnoredWifis();
	
	void close();
	
	boolean isOpen();
	
}
