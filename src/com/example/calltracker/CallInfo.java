package com.example.calltracker;

public class CallInfo {
	private String number="";
	private long date;
	private int durationInMinutes;
	private long id;
	
	public int getDurationInMins() {
		return durationInMinutes;
	}
	public void setDurationInMins(int durationInMinutes) {
		this.durationInMinutes = durationInMinutes;
	}
	public long getDate() {
		return date;
	}
	public void setDate(long date) {
		this.date = date;
	}
	public String getNumber() {
		return number;
	}
	public void setNumber(String number) {
		this.number = number;
	}
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
}
