package com.example.geoev;

import android.app.TabActivity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabHost.TabSpec;
import android.widget.Toast;

public class EventInfo extends TabActivity implements OnTabChangeListener {
	private static final String TAG = EventInfo.class.getSimpleName();
	private TabHost mtabHost;
	private String eid = "";
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.event_view);      
        //
        Bundle extras = getIntent().getExtras();
        eid = extras.getString("eid");
        
        
        
        //i would erase it later
        Toast toast = Toast.makeText(getApplicationContext(), "the event id : eid", Toast.LENGTH_LONG );
        Log.d(TAG, "Event with !!!! ...!! eid : " + eid);
       
        
        
        Resources res = getResources(); 
        TabHost tabHost = getTabHost(); 
         
        //Intent eventFeedIntent;
        //Intent attendeesIntent;
        mtabHost = getTabHost();
        mtabHost.setOnTabChangedListener(this);

        Intent eventFeedIntent=new Intent().setClass(this, FbFeeds.class); 
        eventFeedIntent.putExtra("eid", eid);       
        TabSpec eventfeedspec = tabHost.newTabSpec("Event Feed").setIndicator("Event Feed",res.getDrawable(R.drawable.androidmarker)).setContent(eventFeedIntent);
        //Intent friendsIntent = new Intent(this, AllFriends.class);
        
        
        Intent attendeesIntent = new Intent().setClass(this, AttendeesList.class); 
        attendeesIntent.putExtra("eid", eid);
        TabSpec attendeesspec = tabHost.newTabSpec("Attendess").setIndicator("Attendees",res.getDrawable(R.drawable.androidmarker)).setContent(attendeesIntent);
        //Intent friendsIntent = new Intent(this, AllFriends.class);
       
        
        
         //intent = new Intent().setClass(this, MapEvents.class);
        // spec = tabHost.newTabSpec("Map").setIndicator("Map",res.getDrawable(R.drawable.androidmarker)).setContent(intent);
         tabHost.addTab(eventfeedspec);
         tabHost.addTab(attendeesspec);

        //intent = new Intent().setClass(this, CityActivity.class);
        //spec = tabHost.newTabSpec("city").setIndicator("City", res.getDrawable(R.drawable.ic_tab_city)).setContent(intent); 
        //tabHost.addTab(spec);

        tabHost.setCurrentTab(2);
    }
	
	
	
	@Override
	public void onTabChanged(String tabId){
		int a = mtabHost.getCurrentTab();
        String b = Integer.toString(a); 
       /* if(b.equals("1"))
        {           
            Toast.makeText(getApplicationContext(), "Map Selected", Toast.LENGTH_LONG).show();
        }     */          
    } 
		// TODO Auto-generated method stub
		
	

}
