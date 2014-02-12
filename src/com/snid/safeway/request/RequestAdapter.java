package com.snid.safeway.request;

import java.util.HashMap;
import java.util.Map;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.Request.Method;
import com.android.volley.toolbox.StringRequest;
import com.snid.safeway.Globals;
import com.snid.safeway.request.RequestManager;
import com.snid.safeway.request.MyXMLParser.snidResponse;

public class RequestAdapter implements Response.Listener<String>, Response.ErrorListener
{
	private RequestAdapterListener listener = null;
	
	public interface RequestAdapterListener
	{
		void onFinishRequest(int code, String message, String reason);
	}

	@Override
	public void onErrorResponse(VolleyError e)
	{
		failResponse();
		e.printStackTrace();
	}

	@Override
	public void onResponse(String xml)
	{
		if (null == xml || 0 == xml.length())
		{
			failResponse();
			return;
		}
		
		MyXMLParser parser = new MyXMLParser(xml);
		snidResponse response = parser.GetResponse();
		if (null == response)
		{
			failResponse();
			return;
		}
		
		if (null != listener)
		{
			listener.onFinishRequest(response.Code, response.Message, response.Reason);
		}
	}
	
	private void failResponse()
	{
		if (null != listener)
		{
			listener.onFinishRequest(-1, null, null);
		}		
	}
	
	public void SendSMS(RequestAdapterListener listener, final String phone_number)
	{
		RequestQueue reqQueue = RequestManager.getRequestQueue();
		if (null == reqQueue) return;
		
		this.listener = listener;

		String urlString = Globals.URL_NUMBER_REGISTRATION;

		StringRequest req = new StringRequest(Method.POST, urlString, this, this)
		{
			@Override
			protected Map<String, String> getParams() throws AuthFailureError
			{
				Map<String, String>  params = new HashMap<String, String>();  
	            params.put("telno", phone_number);
				return params;
			}			
		};
		
		reqQueue.add(req);
	}

	public void SendAuthNumber(RequestAdapterListener listener, final String phone_number, final String auth_number)
	{
		RequestQueue reqQueue = RequestManager.getRequestQueue();
		if (null == reqQueue) return;
		
		this.listener = listener;

		String urlString = Globals.URL_NUMBER_REGISTRATION_CHECK;

		StringRequest req = new StringRequest(Method.POST, urlString, this, this)
		{
			@Override
			protected Map<String, String> getParams() throws AuthFailureError
			{
				Map<String, String>  params = new HashMap<String, String>();  
	            params.put("telno", phone_number);
	            params.put("authno", auth_number);
				return params;
			}			
		};
		
		reqQueue.add(req);
	}

}
