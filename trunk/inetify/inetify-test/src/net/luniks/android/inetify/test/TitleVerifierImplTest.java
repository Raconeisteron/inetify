package net.luniks.android.inetify.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;

import net.luniks.android.inetify.TitleVerifierImpl;

import org.junit.Test;

public class TitleVerifierImplTest {
	
	@Test
	public void testAddProtocol() throws MalformedURLException {
		
		assertEquals("http", new URL(TitleVerifierImpl.addProtocol("www.google.de")).getProtocol());
		assertEquals("http", new URL(TitleVerifierImpl.addProtocol("http://www.google.de")).getProtocol());
		assertEquals("https", new URL(TitleVerifierImpl.addProtocol("https://www.google.de")).getProtocol());
	}
	
	@Test(expected=MalformedURLException.class)
	public void testAddProtocolInvalid() throws MalformedURLException {
		
		assertEquals("https", new URL(TitleVerifierImpl.addProtocol("invalid://www.google.de")).getProtocol());
	}
	
	@Test
	public void testGetPageTitle() throws Exception {
		
		TitleVerifierImpl titleVerifier = new TitleVerifierImpl();
		
		String pageTitle = titleVerifier.getPageTitle("www.google.de");
		assertEquals("google".toUpperCase(), pageTitle.toUpperCase());
		
		String pageTitleHTTP = titleVerifier.getPageTitle("http://www.google.de");
		assertEquals("google".toUpperCase(), pageTitleHTTP.toUpperCase());
		
		String pageTitleHTTPS = titleVerifier.getPageTitle("https://www.google.com");
		assertEquals("google".toUpperCase(), pageTitleHTTPS.toUpperCase());
		
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testGetPageTitleInvalidProtocol() throws Exception {
		
		TitleVerifierImpl titleVerifier = new TitleVerifierImpl();
		
		String pageTitleWrong = titleVerifier.getPageTitle("invalid://www.google.de");
		assertEquals("google".toUpperCase(), pageTitleWrong.toUpperCase());
		
	}
	
	@Test(expected=UnknownHostException.class)
	public void testGetPageTitleUnknownHost() throws Exception {
		
		TitleVerifierImpl titleVerifier = new TitleVerifierImpl();
		
		String pageTitle = titleVerifier.getPageTitle("www.unknownhost.domain");
		assertEquals("google".toUpperCase(), pageTitle.toUpperCase());
		
	}
	
	@Test
	public void testIsExpectedTitle() {
		
		TitleVerifierImpl titleVerifier = new TitleVerifierImpl();
		
		String pageTitle = "Google";
		
		assertFalse(titleVerifier.isExpectedTitle(null, pageTitle));
		assertFalse(titleVerifier.isExpectedTitle("", pageTitle));
		assertFalse(titleVerifier.isExpectedTitle(" ", pageTitle));
		assertFalse(titleVerifier.isExpectedTitle("Some Title", pageTitle));
		assertTrue(titleVerifier.isExpectedTitle("oog", pageTitle));
		assertTrue(titleVerifier.isExpectedTitle("Google", pageTitle));
		assertTrue(titleVerifier.isExpectedTitle("gOOGLE", pageTitle));
		
	}
	
}
