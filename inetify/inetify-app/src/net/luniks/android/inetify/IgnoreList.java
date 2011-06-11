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

public class IgnoreList extends ListActivity {
	
	private DatabaseAdapter databaseAdapter;
	
	// TODO How to test dialogs?
	private boolean skipConfirmDeleteDialog = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ignorelist);
		databaseAdapter = new DatabaseAdapterImpl(this);
		
		this.getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
			public void onItemClick(final AdapterView<?> parent, final View view, final int position, final long id) {
				
				TwoLineListItem listItem = (TwoLineListItem)view; 
				final String ssid = listItem.getText1().getText().toString();
				final String bssid = listItem.getText2().getText().toString();
				
				Runnable runDelete = new Runnable() {
					public void run() {
						databaseAdapter.deleteIgnoredWifi(bssid);
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
		});
		
		populate();
	}
	
	// TODO How to test dialogs?
	public void setSkipConfirmDeleteDialog(final boolean skipConfirmDeleteDialog) {
		this.skipConfirmDeleteDialog = skipConfirmDeleteDialog;
	}

	private void populate() {		
        Cursor cursor = databaseAdapter.fetchIgnoredWifis();
        startManagingCursor(cursor);
        
        SimpleCursorAdapter ignoredWifis = new SimpleCursorAdapter(this, android.R.layout.simple_list_item_2, cursor, 
        		new String[] { DatabaseAdapterImpl.IGNORELIST_COLUMN_SSID, DatabaseAdapterImpl.IGNORELIST_COLUMN_BSSID },
				new int[] { android.R.id.text1, android.R.id.text2 });
        
        setListAdapter(ignoredWifis);
    }

	private void showConfirmDeleteDialog(final String message, final Runnable delete) {
		AlertDialog.Builder alert = new AlertDialog.Builder(this);

		alert.setCancelable(true);
		alert.setTitle(R.string.confirm);
		alert.setMessage(getString(R.string.ignorelist_confirm_delete, message));
		
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
