package com.snid.safeway;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

public class RegistrationActivity extends Activity
{
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_registration);
		
		Spinner spinner = (Spinner) findViewById(R.id.regType);

		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.REGISTRATION_TYPES, android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(adapter);
		
	}
}
