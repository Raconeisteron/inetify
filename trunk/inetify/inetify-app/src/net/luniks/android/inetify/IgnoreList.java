package net.luniks.android.inetify;

import android.app.ListActivity;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.SimpleCursorAdapter;

public class IgnoreList extends ListActivity {
	
	private DatabaseAdapter databaseAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ignorelist);
		databaseAdapter = new DatabaseAdapterImpl(this);
		insertTestData();
		populate();
	}
	
	private void insertTestData() {
		this.deleteDatabase("inetifydb");
		databaseAdapter.addIgnoredWifi("00:21:29:A2:48:80", "Celsten");
		databaseAdapter.addIgnoredWifi("00:11:22:33:44:55", "TestSSID1");
		databaseAdapter.addIgnoredWifi("00:66:77:88:99:00", "TestSSID2");
	}

	private void populate() {		
        Cursor cursor = databaseAdapter.fetchIgnoredWifis();
        startManagingCursor(cursor);
        
        SimpleCursorAdapter ignoredWifis = new SimpleCursorAdapter(this, android.R.layout.simple_list_item_2, cursor, 
        		new String[] { DatabaseAdapterImpl.IGNORELIST_COLUMN_SSID, DatabaseAdapterImpl.IGNORELIST_COLUMN_BSSID },
				new int[] { android.R.id.text1, android.R.id.text2 });
        
        setListAdapter(ignoredWifis);
    }

}
