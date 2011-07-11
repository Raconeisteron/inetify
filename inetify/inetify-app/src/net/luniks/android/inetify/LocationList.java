package net.luniks.android.inetify;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import net.luniks.android.impl.ConnectivityManagerImpl;
import net.luniks.android.impl.WifiManagerImpl;
import net.luniks.android.inetify.Locater.Accuracy;
import net.luniks.android.inetify.Locater.LocaterLocationListener;
import net.luniks.android.interfaces.IWifiInfo;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.location.Location;
import android.net.ConnectivityManager;
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
 * show locations on a Google map and delete single entries.
 * 
 * @author torsten.roemer@luniks.net
 */
public class LocationList extends ListActivity {
	
	/**
	 * Timeout in seconds for getting a location.
	 */
	private static final long GET_LOCATIION_TIMEOUT = 30;
	
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
		setContentView(R.layout.locationlist);
		
		databaseAdapter = new DatabaseAdapterImpl(this);
		tester = new TesterImpl(this,
				new ConnectivityManagerImpl((ConnectivityManager)getSystemService(CONNECTIVITY_SERVICE)), 
				new WifiManagerImpl((WifiManager)getSystemService(WIFI_SERVICE)),
				null);
		
		addHeaderView();
		
		this.getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
			public void onItemClick(final AdapterView<?> parent, final View view, final int position, final long id) {
				
				if(position == 0) {
					locate();
				} else {
					Cursor cursor = (Cursor)LocationList.this.getListAdapter().getItem(position);
					cursor.moveToPosition(position - 1);
					
					final String ssid = cursor.getString(2);
					final double lat = cursor.getDouble(3);
					final double lon = cursor.getDouble(4);
					
					showLocationOnMap(ssid, lat, lon);
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
					
					final String bssid = cursor.getString(1);
					final String ssid = cursor.getString(2);
					
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
						String message = getString(R.string.locationlist_confirm_delete, ssid);
						Dialogs.showConfirmDeleteDialog(LocationList.this, message, runDelete);
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
	 * Adds an item allowing to add the currently connected Wifi to the ignore list.
	 */
	private void addHeaderView() {
		TwoLineListItem view = (TwoLineListItem)View.inflate(this, android.R.layout.simple_list_item_2, null);
		// view.setClickable(true);
		view.setEnabled(false);
		view.getText1().setEnabled(false);
		view.getText1().setText(this.getString(R.string.locationlist_add_wifi_location));
		
		String text2 = this.getString(R.string.locationlist_wifi_disconnected);
		if(tester.isWifiConnected()) {
			// view.setClickable(false);
			view.setEnabled(true);
			view.getText1().setEnabled(true);
			text2 = String.format(this.getString(R.string.locationlist_add_location_of_wifi, tester.getWifiInfo().getSSID()));
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
	 * Starts the task to get the current location and then call addWifiLocation() with
	 * the found location.
	 */
	private void locate() {
		new LocateTask().execute(new Void[0]);
	}
	
	/**
	 * Adds the current Wifi connection with the given location to the list of Wifi locations.
	 * @param location
	 */
	private void addWifiLocation(final Location location) {
		if(tester.isWifiConnected()) {
			IWifiInfo wifiInfo = tester.getWifiInfo();
			databaseAdapter.addLocation(wifiInfo.getBSSID(), wifiInfo.getSSID(), location);
			populate();
		} else {
			Dialogs.showOKDialog(this, 
					this.getString(R.string.locationlist_location), 
					this.getString(R.string.locationlist_wifi_disconnected));
		}
	}
	
	/**
	 * Starts the activity to show the location of the Wifi with the given SSID and latitude and longitude
	 * on a Google map.
	 * @param ssid
	 * @param lat
	 * @param lon
	 */
	private void showLocationOnMap(final String ssid, final double lat, final double lon) {		
		Intent launchMapViewIntent = new Intent().setClass(LocationList.this, LocationMapView.class);
		launchMapViewIntent.putExtra(LocationMapView.EXTRA_SSID, ssid);
		launchMapViewIntent.putExtra(LocationMapView.EXTRA_LAT, lat);
		launchMapViewIntent.putExtra(LocationMapView.EXTRA_LON, lon);
		startActivity(launchMapViewIntent);
	}
    
	/**
	 * AsyncTask that tries to get the current location with at least Accuracy.FINE for max.
	 * GET_LOCATIION_TIMEOUT and then calls addWifiLocation(), passing the found location.
	 * 
	 * @author torsten.roemer@luniks.net
	 */
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
			dialog.setTitle(LocationList.this.getString(R.string.locationlist_locating_title));
			dialog.setMessage(LocationList.this.getString(R.string.locationlist_locating_message));
			dialog.setIndeterminate(true);
			dialog.setCancelable(true);			
			dialog.show();
			locater.start(this);
		}

		@Override
		protected void onCancelled() {
			locater.stop();
			Toast.makeText(LocationList.this, LocationList.this.getString(R.string.cancelled), Toast.LENGTH_LONG).show();
		}

		@Override
		protected void onProgressUpdate(Location... values) {
			dialog.setMessage(LocationList.this.getString(R.string.locationlist_current_accuracy, (int)values[0].getAccuracy()));
		}

		@Override
		protected Void doInBackground(final Void... arg) {
			try {
				latch.await(GET_LOCATIION_TIMEOUT, TimeUnit.SECONDS);
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
				Dialogs.showOKDialog(LocationList.this, 
						LocationList.this.getString(R.string.locationlist_location), 
						LocationList.this.getString(R.string.locationlist_could_not_get_accurate_location));
			}
	    }
		
    }

}
