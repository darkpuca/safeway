package com.snid.safeway;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class HistoriesActivity extends Activity
{
	private ListView historyList;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_histories);
		
		this.historyList = (ListView)findViewById(R.id.history_list);
		this.historyList.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, Globals.sample_strings));
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.histories, menu);
		return true;
	}

}
