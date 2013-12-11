package com.noundla.calltracker;

import java.util.ArrayList;

public class CallListDetails {
	private ArrayList<CallInfo> callList;
	private long latestCallDate;
	private long totalUnits;
	
	public ArrayList<CallInfo> getCallList() {
		return callList;
	}
	public void setCallList(ArrayList<CallInfo> callList) {
		this.callList = callList;
	}
	
	public long getTotalUnits() {
		return totalUnits;
	}
	public void setTotalUnits(long totalUnits) {
		this.totalUnits = totalUnits;
	}
	public long getLatestCallDate() {
		return latestCallDate;
	}
	public void setLatestCallDate(long latestCallDate) {
		this.latestCallDate = latestCallDate;
	}
	
	
	
}
