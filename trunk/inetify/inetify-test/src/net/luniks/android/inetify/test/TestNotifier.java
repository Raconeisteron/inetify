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

import net.luniks.android.inetify.Notifier;
import net.luniks.android.inetify.TestInfo;
import net.luniks.android.inetify.WifiLocation;
import android.location.Location;

public class TestNotifier implements Notifier {
	
	private int locatifyCallCount = 0;
	
	public int getLocatifyCallCount() {
		return locatifyCallCount;
	}

	public void inetify(TestInfo info) {
		// TODO Auto-generated method stub
	}

	public void locatify(Location location, WifiLocation nearestlocation) {
		locatifyCallCount++;
	}

}
