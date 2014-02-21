/*
 * Copyright 2011 Torsten Römer
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.luniks.android.inetify;

import java.util.List;
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
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.PowerManager;
import android.preference.PreferenceManager;

public class LocationIntentService extends IntentService implements LocaterLocationListener {
	
	/** Maximum age of a last known location in milliseconds */
	private static final long LOCATION_MAX_AGE = 60 * 1000;
	
	/** Minimum fine accuracy */
	private static final int LOCATION_MIN_ACC_FINE = 100;
	
	/** Minimum coarse accuracy */
	private static final int LOCATION_MIN_ACC_COARSE = 5000;

	/** Tag of the wake lock */
	public static final String WAKE_LOCK_TAG = "net.luniks.android.inetify.LocationIntentService";
	
	/** Timeout in milliseconds for getting a location */
	private static long GET_LOCATION_TIMEOUT = 60 * 1000;
	
	/** Timeout in milliseconds for getting a location when using GPS */
	private static long GET_LOCATION_TIMEOUT_GPS = 30 * 1000;
	
	/** Wake lock, released when the count down latch releases the worker thread */
	static volatile PowerManager.WakeLock wakeLock;
	
	/** Flag to indicate that this service processed the first intent sent to it */
	private final AtomicBoolean ranOnce = new AtomicBoolean(false);
	
	/** CountDownLatch used to let the worker thread wait until a location was found */
	private final CountDownLatch latch = new CountDownLatch(1);
	
	/** Flag to indicate that a location was found */
	private final AtomicBoolean found = new AtomicBoolean(false);
	
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

	/**
	 * Creates an instance with a name.
	 */
	public LocationIntentService() {
		super("LocationIntentService");
		this.setIntentRedelivery(true);
	}
	
	/**
	 * Performs initialization.
	 */
	@Override
	public void onCreate() {
		super.onCreate();
		
		ranOnce.set(false);
		
		handler = new Handler(this.getMainLooper());
		sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
		wifiManager = new WifiManagerImpl((WifiManager)getSystemService(WIFI_SERVICE));
		notifier = new NotifierImpl(this,
				new NotificationManagerImpl((NotificationManager)getSystemService(NOTIFICATION_SERVICE)));
		
		// Some dependencies lazily initialized for testing purposes
		if(locationManager == null) {
			locationManager = new LocationManagerImpl((LocationManager)getSystemService(LOCATION_SERVICE));
		}
		if(databaseAdapter == null) {
			databaseAdapter = new DatabaseAdapterImpl(this);
		}
		if(connectivityManager == null) {
			connectivityManager = new ConnectivityManagerImpl((ConnectivityManager)getSystemService(CONNECTIVITY_SERVICE));
		}
		if(locater == null) {
			locater = new LocaterImpl(locationManager);
		}
	}
	
	/**
	 * Stops the locater if it is not already stopped as it should
	 * have been and closes the database.
	 */
	@Override
	public void onDestroy() {
		latch.countDown();
		locater.stop();
		databaseAdapter.close();
	}
	
	/**
	 * Called on the main thread when a location was found, stops the locater, 
	 * gets the nearest Wifi location and gives a notification and enables Wifi
	 * depending on some settings and conditions.
	 */
	public void onLocationChanged(final Location location) {
		
		locater.stop();
		found.set(true);
		
		WifiLocation nearestLocation = databaseAdapter.getNearestLocationTo(location);
		
		if(nearestLocation == null) {
			return;
		}
		
		boolean autoWifi  = sharedPreferences.getBoolean(Settings.LOCATION_AUTO_WIFI, false);
		boolean notification  = sharedPreferences.getBoolean(Settings.LOCATION_CHECK, false);
		int maxDistance = Integer.valueOf(sharedPreferences.getString(Settings.LOCATION_MAX_DISTANCE, "1500"));
		
		// Log.d(Inetify.LOG_TAG, String.format("Got location from %s with accuracy %s, distance to %s is %s, max. distance is %s", 
		// 		location.getProvider(), location.getAccuracy(), nearestLocation.getName(), nearestLocation.getDistance(), maxDistance));
		
		if(nearestLocation.getDistance() <= maxDistance) {
			locationNear(location, nearestLocation, autoWifi, notification);
		} else {
			locationFar(autoWifi, notification);
		}
		
		latch.countDown();
	}

	/**
	 * Does the work and releases the wake lock, but only for the first 
	 * intent sent to this instance of the service. Any additional intents 
	 * sent to the same instance are ignored until the service stops and
	 * is created again.
	 */
	@Override
	protected void onHandleIntent(final Intent intent) {
				
		// Log.d(Inetify.LOG_TAG, "LocationIntentService onHandleIntent");
		
		try {
			if(intent != null && ! ranOnce.get()) {
				
				// Skip if connected to Wifi as this implies we are near a Wifi network -
				// so no need to waste energy to find out if we are or not.
				if (! isWifiConnectedOrConnecting()) {
					checkAndLocate();
				} else {
					// Log.d(Inetify.LOG_TAG, String.format("Skipping locating since Wifi is connected"));
				}
			}
		} finally {
			ranOnce.set(true);
			
			if(wakeLock != null) {
				if(wakeLock.isHeld()) {
					wakeLock.release();
					
					// Log.d(Inetify.LOG_TAG, String.format("Released wake lock"));
				}
				wakeLock = null;
			}
		}

	}
	
	/**
	 * Checks some preconditions and tries to find a location.
	 */
	private void checkAndLocate() {
		
		if(! isAnyProviderEnabled()) {
			// Log.d(Inetify.LOG_TAG, "No location provider enabled, skipping");
			return;
		}
		
		if(! databaseAdapter.hasLocations()) {
			// Log.d(Inetify.LOG_TAG, "No locations, skipping");
			return;
		}
		
		found.set(false);
				
		boolean useGPS = locater.isProviderEnabled(LocationManager.GPS_PROVIDER) && 
						 sharedPreferences.getBoolean(Settings.LOCATION_USE_GPS, false);

		locate(LOCATION_MIN_ACC_FINE, false);
		if(! found.get()) {
			if(useGPS) {
				locate(LOCATION_MIN_ACC_FINE, true);
			} else {
				locate(LOCATION_MIN_ACC_COARSE, false);
			}
		}
	}
	
	/**
	 * Starts the locater (registers for location updates) on the main thread to 
	 * find a location with the given minimum accuracy, using GPS or not, and lets
	 * the worker thread wait until a location was found or a timeout expired.
	 * @param minAccuracy
	 * @param useGPS
	 */
	private void locate(final int minAccuracy, final boolean useGPS) {				
		try {
			handler.post(new Runnable() {
				public void run() {
					locater.start(LocationIntentService.this, LOCATION_MAX_AGE, minAccuracy, useGPS);
				}
			});
			
			long timeout = useGPS ? GET_LOCATION_TIMEOUT_GPS : GET_LOCATION_TIMEOUT;			
			latch.await(timeout, TimeUnit.MILLISECONDS);
		} catch(InterruptedException e) {
			// Ignore
		} finally {
			locater.stop();
		}
	}
	
	/**
	 * Called when the found location is near enough a Wifi location in respect to the user's
	 * "max distance" setting, enabling Wifi and giving a notification depending on some settings
	 * and conditions.
	 * @param location
	 * @param nearestLocation
	 * @param autoWifi
	 * @param notification
	 */
	private void locationNear(final Location location, final WifiLocation nearestLocation, 
			final boolean autoWifi, final boolean notification) {
		
				if(autoWifi && ! isWifiEnabled()) {
					wifiManager.setWifiEnabled(true);
						
					// Log.d(Inetify.LOG_TAG, "Enabled Wifi");
				} else {
					// Log.d(Inetify.LOG_TAG, "Not enabling Wifi since it is already enabled");
				}
				
				if(notification && ! isWifiConnectedOrConnecting()) {
					notifier.locatify(location, nearestLocation);
				} else {
					// Log.d(Inetify.LOG_TAG, "Not issuing a notification since Wifi is connected");
				}
	}

	/**
	 * Called when the found location is not near enough a Wifi location in respect to the user's
	 * "max distance" setting, disabling wifi and clearing an existing notification depending
	 * on some settings and conditions.
	 * @param autoWifi
	 * @param notification
	 */
	private void locationFar(final boolean autoWifi, final boolean notification) {
		
		if(autoWifi) {
			if(! isWifiEnabled() || isWifiEnabling() || isWifiConnectedOrConnecting()) {
				// Log.d(Inetify.LOG_TAG, "Wifi not disabled because it is not enabled, enabling, connecting or connected");
			} else {
				wifiManager.setWifiEnabled(false);
				
				// Log.d(Inetify.LOG_TAG, "Disabled Wifi");
			}
		}
		
		notifier.locatify(null, null);
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
    
    private boolean isWifiEnabled() {
    	int wifiState = wifiManager.getWifiState();
    	return wifiState == WifiManager.WIFI_STATE_ENABLED;
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
