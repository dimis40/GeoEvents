package com.example.geoev;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Calendar;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class Event_details extends Activity{
	//
	private ProgressDialog dialogfetchdata;
	private NetworkInfo activeNetworkInfo;
	
	private Context context = this;
	private Button join;
	private Button delete;
	private Button directions;
	private Button calendar;
	private Button invite;
    private TextView event_title;
    Bitmap event_icon = null;
    private ImageView eventPic;
    private TextView numbOfattendees;
    private TextView attending_status;
    private TextView start;
    private TextView end;
    private TextView descr;
    private TextView loc;
   
    
    
	//
	private FbEvent event = new FbEvent();
	private Handler mHandler;
	private Global global;
	private UnixTimeConverter myUnix  = new UnixTimeConverter();
	
	public static final String TAG = "Event_details";
	public static final String NEW_MSG_DETAILS = "com.example.geoev.NEW_MSG_DETAILS";
	private GpsReceiver myReceiver;
	//i uses this boolean in order to get on click item correctly
	//in base of i have an adapter with freshed or cached data
	
	private String eid = "";
	private String pic = "";
	private String name = "";
	private String creator = "";
	private String start_time = "";
	private String end_time = "";
	private String location = "";
	private String latitude = "0";
	private String longitude = "0";
	private String event_latitude = "";
	private String event_longitude = "";
	private String description = "";
	private String attending_count = "";
	private String my_attending_status = "";
	private String event_lat_dot = "";
	private String event_lng_dot = "";
	
	
	
	public FeedItemAdapter mAdapter;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.event_dialog);
        isNetworkAvailable();
        //
        event_title = (TextView) Event_details.this.findViewById(R.id.event_title);
        join = (Button) Event_details.this.findViewById(R.id.join_event);
        delete = (Button) Event_details.this.findViewById(R.id.delete_event);
        invite = (Button) Event_details.this.findViewById(R.id.invite_friends);
        directions = (Button) Event_details.this.findViewById(R.id.get_directions);
        calendar = (Button) Event_details.this.findViewById(R.id.add_calendar);
        eventPic = (ImageView) Event_details.this.findViewById(R.id.marker_place);
        numbOfattendees = (TextView) Event_details.this.findViewById(R.id.attendees_number);
        attending_status = (TextView) Event_details.this.findViewById(R.id.attending_status);
        start = (TextView) Event_details.this.findViewById(R.id.event_start);
        end = (TextView) Event_details.this.findViewById(R.id.event_end);
        descr = (TextView) Event_details.this.findViewById(R.id.event_description);
        loc = (TextView) Event_details.this.findViewById(R.id.event_location);
        mHandler = new Handler();
        
        //
        
        
        
         //register receiver
        
        myReceiver = new GpsReceiver();
   	    IntentFilter filter = new IntentFilter(NEW_MSG_DETAILS);
   	    registerReceiver(myReceiver, filter);
        //
        Bundle extras = getIntent().getExtras();
        eid = extras.getString("eid");
        pic = extras.getString("pic");
        name = extras.getString("name");
        location = extras.getString("location");
        event_latitude = extras.getString("latitude");
        event_longitude = extras.getString("longitude");
        start_time = extras.getString("start_time");
        end_time = extras.getString("end_time");
        creator = extras.getString("creator");
        description = extras.getString("description");
        attending_count = extras.getString("attending_count");
        
        // convert latitude and longitude of event in dot 36.789 format
        double event_Lat = Double.parseDouble(event_latitude);
        double event_Latwithdot = event_Lat/1000000;
        event_lat_dot = String.valueOf(event_Latwithdot);
        double event_Lng = Double.parseDouble(event_longitude);
        double event_Lngwithdot = event_Lng/1000000;
        event_lng_dot = String.valueOf(event_Lngwithdot);
        
        global = Global.getInstance();
        
        //
        dialogfetchdata = new ProgressDialog(context);
        dialogfetchdata.setMessage("fetching data, please wait...");
        dialogfetchdata.show();
        
        
        //
        myUnix.hasEventFinished(myUnix.convertCurrentFromAndroidtoUnix(), end_time);
        
        if (creator.equals(global.getFbUid())){
        	join.setEnabled(false);
        }
        	else{
            delete.setEnabled(false);
        	}
        
        join.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on clicks
            	if (my_attending_status.equals("attending")){
            	new RsvpTask(getBaseContext()).execute("maybe");
            	
            	}else{
            		new RsvpTask(getBaseContext()).execute("attending");
            	}
            	}
        });
        
        delete.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on clicks
            	new EventDeleteTask(getBaseContext()).execute("");
            	
            	
            	}
        });
        
        invite.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on clicks
            	launchFriendList();
            	
            	
            	}
        });
       
        calendar.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on clicks
            	Calendar cal = Calendar.getInstance();              
                Intent intent = new Intent(Intent.ACTION_EDIT);
                intent.setType("vnd.android.cursor.item/event");
                //intent.putExtra("beginTime", cal.getTimeInMillis());
                intent.putExtra("beginTime", myUnix.unixFBStringToLong(start_time)*1000);
                intent.putExtra("allDay", false);
                //intent.putExtra("rrule", "FREQ=YEARLY");
                intent.putExtra("eventLocation", location);
                //intent.putExtra("endTime", cal.getTimeInMillis()+60*60*1000);
                //intent.putExtra("endTime", myUnix.unixFBStringToLong(end_time));
                intent.putExtra("endTime", myUnix.unixFBStringToLong(end_time)*1000);
                intent.putExtra("title", name);
                startActivity(intent);
            	
            	
            	}
        });
        
        directions.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	 //if can't get location or there is no network
                if ((latitude.equals("0") && (longitude.equals("0")) || activeNetworkInfo == null)){
                	//toast
                	Toast.makeText(getBaseContext(), 
        	                "You cannot get Directions at the moment. Check your GPS or Network Connection", 
        	                 Toast.LENGTH_SHORT).show();
                }
                else {
            	Intent intent = new Intent(android.content.Intent.ACTION_VIEW, 
            	//Uri.parse("http://maps.google.com/maps?saddr=39.375833,22.963486&daddr=39.370227,22.982326"));
            			Uri.parse("http://maps.google.com/maps?saddr=" + latitude + "," + longitude + "&daddr=" + event_lat_dot + "," + event_lng_dot));
            			startActivity(intent);
            	
            	
            	}
            }
        });
       
        //
 // if (FBService.gotFriends == false || singleton.lock.isLocked()){
        	//get fresh data
        	//Get Events Information using FQL
           
      //Get Events Information using FQL
        //String query_allevents = "Select eid, name, start_time, end_time, location, creator, attending_count, description, host, pic, update_time, venue from event where eid =" + eid;
        String query_event_table = "Select eid, inviter, rsvp_status, uid from event_member where eid =" + eid + " AND uid = me()";
        Bundle params_event = new Bundle();
         params_event.putString("method", "fql.query");
         params_event.putString("query", query_event_table);
         Utility.mAsyncRunner.request(null, params_event,
                 new EventFQLRequestListener());
     
    
  
   
    
    
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
                    String inviter = outer.getString("inviter");
                    my_attending_status = outer.getString("rsvp_status");
                    String uid = outer.getString("uid");
                
                    URL img_value = null;
                   
                   try {
   				//img_value = new URL(image);
                 img_value = new URL(pic);	
                	   
                	   
   					// userpicture.setImageBitmap(mIcon1);
   				} catch (MalformedURLException e) {
   					// TODO Auto-generated catch block
   					e.printStackTrace();
   				}
                 
                   try {
   					event_icon = BitmapFactory.decodeStream(img_value.openConnection().getInputStream());
   				} catch (IOException e) {
   					// TODO Auto-generated catch block
   					e.printStackTrace();
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
					event_title.setText(name);
					numbOfattendees.setText("No of attending: " + attending_count);
					eventPic.setImageBitmap(event_icon);
					attending_status.setText("Attending Status: " + my_attending_status);
					start.setText("Start Date: " + myUnix.convertFromUnixToHuman(start_time));
					end.setText("End Date: " + myUnix.convertFromUnixToHuman(end_time));
					descr.setText("Description: " + description);
					loc.setText("Location: " + location);
					
					if (my_attending_status.equals("attending")){
						join.setText("Unfollow");
					}
					
					//
					dialogfetchdata.dismiss();
				}
				});
				
				
				
			} catch (JSONException e) {
				Log.w(TAG, "JSON Error in response");
				dialogfetchdata.dismiss();
			}
		}
    }
	
	public String getAccessToken() {
		return Utility.mFacebook.getAccessToken();
	}

	 /*public void setRsvpStatus (String msg) {
	        Log.d("Tests", "Testing graph API wall post");
	         try {
	        	   // String response;
	        	    HttpClient httpclient = new DefaultHttpClient();  
	                HttpPost httppost = new HttpPost("https://graph.facebook.com/" + eid + "/" + msg +"/?access_token post=" + getAccessToken());  
	                HttpResponse response = httpclient.execute(httppost);  
	                if (response == null || response.equals("") || 
	                        response.equals("false")) {
	                   Log.v("Error", "Blank response");
	                }
	         } catch(Exception e) {
	             e.printStackTrace();
	         }
	    }*/
	
	//
	 
	 class RsvpTask extends AsyncTask<String, String, String>{
		 Context mContext;
		 
		 public RsvpTask(Context context){
				super();
				mContext = context;
		       
			}
		   
		 ProgressDialog updateRsvpStatus;
		    
		    protected void onPreExecute() {
		    	 updateRsvpStatus = new ProgressDialog(Event_details.this);
		    	 updateRsvpStatus.setMessage("Updating your rsvp status, please wait...");
		         updateRsvpStatus.show();
		    }
		    
		    
		    @Override
		    protected String doInBackground(String... rsvp) {
		        HttpClient httpclient = new DefaultHttpClient();
		        //HttpResponse response;
		        //HttpPost httppost;
		        String responseString = null;
		        String response = null;
		        Log.v(TAG, "BOYYYUUKA!!!!!!!!!!!!!!!!!");
		        
		        try {
		        	 Bundle parameters = new Bundle();
		             parameters.putString("", "");
		             response = Utility.mFacebook.request(eid + "/" + rsvp[0], parameters, 
		                        "POST");
		                Log.d("Tests", "got response: " + response);
		                if (response == null || response.equals("") || 
		                        response.equals("false")) {
		                   Log.v("Error", "Blank response");
		                }
		        	/*HttpPost httppost = new HttpPost("https://graph.facebook.com/" + eid + "/attending/?access_token=" + getAccessToken());
		        	//response = httpclient.execute(new HttpPost(uri[0]));
		            HttpResponse response = httpclient.execute(httppost); 
		            HttpEntity   entity   = response.getEntity();
		            Log.v(TAG, "MAGIAKOFSKI!!!!!!!!!!!!!!!!!");

		            System.out.print("----------------------------------------");
		            System.out.print( response.getStatusLine() );
		            System.out.print("----------------------------------------");

		            if( entity != null ) entity.writeTo( System.out );
		            if( entity != null ) entity.consumeContent();

		            responseString = response.toString();
		            Log.v(TAG, "MAGIAKOFSKI!!!!!!!!!!!!!!!!!" + responseString);
	                if (response == null || response.equals("") || 
	                        response.equals("false")) {
	                   Log.v("Error", "Blank response");
	                }*/
	         } catch(Exception e) {
	             e.printStackTrace();
	         
		        }
		       // return responseString;
		        return response;
		    }

		    @Override
		    protected void onPostExecute(String response) {
		        super.onPostExecute(response);
		        updateRsvpStatus.dismiss();
		        Toast.makeText(getBaseContext(), 
		                "Your rsvp_status has been changed" + response , 
		                 Toast.LENGTH_SHORT).show();
		        Intent myNewIntent = new Intent(getApplicationContext(), Geoevent.class);
		        myNewIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		        startActivity(myNewIntent);
		    }
		}
	 
	 
	 class EventDeleteTask extends AsyncTask<String, String, String>{
		 Context mContext;
		 public EventDeleteTask(Context context){
				super();
				mContext = context;
				
			}
		 
		 ProgressDialog deletingEvent;
		    
		    protected void onPreExecute() {
		    	 deletingEvent = new ProgressDialog(Event_details.this);
		    	 deletingEvent.setMessage("Deleting event," + name + " please wait...");
		    	 deletingEvent.show();
		    }
		 
		    @Override
		    protected String doInBackground(String... rsvp) {
		        HttpClient httpclient = new DefaultHttpClient();
		        //HttpResponse response;
		        //HttpPost httppost;
		        String responseString = null;
		        String response = null;
		        Log.v(TAG, "BOYYYUUKA!!!!!!!!!!!!!!!!!");
		        try {
		        	 Bundle parameters = new Bundle();
		             parameters.putString("", "");
		             response = Utility.mFacebook.request(eid + "/" + rsvp[0], parameters, 
		                        "DELETE");
		                Log.d("Tests", "got response: " + response);
		                if (response == null || response.equals("") || 
		                        response.equals("false")) {
		                   Log.v("Error", "Blank response");
		                }
		        	/*HttpPost httppost = new HttpPost("https://graph.facebook.com/" + eid + "/attending/?access_token=" + getAccessToken());
		        	//response = httpclient.execute(new HttpPost(uri[0]));
		            HttpResponse response = httpclient.execute(httppost); 
		            HttpEntity   entity   = response.getEntity();
		            Log.v(TAG, "MAGIAKOFSKI!!!!!!!!!!!!!!!!!");

		            System.out.print("----------------------------------------");
		            System.out.print( response.getStatusLine() );
		            System.out.print("----------------------------------------");

		            if( entity != null ) entity.writeTo( System.out );
		            if( entity != null ) entity.consumeContent();

		            responseString = response.toString();
		            Log.v(TAG, "MAGIAKOFSKI!!!!!!!!!!!!!!!!!" + responseString);
	                if (response == null || response.equals("") || 
	                        response.equals("false")) {
	                   Log.v("Error", "Blank response");
	                }*/
	         } catch(Exception e) {
	             e.printStackTrace();
	         
		        }
		       // return responseString;
		        return response;
		    }

		    @Override
		    protected void onPostExecute(String response) {
		        super.onPostExecute(response);
		        deletingEvent.dismiss();
		        Toast.makeText(getBaseContext(), 
		                "The event :" + name + "has been deleted" + response , 
		                 Toast.LENGTH_SHORT).show();
		        Intent myNewIntent = new Intent(getApplicationContext(), Geoevent.class);
		        startActivity(myNewIntent);
		    }
		}
	//
	 private class GpsReceiver extends BroadcastReceiver {
			
		  @Override
			public void onReceive(Context context, Intent intent) {
				showDataFromIntent(intent);
			}

			private void showDataFromIntent(Intent intent) {
				//shows up in the log file
				    latitude = String.valueOf(intent.getDoubleExtra(
						"Latitude", 0));
					longitude = String.valueOf(intent.getDoubleExtra(
							"Longtitude", 0));
				 
				 Log.v("INFO","@##$$%^%&^&^**&&((&(&:&*(()!!!!!!!! ");
				 
			}
		}  
	 private boolean isNetworkAvailable() {
		    ConnectivityManager connectivityManager 
		          = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		    activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
		    return activeNetworkInfo != null;
		}
	 
	 public void launchFriendList(){
			if(!Utility.mFacebook.isSessionValid()){
				//showError(getString(R.string.session_no_valid_text));
				return;
			}

			Intent i = new Intent(Event_details.this, FriendList.class);
			i.putExtra(Constants.bundle_Access_Token, Utility.mFacebook.getAccessToken());
			i.putExtra(Constants.bundle_Access_Expires, Utility.mFacebook.getAccessExpires());
			i.putExtra("eid", eid);
			startActivity(i);
		}
}
