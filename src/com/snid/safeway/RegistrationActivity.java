package com.snid.safeway;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.snid.safeway.request.RequestAdapter;
import com.snid.safeway.request.RequestAdapter.RequestAdapterListener;

public class RegistrationActivity extends Activity
implements OnClickListener, RequestAdapterListener
{
	private RequestAdapter req;
	private int req_type = 0;
	
	private EditText sms_edit, auth_edit;
	private Button sms_button, auth_button;
	private ProgressDialog prog;
	
	private static final int REQ_SEND_SMS	= 100;
	private static final int REQ_CHECK_AUTH	= 101;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_registration);
		
		req = new RequestAdapter();
		
		// prepare progress dialog
		prog = new ProgressDialog(this);
		prog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		prog.setCancelable(false);
		prog.setMessage(getResources().getString(R.string.msg_send_request));

		
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
	}

	@Override
	public void onClick(View view)
	{
		if (view.equals(sms_button))
		{
			String number = sms_edit.getText().toString();
			
			if (0 == number.length())
			{
				Utils.GetDefaultTool().ShowMessageDialog(this, R.string.msg_empty_mobile_number);
				return;
			}
			
			if (false == prog.isShowing()) prog.show();
			
			req_type = REQ_SEND_SMS;
			req.SendSMS(this, number);

		}
		else if (view.equals(auth_button))
		{
			String number = sms_edit.getText().toString();
			String authNumber = auth_edit.getText().toString();
			
			if (0 == number.length())
			{
				Utils.GetDefaultTool().ShowMessageDialog(this, R.string.msg_empty_mobile_number);
				return;
			}

			if (0 == authNumber.length())
			{
				Utils.GetDefaultTool().ShowMessageDialog(this, R.string.msg_empty_auth_number);
				return;
			}
			
			if (false == prog.isShowing()) prog.show();
			
			req_type = REQ_CHECK_AUTH;
			req.SendAuthNumber(this, number, authNumber);
		}
	}

	@Override
	public void onFinishRequest(int code, String message, String reason)
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
			// test return value;
			code = 0;
			
			if (Globals.RESPONSE_OK == code)
			{
				Utils.GetDefaultTool().ShowMessageDialog(this, R.string.msg_auth_success);
			}
			else if (Globals.RESPONSE_FAIL == code)
			{
				Utils.GetDefaultTool().ShowMessageDialog(this, R.string.msg_auth_fail);
			}
			
		}
	}
}
