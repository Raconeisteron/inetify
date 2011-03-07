package net.luniks.android.inetify.test;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import net.luniks.android.inetify.ConnectivityUtil;

import org.junit.Test;

public class ConnectivityUtilTest {
	
	@Test
	public void testGetPageTitle() throws IOException {
		
		String pageTitle = ConnectivityUtil.getPageTitle("www.google.de");
		
		assertEquals("google".toUpperCase(), pageTitle.toUpperCase());
		
	}
	
}
