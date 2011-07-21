package net.luniks.android.inetify;

import net.luniks.android.impl.ConnectivityManagerImpl;
import net.luniks.android.impl.WifiManagerImpl;
import net.luniks.android.interfaces.IWifiInfo;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ListActivity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;
import android.widget.TwoLineListItem;

/**
 * Activity that shows the list of Wifi locations and allows to
 * show locations on a Google map and delete single entries.
 * 
 * @author torsten.roemer@luniks.net
 */
public class LocationList extends ListActivity {
	
	/** Extra to pass the SSID with the intent */
	public static final String EXTRA_SSID = "net.luniks.android.inetify.EXTRA_SSID";
	
	/** Extra to pass the location with the intent */
	public static final String EXTRA_LOCATION = "net.luniks.android.inetify.EXTRA_LOCATION";
	
	/** Action to add the location to the list */
	public static final String ADD_LOCATION_ACTION = "net.luniks.android.inetify.ADD_LOCATION";
	
	/** Id of the header view */
	private static final int ID_HEADER_VIEW = 0;
	
	/** Id of the confirm delete dialog */
	private static final int ID_CONFIRM_DELETE_DIALOG = 0;
	
	/** Key to save the instance state of the bssid of the Wifi location to delete */
	private static final String STATE_BUNDLE_KEY_BSSID_TO_DELETE = "bssidToDelete";

	/** Key to save the instance state of the ssid of the Wifi location to delete */
	private static final String STATE_BUNDLE_KEY_SSID_TO_DELETE = "ssidToDelete";

	/** Provider used for locations coming from the database */
	private static final String PROVIDER_DATABASE = "database";
	
	/** Database adapter */
	private DatabaseAdapter databaseAdapter;
	
	/** Tester instance */
	private Tester tester;
	
	/** Broadcast receiver for CONNECTIVITY_ACTION */
	private PostingBroadcastReceiver wifiActionReceiver;
	
	/** Broadcast receiver for ADD_LOCATION_ACTION */
	private AddLocationReceiver addLocationReceiver;
	
	// This would not be necessary with onCreateDialog(int, Bundle) in API 8...
	/** BSSID of the Wifi location to delete */
	private String bssidToDelete = null;
	
	/** SSID of the Wifi location to delete */
	private String ssidToDelete = null;
	
	/**
	 * Hack to allow testing by skipping the confirmation dialog.
	 * TODO How to test dialogs?
	 */
	private boolean skipConfirmDeleteDialog = false;
	
	/**
	 * Hack to allow testing by skipping the confirmation dialog.
	 * TODO How to test dialogs?
	 */
	public void setSkipConfirmDeleteDialog(final boolean skipConfirmDeleteDialog) {
		this.skipConfirmDeleteDialog = skipConfirmDeleteDialog;
	}
	
	/**
	 * Sets the Tester implementation used by the activity - intended for unit tests.
	 * @param tester
	 */
	public void setTester(final Tester tester) {
		this.tester = tester;
	}
	
	/**
	 * Creates the activity.
	 */
	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.locationlist);
		
		TwoLineListItem headerView = (TwoLineListItem)View.inflate(this, android.R.layout.simple_list_item_2, null);
		headerView.setId(ID_HEADER_VIEW);
		this.getListView().addHeaderView(headerView);
		
		databaseAdapter = new DatabaseAdapterImpl(this);
		tester = new TesterImpl(this,
				new ConnectivityManagerImpl((ConnectivityManager)getSystemService(CONNECTIVITY_SERVICE)), 
				new WifiManagerImpl((WifiManager)getSystemService(WIFI_SERVICE)),
				null);
		
		this.getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
			public void onItemClick(final AdapterView<?> parent, final View view, final int position, final long id) {
				
				if(position == 0) {
					findLocation();
				} else {
					Cursor cursor = (Cursor)LocationList.this.getListAdapter().getItem(position);
					cursor.moveToPosition(position - 1);
					
					final String ssid = cursor.getString(2);
					final double lat = cursor.getDouble(3);
					final double lon = cursor.getDouble(4);
					final float acc = cursor.getFloat(5);
					
					Location location = new Location(PROVIDER_DATABASE);
					location.setLatitude(lat);
					location.setLongitude(lon);
					location.setAccuracy(acc);
					
					showLocation(ssid, location);
				}
			}
		});
		
		this.getListView().setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
			public boolean onItemLongClick(final AdapterView<?> parent, final View view, final int position, final long id) {
				
				if(position == 0) {
					// Do nothing
				} else {
					Cursor cursor = (Cursor)LocationList.this.getListAdapter().getItem(position);
					cursor.moveToPosition(position - 1);
					
					bssidToDelete = cursor.getString(1);
					ssidToDelete = cursor.getString(2);
					
					// TODO How to test dialogs?
					if(skipConfirmDeleteDialog) {
						deleteLocation(bssidToDelete);
					} else {
						LocationList.this.showDialog(ID_CONFIRM_DELETE_DIALOG);
					}
				}
				return true;
			}
		});
		
        IntentFilter filter = new IntentFilter(ADD_LOCATION_ACTION);
        addLocationReceiver = new AddLocationReceiver();
        this.registerReceiver(addLocationReceiver, filter);
        Log.d(Inetify.LOG_TAG, "registerReceiver");
		
		listLocations();
	}

	/**
	 * Creates the dialogs managed by this activity.
	 */
	@Override
	protected Dialog onCreateDialog(final int id) {
		if(id == ID_CONFIRM_DELETE_DIALOG) {
			return Dialogs.createConfirmDeleteDialog(this, ID_CONFIRM_DELETE_DIALOG, "");
		}
		return super.onCreateDialog(id);
	}
	
	/**
	 * Prepares the dialogs managed by this activity before they are shown.
	 */
	@Override
	protected void onPrepareDialog(final int id, final Dialog dialog) {
		if(id == ID_CONFIRM_DELETE_DIALOG) {
			final String message = getString(R.string.locationlist_confirm_delete, ssidToDelete);
			AlertDialog alertDialog = (AlertDialog)dialog;
			alertDialog.setMessage(message);
			alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
				public void onClick(final View view) {
					deleteLocation(bssidToDelete);
					dismissDialog(ID_CONFIRM_DELETE_DIALOG);
				}
			});
		}
	}

	/**
	 * Closes the database.
	 */
	@Override
	protected void onDestroy() {
		databaseAdapter.close();
		this.unregisterReceiver(addLocationReceiver);
		super.onDestroy();
	}

	/**
	 * Registers to receive CONNECTIVITY_ACTION broadcast intents and updates
	 * the header view accordingly when the activity becomes visible.
	 */
	@Override
	protected void onResume() {
		super.onResume();
		
		Runnable runnable = new Runnable() {
			public void run() {
				updateHeaderView();
			}
		};
		IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
		wifiActionReceiver = new PostingBroadcastReceiver(runnable, new Handler());
		Intent sticky = this.registerReceiver(wifiActionReceiver, filter);
		if(sticky == null) {
			updateHeaderView();
		}
	}

	/**
	 * Unregisters from CONNECTIVITY_ACTION broadcast intents when the activity becomes
	 * invisible.
	 */
	@Override
	protected void onPause() {
		if(wifiActionReceiver != null) {
			this.unregisterReceiver(wifiActionReceiver);
		}
		super.onPause();
	}

	/**
	 * Restores some instance variables, like the SSID and BSSID of the
	 * Wifi location to be deleted.
	 * TODO There should be something smarter than this?
	 */
	@Override
	protected void onRestoreInstanceState(final Bundle state) {
		bssidToDelete = state.getString(STATE_BUNDLE_KEY_BSSID_TO_DELETE);
		ssidToDelete = state.getString(STATE_BUNDLE_KEY_SSID_TO_DELETE);
		super.onRestoreInstanceState(state);
	}

	/**
	 * Saves some instance variables, like the SSID and BSSID of the
	 * Wifi location to be deleted.
	 * TODO There should be something smarter than this?
	 */
	@Override
	protected void onSaveInstanceState(final Bundle outState) {
		outState.putString(STATE_BUNDLE_KEY_BSSID_TO_DELETE, bssidToDelete);
		outState.putString(STATE_BUNDLE_KEY_SSID_TO_DELETE, ssidToDelete);
		super.onSaveInstanceState(outState);
	}
	
	/**
	 * Updates the header view with the Wifi network currently connected or
	 * Wifi not connected.
	 */
	private void updateHeaderView() {
		TwoLineListItem headerView = (TwoLineListItem)this.findViewById(ID_HEADER_VIEW);
		headerView.getText1().setText(this.getString(R.string.locationlist_add_wifi_location));
		if(tester.isWifiConnected()) {
			headerView.setEnabled(true);
			headerView.getText1().setEnabled(true);
			headerView.getText2().setText(getString(
					R.string.locationlist_add_location_of_wifi, 
					tester.getWifiInfo().getSSID()));
		} else {
			headerView.setEnabled(false);
			headerView.getText1().setEnabled(false);
			headerView.getText2().setText(getString(
					R.string.locationlist_wifi_disconnected));
		}
	}

	/**
	 * Lists the Wifi locations in the database.
	 */
	private void listLocations() {		
        Cursor cursor = databaseAdapter.fetchLocations();
        startManagingCursor(cursor);
        
        SimpleCursorAdapter ignoredWifis = new SimpleCursorAdapter(this, android.R.layout.simple_list_item_2, cursor,
        		new String[] { DatabaseAdapterImpl.COLUMN_SSID, DatabaseAdapterImpl.COLUMN_BSSID },
				new int[] { android.R.id.text1, android.R.id.text2 });
        
        this.setListAdapter(ignoredWifis);
    }
	
	/**
	 * Adds the given location to the database.
	 * @param location
	 */
	private void addLocation(final Location location) {
		if(tester.isWifiConnected()) {
			IWifiInfo wifiInfo = tester.getWifiInfo();
			databaseAdapter.addLocation(wifiInfo.getBSSID(), wifiInfo.getSSID(), location);
			String message = this.getString(R.string.locationlist_added_wifi_location, wifiInfo.getSSID());
			Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
			
			Log.d(Inetify.LOG_TAG, String.format("Added location for %s: %s", wifiInfo.getSSID(), location));
		} else {
			// Toast.makeText(this, R.string.locationlist_wifi_disconnected, Toast.LENGTH_SHORT).show();
			String wifiDisconnected = this.getString(R.string.locationlist_disconnected);
			databaseAdapter.addLocation(wifiDisconnected, wifiDisconnected, location);
			
			Log.d(Inetify.LOG_TAG, String.format("Added location for %s: %s", wifiDisconnected, location));
		}
		listLocations();
	}
	
	/**
	 * Deletes the Wifi location with the given BSSID from the database.
	 * @param bssid
	 */
	private void deleteLocation(final String bssid) {
		databaseAdapter.deleteLocation(bssid);
		listLocations();
	}
	
	/**
	 * Shows the Wifi location and its SSID on the LocationMapView.
	 * @param ssid
	 * @param location
	 */
	private void showLocation(final String ssid, final Location location) {		
		Intent intent = new Intent().setClass(this, LocationMapView.class);
		intent.setAction(LocationMapView.SHOW_LOCATION_ACTION);
		intent.putExtra(EXTRA_SSID, ssid);
		intent.putExtra(EXTRA_LOCATION, location);
		startActivity(intent);
	}
	
	/**
	 * Starts the LocationMapView activity to find the current location.
	 */
	private void findLocation() {
		Intent intent = new Intent().setClass(this, LocationMapView.class);
		intent.setAction(LocationMapView.FIND_LOCATION_ACTION);
		startActivity(intent);
	}
	
	/**
	 * BroadcastReceiver to listen for intents from the LocationMapView,
	 * telling it to add the location in the intent to the database.
	 * 
	 * @author torsten.roemer@luniks.net
	 */
	private class AddLocationReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(final Context context, final Intent intent) {
			if(intent.getAction().equals(ADD_LOCATION_ACTION)) {
				if(intent != null && intent.hasExtra(EXTRA_LOCATION)) {
					Location location = intent.getParcelableExtra(EXTRA_LOCATION);
					addLocation(location);
				}
			}
		}
		
	}

}
