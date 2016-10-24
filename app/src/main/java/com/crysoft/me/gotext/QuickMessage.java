package com.crysoft.me.gotext;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.Calendar;

public class QuickMessage extends Activity {
    Button btnCancel, btnSend;
    ImageView ivBrowseContacts;
    EditText etNumberTosendSMS, etTextToSend;
    String strPhoneNumber, strMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.custom);
        btnCancel = (Button) findViewById(R.id.btnquickdialogcancel);
        btnSend = (Button) findViewById(R.id.btnquickdialogsend);
        etNumberTosendSMS = (EditText) findViewById(R.id.etNumberToSendQuickSMS);
        etTextToSend = (EditText) findViewById(R.id.etquickMessage);
        ivBrowseContacts = (ImageView) findViewById(R.id.ivBrowseContactsQuick);
        btnCancel.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                finish();
            }
        });
        btnSend.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                SendSms();

            }
        });
        ivBrowseContacts.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE);
                startActivityForResult(intent, 0);

            }
        });

    }

    public void SendSms() {

        strPhoneNumber = etNumberTosendSMS.getText().toString();
        strMessage = etTextToSend.getText().toString();

        if (strPhoneNumber.length() < 1) {
            Toast.makeText(getApplicationContext(), "Enter Phone Number",
                    Toast.LENGTH_LONG).show();
            return;
        }

        ContentValues values = new ContentValues();

        values.put("address", strPhoneNumber);
        values.put("body", strMessage);
        values.put("date", Calendar.getInstance().getTimeInMillis());

        if (Utils.sendSMS(strPhoneNumber, strMessage)) {
            try {
                getContentResolver().insert(Constants.URI_SMS_SENT, values);

                etTextToSend.setText("");

                SMSData sms = new SMSData();

                sms.setBody(strMessage);
                sms.setNumber(strPhoneNumber);
                sms.setType(Constants.SMS_SEND);
                sms.setDate(Calendar.getInstance().getTimeInMillis());
                sms.setLock(0);
                sms.setReadStatus(sms.getReadStatus());
                sms.setSeen(sms.getSeen());
                Toast.makeText(getApplicationContext(), " Message Sent",
                        Toast.LENGTH_LONG).show();
                finish();

            } catch (Exception ex) {
                Toast.makeText(getApplicationContext(), "Not Saved",
                        Toast.LENGTH_LONG).show();
            }
        } else {
            getContentResolver().insert(Constants.URI_SMS_FAILED, values);
            Toast.makeText(getApplicationContext(), "Message not sent",
                    Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        if (data != null) {
            Uri uri = data.getData();

            if (uri != null) {
                Cursor c = null;
                try {
                    c = getContentResolver()
                            .query(uri,
                                    new String[]{ContactsContract.CommonDataKinds.Phone.NUMBER},
                                    null, null, null);

                    if (c != null && c.moveToFirst()) {
                        String mNumber = c.getString(0);

                        etNumberTosendSMS.setText(mNumber);
                    }
                } finally {
                    if (c != null) {
                        c.close();
                    }
                }
                etTextToSend.setText("");
            }
        }
    }
}