package com.noundla.calltracker.task;

import java.util.ArrayList;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;

import com.noundla.calltracker.CallInfo;
import com.noundla.calltracker.CallTrackerActivity;
import com.noundla.calltracker.OnCompleteListener;
import com.noundla.calltracker.db.CallListDBHelper;

public class InsertCalllListTask extends AsyncTask<Void, Void, Void>{
	private ProgressDialog mDialog;
	private ArrayList<CallInfo> mCallList;
	private Activity mActivity;
	private OnCompleteListener mListener;
	public InsertCalllListTask(Activity activity,ArrayList<CallInfo> callList, OnCompleteListener listener) {
		mCallList = callList;
		mActivity = activity;
		mListener = listener;
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		if(mDialog==null || !mDialog.isShowing()){
			mDialog = ProgressDialog.show(mActivity, "", "Please wait...");
			mDialog.setCanceledOnTouchOutside(false);
			mDialog.show();
		}
	}
	@Override
	protected Void doInBackground(Void... params) {
		if(mCallList!=null){
			try{
				CallListDBHelper dbHelper = new CallListDBHelper(mActivity);
				dbHelper.insertBulkCallList(mCallList);
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		return null;
	}

	@Override
	protected void onPostExecute(Void result) {
		super.onPostExecute(result);
		if(mDialog!=null && mDialog.isShowing()){
			mDialog.dismiss();
		}
		mListener.onComplete(null);
		
//		new FetchCalllListTask().execute();

	}

}
