package net.luniks.android.inetify;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Implementation of DatabaseAdapter using a SQLite database.
 * What is really used here to identify a Wifi network is the SSID, since
 * there can be many APs (with different BSSID's) participating in the same
 * network using the same SSID (ESSID).
 * The BSSID is just used for information and to replace an entry in case
 * the SSID of an AP was changed (which will probably never apply...)
 * 
 * @author torsten.roemer@luniks.net
 */
public class DatabaseAdapterImpl implements DatabaseAdapter {

	/** Id for SimpleCursorAdapter */
	public static final String IGNORELIST_COLUMN_ROWID = "_id";
	
	/** BSSID of an ignored Wifi network */
	public static final String IGNORELIST_COLUMN_BSSID = "bssid";
	
	/** SSID of an ignored Wifi network */
	public static final String IGNORELIST_COLUMN_SSID = "ssid";
	
	/** Database name */
	private static final String DATABASE_NAME = "inetifydb";
	
	/** Database version */
	private static final int DATABASE_VERSION = 1;
	
	/** Table used for the ignore list */
	private static final String IGNORELIST_TABLE_NAME = "ignorelist";
	
	/** SQL to create the inital database */
	private static final String IGNORELIST_TABLE_CREATE =
		"CREATE TABLE " + IGNORELIST_TABLE_NAME + " (" +
		IGNORELIST_COLUMN_ROWID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
		IGNORELIST_COLUMN_BSSID + " TEXT NOT NULL, " +
		IGNORELIST_COLUMN_SSID + " TEXT NOT NULL, " +
		"UNIQUE (" + IGNORELIST_COLUMN_BSSID + ") ON CONFLICT REPLACE)";
	// private static final String IGNORELIST_TABLE_DROP =
	// 	"DROP TABLE IF EXISTS " + IGNORELIST_TABLE_NAME;
	
	/** Extended DatabaseOpenHelper */
	private final DatabaseOpenHelper helper;
	
	/** The SQLite database */
	private SQLiteDatabase database;
	
	/**
	 * Minimal implementation of DatabaseOpenHelper, onUpgrade() currently not implemented!
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
		}
	
		@Override
		public void onUpgrade(final SQLiteDatabase database, final int oldVersion, final int newVersion) {
			// TODO Implement when needed
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
		values.put(IGNORELIST_COLUMN_BSSID, bssid);
		values.put(IGNORELIST_COLUMN_SSID, ssid);
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
			String[] columns = {IGNORELIST_COLUMN_BSSID};
			String[] selectionArgs = {ssid};
			cursor = database.query(IGNORELIST_TABLE_NAME, columns, 
					IGNORELIST_COLUMN_SSID + " = ?", selectionArgs, null, null, null);
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
				IGNORELIST_COLUMN_SSID + " = ?", whereArgs);
		return rows > 0;
	}
	
	/**
	 * Returns a cursor to all ignored Wifi networks in the database.
	 * @return Cursor all ignored Wifi networks
	 */
    public Cursor fetchIgnoredWifis() {
		
    	openIfNeeded();

        return database.query(IGNORELIST_TABLE_NAME, 
        		new String[] {IGNORELIST_COLUMN_ROWID, IGNORELIST_COLUMN_BSSID, IGNORELIST_COLUMN_SSID}, 
        		null, null, null, null, null);
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
