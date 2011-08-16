/*
 * Copyright 2011 Torsten Römer
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.luniks.android.inetify.test;



import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;

import net.luniks.android.inetify.TitleVerifierImpl;
import android.test.AndroidTestCase;

public class TitleVerifierImplTest extends AndroidTestCase {
	
	public void testAddProtocol() throws MalformedURLException {
		
		assertEquals("http", new URL(TitleVerifierImpl.addProtocol("www.google.de")).getProtocol());
		assertEquals("http", new URL(TitleVerifierImpl.addProtocol("http://www.google.de")).getProtocol());
		assertEquals("https", new URL(TitleVerifierImpl.addProtocol("https://www.google.de")).getProtocol());
	}
	
	public void testAddProtocolInvalid() throws MalformedURLException {
		
		try {
			assertEquals("https", new URL(TitleVerifierImpl.addProtocol("invalid://www.google.de")).getProtocol());
			fail("Expected MalformedURLException");
		} catch(MalformedURLException e) {
			// Expected
		}
	}
	
	public void testGetPageTitle() throws Exception {
		
		TitleVerifierImpl titleVerifier = new TitleVerifierImpl();
		
		String pageTitle = titleVerifier.getPageTitle("www.google.de");
		assertEquals("google".toUpperCase(), pageTitle.toUpperCase());
		
		String pageTitleHTTP = titleVerifier.getPageTitle("http://www.google.de");
		assertEquals("google".toUpperCase(), pageTitleHTTP.toUpperCase());
		
		String pageTitleHTTPS = titleVerifier.getPageTitle("https://www.google.com");
		assertEquals("google".toUpperCase(), pageTitleHTTPS.toUpperCase());
		
	}
	
	public void testGetPageTitleInvalidProtocol() throws Exception {
		
		TitleVerifierImpl titleVerifier = new TitleVerifierImpl();
		
		try {
			titleVerifier.getPageTitle("invalid://www.google.de");
			fail("Expected IllegalArgumentException");
		} catch(IllegalArgumentException e) {
			// Expected
		}
		
	}
	
	public void testGetPageTitleUnknownHost() throws Exception {
		
		TitleVerifierImpl titleVerifier = new TitleVerifierImpl();
		
		try {
			titleVerifier.getPageTitle("www.unknownhost.domain");
			fail("Expected MalformedURLException");
		} catch(UnknownHostException e) {
			// Expected
		}
		
	}
	
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
