package com.crysoft.me.gotext;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.SearchView.OnQueryTextListener;
import android.widget.Toast;

import java.util.ArrayList;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
@SuppressLint("NewApi")
public class InboxActivity extends Activity implements AccelerometerListener {

    static ArrayList<SMSData> smsDataList;
    static boolean isActivityRunning = false;
    static long back_pressed;
    ListView lvMessageList;
    ArrayList<SMSData> searchData = new ArrayList<SMSData>();
    AlertDialog alertDialog;
    static InboxAdapter inboxAdapter;
    String[] longPressedItems = new String[]{"Delete Conversation"};
    boolean isSearch = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_inbox);

        lvMessageList = (ListView) findViewById(R.id.lvInbox);
        ActionBar bar = getActionBar();
        bar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#0154A4")));
        bar.setTitle(R.string.app_name);
        bar.setTitle(Html.fromHtml("<font color='#ffffff'>Messaging</font>"));
        bar.setIcon(R.drawable.icon_top);

        Utils.getOverflowMenu(this);

        attachListeners();

        smsDataList = Utils.getLatestMessageOfAllContacts(InboxActivity.this);

        if (smsDataList.isEmpty()) {
            Toast.makeText(getApplicationContext(), "NO MESSAGES",
                    Toast.LENGTH_LONG).show();
        } else {
            inboxAdapter = new InboxAdapter(InboxActivity.this, smsDataList);
            lvMessageList.setAdapter(inboxAdapter);
        }
    }


    private void attachListeners() {


        lvMessageList.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1,
                                    int position, long arg3) {
                SMSData smsData = new SMSData();
                //get the Selected Message
                if (isSearch) {
                    smsData = searchData.get(position);
                } else {
                    smsData = smsDataList.get(position);
                }
                //Open The Message/Conversation
                Intent intent = new Intent(InboxActivity.this,
                        ConversationActivity.class);
                intent.putExtra("obj", smsData);

                startActivity(intent);
                InboxActivity.this.overridePendingTransition(R.anim.left_move,
                        R.anim.left_move1);
                //Mark the Message as seen
                smsData.setReadStatus("1");
                smsData.setSeen(1);
                inboxAdapter.notifyDataSetChanged();
            }
        });

        lvMessageList.setOnItemLongClickListener(new OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> parent,
                                           final View view, final int threadPosition, long id) {


//                        CustomDialog();
                AlertDialog.Builder dialog = new AlertDialog.Builder(
                        InboxActivity.this);
                getWindow().setBackgroundDrawableResource(R.color.background);
                dialog.setIcon(R.drawable.ic_launcher);


                dialog.setItems(longPressedItems,

                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog,
                                                int position) {
                                SMSData sms = smsDataList
                                        .get(threadPosition);
                                sms.setReadStatus("1");
                                sms.setSeen(1);

                                switch (position) {
                                    case 0:
                                        boolean smsDeleted = Utils.deleteSmsofContact(InboxActivity.this, sms.getNumber(), true);
                                        if (smsDeleted) {
                                            smsDataList.remove(threadPosition);
                                            inboxAdapter.notifyDataSetChanged();

                                        }

                                        break;
                                }
                            }
                        });

                dialog.create().show();

                return true;
            }
        });
    }

    @Override
    public void onBackPressed() {

        if (back_pressed + 2000 > System.currentTimeMillis()) {
            super.onBackPressed();
        } else {
            Toast.makeText(getApplicationContext(), "Press once again to exit!", Toast.LENGTH_SHORT).show();
            back_pressed = System.currentTimeMillis();
        }

    }

    @Override
    public void onAccelerationChanged(float x, float y, float z) {
    }

    @Override
    public void onShake(float force) {
        // Called when Motion Detected

        final ArrayList<SMSData> deleteList = Utils.ShakeToDelete(this);

        if (deleteList != null) {
            if (alertDialog == null) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                        InboxActivity.this);

                alertDialogBuilder.setTitle("Delete ALL READ MESSAGES");
                alertDialogBuilder
                        .setMessage("Are you sure you want to Delete  ALL READ MESSAGE? ")
                        .setCancelable(false)
                        .setPositiveButton("Yes",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,
                                                        int id) {
                                        // if this button is clicked, close current activity

                                        Utils.ShakeToDeleteSMS(
                                                InboxActivity.this, deleteList,
                                                "inbox");
                                    }
                                })
                        .setNegativeButton("No",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,
                                                        int id) {

                                        dialog.cancel();
                                        alertDialog = null;
                                    }
                                });

                alertDialog = alertDialogBuilder.create();

            } else if (!alertDialog.isShowing()) {
                alertDialog.show();
            }

        } else {
            Toast.makeText(getBaseContext(), "Please ! Enable shake to delete",
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        inboxAdapter.notifyDataSetChanged();
        InboxActivity.isActivityRunning = true;
    }

    @Override
    protected void onStop() {
        super.onStop();
        InboxActivity.isActivityRunning = false;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (AccelerometerManager.isSupported(this)) {
            AccelerometerManager.startListening(this);
            AccelerometerManager.configure(30, 10000);
        }
    }

    @Override
    public void onRestart() {
        super.onRestart();
        smsDataList = Utils.getLatestMessageOfAllContacts(InboxActivity.this);
        if (smsDataList.isEmpty()) {
            Toast.makeText(getApplicationContext(), "NO MESSAGES",
                    Toast.LENGTH_LONG).show();
        } else {
            inboxAdapter = new InboxAdapter(InboxActivity.this, smsDataList);
            lvMessageList.setAdapter(inboxAdapter);
            inboxAdapter.notifyDataSetChanged();
        }
    }

    @SuppressLint("NewApi")
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.cool_menu, menu);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.action_search)
                .getActionView();
        int searchImgId = getResources().getIdentifier("android:id/search_button", null, null);
        ImageView v = (ImageView) searchView.findViewById(searchImgId);
        v.setImageResource(R.drawable.searchown);


        searchView.setSearchableInfo(searchManager
                .getSearchableInfo(getComponentName()));

        searchView.setOnQueryTextListener(new OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                if (newText.length() > 0) {
                    isSearch = true;
                    searchData = Utils.SearchMessage(newText,
                            (ArrayList<SMSData>) smsDataList.clone());
                    inboxAdapter.updateList(searchData);
                    inboxAdapter.notifyDataSetChanged();
                } else {
                    isSearch = false;
                    inboxAdapter.updateList(smsDataList);
                    inboxAdapter.notifyDataSetChanged();
                }

                return true;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.Settings:
                Intent p = new Intent(this, SettingsActivity.class);
                startActivity(p);
                this.overridePendingTransition(R.anim.left_move, R.anim.left_move1);
                break;
            case R.id.new_message:
                Intent goo = new Intent(InboxActivity.this,
                        SendNewSMSActivity.class);
                startActivity(goo);
                InboxActivity.this.overridePendingTransition(R.anim.left_move,
                        R.anim.left_move1);
                break;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onPause() {

        super.onPause();
    }


}
