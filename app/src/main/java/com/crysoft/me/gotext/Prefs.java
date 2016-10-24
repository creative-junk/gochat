package com.crysoft.me.gotext;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.preference.RingtonePreference;
import android.preference.SwitchPreference;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.widget.ListView;

@SuppressLint("NewApi")
public class Prefs extends PreferenceActivity {

	SwitchPreference sp;
	ListPreference listPreference;
	RingtonePreference rp;
	PreferenceCategory pc1, pc2, pc3;
	public CheckBoxPreference vb;
	public static Vibrator vib;
	CheckBoxPreference imageviewer;

	@SuppressWarnings("deprecation")
	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);


		getListView().setBackgroundDrawable(
				getResources().getDrawable(R.color.background));
		getListView().setCacheColorHint(Color.TRANSPARENT);
		getPreferenceManager().setSharedPreferencesName("my_prefs");

		addPreferencesFromResource(R.xml.prefs);
		SharedPreference();

		if (listPreference.getValue() == null) {
			// to ensure we don't get a null value
			// set first value by default

			listPreference.setValueIndex(0);

		}
		listPreference.setSummary(listPreference.getValue().toString());
		setValues();

		sp.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {

			@Override
			public boolean onPreferenceChange(Preference preference,
					Object newValue) {
				// TODO Auto-generated method stub

				if (!sp.isChecked()) {
					Utils.savePreferences("Auto-Destory", "Yes", Prefs.this);
					listPreference.setEnabled(true);
					sp.setChecked(true);

				} else {
					Utils.savePreferences("Auto-Destory", "No", Prefs.this);
					listPreference.setEnabled(false);
					sp.setChecked(false);
				}
				return true;
			}
		});
		listPreference
				.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {

					@Override
					public boolean onPreferenceChange(Preference preference,
							Object newValue) {
						// TODO Auto-generated method stub

						if (sp.isChecked())

						{

							String value = listPreference.getValue();
							Utils.savePreferences("Schedule", value, Prefs.this);
							// listPreference.setSummary(value);
							preference.setSummary(newValue.toString());

							setValues();

						}
						return true;
					}
				});
	}

	@SuppressLint("NewApi")
	private void SharedPreference() {
		// TODO Auto-generated method stub
		rp = (RingtonePreference) findPreference("tone");
		pc1 = (PreferenceCategory) findPreference("first_category");
		pc2 = (PreferenceCategory) findPreference("second_category");
		pc3 = (PreferenceCategory) findPreference("pc3");
		vb = (CheckBoxPreference) findPreference("vibrator");
		imageviewer = (CheckBoxPreference) findPreference("Contact_Image_Viewer");
		// for Vibrator on/off

		Spannable summaryIV = new SpannableString(imageviewer.getTitle());
		summaryIV.setSpan(new ForegroundColorSpan(Color.BLACK), 0,
				summaryIV.length(), 0);
		imageviewer.setTitle(summaryIV);

		// for Vibrator title color change
		Spannable summaryvb = new SpannableString(vb.getTitle());
		summaryvb.setSpan(new ForegroundColorSpan(Color.BLACK), 0,
				summaryvb.length(), 0);
		vb.setTitle(summaryvb);

		// for PreferenceCategory title color change
		Spannable summarypc1 = new SpannableString(pc1.getTitle());
		summarypc1.setSpan(new ForegroundColorSpan(Color.BLACK), 0,
				summarypc1.length(), 0);
		pc1.setTitle(summarypc1);

		// for RingtonePreference title color change
		Spannable summaryrp1 = new SpannableString(rp.getTitle());
		summaryrp1.setSpan(new ForegroundColorSpan(Color.BLACK), 0,
				summaryrp1.length(), 0);
		rp.setTitle(summaryrp1);

		updateSummary(rp);

		// divider line for Ring tone Preference
		ListView lv1 = getListView();
		lv1.setDivider(new ColorDrawable(Color.LTGRAY));
		lv1.setDividerHeight(5);

		sp = (SwitchPreference) findPreference("Checkbox");
		// for CheckboxPreference title color change
		Spannable summary = new SpannableString(sp.getTitle());
		summary.setSpan(new ForegroundColorSpan(Color.BLACK), 0,
				summary.length(), 0);
		sp.setTitle(summary);

		// divider line for checkBox preference
		ListView lvs = getListView();
		lvs.setDivider(new ColorDrawable(Color.LTGRAY));
		lvs.setDividerHeight(5);

		listPreference = (ListPreference) findPreference("timer");

		// for listPreference title color change
		Spannable summarylp = new SpannableString(listPreference.getTitle());
		summarylp.setSpan(new ForegroundColorSpan(Color.BLACK), 0,
				summarylp.length(), 0);
		listPreference.setTitle(summarylp);
		// divider line for list preference
		ListView lv = getListView();
		lv.setDivider(new ColorDrawable(Color.LTGRAY));
		lv.setDividerHeight(5);
	}

	@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
	@SuppressLint("NewApi")
	public void setValues() {
		String check = Utils.getPreferences("Auto-Destory", this);
		String listvalue = Utils.getPreferences("Schedule", this);
		listPreference.setValue(listvalue);

		if (check.equalsIgnoreCase("yes")) {

			sp.setChecked(true);
			listPreference.setEnabled(true);
			listPreference.setValue(Utils.getPreferences("Schedule", this));
		} else {

			sp.setChecked(false);
			listPreference.setEnabled(false);
			listPreference.setValue(Utils.getPreferences("Schedule", this));
		}
	}

	// update summary of ringtonePreference
	private void updateSummary(Preference p) {

		if (p instanceof RingtonePreference) {

			Log.i("***", "RingtonePreference " + p.getKey());
			final RingtonePreference ringPref = (RingtonePreference) p;
			ringPref.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
				@Override
				public boolean onPreferenceChange(Preference preference,
						Object newValue) {
					Log.i("***", "Changed " + newValue.toString());
					Ringtone ringtone = RingtoneManager.getRingtone(Prefs.this,
							Uri.parse((String) newValue));
					ringPref.setSummary(ringtone.getTitle(Prefs.this));
					return true;
				}
			});
			String ringtonePath = p.getSharedPreferences().getString(
					p.getKey(), "defValue");
			Ringtone ringtone = RingtoneManager.getRingtone(Prefs.this,
					Uri.parse((String) ringtonePath));
			// ringPref.setSummary(ringtone.getTitle(Prefs.this));

		}
	}

	@Override
	public void onBackPressed() {
		if (vb.isChecked()) {
			Utils.savePreferences("Vibration", "ON", Prefs.this);
		} else {
			Utils.savePreferences("Vibration", "OFF", Prefs.this);
		}
		if (imageviewer.isChecked()) {
			Utils.savePreferences("ImageViewer", "ON", Prefs.this);
		} else if (!imageviewer.isChecked()) {
			Utils.savePreferences("ImageViewer", "OFF", Prefs.this);
		}

		finish();
	}
}