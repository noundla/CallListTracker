package com.example.calltracker.db;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils.InsertHelper;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.calltracker.CallInfo;
import com.example.calltracker.CallListDetails;
import com.example.calltracker.Constants;


public class CallListDBHelper extends SQLiteOpenHelper{

	public CallListDBHelper(Context context) {
		super(context, Constants.DB_NAME, null, Constants.DB_VERSION);
	}
	
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		String query;
		query = "CREATE TABLE "+CallListTable.TABLE_NAME+ "( "+
				CallListTable.callId+" INTEGER PRIMARY KEY, "+
				CallListTable.phoneNumber+" TEXT NOT NULL,"+
				CallListTable.duration+" INTEGER NOT NULL,"+
				CallListTable.date+" INTEGER NOT NULL"+
				")";
		db.execSQL(query);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		//backup old data before upgrate
		db.execSQL("create table "+CallListTable.TABLE_NAME+"_"+oldVersion+" as select "+ CallListTable.callId+","+CallListTable.phoneNumber+","+CallListTable.duration+","+CallListTable.date +"from "+CallListTable.TABLE_NAME);
		db.execSQL("DROP TABLE IF EXISTS " + CallListTable.TABLE_NAME);
		onCreate(db);
	}


	public void insertCallInfo(CallInfo callInfo) {
		SQLiteDatabase db = this.getWritableDatabase();
		try{
			ContentValues values = new ContentValues();

			values.put(CallListTable.callId, callInfo.getId()); 
			values.put(CallListTable.phoneNumber, callInfo.getNumber());
			values.put(CallListTable.duration, callInfo.getDurationInMins());
			values.put(CallListTable.date, callInfo.getDate());

			// Inserting Row
			db.insert(CallListTable.TABLE_NAME, null, values);
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			closeDatabase(db);
		}

	}



	public void insertBulkCallList(ArrayList<CallInfo> callList){
		SQLiteDatabase db = this.getWritableDatabase();
		try{
			// Create a single InsertHelper to handle this set of insertions.
			InsertHelper ih = new InsertHelper(db, CallListTable.TABLE_NAME);

			// Get the numeric indexes for each of the columns that we're updating
			final int callIdColumn = ih.getColumnIndex(CallListTable.callId);
			final int phoneColumn = ih.getColumnIndex(CallListTable.phoneNumber);
			final int durationColumn = ih.getColumnIndex(CallListTable.duration);
			final int dateColumn = ih.getColumnIndex(CallListTable.date);
			try {
				for (CallInfo callInfo : callList) {
					if(callInfo!=null){
						// ... Create the data for this row (not shown) ...

						// Get the InsertHelper ready to insert a single row
						ih.prepareForInsert();

						// Add the data for each column
						ih.bind(callIdColumn, callInfo.getId());
						ih.bind(phoneColumn, callInfo.getNumber());
						ih.bind(durationColumn, callInfo.getDurationInMins());
						ih.bind(dateColumn, callInfo.getDate());

						// Insert the row into the database.
						ih.execute();		
					}
				}
			}catch(Exception e){
				e.printStackTrace();
			}finally {
				if(ih!=null )
					ih.close();  // See comment below from Stefan Anca
			}
		}finally{
			closeDatabase(db);
		}
	}



	public CallListDetails getCallList(long startDate,long endDate, int listType, boolean displayZeroMinNos){
		CallListDetails listDetails = new CallListDetails();
		ArrayList<CallInfo> callList = new ArrayList<CallInfo>();
		long totalUnits=0;
		SQLiteDatabase db = this.getWritableDatabase();
		try{
			Cursor cursor = null;
			try{
				String listTypeStr = null;
				switch (listType) {
				case Constants.FIXED_PHONES:
					listTypeStr = " AND ( "+CallListTable.phoneNumber+" LIKE \"+9140%\" OR "+
							CallListTable.phoneNumber+" LIKE \"9140%\" OR "+
							CallListTable.phoneNumber+" LIKE \"040%\" OR "+
							CallListTable.phoneNumber+" LIKE \"+040%\" )";

					break;
				case Constants.MOBILE_PHONES:


					listTypeStr =" AND ("+

							"(("+CallListTable.phoneNumber+" LIKE \"+919%\" OR "+CallListTable.phoneNumber+" LIKE \"+918%\" OR "+CallListTable.phoneNumber+" LIKE \"+917%\") AND length("+CallListTable.phoneNumber+") = 13 ) OR "+
							"(("+CallListTable.phoneNumber+" LIKE \"919%\" OR "+CallListTable.phoneNumber+" LIKE \"918%\" OR "+CallListTable.phoneNumber+" LIKE \"917%\") AND length("+CallListTable.phoneNumber+") = 12 ) OR "+
							"(("+CallListTable.phoneNumber+" LIKE \"09%\" OR "+CallListTable.phoneNumber+" LIKE \"08%\" OR "+CallListTable.phoneNumber+" LIKE \"07%\") AND length("+CallListTable.phoneNumber+") = 11 ) OR "+
							"(("+CallListTable.phoneNumber+" LIKE \"9%\" OR "+CallListTable.phoneNumber+" LIKE \"8%\" OR "+CallListTable.phoneNumber+" LIKE \"7%\") AND length("+CallListTable.phoneNumber+") = 10 ) "+
							" ) ";
					break;
				case Constants.EXCLUDE_ABOVE_2_AND_3:
					listTypeStr = " AND NOT ( "+CallListTable.phoneNumber+" LIKE \"+9140%\" OR "+
							CallListTable.phoneNumber+" LIKE \"9140%\" OR "+
							CallListTable.phoneNumber+" LIKE \"040%\" OR "+
							CallListTable.phoneNumber+" LIKE \"+040%\" ) AND NOT ("+
							"(("+CallListTable.phoneNumber+" LIKE \"+919%\" OR "+CallListTable.phoneNumber+" LIKE \"+918%\" OR "+CallListTable.phoneNumber+" LIKE \"+917%\") AND length("+CallListTable.phoneNumber+") = 13 ) OR "+
							"(("+CallListTable.phoneNumber+" LIKE \"919%\" OR "+CallListTable.phoneNumber+" LIKE \"918%\" OR "+CallListTable.phoneNumber+" LIKE \"917%\") AND length("+CallListTable.phoneNumber+") = 12 ) OR "+
							"(("+CallListTable.phoneNumber+" LIKE \"09%\" OR "+CallListTable.phoneNumber+" LIKE \"08%\" OR "+CallListTable.phoneNumber+" LIKE \"07%\") AND length("+CallListTable.phoneNumber+") = 11 ) OR "+
							"(("+CallListTable.phoneNumber+" LIKE \"9%\" OR "+CallListTable.phoneNumber+" LIKE \"8%\" OR "+CallListTable.phoneNumber+" LIKE \"7%\") AND length("+CallListTable.phoneNumber+") = 10 ) "+
							" ) ";;
							break;
				case Constants.ALL:
					listTypeStr = null;
					break;	
				default:
					break;
				}

				cursor = db.query(CallListTable.TABLE_NAME, null, CallListTable.date + " >= " + startDate+" AND "+CallListTable.date+" <= "+endDate + (listTypeStr!=null ? listTypeStr:"")+ (displayZeroMinNos ? "" : (" AND "+CallListTable.duration+" > 0")), null, null, null, CallListTable.date+" DESC");

				int id = cursor.getColumnIndex(CallListTable.callId);
				int number = cursor.getColumnIndex(CallListTable.phoneNumber);
				int date = cursor.getColumnIndex( CallListTable.date);
				int duration = cursor.getColumnIndex( CallListTable.duration);
				// looping through all rows and adding to list
				if (cursor!=null && cursor.moveToFirst()) {
					do {
						CallInfo callInfo = new CallInfo();
						callInfo.setId(cursor.getLong(id));
						callInfo.setNumber(cursor.getString(number));
						callInfo.setDate(cursor.getLong(date));
						callInfo.setDurationInMins(cursor.getInt( duration ));
						callList.add(callInfo);
						totalUnits=totalUnits+callInfo.getDurationInMins();
					} while (cursor.moveToNext());
				}
			}catch (Exception e) {
				e.printStackTrace();
			}finally{
				if(cursor!=null){
					cursor.close();
				}
			}

			String maxId = getMaxIdOfCallLogs(db);
			if(maxId!=null){
				listDetails.setLatestCallId(maxId);
			}else{
				if(callList.size()>0){
					listDetails.setLatestCallId(callList.get(0).getId()+"");
				}
			}
		}finally{
			closeDatabase(db);
		}
		listDetails.setTotalUnits(totalUnits);
		listDetails.setCallList(callList);

		return listDetails;
	}

	public String getMaxIdOfCallLogs(SQLiteDatabase db){
		//get the max id from the table
		Cursor maxCursor =null;
		try{
			maxCursor = db.rawQuery("SELECT MAX(" + CallListTable.callId + ") FROM " + CallListTable.TABLE_NAME +";", null);
			if (maxCursor != null) {
				if (maxCursor.moveToFirst() && !maxCursor.isNull(0)) {
					return maxCursor.getString(0);
				}else{
					return null;
				}
			}else{
				return null;

			}

		}catch(Exception e){
			e.printStackTrace();
			return null;
		}finally{
			if(maxCursor!=null){
				maxCursor.close();
			}
		}
	}

	public void closeDatabase(SQLiteDatabase db){
		if(db!=null && db.isOpen()){
			db.close();
		}
	}

}
