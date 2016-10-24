package com.crysoft.me.gotext;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.FragmentActivity;
import android.text.Html;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;
import com.rockerhieu.emojicon.EmojiconGridFragment;
import com.rockerhieu.emojicon.EmojiconsFragment;
import com.rockerhieu.emojicon.emoji.Emojicon;

import java.util.Calendar;

public class SendNewSMSActivity extends FragmentActivity implements
        EmojiconGridFragment.OnEmojiconClickedListener,
        EmojiconsFragment.OnEmojiconBackspaceClickedListener {
    public Animation animShow, animHide;
    String strPhoneNumber, strMessage;
    EditText etNumberTosendSMS, etTextToSend;
    ImageView ivBrowseContacts, ivSendSMS;
    RelativeLayout rlEmojicon;
    int hider = 0;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    public void onEmojiconClicked(Emojicon emojicon) {

        EmojiconsFragment.input(etTextToSend, emojicon);
    }

    @Override
    public void onEmojiconBackspaceClicked(View v) {
        EmojiconsFragment.backspace(etTextToSend);
        // finish();
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar actionBar = getActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#0154A4")));
        actionBar.setTitle(Html.fromHtml("<font color='#ffffff'>GoText </font>"));
        actionBar.setIcon(R.drawable.icon_top);
        setContentView(R.layout.activity_send_new_sms);


        getActionBar().setDisplayHomeAsUpEnabled(true);

        animShow = AnimationUtils.loadAnimation(this, R.anim.popup_show);
        animHide = AnimationUtils.loadAnimation(this, R.anim.popup_hide);


        etNumberTosendSMS = (EditText) findViewById(R.id.etNumberToSendSMS);
        etTextToSend = (EditText) findViewById(R.id.etWriteSMSToSend);
        ivBrowseContacts = (ImageView) findViewById(R.id.ivBrowseContactsQuick);
        ivSendSMS = (ImageView) findViewById(R.id.ivSendNewSMS);
        rlEmojicon = (RelativeLayout) findViewById(R.id.rl_emijicon1);

        Intent i = getIntent();
        String value = i.getStringExtra("TextBox");
        etTextToSend.setText(value);
        etTextToSend.setTextColor(Color.BLACK);

        ivSendSMS.setOnClickListener(new OnClickListener() {

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
        etTextToSend.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (MotionEvent.ACTION_UP == event.getAction()) {
                    rlEmojicon.setVisibility(View.GONE);
                    etTextToSend.setFocusableInTouchMode(true);
                }
                return false; // return is important...
            }
        });
        etNumberTosendSMS.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (MotionEvent.ACTION_UP == event.getAction()) {
                    rlEmojicon.setVisibility(View.GONE);
                    etNumberTosendSMS.setFocusableInTouchMode(true);
                }
                return false; // return is important...
            }
        });

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        this.finish();
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
                Toast.makeText(getApplicationContext(), " Message Sent",
                        Toast.LENGTH_LONG).show();

                etTextToSend.setText("");
                // finish();

                SMSData sms = new SMSData();

                sms.setBody(strMessage);
                sms.setNumber(strPhoneNumber);
                sms.setType(Constants.SMS_SEND);
                sms.setDate(Calendar.getInstance().getTimeInMillis());
                sms.setLock(0);
                sms.setReadStatus(sms.getReadStatus());
                sms.setSeen(sms.getSeen());

                Utils.updateSmsInfo(SendNewSMSActivity.this, sms);

                Utils.addToInboxDataList(SendNewSMSActivity.this, sms);

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


    public void Clicker(View v) {
        switch (v.getId()) {

            case com.crysoft.me.gotext.R.id.smile1:
                if (hider == 0) {
                    rlEmojicon.setVisibility(View.VISIBLE);
                    rlEmojicon.startAnimation(animShow);
                    InputMethodManager imm = (InputMethodManager) getSystemService(
                            Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(etTextToSend.getWindowToken(), 0);
                    hider = 1;

                } else {
                    rlEmojicon.setVisibility(View.GONE);
                    rlEmojicon.startAnimation(animHide);
                    etTextToSend.setFocusable(true);
                    hider = 0;
                }
        }
    }


    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("SendNewSMS Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        AppIndex.AppIndexApi.start(client, getIndexApiAction());
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(client, getIndexApiAction());
        client.disconnect();
    }
}
