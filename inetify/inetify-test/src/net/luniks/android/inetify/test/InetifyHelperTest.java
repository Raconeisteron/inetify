package net.luniks.android.inetify.test;

import net.luniks.android.inetify.InetifyHelper;
import net.luniks.android.inetify.TitleVerifier;
import net.luniks.android.inetify.TitleVerifierImpl;
import net.luniks.android.test.mock.ConnectivityManagerMock;
import net.luniks.android.test.mock.NetworkInfoMock;
import net.luniks.android.test.mock.WifiInfoMock;
import net.luniks.android.test.mock.WifiManagerMock;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.preference.PreferenceManager;
import android.test.AndroidTestCase;

public class InetifyHelperTest extends AndroidTestCase {
	
	private SharedPreferences prefs;
	private TitleVerifier verifier;
	
	public void setUp() {
		this.prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
		this.verifier = new TitleVerifierImpl();
	}
	
	public void testIsWifiConnectedTrue() {
		
		NetworkInfoMock networkInfo = new NetworkInfoMock();
		networkInfo.setType(ConnectivityManager.TYPE_WIFI);
		networkInfo.setConnected(true);
		
		WifiInfoMock wifiInfo = new WifiInfoMock();
		wifiInfo.setSSID("MockSSID");
		
		InetifyHelper helper = new InetifyHelper(getContext(), prefs, 
				new ConnectivityManagerMock(networkInfo), 
				new WifiManagerMock(wifiInfo), 
				verifier);
		
		boolean isWifiConnected = helper.isWifiConnected();
		
		assertTrue(isWifiConnected);
	}
	
	public void testIsWifiConnectedFalse() {
		
		NetworkInfoMock networkInfo = new NetworkInfoMock();
		networkInfo.setType(ConnectivityManager.TYPE_WIFI);
		networkInfo.setConnected(false);
		
		WifiInfoMock wifiInfo = new WifiInfoMock();
		wifiInfo.setSSID("MockSSID");
		
		InetifyHelper helper = new InetifyHelper(getContext(), prefs, 
				new ConnectivityManagerMock(networkInfo), 
				new WifiManagerMock(wifiInfo), 
				verifier);
		
		boolean isWifiConnected = helper.isWifiConnected();
		
		assertFalse(isWifiConnected);
	}
	
	public void testIsWifiConnectedMobile() {
		
		NetworkInfoMock networkInfo = new NetworkInfoMock();
		networkInfo.setType(ConnectivityManager.TYPE_MOBILE);
		networkInfo.setConnected(true);
		
		WifiInfoMock wifiInfo = new WifiInfoMock();
		wifiInfo.setSSID("MockSSID");
		
		InetifyHelper helper = new InetifyHelper(getContext(), prefs, 
				new ConnectivityManagerMock(networkInfo), 
				new WifiManagerMock(wifiInfo), 
				verifier);
		
		boolean isWifiConnected = helper.isWifiConnected();
		
		assertFalse(isWifiConnected);
	}
	
	public void testIsWifiConnectedSSIDNull() {
		
		NetworkInfoMock networkInfo = new NetworkInfoMock();
		networkInfo.setType(ConnectivityManager.TYPE_WIFI);
		networkInfo.setConnected(true);
		
		WifiInfoMock wifiInfo = new WifiInfoMock();
		wifiInfo.setSSID(null);
		
		InetifyHelper helper = new InetifyHelper(getContext(), prefs, 
				new ConnectivityManagerMock(networkInfo), 
				new WifiManagerMock(wifiInfo), 
				verifier);
		
		boolean isWifiConnected = helper.isWifiConnected();
		
		assertFalse(isWifiConnected);
	}
	
}
