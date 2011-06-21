package net.luniks.android.inetify.test;

import java.util.Date;

import net.luniks.android.inetify.TestInfo;
import android.net.ConnectivityManager;
import android.os.Parcel;
import android.test.AndroidTestCase;

public class TestInfoTest extends AndroidTestCase {
	
	public void testSetGet() {
		
		TestInfo info = getTestInfoAllSet();
		
		assertAllGet(info);
	}
	
	public void testToString() {
		
		TestInfo info = getTestInfoAllSet();
		String string = String.valueOf(info);
		
		assertTrue(string.contains("exception = TestException"));
		assertTrue(string.contains("extra = TestExtra"));
		assertTrue(string.contains("extra2 = TestExtra2"));
		assertTrue(string.contains("expectedTitle = true"));
		assertTrue(string.contains("pageTitle = TestPageTitle"));
		assertTrue(string.contains("site = TestSite"));
		assertTrue(string.contains("timestamp = " + new Date(1234567890)));
		assertTrue(string.contains("title = TestTitle"));
		assertTrue(string.contains("type = 0"));
		assertTrue(string.contains("typeName = TestTypeName"));
		
	}
	
	public void testGetNiceTypeNameUnknown() {
		
		TestInfo info = new TestInfo();
		
		assertEquals(TestInfo.NICE_TYPE_NAME_UNKNOWN, info.getNiceTypeName());
		
	}
	
	public void testGetNiceTypeNameMobile() {
		
		TestInfo info = new TestInfo();
		info.setType(ConnectivityManager.TYPE_MOBILE);
		
		assertEquals(TestInfo.NICE_TYPE_NAME_MOBILE, info.getNiceTypeName());
		
	}
	
	public void testGetNiceTypeNameWifi() {
		
		TestInfo info = new TestInfo();
		info.setType(ConnectivityManager.TYPE_WIFI);
		
		assertEquals(TestInfo.NICE_TYPE_NAME_WIFI, info.getNiceTypeName());
		
	}
	
	public void testParcelable() {
		
		TestInfo info = getTestInfoAllSet();
		
		Parcel parcel = Parcel.obtain();
		info.writeToParcel(parcel, 0);
		
		parcel.setDataPosition(0);
		TestInfo fromParcel = new TestInfo(parcel);
		
		assertAllGet(fromParcel);
	}
	
	public void testDescribeContents() {
		
		TestInfo info = new TestInfo();
		
		assertEquals(0, info.describeContents());
	}
	
	private void assertAllGet(final TestInfo info) {
		assertEquals("TestException", info.getException());
		assertEquals("TestExtra", info.getExtra());
		assertEquals("TestExtra2", info.getExtra2());
		assertEquals(true, info.getIsExpectedTitle());
		assertEquals("TestPageTitle", info.getPageTitle());
		assertEquals("TestSite", info.getSite());
		assertEquals(1234567890, info.getTimestamp());
		assertEquals("TestTitle", info.getTitle());
		assertEquals(0, info.getType());
		assertEquals("TestTypeName", info.getTypeName());
	}
	
	private TestInfo getTestInfoAllSet() {
		
		TestInfo info = new TestInfo();
		
		info.setException("TestException");
		info.setExtra("TestExtra");
		info.setExtra2("TestExtra2");
		info.setIsExpectedTitle(true);
		info.setPageTitle("TestPageTitle");
		info.setSite("TestSite");
		info.setTimestamp(1234567890);
		info.setTitle("TestTitle");
		info.setType(0);
		info.setTypeName("TestTypeName");
		
		return info;
	}

}
