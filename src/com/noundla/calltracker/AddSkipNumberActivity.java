package com.noundla.calltracker;

import java.util.ArrayList;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.noundla.calltracker.db.CallListDBHelper;

public class AddSkipNumberActivity extends BaseActivity implements OnClickListener {
	private Activity mActivity;
	private ListView mSkipNumbersListView;
	private EditText mAddNumberET;
	private Button mAddNumberBtn;
	private ArrayList<String> mSkipNumbersList;
	private CallListDBHelper mDbHelper;
	SkipNumbersAdapter mAdapter;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.skip_number);
		mActivity = this;

		mSkipNumbersListView = (ListView)findViewById(R.id.skip_numbers_listView);
		mAddNumberBtn = (Button)findViewById(R.id.addBtn);
		mAddNumberET = (EditText)findViewById(R.id.addNumberEt);
		mAddNumberBtn.setOnClickListener(this);
		mDbHelper = new CallListDBHelper(mActivity);
		new GetSkipNumbersTask().execute();

	}
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.addBtn:
			insertNumber();
			break;
		default:
			break;
		}
	}


	private void insertNumber(){
		String number = mAddNumberET.getText().toString().trim();
		if("".equalsIgnoreCase(number) || number.length()<10){
			Util.showAlert(mActivity, "Alert!", "Please enter valid number");
			return;
		}
		
		
		if(mSkipNumbersList.contains(number) || mSkipNumbersList.contains("+91"+number) || mSkipNumbersList.contains("0"+number)){
			Util.showAlert(mActivity, "Alert!", "Number is already in the skip list. Please enter another one.");
			return;
		}
		
		
		
		String message = "Sorry! Unable to add the number";

		if(mDbHelper.insertSkipNumber(number)){
			message = "Succssfully number added to skip list";
			mSkipNumbersList.add(number);
			mAddNumberET.setText("");
			if(mAdapter!=null){
				mAdapter.notifyDataSetChanged();
			}
		}

		Util.showAlert(mActivity, "Alert!", message);

	}

	private class GetSkipNumbersTask extends AsyncTask<Void, Void, Void>{
		private ProgressDialog mDialog;
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			mDialog = ProgressDialog.show(mActivity, "", "Please wait...");
			mDialog.setCanceledOnTouchOutside(false);
			mDialog.show();
		}
		@Override
		protected Void doInBackground(Void... params) {
			mSkipNumbersList = mDbHelper.getSkipNumberList(true);
			return null;
		}
		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			if(mDialog!=null && mDialog.isShowing()){
				mDialog.dismiss();
			}
			mAdapter = new SkipNumbersAdapter();
			mSkipNumbersListView.setAdapter(mAdapter);
		}

	}

	private class SkipNumbersAdapter extends BaseAdapter{
		@Override
		public int getCount() {
			return mSkipNumbersList==null? 0 :mSkipNumbersList.size();
		}

		@Override
		public String getItem(int pos) {
			return mSkipNumbersList.get(pos);
		}

		@Override
		public long getItemId(int arg0) {
			return arg0;
		}
		@Override
		public View getView(int position, View convertView, ViewGroup group) {
			final String number = mSkipNumbersList.get(position);
			if(convertView==null){
				convertView = getLayoutInflater().inflate(R.layout.skip_number_item, null);
			}
			TextView numberTV = (TextView)convertView.findViewById(R.id.numberTv);
			ImageButton deleteBtn = (ImageButton)convertView.findViewById(R.id.deleteBtn);
			numberTV.setText(number);
			deleteBtn.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					deleteNumber(number);
				}
			});
			return convertView;
		}

		private void deleteNumber(String number){
			String message = "Sorry! Unable to remove the number";
			if(mDbHelper.deleteSkipNumber(number)){
				message = "Deleted successfully";	
				mSkipNumbersList.remove(number);
				if(mAdapter!=null){
					mAdapter.notifyDataSetChanged();
				}
			}
			Util.showAlert(mActivity, "Alert!", message);
		}

	}


}
