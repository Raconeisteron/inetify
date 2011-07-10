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

public class LocationMapView extends MapActivity {
	
	public static final String EXTRA_BSSID = "bssid";
	public static final String EXTRA_SSID = "ssid";
	public static final String EXTRA_LAT = "lat";
	public static final String EXTRA_LON = "lon";
	
	private MapView mapView;
	private List<Overlay> mapOverlays;

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

	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}

}
