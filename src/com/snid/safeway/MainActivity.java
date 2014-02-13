package com.snid.safeway;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.snid.safeway.request.RequestAdapter;
import com.snid.safeway.request.RequestAdapter.RequestAdapterListener;

public class MainActivity extends BaseActivity implements RequestAdapterListener
{
	private GoogleCloudMessaging gcm;
    private AtomicInteger msgId = new AtomicInteger();
    private SharedPreferences prefs;
    private Context context;

	private static final int REQ_DEVICE_REGISTRATION = 100;

    private String phone_number, reg_id;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		this.context = this;
	    this.prefs = getPreferences(context);

		
	    // Check device for Play Services APK.
	    if (false == checkPlayServices())
	    {
	        // If this check succeeds, proceed with normal processing.
	        // Otherwise, prompt user to get valid Play Services APK.
//	    	Toast.makeText(this, "Invalid PlayServices", Toast.LENGTH_LONG).show();
	    	Utils.GetDefaultTool().ShowFinishDialog(context, R.string.msg_play_service_disable);
	    }
	    else
	    {
	    	this.gcm = GoogleCloudMessaging.getInstance(this);
            this.reg_id = getRegistrationId(context);

            if (this.reg_id.isEmpty())
                registerInBackground();

            // show device registration id
            Toast.makeText(context, this.reg_id, Toast.LENGTH_LONG).show();
            
            if (authorizeProc() && registrationProc())
            {
            	showMessageActivity();
            }
	    }
	}

	@Override
	protected void onResume()
	{
		super.onResume();
		
		checkPlayServices();
	}


	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		if (Globals.INTENT_REQUEST_AUTH_NUMBER == requestCode)
		{
			if (RESULT_OK == resultCode)
			{
				this.phone_number = data.getExtras().getString(Globals.PROPERTY_PHONE_NUMBER);
				storePhoneNumber(this, this.phone_number);
				
				if (registrationProc())
				{
					showMessageActivity();
				}
			}
			else
			{
				Utils.GetDefaultTool().ShowFinishDialog(context, R.string.msg_phone_authorize_fail);
			}
		}

		super.onActivityResult(requestCode, resultCode, data);
	}
	
	private boolean authorizeProc()
	{
	    boolean is_authorized = prefs.getBoolean(Globals.PROPERTY_AUTHORIZED_NUMBER, false);
	    
	    if (false == is_authorized)
	    {
	    	// 전화번호 인증이 되지 않았을 경우 인증 화면 전환.
	    	Intent i = new Intent(this, AuthorizeActivity.class);
	    	startActivityForResult(i, Globals.INTENT_REQUEST_AUTH_NUMBER);
	    	return false;
	    }

	    return true;
	}
	
	private boolean registrationProc()
	{
		boolean is_registed = prefs.getBoolean(Globals.PROPERTY_REGISTED_DEVICE, false);

		if (false == is_registed)
		{
			String phone_number = prefs.getString(Globals.PROPERTY_PHONE_NUMBER, "");
			
			if (false == prog.isShowing()) prog.show();
			
			setProgressMessage(R.string.msg_request_regist_device_id);
			req_type = REQ_DEVICE_REGISTRATION;
			req.SendDeviceRegistrationId(this, phone_number, reg_id);
		}
		
		return true;
	}
	
	private void storePhoneNumber(Context context, String phone_number)
	{
	    final SharedPreferences prefs = getPreferences(context);
	    int appVersion = getAppVersion(context);
	    Log.i("safeway", "Saving regId on app version " + appVersion);
	    
	    SharedPreferences.Editor editor = prefs.edit();
	    editor.putString(Globals.PROPERTY_PHONE_NUMBER, phone_number);
	    editor.putBoolean(Globals.PROPERTY_AUTHORIZED_NUMBER, true)
;	    editor.commit();
	}

	private void storeDeviceRegisted(Context context, boolean registed)
	{
	    final SharedPreferences prefs = getPreferences(context);
	    int appVersion = getAppVersion(context);
	    Log.i("safeway", "Saving regId on app version " + appVersion);
	    
	    SharedPreferences.Editor editor = prefs.edit();
	    editor.putBoolean(Globals.PROPERTY_REGISTED_DEVICE, registed)
;	    editor.commit();
	}

	private void showMessageActivity()
	{
		Intent i = new Intent(this, MessagesActivity.class);
		startActivity(i);
	}

	
	/**
	 * Check the device to make sure it has the Google Play Services APK. If
	 * it doesn't, display a dialog that allows users to download the APK from
	 * the Google Play Store or enable it in the device's system settings.
	 */
	private boolean checkPlayServices()
	{
	    int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
	    if (resultCode != ConnectionResult.SUCCESS)
	    {
	        if (GooglePlayServicesUtil.isUserRecoverableError(resultCode))
	        {
	            GooglePlayServicesUtil.getErrorDialog(resultCode, this,
	                    Globals.PLAY_SERVICES_RESOLUTION_REQUEST).show();
	        }
	        else
	        {
	            Log.i("PlayServices", "This device is not supported.");
	            finish();
	        }
	        return false;
	    }
	    return true;
	}
	
	/**
	 * Gets the current registration ID for application on GCM service.
	 * 
	 * If result is empty, the app needs to register.
	 *
	 * @return registration ID, or empty string if there is no existing
	 *         registration ID.
	 */
	private String getRegistrationId(Context context)
	{
	    final SharedPreferences prefs = getPreferences(context);
	    String registrationId = prefs.getString(Globals.PROPERTY_REG_ID, "");
	    if (registrationId.isEmpty()) {
	        Log.i("GCM", "Registration not found.");
	        return "";
	    }
	    
	    // Check if app was updated; if so, it must clear the registration ID
	    // since the existing regID is not guaranteed to work with the new
	    // app version.
	    int registeredVersion = prefs.getInt(Globals.PROPERTY_APP_VERSION, Integer.MIN_VALUE);
	    int currentVersion = getAppVersion(context);
	    
	    if (registeredVersion != currentVersion)
	    {
	        Log.i("GCM", "App version changed.");
	        return "";
	    }

	    return registrationId;
	}

	/**
	 * @return Application's {@code SharedPreferences}.
	 */
	private SharedPreferences getPreferences(Context context)
	{
	    // This sample app persists the registration ID in shared preferences, but
	    // how you store the regID in your app is up to you.
	    return getSharedPreferences(MainActivity.class.getSimpleName(), Context.MODE_PRIVATE);
	}
	
	/**
	 * @return Application's version code from the {@code PackageManager}.
	 */
	private static int getAppVersion(Context context)
	{
	    try
	    {
	        PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
	        return packageInfo.versionCode;
	    }
	    catch (NameNotFoundException e)
	    {
	        // should never happen
	        throw new RuntimeException("Could not get package name: " + e);
	    }
	}
	
	/**
	 * Registers the application with GCM servers asynchronously.
	 * <p>
	 * Stores the registration ID and app versionCode in the application's
	 * shared preferences.
	 */
	private void registerInBackground()
	{
		new gcmRegisterTask().execute(null, null, null);
	}
	
	/**
	 * Sends the registration ID to your server over HTTP, so it can use GCM/HTTP
	 * or CCS to send messages to your app. Not needed for this demo since the
	 * device sends upstream messages to a server that echoes back the message
	 * using the 'from' address in the message.
	 */
	private void sendRegistrationIdToBackend()
	{
	    // Your implementation here.
		// TODO: 서버에 device registration id 전송.
	}
	
	/**
	 * Stores the registration ID and app versionCode in the application's
	 * {@code SharedPreferences}.
	 *
	 * @param context application's context.
	 * @param regId registration ID
	 */
	private void storeRegistrationId(Context context, String regId)
	{
	    final SharedPreferences prefs = getPreferences(context);
	    int appVersion = getAppVersion(context);
	    Log.i("GCM", "Saving regId on app version " + appVersion);
	    
	    SharedPreferences.Editor editor = prefs.edit();
	    editor.putString(Globals.PROPERTY_REG_ID, regId);
	    editor.putInt(Globals.PROPERTY_APP_VERSION, appVersion);
	    editor.commit();
	}
	
	private class gcmRegisterTask extends AsyncTask<Void, Void, String>
	{
		protected String doInBackground(Void... params)
		{
			String msg = "";
			try
			{
				if (gcm == null)
				{
					gcm = GoogleCloudMessaging.getInstance(context);
				}
	                
				reg_id = gcm.register(Globals.GCM_SENDER_ID);
				msg = "Device registered, registration ID=" + reg_id;

				// You should send the registration ID to your server over HTTP,
				// so it can use GCM/HTTP or CCS to send messages to your app.
				// The request to your server should be authenticated if your app
				// is using accounts.
				sendRegistrationIdToBackend();

				// For this demo: we don't need to send it because the device
				// will send upstream messages to a server that echo back the
				// message using the 'from' address in the message.

				// Persist the regID - no need to register again.
				storeRegistrationId(context, reg_id);
			}
			catch (final IOException ex)
			{
				msg = "Error :" + ex.getMessage();
				// If there is an error, don't just keep trying to register.
				// Require the user to click a button again, or perform
				// exponential back-off.
			}
			return msg;
		}

        protected void onPostExecute(String msg)
        {
        	Log.i("GCM", msg);
        	Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
        }
	}

	@Override
	public void onFinishRequest(int code, String message, String reason)
	{
		if (prog.isShowing()) prog.dismiss();
		
		if (REQ_DEVICE_REGISTRATION == req_type)
		{
			// test value
			code = 0;
			
			if (0 == code || 1 == code)
			{
				// 0: ok, 1: already
				storeDeviceRegisted(context, true);
				showMessageActivity();
			}
			else
			{
				// error
				Utils.GetDefaultTool().ShowFinishDialog(context, R.string.msg_device_registration_fail);
			}
		}
	}
}
