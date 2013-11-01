package com.example.calltracker;

public class Constants {
	public static final String APP_NAME="CALL LIST TRACKER";
	
	//DB related 
	public static final String DB_NAME="CALL_LIST_TRACKER.sqlite";
	public static final int DB_VERSION = 1;
	public static final String TABLE_NAME="CallList";
	
	
	
	//call list view type
	public static final int ALL = 1;
	public static final int MOBILE_PHONES = 2;
	public static final int FIXED_PHONES = 3;
	public static final int EXCLUDE_ABOVE_2_AND_3 = 4;
	
	
	//SHARED preferences
	public static final String SP_LAST_CALL_ID = "LAST_CALL_ID";
	public static final String SP_START_DATE = "START_DATE";
	
	//date and time patterns
	public static final String DATE_FORMAT = "dd/MM/yyyy";
	public static final String CALL_DATE_FORMAT = "dd/MM/yyyy hh:mm:ss a";
	

}
