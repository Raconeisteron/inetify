package net.luniks.android.inetify.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import net.luniks.android.inetify.ConnectivityUtil;

import org.junit.Test;

public class ConnectivityUtilTest {
	
	@Test
	public void testGetPageTitle() throws IOException {
		
		String pageTitle = ConnectivityUtil.getPageTitle("www.google.de");
		
		assertEquals("google".toUpperCase(), pageTitle.toUpperCase());
		
	}
	
	@Test
	public void testIsExpectedTitle() {
		
		String pageTitle = "Google";
		
		assertFalse(ConnectivityUtil.isExpectedTitle(null, pageTitle));
		assertFalse(ConnectivityUtil.isExpectedTitle("", pageTitle));
		assertFalse(ConnectivityUtil.isExpectedTitle(" ", pageTitle));
		assertTrue(ConnectivityUtil.isExpectedTitle("oog", pageTitle));
		assertTrue(ConnectivityUtil.isExpectedTitle("Google", pageTitle));
		assertTrue(ConnectivityUtil.isExpectedTitle("gOOGLE", pageTitle));
		assertFalse(ConnectivityUtil.isExpectedTitle("Some Title", pageTitle));
		
	}
	
}
