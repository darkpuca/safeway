package com.snid.safeway;

import java.text.DecimalFormat;

import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

import com.android.volley.RequestQueue;
import com.snid.safeway.request.RequestManager;

public class Utils
{
	private static Utils defaultTool = null;
	
	
	public static Utils GetDefaultTool()
	{
		if (null == defaultTool)
			defaultTool = new Utils();
		
		return defaultTool;
	}
	
	public Utils()
	{

	}
	
	public void ShowMessageDialog(Context context, int messageId)
	{
		new AlertDialog.Builder(context)
		.setTitle(R.string.information)
		.setMessage(messageId)
		.setNeutralButton(R.string.close, null)
		.show();
	}

	public void ShowFinishDialog(final Context context, int messageId)
	{
		AlertDialog.Builder closeDlg = new AlertDialog.Builder(context);
		closeDlg.setCancelable(false);
		closeDlg.setTitle(R.string.information);
		closeDlg.setMessage(messageId);
		closeDlg.setNeutralButton(R.string.close, new DialogInterface.OnClickListener()
		{					
			@Override
			public void onClick(DialogInterface dialog, int which)
			{
				RequestQueue reqQueue = RequestManager.getRequestQueue();
				if (null != reqQueue)
					reqQueue.cancelAll(context);
				
				System.exit(0);
			}
		});
		closeDlg.show();
	}
	
	public void ShowGpsSettingWithDialog(Context context)
	{
		final Context baseContext = context;
		
		AlertDialog.Builder dlg = new AlertDialog.Builder(context);
		dlg.setCancelable(true);
		dlg.setTitle(R.string.information);
		dlg.setMessage(R.string.msg_gps_setting_confirm);
		dlg.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener()
		{			
			@Override
			public void onClick(DialogInterface dialog, int which)
			{
	        	// show gps setting activity
	        	Intent i = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
	        	baseContext.startActivity(i);
			}
		});
		dlg.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener()
		{			
			@Override
			public void onClick(DialogInterface dialog, int which)
			{
				dialog.dismiss();
			}
		});
		dlg.show();		        	
	}
	
	public String DecimalNumberString(double value)
	{
		DecimalFormat format = new DecimalFormat("###,###,###");
        String strVal = format.format(value);
        return strVal;
	}

	public String DecimalNumberString(long value)
	{
		DecimalFormat format = new DecimalFormat("###,###");
        String strVal = format.format(value);
        return strVal;
	}

	public String DecimalNumberString(int value)
	{
		DecimalFormat format = new DecimalFormat("###,###");
        String strVal = format.format(value);
        return strVal;
	}
	
	public void FinishApp(Context context)
	{
		ActivityManager exit = (ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);
		exit.restartPackage(context.getPackageName());
	}

	
}
