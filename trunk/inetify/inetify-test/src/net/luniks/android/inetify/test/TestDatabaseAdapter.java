package net.luniks.android.inetify.test;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

import net.luniks.android.inetify.DatabaseAdapter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;

public class TestDatabaseAdapter implements DatabaseAdapter {
	
	private final Map<String, String> map = new ConcurrentHashMap<String, String>();
	private final AtomicBoolean isOpen = new AtomicBoolean(false);

	public boolean exists() {
		return true;
	}

	public void onCreate(SQLiteDatabase database) {
	}

	public boolean addIgnoredWifi(String bssid, String ssid) {
		isOpen.set(true);
		map.put(bssid, ssid);
		return true;
	}

	public boolean isIgnoredWifi(String ssid) {
		isOpen.set(true);
		return map.containsValue(ssid);
	}

	public boolean deleteIgnoredWifi(String ssid) {
		isOpen.set(true);
		Iterator<Entry<String, String>> it = map.entrySet().iterator();
		while(it.hasNext()) {
			Entry<String, String> entry = it.next();
			if(entry.getValue().equals(ssid)) {
				it.remove();
			}
		}
		return true;
	}

	// TODO Implement when needed
	public Cursor fetchIgnoredWifis() {
		isOpen.set(true);
		return null;
	}

	public boolean addLocation(String bssid, String ssid, String name, Location location) {
		// TODO Auto-generated method stub
		return false;
	}

	public String findWifi(Location location) {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean deleteLocation(String bssid) {
		// TODO Auto-generated method stub
		return false;
	}
	
	public boolean renameLocation(String bssid, String name) {
		// TODO Auto-generated method stub
		return false;
	}

	public Cursor fetchLocations() {
		// TODO Auto-generated method stub
		return null;
	}

	public int getDatabaseVersion() {
		// TODO Auto-generated method stub
		return 0;
	}
	
	public void close() {
		isOpen.set(false);
	}

	public boolean isOpen() {
		return isOpen.get();
	}

}
