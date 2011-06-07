package net.luniks.android.inetify;

import java.util.Map;

import android.database.sqlite.SQLiteDatabase;

public interface DatabaseHelper {
	
	boolean databaseExists();
	
	void onCreate(SQLiteDatabase database);

	boolean addIgnoredWifi(String mac, String ssid);
	
	boolean isIgnoredWifi(String mac);
	
	boolean deleteIgnoredWifi(String mac);
	
	Map<String, String> listIgnoredWifis();
	
}
