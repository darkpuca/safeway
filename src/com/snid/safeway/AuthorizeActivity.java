package com.snid.safeway;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.snid.safeway.request.RequestAdapter.RequestAdapterListener;

public class AuthorizeActivity extends BaseActivity
implements OnClickListener, RequestAdapterListener
{
	private EditText sms_edit, auth_edit;
	private Button sms_button, auth_button;
	
	private String phone_number, auth_number;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_authorize);
		
		Spinner spinner = (Spinner) findViewById(R.id.regType);

		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.registration_types, android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(adapter);
		
		sms_edit = (EditText)findViewById(R.id.phoneNumEdit);
		auth_edit = (EditText)findViewById(R.id.certNumEdit);
		sms_button = (Button)findViewById(R.id.sendButton);
		auth_button = (Button)findViewById(R.id.certButton);
		
		sms_button.setOnClickListener(this);
		auth_button.setOnClickListener(this);
		
		auth_edit.setEnabled(false);
		auth_button.setEnabled(false);
		
		sms_edit.requestFocus();
	}

	@Override
	public void onClick(View view)
	{
		if (view.equals(sms_button))
		{
			this.phone_number = sms_edit.getText().toString();
			
			if (0 == phone_number.length())
			{
				Utils.GetDefaultTool().ShowMessageDialog(this, R.string.msg_empty_mobile_number);
				return;
			}
			
			if (false == prog.isShowing()) prog.show();
			
			req_type = REQ_SEND_SMS;
			setProgressMessage(R.string.msg_request_auth_number);
			req.SendSMS(this, phone_number);

		}
		else if (view.equals(auth_button))
		{
			auth_number = auth_edit.getText().toString();
			
			if (0 == auth_number.length())
			{
				Utils.GetDefaultTool().ShowMessageDialog(this, R.string.msg_empty_auth_number);
				return;
			}
			
			if (false == prog.isShowing()) prog.show();
			
			setProgressMessage(R.string.msg_request_auth_check);
			req_type = REQ_CHECK_AUTH;
			req.SendAuthNumber(this, phone_number, auth_number);
		}
	}

	@Override
	public void onFinishRequest(int code, String message, String reason, int user_type)
	{
		if (prog.isShowing()) prog.dismiss();
		
		if (REQ_SEND_SMS == req_type)
		{
			if (message.equalsIgnoreCase("done"))
			{
				sms_edit.setEnabled(false);
				sms_button.setEnabled(false);
				auth_edit.setEnabled(true);
				auth_button.setEnabled(true);
				
				auth_edit.requestFocus();
			}
			else
			{
				Utils.GetDefaultTool().ShowMessageDialog(this, R.string.msg_empty_auth_number);
			}
		}
		else if (REQ_CHECK_AUTH == req_type)
		{
			if (Globals.RESPONSE_OK == code)
			{
				Intent i = new Intent();
				i.putExtra(Globals.PROPERTY_PHONE_NUMBER, phone_number);
				i.putExtra(Globals.PROPERTY_USER_TYPE, user_type);
				setResult(RESULT_OK, i);
				finish();				
			}
			else if (Globals.RESPONSE_FAIL == code)
			{
				Utils.GetDefaultTool().ShowMessageDialog(this, R.string.msg_auth_fail);
			}
			
		}
	}
}
