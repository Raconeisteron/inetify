package net.luniks.android.inetify;

import net.luniks.android.impl.ConnectivityManagerImpl;
import net.luniks.android.impl.WifiManagerImpl;
import net.luniks.android.interfaces.IWifiInfo;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ListActivity;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
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
	
	/** Tester instance */
	private Tester tester;
	
	/** Broadcast receiver for CONNECTIVITY_ACTION */
	private PostingBroadcastReceiver wifiActionReceiver;
	
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
		setContentView(R.layout.ignorelist);
		
		TwoLineListItem headerView = (TwoLineListItem)View.inflate(this, android.R.layout.simple_list_item_2, null);
		headerView.getText1().setText(this.getString(R.string.ignorelist_add_ignored_wifi));
		headerView.getText2().setText(this.getString(R.string.wifi_status_unknown));
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
	 * Registers to receive WifiManager.NETWORK_STATE_CHANGED_ACTION broadcast intents
	 * and updates the header view accordingly when the activity becomes visible.
	 */
	@Override
	protected void onResume() {
		super.onResume();
		
		Runnable runnable = new Runnable() {
			public void run() {
				updateHeaderView();
			}
		};
		IntentFilter filter = new IntentFilter(WifiManager.NETWORK_STATE_CHANGED_ACTION);
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
	 * Updates the header view with the Wifi network currently connected or
	 * Wifi not connected.
	 */
	private void updateHeaderView() {
		TwoLineListItem headerView = (TwoLineListItem)this.findViewById(ID_HEADER_VIEW);
		headerView.getText1().setText(this.getString(R.string.ignorelist_add_ignored_wifi));
		if(tester.isWifiConnected()) {			
			headerView.setEnabled(true);
			headerView.getText1().setEnabled(true);
			headerView.getText2().setText(getString(
					R.string.ignorelist_ignore_wifi, 
					tester.getWifiInfo().getSSID()));
		} else {
			headerView.setEnabled(false);
			headerView.getText1().setEnabled(false);
			headerView.getText2().setText(getString(
					R.string.ignorelist_wifi_disconnected));
		}
	}
	
	/**
	 * Adds the current Wifi connection to the list of ignored Wifi networks.
	 */
	private void addIgnoredWifi() {
		if(tester.isWifiConnected()) {
			IWifiInfo wifiInfo = tester.getWifiInfo();
			databaseAdapter.addIgnoredWifi(wifiInfo.getBSSID(), wifiInfo.getSSID());
			listIgnoredWifis();
		} else {
			Dialogs.createOKDialog(this, 0,
					this.getString(R.string.ignorelist_ignore), 
					this.getString(R.string.ignorelist_wifi_disconnected));
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

}
