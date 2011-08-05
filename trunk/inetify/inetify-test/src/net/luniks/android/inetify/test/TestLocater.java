package net.luniks.android.inetify.test;

import net.luniks.android.inetify.Locater;
import android.location.Location;

public class TestLocater implements Locater {
	
	private LocaterLocationListener listener;
	
	public void updateLocation(final Location location) {
		if(listener != null) {
			listener.onLocationChanged(location);
		}
	}
	
	// TODO Implement when needed
	public void setMaxAge(long maxAge) {

	}

	public void start(final LocaterLocationListener listener, boolean useGPS) {
		this.listener = listener;
	}

	// TODO Implement when needed
	public void stop() {

	}

	// TODO Implement when needed
	public Location getBestLastKnownLocation(final long maxAge) {
		return null;
	}

	// TODO Implement when needed
	public boolean isAccurateEnough(final Location location, final int meters) {
		return true;
	}

	// TODO Implement when needed
	public boolean isProviderEnabled(String provider) {
		return true;
	}

}
