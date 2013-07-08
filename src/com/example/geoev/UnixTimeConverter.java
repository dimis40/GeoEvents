package com.example.geoev;

import java.util.TimeZone;

import android.text.format.DateFormat;
import android.util.Log;

public class UnixTimeConverter {
	private String inputTime = "";
	private String outputTime ="";
	
	private String convertedTime = "";
	private Long unix_date;
	private Long myUnix_date = new Long(1358760539) ;
	private Long current_unix_time;
	private Long finalTime;
	
	private Long gmtTime;
	private Integer gmtOffset;
	
	
	private static final String TAG = "UnixEpochConverter";
	

	public String convertFromUnixToHuman(String _inputTime) {
		try {
			inputTime = _inputTime;
			
			
			//
			//TimeZone tz = TimeZone.getDefault();
			//TimeZone la = TimeZone.getTimeZone("America/Los_Angeles");
			
		    
			
			
			//unix_date = new Long(formatTimeForEvent(unix_long));
			unix_date = new Long(inputTime) - TimeDifference(inputTime);
			outputTime = (String) DateFormat.format("dd/MM/yyyy hh:mm:ssaa", unix_date * 1000L);

		} catch(Exception e) {
			Log.v("MYYYINFO", "No input detected");
		}
	return outputTime;
	
	}
	
	//convert fb epoch time to correct and return long
	public Long unixFBStringToLong(String _inputTime) {
		
		//initialize variable
		String s="";
		//Long myUnix_date = new Long(0);
		inputTime = _inputTime;
		try {
			
			
			//
			//TimeZone tz = TimeZone.getDefault();
			//TimeZone la = TimeZone.getTimeZone("America/Los_Angeles");
			
			//unix_date = new Long(formatTimeForEvent(unix_long));
			myUnix_date = new Long(inputTime) - TimeDifference(inputTime);
			//outputTime = (String) DateFormat.format("dd/MM/yyyy hh:mm:ssaa", unix_date * 1000L);
			s = String.valueOf(myUnix_date);
			Log.v("MALAKIATEST", s);
		} catch(Exception e) {
			Log.v("MYYYINFO", "No input detected");
			Log.v("MALAKIATEST", "DEN KANEI TIPOTA");
		}
	return myUnix_date;
	
	}
	
	
	
	//for testing purpose
	public String convertCurrentFromAndroidtoUnix() {
		 String s = "";
		try {
			  current_unix_time = System.currentTimeMillis();
		      long unix_time_truncated = (long) current_unix_time/1000;
		      
		      TimeZone tz = TimeZone.getDefault();
		      int offset = tz.getRawOffset()/1000;
		      String offsett = String.valueOf(offset);
		      Log.v("QWERTY", offsett);
		      //ousiastika prostheto 2 ores 3600*2 thelei douleia omos i ora akoma
		      long unix_time_correct = unix_time_truncated + offset;
		      s = String.valueOf(unix_time_correct);
		      /*Date date = new Date(System.currentTimeMillis());
		      SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
			  outputTime =  sdf.format(date);*/

		} catch(Exception e) {
			Log.v("MYYYINFO", "No input detected");
		}
	return s;
	
	}
	
	public boolean hasEventFinished(String currentTime, String FbEventTime){
		long current = new Long(currentTime);
		TimeZone tz = TimeZone.getDefault();
	    int offset = tz.getRawOffset()/1000;
		//auto me to 36000 prepei na to metatrepso se kati pio
		//long eventTime = new Long(FbEventTime)-36000+7200;
		long eventTime = new Long(FbEventTime) - TimeDifference(FbEventTime) + offset;
		String s = String.valueOf(eventTime);
		Log.v("MEGALIMALAKIA", s);
		if (current >= eventTime)
			return true;
		else
		return false;	
	}

	public static long formatTimeForEvent(long pacificTime) {
		return (pacificTime + TimeZone.getTimeZone("America/Los_Angeles").getOffset(pacificTime))
			- TimeZone.getDefault().getOffset(pacificTime);
	}	
	
public static int TimeDifference(String inputTime){
	Long unix_long = new Long(inputTime);
	int la = TimeZone.getTimeZone("America/Los_Angeles").getOffset(unix_long);
	int myOffset = TimeZone.getDefault().getOffset(unix_long);
	int time_difference = (myOffset-la)/1000;
	String s = String.valueOf(time_difference);
	Log.v("HIROHITO", s);
	return time_difference;
}

	
}
