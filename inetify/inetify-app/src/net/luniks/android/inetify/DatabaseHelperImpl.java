package net.luniks.android.inetify;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelperImpl extends SQLiteOpenHelper implements DatabaseHelper {

	private static final String DATABASE_NAME = "inetifydb";
	private static final int DATABASE_VERSION = 1;
	private static final String IGNORELIST_TABLE_NAME = "ignorelist";
	private static final String IGNORELIST_COLUMN_MAC = "mac";
	private static final String IGNORELIST_COLUMN_SSID = "ssid";
	private static final String IGNORELIST_TABLE_CREATE =
		"CREATE TABLE " + IGNORELIST_TABLE_NAME + " (" +
		IGNORELIST_COLUMN_MAC + " TEXT NOT NULL PRIMARY KEY, " +
		IGNORELIST_COLUMN_SSID + " TEXT NOT NULL);";

	private final Context context;

	public DatabaseHelperImpl(final Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		this.context = context;
	}

	@Override
	public void onCreate(final SQLiteDatabase database) {
		database.execSQL(IGNORELIST_TABLE_CREATE);
	}

	@Override
	public void onUpgrade(final SQLiteDatabase database, final int oldVersion, final int newVersion) {
		// TODO Auto-generated method stub
	}
	
	public boolean databaseExists() {
		String[] databaseList = context.databaseList();
		for(String database : databaseList) {
			if(database.startsWith(DATABASE_NAME)) {
				return true;
			}
		}
		return false;
	}

	public boolean addIgnoredWifi(final String mac, final String ssid) {
		if(mac == null || ssid == null) {
			return false;
		}
		SQLiteDatabase database = null;
		try {
			database = this.getWritableDatabase();
			ContentValues values = new ContentValues();
			values.put(IGNORELIST_COLUMN_MAC, mac);
			values.put(IGNORELIST_COLUMN_SSID, ssid);
			long rowId = database.insert(IGNORELIST_TABLE_NAME, null, values);
			return rowId == -1 ? false : true;
		} finally {
			if(database != null) {
				database.close();
			}
		}
		
	}
	
	public boolean isIgnoredWifi(final String mac) {
		if(! databaseExists()) {
			return false;
		}
		if(mac == null) {
			return false;
		}
		SQLiteDatabase database = null;
		Cursor cursor = null;
		try {
			database = this.getReadableDatabase();
			String[] columns = {IGNORELIST_COLUMN_MAC};
			String[] selectionArgs = {mac};
			cursor = database.query(IGNORELIST_TABLE_NAME, columns, IGNORELIST_COLUMN_MAC + " = ?", selectionArgs, null, null, null);
			return cursor.getCount() > 0;
		} finally {
			if(cursor != null) {
				cursor.close();
			}
			if(database != null) {
				database.close();
			}
		}
	}

	public boolean deleteIgnoredWifi(final String mac) {
		if(! databaseExists()) {
			return false;
		}
		if(mac == null) {
			return false;
		}
		SQLiteDatabase database = null;
		try {
			database = this.getWritableDatabase();
			String[] whereArgs = {mac};
			int rows = database.delete(IGNORELIST_TABLE_NAME, IGNORELIST_COLUMN_MAC + " = ?", whereArgs);
			return rows > 0;
		} finally {
			if(database != null) {
				database.close();
			}
		}
	}

	public Map<String, String> listIgnoredWifis() {
		if(! databaseExists()) {
			return Collections.emptyMap();
		}
		SQLiteDatabase database = null;
		Cursor cursor = null;
		Map<String, String> list = new HashMap<String, String>();
		try {
			database = this.getReadableDatabase();
			String[] columns = {IGNORELIST_COLUMN_MAC, IGNORELIST_COLUMN_SSID};
			cursor = database.query(IGNORELIST_TABLE_NAME, columns, null, null, null, null, IGNORELIST_COLUMN_SSID);
			while(cursor.moveToNext()) {
				String mac = cursor.getString(0);
				String ssid = cursor.getString(1);
				list.put(mac, ssid);
			}
			return list;
		} finally {
			if(cursor != null) {
				cursor.close();
			}
			if(database != null) {
				database.close();
			}
		}		
	}

}
