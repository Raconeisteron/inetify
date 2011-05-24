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
	public void setType(int type) {
		this.type = type;
	}
	public String getTypeName() {
		return typeName;
	}
	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}
	public String getSubtypeName() {
		return subtypeName;
	}
	public void setSubtypeName(String subtypeName) {
		this.subtypeName = subtypeName;
	}
	public boolean isConnected() {
		isConnectedCallCount += 1;
		if(disconnectAfter != -1 && isConnectedCallCount > disconnectAfter) {
			connected.set(false);
		}
		return connected.get();
	}
	public void setConnected(boolean connected) {
		this.connected.set(connected);
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
