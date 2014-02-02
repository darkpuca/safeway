package com.snid.safeway;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class MainActivity extends Activity
{
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// test registration activity
		Button registrationButton = (Button)findViewById(R.id.registration_button);
		registrationButton.setOnClickListener(new OnClickListener()
		{			
			@Override
			public void onClick(View v)
			{
				Intent i = new Intent(MainActivity.this, RegistrationActivity.class);
				startActivity(i);
			}
		});

		// test messages activity
		Button messagesButton = (Button)findViewById(R.id.messages_button);
		messagesButton.setOnClickListener(new OnClickListener()
		{			
			@Override
			public void onClick(View v)
			{
				Intent i = new Intent(MainActivity.this, MessagesActivity.class);
				startActivity(i);
			}
		});

		// test histories activity
		Button historiesButton = (Button)findViewById(R.id.histories_button);
		historiesButton.setOnClickListener(new OnClickListener()
		{			
			@Override
			public void onClick(View v)
			{
				Intent i = new Intent(MainActivity.this, HistoriesActivity.class);
				startActivity(i);
			}
		});

		// test notices activity
		Button noticesButton = (Button)findViewById(R.id.notices_button);
		noticesButton.setOnClickListener(new OnClickListener()
		{			
			@Override
			public void onClick(View v)
			{
				Intent i = new Intent(MainActivity.this, NoticesActivity.class);
				startActivity(i);
			}
		});
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{

		super.onActivityResult(requestCode, resultCode, data);
	}
}
