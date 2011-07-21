package net.luniks.android.inetify;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import net.luniks.android.impl.LocationManagerImpl;
import net.luniks.android.inetify.Locater.Accuracy;
import net.luniks.android.inetify.Locater.LocaterLocationListener;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;

/**
 * MapActivity that shows a location (of a Wifi network) on a Google map
 * or finds a location and adds it to the list by broadcasting an intent to
 * LocationList.
 * 
 * @author torsten.roemer@luniks.net
 */
public class LocationMapView extends MapActivity {
	
	/** Action to show the location */
	public static final String SHOW_LOCATION_ACTION = "net.luniks.android.inetify.SHOW_LOCATION";
	
	/** Action to find the location */
	public static final String FIND_LOCATION_ACTION = "net.luniks.android.inetify.FIND_LOCATION";
	
	/** Extra to indicate that the activity was recreated after a config change  */
	public static final String EXTRA_RECREATED_FLAG = "net.luniks.android.inetify.BUSY_FLAG";
	
	/** Id of the "no location found" dialog */
	private static final int ID_NO_LOCATION_FOUND_DIALOG = 0;
	
	/** Timeout in seconds for getting a location */
	private static final long GET_LOCATION_TIMEOUT = 60;
	
	/** The Google map view. */
	private MapView mapView;
	
	/** List of map overlays */
	private List<Overlay> mapOverlays;
	
	/** Icon used as marker */
	private Drawable icon;
	
	/** Textview showing the status */
	private TextView textViewLocationStatus;
	
	/** LocateTask - retained through config changes */
	private LocateTask locateTask;
	
	/**
	 * Retains the locater AsyncTask before a config change
	 */
	@Override
	public Object onRetainNonConfigurationInstance() {
		locateTask.setActivity(null);
		return locateTask;
	}

	/**
	 * Creates the map view.
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		this.setContentView(R.layout.locationmapview);
		
		textViewLocationStatus = (TextView)this.findViewById(R.id.textview_locationstatus);
		
		mapView = (MapView)findViewById(R.id.mapview_location);
		mapView.setBuiltInZoomControls(true);
		mapOverlays = mapView.getOverlays();
		
		icon = this.getResources().getDrawable(R.drawable.icon).mutate();
		icon.setAlpha(85);
		
		Object retained = this.getLastNonConfigurationInstance();
		if(retained == null) {
			locateTask = new LocateTask(this);
		} else {
			locateTask = (LocateTask)retained;
			locateTask.setActivity(this);
		}
		
		Intent intent = this.getIntent();
		if(intent != null) {
			if(intent.getAction().equals(SHOW_LOCATION_ACTION)) {
				String ssid = intent.getStringExtra(LocationList.EXTRA_SSID);
				Location location = intent.getParcelableExtra(LocationList.EXTRA_LOCATION);
				updateLocation(ssid, location, false);
			} else if(intent.getAction().equals(FIND_LOCATION_ACTION)) {
				findLocation();
			}
		}
	}

	/**
	 * Creates the dialogs managed by this activity.
	 */
	@Override
	protected Dialog onCreateDialog(final int id) {
		if(id == ID_NO_LOCATION_FOUND_DIALOG) {
			return Dialogs.createOKDialog(this, ID_NO_LOCATION_FOUND_DIALOG,
					this.getString(R.string.locationlist_location), 
					this.getString(R.string.locationlist_could_not_get_accurate_location));
		}
		return super.onCreateDialog(id);
	}
	
	/**
	 * Cancels finding the location when the user presses the back button.
	 */
	@Override
	public void onBackPressed() {
		this.locateTask.cancel(false);
		super.onBackPressed();
	}

	/**
	 * There is no route to be displayed.
	 */
	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}
	
	/**
	 * Returns an ItemizedOverlay used as marker using the given drawable, geopoint and title.
	 * @param icon
	 * @param point
	 * @param title
	 * @return SimpleItemizedOverlay
	 */
	private SimpleItemizedOverlay getOverlay(final Drawable icon, final GeoPoint point, final String title) {
		SimpleItemizedOverlay itemizedOverlay = new SimpleItemizedOverlay(icon, 0, false);
		OverlayItem overlayItem = new OverlayItem(point, title, "");
		itemizedOverlay.addOverlay(overlayItem);
		return itemizedOverlay;
	}
	
	/**
	 * Starts the AsyncTask to find the location if the intent does not have the EXTRA_RECREATED_FLAG
	 * extra (when the activity is initially started), and just updates the location otherwise
	 * (when the activity is restarted after a config change).
	 */
	private void findLocation() {
		if(this.getIntent().hasExtra(EXTRA_RECREATED_FLAG)) {
			this.updateLocation(null, locateTask.getCurrentLocation(), locateTask.isRunning());
		} else {
			this.getIntent().putExtra(EXTRA_RECREATED_FLAG, true);
			locateTask.execute(new Void[0]);
		}
	}
	
	/**
	 * Moves the marker and the map to the given location, shows the given SSID in the title if it
	 * is not null, and shows a "Searching..." status if searching is true.
	 * @param ssid
	 * @param location
	 * @param searching
	 */
	private void updateLocation(final String ssid, final Location location, final boolean searching) {
		
		if(ssid != null) {
			this.setTitle(this.getString(R.string.locationmapview_label_ssid, ssid));
		}
		
		if(location != null) {
			if(searching) {
				String status = this.getString(R.string.locationmapview_status_searching, Math.round(location.getAccuracy()));
				showStatus(status, View.VISIBLE);
			} else {
				showStatus("", View.GONE);
			}
			
			final Double latE6 = location.getLatitude() * 1E6;
			final Double lonE6 = location.getLongitude() * 1E6;
			
			GeoPoint point = new GeoPoint(latE6.intValue(), lonE6.intValue());
			
			mapOverlays.clear();
			mapOverlays.add(getOverlay(icon, point, ssid));
			
			MapController mapController = mapView.getController();
	        mapController.animateTo(point);
		}
	}
	
	/**
	 * Shows the given status and changes the visibility to the given value.
	 * @param status
	 * @param visibility
	 */
	private void showStatus(final String status, final int visibility) {
		textViewLocationStatus.setText(status, TextView.BufferType.NORMAL);
		if(textViewLocationStatus.getVisibility() != visibility) {
			textViewLocationStatus.setVisibility(visibility);
		}
	}
	
	/**
	 * AsyncTask that starts the Locater, listens for location updates and updates the location
	 * when it receives a location update. Stops when it has received a location with Accuracy.FINE
	 * or when cancelled, and shows a "No accurate location found" dialog if it did not receive an
	 * accurate enough location within GET_LOCATION_TIMEOUT.
	 * 
	 * @author torsten.roemer@luniks.net
	 */
    private static class LocateTask extends AsyncTask<Void, Location, Void> implements LocaterLocationListener {
    	
		private final Locater locater;
    	private final CountDownLatch latch = new CountDownLatch(1);
    	
    	private LocationMapView activity;
    	
    	private Location currentLocation = null;
    	private Location foundLocation = null;
    	
    	private LocateTask(final LocationMapView activity) {
    		this.activity = activity;
    		LocationManager locationManager = (LocationManager)activity.getSystemService(LOCATION_SERVICE);
    		this.locater = new LocaterImpl(new LocationManagerImpl(locationManager));
    	}
    	
    	private void setActivity(final LocationMapView activity) {
    		this.activity = activity;
    	}
    	
    	private synchronized Location getCurrentLocation() {
    		return this.currentLocation;
    	}
    	
    	private boolean isRunning() {
    		return this.getStatus() == Status.RUNNING;
    	}
    	
		public synchronized void onLocationChanged(final Location location) {
			publishProgress(location);
			this.currentLocation = location;
			
			if(locater.isAccurateEnough(location, Accuracy.FINE)) {
				this.foundLocation = location;
				latch.countDown();
			}
		}

		@Override
		protected void onPreExecute() {
			locater.start(this);
			
			Location initialLocation = locater.getBestLastKnownLocation(Long.MAX_VALUE);
			if(initialLocation == null) {
				initialLocation = new Location(LocationManager.NETWORK_PROVIDER);
			}
			activity.updateLocation(null, initialLocation, true);
		}

		@Override
		protected void onCancelled() {
			locater.stop();
			latch.countDown();
		}

		@Override
		protected void onProgressUpdate(Location... values) {
			activity.updateLocation(null, values[0], true);
		}

		@Override
		protected Void doInBackground(final Void... arg) {			
			try {
				latch.await(GET_LOCATION_TIMEOUT, TimeUnit.SECONDS);
			} catch (InterruptedException e) {
				// Ignore
			}
			return null;
		}
		
		@Override
	    protected void onPostExecute(final Void result) {
			locater.stop();
			
			if(foundLocation != null) {
				activity.updateLocation(null, foundLocation, false);
				Intent intent = new Intent();
				intent.setAction(LocationList.ADD_LOCATION_ACTION);
				intent.putExtra(LocationList.EXTRA_LOCATION, foundLocation);
				activity.sendBroadcast(intent);
				
				Log.d(Inetify.LOG_TAG, String.format("Sent broadcast: %s", intent));
				
			} else {
				activity.showStatus("", View.GONE);
				activity.showDialog(ID_NO_LOCATION_FOUND_DIALOG);
			}
	    }
		
    }

}
