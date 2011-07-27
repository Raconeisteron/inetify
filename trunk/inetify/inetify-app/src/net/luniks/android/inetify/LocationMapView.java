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
import android.widget.TwoLineListItem;

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
	private static final long GET_LOCATION_TIMEOUT = 50;
	
	/** The Google map view. */
	private MapView mapView;
	
	/** List of map overlays */
	private List<Overlay> mapOverlays;
	
	/** Icon used as marker */
	private Drawable icon;
	
	/** TwoLineListItem showing the status */
	private TwoLineListItem viewLocationStatus;
	
	/** LocateTask - retained through config changes */
	private LocateTask locateTask;
	
	/**
	 * Retains the locater AsyncTask before a config change occurs.
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
		
		viewLocationStatus = (TwoLineListItem)this.findViewById(R.id.view_locationstatus);
		viewLocationStatus.setBackgroundColor(this.getResources().getColor(R.color.grey_semitransparent));
		
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
				updateLocation(ssid, location, locateTask.getLocateStatus());
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
					this.getString(R.string.locationmapview_location), 
					this.getString(R.string.locationmapview_could_not_get_accurate_location));
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
	 * Starts the AsyncTask to find the location if it is not already running,
	 * and updates the current location otherwise.
	 */
	private void findLocation() {
		if (locateTask.getLocateStatus() != LocateTask.LocateStatus.PENDING) {
			this.updateLocation(null, locateTask.getCurrentLocation(),
					locateTask.getLocateStatus());
		} else {
			locateTask.execute(new Void[0]);
		}
	}

	
	/**
	 * Moves the marker and the map to the given location, shows the given SSID in the title if it
	 * is not null, and shows status information depending on the given location and status.
	 * @param ssid
	 * @param location
	 * @param status
	 */
	private void updateLocation(final String ssid, final Location location, final LocateTask.LocateStatus status) {
				
		if(ssid != null) {
			this.setTitle(this.getString(R.string.locationmapview_label_ssid, ssid));
		}
		
		if(status == LocateTask.LocateStatus.PENDING || status == LocateTask.LocateStatus.NOTFOUND) {
			showStatus("", "", View.GONE);
		} else if(status == LocateTask.LocateStatus.WAITING) {
			String status1 = this.getString(R.string.locationmapview_status1_searching);
			String status2 = this.getString(R.string.locationmapview_status2_waiting);
			showStatus(status1, status2, View.VISIBLE);
		} else if(status == LocateTask.LocateStatus.UPDATING) {
			String status1 = this.getString(R.string.locationmapview_status1_searching);
			String status2 = "";
			if(location != null) {
				status2 = this.getString(R.string.locationmapview_status2_current, 
						Math.round(location.getAccuracy()));
			}
			showStatus(status1, status2, View.VISIBLE);
		} else if(status == LocateTask.LocateStatus.FOUND) {
			String status1 = this.getString(R.string.locationmapview_status1_found);
			String status2 = "";
			if(location != null) {
				status2 = this.getString(R.string.locationmapview_status2_current, 
						Math.round(location.getAccuracy()));
			}
			showStatus(status1, status2, View.VISIBLE);
		}
		
		if(location != null) {			
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
	 * Shows the given two status messages status and changes the visibility to the given value.
	 * @param status1
	 * @param status2
	 * @param visibility
	 */
	private void showStatus(final String status1, final String status2, final int visibility) {
		viewLocationStatus.getText1().setText(status1, TextView.BufferType.NORMAL);
		viewLocationStatus.getText2().setText(status2, TextView.BufferType.NORMAL);
		if(viewLocationStatus.getVisibility() != visibility) {
			viewLocationStatus.setVisibility(visibility);
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
    	
    	private LocateStatus status = LocateStatus.PENDING;
    	
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
    	
    	private synchronized LocateStatus getLocateStatus() {
    		return status;
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
			status = LocateStatus.WAITING;
			activity.updateLocation(null, initialLocation, status);
		}

		@Override
		protected void onCancelled() {
			locater.stop();
			latch.countDown();
		}

		@Override
		protected void onProgressUpdate(Location... values) {
			status = LocateStatus.UPDATING;
			activity.updateLocation(null, values[0], status);
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
				Intent intent = new Intent();
				intent.setAction(LocationList.ADD_LOCATION_ACTION);
				intent.putExtra(LocationList.EXTRA_LOCATION, foundLocation);
				activity.sendBroadcast(intent);
				
				status = LocateStatus.FOUND;
				activity.updateLocation(null, foundLocation, status);
				
				Log.d(Inetify.LOG_TAG, String.format("Sent broadcast: %s", intent));
			} else {
				status = LocateStatus.NOTFOUND;
				activity.updateLocation(null, null, status);
				activity.showDialog(ID_NO_LOCATION_FOUND_DIALOG);
			}
	    }
		
	    /**
	     * Status of finding the location.
	     * 
	     * @author torsten.roemer@luniks.net
	     */
	    private static enum LocateStatus {
	    	PENDING, WAITING, UPDATING, FOUND, NOTFOUND
	    }
		
    }

}
