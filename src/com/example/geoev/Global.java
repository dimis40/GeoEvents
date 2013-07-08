package com.example.geoev;

public class Global {
private boolean fBPostServiceRunning = false;
private static Global instance;
private String monitoring_eid = "";
private String fbUid = "";
private boolean first = true;

private Global(){
	
}

//singleton implementation
public synchronized static Global getInstance(){
	//for singleton implementation
	if (instance == null){
		instance = new Global();
	}
	return instance;
	
}

public boolean getFBPostServiceRunning(){
	return fBPostServiceRunning;
}

public boolean getFirstTimeExecution(){
	return first;
}

public void setFirstTimeExecution(boolean first){
	this.first = first;
}

public void setFBPostServiceRunning(boolean fBPostServiceRunning){
	this.fBPostServiceRunning = fBPostServiceRunning;
}

public String getMonitoring_eid(){
	return monitoring_eid;
}

public void setMonitoring_eid(String monintoring_eid){
	this.monitoring_eid = monintoring_eid;
}

public String getFbUid(){
	return fbUid;
}

public void setFbUid(String fbUid){
this.fbUid = fbUid;
}

}
