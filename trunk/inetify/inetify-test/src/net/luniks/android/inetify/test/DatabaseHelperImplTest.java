package net.luniks.android.inetify.test;

import java.util.Map;

import net.luniks.android.inetify.DatabaseHelperImpl;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;

public class DatabaseHelperImplTest extends AndroidTestCase {
	
	public void setUp() throws Exception {
		super.setUp();
		this.getContext().deleteDatabase("inetifydb");
		this.getContext().deleteDatabase("inetifydb-journal");
	}
	
	public void testExistsDatabase() {
		
		DatabaseHelperImpl helper = new DatabaseHelperImpl(this.getContext());
		
		assertFalse(helper.exists());
		
	}
	
	public void testCreateDatabase() {
		
		DatabaseHelperImpl helper = new DatabaseHelperImpl(this.getContext());
		
		SQLiteDatabase database = helper.getReadableDatabase();
		
		assertTrue(helper.exists());
		assertEquals(1, database.getVersion());
		
		Cursor cursor = database.rawQuery("SELECT * FROM ignorelist", new String[0]);
		
		assertEquals(2, cursor.getColumnCount());
		assertEquals("mac", cursor.getColumnName(0));
		assertEquals("ssid", cursor.getColumnName(1));
		
		database.close();
		
	}
	
	public void testAddIgnoredWifi() {
		
		DatabaseHelperImpl helper = new DatabaseHelperImpl(this.getContext());
		
		assertTrue(helper.addIgnoredWifi("00:21:29:A2:48:80", "Celsten"));
		assertTrue(helper.addIgnoredWifi("00:11:22:33:44:55", "TestSSID1"));
		assertTrue(helper.addIgnoredWifi("00:66:77:88:99:00", "TestSSID2"));
		assertFalse(helper.addIgnoredWifi(null, null));
		
		SQLiteDatabase database = helper.getReadableDatabase();
		
		assertTrue(helper.exists());
		assertEquals(1, database.getVersion());
		
		Cursor cursor = database.rawQuery("SELECT * FROM ignorelist", new String[0]);
		
		assertEquals(3, cursor.getCount());
		assertTrue(cursor.moveToNext());
		assertEquals("00:21:29:A2:48:80", cursor.getString(0));
		assertEquals("Celsten", cursor.getString(1));
		assertTrue(cursor.moveToNext());
		assertEquals("00:11:22:33:44:55", cursor.getString(0));
		assertEquals("TestSSID1", cursor.getString(1));
		assertTrue(cursor.moveToNext());
		assertEquals("00:66:77:88:99:00", cursor.getString(0));
		assertEquals("TestSSID2", cursor.getString(1));
		assertFalse(cursor.moveToNext());
		
		database.close();
		
	}
	
	public void testAddIgnoredWifiTwice() {
		
		DatabaseHelperImpl helper = new DatabaseHelperImpl(this.getContext());
		
		assertTrue(helper.addIgnoredWifi("00:66:77:88:99:00", "TestSSID2"));
		assertFalse(helper.addIgnoredWifi("00:66:77:88:99:00", "TestSSID2"));
		
		SQLiteDatabase database = helper.getReadableDatabase();
		
		assertTrue(helper.exists());
		assertEquals(1, database.getVersion());
		
		Cursor cursor = database.rawQuery("SELECT * FROM ignorelist", new String[0]);
		
		assertEquals(1, cursor.getCount());
		assertTrue(cursor.moveToNext());
		assertEquals("00:66:77:88:99:00", cursor.getString(0));
		assertEquals("TestSSID2", cursor.getString(1));
		assertFalse(cursor.moveToNext());
		
		database.close();
		
	}
	
	public void testIsIgnoredWifi() {
		
		DatabaseHelperImpl helper = new DatabaseHelperImpl(this.getContext());
		
		SQLiteDatabase database = helper.getWritableDatabase();
		
		assertTrue(helper.exists());
		assertEquals(1, database.getVersion());
		
		insertTestWifis(database);
		
		database.close();
		
		assertTrue(helper.isIgnoredWifi("00:21:29:A2:48:80"));
		assertTrue(helper.isIgnoredWifi("00:11:22:33:44:55"));
		assertTrue(helper.isIgnoredWifi("00:66:77:88:99:00"));
		
		assertFalse(helper.isIgnoredWifi("xx:xx:xx:xx:xx:xx"));
		assertFalse(helper.isIgnoredWifi(null));
	}
	
	public void testDeleteIgnoredWifi() {
		
		DatabaseHelperImpl helper = new DatabaseHelperImpl(this.getContext());
		
		SQLiteDatabase database = helper.getWritableDatabase();
		
		assertTrue(helper.exists());
		assertEquals(1, database.getVersion());
		
		insertTestWifis(database);
		
		database.close();
		
		assertTrue(helper.deleteIgnoredWifi("00:11:22:33:44:55"));
		assertFalse(helper.deleteIgnoredWifi("xx:xx:xx:xx:xx:xx"));
		assertFalse(helper.deleteIgnoredWifi(null));
		
		database = helper.getReadableDatabase();
		
		assertTrue(helper.exists());
		assertEquals(1, database.getVersion());
		
		Cursor cursor = database.rawQuery("SELECT * FROM ignorelist", new String[0]);
		
		assertEquals(2, cursor.getCount());
		assertTrue(cursor.moveToNext());
		assertEquals("00:21:29:A2:48:80", cursor.getString(0));
		assertEquals("Celsten", cursor.getString(1));
		assertTrue(cursor.moveToNext());
		assertEquals("00:66:77:88:99:00", cursor.getString(0));
		assertEquals("TestSSID2", cursor.getString(1));
		assertFalse(cursor.moveToNext());
		
		database.close();
		
	}
	
	public void testListIgnoredWifis() {
		
		DatabaseHelperImpl helper = new DatabaseHelperImpl(this.getContext());
		
		SQLiteDatabase database = helper.getWritableDatabase();
		
		assertTrue(helper.exists());
		assertEquals(1, database.getVersion());
		
		insertTestWifis(database);
		
		database.close();
		
		Map<String, String> list = helper.listIgnoredWifis();
		
		assertTrue(list.containsKey("00:21:29:A2:48:80"));
		assertEquals("Celsten", list.get("00:21:29:A2:48:80"));
		assertTrue(list.containsKey("00:11:22:33:44:55"));
		assertEquals("TestSSID1", list.get("00:11:22:33:44:55"));
		assertTrue(list.containsKey("00:66:77:88:99:00"));
		assertEquals("TestSSID2", list.get("00:66:77:88:99:00"));
	}
	
	private void insertTestWifis(final SQLiteDatabase database) {
		ContentValues values = new ContentValues();
		values.put("mac", "00:21:29:A2:48:80");
		values.put("ssid", "Celsten");
		database.insert("ignorelist", null, values);
		values.put("mac", "00:11:22:33:44:55");
		values.put("ssid", "TestSSID1");
		database.insert("ignorelist", null, values);
		values.put("mac", "00:66:77:88:99:00");
		values.put("ssid", "TestSSID2");
		database.insert("ignorelist", null, values);
	}

}
