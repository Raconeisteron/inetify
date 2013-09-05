/*
 * Copyright 2011 Torsten Römer
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.luniks.android.inetify;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.location.Location;

/**
 * Implementation of DatabaseAdapter using a SQLite database.
 * For the ignorelist, the SSID is used to identify a Wifi network, 
 * since there can be many APs (with different BSSID's) participating
 * in the same network using the same SSID (ESSID).
 * The BSSID is just used for information and to replace an entry in case
 * the SSID of an AP was changed (which will probably never apply...)
 * For the locationlist, the BSSID is used to identify a Wifi network, 
 * since the same SSID can exist at different locations, like
 * commercial hotspots.
 * 
 * @author torsten.roemer@luniks.net
 */
public class DatabaseAdapterImpl implements DatabaseAdapter {

	/** Id for SimpleCursorAdapter */
	public static final String COLUMN_ROWID = "_id";
	
	/** BSSID of a Wifi network */
	public static final String COLUMN_BSSID = "bssid";
	
	/** SSID of a Wifi network */
	public static final String COLUMN_SSID = "ssid";
	
	/** Display name of a Wifi network */
	public static final String COLUMN_NAME = "name";
	
	/** Latitude of a location */
	public static final String COLUMN_LAT = "lat";
	
	/** Longitude of a location */
	public static final String COLUMN_LON = "lon";
	
	/** Accuracy of a location */
	public static final String COLUMN_ACC = "acc";
	
	/** Status of the connection */
	public static final String COLUMN_TIMESTAMP = "timestamp";
	
	/** Type of a connection */
	public static final String COLUMN_TYPE = "type";
	
	/** Subtype of a connection */
	public static final String COLUMN_SUBTYPE = "subtype";
	
	/** Status of the connection */
	public static final String COLUMN_STATUS = "status";
	
	/** Table used for the ignore list */
	public static final String IGNORELIST_TABLE_NAME = "ignorelist";
	
	/** Table used for the location list */
	public static final String LOCATIONLIST_TABLE_NAME = "locationlist";
	
	/** Table used for the test results */
	public static final String TESTRESULTS_TABLE_NAME = "testresults";
	
	/** Database name */
	public static final String DATABASE_NAME = "inetifydb";
	
	/** Max length of a name */
	private static final int NAME_MAX_LENGTH = 32;
	
	/** Database version */
	private static final int DATABASE_VERSION = 3;
	
	/** SQL to create the inital database */
	private static final String IGNORELIST_TABLE_CREATE =
		"CREATE TABLE " + IGNORELIST_TABLE_NAME + " (" +
		COLUMN_ROWID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
		COLUMN_BSSID + " TEXT NOT NULL, " +
		COLUMN_SSID + " TEXT NOT NULL, " +
		"UNIQUE (" + COLUMN_BSSID + ") ON CONFLICT REPLACE)";
	private static final String LOCATIONLIST_TABLE_CREATE =
		"CREATE TABLE " + LOCATIONLIST_TABLE_NAME + " (" +
		COLUMN_ROWID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
		COLUMN_BSSID + " TEXT NOT NULL, " +
		COLUMN_SSID + " TEXT NOT NULL, " +
		COLUMN_NAME + " TEXT NOT NULL, " +
		COLUMN_LAT + " NUMBER NOT NULL, " +
		COLUMN_LON + " NUMBER NOT NULL, " +
		COLUMN_ACC + " NUMBER NOT NULL, " +
		"UNIQUE (" + COLUMN_BSSID + ") ON CONFLICT REPLACE)";
	private static final String TESTRESULTS_TABLE_CREATE =
		"CREATE TABLE " + TESTRESULTS_TABLE_NAME + " (" +
		COLUMN_ROWID + " INTEGER PRIMARY KEY, " +
		COLUMN_TIMESTAMP + " LONG, " +
		COLUMN_TYPE + " INTEGER, " +
		COLUMN_SUBTYPE + " TEXT, " +
		COLUMN_STATUS + " INTEGER)";
	
	/** Extended DatabaseOpenHelper */
	private final DatabaseOpenHelper helper;
	
	/** The SQLite database */
	private SQLiteDatabase database;
	
	/**
	 * Implementation of DatabaseOpenHelper.
	 * 
	 * @author torsten.roemer@luniks.net
	 */
	private static class DatabaseOpenHelper extends SQLiteOpenHelper {

		public DatabaseOpenHelper(final Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}
	
		@Override
		public void onCreate(final SQLiteDatabase database) {
			database.execSQL(IGNORELIST_TABLE_CREATE);
			database.execSQL(LOCATIONLIST_TABLE_CREATE);
			database.execSQL(TESTRESULTS_TABLE_CREATE);
		}
	
		@Override
		public void onUpgrade(final SQLiteDatabase database, final int oldVersion, final int newVersion) {
			if(oldVersion < 2 && newVersion >= 2) {
				database.beginTransaction();
				try {
					database.execSQL(LOCATIONLIST_TABLE_CREATE);
					database.setTransactionSuccessful();
				} finally {
					database.endTransaction();
				}
			}
			
			if(oldVersion < 3 && newVersion >= 3) {
				database.beginTransaction();
				try {
					database.execSQL(TESTRESULTS_TABLE_CREATE);
					database.setTransactionSuccessful();
				} finally {
					database.endTransaction();
				}
			}
		}
	}
	
	/**
	 * Initializes the adapter with the given context. 
	 * @param context
	 */
	public DatabaseAdapterImpl(final Context context) {
		this.helper = new DatabaseOpenHelper(context);
	}
	
	/**
	 * Effectively closes the database.
	 */
	public void close() {
		helper.close();
	}
	
	/**
	 * Returns true if the database is open, false otherwise.
	 * @return boolean true if the database is open, false otherwise
	 */
    public boolean isOpen() {
    	if(database == null) {
    		return false;
    	}
    	return database.isOpen();
    }

	/**
	 * Adds the given BSSID and SSID as ignored Wifi network to the database.
	 * If an entry with the same BSSID exists it will be replaced.
	 * @param bssid
	 * @param ssid
	 * @return boolean true if successfully added, false otherwise
	 */
	public boolean addIgnoredWifi(final String bssid, final String ssid) {
		if(bssid == null || ssid == null) {
			return false;
		}
		
		openIfNeeded();
		
		ContentValues values = new ContentValues();
		values.put(COLUMN_BSSID, bssid);
		values.put(COLUMN_SSID, ssid);
		long rowId = database.insert(IGNORELIST_TABLE_NAME, null, values);
		return rowId == -1 ? false : true;
	}

	/**
	 * Returns true if the given SSID is an ignored Wifi network, false otherwise. 
	 * @param ssid
	 * @return boolean true if ignored, false otherwise
	 */
	public boolean isIgnoredWifi(final String ssid) {
		if(ssid == null) {
			return false;
		}

		openIfNeeded();
		
		Cursor cursor = null;
		try {
			String[] columns = {COLUMN_BSSID};
			String[] selectionArgs = {ssid};
			cursor = database.query(IGNORELIST_TABLE_NAME, columns, 
					COLUMN_SSID + " = ?", selectionArgs, null, null, null);
			return cursor.getCount() > 0;
		} finally {
			if(cursor != null) {
				cursor.close();
			}
		}
	}

	/**
	 * Deletes the entrie(s) matching the given SSID as ignored Wifi networks from the database.
	 * @param ssid
	 * @return boolean true if one or more entries deleted, false otherwise
	 */
	public boolean deleteIgnoredWifi(final String ssid) {
		if(ssid == null) {
			return false;
		}
		
		openIfNeeded();
		
		String[] whereArgs = {ssid};
		int rows = database.delete(IGNORELIST_TABLE_NAME, 
				COLUMN_SSID + " = ?", whereArgs);
		return rows > 0;
	}
	
	/**
	 * Returns a cursor to all ignored Wifi networks in the database.
	 * @return Cursor all ignored Wifi networks
	 */
    public Cursor fetchIgnoredWifis() {
		
    	openIfNeeded();

        return database.query(IGNORELIST_TABLE_NAME, 
        		new String[] {COLUMN_ROWID, COLUMN_BSSID, COLUMN_SSID}, 
        		null, null, null, null, COLUMN_SSID + " COLLATE UNICODE");
    }
	
    /**
     * Adds the given Wifi identified by the given BSSID together with its SSID and location
     * to the database, and updates lat, lon and acc if a location with the given BSSID already
     * exists.
     * @param bssid
     * @param ssid
     * @param location
     * @return boolean true if successfully added, false otherwise
     */
	public boolean addLocation(final String bssid, final String ssid, final String name, final Location location) {
		if(bssid == null || ssid == null || location == null) {
			return false;
		}
		
		String localName = name;
		if(name == null || name.length() == 0) {
			localName = ssid;
		}
		
		openIfNeeded();
		
		String[] whereArgs = {bssid};
		ContentValues values = new ContentValues();
		values.put(COLUMN_LAT, location.getLatitude());
		values.put(COLUMN_LON, location.getLongitude());
		values.put(COLUMN_ACC, location.getAccuracy());
		int rows = database.update(LOCATIONLIST_TABLE_NAME, values, 
				COLUMN_BSSID + " = ?", whereArgs);
		
		if(rows == 0) {
			values.put(COLUMN_BSSID, bssid);
			values.put(COLUMN_SSID, ssid);
			values.put(COLUMN_NAME, localName);
			long rowId = database.insert(LOCATIONLIST_TABLE_NAME, null, values);
			return rowId == -1 ? false : true;
		} else {
			return true;
		}
	}

	/**
	 * Deletes the location of the Wifi identified by the given BSSID.
	 * @param bssid
	 * @return boolean true if one or more entries deleted, false otherwise
	 */
	public boolean deleteLocation(final String bssid) {
		if(bssid == null) {
			return false;
		}
		
		openIfNeeded();
		
		String[] whereArgs = {bssid};
		int rows = database.delete(LOCATIONLIST_TABLE_NAME, 
				COLUMN_BSSID + " = ?", whereArgs);
		return rows > 0;
	}
	
	/**
	 * Renames the location of the Wifi identified by the given BSSID
	 * to the given name, if it is not null or empty. The name is truncated
	 * to 32 characters.
	 * @param bssid
	 * @param name
	 * @return boolean true if renamed, false otherwise
	 */
	public boolean renameLocation(final String bssid, final String name) {
		if(name == null || name.length() == 0) {
			return false;
		}
		String localName = name.substring(0, Math.min(NAME_MAX_LENGTH, name.length()));
		
		openIfNeeded();
		
		String[] whereArgs = {bssid};
		ContentValues values = new ContentValues();
		values.put(COLUMN_NAME, localName);
		int rows = database.update(LOCATIONLIST_TABLE_NAME, values, 
				COLUMN_BSSID + " = ?", whereArgs);
		
		return rows > 0;
	}

	/**
	 * Returns a cursor to all Wifi locations in the database.
	 * @return Cursor all Wifi locations
	 */
	public Cursor fetchLocations() {
		
    	openIfNeeded();

        return database.query(LOCATIONLIST_TABLE_NAME, 
        		new String[] {COLUMN_ROWID, COLUMN_BSSID, COLUMN_SSID, COLUMN_NAME, COLUMN_LAT, COLUMN_LON, COLUMN_ACC}, 
        		null, null, null, null, COLUMN_NAME + " COLLATE UNICODE");
	}
	
	/**
	 * Returns true if there is at least one Wifi location in the database,
	 * false otherwise.
	 * @return boolean
	 */
	public boolean hasLocations() {
		
		openIfNeeded();
		
		boolean hasLocations = false;
		
		Cursor cursor = database.query(LOCATIONLIST_TABLE_NAME, 
        		new String[] {COLUMN_ROWID}, 
        		null, null, null, null, null);
		
		try {
			hasLocations = cursor.getCount() > 0;
		} finally {
			cursor.close();
		}
		
		return hasLocations;
	}
	
	/**
	 * Returns the location that is nearest to the given location as
	 * a WifiLocation including the distance to the given location.
	 * @return WifiLocation
	 */
	public WifiLocation getNearestLocationTo(final Location location) {
		
		openIfNeeded();
		
		Cursor cursor = database.query(LOCATIONLIST_TABLE_NAME, 
        		new String[] {COLUMN_ROWID, COLUMN_BSSID, COLUMN_SSID, COLUMN_NAME, COLUMN_LAT, COLUMN_LON, COLUMN_ACC}, 
        		null, null, null, null, null);
		
		WifiLocation nearestWifiLocation = null;
		float shortestDistance = Float.MAX_VALUE;
		
		try {
			while(cursor.moveToNext()) {
				WifiLocation currentWifiLocation = new WifiLocation();
				currentWifiLocation.setBSSID(cursor.getString(1));
				currentWifiLocation.setSSID(cursor.getString(2));
				currentWifiLocation.setName(cursor.getString(3));
				
				Location currentLocation = new Location(Locater.PROVIDER_DATABASE);
				currentLocation.setLatitude(cursor.getDouble(4));
				currentLocation.setLongitude(cursor.getDouble(5));
				currentLocation.setAccuracy(cursor.getFloat(6));
				
				currentWifiLocation.setLocation(currentLocation);
				
				float distance = currentLocation.distanceTo(location);
				if(distance < shortestDistance) {
					shortestDistance = distance;
					nearestWifiLocation = currentWifiLocation;
					nearestWifiLocation.setDistance(distance);
				}
			}
		} finally {
			cursor.close();
		}
		
		return nearestWifiLocation;
	}
	
	public boolean updateTestResult(final long timestamp, final int type, final String subtype, final boolean status) {
		
		openIfNeeded();
		
		final String sql = String.format("INSERT OR REPLACE INTO %s " +
										 "(%s, %s, %s, %s, %s) VALUES (?, ?, ?, ?, ?)", 
										 TESTRESULTS_TABLE_NAME, COLUMN_ROWID, COLUMN_TIMESTAMP, 
										 COLUMN_TYPE, COLUMN_SUBTYPE, COLUMN_STATUS);
		final SQLiteStatement stmt = database.compileStatement(sql);
		stmt.bindLong(1, 0L);
		stmt.bindLong(2, timestamp);
		stmt.bindLong(3, type);
		if (subtype != null) {
			stmt.bindString(4, subtype);
		}
		stmt.bindLong(5, status ? 1 : 0);
		
		try {
			final long rowId = stmt.executeInsert();
			return rowId == -1 ? false : true;
		} catch(SQLException e) {
			return false;
		} finally {
			stmt.close();
		}
	}

	public TestInfo fetchTestResult() {
		
		openIfNeeded();
		
		final Cursor cursor = database.query(TESTRESULTS_TABLE_NAME, 
        		new String[] {COLUMN_TIMESTAMP, COLUMN_TYPE, COLUMN_SUBTYPE, COLUMN_STATUS}, 
        		null, null, null, null, null);
		
		if (! cursor.moveToNext()) {
			return null;
		}
		
		final TestInfo info = new TestInfo();
		info.setTimestamp(cursor.getLong(0));
		info.setType(cursor.getInt(1));
		info.setExtra(cursor.getString(2));
		info.setIsExpectedTitle(cursor.getInt(3) > 0 ? true : false);
		
		cursor.close();
		
		return info;
	}
    
    /**
     * Returns the version of the database.
     * @return int database version
     */
    public int getDatabaseVersion() {
    	
    	openIfNeeded();
    	
    	return database.getVersion();
    }
    
    /**
     * Opens the database if it is not already open.
     */
    private void openIfNeeded() {
    	if(database == null || ! database.isOpen()) {
    		database = helper.getWritableDatabase();
    	}
    }

}
