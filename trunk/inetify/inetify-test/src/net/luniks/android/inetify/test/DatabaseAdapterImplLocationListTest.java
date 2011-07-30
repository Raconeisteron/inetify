package net.luniks.android.inetify.test;

import net.luniks.android.inetify.DatabaseAdapterImpl;
import android.database.Cursor;
import android.location.Location;
import android.test.AndroidTestCase;

public class DatabaseAdapterImplLocationListTest extends AndroidTestCase {
	
	public void setUp() throws Exception {
		super.setUp();
		this.getContext().deleteDatabase("inetifydb");
		this.getContext().deleteDatabase("inetifydb-journal");
	}
	
	public void testDatabaseNotOpen() {
		DatabaseAdapterImpl adapter = new DatabaseAdapterImpl(this.getContext());
		
		assertFalse(adapter.isOpen());
	}
	
	public void testAddLocationOpensDatabase() {
		DatabaseAdapterImpl adapter = new DatabaseAdapterImpl(this.getContext());
		
		adapter.addLocation("00:21:29:A2:48:80", "Celsten", "Celsten", TestUtils.createLocation(0.1, 0.1, 10));
		
		assertTrue(adapter.isOpen());
		
		adapter.close();
		
		assertFalse(adapter.isOpen());
	}
	
	public void testFindWifiIfOpensDatabase() {
		DatabaseAdapterImpl adapter = new DatabaseAdapterImpl(this.getContext());
		
		adapter.findWifi(new Location(""));
		
		assertTrue(adapter.isOpen());
		
		adapter.close();
		
		assertFalse(adapter.isOpen());
	}
	
	public void testDeleteLocationOpensDatabase() {
		DatabaseAdapterImpl adapter = new DatabaseAdapterImpl(this.getContext());
		
		adapter.deleteLocation("00:21:29:A2:48:80");
		
		assertTrue(adapter.isOpen());
		
		adapter.close();
		
		assertFalse(adapter.isOpen());
	}
	
	public void testRenameLocationOpensDatabase() {
		DatabaseAdapterImpl adapter = new DatabaseAdapterImpl(this.getContext());
		
		adapter.renameLocation("00:21:29:A2:48:80", "CelstenName");
		
		assertTrue(adapter.isOpen());
		
		adapter.close();
		
		assertFalse(adapter.isOpen());
	}
	
	public void testFetchLocationsWifisOpensDatabase() {
		DatabaseAdapterImpl adapter = new DatabaseAdapterImpl(this.getContext());
		
		adapter.fetchLocations();
		
		assertTrue(adapter.isOpen());
		
		adapter.close();
		
		assertFalse(adapter.isOpen());
	}
	
	public void testAddLocation() {
		
		DatabaseAdapterImpl adapter = new DatabaseAdapterImpl(this.getContext());
		
		assertTrue(adapter.addLocation("00:21:29:A2:48:80", "Celsten", "CelstenName", TestUtils.createLocation(0.1, 0.1, 10)));
		assertTrue(adapter.addLocation("00:11:22:33:44:55", "TestSSID1", "", TestUtils.createLocation(0.2, 0.2, 20)));
		assertTrue(adapter.addLocation("00:66:77:88:99:00", "TestSSID2", null, TestUtils.createLocation(0.3, 0.3, 30)));
		assertFalse(adapter.addLocation(null, null, null, null));

		Cursor cursor = adapter.fetchLocations();
		
		assertEquals(3, cursor.getCount());
		assertTrue(cursor.moveToNext());
		assertEquals("00:21:29:A2:48:80", cursor.getString(1));
		assertEquals("Celsten", cursor.getString(2));
		assertEquals("CelstenName", cursor.getString(3));
		assertEquals(0.1, cursor.getDouble(4));
		assertEquals(0.1, cursor.getDouble(5));
		assertEquals(10, cursor.getInt(6));
		assertTrue(cursor.moveToNext());
		assertEquals("00:11:22:33:44:55", cursor.getString(1));
		assertEquals("TestSSID1", cursor.getString(2));
		assertEquals("TestSSID1", cursor.getString(3));
		assertEquals(0.2, cursor.getDouble(4));
		assertEquals(0.2, cursor.getDouble(5));
		assertEquals(20, cursor.getInt(6));
		assertTrue(cursor.moveToNext());
		assertEquals("00:66:77:88:99:00", cursor.getString(1));
		assertEquals("TestSSID2", cursor.getString(2));
		assertEquals("TestSSID2", cursor.getString(3));
		assertEquals(0.3, cursor.getDouble(4));
		assertEquals(0.3, cursor.getDouble(5));
		assertEquals(30, cursor.getInt(6));
		assertFalse(cursor.moveToNext());
		
		cursor.close();
		adapter.close();
	}
	
	public void testAddLocationSameBSSIDOtherSSID() {
		
		DatabaseAdapterImpl adapter = new DatabaseAdapterImpl(this.getContext());
		
		assertTrue(adapter.addLocation("00:66:77:88:99:00", "TestSSID2", "Test2", TestUtils.createLocation(0.3, 0.3, 30)));
		assertTrue(adapter.addLocation("00:66:77:88:99:00", "TestSSID2New", "Test2New", TestUtils.createLocation(0.3, 0.3, 30)));
		
		Cursor cursor = adapter.fetchLocations();
		
		assertEquals(1, cursor.getCount());
		assertTrue(cursor.moveToNext());
		assertEquals("00:66:77:88:99:00", cursor.getString(1));
		assertEquals("TestSSID2New", cursor.getString(2));
		assertEquals("Test2New", cursor.getString(3));
		assertFalse(cursor.moveToNext());
		
		cursor.close();
		adapter.close();
	}
	
	// TODO Implement
	public void testFindWifi() {
		
		DatabaseAdapterImpl adapter = new DatabaseAdapterImpl(this.getContext());
		
		insertTestLocations(adapter);
		
		String ssid = adapter.findWifi(new Location(""));
		
		assertEquals("Celsten", ssid);
		adapter.close();
	}
	
	public void testDeleteLocation() {
		
		DatabaseAdapterImpl adapter = new DatabaseAdapterImpl(this.getContext());
		
		insertTestLocations(adapter);
		
		assertTrue(adapter.deleteLocation("00:11:22:33:44:55"));
		assertFalse(adapter.deleteLocation("xx:xx:xx:xx:xx:xx"));
		assertFalse(adapter.deleteLocation(null));
		
		Cursor cursor = adapter.fetchLocations();
		
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
	
	public void testRenameLocation() {
		
		DatabaseAdapterImpl adapter = new DatabaseAdapterImpl(this.getContext());
		
		insertTestLocations(adapter);
		
		assertTrue(adapter.renameLocation("00:11:22:33:44:55", "Test1Renamed"));
		
		Cursor cursor = adapter.fetchLocations();
		
		assertEquals(3, cursor.getCount());
		assertTrue(cursor.moveToNext());
		assertEquals("00:21:29:A2:48:80", cursor.getString(1));
		assertEquals("Celsten", cursor.getString(2));
		assertEquals("Celsten", cursor.getString(3));
		assertTrue(cursor.moveToNext());
		assertEquals("00:11:22:33:44:55", cursor.getString(1));
		assertEquals("TestSSID1", cursor.getString(2));
		assertEquals("Test1Renamed", cursor.getString(3));
		assertTrue(cursor.moveToNext());
		assertEquals("00:66:77:88:99:00", cursor.getString(1));
		assertEquals("TestSSID2", cursor.getString(2));
		assertEquals("Test2", cursor.getString(3));
		assertFalse(cursor.moveToNext());
		
		cursor.close();
		adapter.close();
	}
	
	public void testRenameLocationNameNull() {
		
		DatabaseAdapterImpl adapter = new DatabaseAdapterImpl(this.getContext());
		
		assertTrue(adapter.addLocation("00:21:29:A2:48:80", "Celsten", "CelstenName", TestUtils.createLocation(0.1, 0.1, 10)));
		
		assertFalse(adapter.renameLocation("00:21:29:A2:48:80", null));
		
		Cursor cursor = adapter.fetchLocations();
		
		assertEquals(1, cursor.getCount());
		assertTrue(cursor.moveToNext());
		assertEquals("CelstenName", cursor.getString(3));
		assertFalse(cursor.moveToNext());
		
		cursor.close();
		adapter.close();
	}
	
	public void testRenameLocationNameEmpty() {
		
		DatabaseAdapterImpl adapter = new DatabaseAdapterImpl(this.getContext());
		
		assertTrue(adapter.addLocation("00:21:29:A2:48:80", "Celsten", "CelstenName", TestUtils.createLocation(0.1, 0.1, 10)));
		
		assertFalse(adapter.renameLocation("00:21:29:A2:48:80", ""));
		
		Cursor cursor = adapter.fetchLocations();
		
		assertEquals(1, cursor.getCount());
		assertTrue(cursor.moveToNext());
		assertEquals("CelstenName", cursor.getString(3));
		assertFalse(cursor.moveToNext());
		
		cursor.close();
		adapter.close();
	}
	
	public void testRenameLocationNameLongerThan32Chars() {
		
		DatabaseAdapterImpl adapter = new DatabaseAdapterImpl(this.getContext());
		
		assertTrue(adapter.addLocation("00:21:29:A2:48:80", "Celsten", "CelstenName", TestUtils.createLocation(0.1, 0.1, 10)));
		
		assertTrue(adapter.renameLocation("00:21:29:A2:48:80", String.valueOf(new char[33])));
		
		Cursor cursor = adapter.fetchLocations();
		
		assertEquals(1, cursor.getCount());
		assertTrue(cursor.moveToNext());
		assertEquals(32, cursor.getString(3).length());
		assertFalse(cursor.moveToNext());
		
		cursor.close();
		adapter.close();
	}
	
	public void testFetchLocations() {
		
		DatabaseAdapterImpl adapter = new DatabaseAdapterImpl(this.getContext());
		
		insertTestLocations(adapter);
		
		Cursor cursor = adapter.fetchLocations();
		
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
	
	private void insertTestLocations(final DatabaseAdapterImpl databaseAdapter) {
		databaseAdapter.addLocation("00:21:29:A2:48:80", "Celsten", "Celsten", TestUtils.createLocation(0.1, 0.1, 10));
		databaseAdapter.addLocation("00:11:22:33:44:55", "TestSSID1", "Test1", TestUtils.createLocation(0.2, 0.2, 20));
		databaseAdapter.addLocation("00:66:77:88:99:00", "TestSSID2", "Test2", TestUtils.createLocation(0.3, 0.3, 30));
	}

}
