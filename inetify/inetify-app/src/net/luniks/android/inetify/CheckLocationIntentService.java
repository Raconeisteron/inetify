package net.luniks.android.inetify;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import net.luniks.android.impl.LocationManagerImpl;
import net.luniks.android.impl.NotificationManagerImpl;
import net.luniks.android.inetify.Locater.LocaterLocationListener;
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
	
	/** Maximum age of a location in milliseconds */
	public static long LOCATION_MAX_AGE = 5 * 60 * 1000;
	
	/** UI thread handler */
	private Handler handler;
	
	/** Shared preferences */
	private SharedPreferences sharedPreferences;
		
	/** Notifier */
	private Notifier notifier;
	
	/** Database adapter */
	private DatabaseAdapter databaseAdapter;
	
	/** Locater */
	private Locater locater;
	
	private CountDownLatch countDownLatch;
	
	private AtomicInteger maxDistance = new AtomicInteger(1500);
	
	private AtomicInteger minAccuracy = new AtomicInteger(3000);
	
	private Map<String, WifiLocation> locations = new ConcurrentHashMap<String, WifiLocation>();

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
		this.notifier = new NotifierImpl(this,
				new NotificationManagerImpl((NotificationManager)getSystemService(NOTIFICATION_SERVICE)));
		this.databaseAdapter = new DatabaseAdapterImpl(this);
		this.locater = new LocaterImpl(
				new LocationManagerImpl((LocationManager)this.getSystemService(LOCATION_SERVICE)));
		this.countDownLatch = new CountDownLatch(1);
		
		this.maxDistance.set(Integer.valueOf(sharedPreferences.getString("settings_max_distance", "1500")));
		this.locater.setMaxAge(LOCATION_MAX_AGE);
	}
	
	@Override
	public void onDestroy() {
		locater.stop();
		databaseAdapter.close();
		super.onDestroy();
	}
	
	public void onLocationChanged(final Location location) {
		if(locater.isAccurateEnough(location, minAccuracy.get())) {
			
			Log.d(Inetify.LOG_TAG, String.format("Got location from %s with accuracy %s", 
					location.getProvider(), location.getAccuracy()));
			
			WifiLocation nearestLocation = getNearestLocationTo(location);
			
			notifier.locatify(location, nearestLocation);
			
			countDownLatch.countDown();
		}
	}

	@Override
	protected void onHandleIntent(final Intent intent) {
		
		getLocations();
		
		if(locations.size() == 0) {
			Log.d(Inetify.LOG_TAG, "No locations, doing nothing");
			return;
		}
		
		final boolean gpsEnabled = locater.isProviderEnabled(LocationManager.GPS_PROVIDER);
		final boolean useGPS = sharedPreferences.getBoolean("settings_use_gps", false);
		
		Log.d(Inetify.LOG_TAG, String.format("GPS enabled: %s", useGPS && gpsEnabled));

		try {
			minAccuracy.set(100);
			Log.d(Inetify.LOG_TAG, "Locating, accuracy: 100 m");
			startLocater(useGPS && gpsEnabled);
			countDownLatch.await(GET_LOCATION_TIMEOUT, TimeUnit.SECONDS);
			locater.stop();
			
			minAccuracy.set(3000);
			startLocater(false);
			Log.d(Inetify.LOG_TAG, "Locating, accuracy: 3000 m");
			countDownLatch.await(GET_LOCATION_TIMEOUT, TimeUnit.SECONDS);
			
		} catch (InterruptedException e) {
			// Ignore
		} finally {
			locater.stop();
		}
	}
	
	private void startLocater(final boolean useGPS) {
		handler.post(new Runnable() {
			public void run() {
				locater.start(CheckLocationIntentService.this, useGPS);
			}
		});
	}

	private void getLocations() {
		Cursor cursor = databaseAdapter.fetchLocations();
		
		try {
			while(cursor.moveToNext()) {
				WifiLocation wifiLocation = new WifiLocation();
				String bssid = cursor.getString(1);
				wifiLocation.setSSID(cursor.getString(2));
				wifiLocation.setName(cursor.getString(3));
				Location location = new Location(Locater.PROVIDER_DATABASE);
				location.setLatitude(cursor.getDouble(4));
				location.setLongitude(cursor.getDouble(5));
				location.setAccuracy(cursor.getFloat(6));
				wifiLocation.setLocation(location);
				locations.put(bssid, wifiLocation);
			}
		} finally {
			cursor.close();
		}
	}
	
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
}
