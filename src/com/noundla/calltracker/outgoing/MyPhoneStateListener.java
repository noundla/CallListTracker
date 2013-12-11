package com.noundla.calltracker.outgoing;

import java.util.ArrayList;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.provider.CallLog;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.noundla.calltracker.CallInfo;
import com.noundla.calltracker.Constants;
import com.noundla.calltracker.Util;
import com.noundla.calltracker.db.CallListDBHelper;
import com.noundla.calltracker.db.CallListTable;
import com.noundla.calltracker.task.GetCallListTask;

public class MyPhoneStateListener extends PhoneStateListener {
	private Context mContext;
	public static Boolean phoneRinging = false;
	public static boolean mIsCallOffHook = false;

	public MyPhoneStateListener(Context context) {
		mContext = context;
		mIsCallOffHook = false;
	}
	public void onCallStateChanged(int state, String incomingNumber) {

		switch (state) {
		case TelephonyManager.CALL_STATE_IDLE:
			Log.d("DEBUG", "IDLE");
//			Toast.makeText(mContext, "idle1", 500).show();
			phoneRinging = false;
			if(mIsCallOffHook){
//				Toast.makeText(mContext, "idle2", 500).show();
				mIsCallOffHook = false;
				new GetAndInsertCallLogsTask().execute();

			}
			break;
		case TelephonyManager.CALL_STATE_OFFHOOK:
			Log.d("DEBUG", "OFFHOOK");
//			Toast.makeText(mContext, "OFFHOOK", 500).show();
			phoneRinging = false;
			mIsCallOffHook = true;
			break;
		case TelephonyManager.CALL_STATE_RINGING:
			Log.d("DEBUG", "RINGING");
			//			Toast.makeText(mContext, "RINGING", 500).show();
			phoneRinging = true;

			break;
		}
	}

	public ArrayList<CallInfo> queryAllCallLog() {
		ArrayList<CallInfo> result = new ArrayList<CallInfo>();
		final ContentResolver contentResolver = mContext.getContentResolver();
		String minCallId = Util.getStringFromSP(mContext, Constants.SP_LAST_CALL_ID);

		if(minCallId==null || "".equalsIgnoreCase(minCallId)|| "null".equalsIgnoreCase(minCallId)){
			minCallId = "0";
		}
		Cursor cursor=null;
		try{
			cursor = contentResolver.query(CallLog.Calls.CONTENT_URI, null, CallLog.Calls.TYPE+" = " + CallLog.Calls.OUTGOING_TYPE + " AND " + CallLog.Calls._ID + " > " + minCallId ,null, null);
			int id = cursor.getColumnIndex(CallLog.Calls._ID);
			int number = cursor.getColumnIndex( CallLog.Calls.NUMBER );
			int date = cursor.getColumnIndex( CallLog.Calls.DATE);
			int duration = cursor.getColumnIndex( CallLog.Calls.DURATION);

			if(cursor!=null && cursor.moveToFirst()){
				do {
					CallInfo callInfo = new CallInfo();
					callInfo.setId(cursor.getLong(id));
					callInfo.setNumber(cursor.getString(number));
					callInfo.setDate(Long.parseLong(cursor.getString(date)));
					callInfo.setDurationInMins(Util.getDurationInMinutes(cursor.getString( duration )));
					result.add(callInfo);
				}while ( cursor.moveToNext());
			}
			return result;
		}catch (Exception e) {
			e.printStackTrace();
		}finally{
			if(cursor!=null){
				cursor.close();
			}
		}
		return null;
	}

	private class GetAndInsertCallLogsTask extends AsyncTask<Void, Void, Void>{
		public GetAndInsertCallLogsTask() {

		}
		@Override
		protected Void doInBackground(Void... params) {
			try{
				ArrayList<CallInfo> callList1 = queryAllCallLog();
				CallListDBHelper dbHelper = new CallListDBHelper(mContext);
				dbHelper.insertBulkCallList(callList1);
				SQLiteDatabase db = dbHelper.getWritableDatabase();
				try{
					String maxId = dbHelper.getMaxIdOfCallLogs(dbHelper.getWritableDatabase());
					if(maxId!=null){
						Util.saveStringInSP(mContext, Constants.SP_LAST_CALL_ID, maxId);
					}
				}finally{
					dbHelper.closeDatabase(db);
				}
			}catch (Exception e) {
				e.printStackTrace();
				
			}
			return null;
		}
		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			Toast.makeText(mContext, "Added to call list..", 500).show();
		}
	}
}
