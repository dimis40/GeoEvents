package com.example.geoev;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;
import android.widget.Toast;

public class AllEvents extends Activity{
	//
	private ProgressDialog dialogfetchdata;
	private Context context = this;
	//
	 private DatabaseHelper singleton;
	 private List<FbEvent> events = new ArrayList<FbEvent>();
	 //final ListAdapter listAdapter = getListAdapter();
	
	 private Handler mHandler;
	 private Global global;
	 
	public static final String TAG = "AllEvents";
	//i uses this boolean in order to get on click item correctly
	//in base of i have an adapter with freshed or cached data
	public Boolean hasCashedData = false;
	
	public EntryItemAdapter mAdapter;
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mHandler = new Handler();
        global = Global.getInstance();
        setContentView(R.layout.events);
        //added 25 November 2012
        //odigies gia long click kai normal click se toast
        Toast.makeText(getApplicationContext(), "Press and hold to edit or start Monitoring the Event or click to see event's Info", Toast.LENGTH_LONG).show();
        //
        singleton = DatabaseHelper.getInstance(getApplicationContext());
        //
        dialogfetchdata = new ProgressDialog(context);
        dialogfetchdata.setMessage("fetching data, please wait...");
        dialogfetchdata.show();
        //
    //if (FBService.gotFriends == false || singleton.lock.isLocked()){
        	//get fresh data
        	//Get Events Information using FQL
           String query_allevents = "Select eid, name, start_time, end_time, location, creator, description, host, update_time, venue from event where eid IN (select eid from event_member where uid = me() AND rsvp_status = \"attending\") AND start_time >= now()";
            Bundle params_event = new Bundle();
            params_event.putString("method", "fql.query");
            params_event.putString("query", query_allevents);
            Utility.mAsyncRunner.request(null, params_event,
                    new EventFQLRequestListener());
        
        
        
       mAdapter = new EntryItemAdapter(this,
	   R.layout.event_item, events);
     
                    
	/*}
     else {
        	mAdapter = new EntryItemAdapter(this,
        			R.layout.event_item, singleton.getAllEvents());
        	hasCashedData = true;
      }*/
       
        
        ListView eventsListView = (ListView) findViewById(R.id.events_list);
       eventsListView.setAdapter(mAdapter);
        
        eventsListView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {
			
				//launchEventDetailsActivity((Event) parent
				FbEvent event = new FbEvent();
		        event = (FbEvent) parent.getItemAtPosition(position);
				Intent myNewIntent = new Intent(getApplicationContext(), EventInfo.class);
				myNewIntent.putExtra("eid", event.getId());
				startActivity(myNewIntent);
				/*AlertDialog.Builder adb = new AlertDialog.Builder(
						AllEvents.this);
				        FbEvent event = new FbEvent();
				        event = (FbEvent) parent.getItemAtPosition(position);
						adb.setTitle("ListView OnClick");
						adb.setMessage("Selected Item is = "
						//+ parent.getItemAtPosition(position));
					  + event.getId());
					  //+ parent.getItemIdAtPosition(position));
						adb.setPositiveButton("Ok", null);
						adb.setPositiveButton("Ok", null);
						adb.show();     */
				
						//parent.getItemAtPosition(position);
			}
		});
    
	//added 25 November 2012
        eventsListView.setOnItemLongClickListener(new OnItemLongClickListener() {
        	 public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                 Log.e("MyApp", "get onItem Click position= " + position);
                 Toast.makeText(getApplicationContext(), "Long Click pressed", Toast.LENGTH_LONG).show();
                 //get event id
                 FbEvent event = new FbEvent();
 		         event = (FbEvent) parent.getItemAtPosition(position);
                 //
 		        // convert latitude and longitude of event in dot 36.789 format
 		        //test
                 double Lat = Double.parseDouble(event.getLatitude());
                 double Latwithdot = Lat/1000000;
                 String lat_dot = String.valueOf(Latwithdot);
                 double Lng = Double.parseDouble(event.getLongitude());
                 double Lngwithdot = Lng/1000000;
                 String lng_dot = String.valueOf(Lngwithdot);
                 //added 7/12/2012 i am not sure if i have to check if service is running
                 if (global.getFBPostServiceRunning() == true){
                 stopService(new Intent(FBPost.class.getName()));
                 }
                 //start new service and passing eid
                 Intent myIntent = new Intent(getApplicationContext(), FBPost.class);
                 myIntent.putExtra("eid", event.getId());
                 myIntent.putExtra("ev_lat", lat_dot);
                 myIntent.putExtra("ev_lng", lng_dot);
                 myIntent.putExtra("event_title", event.getTitle());
                 myIntent.putExtra("event_end_time", event.getEndTime());
                 startService(myIntent);
                 
                 //tha to sviso argotera i tha to tropoiiso
                 AlertDialog.Builder adb = new AlertDialog.Builder(
 						AllEvents.this);
                  
 				        //FbEvent event = new FbEvent();
 				       // event = (FbEvent) parent.getItemAtPosition(position);
 						adb.setTitle("Monitor an GeoeEvent");
 						adb.setMessage("Now you inform the other attendees of GeoEvent: "
 						//+ parent.getItemAtPosition(position));
 					  + event.getTitle() + " about your current location. This information will be updated in 2 minutes interval");
 					  //+ parent.getItemIdAtPosition(position));
 						adb.setPositiveButton("Ok", null);
 						
 						//adb.setPositiveButton("Ok", null);
 						adb.show(); 
                 
                 return true;
             }
         });
	
	
	
	}
	
	
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		//if (dataUpdateReceiver != null) unregisterReceiver(dataUpdateReceiver);
	}


	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		
	}

	
	//
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
                    String address = "";
                    String location = "";
                    String locality = "";
                    String country = "";
                    String myLocation = "";
                    String lastcoordinates = "";
                    //split location parameters also in AllEvents and FBEvents
                    //I need to fix the parsing to my needs only for testing now S.O.S...
                    //first split of string
                    
                    //the case is if parsing is succesfull then we will
                    //insert these values, otherwise null
                   if (locationStr != null && description.startsWith("GeoEvent") && start_time!=null && end_time!=null){
                   String aLocParams[] = locationStr.split("Address:");
                    	if (aLocParams.length == 2){
                   myLocation = aLocParams[1];
                   String localityParams[] = aLocParams[1].split(", Locality:");
           		   if (localityParams.length == 2){
           		   location = localityParams[0];
           		   String countryParams[] = localityParams[1].split(", Country:");
           		   if (countryParams.length == 2){
            	   locality = countryParams[0];
            	   String coordinatesParams[] = countryParams[1].split(", Coordinates:");
            	   if (coordinatesParams.length == 2){
                	country = coordinatesParams[0];
                	String lastcoordinatesParams[] = coordinatesParams[1].split(";");
             	   if (lastcoordinatesParams.length == 1){
                 	lastcoordinates = lastcoordinatesParams[0];
                 	Log.d(TAG, "NAIMALAKOPITOURAS");
                	String latlong[] = lastcoordinates.split(",");
                    		if (latlong.length == 2){
                    			 Log.d(TAG, "MALAKOPITOURAS" + latlong[0]);
                    	        //check if lat & lon are numbers regex
                    			if (latlong[0].matches("((-|\\+)?[0-9]+(\\.[0-9]+)?)+") && latlong[1].matches("((-|\\+)?[0-9]+(\\.[0-9]+)?)+")) {
                    				System.out.println("Is a number");
                    				lat = latlong[0];
                                	lon = latlong[1];
                                	//
                                	// convert latitude and longitude of event in dot 36.789 format
                     		        //test
                                     double Lat = Double.parseDouble(lat);
                                     double Latwithdot = Lat/1000000;
                                    // String lat_dot = String.valueOf(Latwithdot);
                                     double Lng = Double.parseDouble(lon);
                                     double Lngwithdot = Lng/1000000;
                                     //String lng_dot = String.valueOf(Lngwithdot);
                                	//check if latitude & longitude are valids
                                    //Latitude measures how far north or south of the equator 
                                     //a place is located. The equator is situated at 0¡, the North Pole 
                                     //at 90¡ north (or 90¡, because a positive latitude implies north), and the 
                                     //South Pole at 90¡ south (or Ð90¡). Latitude measurements range from 0¡ to (+/Ð)90¡.
                                      //Longitude measures how far east or west of the prime meridian a place is located. 
                                     //The prime meridian runs through Greenwich, England. Longitude measurements range from 0¡ to (+/Ð)180¡.
                                     if ((Latwithdot > -90 && Latwithdot < 90) && (Lngwithdot > -180 && Lngwithdot < 180) ){
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
            	   }
           		   }
           		        }
                     }
                    }
                    //second split of string
                    //String temp = aLocParams[1];
                    //third split of string
                    //String LatLong[] = temp.split(" ");
                    //fourth split
                    //String latlong[] = LatLong[2].split(";");
                   // String lat = latlong[0];
                   // String lon = latlong[1];
                   // FbEvent newEvent = new FbEvent(eid, name, start_time, end_time, location, creator, lat, lon, update_time, description);
                   // FbEventAttendee newFbEventAttendee = new FbEventAttendee(eid, uid);
                   //Log.d(TAG, "attending:..............temMmMmmMPLLAAT" + LatLong[2] + "......//....the event...eid : " + eid + " the user with the user_id ");
                  
                    
                    // FbEventAttendees.add(newFbEventAttendee);
                    //events.add(newEvent);
                    //Log.d(TAG, "Event Name: " + newEvent.getTitle());
				}
				
				//singleton.insertAllEvents(events);
               // events.clear();
				
				//very important etsi ananeono to periexomeno tou adapter
				mHandler.post(new Runnable() {
				//AllEvents.this.runOnUiThread(new Runnable() {
					public void run() {
					if (dialogfetchdata != null){
						mAdapter.notifyDataSetChanged();
						dialogfetchdata.dismiss();
						}
				}
				});
				
				
				
			} catch (JSONException e) {
				Log.w(TAG, "JSON Error in response");
				dialogfetchdata.dismiss();
			}
		}
    }



	
}
