package net.luniks.android.inetify;

import net.luniks.android.impl.ConnectivityManagerImpl;
import net.luniks.android.impl.WifiManagerImpl;
import net.luniks.android.interfaces.IWifiInfo;
import android.app.ListActivity;
import android.database.Cursor;
import android.net.ConnectivityManager;
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
	
	/** Database adapter */
	private DatabaseAdapter databaseAdapter;
	
	/** Tester instance */
	private Tester tester;
	
	/**
	 * Hack to allow testing by skipping the confirmation dialog.
	 * TODO How to test dialogs?
	 */
	private boolean skipConfirmDeleteDialog = false;

	/**
	 * Populates the list from the database and sets an OnItemClickListener.
	 */
	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ignorelist);
		
		databaseAdapter = new DatabaseAdapterImpl(this);
		tester = new TesterImpl(this,
				new ConnectivityManagerImpl((ConnectivityManager)getSystemService(CONNECTIVITY_SERVICE)), 
				new WifiManagerImpl((WifiManager)getSystemService(WIFI_SERVICE)),
				null);
		
		addHeaderView();
		
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
					
					final String ssid = cursor.getString(2);
					
					Runnable runDelete = new Runnable() {
						public void run() {
							databaseAdapter.deleteIgnoredWifi(ssid);
							populate();
						}
					};
					
					// TODO How to test dialogs?
					if(skipConfirmDeleteDialog) {
						runDelete.run();
					} else {
						String message = getString(R.string.ignorelist_confirm_delete, ssid);
						Dialogs.showConfirmDeleteDialog(IgnoreList.this, message, runDelete);
					}
				}
				return true;
			}
		});
		
		populate();
	}
	
	/**
	 * Hack to allow testing by skipping the confirmation dialog.
	 * TODO How to test dialogs?
	 */
	public void setSkipConfirmDeleteDialog(final boolean skipConfirmDeleteDialog) {
		this.skipConfirmDeleteDialog = skipConfirmDeleteDialog;
	}
	
	/**
	 * TODO Register for Wifi action intents to keep this up-to-date?
	 * Adds an item allowing to add the location of the currently connected Wifi to the list.
	 */
	private void addHeaderView() {
		TwoLineListItem view = (TwoLineListItem)View.inflate(this, android.R.layout.simple_list_item_2, null);
		// view.setClickable(true);
		view.setEnabled(false);
		view.getText1().setEnabled(false);
		view.getText1().setText(getString(R.string.ignorelist_add_ignored_wifi));
		
		String text2 = getString(R.string.ignorelist_wifi_disconnected);
		if(tester.isWifiConnected()) {
			// view.setClickable(false);
			view.setEnabled(true);
			view.getText1().setEnabled(true);
			text2 = getString(R.string.ignorelist_ignore_wifi, tester.getWifiInfo().getSSID());
		}
		view.getText2().setText(text2);
		
		this.getListView().addHeaderView(view, null, true);
	}
	
	/**
	 * Adds the current Wifi connection to the list of ignored Wifi networks.
	 * @param location
	 */
	private void addIgnoredWifi() {
		if(tester.isWifiConnected()) {
			IWifiInfo wifiInfo = tester.getWifiInfo();
			databaseAdapter.addIgnoredWifi(wifiInfo.getBSSID(), wifiInfo.getSSID());
			populate();
		} else {
			Dialogs.showOKDialog(this, 
					this.getString(R.string.ignorelist_ignore), 
					this.getString(R.string.ignorelist_wifi_disconnected));
		}
	}

	/**
	 * Populates the list with the entries from the database using SimpleCursorAdapter.
	 */
	private void populate() {		
        Cursor cursor = databaseAdapter.fetchIgnoredWifis();
        startManagingCursor(cursor);
        
        SimpleCursorAdapter ignoredWifis = new SimpleCursorAdapter(this, android.R.layout.simple_list_item_2, cursor, 
        		new String[] { DatabaseAdapterImpl.COLUMN_SSID, DatabaseAdapterImpl.COLUMN_BSSID },
				new int[] { android.R.id.text1, android.R.id.text2 });
        
        setListAdapter(ignoredWifis);
    }

}
