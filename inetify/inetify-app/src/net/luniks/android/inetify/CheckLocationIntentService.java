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
	public static long GET_LOCATION_TIMEOUT = 30;
	
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
			
			notifier.locatify(nearestLocation);
			
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

		try {
			if(useGPS && gpsEnabled) {
				Log.d(Inetify.LOG_TAG, "Locating with GPS and 100 m");
				
				minAccuracy.set(100);
				startLocater(true);
				countDownLatch.await(GET_LOCATION_TIMEOUT, TimeUnit.SECONDS);
				stopLocater();
			} 
			
			Log.d(Inetify.LOG_TAG, "Locating with 100 m");
			startLocater(false);
			countDownLatch.await(GET_LOCATION_TIMEOUT, TimeUnit.SECONDS);

			Log.d(Inetify.LOG_TAG, "Locating with 1000 m");
			minAccuracy.set(3000);
			countDownLatch.await(GET_LOCATION_TIMEOUT, TimeUnit.SECONDS);
			
			Log.d(Inetify.LOG_TAG, "Locating with 3000 m");
			minAccuracy.set(3000);
			countDownLatch.await(GET_LOCATION_TIMEOUT, TimeUnit.SECONDS);
			
		} catch (InterruptedException e) {
			// Ignore
		}
	}
	
	private void startLocater(final boolean useGPS) {
		handler.post(new Runnable() {
			public void run() {
				locater.start(CheckLocationIntentService.this, useGPS);
			}
		});
	}
	
	private void stopLocater() {
		handler.post(new Runnable() {
			public void run() {
				locater.stop();
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
			if(currentLocation.getLocation().distanceTo(location) < shortestDistance) {
				nearestLocation = currentLocation;
			}
		}
		return nearestLocation;
	}
}
