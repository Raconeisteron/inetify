package net.luniks.android.inetify.test;

import net.luniks.android.inetify.DatabaseAdapterImpl;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;

public class DatabaseUpgradeTest extends AndroidTestCase {
	
	/** SQL to create the inital database */
	private static final String IGNORELIST_TABLE_CREATE =
		"CREATE TABLE " + DatabaseAdapterImpl.IGNORELIST_TABLE_NAME + " (" +
		DatabaseAdapterImpl.COLUMN_ROWID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
		DatabaseAdapterImpl.COLUMN_BSSID + " TEXT NOT NULL, " +
		DatabaseAdapterImpl.COLUMN_SSID + " TEXT NOT NULL, " +
		"UNIQUE (" + DatabaseAdapterImpl.COLUMN_BSSID + ") ON CONFLICT REPLACE)";
	
	public void setUp() throws Exception {
		super.setUp();
		this.getContext().deleteDatabase("inetifydb");
		this.getContext().deleteDatabase("inetifydb-journal");
	}
	
	public void testUpdateV1ToV2() {
		
		SQLiteDatabase database = this.getContext().openOrCreateDatabase(DatabaseAdapterImpl.DATABASE_NAME, Context.MODE_PRIVATE, null);
		database.setVersion(1);
		database.execSQL(IGNORELIST_TABLE_CREATE);
		
		DatabaseAdapterImpl adapter = new DatabaseAdapterImpl(this.getContext());
		
		assertEquals(2, adapter.getDatabaseVersion());
		
		assertTrue(tableExists(database, DatabaseAdapterImpl.IGNORELIST_TABLE_NAME));
		assertTrue(tableExists(database, DatabaseAdapterImpl.LOCATIONLIST_TABLE_NAME));
		
		database.close();
		
		adapter.close();
		
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
