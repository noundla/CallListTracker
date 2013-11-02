package com.example.calltracker;

import java.util.Calendar;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.RadioButton;

import com.example.calltracker.DateDialogFragment.OnDateSetListener;

public class DateSelectionActivity extends BaseActivity implements OnClickListener{
	public static final String EXTRA_START_DATE_IN_MILLIS="EXTRA_START_DATE";
	public static final String EXTRA_END_DATE_IN_MILLIS="EXTRA_END_DATE";
	public static final String EXTRA_SELECTED_LIST_TYPE="EXTRA_SELECTED_LIST_TYPE";
	public static final String EXTRA_DISPLAY_ZERO_MINS="EXTRA_DISPLAY_ZERO_MINS";
	public static final String EXTRA_DISPLAY_CUG_NOS="EXTRA_CUG_NOS";
	
	
	
	private EditText mStartDateEt;
	private EditText mEndDateEt;
	private Activity mActivity;
	
	/**stores the date to show on date picker*/
	protected Calendar mStartDate=Calendar.getInstance();
	
	protected Calendar mEndDate=Calendar.getInstance();
	
	private RadioButton mAllRB;
	private RadioButton mMobilePhonesRB;
	private RadioButton mFixedPhonesRB;
	private RadioButton mExclude2And3RB;
	private CheckBox mDisplayZeroMinNumbers;
	private RadioButton mCUGNumbersRB;
	private CheckBox mDisplayCUGNumbers;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.date_selection);
		mActivity = this;
		setSettingsButton();
		
		mStartDateEt = (EditText)findViewById(R.id.startDateET);
		mStartDateEt.setOnClickListener(this);
		
		mEndDateEt = (EditText)findViewById(R.id.endDateET);
		mEndDateEt.setOnClickListener(this);
		
		mAllRB = (RadioButton)findViewById(R.id.all);
		mMobilePhonesRB = (RadioButton)findViewById(R.id.mobile_phones);
		mFixedPhonesRB = (RadioButton)findViewById(R.id.fixed_phones);
		mExclude2And3RB = (RadioButton)findViewById(R.id.exclude_above_two);
		mCUGNumbersRB = (RadioButton)findViewById(R.id.only_cug_nos);
		mDisplayZeroMinNumbers = (CheckBox)findViewById(R.id.display_zero_min_nos);
		mDisplayCUGNumbers = (CheckBox)findViewById(R.id.include_skip_nos);
		findViewById(R.id.goBtn).setOnClickListener(this);
		mCUGNumbersRB.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if(isChecked){
					mDisplayCUGNumbers.setChecked(true);
				}
			}
		});
		//check whether start date has been set
		if(Util.getStringFromSP(mActivity, Constants.SP_START_DATE) == null){
			Calendar endDate = Calendar.getInstance();
			endDate.add(Calendar.DATE, -30);
			endDate.set(Calendar.HOUR_OF_DAY, 0);
			endDate.set(Calendar.MINUTE, 0);
			endDate.set(Calendar.SECOND, 0);
			
			Util.saveStringInSP(mActivity, Constants.SP_START_DATE, endDate.getTimeInMillis()+"");	
		}
		Calendar endCal = Calendar.getInstance();
		endCal.setTimeInMillis(Long.parseLong(Util.getStringFromSP(mActivity, Constants.SP_START_DATE)));
		mStartDateEt.setText(Util.getStringFromMillis(Constants.DATE_FORMAT,endCal.getTimeInMillis()));
		mStartDate = endCal;
		
		//set end date by default to current time
		mEndDate = Calendar.getInstance();
		mEndDateEt.setText(Util.getStringFromMillis(Constants.DATE_FORMAT,mEndDate.getTimeInMillis()));		
	}
	

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.startDateET:
			showDatePickerDialog(true);
			break;
			
		case R.id.endDateET:
			showDatePickerDialog(false);
			break;
		case R.id.goBtn:
			Intent intent = new Intent(mActivity, CallTrackerActivity.class);
			intent.putExtra(EXTRA_START_DATE_IN_MILLIS, mStartDate.getTimeInMillis());
			intent.putExtra(EXTRA_END_DATE_IN_MILLIS, mEndDate.getTimeInMillis());
			if(mAllRB.isChecked()){
				intent.putExtra(EXTRA_SELECTED_LIST_TYPE, Constants.ALL);	
			}else if(mMobilePhonesRB.isChecked()){
				intent.putExtra(EXTRA_SELECTED_LIST_TYPE, Constants.MOBILE_PHONES);
			}else if(mFixedPhonesRB.isChecked()){
				intent.putExtra(EXTRA_SELECTED_LIST_TYPE, Constants.FIXED_PHONES);
			}else if(mCUGNumbersRB.isChecked()){
				intent.putExtra(EXTRA_SELECTED_LIST_TYPE, Constants.CUG_NUMBERS);
			}else{
				intent.putExtra(EXTRA_SELECTED_LIST_TYPE, Constants.EXCLUDE_ABOVE_2_AND_3);
			}
			intent.putExtra(EXTRA_DISPLAY_ZERO_MINS, mDisplayZeroMinNumbers.isChecked());
			intent.putExtra(EXTRA_DISPLAY_CUG_NOS, mDisplayCUGNumbers.isChecked());
			
			startActivity(intent);
			break;

		default:
			break;
		}
		
	}
	
	private void showDatePickerDialog(final boolean isStartDate){
		FragmentManager fm = getSupportFragmentManager();
		DateDialogFragment dateDialogFragment = new DateDialogFragment();
		dateDialogFragment.setData( isStartDate?"Start Date":"End Date", isStartDate?mStartDate:mEndDate);
		dateDialogFragment.setOnDateSetListener(new OnDateSetListener() {

			@Override
			public void onDateSet(Calendar date) {
				
				if(isStartDate){
					date.set(Calendar.HOUR_OF_DAY, 0);
					date.set(Calendar.MINUTE, 0);
					date.set(Calendar.SECOND, 0);
					mStartDate = date; 
					mStartDateEt.setText(Util.getStringFromMillis(Constants.DATE_FORMAT, date.getTimeInMillis()));
					Util.saveStringInSP(mActivity, Constants.SP_START_DATE, date.getTimeInMillis()+"");	
				}else{
					mEndDate = date;
					mEndDateEt.setText(Util.getStringFromMillis(Constants.DATE_FORMAT, date.getTimeInMillis()));
				}
			}
		});
		dateDialogFragment.show(fm, isStartDate?"start Date":"end date");
	}
}
