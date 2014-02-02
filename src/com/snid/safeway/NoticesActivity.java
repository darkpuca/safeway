package com.snid.safeway;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class NoticesActivity extends Activity
{
	private ListView noticeList;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_notices);
		
		this.noticeList = (ListView)findViewById(R.id.notices_list);
		this.noticeList.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, Globals.sample_strings));
		this.noticeList.setOnItemClickListener(new OnItemClickListener()
		{
			@Override
			public void onItemClick(AdapterView<?> adpater, View view, int position, long id)
			{
				Intent i = new Intent(NoticesActivity.this, NoticeDetailActivity.class);
				startActivity(i);
			}
		});
	}

}
