package net.luniks.android.inetify;

import android.app.Activity;
import android.os.Bundle;

/**
 * Activity displaying a view containing some information about the app.
 * 
 * @author dode@luniks.net
 */
public class Help extends Activity {

	/** {@inheritDoc} */
	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		this.setContentView(R.layout.help);
	}
	
}
