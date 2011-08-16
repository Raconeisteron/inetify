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

import net.luniks.android.inetify.R;
import android.test.AndroidTestCase;

public class RTest extends AndroidTestCase {
	
	public void testR() {
		
		assertNotNull(new R());
		assertNotNull(new R.attr());
		assertNotNull(new R.array());
		assertNotNull(new R.color());
		assertNotNull(new R.drawable());
		assertNotNull(new R.id());
		assertNotNull(new R.layout());
		assertNotNull(new R.string());
		assertNotNull(new R.style());
		assertNotNull(new R.xml());
		
	}

}
