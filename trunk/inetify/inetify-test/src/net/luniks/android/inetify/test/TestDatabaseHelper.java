package net.luniks.android.inetify.test;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import net.luniks.android.inetify.DatabaseHelper;
import android.database.sqlite.SQLiteDatabase;

public class TestDatabaseHelper implements DatabaseHelper {
	
	private static final Map<String, String> MAP = new ConcurrentHashMap<String, String>();

	public boolean databaseExists() {
		return true;
	}

	public void onCreate(SQLiteDatabase database) {
	}

	public boolean addIgnoredWifi(String mac, String ssid) {
		if(! MAP.containsKey(mac)) {
			MAP.put(mac, ssid);
			return true;
		}
		return false;
	}

	public boolean isIgnoredWifi(String mac) {
		return MAP.containsKey(mac);
	}

	public boolean deleteIgnoredWifi(String mac) {
		MAP.remove(mac);
		return true;
	}

	public Map<String, String> listIgnoredWifis() {
		return MAP;
	}

}
