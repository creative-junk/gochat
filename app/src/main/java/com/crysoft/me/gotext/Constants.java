package com.crysoft.me.gotext;

import android.net.Uri;

public class Constants {
	
	public static final Uri URI_SMS = Uri.parse("content://sms/");;
	
	public static final Uri URI_SMS_SENT = Uri.parse("content://sms/sent");
	
	public static final Uri URI_SMS_FAILED = Uri.parse("content://sms/failed");

	public static final Uri URI_SMS_CONVERSATIONS = Uri.parse("content://sms/conversations");
	
	public static final int SMS_RECEIVE = 1;
	
	public static final int SMS_SEND = 2;
	
	public static final int SMS_SEND_AND_RECEIVE = 3;
	
	
}
