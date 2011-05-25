package net.luniks.android.inetify.test;

import java.util.Date;

import net.luniks.android.inetify.TestInfo;
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
		assertTrue(string.contains("expectedTitle = true"));
		assertTrue(string.contains("pageTitle = TestPageTitle"));
		assertTrue(string.contains("site = TestSite"));
		assertTrue(string.contains("timestamp = " + new Date(1234567890)));
		assertTrue(string.contains("title = TestTitle"));
		assertTrue(string.contains("type = TestType"));
		
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
		assertEquals(true, info.getIsExpectedTitle());
		assertEquals("TestPageTitle", info.getPageTitle());
		assertEquals("TestSite", info.getSite());
		assertEquals(1234567890, info.getTimestamp());
		assertEquals("TestTitle", info.getTitle());
		assertEquals("TestType", info.getType());
	}
	
	private TestInfo getTestInfoAllSet() {
		
		TestInfo info = new TestInfo();
		
		info.setException("TestException");
		info.setExtra("TestExtra");
		info.setIsExpectedTitle(true);
		info.setPageTitle("TestPageTitle");
		info.setSite("TestSite");
		info.setTimestamp(1234567890);
		info.setTitle("TestTitle");
		info.setType("TestType");
		
		return info;
	}

}
