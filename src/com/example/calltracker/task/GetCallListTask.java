package com.example.calltracker.task;

import java.util.ArrayList;

import android.app.Activity;
import android.app.ProgressDialog;
import android.database.Cursor;
import android.os.AsyncTask;
import android.provider.CallLog;
import android.text.TextUtils;

import com.example.calltracker.CallInfo;
import com.example.calltracker.Constants;
import com.example.calltracker.OnCompleteListener;
import com.example.calltracker.Util;

public class GetCallListTask extends AsyncTask<Void, Void, ArrayList<CallInfo>>{
	private ProgressDialog mDialog;
	private Activity mActivity;
	private OnCompleteListener mListener;
	public GetCallListTask(Activity activity, OnCompleteListener listener) {
		mActivity = activity;
		mListener = listener;
	}
	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		mDialog = ProgressDialog.show(mActivity, "", "Please wait...");
		mDialog.setCanceledOnTouchOutside(false);
		mDialog.show();
	}
	@Override
	protected ArrayList<CallInfo> doInBackground(Void... params) {

		return getCallDetails();
	}

	@Override
	protected void onPostExecute(ArrayList<CallInfo> result) {
		super.onPostExecute(result);
		if(mDialog!=null && mDialog.isShowing()){
			mDialog.dismiss();
		}
		if(result!=null){
			mListener.onComplete(result);
		}


		//			LogAdapter adapter = new LogAdapter(result);
		//			mListview.setAdapter(adapter);
	}

	@SuppressWarnings("deprecation")
	private ArrayList<CallInfo> getCallDetails() {
		ArrayList<CallInfo> callList = new ArrayList<CallInfo>();
		Cursor managedCursor = null;
		try{
			String minCallId = Util.getStringFromSP(mActivity, Constants.SP_LAST_CALL_ID);
			if(minCallId==null || "".equalsIgnoreCase(minCallId)|| "null".equalsIgnoreCase(minCallId)){
				minCallId = "0";
			}


			managedCursor = mActivity.managedQuery( CallLog.Calls.CONTENT_URI, null, CallLog.Calls.TYPE+" = " + CallLog.Calls.OUTGOING_TYPE + " AND " + CallLog.Calls._ID + " > " + minCallId ,null, null);
			mActivity.stopManagingCursor(managedCursor);
			int id = managedCursor.getColumnIndex(CallLog.Calls._ID);
			int number = managedCursor.getColumnIndex( CallLog.Calls.NUMBER );
			int date = managedCursor.getColumnIndex( CallLog.Calls.DATE);
			int duration = managedCursor.getColumnIndex( CallLog.Calls.DURATION);

			if(managedCursor!=null && managedCursor.moveToFirst()){
				do {
					CallInfo callInfo = new CallInfo();
					callInfo.setId(managedCursor.getLong(id));
					callInfo.setNumber(managedCursor.getString(number));
					callInfo.setDate(Long.parseLong(managedCursor.getString(date)));
					callInfo.setDurationInMins(Util.getDurationInMinutes(managedCursor.getString( duration )));
					callList.add(callInfo);
				}while ( managedCursor.moveToNext());
			}

			return callList;
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			if(managedCursor!=null){
				managedCursor.close();
				/*Warning: Do not call close() on a cursor obtained using this method, because the activity will do that for you at the appropriate time. 
				 * However, if you call stopManagingCursor(Cursor) on a cursor from a managed query, 
				 * the system will not automatically close the cursor and, in that case, you must call close().*/


			}
		}
		return null;
	}

}
