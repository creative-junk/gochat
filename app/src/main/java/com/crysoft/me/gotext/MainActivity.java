package com.crysoft.me.gotext;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.DrawerLayout.LayoutParams;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class MainActivity extends Activity {


    public static final int SPLASH_TIME_OUT = 2000;
    RelativeLayout rl;
    LinearLayout dialog;
    RelativeLayout parentD;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        String isSplash = Utils.getPreferences("Splash", this);
        if (isSplash.equalsIgnoreCase("UserName")) {
            rl = (RelativeLayout) findViewById(R.id.main_layout);
            DialogView(true);
            Utils.savePreferences("Splash", "false", this);

            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    runOnUiThread(new Runnable() {
                        public void run() {
                            int h = parentD.getHeight();
                            Utils.savePreferencesInt("Height", h, MainActivity.this);

                        }
                    });

                    // This method will be executed once the timer is over
                    // Start app main activity
                    Intent i = new Intent(MainActivity.this,
                            InboxActivity.class);
                    startActivity(i);
                    finish();
                    // inbox();
                }
            }, SPLASH_TIME_OUT);

        } else {
            Intent i = new Intent(MainActivity.this, InboxActivity.class);
            startActivity(i);
            finish();
        }

    }

    @SuppressWarnings("ResourceType")
    @SuppressLint("NewApi")
    public void DialogView(boolean invisible) {

        // TODO Auto-generated method stub
        int height = 0;
        if (parentD != null) {
            height = parentD.getHeight();

        }
        int c = height;
        boolean b = Utils.check;

        if (b == false) {
            Utils.check = true;

            TextView tv;
            HorizontalScrollView sv;
            LinearLayout svLayout;
            Button btnFrwd, btnCall, btnCopy, btnDelete;
            ImageView ivImageView = null;

            parentD = new RelativeLayout(MainActivity.this);
            if (invisible) {
                parentD.setVisibility(View.INVISIBLE);
            }
            parentD.setLayoutParams(new RelativeLayout.LayoutParams(
                    LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
            dialog = new LinearLayout(MainActivity.this);
            dialog.setLayoutParams(new LinearLayout.LayoutParams(
                    LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
            dialog.setOrientation(LinearLayout.VERTICAL);
            dialog.setId(1);
            dialog.setBackgroundColor(Color.GRAY);

            tv = new TextView(MainActivity.this);
            tv.setText("Options");
            tv.setId(2);
            tv.setGravity(Gravity.CENTER_HORIZONTAL);
            dialog.addView(tv);

            sv = new HorizontalScrollView(MainActivity.this);
            LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(
                    LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            p.gravity = Gravity.CENTER;
            sv.setLayoutParams(p);
            sv.setId(3);
            svLayout = new LinearLayout(MainActivity.this);
            svLayout.setOrientation(LinearLayout.HORIZONTAL);
            svLayout.setLayoutParams(new LinearLayout.LayoutParams(
                    LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
            svLayout.setId(4);

            btnFrwd = new Button(MainActivity.this);
            btnFrwd.setId(5);
            btnFrwd.setText("Forward");
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            btnFrwd.setLayoutParams(params);
            svLayout.addView(btnFrwd);

            btnCall = new Button(MainActivity.this);
            btnCall.setId(6);
            params = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
                    LayoutParams.WRAP_CONTENT);
            btnCall.setText("Call");
            btnCall.setLayoutParams(params);
            svLayout.addView(btnCall);

            btnCopy = new Button(MainActivity.this);
            btnCopy.setText("Copy");
            btnCopy.setId(7);
            params = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
                    LayoutParams.WRAP_CONTENT);
            btnCopy.setLayoutParams(params);
            svLayout.addView(btnCopy);

            btnDelete = new Button(MainActivity.this);
            btnDelete.setText("Delete");
            btnDelete.setId(8);
            params = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
                    LayoutParams.WRAP_CONTENT);
            btnDelete.setLayoutParams(params);
            svLayout.addView(btnDelete);

            ivImageView = new ImageView(MainActivity.this);
            // tv1.setText("lucky");
            RelativeLayout.LayoutParams parameters = new
                    RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
                    LayoutParams.WRAP_CONTENT);
            parameters.addRule(RelativeLayout.BELOW, dialog.getId());
            ivImageView.setLayoutParams(params);
            ivImageView.setId(9);
            ivImageView.setImageResource(com.crysoft.me.gotext.R.drawable.ic_launcher);
            parentD.addView(ivImageView);

            sv.addView(svLayout);
            dialog.addView(sv);
            parentD.addView(dialog);

            rl.addView(parentD);
        }
    }
}
