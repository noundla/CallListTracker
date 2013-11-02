package com.example.calltracker;

import java.util.ArrayList;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.calltracker.db.CallListDBHelper;
import com.example.calltracker.task.GetCallListTask;
import com.example.calltracker.task.InsertCalllListTask;

public class CallTrackerActivity extends BaseActivity {
	

	private final int SETTINGS_INTENT=1;
	private ListView mListview;
	private Activity mActivity;

	private long mStartDateInMillis;
	private long mEndDateInMillis;
	private int mSelectedListType=-1;

	private TextView mTotalUnits;

	private boolean mDisplayZeroMinNos = false;
	private boolean mDisplayCUGNos = false;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_call_tracker);
		mActivity = this;
		setSettingsButton();

		loadIntent(getIntent());

		mListview = (ListView)findViewById(R.id.listView1);
		mTotalUnits = (TextView)findViewById(R.id.unitsTV);

		if(mStartDateInMillis>0 && mEndDateInMillis>0){
			if(mSelectedListType>0){
				new GetCallListTask(mActivity, new OnCompleteListener() {
					
					@Override
					public void onComplete(ArrayList<CallInfo> callList) {
						new InsertCalllListTask(mActivity, callList, new OnCompleteListener() {
							
							@Override
							public void onComplete(ArrayList<CallInfo> callList) {
								new FetchCalllListTask().execute();
							}
						}).execute();
						
					}
				}).execute();
			}else{
				Util.showAlert(mActivity, "Alert!", "Please select any of the list type", "Ok", new OnAlertDialogButtonClickListener() {

					@Override
					public void onAlertDialogButtonClick() {
						finish();
					}
				});
			}
		}else{
			Util.showAlert(mActivity, "Alert!", "Please select the start and end date", "Ok", new OnAlertDialogButtonClickListener() {

				@Override
				public void onAlertDialogButtonClick() {
					finish();

				}
			});
		}
	}

	private void loadIntent(Intent intent){
		if(intent!=null){
			if(intent.hasExtra(DateSelectionActivity.EXTRA_START_DATE_IN_MILLIS)){
				mStartDateInMillis  = intent.getLongExtra(DateSelectionActivity.EXTRA_START_DATE_IN_MILLIS, -1);
			}
			if(intent.hasExtra(DateSelectionActivity.EXTRA_END_DATE_IN_MILLIS)){
				mEndDateInMillis  = intent.getLongExtra(DateSelectionActivity.EXTRA_END_DATE_IN_MILLIS, -1);
			}
			if(intent.hasExtra(DateSelectionActivity.EXTRA_SELECTED_LIST_TYPE)){
				mSelectedListType  = intent.getIntExtra(DateSelectionActivity.EXTRA_SELECTED_LIST_TYPE, -1);
			}
			if(intent.hasExtra(DateSelectionActivity.EXTRA_DISPLAY_ZERO_MINS)){
				mDisplayZeroMinNos  = intent.getBooleanExtra(DateSelectionActivity.EXTRA_DISPLAY_ZERO_MINS, false);
			}
			if(intent.hasExtra(DateSelectionActivity.EXTRA_DISPLAY_CUG_NOS)){
				mDisplayCUGNos  = intent.getBooleanExtra(DateSelectionActivity.EXTRA_DISPLAY_CUG_NOS, false);
			}
		}
	}


	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.call_tracker, menu);
		return true;
	}


	private class LogAdapter extends BaseAdapter{
		private ArrayList<CallInfo> mCallList;
		public LogAdapter(ArrayList<CallInfo> callList) {
			mCallList = callList;
		}
		@Override
		public int getCount() {
			return mCallList==null?0:mCallList.size();
		}

		@Override
		public CallInfo getItem(int position) {
			return mCallList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			CallInfo callInfo = mCallList.get(position);

			if(convertView == null){
				convertView = getLayoutInflater().inflate(R.layout.log_item, null);
			}
			TextView number = (TextView)convertView.findViewById(R.id.number);
			TextView duration = (TextView)convertView.findViewById(R.id.duration);
			TextView date = (TextView)convertView.findViewById(R.id.date);

			number.setText(callInfo.getNumber());
			duration.setText(callInfo.getDurationInMins()+" Min(s)");

			date.setText(Util.getStringFromMillis(Constants.CALL_DATE_FORMAT, callInfo.getDate()));
			
			return convertView;
		}
	}

	


	private class FetchCalllListTask extends AsyncTask<Void, Void, CallListDetails>{
		private ProgressDialog mDialog;
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			if(mDialog==null || !mDialog.isShowing()){
				mDialog = ProgressDialog.show(CallTrackerActivity.this, "", "Please wait...");
				mDialog.setCanceledOnTouchOutside(false);
				mDialog.show();
			}
		}

		@Override
		protected CallListDetails doInBackground(Void... params) {
			try{
				CallListDBHelper dbHelper = new CallListDBHelper(mActivity);
				return dbHelper.getCallList(mStartDateInMillis,mEndDateInMillis,mSelectedListType,mDisplayZeroMinNos,mDisplayCUGNos);
			}catch(Exception e){
				e.printStackTrace();
			}

			return null;
		}

		@Override
		protected void onPostExecute(CallListDetails result) {
			super.onPostExecute(result);
			if(result!=null){
				Util.saveStringInSP(mActivity, Constants.SP_LAST_CALL_ID, result.getLatestCallId()+"");
				mTotalUnits.setText(result.getTotalUnits()+"");
				LogAdapter adapter = new LogAdapter(result.getCallList());
				mListview.setAdapter(adapter);
			}

			if(mDialog!=null && mDialog.isShowing()){
				mDialog.dismiss();
			}


		}

	}
}
