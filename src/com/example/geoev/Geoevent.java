package com.example.geoev;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.geoev.Checker.Resource;
import com.example.geoev.SessionEvents.AuthListener;
import com.example.geoev.SessionEvents.LogoutListener;
import com.facebook.android.AsyncFacebookRunner;
import com.facebook.android.Facebook;
import com.facebook.android.FacebookError;
import com.facebook.android.Util;


public class Geoevent extends Activity{

    // Your Facebook Application ID must be set before running this example
    // See http://www.facebook.com/developers/createapp.php
    public static final String APP_ID = "338618139553202";
    //
    public Thread t;
    
    //MENU
    public static final int CREATE_EVENT = Menu.FIRST;
	public static final int GET_EVENTS = Menu.FIRST + 1;
	public static final int MAP_VIEW = Menu.FIRST + 2;
	public static final int STOP_SERVICES = Menu.FIRST + 3;
	public static final int GET_PERSONS = Menu.FIRST + 4;
	public static final int SETTINGS_BUTTON = Menu.FIRST + 5;
	public static final int BATTERY_MONITOR = Menu.FIRST + 6;
	//global item fro singleton implementation
	private Global global;
	
	
	//
	private ImageView mUserPic;
	//
	private ProgressDialog dialogfetchdata;
	public EntryItemAdapter mAdapter;
	//private Context context = this;
	private Context context;
	ListView eventsListView;
    
	public static final String TAG = "Geoevent";

	protected static final ServiceConnection ServiceConnection = null;
	
	private TextView alert;
    private LoginButton mLoginButton;
    private TextView mText;
    
    private UnixTimeConverter testcurrentime  = new UnixTimeConverter();
    
    private DatabaseHelper singleton;
    
    String[] permissions = { "offline_access", "publish_stream", "user_photos", "publish_checkins", "read_stream",
            "photo_upload", "create_event", "manage_pages" , "rsvp_event", "user_events", "friends_events"};
    //private Facebook mFacebook;
    //private AsyncFacebookRunner mAsyncRunner;
    private Handler mHandler;
    
   // private NetworkInfo activeNetworkInfo;
    private ArrayList<FbEvent> events = new ArrayList<FbEvent>();
    
    //added 01/10/2012
    private ArrayList<Person> persons = new ArrayList<Person>();
    //
    Bitmap mIcon1 = null;
    View viewToLoad;
    
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	
    	
    	//for custom titles the order of action is very important
    	//If you call super.onCreate() before any of the other statements you will get a blank title bar, which the hack of finding 
    	//the title bar id and removing all the Views from it will fix but is not recommended.
    	/*requestWindowFeature( Window.FEATURE_CUSTOM_TITLE );
    	setContentView( R.layout.main);
    	getWindow().setFeatureInt( Window.FEATURE_CUSTOM_TITLE, R.layout.maintitlebar );*/
       super.onCreate( savedInstanceState );
       context = this;
       //viewToLoad = getLayoutInflater().inflate(R.layout.main_alt, null);
       //this.setContentView(viewToLoad);
       
      
       
       //tha to xrisimopoiso se alli klasi
      /* Calendar cal = Calendar.getInstance();              
       Intent intent = new Intent(Intent.ACTION_EDIT);
       intent.setType("vnd.android.cursor.item/event");
       intent.putExtra("beginTime", cal.getTimeInMillis());
       intent.putExtra("allDay", true);
       intent.putExtra("rrule", "FREQ=YEARLY");
       intent.putExtra("endTime", cal.getTimeInMillis()+60*60*1000);
       intent.putExtra("title", "A Test Event from android app");
       startActivity(intent);*/
       
       
       
    	//
    	/*t = new Thread(){
    		public void run(){
    		getApplicationContext().bindService(
    		        new Intent(getApplicationContext(), GpsBroadcast.class),
    		        ServiceConnection,
    		        Context.BIND_AUTO_CREATE
    		    );
    		}
    		};*/
    	
    	//singleton = DatabaseHelper.getInstance(getApplicationContext());
    	
    	
    	//mFacebook = ((GeoEventApplication)getApplicationContext()).facebook;
    	
        if (APP_ID == null) {
            Util.showAlert(this, "Warning", "Facebook Applicaton ID must be " +
                    "specified before running this example: see Example.java");
        }
        
        
        
        setContentView(R.layout.main_alt);
        mHandler = new Handler();
       // mLoginButton = (LoginButton) findViewById(R.id.login);
        mText = (TextView) Geoevent.this.findViewById(R.id.txt);
        mUserPic = (ImageView)Geoevent.this.findViewById(R.id.user_pic);
        //alert = (TextView) findViewById(R.id.alert_main);
        
       /* mAdapter = new EntryItemAdapter(viewToLoad.getContext(),
            	 	   R.layout.event_item, events);*/
        eventsListView = (ListView) findViewById(R.id.events_list);
       // eventsListView.setAdapter(mAdapter);
       
        // Create the Facebook Object using the app id.
        Utility.mFacebook= new Facebook(APP_ID);
        // Instantiate the asynrunner object for asynchronous api calls.
       Utility.mAsyncRunner = new AsyncFacebookRunner(Utility.mFacebook);
       mLoginButton = (LoginButton) findViewById(R.id.login);
       	
       // restore session if one exists
        SessionStore.restore(Utility.mFacebook, this);
        SessionEvents.addAuthListener(new SampleAuthListener());
        SessionEvents.addLogoutListener(new SampleLogoutListener());
        
        
        
        
        
        //check network connection
       // isNetworkAvailable();
        //if there is no connection inform the user
        //if (activeNetworkInfo==null) Toast.makeText(Geoevent.this, "No Internet Connection", Toast.LENGTH_LONG).show();
        /*
         * Source Tag: login_tag
         */
        mLoginButton.init(this, Utility.mFacebook, permissions);
        global = Global.getInstance();
        //added in athens
        //if(mAdapter != null){
       
        /*//if(global.getFirstTimeExecution() == false){
        dialogfetchdata = new ProgressDialog(context);
        dialogfetchdata.setMessage("fetching data, please wait...");
        dialogfetchdata.show();*/
       // }
        
        
        eventsListView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {
			
				//launchEventDetailsActivity((Event) parent
				FbEvent event = new FbEvent();
		        event = (FbEvent) parent.getItemAtPosition(position);
				Intent myNewIntent = new Intent(getApplicationContext(), Event_details.class);
				myNewIntent.putExtra("eid", event.getId());
				myNewIntent.putExtra("name", event.getTitle());
				myNewIntent.putExtra("start_time", event.getStartTime());
				myNewIntent.putExtra("end_time", event.getEndTime());
				myNewIntent.putExtra("location", event.getLocation());
				myNewIntent.putExtra("latitude", event.getLatitude());
				myNewIntent.putExtra("longitude", event.getLongitude());
				myNewIntent.putExtra("creator", event.getCreator());
				myNewIntent.putExtra("description", event.getDescription());
				myNewIntent.putExtra("pic", event.getPicUrl());
				myNewIntent.putExtra("attending_count", event.getAttendingCount());
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
        
        
        
        
        
        
        
        
        //if any session exists
        if (Utility.mFacebook.isSessionValid()) {
            global.setFirstTimeExecution(false);
            /*dialogfetchdata = new ProgressDialog(context);
            dialogfetchdata.setMessage("fetching data, please wait...");
            dialogfetchdata.show();*/
        	//get user data
        	requestUserData();
        	//get event data
        	requestEventData();
        	
        	
         
         
      /* mAdapter = new EntryItemAdapter(this,
 	   R.layout.event_item, events);
       
        eventsListView = (ListView) findViewById(R.id.events_list);
        eventsListView.setAdapter(mAdapter);
        eventsListView.setVisibility(View.VISIBLE);*/
        	
        
        	
        	//set alert text to visible
        	//alert.setVisibility(View.VISIBLE);
        	
        	//Start FBPost Service
        	//TODO I want when logout to stop service
        	//but i have to research it
        	//startService(new Intent(FBPost.class.getName()));
        	//t.start();
        	 startService(new Intent(GpsBroadcast.class.getName()));
        	// startService(new Intent(BatteryService.class.getName()));
        	//startService(new Intent(FBService.class.getName()));
        	//startService(new Intent(GPSBroadcast.class.getName()));
        }

    }  
	
    
    @Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}


	//added 30 September 2012
    @Override
    public void onResume() {
    	super.onResume();
    	context = this;
    	  new Checker(this).pass(new Checker.Pass() {
    	     @Override public void pass() {
    	        //do your stuff here, do nothing outside here
    	     }
    	  }).check(Resource.GPS, Resource.NETWORK);
        if(Utility.mFacebook != null) {
            if (!Utility.mFacebook.isSessionValid()) {
                mText.setText("You are logged out! ");
                mUserPic.setImageBitmap(null);
               // if(global.getFirstTimeExecution() == false)
                eventsListView.setVisibility(View.INVISIBLE);
                
                
               // 
                
                
                
            } else {
                Utility.mFacebook.extendAccessTokenIfNeeded(this, null);
                
                //if(global.getFirstTimeExecution() == false)
                eventsListView.setVisibility(View.VISIBLE);
                
                
            }
        }
    }
    
    
    @Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		menu.add(Menu.NONE, CREATE_EVENT, Menu.NONE, "Create Event");
		menu.add(Menu.NONE, GET_EVENTS, Menu.NONE, "My Events");
		menu.add(Menu.NONE, MAP_VIEW, Menu.NONE, "Map Events");
		menu.add(Menu.NONE, STOP_SERVICES, Menu.NONE, "Event Monitoring");
		menu.add(Menu.NONE, GET_PERSONS, Menu.NONE, "Get My Friends");
		menu.add(Menu.NONE, SETTINGS_BUTTON, Menu.NONE, "Settings");
		menu.add(Menu.NONE, BATTERY_MONITOR, Menu.NONE, "Battery");
		
		return true;
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		//menu.setGroupEnabled(0, false);
		MenuItem createeventitem = menu.findItem(Menu.FIRST);
		MenuItem stopServiceItem = menu.findItem(Menu.FIRST + 3);
		
        //auto tha to sviso apla skeftomai mipos apenergopoiiso olo to menu
		if (Utility.mFacebook.isSessionValid()) {
			createeventitem.setTitle("Create Event");
			menu.setGroupEnabled(0, true);
			
		} else {
			menu.setGroupEnabled(0, false);
			//createeventitem.setTitle("Login");
			//getID.setEnabled(false);
		}
		//createeventitem.setEnabled(true);
		if (global.getFBPostServiceRunning() == true){
			stopServiceItem.setTitle("Stop Monitoring the event");
		
	    } else {
	    	stopServiceItem.setEnabled(false);
	    }
		return super.onPrepareOptionsMenu(menu);
	}
    
	
	
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int itemId = item.getItemId();
		switch (itemId) {
		case CREATE_EVENT:
			Intent myIntent = new Intent(getApplicationContext(), CreateEvent.class);
			startActivity(myIntent);
			/*if (mFacebook.isSessionValid()) {
				Log.d(TAG, "sessionValid");
				mText.setText("Logging out...");
				AsyncFacebookRunner asyncRunner = new AsyncFacebookRunner(
						mFacebook);
				
			} else {
				Log.d(TAG, "sessionNOTValid, relogin");
				//mFacebook.authorize(this, PERMS, new LoginDialogListener());
			}*/
			break;

		case GET_EVENTS:
			
					Intent getEventsIntent = new Intent(getApplicationContext(), AllEvents.class);
					startActivity(getEventsIntent);	
			break;
		case MAP_VIEW:
			Intent myNewIntent = new Intent(getApplicationContext(), MapEvents.class);
			startActivity(myNewIntent);
			break;
		case STOP_SERVICES:
			stopService(new Intent(FBPost.class.getName()));;
			break;
		case GET_PERSONS:
			//if (FBService.gotFriends == false || singleton.lock.isLocked()){
			//if service has not finished downloading friends
			Log.d(TAG, "I am getting fresh data");
			Intent getPersonsIntent = new Intent(getApplicationContext(), AllGps_test.class);
			startActivity(getPersonsIntent);
			/*}
			else{
			//get cached data
			Log.d(TAG, "I am getting cached data");
			Intent getPersonsIntent = new Intent(getApplicationContext(), AllFriends.class);
			startActivity(getPersonsIntent);	
			}*/
			break;
		case SETTINGS_BUTTON:
			 Intent settings_intent= new Intent(getApplicationContext(), SettingsActivity.class); 
		        //myIntent.putExtra("event_title", event_title);
		        startActivity(settings_intent);
			break;
		case BATTERY_MONITOR:
			 Intent battery_intent= new Intent(getApplicationContext(), BatteryMonitor.class); 
		        //myIntent.putExtra("event_title", event_title);
		        startActivity(battery_intent);
			break;
		default:
			return false;
		}
		return true;
	}
    
	/*private boolean isNetworkAvailable() {
	    ConnectivityManager connectivityManager 
	          = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
	    activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
	    return activeNetworkInfo != null;
	}*/
    
        
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent data) {
    	 //if (resultCode == Activity.RESULT_OK) {
        Utility.mFacebook.authorizeCallback(requestCode, resultCode, data);
    	// }
    }
    
    /*
     * The Callback for notifying the application when authorization succeeds or
     * fails.
     */
    public class SampleAuthListener implements AuthListener {

        public void onAuthSucceed() {
           
           // set visibilty of Alert text
        	//alert.setVisibility(View.VISIBLE);
        	// request user data when you have successfully log in
        	//global.setFirstTimeExecution(false);
        	/*dialogfetchdata = new ProgressDialog(context);
            dialogfetchdata.setMessage("fetching data, please wait...");
            dialogfetchdata.show();*/
        	
        	 Intent basic= new Intent(getApplicationContext(), Geoevent.class); 
		        //myIntent.putExtra("event_title", event_title);
        	 basic.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		     startActivity(basic);
        	//requestUserData();
		     
		     
		     
        	//requestEventData();
        	//eventsListView.setVisibility(View.VISIBLE);
        	//startService(new Intent(FBPost.class.getName()));
        	//t.start();
        	 startService(new Intent(GpsBroadcast.class.getName()));
        	 //startService(new Intent(BatteryService.class.getName()));
        	//startService(new Intent(FBService.class.getName()));
            
            
        }

        public void onAuthFail(String error) {
            mText.setText("Login Failed: " + error);
        }
    }

    public class SampleLogoutListener implements LogoutListener {
        public void onLogoutBegin() {
            mText.setText("Logging out...");
        }

        public void onLogoutFinish() {
            mText.setText("You have logged out! ");
            mUserPic.setImageBitmap(null);
            //added 25/12/2012
           stopService(new Intent(FBPost.class.getName()));
           /*Toast.makeText(
		            getApplicationContext(),
		            "Stopped posting to facebook events wall.",
		            Toast.LENGTH_LONG).show();*/
          
           
           
           
           eventsListView.setVisibility(View.INVISIBLE);
          //set alert text to visible
        	//alert.setVisibility(View.INVISIBLE);
           
            
            
        }
    }
    
    /*
     * Request user name, and picture to show on the main screen.
     */
    public void requestUserData() {
        mText.setText("Fetching user name, profile pic...");
        Bundle params = new Bundle();
        params.putString("fields", "name, picture");
        Utility.mAsyncRunner.request("me", params, new UserRequestListener());
    }
    
    public void requestEventData(){
    	/*mAdapter = new EntryItemAdapter(viewToLoad.getContext(),
     	 	   R.layout.event_item, events);*/
    	mAdapter = new EntryItemAdapter(this,
    		 	   R.layout.event_item, events);
    	eventsListView.setAdapter(mAdapter);
    	dialogfetchdata = new ProgressDialog(context);
        dialogfetchdata.setMessage("fetching data, please wait...");
        dialogfetchdata.show();
    	//Get Events Information using FQL
         String query_allevents = "Select eid, name, start_time, end_time, location, creator, description, host, pic, attending_count, update_time, venue from event where eid IN (select eid from event_member where uid = me()) AND start_time >= now()";
         Bundle params_event = new Bundle();
         params_event.putString("method", "fql.query");
         params_event.putString("query", query_allevents);
         Utility.mAsyncRunner.request(null, params_event,
                 new EventFQLRequestListener());
     
    }
    
   
    
    /*
     * Callback for fetching current user's name, picture, uid.
     */
    public class UserRequestListener extends BaseRequestListener {

        @Override
        public void onComplete(final String response, final Object state) {
            JSONObject jsonObject;
            try {
            	// process the response here: executed in background thread
                Log.d("GeoEevent", "Response: " + response.toString());
                jsonObject = new JSONObject(response);

                final String picURL = jsonObject.getString("picture");
                final String name = jsonObject.getString("name");
                final String uid = jsonObject.getString("id");
                //i want to have globally the uid especially for deleting events
                global.setFbUid(uid);
                //Utility.userUID = jsonObject.getString("id");
                URL img_value = null;
                
                try {
					img_value = new URL("http://graph.facebook.com/"+ uid +"/picture?type=square");
					//if i want large  use type large or small gia small
					// userpicture.setImageBitmap(mIcon1);
				} catch (MalformedURLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
              
                try {
					mIcon1 = BitmapFactory.decodeStream(img_value.openConnection().getInputStream());
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        mText.setText("Welcome " + name + "!");
                        mUserPic.setImageBitmap(mIcon1);
                       
                        //sos edo to kalo allaprepei na to diorthoso
                        //requestEventData();
                    }
                });

            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

    }
    
    
    //for future use
    public class SampleRequestListener extends BaseRequestListener {

        public void onComplete(final String response, final Object state) {
            try {
                // process the response here: executed in background thread
                Log.d("Facebook-Example", "Response: " + response.toString());
                JSONObject json = Util.parseJson(response);
                final String name = json.getString("name");

                // then post the processed result back to the UI thread
                // if we do not do this, an runtime exception will be generated
                // e.g. "CalledFromWrongThreadException: Only the original
                // thread that created a view hierarchy can touch its views."
                Geoevent.this.runOnUiThread(new Runnable() {
                    public void run() {
                        mText.setText("Hello there, " + name + "!");
                    }
                });
            } catch (JSONException e) {
                Log.w("Facebook-Example", "JSON Error in response");
            } catch (FacebookError e) {
                Log.w("Facebook-Example", "Facebook Error: " + e.getMessage());
            }
        }
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
                    String picture = outer.getString("pic");
                    String attending_count = outer.getString("attending_count");
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
                                	FbEvent newEvent = new FbEvent(eid, name, start_time, end_time, location, creator, lat, lon, update_time, description, picture, attending_count);
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
                   //Log.d(TAG, "malakoboukis:" + "loc:" + myLocation + "add:" + address +"loc:"+ locality + "count:" + country + "last:" + lastcoordinates);
                  
                    
                    // FbEventAttendees.add(newFbEventAttendee);
                    //events.add(newEvent);
                    //Log.d(TAG, "Event Name: " + newEvent.getTitle());
				}
				
				//singleton.insertAllEvents(events);
               // events.clear();
				
				//very important etsi ananeono to periexomeno tou 

				mHandler.post(new Runnable() {
				//AllEvents.this.runOnUiThread(new Runnable() {
					public void run() {
					if (dialogfetchdata != null && dialogfetchdata.isShowing()) {
						//added in athens
						if(mAdapter != null){
						mAdapter.notifyDataSetChanged();
						dialogfetchdata.dismiss();
						global.setFirstTimeExecution(false);
						eventsListView.setVisibility(View.VISIBLE);
						}
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

    


