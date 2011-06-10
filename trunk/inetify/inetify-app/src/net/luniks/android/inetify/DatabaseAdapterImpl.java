package net.luniks.android.inetify;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseAdapterImpl implements DatabaseAdapter {

	public static final String IGNORELIST_COLUMN_ROWID = "_id";
	public static final String IGNORELIST_COLUMN_BSSID = "bssid";
	public static final String IGNORELIST_COLUMN_SSID = "ssid";
	
	private static final String DATABASE_NAME = "inetifydb";
	private static final int DATABASE_VERSION = 1;
	private static final String IGNORELIST_TABLE_NAME = "ignorelist";
	private static final String IGNORELIST_TABLE_CREATE =
		"CREATE TABLE " + IGNORELIST_TABLE_NAME + " (" +
		IGNORELIST_COLUMN_ROWID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
		IGNORELIST_COLUMN_BSSID + " TEXT NOT NULL, " +
		IGNORELIST_COLUMN_SSID + " TEXT NOT NULL, " +
		"UNIQUE (" + IGNORELIST_COLUMN_BSSID + ") ON CONFLICT REPLACE)";
	// private static final String IGNORELIST_TABLE_DROP =
	// 	"DROP TABLE IF EXISTS " + IGNORELIST_TABLE_NAME;
	
	private final DatabaseOpenHelper helper;
	
	private SQLiteDatabase database;
	
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
	
	public DatabaseAdapterImpl(final Context context) {
		this.helper = new DatabaseOpenHelper(context);
	}
	
	public void close() {
		helper.close();
	}
	
    public boolean isOpen() {
    	if(database == null) {
    		return false;
    	}
    	return database.isOpen();
    }

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
	
	public boolean isIgnoredWifi(final String ssid) {
		if(ssid == null) {
			return false;
		}

		openIfNeeded();
		
		Cursor cursor = null;
		try {
			database = helper.getReadableDatabase();
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

	public boolean deleteIgnoredWifi(final String bssid) {
		if(bssid == null) {
			return false;
		}
		
		openIfNeeded();
		
		String[] whereArgs = {bssid};
		int rows = database.delete(IGNORELIST_TABLE_NAME, 
				IGNORELIST_COLUMN_BSSID + " = ?", whereArgs);
		return rows > 0;
	}
	
    public Cursor fetchIgnoredWifis() {
		
    	openIfNeeded();

        return database.query(IGNORELIST_TABLE_NAME, 
        		new String[] {IGNORELIST_COLUMN_ROWID, IGNORELIST_COLUMN_BSSID, IGNORELIST_COLUMN_SSID}, 
        		null, null, null, null, null);
    }
    
    private void openIfNeeded() {
    	if(database == null || ! database.isOpen()) {
    		database = helper.getWritableDatabase();
    	}
    }

}
