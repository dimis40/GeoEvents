package com.example.geoev;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Date;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

public class FBPost extends Service{
    
	
	private static final String TAG = FBPost.class.getSimpleName();	
	private boolean ok = false;
	private Timer timer;
	//GPSTracker gps;
	//GPSData gpsdata;
	private String latitude = "0";
	private String longitude = "0";
	private String speed = "undefined";
	private String altitude = "undefined";
	private String time = "";
	//GPSService gpsservice;
	//LocationService locationService;
	public static final String NEW_MSG = "com.example.geov.NEW_MSG";
	private GpsReceiver myReceiver;
	private String duration = "undefined";
	private String gDistance = "undefined";
	private String currentDateTimeString;
	//
	private String eid = "";
	private String event_title = "";
	private String distance = "";
	//distance to event in meters
	private Double distanceToEvent = 40.00;
	private String eventLat = "";
	private String eventLng = "";
	private String event_end_time = "";
	private String start_address = "undefined";
	private Handler mHandler;
	ProgressDialog dialog;
	//singleton object
	private Global global;
	private UnixTimeConverter myUnix = new UnixTimeConverter();
	private long unix_time;
	
	
	
	private NetworkInfo activeNetworkInfo;
	
	private DatabaseHelper singleton;
	//
	private static final int NOTIFICATION_ID = 1000;

	
	private TimerTask updateTask = new TimerTask() {
		 
		@Override
	    public void run() {
	      Log.i(TAG, "Timer task doing work");
	      
	      // I am trying to avoid unnecessary internt calls
	      isNetworkAvailable();
	     /* if(gps.canGetLocation()){ // gps enabled} // return boolean true/false
	    	  gps.getLatitude(); // returns latitude
	    	  gps.getLongitude(); // returns longitude 
	    	 // Toast.makeText(getApplicationContext(), "Your Location is - \nLat: " + latitude + "\nLong: " + longitude, Toast.LENGTH_LONG).show();
	     }*/
	      
	      //locationService.getMyLocation();
	      
	      //latitude = String.valueOf(gps.getLatitude());
	      //longitude = String.valueOf(gps.getLongitude());
	      //Check network
	      
	      //for testing tha to sviso ektos an thelo na emfanizetai
	      Date date = new Date(System.currentTimeMillis());
	      SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
		  currentDateTimeString =  sdf.format(date);
	      
	      
	     // currentDateTimeString = myUnix.displayCurrentTime();
	      Log.v(TAG, longitude);
	      //post on the wall in 1 minute interval although 
	      //facebook does not support resending the same message status
	       //if there is no connection inform the user
    	  //avoiding unnenecary internet calls
	      //if ((latitude.equals("0")) && (longitude.equals("0"))){
	      if ((latitude.equals("0") && (longitude.equals("0")) || activeNetworkInfo== null)){
	    	  Log.v(TAG, "RTTTTTJJJ!!!!!!!!!!!!!!!!!!!!!!### DO NOTHING !!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
	    	  //do nothing
	      }
	      else{
	    	  
	    	 if (myUnix.hasEventFinished(myUnix.convertCurrentFromAndroidtoUnix(), event_end_time) == true){
		    	  //
		    	  Log.v(TAG, "HAVE BEEN LATE FOR THE EVENT");
		    	  Context context = getApplicationContext();
		    	  NotificationManager notificationManager = 
		    	            (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		    	        
		    	        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, null, 0);        
		    	        
		    	        Notification notification = createNotification();
		    	        notification.setLatestEventInfo(context, 
		    	            "Time Alert!", "Sorry the event has expired.", pendingIntent);
		    	        
		    	        notificationManager.notify(NOTIFICATION_ID, notification);
		    	  
		    	  
		    	  stopService(new Intent(FBPost.class.getName()));
	    	  
	      }
	      else{
	      distance =String.valueOf(distFrom(latitude, longitude, eventLat, eventLng)); 
	      distFrom(latitude, longitude, eventLat, eventLng);
		  getDuration(latitude, longitude, eventLat, eventLng);
	      //postOnWall("Hello" + latitude +" "+ longitude +" " + altitude + " " + speed + " " + "time : " + currentDateTimeString + " epoch_time : " + myUnix.convertCurrentFromAndroidtoUnix() + " duration: " + duration + " distance from Event: " + distance + " meters" + " Google Distance: " + gDistance);
		  postOnWall("Now I am at :" + start_address + ". My distance from GeoEvent is " + gDistance + " .I would be there in " + duration);
		  //postOnOpenGraph(latitude, longitude);
	      Log.v(TAG, "https://simple-night-1868.herokuapp.com/geolocation.php?fb%3Aapp_id=338618139553202&og%3Atype=geoeventing%3Ageolocation&og%3Atitle=Elia&og%3Aimage=+https%3A%2F%2Fsstatic.ak.fbcdn.net%2Fimages%2Fdevsite%2Fattachment_blank.png&geoeventing%3Aroute%3Alatitude=" + latitude + "&geoeventing%3Aroute%3Alongitude=" + longitude +"&body=GeoEvent");
	     // }
	      }
	     }
		}  
	    
	  };

	  @Override
	  public IBinder onBind(Intent intent) {
	    // TODO Auto-generated method stub
	    return null;
	  }
	 
	  @Override
	  public void onCreate() {
	    super.onCreate();
	    Log.i(TAG, "Service creating");
	    singleton = DatabaseHelper.getInstance(getApplicationContext());
	    global = Global.getInstance();
	    
	    //Added 25 Novenber 2012
	    
	    
	    
	    myReceiver = new GpsReceiver();
		IntentFilter filter = new IntentFilter(NEW_MSG);
		registerReceiver(myReceiver, filter);
		//startService(new Intent(FBPost.this, GpsBroadcast.class));
	    
		
        
	    //gps = new GPSTracker(this);
	    //gpsservice = new GPSService();
	    // gpsdata = gpsservice.new GPSData();
		
	    timer = new Timer("FacebookPostTimer");
	    timer.schedule(updateTask, 1000L, 60 * 1000L);
	    
	    
	    //locationService = new LocationService(this);
	  }
	  
	  
	  @Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		  //
		  mHandler = new Handler();
		 
		  global.setFBPostServiceRunning(true);
		    
		  //get event id
		    Bundle extras = intent.getExtras();
	        eid = extras.getString("eid");
	        eventLat = extras.getString("ev_lat");
	        eventLng = extras.getString("ev_lng");
	        event_title = extras.getString("event_title");
	        event_end_time = extras.getString("event_end_time");
	        
	        singleton.open();
	        global.setMonitoring_eid(eid);
	        
		return super.onStartCommand(intent, flags, startId);
	}
	  
	  

	@Override
	  public void onDestroy() {
	    super.onDestroy();
	    Log.i(TAG, "Service destroying");
	     
	    timer.cancel();
	    timer = null;
	    
	    global.setFBPostServiceRunning(false);
	    
	    //added 7/12/2012
	    //start intent service Opengraph
	    Intent myIntent = new Intent(getApplicationContext(), Opengraph.class);
        myIntent.putExtra("eid", eid);
        myIntent.putExtra("ev_lat", eventLat);
        myIntent.putExtra("ev_lng", eventLng);
        //myIntent.putExtra("event_title", event_title);
        startService(myIntent);
	    
	  }
	
	  ///Very Important !!!
	 ///by removing any declaration  like .remote android: in Manifest
	 // we can use the same Utility.mfacebook object
	  public void postOnWall(String msg) {
	        Log.d("Tests", "Testing graph API wall post");
	         try {
	        	    String response;
	               // String response = Utility.mFacebook.request("me");
	                Bundle parameters = new Bundle();
	                parameters.putString("message", msg);
	                
	                parameters.putString("name", "Posted by mobile");
	                parameters.putString("link", "http://www.volos-city.gr");
	                parameters.putString("caption", "GeoEvent");
	                parameters.putString("description", "test test test");
	                parameters.putString("picture", "http://www.youngprepro.com/wp-content/uploads/2011/08/read-a-lot-books-productivity-tips-for-writers.jpg");
	                parameters.putString("link", "http://www.google.com");
	                //parameters.putString("picture", "http://www.youngprepro.com/wp-content/uploads/2011/08/read-a-lot-books-productivity-tips-for-writers.jpg");
	                //response = Utility.mFacebook.request("me/feed", parameters,
	                response = Utility.mFacebook.request(eid + "/feed", parameters, 
	                        "POST");
	                Log.d("Tests RAFAELA !!!", "got response: " + response);
	                if (response == null || response.equals("") || 
	                        response.equals("false")) {
	                   Log.v("Error", "Blank response");
	                }
	         } catch(Exception e) {
	             e.printStackTrace();
	         }
	    }
	  
	  
	  //post on open graph
	  public void postOnOpenGraph(String startLat, String startLng) {
		  Log.d("Tests", "Testing Open graph API post");
		  try {
		  Bundle graphParams = new Bundle();
		 //graphParams.putString("route", "https://simple-night-1868.herokuapp.com/location.php?fb%3Aapp_id=338618139553202&og%3Atype=geoeventing%3Aroute&og%3Atitle=GeoEvent&og%3Aimage=+https%3A%2F%2Fs-static.ak.fbcdn.net%2Fimages%2Fdevsite%2Fattachment_blank.png&geoeventing%3Astartpoint%3Alatitude=" + startLat + "&geoeventing%3Astartpoint%3Alongitude=" + startLng +"&body=GeoEvent");
		 
		  graphParams.putString("route", "http://geoevent.hostei.com//location.php?fb%3Aapp_id=338618139553202&og%3Atype=geoeventing%3Aroute&og%3Atitle=route&og%3Aimage=+https%3A%2F%2Fsstatic.ak.fbcdn.net%2Fimages%2Fdevsite%2Fattachment_blank.png&geoeventing%3Aroute%3Alatitude=" + startLat + "&geoeventing%3Aroute%3Alongitude=" + startLng +"&body=GeoEvent");
		 //https://simple-night-1868.herokuapp.com/geolocation.php?fb%3Aapp_id=338618139553202&og%3Atype=geoeventing%3Ageolocation&og%3Atitle=Elia&og%3Aimage=+https%3A%2F%2Fsstatic.ak.fbcdn.net%2Fimages%2Fdevsite%2Fattachment_blank.png&geoeventing%3Alocation%3Alatitude=
		 // graphParams.putString("route", "https://simple-night-1868.herokuapp.com/location.php?fb%3Aapp_id=338618139553202&og%3Atype=geoeventing%3Aroute&og%3Atitle=GeoEvent&og%3Aimage=+https%3A%2F%2Fs-static.ak.fbcdn.net%2Fimages%2Fdevsite%2Fattachment_blank.png&geoeventing%3Astartpoint%3Alatitude=39.29354891&geoeventing%3Astartpoint%3Alongitude=23.13751019&body=GeoEvent");
		  String response = Utility.mFacebook.request("me/geoeventing:report", graphParams, "POST");
		  if (response == null || response.equals("") || 
                  response.equals("false")) {
             Log.v("Error", "Blank response");
          }
   } catch(Exception e) {
       e.printStackTrace();
   }
		  //Utility.mAsyncRunner.request("me/geoeventing:stay_on", graphParams, "POST", new addToTimelineListener(), null);
	  }
	  
	
	  
	  
	  
	  public void getDuration(String startLat, String startLng, String destLat, String destLng) {
	  // public void getDuration(String startLoc, String destLoc) {
	  //public String getDuration(String startLon, String startLat, String destLon, String destLat ) {
		  //StringBuilder response  = new StringBuilder();
         String stringUrl = "http://maps.googleapis.com/maps/api/directions/json?origin="+startLat+","+startLng+"&destination="+destLat+","+destLng+"&sensor=false";
		 //String stringUrl = "http://maps.googleapis.com/maps/api/directions/json?origin="+startLoc+"&destination="+destLoc+"&sensor=false";
		  Log.v(TAG, stringUrl);
	      String responseText;
		try {
			responseText = getResponseText(stringUrl);
			JSONObject mainResponseObject = new JSONObject(responseText);
			JSONArray routeObject = mainResponseObject.getJSONArray("routes");
			JSONObject route = routeObject.getJSONObject(0);
			// Take all legs from the route
			JSONArray legs = route.getJSONArray("legs");
			// Grab first leg
			JSONObject leg = legs.getJSONObject(0);

			JSONObject durationObject = leg.getJSONObject("duration");
			duration = durationObject.getString("text");
			JSONObject distanceObject = leg.getJSONObject("distance");
			gDistance = distanceObject.getString("text");
			start_address = leg.getString("start_address");
			
		    Log.v(TAG, "TRY IS WORKING");
		    Log.v(TAG, "duration :"+duration);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	     
		//String duration = "newDuration";
	

	  }
	  
	  private String getResponseText(String stringUrl) throws IOException
	  {
	      StringBuilder response  = new StringBuilder();

	      URL url = new URL(stringUrl);
	      HttpURLConnection httpconn = (HttpURLConnection)url.openConnection();
	      if (httpconn.getResponseCode() == HttpURLConnection.HTTP_OK)
	      {
	          BufferedReader input = new BufferedReader(new InputStreamReader(httpconn.getInputStream()),8192);
	          String strLine = null;
	          while ((strLine = input.readLine()) != null)
	          {
	              response.append(strLine);
	          }
	          input.close();
	      }
	      return response.toString();
	  }
	  
	
	  private class GpsReceiver extends BroadcastReceiver {
			
		  @Override
			public void onReceive(Context context, Intent intent) {
				showDataFromIntent(intent);
			}

			private void showDataFromIntent(Intent intent) {
				//shows up in the log file
				 Log.v(TAG, (formatCoordinates(intent.getDoubleExtra(
						"Longtitude", 0))));
				latitude = String.valueOf(intent.getDoubleExtra(
					"Latitude", 0));
				longitude = String.valueOf(intent.getDoubleExtra(
						"Longtitude", 0));
				altitude = String.valueOf(intent.getDoubleExtra(
						"Altitude", 0));
				speed = String.valueOf(intent.getDoubleExtra(
						"Speed", 0));
				//long unix_date = intent.getLongExtra("Time", 0);
				//time = (String) DateFormat.format("dd/MM/yyyy hh:mm:ssaa", unix_date * 1000L);
				time = String.valueOf(intent.getLongExtra(
						"Time", 0));
			      //longitude = String.valueOf(gps.getLongitude());
				 //longitude = (formatCoordinates(intent.getDoubleExtra(
							//"Longtitude", 0)));
				 Log.v(TAG, (formatCoordinates(intent.getDoubleExtra(
						"Latitude", 0))));
				 
				 Log.v(TAG, "SAVING GPS DATA");
				 if ((latitude.equals("0")) && (longitude.equals("0"))){
			    	  //do nothing
			      }
			      else{
			    	  
			    	  if ((distFrom(latitude, longitude, eventLat, eventLng)<distanceToEvent)){
				    	  //
				    	  Log.v(TAG, "RTTTTTJJJ!!!!!!!!!!!!!!!!!!!!!!###");
				    	  Context context = getApplicationContext();
				    	  NotificationManager notificationManager = 
				    	            (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
				    	        
				    	        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, null, 0);        
				    	        
				    	        Notification notification = createNotification();
				    	        notification.setLatestEventInfo(context, 
				    	            "Proximity Alert!", "You have reach your destination.", pendingIntent);
				    	        
				    	        notificationManager.notify(NOTIFICATION_ID, notification);
				    	  
				    	  
				    	  
				    	  stopService(new Intent(FBPost.class.getName()));;
			    	  }
				    	  else{
			    	  
			    	  singleton.insertGpsData(eid, event_title, latitude, longitude, speed, altitude, time);
				    	  }
				    	  }
				 //latitude = (formatCoordinates(intent.getDoubleExtra(
							//"Latitude", 0)));
			}
		}  
	  
	 
	  public String formatCoordinates(double base_number) {
			double degrees = Math.floor(base_number);

			double tempMinLeft = (base_number - degrees) * 60.0d;
			double min = Math.floor(tempMinLeft);

			double sec = (tempMinLeft - min) * 60.0d;

			NumberFormat formatter = new DecimalFormat("00");
			NumberFormat formatter_seconds = new DecimalFormat("00.00");

			String deg = formatter.format(degrees);
			String minnutes = formatter.format(min);
			String seconds = formatter_seconds.format(sec);
			String return_string = deg + "° : " + minnutes + "' : " + seconds
					+ "''";

			return return_string;
		}
	  
	  public String getAccessToken() {
			return Utility.mFacebook.getAccessToken();
		}
	  
	  //Haversine formula
	  public static double distFrom(String startLat, String startLng, String destLat, String destLng) {
		    
		    double lat1 = Double.parseDouble(startLat);
		    double lat2 = Double.parseDouble(destLat);
		    double lng1 = Double.parseDouble(startLng);
		    double lng2 = Double.parseDouble(destLng);
		  
		  //double earthRadius = 3958.75; for miles
		    //for meters
		    double earthRadius = 6371000;
		    double dLat = Math.toRadians(lat2-lat1);
		    double dLng = Math.toRadians(lng2-lng1);
		    double sindLat = Math.sin(dLat / 2);
		    double sindLng = Math.sin(dLng / 2);
		    double a = Math.pow(sindLat, 2) + Math.pow(sindLng, 2)
		            * Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2));
		    double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
		    double dist = earthRadius * c;

		    return dist;
		    }
	  
	  //
	  private Notification createNotification() {
	        Notification notification = new Notification();
	        
	        notification.icon = R.drawable.androidmarker;
	        notification.when = System.currentTimeMillis();
	        
	        notification.flags |= Notification.FLAG_AUTO_CANCEL;
	        notification.flags |= Notification.FLAG_SHOW_LIGHTS;
	        
	        notification.defaults |= Notification.DEFAULT_VIBRATE;
	        notification.defaults |= Notification.DEFAULT_LIGHTS;
	        
	        notification.ledARGB = Color.WHITE;
	        notification.ledOnMS = 1500;
	        notification.ledOffMS = 1500;
	        
	        return notification;
	    }
	  
	  
	  private boolean isNetworkAvailable() {
		    ConnectivityManager connectivityManager 
		          = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		    activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
		    return activeNetworkInfo != null;
		}
	  
	  
	}
