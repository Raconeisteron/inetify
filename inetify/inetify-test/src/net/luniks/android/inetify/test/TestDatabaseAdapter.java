package net.luniks.android.inetify.test;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import net.luniks.android.inetify.DatabaseAdapter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class TestDatabaseAdapter implements DatabaseAdapter {
	
	private final Map<String, String> map = new ConcurrentHashMap<String, String>();

	public boolean exists() {
		return true;
	}

	public void onCreate(SQLiteDatabase database) {
	}

	public boolean addIgnoredWifi(String bssid, String ssid) {
		map.put(bssid, ssid);
		return true;
	}

	public boolean isIgnoredWifi(String ssid) {
		return map.containsValue(ssid);
	}

	public boolean deleteIgnoredWifi(String bssid) {
		map.remove(bssid);
		return true;
	}

	public Map<String, String> listIgnoredWifis() {
		return map;
	}

	public Cursor fetchIgnoredWifis() {
		// TODO Auto-generated method stub
		return null;
	}

}
