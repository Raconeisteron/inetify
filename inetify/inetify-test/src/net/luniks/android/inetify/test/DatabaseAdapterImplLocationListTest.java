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
		DatabaseAdapterImpl helper = new DatabaseAdapterImpl(this.getContext());
		
		assertFalse(helper.isOpen());
	}
	
	public void testAddLocationOpensDatabase() {
		DatabaseAdapterImpl helper = new DatabaseAdapterImpl(this.getContext());
		
		helper.addLocation("00:21:29:A2:48:80", "Celsten", TestUtils.getLocation(0.1, 0.1, 10));
		
		assertTrue(helper.isOpen());
		
		helper.close();
		
		assertFalse(helper.isOpen());
	}
	
	public void testFindWifiIfOpensDatabase() {
		DatabaseAdapterImpl helper = new DatabaseAdapterImpl(this.getContext());
		
		helper.findWifi(new Location(""));
		
		assertTrue(helper.isOpen());
		
		helper.close();
		
		assertFalse(helper.isOpen());
	}
	
	public void testDeleteLocationOpensDatabase() {
		DatabaseAdapterImpl helper = new DatabaseAdapterImpl(this.getContext());
		
		helper.deleteLocation("00:21:29:A2:48:80");
		
		assertTrue(helper.isOpen());
		
		helper.close();
		
		assertFalse(helper.isOpen());
	}
	
	public void testFetchLocationsWifisOpensDatabase() {
		DatabaseAdapterImpl helper = new DatabaseAdapterImpl(this.getContext());
		
		helper.fetchLocations();
		
		assertTrue(helper.isOpen());
		
		helper.close();
		
		assertFalse(helper.isOpen());
	}
	
	public void testAddLocation() {
		
		DatabaseAdapterImpl helper = new DatabaseAdapterImpl(this.getContext());
		
		assertTrue(helper.addLocation("00:21:29:A2:48:80", "Celsten", TestUtils.getLocation(0.1, 0.1, 10)));
		assertTrue(helper.addLocation("00:11:22:33:44:55", "TestSSID1", TestUtils.getLocation(0.2, 0.2, 20)));
		assertTrue(helper.addLocation("00:66:77:88:99:00", "TestSSID2", TestUtils.getLocation(0.3, 0.3, 30)));
		assertFalse(helper.addLocation(null, null, null));

		Cursor cursor = helper.fetchLocations();
		
		assertEquals(3, cursor.getCount());
		assertTrue(cursor.moveToNext());
		assertEquals("00:21:29:A2:48:80", cursor.getString(1));
		assertEquals("Celsten", cursor.getString(2));
		assertEquals(0.1, cursor.getDouble(3));
		assertEquals(0.1, cursor.getDouble(4));
		assertEquals(10, cursor.getInt(5));
		assertTrue(cursor.moveToNext());
		assertEquals("00:11:22:33:44:55", cursor.getString(1));
		assertEquals("TestSSID1", cursor.getString(2));
		assertEquals(0.2, cursor.getDouble(3));
		assertEquals(0.2, cursor.getDouble(4));
		assertEquals(20, cursor.getInt(5));
		assertTrue(cursor.moveToNext());
		assertEquals("00:66:77:88:99:00", cursor.getString(1));
		assertEquals("TestSSID2", cursor.getString(2));
		assertEquals(0.3, cursor.getDouble(3));
		assertEquals(0.3, cursor.getDouble(4));
		assertEquals(30, cursor.getInt(5));
		assertFalse(cursor.moveToNext());
		
		helper.close();
	}
	
	public void testAddLocationSameBSSIDOtherSSID() {
		
		DatabaseAdapterImpl helper = new DatabaseAdapterImpl(this.getContext());
		
		assertTrue(helper.addLocation("00:66:77:88:99:00", "TestSSID2", TestUtils.getLocation(0.3, 0.3, 30)));
		assertTrue(helper.addLocation("00:66:77:88:99:00", "TestSSID2New", TestUtils.getLocation(0.3, 0.3, 30)));
		
		Cursor cursor = helper.fetchLocations();
		
		assertEquals(1, cursor.getCount());
		assertTrue(cursor.moveToNext());
		assertEquals("00:66:77:88:99:00", cursor.getString(1));
		assertEquals("TestSSID2New", cursor.getString(2));
		assertFalse(cursor.moveToNext());
		
		helper.close();
	}
	
	public void testFindWifi() {
		
		DatabaseAdapterImpl helper = new DatabaseAdapterImpl(this.getContext());
		
		insertTestLocations(helper);
		
		String ssid = helper.findWifi(new Location(""));
		
		assertEquals("Celsten", ssid);
		
		helper.close();
	}
	
	public void testDeleteLocation() {
		
		DatabaseAdapterImpl helper = new DatabaseAdapterImpl(this.getContext());
		
		insertTestLocations(helper);
		
		assertTrue(helper.deleteLocation("00:11:22:33:44:55"));
		assertFalse(helper.deleteLocation("xx:xx:xx:xx:xx:xx"));
		assertFalse(helper.deleteLocation(null));
		
		Cursor cursor = helper.fetchLocations();
		
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
	
	public void testFetchLocations() {
		
		DatabaseAdapterImpl helper = new DatabaseAdapterImpl(this.getContext());
		
		insertTestLocations(helper);
		
		Cursor cursor = helper.fetchLocations();
		
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
	
	private void insertTestLocations(final DatabaseAdapterImpl databaseAdapter) {
		databaseAdapter.addLocation("00:21:29:A2:48:80", "Celsten", TestUtils.getLocation(0.1, 0.1, 10));
		databaseAdapter.addLocation("00:11:22:33:44:55", "TestSSID1", TestUtils.getLocation(0.2, 0.2, 20));
		databaseAdapter.addLocation("00:66:77:88:99:00", "TestSSID2", TestUtils.getLocation(0.3, 0.3, 30));
	}

}
