package net.luniks.android.inetify;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import net.luniks.android.inetify.Locater.Accuracy;
import net.luniks.android.inetify.Locater.LocaterLocationListener;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.location.Location;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;
import android.widget.TwoLineListItem;

/**
 * Activity that shows the list of Wifi locations and allows to
 * delete single entries.
 * 
 * @author torsten.roemer@luniks.net
 */
public class LocationList extends ListActivity {
	
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
		setContentView(R.layout.locationlist);
		addHeaderView();
		databaseAdapter = new DatabaseAdapterImpl(this);
		
		this.getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
			public void onItemClick(final AdapterView<?> parent, final View view, final int position, final long id) {
				
				if(position == 0) {
					locate();
				} else {
					if(view instanceof TwoLineListItem) {
						TwoLineListItem listItem = (TwoLineListItem)view; 
						final String ssid = listItem.getText1().getText().toString();
						final String bssid = listItem.getText2().getText().toString();
						
						Runnable runDelete = new Runnable() {
							public void run() {
								databaseAdapter.deleteLocation(bssid);
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
	
	// TODO Register for Wifi action intents to keep this up-to-date?
	private void addHeaderView() {
		TwoLineListItem view = (TwoLineListItem)View.inflate(this, android.R.layout.simple_list_item_2, null);
		// view.setClickable(true);
		view.setEnabled(false);
		view.getText1().setEnabled(false);
		view.getText1().setText("Add Wifi Location");
		
		WifiInfo wifiInfo = getWifiInfo();
		String text2 = "Wifi is not connected";
		if(wifiInfo != null && wifiInfo.getSSID() != null) {
			// view.setClickable(false);
			view.setEnabled(true);
			view.getText1().setEnabled(true);
			text2 = String.format("Add location of Wifi %s", wifiInfo.getSSID());
		}
		view.getText2().setText(text2);
		
		this.getListView().addHeaderView(view, null, true);
	}

	/**
	 * Populates the list with the entries from the database using SimpleCursorAdapter.
	 */
	private void populate() {		
        Cursor cursor = databaseAdapter.fetchLocations();
        startManagingCursor(cursor);
        
        SimpleCursorAdapter ignoredWifis = new SimpleCursorAdapter(this, android.R.layout.simple_list_item_2, cursor,
        		new String[] { DatabaseAdapterImpl.COLUMN_SSID, DatabaseAdapterImpl.COLUMN_BSSID },
				new int[] { android.R.id.text1, android.R.id.text2 });
        
        this.setListAdapter(ignoredWifis);
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
		alert.setMessage(getString(R.string.locationlist_confirm_delete, ssid));
		
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
	
	private void showOKDialog(final String title, final String message) {
		AlertDialog.Builder alert = new AlertDialog.Builder(this);
		
		alert.setTitle(title);
		alert.setMessage(message);
		alert.setCancelable(true);
		       
		alert.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
			public void onClick(final DialogInterface dialog, final int id) {
				dialog.dismiss();
			}
		});
		
		alert.show();
	}
	
	private void locate() {
		new LocateTask().execute(new Void[0]);
	}
	
	private void addWifiLocation(final Location location) {
		WifiInfo wifiInfo = getWifiInfo();
		if(wifiInfo != null && wifiInfo.getBSSID() != null && wifiInfo.getSSID() != null) {
			databaseAdapter.addLocation(wifiInfo.getBSSID(), wifiInfo.getSSID(), location);
			populate();
			Toast.makeText(LocationList.this, String.format("Added location of Wifi %s", 
					wifiInfo.getSSID()), Toast.LENGTH_LONG).show();
		} else {
			showOKDialog("Location", "Wifi was disconnected.");
		}
	}
	
	private WifiInfo getWifiInfo() {
		WifiManager wifiManager = (WifiManager)this.getSystemService(WIFI_SERVICE);
		return wifiManager.getConnectionInfo();
	}
    
    private class LocateTask extends AsyncTask<Void, Location, Void> implements LocaterLocationListener {
    	
    	private final Locater locater = new LocaterImpl(LocationList.this);
    	private final CountDownLatch latch = new CountDownLatch(1);
    	
    	private volatile Location location = null;
    	
    	private ProgressDialog dialog = new ProgressDialog(LocationList.this) {

			@Override
			public void onBackPressed() {
				super.onBackPressed();
				LocateTask.this.cancel(true);
			}
    	};
    	
    	
		
		public void onNewLocation(Location location) {
			publishProgress(location);
			
			if(locater.isAccurateEnough(location, Accuracy.FINE)) {
				this.location = location;
				latch.countDown();
			}
		}

		@Override
		protected void onPreExecute() {
			dialog.setTitle("Locating...");
			dialog.setMessage("Waiting for location");
			dialog.setIndeterminate(true);
			dialog.setCancelable(true);			
			dialog.show();
			locater.start(this);
		}

		@Override
		protected void onCancelled() {
			locater.stop();
			Toast.makeText(LocationList.this, "Cancelled", Toast.LENGTH_LONG).show();
		}

		@Override
		protected void onProgressUpdate(Location... values) {
			dialog.setMessage(String.format("Current accuracy: %s meters", (int)values[0].getAccuracy()));
		}

		@Override
		protected Void doInBackground(final Void... arg) {
			try {
				latch.await(30, TimeUnit.SECONDS);
			} catch (InterruptedException e) {
				// Ignore
			}
			return null;
		}
		
		@Override
	    protected void onPostExecute(final Void result) {
			locater.stop();
			dialog.cancel();
			
			if(location != null) {
				addWifiLocation(location);
			} else {
				showOKDialog("Location", "Could not find an accurate location.");
			}
	    }
		
    }

}
