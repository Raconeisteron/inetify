package net.luniks.android.inetify.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;

import net.luniks.android.inetify.ConnectivityUtil;

import org.junit.Test;

public class ConnectivityUtilTest {
	
	@Test
	public void testAddProtocol() throws MalformedURLException {
		
		assertEquals("http", new URL(ConnectivityUtil.addProtocol("www.google.de")).getProtocol());
		assertEquals("http", new URL(ConnectivityUtil.addProtocol("http://www.google.de")).getProtocol());
		assertEquals("https", new URL(ConnectivityUtil.addProtocol("https://www.google.de")).getProtocol());
	}
	
	@Test(expected=MalformedURLException.class)
	public void testAddProtocolInvalid() throws MalformedURLException {
		
		assertEquals("https", new URL(ConnectivityUtil.addProtocol("invalid://www.google.de")).getProtocol());
	}
	
	@Test
	public void testGetPageTitle() throws Exception {
		
		String pageTitle = ConnectivityUtil.getPageTitle("www.google.de");
		assertEquals("google".toUpperCase(), pageTitle.toUpperCase());
		
		String pageTitleHTTP = ConnectivityUtil.getPageTitle("http://www.google.de");
		assertEquals("google".toUpperCase(), pageTitleHTTP.toUpperCase());
		
		String pageTitleHTTPS = ConnectivityUtil.getPageTitle("https://www.google.com");
		assertEquals("google".toUpperCase(), pageTitleHTTPS.toUpperCase());
		
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testGetPageTitleInvalidProtocol() throws Exception {
		
		String pageTitleWrong = ConnectivityUtil.getPageTitle("invalid://www.google.de");
		assertEquals("google".toUpperCase(), pageTitleWrong.toUpperCase());
		
	}
	
	@Test(expected=UnknownHostException.class)
	public void testGetPageTitleUnknownHost() throws Exception {
		
		String pageTitle = ConnectivityUtil.getPageTitle("www.unknownhost.domain");
		assertEquals("google".toUpperCase(), pageTitle.toUpperCase());
		
	}
	
	@Test
	public void testIsExpectedTitle() {
		
		String pageTitle = "Google";
		
		assertFalse(ConnectivityUtil.isExpectedTitle(null, pageTitle));
		assertFalse(ConnectivityUtil.isExpectedTitle("", pageTitle));
		assertFalse(ConnectivityUtil.isExpectedTitle(" ", pageTitle));
		assertFalse(ConnectivityUtil.isExpectedTitle("Some Title", pageTitle));
		assertTrue(ConnectivityUtil.isExpectedTitle("oog", pageTitle));
		assertTrue(ConnectivityUtil.isExpectedTitle("Google", pageTitle));
		assertTrue(ConnectivityUtil.isExpectedTitle("gOOGLE", pageTitle));
		
	}
	
}
