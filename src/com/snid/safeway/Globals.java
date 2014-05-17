package com.snid.safeway;

public class Globals
{
	public static final String[] sample_strings = { "string1", "string2", "string3", "string4", "string5", "string6", "string7", "string8", "string9" };

	public static final String GCM_SENDER_ID = "1048918795666";

	public static final String URI_SERVER = "http://darc1004.com/";
	//public static final String URI_SERVER = "http://210.107.227.23/";
	public static final String URL_NUMBER_REGISTRATION			= URI_SERVER + "_app/send_auth_sms.php";
	public static final String URL_NUMBER_REGISTRATION_CHECK	= URI_SERVER + "_app/do_check_auth_no.php";
	public static final String URL_DEVICE_REGISTRATION			= URI_SERVER + "_app/do_regist_user.php";
	public static final String URL_DEVICE_KEEP_ALIVE			= URI_SERVER + "_app/do_check_uid.php";
	public static final String URL_GET_MESSAGES					= URI_SERVER + "_app/get_messages.php";
	public static final String URL_UPDATE_LAST_MESSAGE_ID		= URI_SERVER + "_app/do_update_smslogidx.php";
	
	public static final int RESPONSE_OK = 0;
	public static final int RESPONSE_FAIL = 1;
	
	
	public static final String EXTRA_MESSAGE = "message";
	public static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
	
    public static final String PROPERTY_REG_ID = "registration_id";
    public static final String PROPERTY_APP_VERSION = "appVersion";
	public static final String PROPERTY_AUTHORIZED_NUMBER = "authorized_number";
	public static final String PROPERTY_REGISTED_DEVICE = "registed_device";
	public static final String PROPERTY_PHONE_NUMBER = "phone_number";
	public static final String PROPERTY_USER_TYPE = "user_type";
	public static final String PROPERTY_LAST_RECEIVE_MESSAGE_ID = "last_msg_id";
	
	public static final int REQ_SEND_SMS				= 100;
	public static final int REQ_CHECK_AUTH				= 101;
	public static final int REQ_DEVICE_REGISTRATION		= 102;
	public static final int REQ_KEEP_ALIVE				= 103;
	public static final int REQ_GET_MESSAGES			= 104;
	public static final int REQ_UPDATE_LAST_MESSAGE_ID	= 105;


	public static final int INTENT_REQUEST_AUTH_NUMBER = 1000;
	public static final int INTENT_MESSAGE_LIST = 1001;

	public static final int USERTYPE_PARENTS = 200;
	public static final int USERTYPE_STUDENT = 201;
	public static final int USERTYPE_TEACHER = 202;
	public static final int USERTYPE_AFTER_TEACHER = 203;
	
	public static final String DATETIME_FORMAT_FOR_MESSAGE = "yyyy³â M¿ù dÀÏ a hh:mm";
	public static final String DATETIME_FORMAT_FOR_SERVER = "yyyy-MM-dd HH:mm:ss";

	public static final long INTRO_WAITING = 3000;
	public static final int REQUEST_TIMEOUT = 10000;

}
