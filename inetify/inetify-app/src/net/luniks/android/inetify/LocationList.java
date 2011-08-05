package net.luniks.android.inetify;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import net.luniks.android.impl.LocationManagerImpl;
import net.luniks.android.impl.WifiManagerImpl;
import net.luniks.android.inetify.Dialogs.InputDialog;
import net.luniks.android.interfaces.ILocationManager;
import net.luniks.android.interfaces.IWifiInfo;
import net.luniks.android.interfaces.IWifiManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ListActivity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationManager;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;
import android.widget.TwoLineListItem;

/**
 * Activity that shows the list of Wifi locations and allows to
 * show locations on a Google map and rename or delete single entries.
 * 
 * @author torsten.roemer@luniks.net
 */
public class LocationList extends ListActivity {
	
	/** Extra to pass the name with the intent */
	public static final String EXTRA_NAME = "net.luniks.android.inetify.EXTRA_NAME";
	
	/** Extra to pass the location with the intent */
	public static final String EXTRA_LOCATION = "net.luniks.android.inetify.EXTRA_LOCATION";
	
	/** Action to add the location to the list */
	public static final String ADD_LOCATION_ACTION = "net.luniks.android.inetify.ADD_LOCATION";
	
	/** Id of the header view */
	private static final int ID_HEADER_VIEW = 0;
	
	/** Id of the context dialog */
	private static final int ID_CONTEXT_DIALOG = 0;
	
	/** Id of the rename dialog */
	private static final int ID_RENAME_DIALOG = 1;
	
	/** Id of the confirm delete dialog */
	private static final int ID_CONFIRM_DELETE_DIALOG = 2;
	
	/** Key to save the instance state of the bssid of the selected Wifi location */
	private static final String STATE_BUNDLE_KEY_SELECTED_BSSID = "selectedBSSID";

	/** Key to save the instance state of the name of the selected Wifi location */
	private static final String STATE_BUNDLE_KEY_SELECTED_NAME = "selectedName";
	
	/** Wifi connection state */
	private final AtomicBoolean wifiConnected = new AtomicBoolean(false); 
	
	/** Database adapter */
	private DatabaseAdapter databaseAdapter;
	
	/** Wifi manager */
	private IWifiManager wifiManager;
	
	/** Location manager */
	private ILocationManager locationManager;
	
	/** Broadcast receiver for CONNECTIVITY_ACTION */
	private WifiStateReceiver wifiActionReceiver;
	
	/** Broadcast receiver for ADD_LOCATION_ACTION */
	private AddLocationReceiver addLocationReceiver;
	
	// This would not be necessary with onCreateDialog(int, Bundle) in API 8...
	/** BSSID of the selected Wifi location */
	private String selectedBSSID = null;
	
	/** Name of the selected Wifi location */
	private String selectedName = null;
	
	// TODO Is there some way to get a reference to the "current" dialog?
	/** For testing only, read using reflection */
	@SuppressWarnings("unused")
	private volatile Dialog currentDialog = null;
	
	/**
	 * Creates the activity.
	 */
	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.locationlist);
		
		TwoLineListItem headerView = (TwoLineListItem)View.inflate(this, android.R.layout.simple_list_item_2, null);
		headerView.setId(ID_HEADER_VIEW);
		headerView.setEnabled(false);
		headerView.getText1().setEnabled(false);
		headerView.getText1().setText(this.getString(R.string.locationlist_add_wifi_location));
		headerView.getText2().setText(this.getString(R.string.wifi_status_unknown));
		this.getListView().addHeaderView(headerView);
		
		databaseAdapter = new DatabaseAdapterImpl(this);
		wifiManager = new WifiManagerImpl((WifiManager)getSystemService(WIFI_SERVICE));
		locationManager = new LocationManagerImpl((LocationManager)getSystemService(LOCATION_SERVICE));
		
		this.getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
			public void onItemClick(final AdapterView<?> parent, final View view, final int position, final long id) {
				
				if(position == 0) {
					findLocation();
				} else {
					Cursor cursor = (Cursor)LocationList.this.getListAdapter().getItem(position);
					cursor.moveToPosition(position - 1);
					
					final String name = cursor.getString(3);
					final double lat = cursor.getDouble(4);
					final double lon = cursor.getDouble(5);
					final float acc = cursor.getFloat(6);
					
					Location location = new Location(Locater.PROVIDER_DATABASE);
					location.setLatitude(lat);
					location.setLongitude(lon);
					location.setAccuracy(acc);
					
					showLocation(name, location);
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
					
					selectedBSSID = cursor.getString(1);
					selectedName = cursor.getString(3);
					
					LocationList.this.showDialog(ID_CONTEXT_DIALOG);
				}
				return true;
			}
		});
		
        IntentFilter filter = new IntentFilter(ADD_LOCATION_ACTION);
        addLocationReceiver = new AddLocationReceiver();
        this.registerReceiver(addLocationReceiver, filter);
		
		listLocations();
	}

	/**
	 * Creates the dialogs managed by this activity.
	 */
	@Override
	protected Dialog onCreateDialog(final int id) {
		Dialog dialog = super.onCreateDialog(id);
		if(id == ID_CONTEXT_DIALOG) {
			final String rename = getString(R.string.locationlist_context_rename);
			final String delete = getString(R.string.locationlist_context_delete);
			CharSequence[] items = new CharSequence[] {rename, delete};
			DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
			    public void onClick(final DialogInterface dialog, final int item) {
			        if(item == 0) {
			        	LocationList.this.showDialog(ID_RENAME_DIALOG);
			        	LocationList.this.dismissDialog(id);
			        } else if(item == 1) {
			        	LocationList.this.showDialog(ID_CONFIRM_DELETE_DIALOG);
			        	LocationList.this.dismissDialog(id);
			        }
			    }
			};
			dialog = Dialogs.createContextDialog(this, id, items, listener);
		}
		else if(id == ID_RENAME_DIALOG) {
			DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
				public void onClick(final DialogInterface dialog, final int whichButton) {
					InputDialog inputDialog = (InputDialog)dialog;
					renameLocation(selectedBSSID, inputDialog.getInputText());
				}
			};
			final String message = getString(R.string.locationlist_input_rename);
			dialog = Dialogs.createInputDialog(this, id, message, listener);
		}
		else if(id == ID_CONFIRM_DELETE_DIALOG) {
			DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
			    public void onClick(final DialogInterface dialog, final int whichButton) {
					deleteLocation(selectedBSSID);
					dismissDialog(id);
			    }
			};
			final String message = getString(R.string.locationlist_confirm_delete);
			dialog = Dialogs.createConfirmDialog(this, id, message, listener);
		}
		this.currentDialog = dialog;
		return dialog;
	}
	
	/**
	 * Prepares the dialogs managed by this activity before they are shown.
	 */
	@Override
	protected void onPrepareDialog(final int id, final Dialog dialog) {
		AlertDialog alertDialog = (AlertDialog)dialog;
		alertDialog.setTitle(selectedName);
	}

	/**
	 * Closes the database and unregisters the broadcast receiver.
	 */
	@Override
	protected void onDestroy() {
		databaseAdapter.close();
		this.unregisterReceiver(addLocationReceiver);
		currentDialog = null;
		super.onDestroy();
	}

	/**
	 * Registers WifiStateReceiver and updates the header view accordingly
	 * when the activity becomes visible.
	 */
	@Override
	protected void onResume() {
		super.onResume();
		
		WifiStateReceiver.WifiStateListener listener = new WifiStateReceiver.WifiStateListener() {
			public void onWifiStateChanged(final boolean connected) {
				wifiConnected.set(connected);
				updateHeaderView();
			}
		};
		IntentFilter filter = new IntentFilter(WifiManager.NETWORK_STATE_CHANGED_ACTION);
		wifiActionReceiver = new WifiStateReceiver(listener);
		Intent sticky = this.registerReceiver(wifiActionReceiver, filter);
		if(sticky == null) {
			wifiConnected.set(false);
			updateHeaderView();
		}
	}

	/**
	 * Unregisters WifiStateReceiver when the activity becomes invisible.
	 */
	@Override
	protected void onPause() {
		if(wifiActionReceiver != null) {
			this.unregisterReceiver(wifiActionReceiver);
		}
		super.onPause();
	}

	/**
	 * Restores some instance variables, like the name and BSSID of the
	 * selected Wifi location.
	 */
	@Override
	protected void onRestoreInstanceState(final Bundle state) {
		selectedBSSID = state.getString(STATE_BUNDLE_KEY_SELECTED_BSSID);
		selectedName = state.getString(STATE_BUNDLE_KEY_SELECTED_NAME);
		super.onRestoreInstanceState(state);
	}

	/**
	 * Saves some instance variables, like the name and BSSID of the
	 * selected Wifi location.
	 */
	@Override
	protected void onSaveInstanceState(final Bundle outState) {
		outState.putString(STATE_BUNDLE_KEY_SELECTED_BSSID, selectedBSSID);
		outState.putString(STATE_BUNDLE_KEY_SELECTED_NAME, selectedName);
		super.onSaveInstanceState(outState);
	}
	
	/**
	 * Updates the header view depending on the current Wifi connection status.
	 */
	private void updateHeaderView() {
		TwoLineListItem headerView = (TwoLineListItem)this.findViewById(ID_HEADER_VIEW);
		headerView.getText1().setText(this.getString(R.string.locationlist_add_wifi_location));
		IWifiInfo wifiInfo = wifiManager.getConnectionInfo();
		if(wifiConnected.get() && wifiInfoAvailable(wifiInfo)) {
			headerView.setEnabled(true);
			headerView.getText1().setEnabled(true);
			headerView.getText2().setText(getString(
					R.string.locationlist_add_location_of_wifi, 
					wifiInfo.getSSID()));
		} else {
			headerView.setEnabled(false);
			headerView.getText1().setEnabled(false);
			headerView.getText2().setText(getString(
					R.string.wifi_disconnected));
		}
	}

	/**
	 * Lists the Wifi locations in the database.
	 */
	private void listLocations() {		
        Cursor cursor = databaseAdapter.fetchLocations();
        startManagingCursor(cursor);
        
        SimpleCursorAdapter ignoredWifis = new SimpleCursorAdapter(this, android.R.layout.simple_list_item_2, cursor,
        		new String[] { DatabaseAdapterImpl.COLUMN_NAME, DatabaseAdapterImpl.COLUMN_BSSID },
				new int[] { android.R.id.text1, android.R.id.text2 });
        
        this.setListAdapter(ignoredWifis);
    }
	
	/**
	 * Adds the given location to the database.
	 * @param location
	 */
	private void addLocation(final Location location) {
		if(location == null) {
			return;
		}
		IWifiInfo wifiInfo = wifiManager.getConnectionInfo();
		if(wifiConnected.get() && wifiInfoAvailable(wifiInfo)) {
			databaseAdapter.addLocation(wifiInfo.getBSSID(), wifiInfo.getSSID(), null, location);
			String message = this.getString(R.string.locationlist_added_wifi_location, wifiInfo.getSSID());
			Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
			
			Log.d(Inetify.LOG_TAG, String.format("Added location for %s: %s", wifiInfo.getSSID(), location));
		} else {
			// Toast.makeText(this, R.string.wifi_disconnected, Toast.LENGTH_SHORT).show();
			String wifiDisconnected = this.getString(R.string.disconnected);
			long timestamp = System.currentTimeMillis();
			databaseAdapter.addLocation(Utils.getDateTimeString(this, timestamp), wifiDisconnected, null, location);
			
			Log.d(Inetify.LOG_TAG, String.format("Added location for %s: %s", wifiDisconnected, location));
		}
		listLocations();
	}
	
	/**
	 * Renames the Wifi location with the given BSSID to the given name.
	 * @param bssid
	 * @param name
	 */
	private void renameLocation(final String bssid, final String name) {
		databaseAdapter.renameLocation(bssid, name);
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
	 * Shows the Wifi location and its name on the LocationMapView.
	 * @param name
	 * @param location
	 */
	private void showLocation(final String name, final Location location) {		
		Intent intent = new Intent().setClass(this, LocationMapView.class);
		intent.setAction(LocationMapView.SHOW_LOCATION_ACTION);
		intent.putExtra(EXTRA_NAME, name);
		intent.putExtra(EXTRA_LOCATION, location);
		startActivity(intent);
	}
	
	/**
	 * Starts the LocationMapView activity to find the current location.
	 */
	private void findLocation() {
		
		if(! areAllProvidersEnabled()) {
			Toast.makeText(this, R.string.locationlist_not_all_providers_enabled, Toast.LENGTH_LONG).show();
		}
		
		Intent intent = new Intent().setClass(this, LocationMapView.class);
		intent.setAction(LocationMapView.FIND_LOCATION_ACTION);
		IWifiInfo wifiInfo = wifiManager.getConnectionInfo();
		if(wifiConnected.get() && wifiInfoAvailable(wifiInfo)) {
			intent.putExtra(EXTRA_NAME, wifiInfo.getSSID());
		}
		startActivity(intent);
	}

	/**
	 * Returns true if the given wifi info is not null and its BSSID
	 * and SSID are not null, false otherwise.
	 * @param wifiInfo
	 * @return boolean
	 */
	private boolean wifiInfoAvailable(final IWifiInfo wifiInfo) {
		return wifiInfo != null && wifiInfo.getBSSID() != null && wifiInfo.getSSID() != null;
	}
	
	/**
	 * Returns true if all providers are enabled, false otherwise.
	 * @return boolean
	 */
	public boolean areAllProvidersEnabled() {
		List<String> providers = locationManager.getAllProviders();
		for(String provider : providers) {
			if(! locationManager.isProviderEnabled(provider)) {
				return false;
			}
		}
		return true;
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
			if(intent != null && intent.getAction().equals(ADD_LOCATION_ACTION)) {
				Location location = intent.getParcelableExtra(EXTRA_LOCATION);
				addLocation(location);
			}
		}
		
	}

}
