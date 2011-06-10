package net.luniks.android.inetify.test;

import net.luniks.android.inetify.DatabaseAdapterImpl;
import android.database.Cursor;
import android.test.AndroidTestCase;

public class DatabaseAdapterImplTest extends AndroidTestCase {
	
	public void setUp() throws Exception {
		super.setUp();
		this.getContext().deleteDatabase("inetifydb");
		this.getContext().deleteDatabase("inetifydb-journal");
	}
	
	public void testDatabaseNotOpen() {
		DatabaseAdapterImpl helper = new DatabaseAdapterImpl(this.getContext());
		
		assertFalse(helper.isOpen());
	}
	
	public void testAddIgnoredWifiOpensDatabase() {
		DatabaseAdapterImpl helper = new DatabaseAdapterImpl(this.getContext());
		
		helper.addIgnoredWifi("00:21:29:A2:48:80", "Celsten");
		
		assertTrue(helper.isOpen());
		
		helper.close();
		
		assertFalse(helper.isOpen());
	}
	
	public void testIsIgnoredWifiIfOpensDatabase() {
		DatabaseAdapterImpl helper = new DatabaseAdapterImpl(this.getContext());
		
		helper.isIgnoredWifi("00:21:29:A2:48:80");
		
		assertTrue(helper.isOpen());
		
		helper.close();
		
		assertFalse(helper.isOpen());
	}
	
	public void testDeleteIgnoredWifiOpensDatabase() {
		DatabaseAdapterImpl helper = new DatabaseAdapterImpl(this.getContext());
		
		helper.deleteIgnoredWifi("00:21:29:A2:48:80");
		
		assertTrue(helper.isOpen());
		
		helper.close();
		
		assertFalse(helper.isOpen());
	}
	
	public void testFetchIgnoredWifisOpensDatabase() {
		DatabaseAdapterImpl helper = new DatabaseAdapterImpl(this.getContext());
		
		helper.fetchIgnoredWifis();
		
		assertTrue(helper.isOpen());
		
		helper.close();
		
		assertFalse(helper.isOpen());
	}
	
	public void testAddIgnoredWifi() {
		
		DatabaseAdapterImpl helper = new DatabaseAdapterImpl(this.getContext());
		
		assertTrue(helper.addIgnoredWifi("00:21:29:A2:48:80", "Celsten"));
		assertTrue(helper.addIgnoredWifi("00:11:22:33:44:55", "TestSSID1"));
		assertTrue(helper.addIgnoredWifi("00:66:77:88:99:00", "TestSSID2"));
		assertFalse(helper.addIgnoredWifi(null, null));

		Cursor cursor = helper.fetchIgnoredWifis();
		
		assertEquals(3, cursor.getCount());
		assertTrue(cursor.moveToNext());
		assertEquals("00:21:29:A2:48:80", cursor.getString(1));
		assertEquals("Celsten", cursor.getString(2));
		assertTrue(cursor.moveToNext());
		assertEquals("00:11:22:33:44:55", cursor.getString(1));
		assertEquals("TestSSID1", cursor.getString(2));
		assertTrue(cursor.moveToNext());
		assertEquals("00:66:77:88:99:00", cursor.getString(1));
		assertEquals("TestSSID2", cursor.getString(2));
		assertFalse(cursor.moveToNext());
		
		helper.close();
	}
	
	public void testAddIgnoredWifiSameMACOtherSSID() {
		
		DatabaseAdapterImpl helper = new DatabaseAdapterImpl(this.getContext());
		
		assertTrue(helper.addIgnoredWifi("00:66:77:88:99:00", "TestSSID2"));
		assertTrue(helper.addIgnoredWifi("00:66:77:88:99:00", "TestSSID2New"));
		
		Cursor cursor = helper.fetchIgnoredWifis();
		
		assertEquals(1, cursor.getCount());
		assertTrue(cursor.moveToNext());
		assertEquals("00:66:77:88:99:00", cursor.getString(1));
		assertEquals("TestSSID2New", cursor.getString(2));
		assertFalse(cursor.moveToNext());
		
		helper.close();
	}
	
	public void testIsIgnoredWifi() {
		
		DatabaseAdapterImpl helper = new DatabaseAdapterImpl(this.getContext());
		
		insertTestWifis(helper);
		
		assertTrue(helper.isIgnoredWifi("Celsten"));
		assertTrue(helper.isIgnoredWifi("TestSSID1"));
		assertTrue(helper.isIgnoredWifi("TestSSID2"));
		
		assertFalse(helper.isIgnoredWifi("XXX"));
		assertFalse(helper.isIgnoredWifi(null));
		
		helper.close();
	}
	
	public void testDeleteIgnoredWifi() {
		
		DatabaseAdapterImpl helper = new DatabaseAdapterImpl(this.getContext());
		
		insertTestWifis(helper);
		
		assertTrue(helper.deleteIgnoredWifi("00:11:22:33:44:55"));
		assertFalse(helper.deleteIgnoredWifi("xx:xx:xx:xx:xx:xx"));
		assertFalse(helper.deleteIgnoredWifi(null));
		
		Cursor cursor = helper.fetchIgnoredWifis();
		
		assertEquals(2, cursor.getCount());
		assertTrue(cursor.moveToNext());
		assertEquals("00:21:29:A2:48:80", cursor.getString(1));
		assertEquals("Celsten", cursor.getString(2));
		assertTrue(cursor.moveToNext());
		assertEquals("00:66:77:88:99:00", cursor.getString(1));
		assertEquals("TestSSID2", cursor.getString(2));
		assertFalse(cursor.moveToNext());
		
		helper.close();
	}
	
	public void testFetchIgnoredWifis() {
		
		DatabaseAdapterImpl helper = new DatabaseAdapterImpl(this.getContext());
		
		insertTestWifis(helper);
		
		Cursor cursor = helper.fetchIgnoredWifis();
		
		assertEquals(3, cursor.getCount());
		assertTrue(cursor.moveToNext());
		assertEquals("00:21:29:A2:48:80", cursor.getString(1));
		assertEquals("Celsten", cursor.getString(2));
		assertTrue(cursor.moveToNext());
		assertEquals("00:11:22:33:44:55", cursor.getString(1));
		assertEquals("TestSSID1", cursor.getString(2));
		assertTrue(cursor.moveToNext());
		assertEquals("00:66:77:88:99:00", cursor.getString(1));
		assertEquals("TestSSID2", cursor.getString(2));
		assertFalse(cursor.moveToNext());
		
		helper.close();
	}
	
	private void insertTestWifis(final DatabaseAdapterImpl helper) {
		helper.addIgnoredWifi("00:21:29:A2:48:80", "Celsten");
		helper.addIgnoredWifi("00:11:22:33:44:55", "TestSSID1");
		helper.addIgnoredWifi("00:66:77:88:99:00", "TestSSID2");
	}

}
