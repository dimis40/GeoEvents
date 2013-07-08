package com.example.geoev;

import java.util.Date;

public class FbEvent {
	private String id;
	private String title;
	private String startTime;
	private String endTime;
	private String location;
	private String creator;
	private String description;
	private String updateTime;
	private String latitude;
	private String longitude;
	private String pic;
	private String attending_count;
	
	//
	private Date startDate;
	private Date endDate;
	
	
 
	//provlima me tous constructor
	//ean den iparxei p.x location i end time yparxei provlima
	
	
	public FbEvent(String _id, String _title, String _sT, String _eT, String _loc, String _creator, String _latitude, String _longitude, String _updateTime, String _description){
		this.id = _id;
		this.title = _title;
		this.startTime = _sT;
		this.endTime = _eT;
		this.location = _loc;
		this.creator = _creator;
		this.latitude = _latitude;
		this.longitude = _longitude;
		this.description = _description;
		this.updateTime = _updateTime;
		
	}
	
	public FbEvent(String _id, String _title, String _sT, String _loc){
		this.id = _id;
		this.title = _title;
		this.startTime = _sT;
		this.location = _loc;
	}
	
	//i use this method for creating FBEvent Objects with attending status
	public FbEvent(String _id, String _title, String _sT, String _eT, String _loc, String _creator, String _latitude, String _longitude, String _updateTime, String _description, String _pic, String _attending_count){
		this.id = _id;
		this.title = _title;
		this.startTime = _sT;
		this.endTime = _eT;
		this.location = _loc;
		this.creator = _creator;
		this.latitude = _latitude;
		this.longitude = _longitude;
		this.description = _description;
		this.updateTime = _updateTime;
		this.pic = _pic;
		this.attending_count = _attending_count;
		
		
	}
	
	public FbEvent(){
	
	}
 
	public String getId(){
		return id;
	}
 
	public String getTitle(){
		return title;
	}
 
	public String getStartTime(){
		return startTime;
	}
 
	public String getEndTime(){
		return endTime;
	}
	
 
	public String getLocation(){
		return location;
	}
	
	public String getCreator(){
		return creator;
	}
	
	public String getLatitude(){
		return latitude;
	}
	
	public String getLongitude(){
		return longitude;
	}
	
	public String getDescription(){
		return description;
	}
	
	public String getUpdateTime(){
		return updateTime;
	}
	
	public String getPicUrl(){
		return pic;
	}
	
	public String getAttendingCount(){
		return attending_count;
	}
	
	public void setId(String id) {
		this.id = id;
		}
	
	public void setTitle(String title) {
		this.title = title;
		}
	
	public void setStartTime(String startTime){
		this.startTime = startTime;
	}
	public void setendTime(String endTime){
		this.endTime = endTime;
	}
	public void setLocation(String location){
		this.location = location;
	}
	
	public void setCreator(String creator){
		this.creator = creator;
	}
	
	public void setLatitude(String lat){
		this.latitude = lat;
	}
	
	public void setLongitude(String lon){
		this.longitude = lon;
	}
	
	public void setDescription(String desc){
		this.description = desc;
	}
	
	public void setUpdateTime(String time){
		this.updateTime = time;
	}
	
	public void setPicUrl(String pic){
		this.pic = pic;
	}
	
	public void setAttendingCount(String attending_count){
		this.attending_count = attending_count;
	}
	
	//added 30/10/2012
	//I am thinking of converting the dates from string to date in order to 
	//know about the events comparing to the current date
	
	private Date stringToDate(String dateString)
	{
		int pos = 0;
		int year = Integer.parseInt(dateString.substring(pos, pos = dateString.indexOf("-", pos)));
		int month = Integer.parseInt(dateString.substring(pos + 1, pos = dateString.indexOf("-", pos + 1))) - 1;
		int day = Integer.parseInt(dateString.substring(pos + 1, pos = dateString.indexOf("T", pos + 1)));
		int hours = Integer.parseInt(dateString.substring(pos + 1, pos = pos+3));
		int minutes = Integer.parseInt(dateString.substring(pos + 1, pos+3));

		hours = hours - 8;
		if(hours < 0) {
			hours = 24 + hours;
			day--;
		}
		
		return new Date(year - 1900, month, day, hours, minutes);
	}
	
}

