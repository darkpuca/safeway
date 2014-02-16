package com.snid.safeway;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap.CompressFormat;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.snid.safeway.request.ImageCacheManager;
import com.snid.safeway.request.ImageCacheManager.CacheType;
import com.snid.safeway.request.RequestManager;


public class MainApplication extends Application
{
	private static String phone_number;
	private static int user_type;
	
	private static int DISK_IMAGECACHE_SIZE = 1024*1024*10;
	private static CompressFormat DISK_IMAGECACHE_COMPRESS_FORMAT = CompressFormat.PNG;
	private static int DISK_IMAGECACHE_QUALITY = 100;  //PNG is lossless so quality is ignored but must be provided
	
	private ConnectivityManager connectivity;
	private NetworkInfo wifiNetInfo, mobileNetInfo;
	
	
	public static String getPhoneNumber() {
		return phone_number;
	}

	public static void setPhoneNumber(String number) {
		phone_number = number;
	}

	public static int getUserType() {
		return user_type;
	}

	public static void setUserType(int type) {
		user_type = type;
	}



	@Override
	public void onCreate()
	{
		super.onCreate();

		init();
		
		SharedPreferences prefs = getSharedPreferences(MainActivity.class.getSimpleName(), Context.MODE_PRIVATE);
		boolean is_registed = prefs.getBoolean(Globals.PROPERTY_REGISTED_DEVICE, false);
		if (is_registed)
		{
			phone_number = prefs.getString(Globals.PROPERTY_PHONE_NUMBER, "");
			user_type = prefs.getInt(Globals.PROPERTY_USER_TYPE, 0);
		}

		
	}
	
	

	/**
	 * Intialize the request manager and the image cache 
	 */
	private void init()
	{
		RequestManager.init(this);
		createImageCache();
	}
	
	/**
	 * Create the image cache. Uses Memory Cache by default. Change to Disk for a Disk based LRU implementation.  
	 */
	private void createImageCache(){
		ImageCacheManager.getInstance().init(this,
				this.getPackageCodePath()
				, DISK_IMAGECACHE_SIZE
				, DISK_IMAGECACHE_COMPRESS_FORMAT
				, DISK_IMAGECACHE_QUALITY
				, CacheType.MEMORY);
	}
	
	public boolean IsNetworkAvailable()
	{
		connectivity = (ConnectivityManager)this.getSystemService(Context.CONNECTIVITY_SERVICE);
		wifiNetInfo = connectivity.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		mobileNetInfo = connectivity.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
		
//		Toast.makeText(m_baseActivity, "wifi: " + wifiNetInfo.isConnected() + " mobile: " + mobileNetInfo.isConnected(), Toast.LENGTH_SHORT).show();
		
		if (false == wifiNetInfo.isAvailable() && false == mobileNetInfo.isAvailable())
			return false;

		return true;
	}
	
	public boolean IsGpsAvailable()
	{
        LocationManager lm = (LocationManager)this.getSystemService(Context.LOCATION_SERVICE);
        return lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
	}
}