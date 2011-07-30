package net.luniks.android.inetify.test;

import net.luniks.android.inetify.DatabaseAdapterImpl;
import android.database.Cursor;
import android.test.AndroidTestCase;

public class DatabaseAdapterImplIgnoreListTest extends AndroidTestCase {
	
	public void setUp() throws Exception {
		super.setUp();
		this.getContext().deleteDatabase("inetifydb");
		this.getContext().deleteDatabase("inetifydb-journal");
	}
	
	public void testDatabaseNotOpen() {
		DatabaseAdapterImpl adapter = new DatabaseAdapterImpl(this.getContext());
		
		assertFalse(adapter.isOpen());
	}
	
	public void testAddIgnoredWifiOpensDatabase() {
		DatabaseAdapterImpl adapter = new DatabaseAdapterImpl(this.getContext());
		
		adapter.addIgnoredWifi("00:21:29:A2:48:80", "Celsten");
		
		assertTrue(adapter.isOpen());
		
		adapter.close();
		
		assertFalse(adapter.isOpen());
	}
	
	public void testIsIgnoredWifiIfOpensDatabase() {
		DatabaseAdapterImpl adapter = new DatabaseAdapterImpl(this.getContext());
		
		adapter.isIgnoredWifi("Celsten");
		
		assertTrue(adapter.isOpen());
		
		adapter.close();
		
		assertFalse(adapter.isOpen());
	}
	
	public void testDeleteIgnoredWifiOpensDatabase() {
		DatabaseAdapterImpl adapter = new DatabaseAdapterImpl(this.getContext());
		
		adapter.deleteIgnoredWifi("Celsten");
		
		assertTrue(adapter.isOpen());
		
		adapter.close();
		
		assertFalse(adapter.isOpen());
	}
	
	public void testFetchIgnoredWifisOpensDatabase() {
		DatabaseAdapterImpl adapter = new DatabaseAdapterImpl(this.getContext());
		
		adapter.fetchIgnoredWifis();
		
		assertTrue(adapter.isOpen());
		
		adapter.close();
		
		assertFalse(adapter.isOpen());
	}
	
	public void testAddIgnoredWifi() {
		
		DatabaseAdapterImpl adapter = new DatabaseAdapterImpl(this.getContext());
		
		assertTrue(adapter.addIgnoredWifi("00:21:29:A2:48:80", "Celsten"));
		assertTrue(adapter.addIgnoredWifi("00:11:22:33:44:55", "TestSSID1"));
		assertTrue(adapter.addIgnoredWifi("00:66:77:88:99:00", "TestSSID2"));
		assertFalse(adapter.addIgnoredWifi(null, null));

		Cursor cursor = adapter.fetchIgnoredWifis();
		
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
		
		cursor.close();
		adapter.close();
	}
	
	public void testAddIgnoredWifiSameBSSIDOtherSSID() {
		
		DatabaseAdapterImpl adapter = new DatabaseAdapterImpl(this.getContext());
		
		assertTrue(adapter.addIgnoredWifi("00:66:77:88:99:00", "TestSSID2"));
		assertTrue(adapter.addIgnoredWifi("00:66:77:88:99:00", "TestSSID2New"));
		
		Cursor cursor = adapter.fetchIgnoredWifis();
		
		assertEquals(1, cursor.getCount());
		assertTrue(cursor.moveToNext());
		assertEquals("00:66:77:88:99:00", cursor.getString(1));
		assertEquals("TestSSID2New", cursor.getString(2));
		assertFalse(cursor.moveToNext());
		
		cursor.close();
		adapter.close();
	}
	
	public void testIsIgnoredWifi() {
		
		DatabaseAdapterImpl adapter = new DatabaseAdapterImpl(this.getContext());
		
		insertTestWifis(adapter);
		
		assertTrue(adapter.isIgnoredWifi("Celsten"));
		assertTrue(adapter.isIgnoredWifi("TestSSID1"));
		assertTrue(adapter.isIgnoredWifi("TestSSID2"));
		
		assertFalse(adapter.isIgnoredWifi("XXX"));
		assertFalse(adapter.isIgnoredWifi(null));
		
		adapter.close();
	}
	
	public void testDeleteIgnoredWifi() {
		
		DatabaseAdapterImpl adapter = new DatabaseAdapterImpl(this.getContext());
		
		insertTestWifis(adapter);
		
		assertTrue(adapter.deleteIgnoredWifi("TestSSID1"));
		assertFalse(adapter.deleteIgnoredWifi("XXX"));
		assertFalse(adapter.deleteIgnoredWifi(null));
		
		Cursor cursor = adapter.fetchIgnoredWifis();
		
		assertEquals(2, cursor.getCount());
		assertTrue(cursor.moveToNext());
		assertEquals("00:21:29:A2:48:80", cursor.getString(1));
		assertEquals("Celsten", cursor.getString(2));
		assertTrue(cursor.moveToNext());
		assertEquals("00:66:77:88:99:00", cursor.getString(1));
		assertEquals("TestSSID2", cursor.getString(2));
		assertFalse(cursor.moveToNext());
		
		cursor.close();
		adapter.close();
	}
	
	public void testDeleteIgnoredWifiMultiBSSIDSameSSID() {
		
		DatabaseAdapterImpl adapter = new DatabaseAdapterImpl(this.getContext());
		
		insertTestWifis(adapter);
		adapter.addIgnoredWifi("10:11:22:33:44:55", "TestSSID1");
		adapter.addIgnoredWifi("20:11:22:33:44:55", "TestSSID1");
		
		assertTrue(adapter.deleteIgnoredWifi("TestSSID1"));
		assertFalse(adapter.deleteIgnoredWifi("XXX"));
		assertFalse(adapter.deleteIgnoredWifi(null));
		
		Cursor cursor = adapter.fetchIgnoredWifis();
		
		assertEquals(2, cursor.getCount());
		assertTrue(cursor.moveToNext());
		assertEquals("00:21:29:A2:48:80", cursor.getString(1));
		assertEquals("Celsten", cursor.getString(2));
		assertTrue(cursor.moveToNext());
		assertEquals("00:66:77:88:99:00", cursor.getString(1));
		assertEquals("TestSSID2", cursor.getString(2));
		assertFalse(cursor.moveToNext());
		
		cursor.close();
		adapter.close();
	}
	
	public void testFetchIgnoredWifis() {
		
		DatabaseAdapterImpl adapter = new DatabaseAdapterImpl(this.getContext());
		
		insertTestWifis(adapter);
		
		Cursor cursor = adapter.fetchIgnoredWifis();
		
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
		
		cursor.close();
		adapter.close();
	}
	
	private void insertTestWifis(final DatabaseAdapterImpl databaseAdapter) {
		databaseAdapter.addIgnoredWifi("00:21:29:A2:48:80", "Celsten");
		databaseAdapter.addIgnoredWifi("00:11:22:33:44:55", "TestSSID1");
		databaseAdapter.addIgnoredWifi("00:66:77:88:99:00", "TestSSID2");
	}

}
