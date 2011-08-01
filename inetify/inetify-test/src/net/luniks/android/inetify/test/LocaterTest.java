package net.luniks.android.inetify.test;

import net.luniks.android.inetify.Locater;
import android.test.AndroidTestCase;

public class LocaterTest extends AndroidTestCase {
	
	public void testAccuracyFine() {
		assertEquals(100, Locater.Accuracy.FINE.getMeters());
	}
	
	public void testAccuracyCoarse() {
		assertEquals(1500, Locater.Accuracy.COARSE.getMeters());
	}
	
}
