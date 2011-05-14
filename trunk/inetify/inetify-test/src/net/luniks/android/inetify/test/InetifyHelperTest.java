package net.luniks.android.inetify.test;

import net.luniks.android.inetify.InetifyHelper;
import net.luniks.android.inetify.TitleVerifier;
import net.luniks.android.inetify.TitleVerifierImpl;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.test.AndroidTestCase;
import android.test.mock.MockContext;

public class InetifyHelperTest extends AndroidTestCase {
	
	public void testIsWifiConnectedTrue() {
		
		Context context = this.getContext();
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		TitleVerifier verifier = new TitleVerifierImpl();
		
		InetifyHelper helper = new InetifyHelper(context, prefs, verifier);
		
		boolean isWifiConnected = helper.isWifiConnected();
		
		// FIXME How to get isWifiConnected == true?
		// assertTrue(isWifiConnected);
	}
	
	public void testIsWifiConnectedFalse() {
		
		Context context = this.getContext();
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		TitleVerifier verifier = new TitleVerifierImpl();
		
		InetifyHelper helper = new InetifyHelper(context, prefs, verifier);
		
		boolean isWifiConnected = helper.isWifiConnected();
		
		assertFalse(isWifiConnected);
	}
	
	private class TestContext extends MockContext {
		
		private final Context context;
		
		public TestContext(final Context context) {
			this.context = context;
		}

		@Override
		public String getPackageName() {
			return context.getPackageName();
		}

		@Override
		public SharedPreferences getSharedPreferences(String name, int mode) {
			return context.getSharedPreferences(name, mode);
		}

		@Override
		public Object getSystemService(String name) {
			if(name.equals(CONNECTIVITY_SERVICE)) {
				// return ConnectivityManager
				return null;
			} else if(name.equals(WIFI_SERVICE)) {
				// return WifiManager
				return null;
			} else {
				return context.getSystemService(name);
			}
		}
		
	}

}
