package com.example.calltracker;

import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.view.Window;

public class SettingsActivity extends PreferenceActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		addPreferencesFromResource(R.xml.settings);
	}
}	
