package com.example.geoev;

import java.util.Timer;

import android.app.Activity;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.TextView;

public class AllFriends extends Activity {
	//test
	private Timer timer;
	
	
	//test
	public MyMalakiaTest updater;
	
	//final String SOME_ACTION = "com.example.geoev.AllFriends.DataUpdateReceiver";
    //IntentFilter intentFilter = new IntentFilter(SOME_ACTION);
   // private DataUpdateReceiver dataUpdateReceiver;
	
	public static final String TAG = "AllFriends";
	//private ArrayList<Person> persons;
	private FBFriends fbfriends;
	private TextView mText;
	private DatabaseHelper singleton;
	
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
		//if (dataUpdateReceiver == null) dataUpdateReceiver = new DataUpdateReceiver();
		IntentFilter intentFilter = new IntentFilter("com.example.geoev.AllEvents.DataUpdateReceiver");
		//registerReceiver(dataUpdateReceiver, intentFilter);
	}


	private LinearLayout rootView;
	//private Facebook mFacebook;    
    //private AsyncFacebookRunner mAsyncRunner; 
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	//setContentView(R.layout.create_event);
	Log.v("INFO", "AllFriends.onCreate()");
	fbfriends = new FBFriends();
	//persons = fbfriends.getPersons();
	// setup the content view
	initLayout();
	singleton = DatabaseHelper.getInstance(getApplicationContext());
	
	//test
	 
	 
	//VERY VERY IMPORTANT THE PROBLEM IS THAT FIRST I HAVE TO GET THE FRIENDS LIST
	//AND THEN SHOW ON TEXT VIEW SO I DELAY
	// Fetch the profile image - delay 3 seconds to give authentication time to propagate.
	   
	   new Handler().postAtTime(new Runnable() {
    	
        @Override
        public void run() {
        	showFBFriends2();
            //task.execute(EVENT_URL + "/picture?access_token=" + accessToken + "&type=large");
        }
    	
    } ,SystemClock.uptimeMillis() + 1
    );
	    };
	
	//mFacebook = ((GeoEventApplication)getApplicationContext()).facebook;
	
	//Call the graph Api
	//Utility.mAsyncRunner.request("me/friends", new FriendsRequestListener());
	
	//gia na doume
	//postOnWall("Hello");

	
	
	protected void initLayout() {
		 
		rootView = new LinearLayout(this.getApplicationContext());
		rootView.setOrientation(LinearLayout.VERTICAL);

		
		
		this.mText = new TextView(this.getApplicationContext());
		this.mText.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT));
		rootView.addView(this.mText);
		

		this.setContentView(rootView);

	}
	
	
	/*
	/*
     * Callback for fetching user's facebook friends
     */
    public void showFBFriends() {
    	
			    // then post the processed result back to the UI thread
				// if we do not do this, an runtime exception will be generated
				// e.g. "CalledFromWrongThreadException: Only the original
				// thread that created a view hierarchy can touch its views."
				AllFriends.this.runOnUiThread(new Runnable() {
				public void run() {
						Log.d(TAG, "run");
						for (Person person : fbfriends.getPersons()) {
							TextView view = new TextView(
									getApplicationContext());
							Log.d(TAG, person.getName());
							view.setText(person.getName());
							view.setTextSize(16);
							rootView.addView(view);
					}
				}
				});
			
		}
    
    /*
     * Callback for fetching user's facebook friends
     */
    public void showFBFriends2() {
    	
			    // then post the processed result back to the UI thread
				// if we do not do this, an runtime exception will be generated
				// e.g. "CalledFromWrongThreadException: Only the original
				// thread that created a view hierarchy can touch its views."
				AllFriends.this.runOnUiThread(new Runnable() {
				public void run() {
						Log.d(TAG, "run");
						//singleton.getAllFriends();
						for (Person person : singleton.getAllFriends()) {
							TextView view = new TextView(
									getApplicationContext());
							Log.d(TAG, person.getName());
							view.setText(person.getName());
							view.setTextSize(16);
							rootView.addView(view);
					}
						//concurrent seems to be working
						//singleton.insertEvent("Hello", "id", "start", "end", "loc");
				}
				
				});
			
		}
    
    
    //test not used
    public class MyMalakiaTest extends Thread{
    	static final long DELAY = 1000 * 10;   
    	 
    	public void run() {
    		//for (int i=1;i<3000;i++){
    		//try{
    		 showFBFriends2();
    		 //Thread.currentThread();
             //Thread.sleep(DELAY);;
    		// }catch (InterruptedException e) {
    		 //}
    		// }
    	 }
    }
    	 
    	
	
    
}
