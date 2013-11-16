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
package net.luniks.android.inetify.test;

import net.luniks.android.inetify.Locater.LocaterLocationListener;
import net.luniks.android.inetify.LocationList;
import net.luniks.android.inetify.LocationMapView;
import net.luniks.android.inetify.R;
import net.luniks.android.inetify.Utils;

import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.ItemizedOverlay;
import org.osmdroid.views.overlay.OverlayItem;

import android.app.AlertDialog;
import android.app.Instrumentation.ActivityMonitor;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.location.LocationManager;
import android.test.ActivityInstrumentationTestCase2;
import android.view.View;
import android.widget.TwoLineListItem;

// TODO These tests are slow and timing critical
public class LocationMapViewTest extends ActivityInstrumentationTestCase2<LocationMapView> {
	
	public LocationMapViewTest() {
		super("net.luniks.android.inetify", LocationMapView.class);
	}
	
	public void testShowLocation() throws InterruptedException {
		
		IntentFilter filter = new IntentFilter(LocationMapView.SHOW_LOCATION_ACTION);
		ActivityMonitor monitor = new ActivityMonitor(filter, null, false);
		this.getInstrumentation().addMonitor(monitor);
		
		Location location = new Location(LocationManager.GPS_PROVIDER);
		location.setLatitude(50.628707);
		location.setLongitude(3.538688);
		
		Intent intent = new Intent().setClass(this.getInstrumentation().getContext(), LocationMapView.class);
		intent.setAction(LocationMapView.SHOW_LOCATION_ACTION);
		intent.putExtra(LocationList.EXTRA_NAME, "TestLocation");
		intent.putExtra(LocationList.EXTRA_LOCATION, location);
		
		this.setActivityIntent(intent);
		
		LocationMapView activity = this.getActivity();
		
		TestUtils.waitForHitCount(monitor, 1, 60000);
		
		assertEquals(activity.getString(R.string.locationmapview_label_name, "TestLocation"), activity.getTitle());
		
		MapView mapView = (MapView)activity.findViewById(R.id.mapview_location);
		ItemizedOverlay<OverlayItem> overlay = (ItemizedOverlay<OverlayItem>)mapView.getOverlays().get(0);
		GeoPoint point = overlay.getItem(0).getPoint();
		
		assertEquals(Double.valueOf(location.getLatitude() * 1E6).intValue(), point.getLatitudeE6());
		assertEquals(Double.valueOf(location.getLongitude() * 1E6).intValue(), point.getLongitudeE6());
		
		this.getInstrumentation().removeMonitor(monitor);
		
		activity.finish();
	}
	
	public void testFindLocationNotFound() throws Exception {
		
		TestUtils.setStaticFieldValue(LocationMapView.class, "GET_LOCATION_TIMEOUT", 3 * 1000);
		
		IntentFilter filter = new IntentFilter(LocationMapView.FIND_LOCATION_ACTION);
		ActivityMonitor monitor = new ActivityMonitor(filter, null, false);
		this.getInstrumentation().addMonitor(monitor);
		
		Intent intent = new Intent().setClass(this.getInstrumentation().getContext(), LocationMapView.class);
		intent.setAction(LocationMapView.FIND_LOCATION_ACTION);
		intent.putExtra(LocationList.EXTRA_NAME, "TestLocation");
		
		this.setActivityIntent(intent);
		
		LocationMapView activity = this.getActivity();
		
		TestUtils.waitForHitCount(monitor, 1, 60000);
		
		assertEquals(activity.getString(R.string.locationmapview_label_name, "TestLocation"), activity.getTitle());
		
		TwoLineListItem statusView = (TwoLineListItem)activity.findViewById(LocationMapView.ID_STATUS_VIEW);
		
		assertEquals(View.VISIBLE, statusView.getVisibility());
		assertEquals(activity.getString(R.string.locationmapview_status1_searching), statusView.getText1().getText());
		assertEquals(activity.getString(R.string.locationmapview_status2_waiting), statusView.getText2().getText());
		
		AlertDialog confirmDialog = (AlertDialog)TestUtils.waitForCurrentDialogShowing(activity, 10000);
		
		TestUtils.performClickOnUIThread(activity, confirmDialog.getButton(AlertDialog.BUTTON_POSITIVE));
		
		TestUtils.waitForDialogNotShowing(confirmDialog, 10000);
		
		assertEquals(View.VISIBLE, statusView.getVisibility());
		assertEquals(activity.getString(R.string.locationmapview_status1_notfound), statusView.getText1().getText());
		assertEquals(activity.getString(R.string.locationmapview_status2_notfound), statusView.getText2().getText());
		
		this.getInstrumentation().removeMonitor(monitor);
		
		activity.finish();
	}
	
	public void testFindLocationFound() throws Exception {
		
		TestUtils.setStaticFieldValue(LocationMapView.class, "GET_LOCATION_TIMEOUT", 50 * 1000);
		
		IntentFilter filter = new IntentFilter(LocationMapView.FIND_LOCATION_ACTION);
		ActivityMonitor monitor = new ActivityMonitor(filter, null, false);
		this.getInstrumentation().addMonitor(monitor);
		
		Intent intent = new Intent().setClass(this.getInstrumentation().getContext(), LocationMapView.class);
		intent.setAction(LocationMapView.FIND_LOCATION_ACTION);
		intent.putExtra(LocationList.EXTRA_NAME, "TestLocation");
		
		this.setActivityIntent(intent);
		
		LocationMapView activity = this.getActivity();
		
		TestUtils.waitForHitCount(monitor, 1, 60000);
		
		assertEquals(activity.getString(R.string.locationmapview_label_name, "TestLocation"), activity.getTitle());
		
		TwoLineListItem statusView = (TwoLineListItem)activity.findViewById(LocationMapView.ID_STATUS_VIEW);
		
		assertEquals(View.VISIBLE, statusView.getVisibility());
		assertEquals(activity.getString(R.string.locationmapview_status1_searching), statusView.getText1().getText());
		assertEquals(activity.getString(R.string.locationmapview_status2_waiting), statusView.getText2().getText());
		
		Location location = new Location(LocationManager.GPS_PROVIDER);
		location.setLatitude(50.850357);
		location.setLongitude(4.351721);
		location.setAccuracy(33);
		
		LocaterLocationListener listener = (LocaterLocationListener)TestUtils.getFieldValue(activity, "locateTask");
		listener.onLocationChanged(location);
		
		// FIXME Wait for condition with timeout
		Thread.sleep(1000);
		
		MapView mapView = (MapView)activity.findViewById(R.id.mapview_location);
		ItemizedOverlay<OverlayItem> overlay = (ItemizedOverlay<OverlayItem>)mapView.getOverlays().get(0);
		GeoPoint point = overlay.getItem(0).getPoint();
		
		assertEquals(Double.valueOf(location.getLatitude() * 1E6).intValue(), point.getLatitudeE6());
		assertEquals(Double.valueOf(location.getLongitude() * 1E6).intValue(), point.getLongitudeE6());
		
		assertEquals(View.VISIBLE, statusView.getVisibility());
		assertEquals(activity.getString(R.string.locationmapview_status1_found), statusView.getText1().getText());
		assertEquals(activity.getString(R.string.locationmapview_status2_current, Utils.getLocalizedRoundedMeters(location.getAccuracy())), statusView.getText2().getText());
		
		this.getInstrumentation().removeMonitor(monitor);
		
		activity.finish();
	}

}
