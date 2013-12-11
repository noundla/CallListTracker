package com.noundla.calltracker;

import java.util.ArrayList;

public class CallListDetails {
	private ArrayList<CallInfo> callList;
	private String latestCallId;
	private long totalUnits;
	
	public ArrayList<CallInfo> getCallList() {
		return callList;
	}
	public void setCallList(ArrayList<CallInfo> callList) {
		this.callList = callList;
	}
	public String getLatestCallId() {
		return latestCallId;
	}
	public void setLatestCallId(String latestCallId) {
		this.latestCallId = latestCallId;
	}
	public long getTotalUnits() {
		return totalUnits;
	}
	public void setTotalUnits(long totalUnits) {
		this.totalUnits = totalUnits;
	}
	
	
}
