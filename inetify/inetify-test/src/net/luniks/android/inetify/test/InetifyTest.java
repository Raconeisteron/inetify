package net.luniks.android.inetify.test;

import net.luniks.android.inetify.Inetify;
import android.test.ActivityInstrumentationTestCase2;
import android.widget.TextView;

public class InetifyTest extends ActivityInstrumentationTestCase2<Inetify> {

	private Inetify mActivity;
	private TextView mView;
	private String resourceString;

	public InetifyTest() {
		super("net.luniks.android.inetify", Inetify.class);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		mActivity = this.getActivity();
		mView = (TextView) mActivity.findViewById(net.luniks.android.inetify.R.id.textview);
		resourceString = mActivity.getString(net.luniks.android.inetify.R.string.hello);
	}

	public void testPreconditions() {
		assertNotNull(mView);
	}

	public void testText() {
		assertEquals(resourceString, (String) mView.getText());
	}

}
