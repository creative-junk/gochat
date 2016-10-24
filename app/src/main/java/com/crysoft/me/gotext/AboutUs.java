package com.crysoft.me.gotext;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;

import com.crysoft.me.gotext.R;


public class AboutUs extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.about);


	}

}
