package net.luniks.android.inetify;

import java.io.IOException;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.TextView.BufferType;

public class Inetify extends Activity {
	
	public static final String LOG_TAG = "Inetify";
	
	private static final int REQUEST_CODE_PREFERENCES = 1;
	
	private SharedPreferences sharedPreferences;

	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		PreferenceManager.setDefaultValues(this, R.xml.settings, false);
		sharedPreferences = PreferenceManager.getDefaultSharedPreferences(Inetify.this.getApplicationContext());
		
		this.setContentView(R.layout.main);
	}

	@Override
	public boolean onCreateOptionsMenu(final Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(final MenuItem item) {
		switch (item.getItemId()) {
		case R.id.refresh:
			new TestTask().execute(new Void[0]);
			return true;

		case R.id.settings:
			Intent launchPreferencesIntent = new Intent().setClass(this, Settings.class);
			startActivityForResult(launchPreferencesIntent, REQUEST_CODE_PREFERENCES);
			return true;

		default:
			break;
		}

		return false;
	}

	@Override
	protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == REQUEST_CODE_PREFERENCES) {
			// Do something when settings were saved?
		}
	}
	
	private TestInfo test() {
		ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(CONNECTIVITY_SERVICE);
		WifiManager wifiManager =  (WifiManager)getSystemService(Context.WIFI_SERVICE);
		NetworkInfo networkInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		WifiInfo wifiInfo = wifiManager.getConnectionInfo();
		
		String server = sharedPreferences.getString("settings_server", null);
		String title = sharedPreferences.getString("settings_title", null);
		
		boolean isWifiConnected = networkInfo.isConnected();
		String wifiSSID = wifiInfo.getSSID();
		if(! isWifiConnected) {
			wifiSSID = "Not connected";
		}

		try {
			String pageTitle = ConnectivityUtil.getPageTitle(server);
			boolean isExpectedTitle = ConnectivityUtil.isExpectedTitle(title, pageTitle);
			
			TestInfo info = new TestInfo();
			info.setWifiConnected(isWifiConnected);
			info.setWifiSSID(wifiSSID);
			info.setSite(server);
			info.setTitle(title);
			info.setPageTitle(pageTitle);
			info.setExpectedTitle(isExpectedTitle);
			
			return info;
			
		} catch (IOException e) {
			return new TestInfo(e);
		}
		
	}
	
	private void showTestInfo(final TestInfo info) {
		TableLayout tableLayoutInfo = (TableLayout)findViewById(R.id.tableLayoutInfo);
		
		TableRow tableRowWifi = (TableRow)View.inflate(this, R.layout.tablerow_info, null);
		TextView textViewInfoPropertyWifi = (TextView)tableRowWifi.findViewById(R.id.textview_info_property);
		textViewInfoPropertyWifi.setText("Wifi Connection:", BufferType.NORMAL);
		TextView textViewInfoValueWifi = (TextView)tableRowWifi.findViewById(R.id.textview_info_value);
		textViewInfoValueWifi.setText(info.getWifiSSID(), BufferType.NORMAL);
		tableLayoutInfo.addView(tableRowWifi, new TableLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		
		TableRow tableRowSite = (TableRow)View.inflate(this, R.layout.tablerow_info, null);
		TextView textViewInfoPropertySite = (TextView)tableRowSite.findViewById(R.id.textview_info_property);
		textViewInfoPropertySite.setText("Intenet Site:", BufferType.NORMAL);
		TextView textViewInfoValueSite = (TextView)tableRowSite.findViewById(R.id.textview_info_value);
		textViewInfoValueSite.setText(info.getSite(), BufferType.NORMAL);
		tableLayoutInfo.addView(tableRowSite, new TableLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		
		TableRow tableRowPageTitle = (TableRow)View.inflate(this, R.layout.tablerow_info, null);
		TextView textViewInfoPropertyPageTitle = (TextView)tableRowPageTitle.findViewById(R.id.textview_info_property);
		textViewInfoPropertyPageTitle.setText("Page Title:", BufferType.NORMAL);
		TextView textViewInfoValuePageTitle = (TextView)tableRowPageTitle.findViewById(R.id.textview_info_value);
		textViewInfoValuePageTitle.setText(info.getPageTitle(), BufferType.NORMAL);
		tableLayoutInfo.addView(tableRowPageTitle, new TableLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		
		TableRow tableRowInternet = (TableRow)View.inflate(this, R.layout.tablerow_info, null);
		TextView textViewInfoPropertyInternet = (TextView)tableRowInternet.findViewById(R.id.textview_info_property);
		textViewInfoPropertyInternet.setText("Internet Connectivity:", BufferType.NORMAL);
		TextView textViewInfoValueInternet = (TextView)tableRowInternet.findViewById(R.id.textview_info_value);
		textViewInfoValueInternet.setText(getInternetConnectivityString(info.isExpectedTitle()), BufferType.NORMAL);
		tableLayoutInfo.addView(tableRowInternet, new TableLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		
		TableRow tableRowInfoNotification = (TableRow)View.inflate(this, R.layout.tablerow_info_notification, null);
		ImageView imageViewInfoNotification = (ImageView)tableRowInfoNotification.findViewById(R.id.imageview_info_notification);
		if(info.isExpectedTitle()) {
			imageViewInfoNotification.setImageResource(R.drawable.notification_ok);
		} else {
			imageViewInfoNotification.setImageResource(R.drawable.notification_nok);
		}
		tableLayoutInfo.addView(tableRowInfoNotification, new TableLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
				
	}
	
	private String getInternetConnectivityString(final boolean ok) {
		if(ok) {
			return "Seems OK!";
		} else {
			return "Seems not OK!";
		}
	}
	
    private class TestTask extends AsyncTask<Void, Void, TestInfo> {
    	
    	ProgressDialog dialog = ProgressDialog.show(Inetify.this, "", "Testing, please wait...", true);

		@Override
		protected void onPreExecute() {
			dialog.show();
		}

		@Override
		protected TestInfo doInBackground(Void... arg) {
			return test();
		}
		
		@Override
	    protected void onPostExecute(TestInfo info) {
			dialog.cancel();
			showTestInfo(info);
	    }
		
    }
	
}
