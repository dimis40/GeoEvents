package com.example.geoev;

public class GpsData {
	private Integer id;
	private String eid;
	private String event_title;
	private String latitude;
	private String longitude;
	private String speed;
	private String altitude;
	private String time;
	
	//Constructor
	public GpsData(Integer id, String eid, String event_title, String latitude, String longitude, String speed, 
		String altitude, String time) {
		this.id = id;
		this.eid = eid;
		this.event_title = event_title;
		this.latitude = latitude;
		this.longitude = longitude;
		this.speed = speed;
		this.altitude = altitude;
		this.time = time;
	}
	
	
	//Standard Constructors
	public GpsData() {}
	
	//getters and setters
	
	public Integer getId() { 
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
		}
	
	public String getEid() {
		return eid;
		}
		
	public void setEid(String eid) {
		this.eid = eid;
		}
	
	public String getTitle() {
		return event_title;
		}
		
	public void setTitle(String name) {
		this.event_title = name;
		}
	
	public String getLatitude() {
		return latitude;
		}
	public void setLatitude(String lat){
		this.latitude = lat;
	}
	
	public String getLongitude() {
		return longitude;
		}
	
	public void setLongitude(String lon){
		this.longitude = lon;
	}
	
	public String getSpeed() {
		return speed;
		}
	
	public void setSpeed(String speed){
		this.speed = speed;
	}
	
	public String getAltitude() {
		return altitude;
		}
	
	public void setAltitude(String alt){
		this.altitude = alt;
	}
	
	public String getTime() {
		return time;
		}
	
	public void setTime(String time){
		this.time = time;
	}
}

