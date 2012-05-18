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
package net.luniks.android.test.impl;

import net.luniks.android.impl.NetworkInfoImpl;
import net.luniks.android.interfaces.INetworkInfo;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.test.AndroidTestCase;

public class NetworkInfoImplTest extends AndroidTestCase {
	
	public void testGetInstanceNull() {
		
		INetworkInfo wrapped = NetworkInfoImpl.getInstance(null);
		
		assertNull(wrapped);
		
	}
	
	public void testNetworkInfoImpl() {
		
		NetworkInfo real = ((ConnectivityManager)this.getContext().getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
		if(real == null) {
			fail("ConnectivityManager.getActiveNetworkInfo() is null, cannot run NetworkInfoImplTest");
		}
		
		INetworkInfo wrapped = NetworkInfoImpl.getInstance(real);
		
		assertEquals(real.getType(), wrapped.getType());
		assertEquals(real.getTypeName(), wrapped.getTypeName());
		assertEquals(real.getSubtypeName(), wrapped.getSubtypeName());
		assertEquals(real.isConnectedOrConnecting(), wrapped.isConnectedOrConnecting());
		
	}

}
