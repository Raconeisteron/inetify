/*
 * Copyright 2011 Torsten Römer
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
