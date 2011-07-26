package net.luniks.android.inetify;

import net.luniks.android.impl.WifiManagerImpl;
import net.luniks.android.interfaces.IWifiInfo;
import net.luniks.android.interfaces.IWifiManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ListActivity;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.SimpleCursorAdapter;
import android.widget.TwoLineListItem;

/**
 * Activity that shows the list of ignored Wifi networks and allows to
 * delete single entries.
 * 
 * @author torsten.roemer@luniks.net
 */
public class IgnoreList extends ListActivity {
	
	/** Id of the header view */
	private static final int ID_HEADER_VIEW = 0;
	
	/** Id of the confirm delete dialog */
	private static final int ID_CONFIRM_DELETE_DIALOG = 0;

	/** Key to save the instance state of the ssid of the ignored Wifi to delete */
	private static final String STATE_BUNDLE_KEY_SSID_TO_DELETE = "ssidToDelete";
	
	/** Database adapter */
	private DatabaseAdapter databaseAdapter;
	
	/** Wifi manager */
	private IWifiManager wifiManager;
	
	/** Broadcast receiver for CONNECTIVITY_ACTION */
	private WifiStateReceiver wifiActionReceiver;
	
	// This would not be necessary with onCreateDialog(int, Bundle) in API 8...	
	/** SSID of the ignored Wifi to delete */
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
	 * Sets the Wifi manager implementation used by the activity - intended for unit tests.
	 * @param wifiManager
	 */
	public void setWifiManager(final IWifiManager wifiManager) {
		this.wifiManager = wifiManager;
	}

	/**
	 * Creates the activity.
	 */
	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ignorelist);
		
		TwoLineListItem headerView = (TwoLineListItem)View.inflate(this, android.R.layout.simple_list_item_2, null);
		headerView.setId(ID_HEADER_VIEW);
		headerView.setEnabled(false);
		headerView.getText1().setEnabled(false);
		headerView.getText1().setText(this.getString(R.string.ignorelist_add_ignored_wifi));
		headerView.getText2().setText(this.getString(R.string.wifi_status_unknown));
		this.getListView().addHeaderView(headerView);
		
		databaseAdapter = new DatabaseAdapterImpl(this);
		wifiManager = new WifiManagerImpl((WifiManager)getSystemService(WIFI_SERVICE));
		
		this.getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
			public void onItemClick(final AdapterView<?> parent, final View view, final int position, final long id) {
				
				if(position == 0) {
					addIgnoredWifi();
				}
			}
		});
		
		this.getListView().setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
			public boolean onItemLongClick(final AdapterView<?> parent, final View view, final int position, final long id) {
								
				if(position == 0) {
					// Do nothing
				} else {
					Cursor cursor = (Cursor)IgnoreList.this.getListAdapter().getItem(position);
					cursor.moveToPosition(position - 1);
					
					ssidToDelete = cursor.getString(2);
					
					// TODO How to test dialogs?
					if(skipConfirmDeleteDialog) {
						deleteIgnoredWifi(ssidToDelete);
					} else {
						IgnoreList.this.showDialog(ID_CONFIRM_DELETE_DIALOG);
					}
				}
				return true;
			}
		});
		
		listIgnoredWifis();
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
			final String message = getString(R.string.ignorelist_confirm_delete, ssidToDelete);
			AlertDialog alertDialog = (AlertDialog)dialog;
			alertDialog.setMessage(message);
			alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
				public void onClick(final View view) {
					deleteIgnoredWifi(ssidToDelete);
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
				updateHeaderView(connected);
			}
		};
		IntentFilter filter = new IntentFilter(WifiManager.NETWORK_STATE_CHANGED_ACTION);
		wifiActionReceiver = new WifiStateReceiver(listener);
		Intent sticky = this.registerReceiver(wifiActionReceiver, filter);
		if(sticky == null) {
			updateHeaderView(false);
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
	 * Restores some instance variables, like the SSID of the
	 * ignored Wifi to be deleted.
	 * TODO There should be something smarter than this?
	 */
	@Override
	protected void onRestoreInstanceState(final Bundle state) {
		ssidToDelete = state.getString(STATE_BUNDLE_KEY_SSID_TO_DELETE);
		super.onRestoreInstanceState(state);
	}

	/**
	 * Saves some instance variables, like the SSID of the
	 * ignored Wifi to be deleted.
	 * TODO There should be something smarter than this?
	 */
	@Override
	protected void onSaveInstanceState(final Bundle outState) {
		outState.putString(STATE_BUNDLE_KEY_SSID_TO_DELETE, ssidToDelete);
		super.onSaveInstanceState(outState);
	}
	
	/**
	 * Updates the header view depending on the given Wifi connection status.
	 * @param connected
	 */
	private void updateHeaderView(final boolean connected) {
		TwoLineListItem headerView = (TwoLineListItem)this.findViewById(ID_HEADER_VIEW);
		headerView.getText1().setText(this.getString(R.string.ignorelist_add_ignored_wifi));
		IWifiInfo wifiInfo = wifiManager.getConnectionInfo();
		if(connected && wifiInfoAvailable(wifiInfo)) {
			headerView.setEnabled(true);
			headerView.getText1().setEnabled(true);
			headerView.getText2().setText(getString(
					R.string.ignorelist_ignore_wifi, 
					wifiInfo.getSSID()));
		} else {
			headerView.setEnabled(false);
			headerView.getText1().setEnabled(false);
			headerView.getText2().setText(getString(
					R.string.wifi_disconnected));
		}
	}
	
	/**
	 * Lists the ignored Wifi networks in the database.
	 */
	private void listIgnoredWifis() {		
        Cursor cursor = databaseAdapter.fetchIgnoredWifis();
        startManagingCursor(cursor);
        
        SimpleCursorAdapter ignoredWifis = new SimpleCursorAdapter(this, android.R.layout.simple_list_item_2, cursor, 
        		new String[] { DatabaseAdapterImpl.COLUMN_SSID, DatabaseAdapterImpl.COLUMN_BSSID },
				new int[] { android.R.id.text1, android.R.id.text2 });
        
        setListAdapter(ignoredWifis);
    }
	
	/**
	 * Adds the current Wifi connection to the list of ignored Wifi networks if
	 * wifi info is available.
	 */
	private void addIgnoredWifi() {
		IWifiInfo wifiInfo = wifiManager.getConnectionInfo();
		if(wifiInfoAvailable(wifiInfo)) {
			databaseAdapter.addIgnoredWifi(wifiInfo.getBSSID(), wifiInfo.getSSID());
			listIgnoredWifis();
		}
	}
	
	/**
	 * Deletes the ignored Wifi with the given SSID from the database.
	 * @param ssid
	 */
	private void deleteIgnoredWifi(final String ssid) {
		databaseAdapter.deleteIgnoredWifi(ssid);
		listIgnoredWifis();
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

}
