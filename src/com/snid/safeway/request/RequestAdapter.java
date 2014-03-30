package com.snid.safeway.request;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request.Method;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.snid.safeway.Globals;
import com.snid.safeway.request.MyXMLParser.snidResponse;

public class RequestAdapter implements Response.Listener<String>, Response.ErrorListener
{
    static private String TAG = "SERVER"; 
	
	private RequestAdapterListener listener = null;
	
	public interface RequestAdapterListener
	{
		void onFinishRequest(int code, String message, String reason, int user_type);
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
		
//		System.out.println("response xml: " + xml);
		
		MyXMLParser parser = new MyXMLParser(xml);
		snidResponse response = parser.GetResponse();
		if (null == response)
		{
			Log.d(TAG, "response xml: " + xml);
			
			failResponse();
			return;
		}
		
		if (null != listener)
		{
			listener.onFinishRequest(response.Code, response.Message, response.Reason, response.UserType);
		}
	}
	
	private void failResponse()
	{
		if (null != listener)
		{
			listener.onFinishRequest(-1, null, null, 0);
		}		
	}
	
	public void SendSMS(RequestAdapterListener listener, final String phone_number)
	{
		RequestQueue reqQueue = RequestManager.getRequestQueue();
		if (null == reqQueue) return;
		
		this.listener = listener;

		String urlString = Globals.URL_NUMBER_REGISTRATION;
		Log.d(TAG, "server request: " + urlString);

		StringRequest req = new StringRequest(Method.POST, urlString, this, this)
		{
			@Override
			protected Map<String, String> getParams() throws AuthFailureError
			{
				Date now = new Date();
				Random rand = new Random(now.getTime());
				int rnumber = Math.abs(rand.nextInt());

				Map<String, String>  params = new HashMap<String, String>();  
	            params.put("telno", phone_number);
	            params.put("rnumber", Integer.toString(rnumber));
//				Log.d("SafeWay-Debug", params.toString());
	            
				return params;
			}			
		};
		
		req.setRetryPolicy(new DefaultRetryPolicy(Globals.REQUEST_TIMEOUT, 0, 0));
		
		reqQueue.add(req);
	}

	public void SendAuthNumber(RequestAdapterListener listener, final String phone_number, final String auth_number)
	{
		RequestQueue reqQueue = RequestManager.getRequestQueue();
		if (null == reqQueue) return;
		
		this.listener = listener;

		String urlString = Globals.URL_NUMBER_REGISTRATION_CHECK;
		Log.d(TAG, "server request: " + urlString);

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
		
		req.setRetryPolicy(new DefaultRetryPolicy(Globals.REQUEST_TIMEOUT, 0, 0));
		
		reqQueue.add(req);
	}

	public void SendDeviceRegistrationId(RequestAdapterListener listener, final String phone_number, final String registration_id)
	{
		RequestQueue reqQueue = RequestManager.getRequestQueue();
		if (null == reqQueue) return;
		
		this.listener = listener;

		String urlString = Globals.URL_DEVICE_REGISTRATION;
		Log.d(TAG, "server request: " + urlString);

		StringRequest req = new StringRequest(Method.POST, urlString, this, this)
		{
			@Override
			protected Map<String, String> getParams() throws AuthFailureError
			{
				Map<String, String>  params = new HashMap<String, String>();  
	            params.put("telno", phone_number);
	            params.put("uid", registration_id);
	            params.put("type", "A");
				return params;
			}			
		};
		
		req.setRetryPolicy(new DefaultRetryPolicy(Globals.REQUEST_TIMEOUT, 0, 0));
		
		reqQueue.add(req);
	}

	public void SendRegistrationKeepAlive(RequestAdapterListener listener, final String phone_number, final String registration_id)
	{
		RequestQueue reqQueue = RequestManager.getRequestQueue();
		if (null == reqQueue) return;
		
		this.listener = listener;

		String urlString = Globals.URL_DEVICE_KEEP_ALIVE;
		Log.d(TAG, "server request: " + urlString);

		StringRequest req = new StringRequest(Method.POST, urlString, this, this)
		{
			@Override
			protected Map<String, String> getParams() throws AuthFailureError
			{
				Map<String, String>  params = new HashMap<String, String>();  
	            params.put("telno", phone_number);
	            params.put("uid", registration_id);
				return params;
			}			
		};
		
		req.setRetryPolicy(new DefaultRetryPolicy(Globals.REQUEST_TIMEOUT, 0, 0));
		
		reqQueue.add(req);
	}
	
}
