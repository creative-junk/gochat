package com.crysoft.me.gotext;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import Constant.MySQLiteHelper;

/**
 * Created by Shahzad Ahmad on 3/27/2015.
 */
public class SettingsActivity extends Activity {
    Context context;
    CheckBox cbAvatar, cbVibration, cbDelete, cbQuickMessage;
    RadioGroup radioGroupDuration;
    RadioButton cbOneweek, cbTwoweek, cbThreeweek, cbOnemonth;
    int intAvt, intVib, intDel, intQuick = 0;
    RelativeLayout duration;
    TextView tvDeleteDuration;
    EditText etSpecialPeople;
    MySQLiteHelper Db;
    int intLimit, intCounter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.preferences);
        ActionBar bar = getActionBar();
        bar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#0154A4")));
        bar.setIcon(R.drawable.icon_top);
        bar.setTitle(Html.fromHtml("<font color='#ffffff'>WeText </font>"));


        context = this;


        Db = new MySQLiteHelper(context);
        cbAvatar = (CheckBox) findViewById(R.id.cbAvatar);
        cbVibration = (CheckBox) findViewById(R.id.cbVibration);
        cbDelete = (CheckBox) findViewById(R.id.cbDeletor);
        cbQuickMessage = (CheckBox) findViewById(R.id.cbQuickMessage);

        etSpecialPeople = (EditText) findViewById(R.id.etspecialpeople);

        duration = (RelativeLayout) findViewById(R.id.rlDeleteDuration);
        tvDeleteDuration = (TextView) findViewById(R.id.tvselectdeleteduration);
        Checker();


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
    }

    private void Checker() {
        String strImageViewer = Utils.getPreferences("ImageViewer", context);
        String strVibrator = Utils.getPreferences("Vibration", context);
        String strAutoDestroy = Utils.getPreferences("Auto-Destory", context);
        String strSchedule = Utils.getPreferences("Schedule", context);
        String strQuickmessage = Utils.getPreferences("QuickMessage", context);

        if (strQuickmessage.equalsIgnoreCase("Yes")) {
            cbQuickMessage.setChecked(true);
        }
        if (strImageViewer.equalsIgnoreCase("ON")) {
            cbAvatar.setChecked(true);
        }
        if (strVibrator.equalsIgnoreCase("ON")) {
            cbVibration.setChecked(true);
        }
        if (strAutoDestroy.equalsIgnoreCase("YES")) {
            cbDelete.setChecked(true);
        }

    }


    public void CheckBoxClicker(View v) {
        switch (v.getId()) {
            case R.id.cbQuickMessage:
                if (intQuick != 0) {
                    cbQuickMessage.setChecked(false);

                    Utils.savePreferences("QuickMessage", "No", context);
                    intQuick = 0;
                } else {
                    cbQuickMessage.setChecked(true);

                    Utils.savePreferences("QuickMessage", "Yes", context);
                    intQuick = 1;
                }
                break;
            case R.id.cbAvatar:
                if (intAvt != 0) {
                    cbAvatar.setChecked(false);
                    Utils.savePreferences("ImageViewer", "OFF", context);
                    intAvt = 0;
                } else {
                    cbAvatar.setChecked(true);
                    Utils.savePreferences("ImageViewer", "ON", context);
                    intAvt = 1;
                }
                break;
            case R.id.cbVibration:
                if (intVib != 0) {
                    cbVibration.setChecked(false);
                    Utils.savePreferences("Vibration", "OFF", context);
                    intVib = 0;
                } else {
                    cbVibration.setChecked(true);
                    Utils.savePreferences("Vibration", "ON", context);
                    intVib = 1;
                }
                break;
            case R.id.cbDeletor:
                if (intDel != 0) {
                    cbDelete.setChecked(false);
                    Utils.savePreferences("Auto-Destory", "No", context);
                    duration.setClickable(false);
                    tvDeleteDuration.setTypeface(Typeface.DEFAULT);
                    intDel = 0;
                } else {
                    cbDelete.setChecked(true);
                    Utils.savePreferences("Auto-Destory", "Yes", context);
                    duration.setClickable(true);
                    tvDeleteDuration.setTypeface(Typeface.DEFAULT_BOLD);
                    intDel = 1;
                }
                break;
            case R.id.btnpreferenceadd:
                intLimit = Utils.getPreferencesInt("SpecialPeopleLimit", this);
                String strSpecialname = etSpecialPeople.getText().toString();

                intCounter = Utils.getPreferencesInt("SpecialCounter", this);
                if (intCounter < intLimit) {
                    if (!strSpecialname.equals("")) {
                        Db.insertSpecialUsers(strSpecialname);
                        etSpecialPeople.setText("");
                        Toast.makeText(context, "Added", Toast.LENGTH_SHORT).show();
                        int count = intCounter + 1;
                        Utils.savePreferencesInt("SpecialCounter", count, this);
                    } else {
                        String toas = "Please Enter Name First !";
                        Toast.makeText(SettingsActivity.this, "Please Enter Name First !", Toast.LENGTH_SHORT).show();

                    }
                } else {
                    Toast.makeText(SettingsActivity.this, "Limit Reached", Toast.LENGTH_SHORT).show();
                    //@TODO What will we do when a user reaches their Limit
                }
                break;
        }
    }

    public void CustomDialog() {
        // custom dialog

        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.customdialog);
        String strSchedule = Utils.getPreferences("Schedule", context);
        cbOneweek = (RadioButton) dialog.findViewById(R.id.rloneweek);
        cbTwoweek = (RadioButton) dialog.findViewById(R.id.rltwoweek);
        cbThreeweek = (RadioButton) dialog.findViewById(R.id.rlthreeweek);
        cbOnemonth = (RadioButton) dialog.findViewById(R.id.rlonemonth);

        if (strSchedule.equalsIgnoreCase("One Week Selected")) {
            cbOneweek.setChecked(true);

        } else if (strSchedule.equalsIgnoreCase("Two Week Selected")) {
            cbTwoweek.setChecked(true);
        } else if (strSchedule.equalsIgnoreCase("Three Week Selected")) {
            cbThreeweek.setChecked(true);
        } else if (strSchedule.equalsIgnoreCase("One Month Selected")) {
            cbOnemonth.setChecked(true);
        }

        radioGroupDuration = (RadioGroup) dialog.findViewById(R.id.rgduration);

        radioGroupDuration.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                if (checkedId == R.id.rloneweek) {
                    String oneWeek = "One Week Selected";
                    Utils.savePreferences("Schedule", oneWeek, context);
                } else if (checkedId == R.id.rltwoweek) {
                    String twoWeek = "Two Week Selected";
                    Utils.savePreferences("Schedule", twoWeek, context);
                } else if (checkedId == R.id.rlthreeweek) {
                    String threeWeek = "Three Week Selected";
                    Utils.savePreferences("Schedule", threeWeek, context);
                } else if (checkedId == R.id.rlonemonth) {
                    String onemonth = "One Month Selected";
                    Utils.savePreferences("Schedule", onemonth, context);
                }

            }
        });
        dialog.show();
    }


    public void PreferenceClicker(View v) {
        switch (v.getId()) {

            case R.id.rlAvatar:
                if (cbAvatar.isChecked()) {
                    cbAvatar.setChecked(false);
                    Utils.savePreferences("ImageViewer", "OFF", context);
                    intAvt = 0;
                } else {
                    cbAvatar.setChecked(true);
                    Utils.savePreferences("ImageViewer", "ON", context);
                    intAvt = 1;
                }
                break;

            case R.id.rlVibration:
                if (cbVibration.isChecked()) {
                    cbVibration.setChecked(false);
                    Utils.savePreferences("Vibration", "OFF", context);
                    intVib = 0;
                } else {
                    cbVibration.setChecked(true);
                    Utils.savePreferences("Vibration", "ON", context);
                    intVib = 1;
                }
                break;
            case R.id.rlshaketodelete:
                if (cbDelete.isChecked()) {
                    cbDelete.setChecked(false);
                    Utils.savePreferences("Auto-Destory", "No", context);
                    duration.setClickable(false);
                    tvDeleteDuration.setTypeface(Typeface.DEFAULT);
                    intDel = 0;
                } else {
                    cbDelete.setChecked(true);
                    Utils.savePreferences("Auto-Destory", "Yes", context);
                    duration.setClickable(true);
                    tvDeleteDuration.setTypeface(Typeface.DEFAULT_BOLD);
                    intDel = 1;
                }
                break;

            case R.id.rlDeleteDuration:
                if (cbDelete.isChecked()) {
                    CustomDialog();
                }
                break;
            case R.id.rlquickmessage:
                if (cbQuickMessage.isChecked()) {
                    cbQuickMessage.setChecked(false);
                    Utils.savePreferences("QuickMessage", "No", context);
                    intQuick = 0;
                } else {
                    cbQuickMessage.setChecked(true);
                    Utils.savePreferences("QuickMessage", "Yes", context);
                    intQuick = 1;
                }
                break;

            case R.id.rlaboutus:
                Intent i = new Intent("android.intent.action.ABOUT");
                startActivity(i);
                this.overridePendingTransition(R.anim.left_move, R.anim.left_move1);
                break;
        }


    }


}
