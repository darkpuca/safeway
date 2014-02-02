package com.snid.safeway;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class MessagesActivity extends Activity
{
	private ListView messageList;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_messages);
		
		this.messageList = (ListView)findViewById(R.id.message_list);
		this.messageList.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, Globals.sample_strings));
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.messages, menu);
		return true;
	}

}
