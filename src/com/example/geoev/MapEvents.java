package com.example.geoev;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.LinearLayout;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;

public class MapEvents extends MapActivity {
    
	private MapController mapController;
	private MapView mapView;
	public static final String NEW_MSG_MAP = "com.example.geoev.NEW_MSG_MAP";
	private GpsReceiver myMapReceiver;
	//private MyLocationOverlay myLocationOverlay;
	private DatabaseHelper singleton;
	public static final String TAG = "MapEvents";
	private List<FbEvent> events = new ArrayList<FbEvent>();
	private EventOverlay itemizedoverlay;
	private List<Overlay> mapOverlays;
	private Handler mHandler;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		//
		
		
		mHandler = new Handler();
		
		//This loads the layout file
		setContentView(R.layout.mapview);
		singleton = DatabaseHelper.getInstance(getApplicationContext());
		//there's a very simple zoom feature built into the MapView class
		//mapView.setBuiltInZoomControls(true);
		LinearLayout linearLayout = (LinearLayout) findViewById(R.id.mainlayout);
		mapView = (MapView) findViewById(R.id.mapview);
		mapView.setBuiltInZoomControls(true);
		mapView.setStreetView(true);
		
		
		//added 04/11/2012 its working
				//List<Overlay> mapOverlays = mapView.getOverlays();
		         mapOverlays = mapView.getOverlays();
				Drawable drawable = this.getResources().getDrawable(R.drawable.androidmarker);
				//EventOverlay itemizedoverlay = new EventOverlay(drawable, this);
				itemizedoverlay = new EventOverlay(drawable, this);
				
				//
			    //
			   // if (FBService.gotFriends == false || singleton.lock.isLocked()){
			        	//get fresh data
			        	//Get Events Information using FQL
		           String query_allevents = "Select eid, name, start_time, end_time, location, creator, description, host, update_time, venue from event where eid IN (select eid from event_member where uid = me() AND rsvp_status = \"attending\") AND start_time >= now()";
			            Bundle params_event = new Bundle();
			            params_event.putString("method", "fql.query");
			            params_event.putString("query", query_allevents);
			            Utility.mAsyncRunner.request(null, params_event,
			                    new EventFQLRequestListener());
			   /* }
			    else {
				
				
				for (FbEvent event : singleton.getAllEvents()) {
				
				//GeoPoint point = new GeoPoint(39304390,23115921);
				GeoPoint point = new GeoPoint(Integer.parseInt(event.getLatitude()),Integer.parseInt(event.getLongitude()));
				OverlayItem overlayitem = new OverlayItem(point, event.getTitle(), event.getDescription());
				itemizedoverlay.addOverlay(overlayitem);
				mapOverlays.add(itemizedoverlay);
				}
			     }*/
		
		MyLocationOverlay myLocationOverlay = new MyLocationOverlay(this, mapView);
		//myLocationOverlay = new MyLocationOverlay(this, mapView);
        mapView.getOverlays().add(myLocationOverlay);
        myLocationOverlay.enableCompass(); // if you want to display a compass also
        myLocationOverlay.enableMyLocation();
        
		
		mapController = mapView.getController();
		mapController.setZoom(14); // Zoon 1 is world view*/
		//ArrayList all_geo_points = getDirections(10.154929, 76.390316, 10.015861, 76.341867);
		
		
		
		mapView.invalidate();
		

		
		myMapReceiver = new GpsReceiver();
		IntentFilter filter = new IntentFilter(NEW_MSG_MAP);
		registerReceiver(myMapReceiver, filter);
		
		//mapView.getOverlays().add(new MyOverlay(all_geo_points));

	}
    //This method is required for some accounting from the Maps service to see 
	//if you're currently 
	//displaying any route information. In this case, you're not, so return false.
	@Override
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return false;
	}
	
	//
	public boolean onCreateOptionsMenu(Menu menu){
	    super.onCreateOptionsMenu(menu);
	    MenuInflater oMenu = getMenuInflater();
	    oMenu.inflate(R.menu.maps_menu, menu);
	    return true;
	}

	public boolean onOptionsItemSelected(MenuItem item){
	    switch(item.getItemId()){
	    case R.id.mapStreet:
	         mapView.setStreetView(true);
	         mapView.setSatellite(false);
	         mapView.invalidate();
	         return true;

	    case R.id.mapSat:
	         mapView.setSatellite(true);
	         mapView.setStreetView(false);
	         mapView.invalidate();
	         return true;
	    }
	    return false;
	}
	

	private class GpsReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			showDataFromIntent(intent);
		}

		private void showDataFromIntent(Intent intent) {
			int lat = (int) (intent.getDoubleExtra(
					"Latitude", 0) * 1E6);
			int lng = (int) (intent.getDoubleExtra("Longtitude", 0) * 1E6);
           // Location loc = new Location(lat, lng);
			GeoPoint point = new GeoPoint(lat, lng);
			mapController.animateTo(point); // mapController.setCenter(point);
			//myLocationOverlay.onLocationChanged();
		}
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
                    String name = outer.getString("name");
                    String start_time = outer.getString("start_time");
                    String end_time = outer.getString("end_time");
                    String locationStr = outer.getString("location");
                    String creator = outer.getString("creator");
                    String update_time = outer.getString("update_time");
                    String description = outer.getString("description");
                    //initiate strings that are associated with parsing
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
				//MapEvents.this.runOnUiThread(new Runnable() {
				mHandler.post(new Runnable() {
					public void run() {
						for (FbEvent event : events) {
							
							//GeoPoint point = new GeoPoint(39304390,23115921);
							GeoPoint point = new GeoPoint(Integer.parseInt(event.getLatitude()),Integer.parseInt(event.getLongitude()));
							OverlayItem overlayitem = new OverlayItem(point, event.getTitle(), event.getDescription());
							itemizedoverlay.addOverlay(overlayitem);
							mapOverlays.add(itemizedoverlay);
							}
					}
				});
				
				
				
			} catch (JSONException e) {
				Log.w(TAG, "JSON Error in response");
			}
		}
    }
}
