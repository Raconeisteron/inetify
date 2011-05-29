package net.luniks.android.inetify.test;

import net.luniks.android.inetify.R;
import android.test.AndroidTestCase;

public class RTest extends AndroidTestCase {
	
	public void testR() {
		
		assertNotNull(new R());
		assertNotNull(new R.attr());
		assertNotNull(new R.color());
		assertNotNull(new R.drawable());
		assertNotNull(new R.id());
		assertNotNull(new R.layout());
		assertNotNull(new R.string());
		assertNotNull(new R.style());
		assertNotNull(new R.xml());
		
	}

}
