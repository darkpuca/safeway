package com.snid.safeway;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.GridView;

public class ParentsMainActivity extends Activity
{
	GridView menuGrid;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_parents_main);
		
		this.menuGrid = (GridView)findViewById(R.id.menu_grid);
		this.menuGrid.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, Globals.sample_strings));
		
	}


}
