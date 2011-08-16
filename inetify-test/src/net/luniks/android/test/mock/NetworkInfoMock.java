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
package net.luniks.android.test.mock;

import java.util.concurrent.atomic.AtomicBoolean;

import net.luniks.android.interfaces.INetworkInfo;

public class NetworkInfoMock implements INetworkInfo {

	private int type;
	private String typeName;
	private String subtypeName;
	private AtomicBoolean connected = new AtomicBoolean();
	
	private int isConnectedCallCount = 0;
	private int disconnectAfter = -1;
	
	public int getType() {
		return type;
	}
	public NetworkInfoMock setType(final int type) {
		this.type = type;
		return this;
	}
	public String getTypeName() {
		return typeName;
	}
	public NetworkInfoMock setTypeName(final String typeName) {
		this.typeName = typeName;
		return this;
	}
	public String getSubtypeName() {
		return subtypeName;
	}
	public NetworkInfoMock setSubtypeName(final String subtypeName) {
		this.subtypeName = subtypeName;
		return this;
	}
	public boolean isConnectedOrConnecting() {
		isConnectedCallCount += 1;
		if(disconnectAfter != -1 && isConnectedCallCount > disconnectAfter) {
			connected.set(false);
		}
		return connected.get();
	}
	public NetworkInfoMock setConnected(final boolean connected) {
		this.connected.set(connected);
		return this;
	}
	
	public void disconnectAfter(final int disconnectAfter) {
		this.disconnectAfter = disconnectAfter;
	}
	
	public void disconnectAfterDelay(final long delay) {
		new Thread() {
			public void run() {
				try {
					Thread.sleep(delay);
				} catch (InterruptedException e) {
					// Ignore
				}
				connected.set(false);
			}
		}.start();
	}

}
