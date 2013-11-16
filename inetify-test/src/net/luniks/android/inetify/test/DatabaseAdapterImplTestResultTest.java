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

import net.luniks.android.inetify.DatabaseAdapterImpl;
import net.luniks.android.inetify.TestInfo;
import android.net.ConnectivityManager;
import android.test.AndroidTestCase;

public class DatabaseAdapterImplTestResultTest extends AndroidTestCase {

	public void setUp() throws Exception {
		super.setUp();
		this.getContext().deleteDatabase("inetifydb");
		this.getContext().deleteDatabase("inetifydb-journal");
	}
	
	public void testFetchNoResult() {
		
		final DatabaseAdapterImpl adapter = new DatabaseAdapterImpl(this.getContext());
		
		final TestInfo info = adapter.fetchTestResult();
		
		assertNull(info);
		
	}
	
	public void testUpdateTestResult() {
		
		final DatabaseAdapterImpl adapter = new DatabaseAdapterImpl(this.getContext());
		
		assertTrue(adapter.updateTestResult(1234567890L, ConnectivityManager.TYPE_WIFI, "Sputnik", true));
		
		TestInfo info = adapter.fetchTestResult();
		
		assertNotNull(info);
		assertEquals(1234567890L, info.getTimestamp());
		assertEquals(ConnectivityManager.TYPE_WIFI, info.getType());
		assertEquals("Sputnik", info.getExtra());
		assertTrue(info.getIsExpectedTitle());
		
		assertTrue(adapter.updateTestResult(0, -1, null, false));
		
		assertTrue(adapter.updateTestResult(0L, ConnectivityManager.TYPE_MOBILE, "UMTS", false));
		
		info = adapter.fetchTestResult();
		
		assertNotNull(info);
		assertEquals(0L, info.getTimestamp());
		assertEquals(ConnectivityManager.TYPE_MOBILE, info.getType());
		assertEquals("UMTS", info.getExtra());
		assertFalse(info.getIsExpectedTitle());
		
		adapter.close();
	}
	
}
