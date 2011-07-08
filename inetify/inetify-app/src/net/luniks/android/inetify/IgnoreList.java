package net.luniks.android.inetify;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.database.Cursor;
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
		
		this.getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
			public void onItemClick(final AdapterView<?> parent, final View view, final int position, final long id) {
				
				if(view instanceof TwoLineListItem) {
					TwoLineListItem listItem = (TwoLineListItem)view; 
					final String ssid = listItem.getText1().getText().toString();
					
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
						showConfirmDeleteDialog(ssid, runDelete);
					}
				}
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

	/**
	 * Shows a confirmation dialog before deleting an entry from the list/database.
	 * @param ssid the ssid to use in the confirmation text
	 * @param delete Runnable to delete the entry from the database and refresh the list
	 */
	private void showConfirmDeleteDialog(final String ssid, final Runnable delete) {
		AlertDialog.Builder alert = new AlertDialog.Builder(this);

		alert.setCancelable(true);
		alert.setTitle(R.string.confirm);
		alert.setMessage(getString(R.string.ignorelist_confirm_delete, ssid));
		
		alert.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
			public void onClick(final DialogInterface dialog, final int whichButton) {
				dialog.dismiss();
				delete.run();
			}
		});
		
		alert.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
			public void onClick(final DialogInterface dialog, final int whichButton) {
				dialog.dismiss();
			}
		});
		
		alert.show();		
	}

}
