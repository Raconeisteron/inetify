package net.luniks.android.inetify;

import android.location.Location;


public interface Locater {

	void start(LocaterLocationListener listener);
	
	void stop();

	boolean isAccurateEnough(Location location, Accuracy accuracy);
	
	public interface LocaterLocationListener {
		
		void onNewLocation(Location location);
		
	}
	
	public enum Accuracy {
		
		FINE(100), 
		COARSE(1500);
		
		int meters;
		
		Accuracy(final int meters) {
			this.meters = meters;
		}
		
		public int getMeters() {
			return meters;
		}
	}
	
}

