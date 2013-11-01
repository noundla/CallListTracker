package com.example.calltracker;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

public class BaseActivity extends FragmentActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
	}
	
	protected void setSettingsButton(){
		ImageView settingsBtn = (ImageView)findViewById(R.id.settingBtn);
		if(settingsBtn!=null){
			settingsBtn.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					Intent intent = new Intent(BaseActivity.this, AddSkipNumberActivity.class);
					startActivity(intent);
				}
			});
		}
	}
}
