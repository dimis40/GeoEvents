package com.example.geoev;

import java.io.IOException;
import java.net.MalformedURLException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.facebook.android.FacebookError;
import com.facebook.android.Util;



public class CreateEvent extends Activity implements Runnable {
	private boolean ok = false; 
	private Button btnEvent;
	private EditText title;
	private String titleText;
	private TextView invitation_message;
	private EditText location;
	private String locationText;
	private EditText message;
	private String messageText;
	private String facebookStartDate = null; 
	private String facebookEndDate = null; 
	private FBFriends fbfriends;
	//private Session singleton = null;
	private ProgressDialog progress = null; 
	private boolean startDay = false;
	private boolean endDay = false;
	private boolean startTime = false; 
	private boolean endTime = false;
	//
	private SharedPreferences sp;
	private boolean cb1;
	
	//private String latitude = "0";
	//private String longitude = "0";
	private Button btnCheckLocation;
	private String sStartHour; 
	private String sStartMinute; 
	private String sEndHour; 
	private String sEndMinute; 
	private String sStartYear; 
	private String sStartMonth; 
	private String sStartDay; 
	private String sEndYear; 
	private String sEndMonth; 
	private String sEndDay;
	private static final String TAG = "Create Event";
	public static final String NEW_MSG_EVENT = "com.example.geov.NEW_MSG_EVENT";
	private GpsReceiver myReceiver;
	//
	private Integer lat = 0;
	private Integer lont = 0;

	//Variables, Button and TextView to calculate and display the start time referenced and initializer
	private TextView mPickStartTimeDisplay; 
	private Button mPickStartTime;
	private int mStartHour;
	private int mStartMinute;
	static final int TIME_DIALOG_ID = 0;
	
	//Variables, Button and TextView to calculate and display the start date referenced and initializer
	private TextView mStartDateDisplay; 
	private Button mPickStartDate; 
	private int mStartYear;
	private int mStartMonth;
	private int mStartDay;
	static final int DATE_DIALOG_ID = 1;
	
	//Variables, Button and TextView to calculate and display the end time referenced and initializer
	private TextView mEndTimeDisplay; 
	private Button mPickEndTime; 
	private int mEndHour;
	private int mEndMinute;
	static final int TIME_END_DIALOG_ID = 2;
		
	//Variables, Button and TextView to calculate and display the end date referenced and initializer
	private TextView mEndDateDisplay; 
	private Button mPickEndDate;
	private int mEndYear;
	private int mEndMonth;
	private int mEndDay;
	static final int DATE_END_DIALOG_ID = 3;
	
	
@Override
protected void onCreate(Bundle savedInstanceState) {
	// TODO Auto-generated method stub
	super.onCreate(savedInstanceState);
	setContentView(R.layout.create_event);
	Log.v("INFO", "FacebookEvent.onCreate()");
	
	 myReceiver = new GpsReceiver();
	 IntentFilter filter = new IntentFilter(NEW_MSG_EVENT);
	 registerReceiver(myReceiver, filter);
	
	fbfriends = new FBFriends();
	
    //
	invitation_message = (TextView) findViewById(R.id.textFacebook);
	title = (EditText) findViewById(R.id.editTextTitle);
    message = (EditText) findViewById(R.id.editTextMessage);
    location = (EditText) findViewById(R.id.editTextLocation);
    //
    
    mPickStartTimeDisplay = (TextView) findViewById(R.id.timeStartDisplay); 
    mPickStartTime = (Button) findViewById(R.id.pickStartTime);
    mEndTimeDisplay = (TextView) findViewById(R.id.timeEndDisplay); 
    mPickEndTime = (Button) findViewById(R.id.pickEndTime); 
    mStartDateDisplay = (TextView) findViewById(R.id.dateStartDisplay); 
    mPickStartDate = (Button) findViewById(R.id.pickStartDate); 
    mEndDateDisplay = (TextView) findViewById(R.id.dateEndDisplay); 
    mPickEndDate = (Button) findViewById(R.id.pickEndDate);
    btnEvent = (Button) findViewById(R.id.buttonSender);
    
    //I use an if case because the first time the activity created there is no message from other
    //activities, particularly the LocateMe.class
    Bundle extras = getIntent().getExtras();
    if (extras != null) {
    location.setText(extras.getString("Address"));
    }
    
    //
    //shared preferences
        sp = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
	    cb1 = sp.getBoolean("invite_friends", true);
	    //set invite text
	    if (cb1 == true){
	    	invitation_message.setText("Here you will create a Facebook event. The persons from the list will automatically notified:");
	    }
    
    //Methods for setting start and end
    getStartTime(); 
    getEndTime(); 
    getStartDate(); 
    getEndDate();
   
    
    //Register Listener for Button mPickStartTime
    mPickStartTime.setOnClickListener(new OnClickListener() {
		
    	/*
    	* like http://developer.android.com/reference/android/view/View.html
    	*/
		@Override
		public void onClick(View v) {
			showDialog(TIME_DIALOG_ID);
			
		}
	});
    
    
  //Register Listener for Button mPickStartDate
    mPickStartDate.setOnClickListener(new OnClickListener() {
		
    	/*
    	* like http://developer.android.com/reference/android/view/View.html
    	*/
		@Override
		public void onClick(View v) {
			showDialog(DATE_DIALOG_ID);
			
		}
	});

    
  //Register Listener for Button mPickEndTime
    mPickEndTime.setOnClickListener(new OnClickListener() {
		
    	/*
    	* like http://developer.android.com/reference/android/view/View.html
    	*/
		@Override
		public void onClick(View v) {
			showDialog(TIME_END_DIALOG_ID);
			
		}
	});

  //Register Listener for Button mPickEndDate
    mPickEndDate.setOnClickListener(new OnClickListener() {
		
    	/*
    	* like http://developer.android.com/reference/android/view/View.html
    	*/
		@Override
		public void onClick(View v) {
			showDialog(DATE_END_DIALOG_ID);
			
		}
	});

  //Register Listener for Button btnEvent
    btnEvent.setOnClickListener(new OnClickListener() {
		
    	/*
    	* like http://developer.android.com/reference/android/view/View.html
    	*/
		@Override
		public void onClick(View v) {
			//Reading the text boxes
			titleText = title.getText().toString();
			locationText = location.getText().toString();
			messageText = message.getText().toString();
			//na ta do kalytera
			createFacebookTimeScheme();
			//setConnection();
			
			//Check whether all fields and date / time has been set
			if((startTime == false) | (startDay == false) | (endDay == false) | (endTime == false) | (titleText.equals("")) | (locationText.equals("")) | (messageText.equals(""))){
				Toast.makeText(getApplicationContext(), "Please fiil in all filelds and choose time", Toast.LENGTH_LONG).show();
			}
			else{
			progress = ProgressDialog.show(CreateEvent.this, "Please wait ... "," event is sent!", true); 
				//Das absenden des Events wird in einem Thread realisiert
				Thread thread = new Thread(CreateEvent.this);
				thread.start();
			}
		}
	}); 
    
  //Register Listener for editText Location
    location.setOnClickListener(new OnClickListener() {
		
    	/*
    	* like http://developer.android.com/reference/android/view/View.html
    	*/
		@Override
		public void onClick(View v) {
			registerForContextMenu(v);
            openContextMenu(v);
			//Intent intent = new Intent(CreateEvent.this, LocateMe.class);
	        //startActivity(intent);
			
		}
	}); 
}

//
@Override
public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
    super.onCreateContextMenu(menu, v, menuInfo);
    menu.setHeaderTitle(R.string.menu_title);
    MenuInflater inflater = getMenuInflater();
    inflater.inflate(R.menu.actions, menu);
}

@Override
public boolean onContextItemSelected(MenuItem item) {
    switch(item.getItemId()) {
        case(R.id.cnt_mnu_tap):
            if(item.isChecked()) {
                item.setChecked(false);
            } else {
                item.setChecked(true);
                Toast.makeText(this, "Tap", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(CreateEvent.this, GetLocationByTouch.class);
    	        startActivity(intent);
            }
            break;
        case(R.id.cnt_mnu_address):
            if(item.isChecked()) {
                item.setChecked(false);
            } else {
                item.setChecked(true);
                Toast.makeText(this, "Address", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(CreateEvent.this, LocateMe.class);
    	        startActivity(intent);
            }
            break;
        case(R.id.cnt_mnu_current):
            if(item.isChecked()) {
                item.setChecked(false);
            } else {
                item.setChecked(true);
                Toast.makeText(this, "current", Toast.LENGTH_SHORT).show();
                Double[] lat_long = new Double[] { lat/1E6, lont/1E6 };
                //Double[] lat_long = new Double[] { latitude, longitude};
                
            	// Executing ReverseGeocodingTask to get Address
            	new ReverseGeocodingTask(getBaseContext()).execute(lat_long);
                
            }
            break;
        default:
            return super.onContextItemSelected(item);
    }
    return true;
}





//Set the current system time as a start time of the preliminary event
public void getStartTime() {
	Log.v("INFO", "FacebookEvent.getStartTime()");
	final Calendar Starttime = Calendar.getInstance(); 
	mStartHour = Starttime.get(Calendar.HOUR_OF_DAY); 
	mStartMinute = Starttime.get(Calendar.MINUTE); 
	sStartHour = (new Integer(mStartHour).toString()); 
	sStartMinute = (new Integer(mStartMinute).toString()); 
	startTime = true;
	updateDisplayStartTime();
	
}


//Set the current system time as the end time of the preliminary event
public void getEndTime() {
	Log.v("INFO", "FacebookEvent.getEndTime()");
	final Calendar Endtime = Calendar.getInstance(); 
	mEndHour = Endtime.get(Calendar.HOUR_OF_DAY); 
	mEndMinute = Endtime.get(Calendar.MINUTE); 
	sEndHour = (new Integer(mEndHour).toString()); 
	sEndMinute = (new Integer(mEndMinute).toString()); 
	endTime = true;
	updateDisplayEndTime();
	
}




//Set the current system date as the date of the preliminary event
public void getStartDate() {
	Log.v("INFO", "FacebookEvent.getStartDate()");
	final Calendar Startdate = Calendar.getInstance(); 
	mStartYear = Startdate.get(Calendar.YEAR);
	mStartMonth = Startdate.get(Calendar.MONTH);
	mStartDay = Startdate.get(Calendar.DAY_OF_MONTH); 
	sStartYear = (new Integer(mStartYear).toString()); 
	sStartMonth = (new Integer(mStartMonth+1).toString()); 
	sStartDay = (new Integer(mStartDay).toString()); 
	startDay = true;
	updateDisplayStartDate();
	
}




//Set the current system date as the end date of the preliminary event
public void getEndDate() {
	Log.v("INFO", "FacebookEvent.getEndDate()");
	final Calendar Enddate = Calendar.getInstance(); 
	mEndYear = Enddate.get(Calendar.YEAR);
	mEndMonth = Enddate.get(Calendar.MONTH);
	mEndDay = Enddate.get(Calendar.DAY_OF_MONTH); 
	sEndYear = (new Integer(mEndYear).toString()); 
	sEndMonth = (new Integer(mEndMonth+1).toString()); 
	sEndDay = (new Integer(mEndDay).toString()); 
	endDay = true;
	updateDisplayEndDate();	
}

//Method assembles a string that stands for outstanding the start and end point of the event
public void createFacebookTimeScheme(){
Log.v("INFO", "Event.createFacebookTimeScheme()");
facebookStartDate = sStartYear+"-"+sStartMonth+"-"+sStartDay+"T" + sStartHour + ":" + sStartMinute + ":00"; 
facebookEndDate = sEndYear+"-"+sEndMonth+"-"+sEndDay+"T" + sEndHour + ":" + sEndMinute + ":00";
}

//Callback method that is executed when the user sets a time in the Time Picker dialog
private TimePickerDialog.OnTimeSetListener mTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
	
	@Override
	public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
		// TODO Auto-generated method stub
		mStartHour = hourOfDay;
		mStartMinute = minute;
		sStartHour = (new Integer(mStartHour).toString());
		sStartMinute = (new Integer(mStartMinute).toString());
		startTime = true;
		updateDisplayStartTime();
	}
};

//Callback method that is executed when the user sets a time in the Time Picker dialog
private TimePickerDialog.OnTimeSetListener mTimeEndSetListener = new TimePickerDialog.OnTimeSetListener() {
	
	@Override
	public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
		// TODO Auto-generated method stub
		mEndHour = hourOfDay;
		mEndMinute = minute;
		sEndHour = (new Integer(mEndHour).toString());
		sEndMinute = (new Integer(mEndMinute).toString());
		endTime = true;
		updateDisplayEndTime();
	}
};

//Callback method that is executed when the user sets a time in the Date Picker dialog
private DatePickerDialog.OnDateSetListener mDateEndSetListener = new DatePickerDialog.OnDateSetListener() {
	
	@Override
	public void onDateSet(DatePicker view, int year, int monthOfYear,
			int dayOfMonth) {
		// TODO Auto-generated method stub
		mEndYear = year;
		mEndMonth = monthOfYear;
		mEndDay = dayOfMonth;
		
		sEndYear = (new Integer(mEndYear).toString());
		sEndMonth = (new Integer(mEndMonth+1).toString());
		sEndDay = (new Integer(mEndDay).toString());
		endDay = true;
	
		updateDisplayEndDate();
		
	}
};

//Callback method that is executed when the user sets a time in the Date Picker dialog
private DatePickerDialog.OnDateSetListener mDateSetListener = new DatePickerDialog.OnDateSetListener() {
	
	@Override
	public void onDateSet(DatePicker view, int year, int monthOfYear,
			int dayOfMonth) {
		// TODO Auto-generated method stub
		mStartYear = year;
		mStartMonth = monthOfYear;
		mStartDay = dayOfMonth;
		
		sStartYear = (new Integer(mStartYear).toString());
		sStartMonth = (new Integer(mStartMonth+1).toString());
		sStartDay = (new Integer(mStartDay).toString());
		startDay = true;
	
		updateDisplayStartDate();
		
	}
};

//Select which dialogue should be started
protected Dialog onCreateDialog (int id){
	switch (id) {
	case TIME_DIALOG_ID:
		return new TimePickerDialog(this, mTimeSetListener, mStartHour, mStartMinute, false);
	case DATE_DIALOG_ID:
	return new DatePickerDialog(this, mDateSetListener, mStartYear, mStartMonth, mStartDay);
	case TIME_END_DIALOG_ID:
	return new TimePickerDialog(this, mTimeEndSetListener, mEndHour, mEndMinute, false);
	case DATE_END_DIALOG_ID:
	//return new DatePickerDialog(this, mDateEndSetListener, mStartYear, mStartMonth, mStartDay);
		return new DatePickerDialog(this, mDateEndSetListener, mEndYear, mEndMonth, mEndDay);
	}
	return null;
}



//Adds a zero before the number, if it only consists of a number
private static String pad(int c) {
if (c >= 10)
return String.valueOf(c);
else
	return "0" + String.valueOf(c);
}

@Override
public void run() {
	Log.v("INFO", "Event.run()");
	Bundle data = new Bundle();
	Message msg = new Message();
	ok = setEvent(titleText, locationText, messageText, facebookStartDate, facebookEndDate, fbfriends.getPersons());
	//pareleipsa auto singleton.getPersonen()
	data.putInt("status", 2); msg.setData(data); handler.sendMessage(msg);
	
}

private Handler handler = new Handler(){
//like http://developer.android.com/reference/android/os/Handler.html
	public void handleMessage(Message msg){
		Log.v("INFO", "Event.Handler()");
		progress.dismiss();
		if (ok){
		Toast.makeText(getApplicationContext(), "Successfully uploaded event", Toast.LENGTH_LONG).show();
		Intent myNewIntent = new Intent(getApplicationContext(), Geoevent.class);
        startActivity(myNewIntent);
		}else{
		Toast.makeText(getApplicationContext(), "Failed to upload the event", Toast.LENGTH_LONG).show();
		}
		}
};

private void updateDisplayStartTime() {
	mPickStartTimeDisplay.setText(new StringBuilder()

	.append("Start Date / Time")
	.append(pad(mStartHour))
	.append(":") 
	.append(pad(mStartMinute)));
}

private void updateDisplayStartDate() {
	mStartDateDisplay.setText(new StringBuilder()
	.append(" ")
	.append(mStartDay)
	.append(".") 
	.append(mStartMonth + 1)
	.append(".") 
	.append(mStartYear)
	.append(" "));
	
	
}

private void updateDisplayEndTime() {
	mEndTimeDisplay.setText(new StringBuilder()
	.append("End Date / Time")
	.append(pad(mEndHour))
	.append(":") 
	.append(pad(mEndMinute)));
	
}


private void updateDisplayEndDate() {
	mEndDateDisplay.setText(new StringBuilder()
	
	.append(" ") 
	.append(mEndDay)
	.append(".") 
	.append(mEndMonth + 1)
	.append(".") 
	.append(mEndYear)
	.append(" "));
	
}

//gia na doume
public boolean setEvent(String title, String location, String message, String startTime, String endTime, ArrayList<Person> friends){
// prepei na kano ArrayList<Person> friends
	Log.v("INFO", "FacebookConnectionActivity.setEvent()"); String response = null;
    String responseInvitation = null;
    String eventId = null;
    try {
    	response = Utility.mFacebook.request("me");
    } catch (MalformedURLException e1) { Log.v("INFO", e1.toString());
    } catch (IOException e1) { Log.v("INFO", e1.toString());
    }
    //Parameters that are required
    Bundle parameters = new Bundle();
    parameters.putString("name", title);
    parameters.putString("location", location);
    parameters.putString("description", "GeoEvent : " + message);
    parameters.putString("start_time", startTime);
    Log.v("INFO", startTime);
    parameters.putString("end_time", endTime);
    //parameters.putString("picture", "https://foursquare.com/mapproxy/59.3280/18.0506/map.png");

    Log.v("INFO", endTime);
    parameters.putString("privacy_type", "OPEN");
    /*parameters.putString("street", "Antonopoulou 165");
    parameters.putString("city", "Volos");
    parameters.putString("country", "Greece");
    parameters.putString("latitude", "39.3043905");
    parameters.putString("longitude", "23.115921");*/
    //parameters.putString("page_id", "04005829635705");
    parameters.putString("tagline", "GeoEvent");
    //if (isSession()){
    	Log.v("INFO", "FacebookEvent -> sessionValid");
    	try {
    		response = Utility.mFacebook.request("me/events", parameters, "POST");
    		JSONObject json = Util.parseJson(response);
    		Log.v("INFO", "!!event ID!!"+response);
    		eventId = json.getString("id");
    //if id  = null
    		if(eventId == null) return false;
    		Log.v("INFO", "Event ID in JSON:"+ eventId);
    		
    		//S.O.S tha to energopoiiso sto teliko stadio gia
    		//invitation gia na min kourazo olous
    		 if (cb1 == true){
    		for(int h=0; h<friends.size();h++){
    			Bundle invitation = new Bundle(); 
    			invitation.putString("method", "events.invite"); 
    			invitation.putString("eid", eventId); 
    			invitation.putString("uids", friends.get(h).getId()); 
    			responseInvitation = Utility.mFacebook.request(invitation);
    			}
    		 }
    //
    		 Bundle picture_parameters = new Bundle();
    		 picture_parameters.putString("source", "https://foursquare.com/mapproxy/59.3280/18.0506/map.png");
    		 response = Utility.mFacebook.request(eventId + "/picture", picture_parameters, "POST");
    		 JSONObject myjson = Util.parseJson(response);
     		 Log.v("INFO", "!!!picture posted "+ response);
    		 
    } catch (JSONException e) {
    	Log.v("INFO", "JSONException: " + e.getMessage());
    	return false;
    	} catch (FacebookError e) {
    	Log.v("INFO", "FacebookError: " + e.getMessage());
    	return false;
    	} catch (MalformedURLException e) {
    	Log.v("INFO", "MalformedURLException: " + e.getMessage());
    	return false;
    	} catch (IOException e) {
    		Log.v ("INFO","IOException: " +e.getMessage() );
    		return false;
    	}
    //}else{
    	//getFacebook().authorize(this, PERMS, Facebook.FORCE_DIALOG_AUTH, new LoginDialogListener());
    	//return false;
  //  }
    return true;
	}

private class GpsReceiver extends BroadcastReceiver {
	
	  @Override
		public void onReceive(Context context, Intent intent) {
			showDataFromIntent(intent);
		}

		private void showDataFromIntent(Intent intent) {
			//shows up in the log file
			lat = (int) (intent.getDoubleExtra(
					"Latitude", 0) * 1E6);
			lont = (int) (intent.getDoubleExtra("Longtitude", 0) * 1E6);
			 
			 Log.v("INFO","@##$$%^%&^&^**&&((&(&:&*(()!!!!!!!! ");
			 
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
	String return_string = deg + "¡ : " + minnutes + "' : " + seconds
			+ "''";

	return return_string;
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
		
		List<Address> addresses = null;
		//StringBuilder addressText = new StringBuilder("");
		String addressText="";
		//String GeoAddress = "";
		try {
			addresses = geocoder.getFromLocation(latitude, longitude,1);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		if(addresses != null && addresses.size() > 0 ){
			Address address = addresses.get(0);
			
			/*addressText.append(address.getAddressLine(0)).append(",").
            append(address.getLocality()).append(",").
            append(address.getCountryName()).append(",").
            append(String.valueOf(lat)).append(";").	
            append(String.valueOf(lont));*/
			
			//GeoAddress = addressText.toString();
			addressText = String.format("Address:%s, Locality:%s, Country:%s, Coordinates:%s,%s",
                   // address.getMaxAddressLineIndex() > 0 ?address.getAddressLine(0) : "",
					address.getMaxAddressLineIndex() > 0 ? address.getAddressLine(0) : "",
                    address.getLocality(),	                    
                    address.getCountryName(),
                    String.valueOf(lat),
                    String.valueOf(lont)
                    );			
		}
		
		return addressText;
	}		
	
	@Override
	protected void onPostExecute(String addressText) {			
		// Setting address of the touched Position
		Toast.makeText(getBaseContext(), 
               addressText , 
                Toast.LENGTH_SHORT).show();
    	location.setText(addressText);
	}
	
}



}

