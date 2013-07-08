package com.example.geoev;

import java.util.ArrayList;
import java.util.List;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;

public class TouchedLocationOverlay extends ItemizedOverlay<OverlayItem> {
	
	private ArrayList<OverlayItem> mOverlays = new ArrayList<OverlayItem>();	
	private Handler handler;

	public TouchedLocationOverlay(Drawable defaultMarker,Handler h) {
		super(boundCenterBottom(defaultMarker));	
		
		// Handler object instantiated in the class MainActivity
		this.handler = h;
	}

	// Executed, when populate() method is called
	@Override
	protected OverlayItem createItem(int arg0) {
		return mOverlays.get(arg0);		
	}	

	@Override
	public int size() {		
		return mOverlays.size();
	}	
	
	public void addOverlay(OverlayItem overlay){
		mOverlays.add(overlay);
		populate(); // Invokes the method createItem()
	}
	
	// This method is invoked, when user tap on the map
	@Override
	public boolean onTap(GeoPoint p, MapView map) {		
            
	    List<Overlay> overlays = map.getOverlays();
	    
	    // Creating a Message object to send to Handler
	    Message message = new Message();
	    
	    // Creating a Bundle object ot set in Message object
	    Bundle data = new Bundle();
	    
	    // Setting latitude in Bundle object
	    data.putInt("latitude", p.getLatitudeE6());
	    
	    // Setting longitude in the Bundle object
	    data.putInt("longitude", p.getLongitudeE6());
	    
	    // Setting the Bundle object in the Message object
	    message.setData(data);
	    
	    // Sending Message object to handler
	    handler.sendMessage(message);		
		
		return super.onTap(p, map);
	}	
}