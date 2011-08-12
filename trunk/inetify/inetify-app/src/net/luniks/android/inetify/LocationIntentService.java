package net.luniks.android.inetify;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import net.luniks.android.impl.ConnectivityManagerImpl;
import net.luniks.android.impl.LocationManagerImpl;
import net.luniks.android.impl.NotificationManagerImpl;
import net.luniks.android.impl.WifiManagerImpl;
import net.luniks.android.inetify.Locater.LocaterLocationListener;
import net.luniks.android.interfaces.IConnectivityManager;
import net.luniks.android.interfaces.ILocationManager;
import net.luniks.android.interfaces.INetworkInfo;
import net.luniks.android.interfaces.IWifiManager;
import android.app.IntentService;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.PowerManager;
import android.preference.PreferenceManager;
import android.util.Log;

public class LocationIntentService extends IntentService implements LocaterLocationListener {
	
	/** Timeout in seconds for getting a location */
	public static long GET_LOCATION_TIMEOUT = 60;
	
	/** Timeout in seconds for getting a location when using GPS */
	public static long GET_LOCATION_TIMEOUT_GPS = 30;
	
	/** Maximum age of a last known location in milliseconds */
	public static long LOCATION_MAX_AGE = 60 * 1000;
	
	/** Minimum fine accuracy */
	public static int LOCATION_MIN_ACC_FINE = 100;
	
	/** Minimum coarse accuracy */
	public static int LOCATION_MIN_ACC_COARSE = 3000;
	
	/** Wake lock, released in onCreate() */
	static volatile PowerManager.WakeLock wakeLock;
	
	/** Shared preferences key used to store the BSSID of the previous nearest location */
	private static final String SHARED_PREFERENCES_PREVIOUS_BSSID = "nearest_location_previous_bssid";
	
	/** UI thread handler */
	private Handler handler;
	
	/** Shared preferences */
	private SharedPreferences sharedPreferences;
	
	/** Location manager */
	private ILocationManager locationManager;
	
	/** Wifi manager */
	private IWifiManager wifiManager;
	
	/** Connectivity manager */
	private IConnectivityManager connectivityManager;
		
	/** Notifier */
	private Notifier notifier;
	
	/** Database adapter */
	private DatabaseAdapter databaseAdapter;
	
	/** Locater */
	private Locater locater;
	
	/** CountDownLatch used to let the worker thread wait until a location was found */
	private CountDownLatch latch;
	
	/** Flag to indicate that a location was found */
	private AtomicBoolean found = new AtomicBoolean(false);
	
	/** The Wifi locations fetched from the database */
	private Map<String, WifiLocation> locations = new ConcurrentHashMap<String, WifiLocation>();

	/**
	 * Creates an instance with a name.
	 */
	public LocationIntentService() {
		super("LocationIntentService");
	}
	
	/**
	 * Performs initialization.
	 */
	@Override
	public void onCreate() {
		super.onCreate();
		
		// TODO Is this the right moment to release the wake lock?
		if(wakeLock != null && wakeLock.isHeld()) {
			wakeLock.release();
			
			Log.d(Inetify.LOG_TAG, String.format("Released wake lock"));
		}
		
		this.handler = new Handler();
		this.sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
		this.locationManager = new LocationManagerImpl((LocationManager)getSystemService(LOCATION_SERVICE));
		this.wifiManager = new WifiManagerImpl((WifiManager)getSystemService(WIFI_SERVICE));
		this.connectivityManager = new ConnectivityManagerImpl((ConnectivityManager)getSystemService(CONNECTIVITY_SERVICE));
		this.notifier = new NotifierImpl(this,
				new NotificationManagerImpl((NotificationManager)getSystemService(NOTIFICATION_SERVICE)));
		this.databaseAdapter = new DatabaseAdapterImpl(this);
		this.locater = new LocaterImpl(locationManager);
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
	 * gets the nearest Wifi location and gives a notification and enables Wifi
	 * depending on some settings and conditions.
	 */
	public void onLocationChanged(final Location location) {
		this.found.set(true);
		latch.countDown();
		
		WifiLocation nearestLocation = getNearestLocationTo(location);
		
		boolean autoWifi = sharedPreferences.getBoolean("settings_auto_wifi", false);
		int maxDistance = Integer.valueOf(sharedPreferences.getString("settings_max_distance", "1500"));
		
		Log.d(Inetify.LOG_TAG, String.format("Got location from %s with accuracy %s, distance to %s is %s, max. distance is %s", 
				location.getProvider(), location.getAccuracy(), nearestLocation.getName(), nearestLocation.getDistance(), maxDistance));
		
		if(nearestLocation.getDistance() <= maxDistance) {
			locationNear(location, nearestLocation, autoWifi);
		} else {
			locationFar(autoWifi);
		}
	}

	/**
	 * Starts the locater to find the current location.
	 */
	@Override
	protected void onHandleIntent(final Intent intent) {
		
		Log.d(Inetify.LOG_TAG, "LocationIntentService onHandleIntent");
		
		if(! isAnyProviderEnabled()) {
			Log.d(Inetify.LOG_TAG, "No location provider enabled, skipping");
			return;
		}
		
		getLocations();
		
		if(locations.size() == 0) {
			Log.d(Inetify.LOG_TAG, "No locations, skipping");
			return;
		}
		
		this.latch = new CountDownLatch(1);
		this.found.set(false);
				
		boolean useGPS = locater.isProviderEnabled(LocationManager.GPS_PROVIDER) && 
						 sharedPreferences.getBoolean("settings_use_gps", false);

		locate(LOCATION_MIN_ACC_FINE, useGPS);
		// TODO Test this scenario
		if(! found.get()) {
			int minAccuracy = useGPS ? LOCATION_MIN_ACC_FINE : LOCATION_MIN_ACC_COARSE;
			locate(minAccuracy, false);
		}
	}
	
	/**
	 * Starts the locater on the main thread to find a location with the given
	 * minimum accuracy, using GPS or not, and lets the worker thread wait until
	 * a timeout expired.
	 * @param minAccuracy
	 * @param useGPS
	 */
	private void locate(final int minAccuracy, final boolean useGPS) {
		
		handler.post(new Runnable() {
			public void run() {
				locater.start(LocationIntentService.this, LOCATION_MAX_AGE, minAccuracy, useGPS);
			}
		});
		
		try {
			long timeout = useGPS ? GET_LOCATION_TIMEOUT_GPS : GET_LOCATION_TIMEOUT;
			latch.await(timeout, TimeUnit.SECONDS);			
		} catch(InterruptedException e) {
			// Ignore
		}
	}
	
	/**
	 * Called when the found location is near enough a Wifi location in respect to the user's
	 * "max distance" setting, enabling Wifi and giving a notification depending on some settings
	 * and conditions.
	 * @param location
	 * @param nearestLocation
	 * @param autoWifi
	 */
	private void locationNear(final Location location, final WifiLocation nearestLocation, final boolean autoWifi) {
		String nearestLocationNotified = sharedPreferences.getString(SHARED_PREFERENCES_PREVIOUS_BSSID, "");
		if(! nearestLocation.getBSSID().equals(nearestLocationNotified)) {
			
			if(autoWifi) {
				wifiManager.setWifiEnabled(true);
				
				Log.d(Inetify.LOG_TAG, "Enabled Wifi");
			}
			
			notifier.locatify(location, nearestLocation);
			sharedPreferences.edit().putString(SHARED_PREFERENCES_PREVIOUS_BSSID, nearestLocation.getBSSID()).commit();
		} else {
			// TODO Test this scenario (staying in proximity of same Wifi should not give new notification)
			Log.d(Inetify.LOG_TAG, String.format("Location %s is same as previous one, will not enable Wifi and not notify again", 
					nearestLocation.getName()));
		}
	}

	/**
	 * Called when the found location is not near enough a Wifi location in respect to the user's
	 * "max distance" setting, disabling wifi depending on some settings and conditions.
	 * @param autoWifi
	 */
	private void locationFar(final boolean autoWifi) {
		if(autoWifi) {
			if(isWifiEnabling() || isWifiConnectedOrConnecting()) {
				Log.d(Inetify.LOG_TAG, "Wifi not disabled because it is enabling, connecting or connected");
			} else {
				wifiManager.setWifiEnabled(false);
				
				Log.d(Inetify.LOG_TAG, "Disabled Wifi");
			}
		}
		
		// TODO Test this scenario (leaving and reentering proximity of same Wifi should give new notification)
		sharedPreferences.edit().putString(SHARED_PREFERENCES_PREVIOUS_BSSID, "").commit();
		
		Log.d(Inetify.LOG_TAG, "Not notifying");
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
	 * Returns true if at least one provider is enabled, false otherwise.
	 * @return boolean
	 */
	private boolean isAnyProviderEnabled() {
		List<String> providers = locationManager.getAllProviders();
		for(String provider : providers) {
			if(locationManager.isProviderEnabled(provider)) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Returns true if Wifi is enabling, false otherwise.
	 * @return boolean true if Wifi is enabling
	 */
    private boolean isWifiEnabling() {
    	int wifiState = wifiManager.getWifiState();
    	return wifiState == WifiManager.WIFI_STATE_ENABLING;
    }
	
	/**
	 * Returns true if there currently is a Wifi connected or connecting, false otherwise.
	 * TODO Duplication, same method in TesterImpl
	 * @return boolean true if Wifi is connected or connecting, false otherwise
	 */
    public boolean isWifiConnectedOrConnecting() {
    	INetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
    	
    	if(networkInfo != null && 
    			networkInfo.getType() == ConnectivityManager.TYPE_WIFI && 
    			networkInfo.isConnectedOrConnecting()) {
    		return true;
    	}
    	return false;
    }
}
