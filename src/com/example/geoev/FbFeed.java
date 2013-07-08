package com.example.geoev;

public class FbFeed {
	private String ev_id;
	private String message;
	private String user_id;
	private String created_at;
	private String user_name;
	private String post_id;
 
	public FbFeed(String _ev_id, String _message, String _post_id, String _created_at, String _user_id, String _user_name){
		this.ev_id = _ev_id;
		this.message = _message;
		this.user_id = _user_id;
		this.created_at = _created_at;
		this.post_id = _post_id;
		this.user_name = _user_name;
	}
	
	public FbFeed(){
	
	}
 
	public String getEvId(){
		return ev_id;
	}
	
	public String getMessage(){
		return message;
	}
 
	public String getPostId(){
		return post_id;
	}
 
	public String getCreatedTime(){
		return created_at;
	}
 
	public String getUserId(){
		return user_id;
	}
 
	public String getUserName(){
		return user_name;
	}
	
	public void setEvId(String ev_id) {
		this.ev_id = ev_id;
		}
	
	public void setMessage(String message) {
		this.message = message;
		}
	
	public void setPostId(String post_id){
		this.post_id = post_id;
	}
	
	public void setCreatedAt(String created_at){
		this.created_at = created_at;
	}
	
	public void setUserId(String user_id){
		this.user_id = user_id;
	}
	
	public void setUserName(String user_name){
		this.user_name = user_name;
	}
	
	
	
}

