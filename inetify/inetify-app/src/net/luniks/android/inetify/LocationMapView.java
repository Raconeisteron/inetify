package net.luniks.android.inetify;

import java.util.List;

import android.graphics.drawable.Drawable;
import android.os.Bundle;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;

/**
 * MapActivity that shows a location (of a Wifi network) on a Google map.
 * 
 * @author torsten.roemer@luniks.net
 */
public class LocationMapView extends MapActivity {
	
	/** Extra to pass the SSID with the intent */
	public static final String EXTRA_SSID = "ssid";
	
	/** Extra to pass the latitude with the intent */
	public static final String EXTRA_LAT = "lat";
	
	/** Extra to pass the longitude with the intent */
	public static final String EXTRA_LON = "lon";
	
	/** The Google map view. */
	private MapView mapView;
	
	/** List of map overlays */
	private List<Overlay> mapOverlays;

	/**
	 * Creates the map view and location marker, and "animates" to the location.
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		this.setContentView(R.layout.locationmapview);
		
		final String ssid = this.getIntent().getStringExtra(EXTRA_SSID);
		final Double latE6 = this.getIntent().getDoubleExtra(EXTRA_LAT, 0) * 1E6;
		final Double lonE6 = this.getIntent().getDoubleExtra(EXTRA_LON, 0) * 1E6;
		
		this.setTitle(this.getString(R.string.locationmapview_label, ssid));
		
		mapView = (MapView)findViewById(R.id.mapview);
		mapView.setBuiltInZoomControls(true);
		mapOverlays = mapView.getOverlays();
		
		GeoPoint point = new GeoPoint(latE6.intValue(), lonE6.intValue());
		
		Drawable drawable = this.getResources().getDrawable(R.drawable.icon).mutate();
		drawable.setAlpha(85);
		SimpleItemizedOverlay itemizedOverlay = new SimpleItemizedOverlay(drawable, 0, false);
		OverlayItem overlayItem = new OverlayItem(point, ssid, "");
		itemizedOverlay.addOverlay(overlayItem);
		mapOverlays.add(itemizedOverlay);
		
		MapController mapController = mapView.getController();
        mapController.animateTo(point);
	}

	/**
	 * There is no route to be displayed.
	 */
	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}

}
