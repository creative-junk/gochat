package com.crysoft.me.gotext;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.provider.ContactsContract.PhoneLookup;
import android.telephony.SmsManager;
import android.view.ViewConfiguration;
import android.widget.Toast;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;

public class Utils {

    public static boolean check = false;

    private static String[] smsProjection = new String[]{"_id", "thread_id",
            "address", "date", "read", "seen", "type", "body", "locked"};

    public static void getOverflowMenu(Context context) {

        try {
            ViewConfiguration config = ViewConfiguration.get(context);
            Field menuKeyField = ViewConfiguration.class
                    .getDeclaredField("sHasPermanentMenuKey");
            if (menuKeyField != null) {
                menuKeyField.setAccessible(true);
                menuKeyField.setBoolean(config, false);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Fetch All Messages of a Specific Number

    public static ArrayList<SMSData> fetchMessages(Context ctx, String number,
                                                   int type) {
        ArrayList<SMSData> smsList = new ArrayList<SMSData>();

        String where = "address=? AND type=?";
        String[] whereArgs = new String[]{"" + number, "" + type};

        String number2 = number;
        switch (number.charAt(0)) {
            case '0':
                number2 = "+254" + number.subSequence(1, number.length());
                break;
            case '+':
                number2 = "0" + number.subSequence(3, number.length());
                break;
            default:
                break;
        }

        switch (type) {
            case Constants.SMS_RECEIVE:
            case Constants.SMS_SEND:
                break;
            case Constants.SMS_SEND_AND_RECEIVE:
                where = "(address=? OR address=?) AND (type=? OR type=?)";
                whereArgs = new String[]{"" + number, "" + number2, "1", "2"};
                break;
            default:
                return smsList;
        }

        Cursor cursor = ctx.getContentResolver().query(Constants.URI_SMS,
                smsProjection, where, whereArgs, "date ASC"); //

        if (cursor.moveToFirst()) {
            for (int i = 0; i < cursor.getCount(); i++) {

                SMSData sd = new SMSData();
                try {
                    sd.setId(cursor.getLong(cursor.getColumnIndexOrThrow("_id")));
                    sd.setThreadId(cursor.getLong(cursor
                            .getColumnIndexOrThrow("thread_id")));
                    sd.setNumber(cursor.getString(
                            cursor.getColumnIndexOrThrow("address")).toString());
                    sd.setDate(Long.parseLong(cursor.getString(
                            cursor.getColumnIndexOrThrow("date")).toString()));
                    sd.setReadStatus(cursor.getString(cursor
                            .getColumnIndexOrThrow("read")));
                    sd.setSeen(cursor.getInt(cursor
                            .getColumnIndexOrThrow("seen")));
                    sd.setType(cursor.getInt(cursor
                            .getColumnIndexOrThrow("type")));
                    sd.setBody(cursor.getString(
                            cursor.getColumnIndexOrThrow("body")).toString());
                    sd.setLock(cursor.getInt(cursor
                            .getColumnIndexOrThrow("locked")));

                    smsList.add(sd);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                cursor.moveToNext();
            }

        }
        cursor.close();
        return smsList;
    }

    public static void updateSmsInfo(Context ctx, SMSData sms) {

        String where = "address=? AND body=?";
        String[] whereArgs = new String[]{"" + sms.getNumber(), sms.getBody()};

        String number2 = sms.getNumber();

        number2 = number2.substring(1);
        if (number2.equalsIgnoreCase("0")) {
            String str = "+254";
            number2 = str + number2;
        }

        Cursor cursor = ctx.getContentResolver().query(Constants.URI_SMS,
                smsProjection, where, whereArgs, null); //

        if (cursor.moveToFirst()) {


            try {
                sms.setId(cursor.getLong(cursor.getColumnIndexOrThrow("_id")));
                sms.setThreadId(cursor.getLong(cursor
                        .getColumnIndexOrThrow("thread_id")));
                sms.setNumber(cursor.getString(
                        cursor.getColumnIndexOrThrow("address")).toString());
                sms.setDate(Long.parseLong(cursor.getString(
                        cursor.getColumnIndexOrThrow("date")).toString()));
                sms.setReadStatus(cursor.getString(cursor
                        .getColumnIndexOrThrow("read")));
                sms.setSeen(cursor.getInt(cursor.getColumnIndexOrThrow("seen")));
                sms.setType(cursor.getInt(cursor.getColumnIndexOrThrow("type")));
                sms.setBody(cursor.getString(
                        cursor.getColumnIndexOrThrow("body")).toString());
                sms.setLock(cursor.getInt(cursor
                        .getColumnIndexOrThrow("locked")));
            } catch (Exception e) {
                e.printStackTrace();
            }
            cursor.moveToNext();
            // }

        }
        cursor.close();
    }

    public static ArrayList<SMSData> getLatestMessageOfAllContacts(Context ctx) {

        ArrayList<SMSData> smsList = new ArrayList<SMSData>();
        String where = "type=? OR type=?";// ) GROUP BY ( thread_id
        String[] whereArgs = new String[]{"" + Constants.SMS_SEND,
                "" + Constants.SMS_RECEIVE + ""};

        try {
            Cursor cursor = ctx.getContentResolver().query(Constants.URI_SMS,
                    smsProjection, where, whereArgs, "date DESC");
            long threadId;
            boolean doNotAddMessage = false;
            if (cursor.moveToFirst()) {

                for (int i = 0; i < cursor.getCount(); i++) {
                    SMSData sd = new SMSData();

                    threadId = cursor.getLong(cursor
                            .getColumnIndexOrThrow("thread_id"));

                    for (int j = 0; j < smsList.size(); j++) {
                        if (smsList.get(j).getThreadId() == threadId) {
                            doNotAddMessage = true;
                        }
                    }

                    if (doNotAddMessage) {
                        cursor.moveToNext();
                        doNotAddMessage = false;
                        continue;
                    }

                    sd.setId(cursor.getLong(cursor.getColumnIndexOrThrow("_id")));
                    sd.setThreadId(threadId);
                    sd.setNumber(cursor.getString(
                            cursor.getColumnIndexOrThrow("address")).toString());
                    sd.setDate(Long.parseLong(cursor.getString(
                            cursor.getColumnIndexOrThrow("date")).toString()));
                    sd.setReadStatus(cursor.getString(cursor
                            .getColumnIndexOrThrow("read")));
                    sd.setSeen(cursor.getInt(cursor
                            .getColumnIndexOrThrow("seen")));
                    sd.setType(cursor.getInt(cursor
                            .getColumnIndexOrThrow("type")));
                    sd.setBody(cursor.getString(
                            cursor.getColumnIndexOrThrow("body")).toString());
                    sd.setLock(cursor.getInt(cursor
                            .getColumnIndexOrThrow("locked")));
                    getContactName(sd.getNumber(), ctx, sd);

                    smsList.add(sd);

                    cursor.moveToNext();
                }
            }

            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return smsList;
    }

    // / Get All Messages of a GivenFolder

    public static ArrayList<SMSData> GetAllMessages(String strFolder, Context ctx)

    {
        ArrayList<SMSData> smsList = new ArrayList<SMSData>();
        try {
            Uri uriSms = Uri.parse("content://sms/" + strFolder);
            Cursor cursor = ctx.getContentResolver().query(uriSms, null, null,
                    null, null);
            if (cursor.moveToFirst()) {
                for (int i = 0; i < cursor.getCount(); i++) {

                    SMSData sd = new SMSData();

                    sd.setId(cursor.getLong(cursor.getColumnIndexOrThrow("_id")));
                    sd.setThreadId(cursor.getLong(cursor
                            .getColumnIndexOrThrow("thread_id")));
                    sd.setNumber(cursor.getString(
                            cursor.getColumnIndexOrThrow("address")).toString());
                    sd.setDate(Long.parseLong(cursor.getString(
                            cursor.getColumnIndexOrThrow("date")).toString()));
                    sd.setReadStatus(cursor.getString(cursor
                            .getColumnIndexOrThrow("read")));
                    sd.setSeen(cursor.getInt(cursor
                            .getColumnIndexOrThrow("seen")));
                    sd.setType(cursor.getInt(cursor
                            .getColumnIndexOrThrow("type")));
                    sd.setBody(cursor.getString(
                            cursor.getColumnIndexOrThrow("body")).toString());
                    sd.setLock(cursor.getInt(cursor
                            .getColumnIndexOrThrow("locked")));

                    int count = 0;
                    if (smsList.size() > 0) {
                        for (int j = 0; j < smsList.size(); j++) {

                            if ((sd.getNumber().equalsIgnoreCase(smsList.get(j)
                                    .getNumber()))) {
                                count = 1;
                                break;
                            }

                        }
                    }

                    if (count < 1 || smsList.size() == 0) {
                        smsList.add(sd);
                    }

                    cursor.moveToNext();

                }

            }
            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return smsList;
    }


    public static String getContactName(final String phoneNumber, Context ctx,
                                        SMSData sms) {

        Uri uri = Uri.withAppendedPath(PhoneLookup.CONTENT_FILTER_URI,
                Uri.encode(phoneNumber));
        Cursor cursor = ctx.getContentResolver().query(uri,
                new String[]{PhoneLookup._ID, PhoneLookup.DISPLAY_NAME},
                ContactsContract.Contacts.HAS_PHONE_NUMBER + "=?",
                new String[]{phoneNumber}, null);

        String contactName = "";

        if (cursor.moveToFirst()) {
            sms.setContactID(cursor.getInt(0));
            sms.setName(cursor.getString(1));

        } else {
            sms.setName("");
            sms.setContactID(0);
        }

        cursor.close();
        cursor = null;

        return contactName;
    }


    public static String GetCurrentDate() {
        Calendar c = Calendar.getInstance();

        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        String CurrentDate = df.format(c.getTime());
        return CurrentDate;

    }

    public static ArrayList<SMSData> SortDate(ArrayList<SMSData> data) {
        ArrayList<SMSData> newlist = (ArrayList<SMSData>) data.clone();
        Collections.sort(newlist, new DateComparator());

        return newlist;
    }

    public static ArrayList<SMSData> SearchMessage(String query,
                                                   ArrayList<SMSData> data) {
        ArrayList<SMSData> seachedItems = new ArrayList<SMSData>();
        for (int i = 0; i < data.size(); i++) {

            SMSData item = data.get(i);

            if (item.getName() == null || item.getName().equalsIgnoreCase("")) {

                if (query.length() <= item.getNumber().length()) {
                    if (query.equalsIgnoreCase(item.getNumber().substring(0,
                            query.length()))) {
                        seachedItems.add(item);
                    }
                }

            } else {

                if (query.length() <= item.getName().length()) {
                    if (query.equalsIgnoreCase(item.getName().substring(0,
                            query.length()))) {
                        seachedItems.add(item);
                    }
                }

            }
        }

        return seachedItems;
    }

    public static String getPreferences(String key, Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String userName = sharedPreferences.getString(key, "UserName");
        return userName;
    }

    public static boolean savePreferences(String key, String value,
                                          Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.commit();
        return true;
    }

    public static int getPreferencesInt(String key, Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        int userName = sharedPreferences.getInt(key, 0);
        return userName;
    }

    public static boolean savePreferencesInt(String key, int value,
                                             Context context) {
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(context);
        Editor editor = sharedPreferences.edit();
        editor.putInt(key, value);
        return editor.commit();
    }

    public static long OneWeekSMSData() {

        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss");
        Calendar c = Calendar.getInstance();

        Date myDate = c.getTime();

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(myDate);
        calendar.add(Calendar.DAY_OF_YEAR, -7);
        Date newDate = (Date) calendar.getTime();
        long Date = newDate.getTime();
        return Date;

    }

    // For Two Week smsdata delete

    public static long TwoWeekSMSData() {

        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss");
        Calendar c = Calendar.getInstance();

        Date myDate = c.getTime();

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(myDate);
        calendar.add(Calendar.DAY_OF_YEAR, -14);
        Date newDate = (Date) calendar.getTime();
        long Date = newDate.getTime();
        return Date;

    }

    // For Three Week smsdata delete

    public static long ThreeWeekSMSData() {

        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss");
        Calendar c = Calendar.getInstance();

        Date myDate = c.getTime();

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(myDate);
        calendar.add(Calendar.DAY_OF_YEAR, -21);
        Date newDate = (Date) calendar.getTime();
        long Date = newDate.getTime();
        return Date;

    }

    // For One Four smsdata delete

    public static long OneMonthSMSData() {

        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss");
        Calendar c = Calendar.getInstance();

        Date myDate = c.getTime();

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(myDate);
        calendar.add(Calendar.DAY_OF_YEAR, -30);
        Date newDate = (Date) calendar.getTime();
        long Date = newDate.getTime();
        return Date;

    }

    public static ArrayList<SMSData> ShakeToDelete(Context context) {


        ArrayList<SMSData> listInbox, listSent, listConversation, smsWithinSelectedDate = new ArrayList<SMSData>();

        String check = Utils.getPreferences("Auto-Destroy", context);
        String listValue = Utils.getPreferences("Schedule", context);

        if (check.equalsIgnoreCase("yes")) {
            long datePeriodToDelete;
            if (listValue.equals("One Week Selected")) {
                datePeriodToDelete = OneWeekSMSData();

            } else if (listValue.equals("Two Week Selected")) {
                datePeriodToDelete = TwoWeekSMSData();
            } else if (listValue.equals("Three Week Selected")) {
                datePeriodToDelete = ThreeWeekSMSData();
            } else {
                datePeriodToDelete = OneMonthSMSData();
            }

            listInbox = Utils.GetAllMessages("inbox", context);
            listSent = Utils.GetAllMessages("sent", context);

            listConversation = new ArrayList<>();
            for (int i = 0; i < listInbox.size(); i++) {
                SMSData ss = listInbox.get(i);

                listConversation.add(ss);

            }

            for (int i = 0; i < listSent.size(); i++) {
                SMSData ss = listSent.get(i);
                listConversation.add(ss);
            }

            for (int i = 0; i < listConversation.size(); i++) {

                SMSData smsData = listConversation.get(i);

                long smsDate = smsData.getDate();

                if (smsDate > datePeriodToDelete) {
                    smsWithinSelectedDate.add(smsData);
                }

            }
            return smsWithinSelectedDate;
        }
        return null;
    }

    public static void ShakeToDeleteSMS(Context context,
                                        ArrayList<SMSData> itemsToDelete, String folder) {


        try {

            Uri uriSms = Uri.parse("content://sms/" + folder);
            Cursor c = context.getContentResolver().query(
                    uriSms,
                    new String[]{"_id", "thread_id", "address", "person",
                            "date", "body"}, "read = 1", null, null);

            if (c != null && c.moveToFirst()) {
                do {
                    long id = c.getLong(0);
                    long threadId = c.getLong(1);
                    String address = c.getString(2);
                    String body = c.getString(5);
                    String date = c.getString(4);
                    for (int i = 0; i < itemsToDelete.size(); i++) {

                        if (date.equals(String.valueOf(itemsToDelete.get(i).getDate()))) {

                            int d = context.getContentResolver().delete(
                                    Uri.parse("content://sms/" + id), null,
                                    null);
                            break;
                        }
                    }
                } while (c.moveToNext());
                c.close();
            }
        } catch (Exception e) {
            Toast.makeText(context, "Not Deleted ", Toast.LENGTH_LONG).show();
        }
    }

    public static boolean deleteSmsofContact(Context context, String number,
                                             boolean deleteLocked) {
        int result;

        if (deleteLocked) {
            result = context.getContentResolver().delete(Constants.URI_SMS,
                    "address=?", new String[]{number});
        } else {
            result = context.getContentResolver().delete(Constants.URI_SMS,
                    "address=? AND locked=?", new String[]{number, "1"});
        }

        if (result > 0) {
            return true;
        }

        return false;
    }

    // delete one message
    public static boolean deleteSms(Context context, SMSData sms) {
        String[] whereArgs = new String[]{"" + sms.getId(),
                "" + sms.getLocked()};
        int v = context.getContentResolver().delete(Constants.URI_SMS,
                "_id=? AND locked=?", whereArgs);

        if (v > 0) {
            return true;
        }

        return false;
    }

    public static void markSmsAsRead(Context context, String number) {

        String selection = "address = ? AND read = ? OR seen = ?";// AND body =
        // ?
        String[] selectionArgs = {number, "0", "0"};// body,

        ContentValues values = new ContentValues();
        values.put("read", true);
        values.put("seen", true);

        context.getContentResolver().update(Constants.URI_SMS, values,
                selection, selectionArgs);
    }

    public static boolean sendSMS(String number, String message) {
        SmsManager smManager = SmsManager.getDefault();

        if (!(number.charAt(0) == '+')) {
            number = number.substring(1);
            String str = "+254";
            number = str + number;
        }

        ArrayList<String> parts = smManager.divideMessage(message);

        try {
            smManager.sendMultipartTextMessage(number, null, parts, null, null);
        } catch (Exception e) {
            return false;
        }

        return true;
    }

    public static long getThreadId(Context context, String number) {
        try {
            Cursor cursor = context.getContentResolver().query(
                    Constants.URI_SMS, new String[]{"thread_id"},
                    "address=?", new String[]{number}, null);
            if (cursor.moveToFirst()) {
                return cursor.getLong(cursor.getColumnIndex("thread_id"));
            }

            return 0;
        } catch (Exception e) {
            return 0;
        }
    }

    public static SMSData addToInboxDataList(Context ctx, SMSData sms) {
        try {
            boolean isNotAvailable = true;

            for (int i = 0; i < InboxActivity.smsDataList.size(); i++) {
                if (InboxActivity.smsDataList.get(i).getThreadId() == sms
                        .getThreadId()) {
                    isNotAvailable = false;

                    SMSData smsObj = InboxActivity.smsDataList.remove(i);
                    smsObj.setBody(sms.getBody());
                    smsObj.setDate(sms.getDate());
                    smsObj.setLock(0);
                    smsObj.setReadStatus(sms.getReadStatus());
                    smsObj.setSeen(sms.getSeen());
                    smsObj.setType(sms.getType());
                    InboxActivity.smsDataList.add(0, smsObj);

                    InboxActivity.inboxAdapter.notifyDataSetChanged();
                    return smsObj;
                }
            }

            if (isNotAvailable) {
                SMSData smsClone = sms.clone();

                Utils.getContactName(sms.getNumber(), ctx, smsClone);

                InboxActivity.smsDataList.add(0, smsClone);
                InboxActivity.inboxAdapter.notifyDataSetChanged();

                return smsClone;
            }

            return null;
        } catch (Exception e) {
            return null;
        }
    }

    public static int getColor(char c) {
        switch (c) {
            case 'a':
            case 'A':
                return Color.rgb(7, 102, 198);
            case 'b':
            case 'B':
                return Color.rgb(7, 115, 223);
            case 'c':
            case 'C':
                return Color.rgb(8, 123, 239);
            case 'd':
            case 'D':
                return Color.rgb(118, 1, 143);
            case 'e':
            case 'E':
                return Color.rgb(61, 11, 72);
            case 'f':
            case 'F':
                return Color.rgb(102, 11, 142);
            case 'g':
            case 'G':
                return Color.rgb(85, 21, 142);
            case 'h':
            case 'H':
                return Color.rgb(65, 27, 142);
            case 'i':
            case 'I':
                return Color.rgb(59, 37, 142);
            case 'j':
            case 'J':
                return Color.rgb(51, 42, 142);
            case 'k':
            case 'K':
                return Color.rgb(37, 53, 142);
            case 'l':
            case 'L':
                return Color.rgb(26, 58, 142);
            case 'm':
            case 'M':
                return Color.rgb(1, 116, 223);
            case 'n':
            case 'N':
                return Color.rgb(1, 84, 162);
            case 'o':
            case 'O':
                return Color.rgb(1, 126, 224);
            case 'p':
            case 'P':
                return Color.rgb(1, 63, 122);
            case 'q':
            case 'Q':
                return Color.rgb(6, 98, 183);
            case 'r':
            case 'R':
                return Color.rgb(0, 42, 81);
            case 's':
            case 'S':
                return Color.rgb(0, 16, 33);
            case 't':
            case 'T':
                return Color.rgb(17, 181, 228);
            case 'u':
            case 'U':
                return Color.rgb(20, 129, 186);
            case 'v':
            case 'V':
                return Color.rgb(12, 170, 220);
            case 'w':
            case 'W':
                return Color.rgb(137, 19, 31);
            case 'x':
            case 'X':
                return Color.rgb(141, 7, 61);
            case 'y':
            case 'Y':
                return Color.rgb(130, 13, 72);
            case 'z':
            case 'Z':
                return Color.rgb(124, 7, 102);

            default:
                return Color.rgb(1, 84, 164);
        }
    }


    public static int TotalMessages(Context context, String number) {
        final Uri SMS_INBOX = Uri.parse("content://sms/inbox");
        String selection = "address = ?AND read = ? OR seen = ?";
        String[] selectionArgs = {number, "0", "0"};
        Cursor c = context.getContentResolver().query(SMS_INBOX, null, null, selectionArgs, selection);
        int unreadMessagesCount = c.getCount();
        c.deactivate();
        return unreadMessagesCount;
    }
}
