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

import net.luniks.android.inetify.DatabaseAdapter;
import net.luniks.android.inetify.DatabaseAdapterImpl;
import net.luniks.android.inetify.Inetify;
import net.luniks.android.inetify.R;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.test.ActivityInstrumentationTestCase2;
import android.widget.ListView;
import android.widget.TwoLineListItem;

public class InetifyTest2 extends ActivityInstrumentationTestCase2<Inetify> {

	private Inetify activity;

	public InetifyTest2() {
		super("net.luniks.android.inetify", Inetify.class);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		
		this.getInstrumentation().getTargetContext().deleteDatabase("inetifydb");
		this.getInstrumentation().getTargetContext().deleteDatabase("inetifydb-journal");
		
		DatabaseAdapter databaseAdapter = new DatabaseAdapterImpl(this.getInstrumentation().getTargetContext());
		databaseAdapter.updateTestResult(0, ConnectivityManager.TYPE_WIFI, "Sputnik", true);
		
		activity = this.getActivity();
	}
	
	public void testTestWithLastTestResult() throws Exception {
		
		ListView listView = (ListView)activity.findViewById(R.id.listview_main);
		
		TwoLineListItem listItemTest = (TwoLineListItem)TestUtils.selectAndFindListViewChildAt(activity, listView, 0, 3000);
		
		assertTrue(listItemTest.isEnabled());
		
		assertEquals(activity.getString(R.string.main_title_test), listItemTest.getText1().getText());
		assertEquals("1/1/1970 1:00 AM OK (Sputnik)", listItemTest.getText2().getText());
		
	}
	
	public void testTestUpdateLastTestResult() throws Exception {
		
		DatabaseAdapter databaseAdapter = new DatabaseAdapterImpl(this.getInstrumentation().getTargetContext());
		databaseAdapter.updateTestResult(0, ConnectivityManager.TYPE_WIFI, "Gaulan", false);
		
		this.getInstrumentation().getTargetContext().sendBroadcast(new Intent(Inetify.UPDATE_TESTRESULT_ACTION));
		
		ListView listView = (ListView)activity.findViewById(R.id.listview_main);
		
		TwoLineListItem listItemTest = (TwoLineListItem)TestUtils.selectAndFindListViewChildAt(activity, listView, 0, 3000);
		
		assertTrue(listItemTest.isEnabled());
		
		assertEquals(activity.getString(R.string.main_title_test), listItemTest.getText1().getText());
		assertEquals("1/1/1970 1:00 AM Not OK (Gaulan)", listItemTest.getText2().getText());
		
	}

}
