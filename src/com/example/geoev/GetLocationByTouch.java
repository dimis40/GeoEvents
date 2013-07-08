package com.example.geoev;

import java.io.IOException;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;

public class GetLocationByTouch extends MapActivity {

	private MapView mapView;
	private TextView eventLocation;
	private Integer latitude = 39061849;
	private Integer longitude = 22587891 ;
	private Button btnEvent;
	private String addressText="";
	
	// Handles Taps on the Google Map
	Handler h = new Handler(){
		
		// Invoked by the method onTap() 
		// in the class CurrentLocationOverlay
		@Override
		public void handleMessage(Message msg) {			
			Bundle data = msg.getData();
			
			// Getting the Latitude of the location
			int latitude = data.getInt("latitude");
			
			// Getting the Longitude of the location
			int longitude = data.getInt("longitude");	
			
			// Show the location in the Google Map
			showLocation(latitude,longitude);			 
		}
		
	};

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mapview_ontouch);
        
        // Getting reference to map_view available in activity_main.xml
        mapView = (MapView) findViewById(R.id.mapview_ontouch);
        
        // Getting reference to tv_location available in activity_main.xml
        eventLocation = (TextView) findViewById(R.id.event_location);
        btnEvent = (Button) findViewById(R.id.clickBtn);
        // Default Latitude
       // int latitude = 28426365;
        
        // Default Longitude
    	//int longitude = 77320393;
        
    	// Show the location in the Google Map
        showLocation(latitude,longitude);  
    
      //Register Listener for Button btnEvent
        btnEvent.setOnClickListener(new OnClickListener() {
    		
        	/*
        	* like http://developer.android.com/reference/android/view/View.html
        	*/
    		@Override
    		public void onClick(View v) {
    			Intent resultIntent = new Intent(GetLocationByTouch.this, CreateEvent.class);
    			//resultIntent.putExtra("Latitude", mLocation_Lat);
    			//resultIntent.putExtra("Longitude", mLocation_Lng);
    			resultIntent.putExtra("Address", addressText);
    			//Intent.FLAG_ACTIVITY_CLEAR_TOP |
    			resultIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
    			//resultIntent.putExtra("GPS_FLAG", String.valueOf(iUseGPSFlag));
    			//setResult(Activity.RESULT_OK, resultIntent);
    			startActivity(resultIntent);
    		}
    		
    	}); 
        
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
    private void showLocation(int latitude, int longitude){    
    	
    	
    	// Setting Zoom Controls
    	mapView.setBuiltInZoomControls(true);
       
    	// Getting the MapController
    	MapController mapController = mapView.getController();
       
    	// Getting Overlays of the map
    	List<Overlay> overlays = mapView.getOverlays();
       
    	// Getting Drawable object corresponding to a resource image
    	Drawable drawable = getResources().getDrawable(R.drawable.androidmarker);
       
    	// Creating an ItemizedOverlay
    	TouchedLocationOverlay locationOverlay = new TouchedLocationOverlay(drawable,h);        
       
    	// Getting the MapController
    	MapController mc = mapView.getController();        

    	// Creating an instance of GeoPoint, to display in Google Map
    	GeoPoint p = new GeoPoint(
	    					latitude, 
	    					longitude
    					);

    	// Locating the point in the Google Map
    	mc.animateTo(p);        
       
    	// Creating an OverlayItem to mark the point
    	OverlayItem overlayItem = new OverlayItem(p, "Item", "Item");
       
    	// Adding the OverlayItem in the LocationOverlay
    	locationOverlay.addOverlay(overlayItem);
       
    	// Clearing the overlays
    	overlays.clear();
       
    	// Adding locationOverlay to the overlay
    	overlays.add(locationOverlay);
       
    	// Redraws the map
    	mapView.invalidate();
    	
    	
    	 Double[] lat_long = new Double[] { latitude/1E6, longitude/1E6 };
    	//Double[] lat_long = new Double[] { (double) latitude, (double) longitude };
    
    	// Executing ReverseGeocodingTask to get Address
    	new ReverseGeocodingTask(getBaseContext()).execute(lat_long);
    	
    }

   /* @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return false;
    }*/

	@Override
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return false;
	}	
	
	private class ReverseGeocodingTask extends AsyncTask<Double, Void, String>{
		Context mContext;
		
		public ReverseGeocodingTask(Context context){
			super();
			mContext = context;
		}

		@Override
		protected String doInBackground(Double... params) {
			Geocoder geocoder = new Geocoder(mContext);
			double latitude = params[0].doubleValue();
			double longitude = params[1].doubleValue();
			double myLatitude = latitude*1000000;
			double myLongitude = longitude*1000000;
			int mylat = (int)myLatitude;
			int mylong  = (int)myLongitude;
			
			List<Address> addresses = null;
			//String addressText="";
			//String addressTextToSend = "";
			
			try {
				addresses = geocoder.getFromLocation(latitude, longitude,1);
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			if(addresses != null && addresses.size() > 0 ){
				Address address = addresses.get(0);
				
				/*addressText = String.format("%s, %s, %s",
	                    address.getMaxAddressLineIndex() > 0 ? address.getAddressLine(0) : "",
	                    address.getLocality(),	                    
	                    address.getCountryName());		*/
				
				addressText = String.format("Address:%s, Locality:%s, Country:%s, Coordinates:%s,%s",
	                    address.getMaxAddressLineIndex() > 0 ? address.getAddressLine(0) : "",
	                    address.getLocality(),	                    
	                    address.getCountryName(),
	                    String.valueOf(mylat),
	                    String.valueOf(mylong)
	                    );
			}
			
			return addressText;
		}		
		
		@Override
		protected void onPostExecute(String addressText) {			
			// Setting address of the touched Position
	    	eventLocation.setText(addressText);
		}
		
	}
}
