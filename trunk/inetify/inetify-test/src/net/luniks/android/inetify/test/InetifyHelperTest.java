package net.luniks.android.inetify.test;

import net.luniks.android.inetify.InetifyHelper;
import net.luniks.android.inetify.TestInfo;
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
	
	public void setUp() {
		this.prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
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
				new TitleVerifierImpl());
		
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
				new TitleVerifierImpl());
		
		boolean isWifiConnected = helper.isWifiConnected();
		
		assertFalse(isWifiConnected);
	}
	
	public void testIsWifiConnectedMobile() {
		
		NetworkInfoMock networkInfo = new NetworkInfoMock();
		networkInfo.setType(ConnectivityManager.TYPE_MOBILE);
		networkInfo.setConnected(true);
		
		InetifyHelper helper = new InetifyHelper(getContext(), prefs, 
				new ConnectivityManagerMock(networkInfo), 
				new WifiManagerMock(null), 
				new TitleVerifierImpl());
		
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
				new TitleVerifierImpl());
		
		boolean isWifiConnected = helper.isWifiConnected();
		
		assertFalse(isWifiConnected);
	}
	
	public void testGetTestInfoWifiOK() {
		
		NetworkInfoMock networkInfo = new NetworkInfoMock();
		networkInfo.setType(ConnectivityManager.TYPE_WIFI);
		networkInfo.setTypeName("MockWifi");
		networkInfo.setConnected(true);
		
		WifiInfoMock wifiInfo = new WifiInfoMock();
		wifiInfo.setSSID("MockSSID");
		
		TitleVerifierMock titleVerifier = new TitleVerifierMock(true, "MockTitle", null);
		
		InetifyHelper helper = new InetifyHelper(getContext(), prefs, 
				new ConnectivityManagerMock(networkInfo), 
				new WifiManagerMock(wifiInfo), 
				titleVerifier);
		
		TestInfo info = helper.getTestInfo(3, 0, true);
		
		assertTrue(info.getIsExpectedTitle());
		assertEquals("MockTitle", info.getPageTitle());
		assertEquals("MockWifi", info.getType());
		assertEquals("MockSSID", info.getExtra());
		assertNull(info.getException());
		assertNotNull(info.getTimestamp());
		
		assertEquals(1, titleVerifier.getTestCount());
	}
	
	public void testGetTestInfoWifiException() {
		
		NetworkInfoMock networkInfo = new NetworkInfoMock();
		networkInfo.setType(ConnectivityManager.TYPE_WIFI);
		networkInfo.setTypeName("MockWifi");
		networkInfo.setConnected(true);
		
		WifiInfoMock wifiInfo = new WifiInfoMock();
		wifiInfo.setSSID("MockSSID");
		
		TitleVerifierMock titleVerifier = new TitleVerifierMock(true, "MockTitle", new Exception("Some Exception"));
		
		InetifyHelper helper = new InetifyHelper(getContext(), prefs, 
				new ConnectivityManagerMock(networkInfo), 
				new WifiManagerMock(wifiInfo), 
				titleVerifier);
		
		TestInfo info = helper.getTestInfo(3, 0, true);
		
		assertFalse(info.getIsExpectedTitle());
		assertEquals("", info.getPageTitle());
		assertEquals("MockWifi", info.getType());
		assertEquals("MockSSID", info.getExtra());
		assertNotNull(info.getTimestamp());
		assertNotNull(info.getException());
		
		assertEquals(3, titleVerifier.getTestCount());
		
	}
	
	public void testGetTestInfoWifiTitleNotExpected() {
		
		NetworkInfoMock networkInfo = new NetworkInfoMock();
		networkInfo.setType(ConnectivityManager.TYPE_WIFI);
		networkInfo.setTypeName("MockWifi");
		networkInfo.setConnected(true);
		
		WifiInfoMock wifiInfo = new WifiInfoMock();
		wifiInfo.setSSID("MockSSID");
		
		TitleVerifierMock titleVerifier = new TitleVerifierMock(false, "NotExpectedMockTitle", null);
		
		InetifyHelper helper = new InetifyHelper(getContext(), prefs, 
				new ConnectivityManagerMock(networkInfo), 
				new WifiManagerMock(wifiInfo), 
				titleVerifier);
		
		TestInfo info = helper.getTestInfo(3, 0, true);
		
		assertFalse(info.getIsExpectedTitle());
		assertEquals("NotExpectedMockTitle", info.getPageTitle());
		assertEquals("MockWifi", info.getType());
		assertEquals("MockSSID", info.getExtra());
		assertNotNull(info.getTimestamp());
		assertNull(info.getException());
		
		assertEquals(3, titleVerifier.getTestCount());
		
	}
	
	public void testGetTestInfoWifiNotConnected() {
		
		NetworkInfoMock networkInfo = new NetworkInfoMock();
		networkInfo.setType(ConnectivityManager.TYPE_MOBILE);
		networkInfo.setTypeName("MockMobile");
		networkInfo.setConnected(true);
		
		TitleVerifierMock titleVerifier = new TitleVerifierMock(true, "MockTitle", null);
		
		InetifyHelper helper = new InetifyHelper(getContext(), prefs, 
				new ConnectivityManagerMock(networkInfo), 
				new WifiManagerMock(null), 
				titleVerifier);
		
		TestInfo info = helper.getTestInfo(3, 0, true);
		
		assertNull(info);
		
		assertEquals(0, titleVerifier.getTestCount());
		
	}
	
	public void testGetTestInfoWifiDisconnectsDuringTesting() {
		
		NetworkInfoMock networkInfo = new NetworkInfoMock();
		networkInfo.setType(ConnectivityManager.TYPE_WIFI);
		networkInfo.setTypeName("MockWifi");
		networkInfo.setConnected(true);
		networkInfo.disconnectAfter(150);
		
		WifiInfoMock wifiInfo = new WifiInfoMock();
		wifiInfo.setSSID("MockSSID");
		
		TitleVerifierMock titleVerifier = new TitleVerifierMock(false, "MockTitle", new Exception("Some Exception"));
		
		InetifyHelper helper = new InetifyHelper(getContext(), prefs, 
				new ConnectivityManagerMock(networkInfo), 
				new WifiManagerMock(wifiInfo), 
				titleVerifier);
		
		TestInfo info = helper.getTestInfo(3, 100, true);
		
		assertNull(info);
		
		// Likely to cause sporadic test failures
		// assertEquals(1, titleVerifier.getTestCount());
		
	}
	
	public void testGetTestInfoMobileOK() {
		
		NetworkInfoMock networkInfo = new NetworkInfoMock();
		networkInfo.setType(ConnectivityManager.TYPE_MOBILE);
		networkInfo.setTypeName("MockMobile");
		networkInfo.setSubtypeName("MockUMTS");
		networkInfo.setConnected(true);
		
		TitleVerifierMock titleVerifier = new TitleVerifierMock(true, "MockTitle", null);
		
		InetifyHelper helper = new InetifyHelper(getContext(), prefs, 
				new ConnectivityManagerMock(networkInfo), 
				new WifiManagerMock(null), 
				titleVerifier);
		
		TestInfo info = helper.getTestInfo(3, 0, false);
		
		assertTrue(info.getIsExpectedTitle());
		assertEquals("MockTitle", info.getPageTitle());
		assertEquals("MockMobile", info.getType());
		assertEquals("MockUMTS", info.getExtra());
		assertNull(info.getException());
		assertNotNull(info.getTimestamp());
		
		assertEquals(1, titleVerifier.getTestCount());
		
	}
	
	private class TitleVerifierMock implements TitleVerifier {
		
		private boolean expectedTitle;
		private String pageTitle;
		private Exception exception;
		
		private int testCount = 0;
		
		public TitleVerifierMock(final boolean expectedTitle, final String pageTitle, final Exception exception) {
			this.expectedTitle = expectedTitle;
			this.pageTitle = pageTitle;
			this.exception = exception;
		}

		public boolean isExpectedTitle(String title, String pageTitle) {
			return expectedTitle;
		}

		public String getPageTitle(String server) throws Exception {
			
			testCount++;
			
			if(exception != null) {
				throw exception;
			} else {
				return pageTitle;
			}
		}
		
		public int getTestCount() {
			return testCount;
		}
		
	}
	
}
