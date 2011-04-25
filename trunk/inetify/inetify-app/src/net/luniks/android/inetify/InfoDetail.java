package net.luniks.android.inetify;

import android.app.Activity;
import android.os.Bundle;
import android.text.Html;
import android.widget.TextView;

/**
 * Activity that shows detailed information about the status of internet connectivity.
 * 
 * @author dode
 */
public class InfoDetail extends Activity {

	public static String KEY_IS_EXPECTED_TITLE = "infodetail.is_expected_title";
	public static String KEY_TEXT = "infodetail.text";

	/** {@inheritDoc} */
	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		this.setContentView(R.layout.infodetail);

		Bundle extras = this.getIntent().getExtras();

		Boolean isExpectedTitle = null;
		String text = this.getString(R.string.infodetail_na);
		if(extras != null) {
			String infodetail = extras.getString(KEY_TEXT);
			if(infodetail != null && infodetail.length() > 0) {
				text = infodetail;
			}
			isExpectedTitle = extras.getBoolean(KEY_IS_EXPECTED_TITLE, false);
		}
		
		if(isExpectedTitle != null) {
			TextView textViewInfodetail = (TextView)findViewById(R.id.textview_infodetail);
			int drawableResid = 0;
			int textResid = 0;
			if(isExpectedTitle) {
				drawableResid = R.drawable.icon_ok;
				textResid = R.string.inetify_info_string_ok;
			} else {
				drawableResid = R.drawable.icon_nok;
				textResid = R.string.inetify_info_string_nok;
			}
			textViewInfodetail.setCompoundDrawablesWithIntrinsicBounds(drawableResid, 0, 0, 0);
			textViewInfodetail.setText(textResid);
		}

		TextView textViewInfodetailText = (TextView)findViewById(R.id.textview_infodetail_text);
		CharSequence styledText = Html.fromHtml(text);
		textViewInfodetailText.setText(styledText);

	}

}
