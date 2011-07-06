package net.luniks.android.inetify;

import android.location.Location;


public interface Locater {

	void start(LocaterLocationListener listener);
	
	void stop();

	boolean isAccurateEnough(Location location);
	
	public interface LocaterLocationListener {
		
		void onNewLocation(Location location);
		
	}
	
}

