package com.crysoft.me.gotext;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.ActionBar;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.rockerhieu.emojicon.EmojiconGridFragment;
import com.rockerhieu.emojicon.EmojiconsFragment;
import com.rockerhieu.emojicon.emoji.Emojicon;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import Constant.MySQLiteHelper;
import popup.ActionItem;
import popup.QuickAction;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class ConversationActivity extends FragmentActivity implements
        OnClickListener, EmojiconGridFragment.OnEmojiconClickedListener,
        EmojiconsFragment.OnEmojiconBackspaceClickedListener {
    private static final int ID_COPY = 1;
    private static final int ID_FORWARD = 2;
    private static final int ID_DELETE = 3;
    private static final int ID_CALL = 4;
    public static ArrayList<SMSData> smsDataList;
    public static ConversationAdapter conversationAdapter;
    public static long threadId = 0;
    public static float x, y;
    static boolean isActivityRunning = false;
    static String strCopyText;
    static int pos;
    static TextView tvReceiver, tvSender;
    public Animation anAnimShow, anAnimHide;
    String strPhoneNumber, strMessage;
    ImageView ivSendSMS, ivSmile;
    EditText etMessageToSend;
    SMSData smsFriendData;
    Animation anFadeOut;
    RelativeLayout rlEmojicon, rlParent;
    int hideEmoji = 0;
    QuickAction mQuickAction;
    MySQLiteHelper Db;
    Context ctxContext;
    String strUserNo;
    //We Create an OnTouch Listener to be used in the Container
    OnTouchListener ontouch = new OnTouchListener() {

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            // TODO Auto-generated method stub
            x = event.getX();
            y = event.getY();

            return false;
        }
    };
    LayoutInflater inflater;
    // Animation
    ArrayList<TextView> tvList = new ArrayList<TextView>();
    private int userNr;
    // Parent view for all rows and the add button.
    private ListView lvSenderContainer;
    private int mSelectedRow = 0;

    /**
     * It called, when click on icon of Emoticons.
     *
     * @param emojicon
     */
    @Override
    public void onEmojiconClicked(Emojicon emojicon) {

        EmojiconsFragment.input(etMessageToSend, emojicon);
    }

    /**
     * It called, when click on backspace button of Emoticons.
     *
     * @param view
     */
    @Override
    public void onEmojiconBackspaceClicked(View view) {
        EmojiconsFragment.backspace(etMessageToSend);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.popup);
        ctxContext = this;
        Db = new MySQLiteHelper(ctxContext);


        anAnimShow = AnimationUtils.loadAnimation(this, R.anim.popup_show);
        anAnimHide = AnimationUtils.loadAnimation(this, R.anim.popup_hide);


        android.app.ActionBar actionBar = getActionBar();
        // add the custom view to the action bar
        actionBar.setCustomView(R.layout.action_bar);
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#0154A4")));
        actionBar.setIcon(R.drawable.icon_top);
        TextView tvName = (TextView) actionBar.getCustomView().findViewById(
                R.id.tvContactName);
        TextView tvNumber = (TextView) actionBar.getCustomView().findViewById(
                R.id.tvContactNumber);
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM
                | ActionBar.DISPLAY_SHOW_HOME);

        actionBar.setTitle(Html.fromHtml("<font color='#ffffff'>WeText </font>"));

        anFadeOut = AnimationUtils.loadAnimation(this, R.anim.fade_out);
        smsFriendData = (SMSData) getIntent().getSerializableExtra("obj");

        etMessageToSend = (EditText) findViewById(R.id.etmessage);
        lvSenderContainer = (ListView) findViewById(R.id.lv_sender);
        rlEmojicon = (RelativeLayout) findViewById(R.id.rl_emojicon);
        rlParent = (RelativeLayout) findViewById(R.id.conversation_layout);

        ivSmile = (ImageView) findViewById(R.id.smile1);
        ivSmile.setOnClickListener(this);

        String strUserName = smsFriendData.getName();
        tvName.setText(strUserName);
        tvNumber.setText(smsFriendData.getNumber());
        String showImages = Utils.getPreferences("ImageViewer", this);


        boolean userExists = Db.search(strUserName);

        if (userExists) {
            strUserNo = smsFriendData.getNumber();
            userNr = 1;
            rlParent.setBackgroundResource(R.drawable.conversation_special_background);
        }

        if (showImages.equalsIgnoreCase("ON")) {
            try {
                //Get the users Contact image using the id from their smsFriendData
                InputStream inputStream = ContactsContract.Contacts.openContactPhotoInputStream(this.getContentResolver(),
                        ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, new Long(smsFriendData.getContactID())));

                if (inputStream != null) {
                    Bitmap photo = BitmapFactory.decodeStream(inputStream);
                    Drawable d = new BitmapDrawable(getResources(), photo);
                    actionBar.setIcon(d);
                } else {
                    //No image use the default image
                    actionBar.setIcon(R.drawable.namesender1);

                }
            } catch (Exception e) {

            }
        }

        lvSenderContainer.setOnTouchListener(ontouch);
        //Process the Options provided in the Popup
        ActionItem copyItem = new ActionItem(ID_COPY, "Copy", getResources().getDrawable(R.drawable.ic_up));
        ActionItem forwardItem = new ActionItem(ID_FORWARD, "Forward", getResources().getDrawable(R.drawable.ic_accept));
        ActionItem deleteItem = new ActionItem(ID_DELETE, "Delete", getResources().getDrawable(R.drawable.ic_up_2));
        ActionItem calItem = new ActionItem(ID_CALL, "Call", getResources().getDrawable(R.drawable.ic_add));

        mQuickAction = new QuickAction(this);

        mQuickAction.addActionItem(copyItem);
        mQuickAction.addActionItem(forwardItem);
        mQuickAction.addActionItem(deleteItem);
        mQuickAction.addActionItem(calItem);

        //setup the action item click listener
        mQuickAction.setOnActionItemClickListener(new QuickAction.OnActionItemClickListener() {
            @Override
            public void onItemClick(QuickAction quickAction, int pos1, int actionId) {
                ActionItem actionItem = quickAction.getActionItem(pos1);

                if (actionId == ID_COPY) { //Add item selected
                    ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                    ClipData clip = ClipData.newPlainText("Copied", strCopyText);
                    clipboard.setPrimaryClip(clip);
                    Toast.makeText(getApplicationContext(), "Text Copied",
                            Toast.LENGTH_LONG).show();
                } else if (actionId == ID_FORWARD) {
                    Intent intent = new Intent(ConversationActivity.this,
                            SendNewSMSActivity.class);
                    intent.putExtra("TextBox", strCopyText);
                    startActivity(intent);
                    Toast.makeText(getApplicationContext(), "Forward Text",
                            Toast.LENGTH_LONG).show();

                } else if (actionId == ID_DELETE) {
                    int a = pos;
                    SMSData sms = smsDataList.get(a);
                    boolean isDeleted = Utils.deleteSms(
                            ConversationActivity.this, sms);
                    if (isDeleted) {
                        smsDataList.remove(pos);
                        conversationAdapter.notifyDataSetChanged();
                    }
                    Toast.makeText(getApplicationContext(), "Delete Text",
                            Toast.LENGTH_LONG).show();

                } else if (actionId == ID_CALL) {
                    String Phone;
                    Phone = smsFriendData.getNumber();
                    Intent callIntent = new Intent(Intent.ACTION_CALL);
                    callIntent.setData(Uri.parse("tel:" + Phone));
                    callIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(callIntent);
                    Toast.makeText(getApplicationContext(), "Call",
                            Toast.LENGTH_LONG).show();
                }
            }
        });

        //setup on dismiss listener, set the icon back to normal
        mQuickAction.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
            }
        });

        //Setup the Message EditText to Listen for Emoji selection
        etMessageToSend.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (MotionEvent.ACTION_UP == event.getAction()) {
                    rlEmojicon.setVisibility(View.GONE);
                    etMessageToSend.setFocusableInTouchMode(true);
                }
                return false; // return is important...
            }
        });

        //Listen for the Long Click as it brings up the Popup Options
        lvSenderContainer.setOnItemLongClickListener(longClick());

        //Get all the messages for this particular user conversation
        smsDataList = Utils.fetchMessages(
                ConversationActivity.this, smsFriendData.getNumber(),
                Constants.SMS_SEND_AND_RECEIVE);

        //For each of the fetched SMS,mark them as read
        (new Thread(new Runnable() {

            @Override
            public void run() {
                Utils.markSmsAsRead(ConversationActivity.this,
                        smsFriendData.getNumber());

            }
        })).start();


        //Setup the ListView for the Conversation
        conversationAdapter = new ConversationAdapter(userNr);
        lvSenderContainer.setAdapter(conversationAdapter);
        lvSenderContainer.requestLayout();

        lvSenderContainer.setSelection(smsDataList.size() - 1);

        ivSendSMS = (ImageView) findViewById(R.id.ivSendNewSMS);

        ivSendSMS.setOnClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(android.view.Menu menu) {
        super.onCreateOptionsMenu(menu);
        //Set up the Menu in this Layout
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.conversation, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                //Process the Call Option in the Menu
                String Phone = smsFriendData.getNumber();
                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel:" + Phone));
                callIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(callIntent);
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.ivSendNewSMS:
                //Send Button
                String message = etMessageToSend.getText().toString();
                if (message.equals("")) {
                    Toast.makeText(getApplicationContext(),
                            " Enter Message To Send", Toast.LENGTH_LONG).show();
                } else {
                    SendSms();
                }

                break;
            case R.id.smile1:
                if (hideEmoji == 0) {
                    //Show the Emojis
                    rlEmojicon.setVisibility(View.VISIBLE);
                    rlEmojicon.startAnimation(anAnimShow);
                    //Hide the Keyboard
                    InputMethodManager imm = (InputMethodManager) getSystemService(
                            Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(etMessageToSend.getWindowToken(), 0);
                    hideEmoji = 1;

                } else {
                    //Hide the emojis
                    rlEmojicon.setVisibility(View.GONE);
                    rlEmojicon.startAnimation(anAnimHide);
                    //return the Focus back to the Message EditText
                    etMessageToSend.setFocusable(true);
                    hideEmoji = 0;
                }
                break;
        }
    }

    private void SendSms() {

        strPhoneNumber = smsFriendData.getNumber();
        strMessage = etMessageToSend.getText().toString();

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
                //Insert the SMS
                getContentResolver().insert(Constants.URI_SMS_SENT, values);
                Toast.makeText(getApplicationContext(), " Message Sent",
                        Toast.LENGTH_LONG).show();
                etMessageToSend.setText("");
                //Create a data Model for it
                SMSData sms = new SMSData();

                sms.setBody(strMessage);
                sms.setNumber(strPhoneNumber);
                sms.setType(Constants.SMS_SEND);
                sms.setDate(Calendar.getInstance().getTimeInMillis());
                sms.setLock(0);
                sms.setReadStatus(sms.getReadStatus());
                sms.setSeen(sms.getSeen());

                //Add the SMS model to the SMS data ArrayList & Notify the Layout of the Change
                ConversationActivity.this.smsDataList.add(sms);
                conversationAdapter.notifyDataSetChanged();

                Utils.updateSmsInfo(ConversationActivity.this, sms);

                Utils.addToInboxDataList(this, sms);

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        } else {
            getContentResolver().insert(Constants.URI_SMS_FAILED, values);
            Toast.makeText(getApplicationContext(), "Message not send",
                    Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStart() {
        super.onStart();
        isActivityRunning = true;
        //Set the user number as the Thread ID
        ConversationActivity.threadId = Utils.getThreadId(this,
                smsFriendData.getNumber());
    }

    @Override
    protected void onStop() {
        super.onStop();
        isActivityRunning = false;
    }

    private OnItemLongClickListener longClick() {

        OnItemLongClickListener longClick = new OnItemLongClickListener() {

            @SuppressLint("NewApi")
            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View view,
                                           final int position, long arg3) {
                pos = position;

                tvReceiver = (TextView) view.findViewById(R.id.tvreceiver);

                tvSender = (TextView) view.findViewById(R.id.tvsender);
                //Which Message are we selecting, from the Receiver or from the Sender
                if (tvSender != null) {
                    strCopyText = tvReceiver.getText().toString();
                } else {
                    strCopyText = tvSender.getText().toString();
                }
                mSelectedRow = position; //set the selected row
                mQuickAction.show(view);

                return true;
            }

        };
        return longClick;

    }

    @Override
    protected void onDestroy() {
        if (tvList != null) {
            tvList.clear();
        }
        if (smsDataList != null) {
            smsDataList.clear();
        }
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        // TODO Auto-generated method stub
        super.onBackPressed();
        this.overridePendingTransition(R.anim.left_move, R.anim.left_move1);
        this.finish();

    }

    public class ConversationAdapter extends BaseAdapter {
        LayoutInflater inflater;
        TextView texttime = null;
        int usernumber;

        public ConversationAdapter(int userno) {
            inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            usernumber = userno;
        }

        @Override
        public int getCount() {
            return smsDataList.size();
        }

        @Override
        public Object getItem(int arg0) {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public long getItemId(int arg0) {
            // TODO Auto-generated method stub
            return 0;
        }

        @Override
        public View getView(int position, View view, ViewGroup parent) {
            SMSData sms = smsDataList.get(position);

            final TextView textmessage;
            String MSGDate;
            SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");

            switch (sms.getType()) {
                case Constants.SMS_RECEIVE:
                    view = inflater.inflate(R.layout.receiver_activity, null);

                    texttime = (TextView) view.findViewById(R.id.textView1);
                    textmessage = (TextView) view.findViewById(R.id.tvreceiver);


                    MSGDate = df.format(sms.getDate());
                    if (MSGDate.equalsIgnoreCase(Utils.GetCurrentDate())) {
                        SimpleDateFormat df1 = new SimpleDateFormat("HH:mm");
                        String MSGDate1 = df1.format(sms.getDate());
                        texttime.setText(MSGDate1);
                    } else {
                        texttime.setText(MSGDate);
                    }

                    textmessage.setText(sms.getBody());

                    break;
                case Constants.SMS_SEND:
                    view = inflater.inflate(R.layout.sender_activity, null);


                    texttime = (TextView) view.findViewById(R.id.textView1);
                    textmessage = (TextView) view.findViewById(R.id.tvsender);

                    MSGDate = df.format(sms.getDate());

                    if (MSGDate.equalsIgnoreCase(Utils.GetCurrentDate())) {
                        SimpleDateFormat df1 = new SimpleDateFormat("HH:mm");
                        String MSGDate1 = df1.format(sms.getDate());
                        texttime.setText(MSGDate1);
                    } else {
                        texttime.setText(MSGDate);
                    }
                    textmessage.setText(sms.getBody());
                default:
                    break;
            }


            return view;
        }

    }
}
