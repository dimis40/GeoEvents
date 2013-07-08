package com.example.geoev;

public class FbEventAttendingStatus {
	private String eid;
	private String uid;
	private String rsvp_status;
	
 
	//provlima me tous constructor
	//ean den iparxei p.x location i end time yparxei provlima
	
	
	public FbEventAttendingStatus(String _uid, String _eid, String _rsvp_status){
		this.eid = _eid;
		this.uid = _uid;
		this.rsvp_status = _rsvp_status;
	}
	
	
	
	public FbEventAttendingStatus(){
	
	}
 
	public String getUid(){
		return uid;
	}
 
	public String getEid(){
		return eid;
	}
	
	public String getRsvp_status(){
		return rsvp_status;
	}
 
	public void setUid(String uid) {
		this.uid = uid;
		}
	
	public void setEid(String eid) {
		this.eid = eid;
		}
	public void setRsvp_status(String rsvp_status) {
		this.rsvp_status = rsvp_status;
		}
}

