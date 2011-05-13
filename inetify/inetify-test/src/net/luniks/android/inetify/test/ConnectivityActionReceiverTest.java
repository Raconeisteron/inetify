package net.luniks.android.inetify.test;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;

import net.luniks.android.inetify.ConnectivityActionReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.test.AndroidTestCase;
import android.test.mock.MockContext;

public class ConnectivityActionReceiverTest extends AndroidTestCase {
	
	private ConnectivityActionReceiver receiver;
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		
		receiver = new ConnectivityActionReceiver();
	}

	public void testWifiConnected() throws Exception {
		
		NetworkInfo mobileDisconnected = createNetworkInfo(getContext(), ConnectivityManager.TYPE_MOBILE, false);
		NetworkInfo wifiConnected = createNetworkInfo(getContext(), ConnectivityManager.TYPE_WIFI, true);
		
		// Mobile disconnects
		Intent connectivityActionMobileDisconnect = new Intent(ConnectivityManager.CONNECTIVITY_ACTION);
		connectivityActionMobileDisconnect.putExtra(ConnectivityManager.EXTRA_NETWORK_INFO, mobileDisconnected);
		
		// Wifi connects
		Intent wifiActionConnect = new Intent(WifiManager.NETWORK_STATE_CHANGED_ACTION);
		wifiActionConnect.putExtra(WifiManager.EXTRA_NETWORK_INFO, wifiConnected);
		
		// Connectivity connects 1st intent
		Intent connectivityActionWifiConnect1 = new Intent(ConnectivityManager.CONNECTIVITY_ACTION);
		connectivityActionWifiConnect1.putExtra(ConnectivityManager.EXTRA_NETWORK_INFO, wifiConnected);
		
		// Connectivity connects 2nd intent
		Intent connectivityActionWifiConnect2 = new Intent(ConnectivityManager.CONNECTIVITY_ACTION);
		connectivityActionWifiConnect2.putExtra(ConnectivityManager.EXTRA_NETWORK_INFO, wifiConnected);
		
		TestContext testContext = new TestContext(getContext());
		
		receiver.onReceive(testContext, connectivityActionMobileDisconnect);
		receiver.onReceive(testContext, wifiActionConnect);
		receiver.onReceive(testContext, connectivityActionWifiConnect1);
		receiver.onReceive(testContext, connectivityActionWifiConnect2);
		
		assertEquals(1, testContext.getStartServiceCount());
		
		Intent service = testContext.getStartServiceIntent();
		assertNotNull(service);
		
		boolean connected = service.getBooleanExtra(ConnectivityActionReceiver.EXTRA_IS_WIFI_CONNECTED, false);
		assertTrue(connected);
		
	}
	
	public void testWifiDisconnected() throws Exception {
		
		NetworkInfo wifiDisconnected = createNetworkInfo(getContext(), ConnectivityManager.TYPE_WIFI, false);
		NetworkInfo mobileConnected = createNetworkInfo(getContext(), ConnectivityManager.TYPE_MOBILE, true);
		
		// Wifi disconnects
		Intent connectivityActionWifiDisconnects = new Intent(ConnectivityManager.CONNECTIVITY_ACTION);
		connectivityActionWifiDisconnects.putExtra(ConnectivityManager.EXTRA_NETWORK_INFO, wifiDisconnected);
		
		// Mobile connects
		Intent connectivityActionMobileConnects = new Intent(ConnectivityManager.CONNECTIVITY_ACTION);
		connectivityActionMobileConnects.putExtra(ConnectivityManager.EXTRA_NETWORK_INFO, mobileConnected);
		
		TestContext testContext = new TestContext(getContext());
		
		receiver.onReceive(testContext, connectivityActionWifiDisconnects);
		receiver.onReceive(testContext, connectivityActionMobileConnects);
		
		assertEquals(1, testContext.getStartServiceCount());
		
		Intent service = testContext.getStartServiceIntent();
		assertNotNull(service);
		
		boolean connected = service.getBooleanExtra(ConnectivityActionReceiver.EXTRA_IS_WIFI_CONNECTED, true);
		assertFalse(connected);
		
	}
	
	private class TestContext extends MockContext {
		
		private final Context context;
		
		private int startServiceCount = 0;
		private Intent startServiceIntent = null;
		
		public TestContext(final Context context) {
			this.context = context;
		}
		
		public int getStartServiceCount() {
			return startServiceCount;
		}

		public Intent getStartServiceIntent() {
			return startServiceIntent;
		}

		@Override
		public ComponentName startService(Intent service) {
			startServiceCount++;
			this.startServiceIntent = service;
			return null;
		}

		@Override
		public String getPackageName() {
			return context.getPackageName();
		}

		@Override
		public SharedPreferences getSharedPreferences(String name, int mode) {
			return context.getSharedPreferences(name, mode);
		}
		
	}
	
	private NetworkInfo createNetworkInfo(final Context context, final int type, final boolean connected) throws Exception {
		
		Constructor<NetworkInfo> ctor = NetworkInfo.class.getDeclaredConstructor(int.class);
		ctor.setAccessible(true);
		NetworkInfo networkInfo = ctor.newInstance(0);
		Field typeField = NetworkInfo.class.getDeclaredField("mNetworkType");
		Field connectedField = NetworkInfo.class.getDeclaredField("mState");
		typeField.setAccessible(true);
		connectedField.setAccessible(true);
		typeField.setInt(networkInfo, type);
		connectedField.set(networkInfo, connected == true ? NetworkInfo.State.CONNECTED : NetworkInfo.State.DISCONNECTED);
		return networkInfo;
	}

}
