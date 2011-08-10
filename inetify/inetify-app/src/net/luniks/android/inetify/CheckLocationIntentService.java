package net.luniks.android.inetify;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import net.luniks.android.impl.LocationManagerImpl;
import net.luniks.android.impl.NotificationManagerImpl;
import net.luniks.android.inetify.Locater.LocaterLocationListener;
import net.luniks.android.interfaces.ILocationManager;
import android.app.IntentService;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationManager;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;

public class CheckLocationIntentService extends IntentService implements LocaterLocationListener {
	
	/** Timeout in seconds for getting a location (used more than once) */
	public static long GET_LOCATION_TIMEOUT = 60;
	
	/** Maximum age of a last known location in milliseconds */
	public static long LOCATION_MAX_AGE = 60 * 1000;
	
	/** Minimum fine accuracy */
	public static int LOCATION_MIN_ACC_FINE = 100;
	
	/** Minimum coarse accuracy */
	public static int LOCATION_MIN_ACC_COARSE = 3000;
	
	/** Shared preferences key used to store the BSSID of the last nearest location notified about */
	private static final String SHARED_PREFERENCES_NOTIFIED_BSSID = "nearest_location_notified_bssid";
	
	/** UI thread handler */
	private Handler handler;
	
	/** Shared preferences */
	private SharedPreferences sharedPreferences;
	
	/** Location manager */
	private ILocationManager locationManager;
		
	/** Notifier */
	private Notifier notifier;
	
	/** Database adapter */
	private DatabaseAdapter databaseAdapter;
	
	/** Locater */
	private Locater locater;
	
	/** CountDownLatch used to let the worker thread wait until a location was found */
	private CountDownLatch countDownLatch;
	
	/** Flag to indicate that a location was found */
	private AtomicBoolean locationFound = new AtomicBoolean(false);
	
	/** The "max_distance" from the settings */
	private AtomicInteger maxDistance = new AtomicInteger(1500);
	
	/** The Wifi locations fetched from the database */
	private Map<String, WifiLocation> locations = new ConcurrentHashMap<String, WifiLocation>();

	/**
	 * Creates an instance with a name.
	 */
	public CheckLocationIntentService() {
		super("CheckLocationIntentService");
	}
	
	/**
	 * Performs initialization.
	 */
	@Override
	public void onCreate() {
		super.onCreate();
		this.handler = new Handler();
		this.sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
		locationManager = new LocationManagerImpl((LocationManager)getSystemService(LOCATION_SERVICE));
		this.notifier = new NotifierImpl(this,
				new NotificationManagerImpl((NotificationManager)getSystemService(NOTIFICATION_SERVICE)));
		this.databaseAdapter = new DatabaseAdapterImpl(this);
		this.locater = new LocaterImpl(
				new LocationManagerImpl((LocationManager)this.getSystemService(LOCATION_SERVICE)));
		
		this.maxDistance.set(Integer.valueOf(sharedPreferences.getString("settings_max_distance", "1500")));
	}
	
	/**
	 * Stops the locater if it is not already stopped as it should
	 * and closes the database.
	 */
	@Override
	public void onDestroy() {
		locater.stop();
		databaseAdapter.close();
		super.onDestroy();
	}
	
	/**
	 * Called on the main thread when a location was found, stops the locater, 
	 * gets the nearest Wifi location and gives a notification depending on
	 * some settings and conditions.
	 */
	public void onLocationChanged(final Location location) {
		this.locationFound.set(true);
		countDownLatch.countDown();
		
		Log.d(Inetify.LOG_TAG, String.format("Got location from %s with accuracy %s", 
				location.getProvider(), location.getAccuracy()));
		
		WifiLocation nearestLocation = getNearestLocationTo(location);
		
		int maxDistance = Integer.valueOf(sharedPreferences.getString("settings_max_distance", "1500"));
		if(nearestLocation.getDistance() <= maxDistance) {
			String nearestLocationNotified = sharedPreferences.getString(SHARED_PREFERENCES_NOTIFIED_BSSID, "");
			if(! nearestLocation.getBSSID().equals(nearestLocationNotified)) {
				notifier.locatify(location, nearestLocation);
				sharedPreferences.edit().putString(SHARED_PREFERENCES_NOTIFIED_BSSID, nearestLocation.getBSSID()).commit();
			} else {
				// TODO Test this scenario (staying in proximity of same Wifi should not give new notification)
				Log.d(Inetify.LOG_TAG, String.format("Already notified about location %s, will not notify again", 
						nearestLocation.getName()));
			}
		} else {
			// TODO Test this scenario (leaving and reentering proximity of same Wifi should give new notification)
			sharedPreferences.edit().putString(SHARED_PREFERENCES_NOTIFIED_BSSID, "").commit();
			Log.d(Inetify.LOG_TAG, String.format("Distance %s is more than max distance %s, not notifying", 
					nearestLocation.getDistance(), maxDistance));
		}
	}

	/**
	 * Starts the locater to find the current location.
	 */
	@Override
	protected void onHandleIntent(final Intent intent) {
		
		if(areNoProvidersEnabled()) {
			Log.d(Inetify.LOG_TAG, "No location providers enabled, skipping");
		}
		
		getLocations();
		
		if(locations.size() == 0) {
			Log.d(Inetify.LOG_TAG, "No locations, skipping");
			return;
		}
		
		this.countDownLatch = new CountDownLatch(1);
		this.locationFound.set(false);
				
		final boolean gpsEnabled = locater.isProviderEnabled(LocationManager.GPS_PROVIDER);
		final boolean useGPS = sharedPreferences.getBoolean("settings_use_gps", false);

		locate(LOCATION_MIN_ACC_FINE, useGPS && gpsEnabled);
		// TODO Test this scenario
		if(! locationFound.get()) {
			locate(LOCATION_MIN_ACC_COARSE, false);
		}
	}
	
	/**
	 * Starts the locater on the main thread to find a location with the given
	 * minimum accuracy, using GPS or not, and lets the worker thread wait util
	 * GET_LOCATION_TIMEOUT expired.
	 * @param minAccuracy
	 * @param useGPS
	 */
	private void locate(final int minAccuracy, final boolean useGPS) {
		
		handler.post(new Runnable() {
			public void run() {
				locater.start(CheckLocationIntentService.this, LOCATION_MAX_AGE, minAccuracy, useGPS);
			}
		});
		
		try {
			countDownLatch.await(GET_LOCATION_TIMEOUT, TimeUnit.SECONDS);			
		} catch (InterruptedException e) {
			// Ignore
		}
	}

	/**
	 * Fetches the Wifi locations from the database and puts them in the map.
	 */
	private void getLocations() {
		Cursor cursor = databaseAdapter.fetchLocations();
		
		try {
			while(cursor.moveToNext()) {
				WifiLocation wifiLocation = new WifiLocation();
				wifiLocation.setBSSID(cursor.getString(1));
				wifiLocation.setSSID(cursor.getString(2));
				wifiLocation.setName(cursor.getString(3));
				Location location = new Location(Locater.PROVIDER_DATABASE);
				location.setLatitude(cursor.getDouble(4));
				location.setLongitude(cursor.getDouble(5));
				location.setAccuracy(cursor.getFloat(6));
				wifiLocation.setLocation(location);
				locations.put(wifiLocation.getBSSID(), wifiLocation);
			}
		} finally {
			cursor.close();
		}
	}
	
	/**
	 * Returns the Wifi location that is nearest to the given location. 
	 * @param location
	 * @return WifiLocation
	 */
	private WifiLocation getNearestLocationTo(final Location location) {
		float shortestDistance = Float.MAX_VALUE;
		WifiLocation nearestLocation = null;
		for(Map.Entry<String, WifiLocation> entry : locations.entrySet()) {
			WifiLocation currentLocation = entry.getValue();
			float distance = currentLocation.getLocation().distanceTo(location);
			if(distance < shortestDistance) {
				shortestDistance = distance;
				nearestLocation = currentLocation;
				nearestLocation.setDistance(shortestDistance);
			}
		}
		return nearestLocation;
	}
	
	/**
	 * Returns true if no providers are enabled, false otherwise.
	 * @return boolean
	 */
	private boolean areNoProvidersEnabled() {
		List<String> providers = locationManager.getAllProviders();
		for(String provider : providers) {
			if(locationManager.isProviderEnabled(provider)) {
				return true;
			}
		}
		return false;
	}
}
