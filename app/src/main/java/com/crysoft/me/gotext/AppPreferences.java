package com.crysoft.me.gotext;

import android.content.Context;
import android.graphics.Color;
import android.preference.PreferenceCategory;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

public class AppPreferences extends PreferenceCategory{

	public AppPreferences(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}
	
	
	public AppPreferences(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}
	
	
	public AppPreferences(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}
	
	
	
	@Override
	protected void onBindView(View view) {
		// TODO Auto-generated method stub
		super.onBindView(view);
		  TextView titleView = (TextView) view.findViewById(android.R.id.title);
	        titleView.setTextColor(Color.GRAY);
	}

}


