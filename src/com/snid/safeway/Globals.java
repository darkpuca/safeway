package com.snid.safeway;

public class Globals
{
	public static final String[] sample_strings = { "string1", "string2", "string3", "string4", "string5", "string6", "string7", "string8", "string9" };

	public static final String GCM_SENDER_ID = "1048918795666";

	public static final String URI_SERVER = "http://210.107.227.23/";
	public static final String URL_NUMBER_REGISTRATION			= URI_SERVER + "_app/send_auth_sms.php";
	public static final String URL_NUMBER_REGISTRATION_CHECK	= URI_SERVER + "_app/do_check_auth_no.php";
	public static final String URL_DEVICE_REGISTRATION			= URI_SERVER + "_app/do_regist_user.php";
	
	
	
	
	public static final String EXTRA_MESSAGE = "message";
    public static final String PROPERTY_REG_ID = "registration_id";
    public static final String PROPERTY_APP_VERSION = "appVersion";
	public static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

}
