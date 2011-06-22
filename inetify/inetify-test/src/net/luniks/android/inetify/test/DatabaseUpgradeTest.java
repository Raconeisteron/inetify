package net.luniks.android.inetify.test;

import net.luniks.android.inetify.DatabaseAdapterImpl;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;

public class DatabaseUpgradeTest extends AndroidTestCase {
	
	private static final String IGNORELIST_TABLE_CREATE_V1 =
		"CREATE TABLE " + DatabaseAdapterImpl.IGNORELIST_TABLE_NAME + " (" +
		DatabaseAdapterImpl.IGNORELIST_COLUMN_ROWID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
		DatabaseAdapterImpl.IGNORELIST_COLUMN_BSSID + " TEXT NOT NULL, " +
		DatabaseAdapterImpl.IGNORELIST_COLUMN_SSID + " TEXT NOT NULL, " +
		"UNIQUE (" + DatabaseAdapterImpl.IGNORELIST_COLUMN_BSSID + ") ON CONFLICT REPLACE)";
	
	public void setUp() throws Exception {
		super.setUp();
		this.getContext().deleteDatabase("inetifydb");
		this.getContext().deleteDatabase("inetifydb-journal");
	}
	
	public void testUpdateV1ToV2() {
		
		SQLiteDatabase database = this.getContext().openOrCreateDatabase(DatabaseAdapterImpl.DATABASE_NAME, Context.MODE_PRIVATE, null);
		database.setVersion(1);
		database.execSQL(IGNORELIST_TABLE_CREATE_V1);
		
		insertTestWifi(database, "TestBSSID1", "TestSSID1");
		insertTestWifi(database, "TestBSSID2", "TestSSID2");
		insertTestWifi(database, "TestBSSID3", "TestSSID3");
		
		DatabaseAdapterImpl adapter = new DatabaseAdapterImpl(this.getContext());
		
		assertEquals(2, adapter.getDatabaseVersion());
		
		Cursor cursor = adapter.fetchIgnoredWifis();
		
		assertEquals(3, cursor.getCount());
		assertTrue(cursor.moveToNext());
		assertEquals("TestBSSID1", cursor.getString(1));
		assertEquals("TestSSID1", cursor.getString(2));
		assertTrue(cursor.moveToNext());
		assertEquals("TestBSSID2", cursor.getString(1));
		assertEquals("TestSSID2", cursor.getString(2));
		assertTrue(cursor.moveToNext());
		assertEquals("TestBSSID3", cursor.getString(1));
		assertEquals("TestSSID3", cursor.getString(2));
		assertFalse(cursor.moveToNext());
		
		assertTrue(tableExists(database, DatabaseAdapterImpl.IGNORELIST_TABLE_NAME));
		assertFalse(tableExists(database, DatabaseAdapterImpl.IGNORELIST_TABLE_NAME_UPGRADE));
		
		database.close();
		
		adapter.close();
		
	}
	
	private void insertTestWifi(final SQLiteDatabase database, final String bssid, final String ssid) {
		ContentValues values = new ContentValues();
		values.put(DatabaseAdapterImpl.IGNORELIST_COLUMN_BSSID, bssid);
		values.put(DatabaseAdapterImpl.IGNORELIST_COLUMN_SSID, ssid);
		database.insert(DatabaseAdapterImpl.IGNORELIST_TABLE_NAME, null, values);
	}
	
	private boolean tableExists(final SQLiteDatabase database, final String table) {
		String[] args = new String[] {table};
		Cursor cursor = database.rawQuery("SELECT COUNT(*) FROM sqlite_master WHERE type = 'table' AND name = ?", args);
		cursor.moveToNext();
		boolean exists = cursor.getInt(0) == 1;
		cursor.close();
		return exists;
	}

}
