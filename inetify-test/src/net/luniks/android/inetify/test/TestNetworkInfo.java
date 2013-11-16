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

import net.luniks.android.interfaces.INetworkInfo;
import android.net.ConnectivityManager;

public class TestNetworkInfo implements INetworkInfo {
	
	private int type = ConnectivityManager.TYPE_WIFI;
	private boolean isConnectedOrConnecting;
	
	public void setType(final int type) {
		this.type = type;
	}
	
	public void setIsConnectedOrConnecting(final boolean isConnectedOrConnecting) {
		this.isConnectedOrConnecting = isConnectedOrConnecting;
	}

	public String getTypeName() {
		// TODO Auto-generated method stub
		return null;
	}

	public int getType() {
		return type;
	}

	public String getSubtypeName() {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean isConnectedOrConnecting() {
		return isConnectedOrConnecting;
	}

}
