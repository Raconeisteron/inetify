package net.luniks.android.inetify;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import net.luniks.android.impl.ConnectivityManagerImpl;
import net.luniks.android.impl.LocationManagerImpl;
import net.luniks.android.impl.WifiManagerImpl;
import net.luniks.android.inetify.Locater.Accuracy;
import net.luniks.android.inetify.Locater.LocaterLocationListener;
import net.luniks.android.interfaces.IWifiInfo;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
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
	
	/** Id of the header view */
	private static final int ID_HEADER_VIEW = 0;
	
	/** Id of the progress dialog */
	private static final int ID_PROGRESS_DIALOG = 1;
	
	/** Id of the "no location found" dialog */
	private static final int ID_NO_LOCATION_DIALOG = 2;
	
	/** Id of the confirm delete dialog */
	private static final int ID_CONFIRM_DELETE_DIALOG = 3;
	
	/** Timeout in seconds for getting a location */
	private static final long GET_LOCATIION_TIMEOUT = 60;
	
	/** Key to save the instance state of the bssid of the Wifi location to delete */
	private static final String STATE_BUNDLE_KEY_BSSID_TO_DELETE = "bssidToDelete";

	/** Key to save the instance state of the ssid of the Wifi location to delete */
	private static final String STATE_BUNDLE_KEY_SSID_TO_DELETE = "ssidToDelete";
	
	/** Key to save the instance state of the progress message */
	private static final String STATE_BUNDLE_KEY_PROGRESS_MESSAGE = "progressMessage";
	
	/** Database adapter */
	private DatabaseAdapter databaseAdapter;
	
	/** Tester instance */
	private Tester tester;
	
	/** Broadcast receiver */
	private PostingBroadcastReceiver wifiActionReceiver;
	
	/** LocateTask - retained through config changes */
	private LocateTask locateTask;
	
	/** Progress dialog */
	private ProgressDialog progressDialog;
	
	/** Message shown in the progress dialog */
	private String progressMessage;
	
	// This would not be necessary with onCreateDialog(int, Bundle) in API 8...
	/** BSSID of the Wifi location to delete */
	private String bssidToDelete = null;
	
	/** SSID of the Wifi location to delete */
	private String ssidToDelete = null;
	
	/** Flag indicating if the activity is in stopped state */
	private AtomicBoolean stopped = new AtomicBoolean(false);
	
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
	
	public void updateProgress(final String message) {
		this.progressMessage = message;
		if(progressDialog != null) {
			progressDialog.setMessage(message);
		}
	}
	
	@Override
	public Object onRetainNonConfigurationInstance() {
		return locateTask;
	}
	
	/**
	 * Populates the list from the database and sets an OnItemClickListener.
	 */
	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.locationlist);
		
		TwoLineListItem headerView = (TwoLineListItem)View.inflate(LocationList.this, android.R.layout.simple_list_item_2, null);
		headerView.setId(ID_HEADER_VIEW);
		this.getListView().addHeaderView(headerView);
		
		Object retained = this.getLastNonConfigurationInstance();
		if(retained == null) {
			locateTask = new LocateTask(this);
		} else {
			locateTask = (LocateTask)retained;
			locateTask.setActivity(this);
		}
		
		databaseAdapter = new DatabaseAdapterImpl(this);
		tester = new TesterImpl(this,
				new ConnectivityManagerImpl((ConnectivityManager)getSystemService(CONNECTIVITY_SERVICE)), 
				new WifiManagerImpl((WifiManager)getSystemService(WIFI_SERVICE)),
				null);
		
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
					
					bssidToDelete = cursor.getString(1);
					ssidToDelete = cursor.getString(2);
					
					// TODO How to test dialogs?
					if(skipConfirmDeleteDialog) {
						deleteWifiLocation(bssidToDelete);
					} else {
						LocationList.this.showDialog(ID_CONFIRM_DELETE_DIALOG);
					}
				}
				return true;
			}
		});
		
		populate();
	}

	@Override
	protected Dialog onCreateDialog(final int id) {
		if(id == ID_PROGRESS_DIALOG) {
			ProgressDialog dialog = new ProgressDialog(this);
			dialog.setTitle(this.getString(R.string.locationlist_locating_title));
			dialog.setMessage(this.getString(R.string.locationlist_locating_message));
			dialog.setIndeterminate(true);
			dialog.setCancelable(true);
			dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
				public void onCancel(DialogInterface dialog) {
					locateTask.cancel(false);
				}
			});
			this.progressDialog = dialog;
			return dialog;
		}
		if(id == ID_NO_LOCATION_DIALOG) {
			if(! stopped.get()) {
				return Dialogs.createOKDialog(LocationList.this, ID_NO_LOCATION_DIALOG,
						this.getString(R.string.locationlist_location), 
						this.getString(R.string.locationlist_could_not_get_accurate_location));
			}
		}
		if(id == ID_CONFIRM_DELETE_DIALOG) {
			return Dialogs.createConfirmDeleteDialog(LocationList.this, ID_CONFIRM_DELETE_DIALOG,
					"");
		}
		return super.onCreateDialog(id);
	}
	
	@Override
	protected void onPrepareDialog(final int id, final Dialog dialog) {
		if(id == ID_PROGRESS_DIALOG) {
			if(progressMessage != null) {
				this.progressDialog.setMessage(progressMessage);
			}
		}
		if(id == ID_CONFIRM_DELETE_DIALOG) {
			final String message = getString(R.string.locationlist_confirm_delete, ssidToDelete);
			AlertDialog alertDialog = (AlertDialog)dialog;
			alertDialog.setMessage(message);
			alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
				public void onClick(final View view) {
					deleteWifiLocation(bssidToDelete);
					LocationList.this.dismissDialog(ID_CONFIRM_DELETE_DIALOG);
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

	@Override
	protected void onPause() {
		if(wifiActionReceiver != null) {
			this.unregisterReceiver(wifiActionReceiver);
		}
		super.onPause();
	}
	
	@Override
	protected void onStart() {
		this.stopped.set(false);
		super.onStop();
	}
	
	@Override
	protected void onStop() {
		this.stopped.set(true);
		super.onStop();
	}

	@Override
	protected void onRestoreInstanceState(final Bundle state) {
		bssidToDelete = state.getString(STATE_BUNDLE_KEY_BSSID_TO_DELETE);
		ssidToDelete = state.getString(STATE_BUNDLE_KEY_SSID_TO_DELETE);
		progressMessage = state.getString(STATE_BUNDLE_KEY_PROGRESS_MESSAGE);
		super.onRestoreInstanceState(state);
	}

	@Override
	protected void onSaveInstanceState(final Bundle outState) {
		outState.putString(STATE_BUNDLE_KEY_BSSID_TO_DELETE, bssidToDelete);
		outState.putString(STATE_BUNDLE_KEY_SSID_TO_DELETE, ssidToDelete);
		outState.putString(STATE_BUNDLE_KEY_PROGRESS_MESSAGE, progressMessage);
		super.onSaveInstanceState(outState);
	}
	
	private void updateHeaderView() {
		TwoLineListItem headerView = (TwoLineListItem)this.findViewById(ID_HEADER_VIEW);
		headerView.getText1().setText(this.getString(R.string.locationlist_add_wifi_location));
		if(tester.isWifiConnected()) {
			headerView.setEnabled(true);
			headerView.getText1().setEnabled(true);
			headerView.getText2().setText(LocationList.this.getString(
					R.string.locationlist_add_location_of_wifi, 
					tester.getWifiInfo().getSSID()));
		} else {
			headerView.setEnabled(false);
			headerView.getText1().setEnabled(false);
			headerView.getText2().setText(LocationList.this.getString(
					R.string.locationlist_wifi_disconnected));
		}
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
	 * Adds the current Wifi connection with the given location to the list of Wifi locations.
	 * @param location
	 */
	private void addWifiLocation(final Location location) {
		if(tester.isWifiConnected()) {
			IWifiInfo wifiInfo = tester.getWifiInfo();
			databaseAdapter.addLocation(wifiInfo.getBSSID(), wifiInfo.getSSID(), location);
			populate();
		} else {
			// Toast.makeText(this, R.string.locationlist_wifi_disconnected, Toast.LENGTH_SHORT).show();
			databaseAdapter.addLocation("Dummy", "Dummy", location);
			populate();
		}
	}
	
	private void deleteWifiLocation(final String bssid) {
		databaseAdapter.deleteLocation(bssid);
		populate();
	}
	
	/**
	 * Starts the task to get the current location and then call addWifiLocation() with
	 * the found location.
	 */
	private void locate() {
		// if(tester.isWifiConnected()) {
			locateTask = new LocateTask(this);
			locateTask.execute(new Void[0]);
		// } else {
		// 	Toast.makeText(this, R.string.locationlist_wifi_disconnected, Toast.LENGTH_SHORT).show();
		// }
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
	 * GET_LOCATION_TIMEOUT and then calls addWifiLocation(), passing the found location.
	 * 
	 * @author torsten.roemer@luniks.net
	 */
    private static class LocateTask extends AsyncTask<Void, Location, Void> implements LocaterLocationListener {
    	
		private final Locater locater;
    	private final CountDownLatch latch = new CountDownLatch(1);
    	
    	private LocationList activity;
    	
    	private volatile Location location = null;
    	
    	private LocateTask(final LocationList activity) {
    		this.activity = activity;
    		LocationManager locationManager = (LocationManager)activity.getSystemService(LOCATION_SERVICE);
    		this.locater = new LocaterImpl(this.activity, new LocationManagerImpl(locationManager));
    	}
    	
    	private void setActivity(final LocationList activity) {
    		this.activity = activity;
    	}
    	
		public void onNewLocation(final Location location) {
			publishProgress(location);
			
			if(locater.isAccurateEnough(location, Accuracy.FINE)) {
				this.location = location;
				latch.countDown();
			}
		}

		@Override
		protected void onPreExecute() {
			activity.updateProgress(activity.getString(R.string.locationlist_locating_message));
			activity.showDialog(ID_PROGRESS_DIALOG);
			locater.start(this);
		}

		@Override
		protected void onCancelled() {
			locater.stop();
			latch.countDown();
			
			dismissProgressDialog();
			
			Toast.makeText(activity, activity.getString(R.string.cancelled), Toast.LENGTH_SHORT).show();
		}

		@Override
		protected void onProgressUpdate(Location... values) {
			String message = activity.getString(R.string.locationlist_current_accuracy, (int)values[0].getAccuracy());
			activity.updateProgress(message);
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
			
			dismissProgressDialog();
			
			if(location != null) {
				activity.addWifiLocation(location);
			} else {
				activity.showDialog(ID_NO_LOCATION_DIALOG);
			}
	    }
		
		private void dismissProgressDialog() {
			try {
				activity.dismissDialog(ID_PROGRESS_DIALOG);
				// http://code.google.com/p/android/issues/detail?id=4266
				activity.removeDialog(ID_PROGRESS_DIALOG);
			}
			catch(Exception e) {
				// Ignore
			}
		}
		
    }

}
