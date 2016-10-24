package com.crysoft.me.gotext;

import android.app.Activity;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class NotificationReceiver extends Activity {
    Button btnCancel, btnReply;
    EditText etTextToSend;
    ImageView ivAvatar;
    TextView tvNumberTosendSMS, tvName, tvSMSBody, tvDate;
    String strPhoneNumber, strMessage;
    String strMSGDate;
    SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
    private String strSMSBody, strNumber, strContactName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.custom1);

        Bundle extras = getIntent().getExtras();
        strSMSBody = extras.getString("SMS");
        strNumber = extras.getString("Sender");
        strContactName = extras.getString("Name");
        String strContactId = extras.getString("ContactId");
        String strDate = extras.getString("Date");
        long longdate = Long.valueOf(strDate);

        strMSGDate = df.format(longdate);
        long id = Long.valueOf(strContactId);


        btnCancel = (Button) findViewById(R.id.btnquickdialogcancel);
        btnReply = (Button) findViewById(R.id.btnquickdialogsend);
        tvNumberTosendSMS = (TextView) findViewById(R.id.tvnumberofsender);
        tvSMSBody = (TextView) findViewById(R.id.tvsendermsg);
        tvName = (TextView) findViewById(R.id.tvnameofsender);
        tvDate = (TextView) findViewById(R.id.tvsenderdate);

        etTextToSend = (EditText) findViewById(R.id.etquickMessage);
        ivAvatar = (ImageView) findViewById(R.id.ivsenderquickimg);

        tvNumberTosendSMS.setText(strNumber);
        tvSMSBody.setText(strSMSBody);
        tvName.setText(strContactName);

        if (strMSGDate.equalsIgnoreCase(Utils.GetCurrentDate())) {
            SimpleDateFormat df1 = new SimpleDateFormat("HH:mm");

            String strTodaysDate = df1.format(longdate);
            tvDate.setText(strTodaysDate);
        } else {
            tvDate.setText(strMSGDate);
        }

        try {
            InputStream inputStream = ContactsContract.Contacts.openContactPhotoInputStream(this.getContentResolver(),
                    ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, new Long(id)));

            if (inputStream != null) {
                Bitmap photo = BitmapFactory.decodeStream(inputStream);
                Drawable d = new BitmapDrawable(getResources(), photo);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    ivAvatar.setBackground(d);
                } else {
                    ivAvatar.setBackgroundDrawable(d);
                }

            } else {
                ivAvatar.setBackgroundResource(com.crysoft.me.gotext.R.drawable.namesender1);

            }
        } catch (Exception e) {

        }

        btnCancel.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();
            }
        });
        btnReply.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                SendSms();

            }
        });

    }

    public void SendSms() {

        strPhoneNumber = tvNumberTosendSMS.getText().toString();
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

                        tvNumberTosendSMS.setText(mNumber);
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