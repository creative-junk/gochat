package com.crysoft.me.gotext;

import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.InputStream;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import Constant.MySQLiteHelper;
import Constant.RoundedImageView;


public class InboxAdapter extends BaseAdapter {

    Context context;
    RoundedImageView ivUserImage;
    TextView tvImage, tvName, tvMessage, tvTime;
    ArrayList<SMSData> smsDataList;
    String strImageViewer;

    MySQLiteHelper Db;

    public InboxAdapter(Context context, ArrayList<SMSData> list) {
        // super();
        this.context = context;
        smsDataList = list;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return smsDataList.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return smsDataList.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return 0;
    }

    @SuppressWarnings("static-access")
    @Override
    public View getView(int position, View v, ViewGroup parent) {

        final SMSData smsData = smsDataList.get(position);
        //Check if the user wants Images to be used
        strImageViewer = Utils.getPreferences("ImageViewer", context);

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        Db = new MySQLiteHelper(context);
        if (strImageViewer.equals("ON")) {
            v = inflater.inflate(R.layout.custom_inboxview, null);
            parent = (RelativeLayout) v.findViewById(R.id.parent_layout);

        } else {
            v = inflater.inflate(R.layout.custom_inboxtextview, null);
            parent = (RelativeLayout) v.findViewById(R.id.parent_layout);
        }
        if (smsData.getName().isEmpty()) {

            RelativeLayout image_layout = (RelativeLayout) v.findViewById(R.id.image_layout);
            image_layout.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View arg0) {
                    // TODO Auto-generated method stub
                    Intent addContactIntent = new Intent(Intent.ACTION_INSERT);
                    addContactIntent.setType(ContactsContract.Contacts.CONTENT_TYPE);
                    addContactIntent.putExtra(ContactsContract.Intents.Insert.PHONE, smsData.getNumber().toString());
                    context.startActivity(addContactIntent);
                }
            });
        }
        tvName = (TextView) v.findViewById(R.id.tvCustomInboxName);
        tvMessage = (TextView) v.findViewById(R.id.tvCustomInboxMessage);
        tvTime = (TextView) v.findViewById(R.id.tvCustomInboxTime);


        Date date = new Date(smsData.getDate());
        String MsgTime = new SimpleDateFormat("hh:mm a").format(date);
        tvName.setText(smsData.getName());
        tvMessage.setText(smsData.getBody());
        {
            if (GetCurrentDate().equals(GetMSGtDate(date)))

                tvTime.setText(MsgTime);
            else
                tvTime.setText(GetMSGtDate(date));
        }
        {
            if (smsData.getName().length() > 0) {
                String name = smsData.getName();

                boolean userExists = Db.search(name);

                if (userExists) {
                    parent.setBackgroundResource(R.drawable.special_people);

                }

                tvName.setText(smsData.getName());
            } else

                tvName.setText(smsData.getNumber());
        }
        if (strImageViewer.equals("ON")) {
            ivUserImage = (RoundedImageView) v.findViewById(R.id.ivCustomInbox);

            try {
                InputStream inputStream = ContactsContract.Contacts.openContactPhotoInputStream(context.getContentResolver(),
                        ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, new Long(smsData.getContactID())));

                if (inputStream != null) {
                    Bitmap photo = BitmapFactory.decodeStream(inputStream);
                    ivUserImage.setImageBitmap(photo);
                }
            } catch (Exception e) {
                ivUserImage.setImageResource(R.drawable.namesender1);
            }
        } else {
            tvImage = (TextView) v.findViewById(R.id.ivinboxview);

            if (smsData.getName().isEmpty()) {

                tvImage.setText("+");
            } else {
                tvImage.setText(smsData.getName());

            }
        }
        if (!smsData.getReadStatus().equalsIgnoreCase("1")) {
            tvName.setTypeface(null, Typeface.BOLD);
            tvTime.setTypeface(null, Typeface.BOLD);
            tvMessage.setTypeface(null, Typeface.BOLD);

            tvMessage.setTextColor(Color.BLACK);
        }
        return v;
    }

    private String GetCurrentDate() {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");
        String CurrentDate = df.format(c.getTime());
        return CurrentDate;
    }

    private String GetMSGtDate(Date date) {
        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");
        String MSGDate = df.format(date);
        return MSGDate;
    }

    public void updateList(ArrayList<SMSData> searchData) {
        this.smsDataList = searchData;
    }



}
