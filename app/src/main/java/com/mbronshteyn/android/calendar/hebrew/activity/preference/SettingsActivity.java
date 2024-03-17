package com.mbronshteyn.android.calendar.hebrew.activity.preference;

import android.os.Bundle;
import android.preference.PreferenceActivity;

import com.mbronshteyn.android.calendar.hebrew.R;

public class SettingsActivity extends PreferenceActivity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preferences);
	}

}
