package net.luniks.android.inetify.test;

import net.luniks.android.inetify.Help;
import net.luniks.android.inetify.R;
import android.test.ActivityInstrumentationTestCase2;
import android.widget.TextView;

public class HelpTest extends ActivityInstrumentationTestCase2<Help> {

	private Help activity;

	public HelpTest() {
		super("net.luniks.android.inetify", Help.class);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		activity = this.getActivity();
	}

	public void testHelp() {
		
		TextView textViewName = (TextView)activity.findViewById(R.id.textview_help);
		
		assertEquals(activity.getString(R.string.help), (String)textViewName.getText().toString());

	}

}
