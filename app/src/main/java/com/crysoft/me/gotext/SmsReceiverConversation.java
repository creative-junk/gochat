package com.crysoft.me.gotext;

//import java.util.ArrayList;
//import java.util.Calendar;

import java.util.Calendar;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.telephony.SmsMessage;
import android.widget.LinearLayout;
import android.widget.Toast;

@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
public class SmsReceiverConversation extends BroadcastReceiver {

	IntentFilter intentFilter;
	LinearLayout Containter;

	class Sms {
		public SMSData sms = null;
		public SMSData smsClone = null;
	}

	static String body = "";
	static String number = "";
	public static NotificationManager myNotificationManager;
	public static int NOTIFICATION_ID = 1;
	static SMSData sms = new SMSData();

	@Override
	public void onReceive(final Context contx, Intent intent) {

		Vibrator vib = (Vibrator) contx
				.getSystemService(Context.VIBRATOR_SERVICE);
		myNotificationManager = (NotificationManager) contx
				.getSystemService(Context.NOTIFICATION_SERVICE);

		myNotificationManager = (NotificationManager) contx
				.getSystemService(Context.NOTIFICATION_SERVICE);
		myNotificationManager.cancel("101", NOTIFICATION_ID);
		NOTIFICATION_ID++;
		body = "";
		number = "";

		String body = "";
		String number = "";

		Bundle bundle = intent.getExtras();
		SmsMessage[] msgs = null;

		if (bundle != null) {
			// ---retrieve the SMS message received---
			Object[] pdus = (Object[]) bundle.get("pdus");
			msgs = new SmsMessage[pdus.length];
			for (int i = 0; i < msgs.length; i++) {
				msgs[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
				body += msgs[i].getMessageBody().toString();
				number += msgs[i].getOriginatingAddress();

			}

			final SMSData sms = new SMSData();

			sms.setThreadId(Utils.getThreadId(contx, number));
			sms.setNumber(number);
			sms.setBody(body);
			sms.setDate(Calendar.getInstance().getTimeInMillis());
			sms.setReadStatus("0");
			sms.setSeen(0);
			sms.setLock(0);
			sms.setType(Constants.SMS_RECEIVE);

			final Sms smsObj = new Sms();
			smsObj.sms = sms;

			if (ConversationActivity.isActivityRunning) {
				if (ConversationActivity.threadId == sms.getThreadId()) {
					sms.setReadStatus("1");
					sms.setSeen(1);
					ConversationActivity.smsDataList.add(smsObj.sms);
					ConversationActivity.conversationAdapter.notifyDataSetChanged();
				}

				smsObj.smsClone = Utils.addToInboxDataList(contx, sms);

			} else {
				smsObj.sms = Utils.addToInboxDataList(contx, sms);
			}

			this.sms = sms;

			Thread task = new Thread(new Runnable() {

				@Override
				public void run() {
					if (smsObj.sms != null) {
						Toast.makeText(contx,
								"sms body: " + smsObj.sms.getBody(),
								Toast.LENGTH_LONG).show();
						Utils.updateSmsInfo(contx, smsObj.sms);
						Toast.makeText(contx,
								"sms body: " + smsObj.sms.getBody(),
								Toast.LENGTH_LONG).show();
					}

					if (smsObj.smsClone != null) {
						Utils.updateSmsInfo(contx, smsObj.smsClone);
					}
				}
			});

			final ScheduledExecutorService workers = Executors
					.newSingleThreadScheduledExecutor();

			workers.schedule(task, 5, TimeUnit.SECONDS);

			createStatusBarNotification(contx, body);
			String strQuickreply=Utils.getPreferences("QuickMessage", contx);
			if(strQuickreply.equalsIgnoreCase("Yes")) {
				String strNumber = sms.getNumber();
				String strTitle = sms.getNumber();
				long date = sms.getDate();
				String strDate = String.valueOf(date);
				long pic = sms.getContactID();
				String img = String.valueOf(pic);
				if (sms.getName() != null && !sms.getName().equals("")) {
					strTitle = sms.getName();
				}
				Intent i = new Intent(contx, NotificationReceiver.class);
				i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				i.putExtra("SMS", body);
				i.putExtra("Sender", strNumber);
				i.putExtra("Name", strTitle);
				i.putExtra("Date", strDate);
				i.putExtra("ContactId", img);
				contx.startActivity(i);
			}

		}

		try {
			String vb = Utils.getPreferences("Vibration", contx);
			if (vb.equals("ON")) {
				vib.vibrate(500);

			} else if (vb.equals("OFF")) {
				vib.cancel();

			}


		} catch (Exception e) {
			e.printStackTrace();

		}

	}
	private static void createStatusBarNotification(Context context, String text) {
		Notification notification;
		String strName = Utils.getContactName(sms.getNumber(), context, sms);
		Intent intent = new Intent(context, InboxActivity.class);
		PendingIntent pIntent = PendingIntent.getActivity(context, 0, intent,
				PendingIntent.FLAG_UPDATE_CURRENT);
		Bitmap bm = BitmapFactory.decodeResource(context.getResources(), R.drawable.logo);
		String strTitle = sms.getNumber();
		if (sms.getName() != null && !sms.getName().equals("")) {
			strTitle = sms.getName();
		}
		notification = new Notification.Builder(context)
				.setContentTitle(strTitle)
				.setContentText(sms.getBody())
				.setSmallIcon(R.drawable.notification_icon)
				.setLargeIcon(bm)
				.setContentIntent(pIntent)
				.setDefaults(Notification.DEFAULT_LIGHTS).setAutoCancel(true)
				.build();
		myNotificationManager.notify(0, notification);

	}

}
