package com.example.geoev;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.util.Log;


public final class FBEvents {
	
	private ArrayList<FbEvent> events = new ArrayList<FbEvent>();
	public static final String TAG = "FBFEvents";
	
	//ArrayList <Person> getClickedPersons = new ArrayList <Person>();
	
	//Constructor
	public FBEvents() {
		//Get Events Information using FQL
        String query_allevents = "Select eid, name, start_time, end_time, location, creator from event where eid IN (select eid from event_member where uid = me() AND rsvp_status = \"attending\")";
        Bundle params_event = new Bundle();
        params_event.putString("method", "fql.query");
        params_event.putString("query", query_allevents);
        Utility.mAsyncRunner.request(null, params_event,
                new EventFQLRequestListener());
		Log.v("INFO", "REQUEST Events");
		
	}
	
	/*
     * Callback for fetching user's facebook events
     */
    public class EventFQLRequestListener extends BaseRequestListener {
			public void onComplete(String response, Object state) {
				try {
					// process the response here: executed in background thread
					Log.d(TAG, "Response from Event: " + response.toString());
					//final JSONObject json = new JSONObject(response);
					//JSONArray d = json.getJSONArray("data");
					JSONArray d = new JSONArray(response);
					for (int i = 0; i < d.length(); i++) {
	                    JSONObject outer = d.getJSONObject(i);
	                    String eid = outer.getString("eid");
	                    String name = outer.getString("name");
	                    String start_time = outer.getString("start_time");
	                    String end_time = outer.getString("end_time");
	                    String locationStr = outer.getString("location");
	                    String creator = outer.getString("creator");
	                    String update_time = outer.getString("update_time");
	                    String description = outer.getString("description");
	                  //initiate strings that are associated with parsing
	                    String lat = "";
	                    String lon = "";
	                    String location = "";
	                    //split location parameters also in AllEvents and FBEvents
	                    //I need to fix the parsing to my needs only for testing now S.O.S...
	                    //first split of string
	                    
	                    //the case is if parsing is succesfull then we will
	                    //insert these values, otherwise null
	                   if (locationStr != null && description.startsWith("GeoEvent")){
	                   String aLocParams[] = locationStr.split(",");
	                    	if (aLocParams.length == 4){
	                    location = aLocParams[0];
	                   String latlong[] = aLocParams[3].split(";");
	                    		if (latlong.length==2){
	                    	        //check if lat & lon are numbers regex
	                    			if (latlong[0].matches("((-|\\+)?[0-9]+(\\.[0-9]+)?)+") && latlong[1].matches("((-|\\+)?[0-9]+(\\.[0-9]+)?)+")) {
	                    				System.out.println("Is a number");
	                    				lat = latlong[0];
	                                	lon = latlong[1];
	                                	FbEvent newEvent = new FbEvent(eid, name, start_time, end_time, location, creator, lat, lon, update_time, description);
	                                	events.add(newEvent);
	             	                    Log.d(TAG, "Event Name: " + newEvent.getTitle());
	                    			} else {
	                    				System.out.println("Is not a number");
	                    				//do nothing
	                    			}
	                    		}
	                    	}
	                    }
	                    
	                    //FbEvent newEvent = new FbEvent(eid, name, start_time, end_time, location, creator, lat, lon, update_time, description);
	                   // FbEventAttendee newFbEventAttendee = new FbEventAttendee(eid, uid);
	                  //Log.d(TAG, "attending:....................//....the event...eid : " + eid + " the user with the user_id " + uid);
					 // FbEventAttendees.add(newFbEventAttendee);
	                   
					}
				
			} catch (JSONException e) {
				Log.w(TAG, "JSON Error in response");
			}
		}

    }
	
		public ArrayList <FbEvent> getPEvents(){
		return events;
		}
		
		

    
}
