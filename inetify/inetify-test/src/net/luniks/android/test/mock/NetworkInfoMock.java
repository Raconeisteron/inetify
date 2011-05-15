package net.luniks.android.test.mock;

import net.luniks.android.interfaces.INetworkInfo;

public class NetworkInfoMock implements INetworkInfo {

	private int type;
	private String typeName;
	private String subtypeName;
	private boolean connected;
	
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
		return connected;
	}
	public void setConnected(boolean connected) {
		this.connected = connected;
	}

}
