package com.example.geoev;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

public class Opengraph extends IntentService{

	private DatabaseHelper singleton;
	private String eid = "";
	//private String event_title = "";
	private String eventLat = "";
	private String eventLng = "";
	private JSONArray jsonarray;
	private JSONObject jsonobject;
	private JSONObject parent;
	private static String json = "";
	private SharedPreferences sp ;
	private boolean cb1;
	
	private static final String TAG = "Opengraph";
	
	public Opengraph() {
	    super(TAG);
	    // TODO Auto-generated constructor stub
	}

	@Override
	public IBinder onBind(Intent arg0) {
	    // TODO Auto-generated method stub
	    return null;
	}

	@Override
	public void onCreate() {
	    // TODO Auto-generated method stub
	    super.onCreate();
	    singleton = DatabaseHelper.getInstance(getApplicationContext());
	    Log.e("Service Example", "Service Started.. ");
	    sp = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
	   // boolean cb1 = sp.getBoolean("cbp_checkbox_preference1", false);
	    cb1 = sp.getBoolean("pref_sync", true);
	    // pushBackground();

	}

	@Override
	public void onDestroy() {
	    // TODO Auto-generated method stub
	    super.onDestroy();
	    Log.e("Service Example", "Service Destroyed.. ");
	}

	@Override
	protected void onHandleIntent(Intent intent) {
	    // TODO Auto-generated method stub
		Bundle extras = intent.getExtras();
        eid = extras.getString("eid");
        eventLat = extras.getString("eventLat");
        eventLng = extras.getString("eventLng");
        //event_title = extras.getString("event_title");
        Log.e("Started service", "!!!!!!!//! eidd " + eid);
        JSONArray jsonarray = new JSONArray();
        
        JSONObject parent = new JSONObject();
        //
        for (int i=0;i<singleton.getAllGpsDataWithSpecificEid(eid).size();i++) {
        	
        	try{
        		jsonobject = new JSONObject();
        		jsonobject.put("eid", singleton.getAllGpsDataWithSpecificEid(eid).get(i).getEid());
        		jsonobject.put("event_title", singleton.getAllGpsDataWithSpecificEid(eid).get(i).getTitle());
        		jsonobject.put("latitude", singleton.getAllGpsDataWithSpecificEid(eid).get(i).getLatitude());
        		jsonobject.put("longitude", singleton.getAllGpsDataWithSpecificEid(eid).get(i).getLongitude());
        		jsonobject.put("altitude", singleton.getAllGpsDataWithSpecificEid(eid).get(i).getAltitude());
        		jsonobject.put("speed", singleton.getAllGpsDataWithSpecificEid(eid).get(i).getSpeed());
        		jsonobject.put("time", singleton.getAllGpsDataWithSpecificEid(eid).get(i).getTime());
        		jsonarray.put(jsonobject);
        		//jsonarray.put(jsonobject);
        		//parent.put("array", jsonarray);
        	}
        	catch (Exception e){
        		Log.d(TAG, "JSON ERROR");
        	}
        
        }
        	try {
				parent.put("array", jsonarray);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        
        	 Log.d(TAG, "Lets see " + parent.toString());
        	 
        	try {  
                // Create a new HttpClient and Post Header  
                HttpClient httpclient = new DefaultHttpClient();  
                HttpPost httppost = new HttpPost("http://geoevent.hostei.com/set.php");  
                // Post the data:  
                StringEntity se = new StringEntity(parent.toString(), "UTF-8");  
                se.setContentType("application/json;charset=UTF-8");
                httppost.setEntity(se);  
                //httppost.setEntity(new UrlEncodedFormEntity(se, "UTF-8");
                httppost.setHeader("Accept", "application/json");  
                httppost.setHeader("Content-type", "application/json");  
                // Execute HTTP Post Request  
                System.out.print(parent);  
                HttpResponse response = httpclient.execute(httppost);  
                // for JSON:  
                if(response != null)  
                {  
                     InputStream is = response.getEntity().getContent();  
                     BufferedReader reader = new BufferedReader(new InputStreamReader(is));  
                     StringBuilder sb = new StringBuilder();  
                     String line = null;  
                     try {  
                          while ((line = reader.readLine()) != null) {  
                          sb.append(line + "\n");  
                          }  
                     } catch (IOException e) {  
                          e.printStackTrace();  
                          }   
                     finally {  
                          try {
                        	  json = sb.toString().substring(0, 10);
                              Log.e("JSON", json);
                              if (cb1 == true){
                              Toast.makeText(getApplicationContext(), "Your Route has been posted on Facebook...", Toast.LENGTH_LONG).show();
                              postOnOpenGraph(json);
                              }
                               is.close();  
                          } catch (IOException e) {  
                                              e.printStackTrace();  
                                         }  
                                    }  
                               }  
                }  
                catch (ClientProtocolException e) {                 
                } catch (IOException e) {   
             }            
        
        
        	
       
        /*for (GpsData gpsdata : singleton.getAllGpsDataWithSpecificEid(eid)) {
        	Log.d(TAG, gpsdata.getTitle() + " " + gpsdata.getLatitude() + "!!!!!!");
        	try{
        		jsonobject.put("eid", eid);
        		jsonobject.put("event_title", gpsdata.getTitle());
        		jsonobject.put("latitude", gpsdata.getLatitude());
        		jsonobject.put("longitude", gpsdata.getLongitude());
        		jsonobject.put("altitude", gpsdata.getAltitude());
        		jsonobject.put("speed", gpsdata.getSpeed());
        		jsonobject.put("time", gpsdata.getTime());
        		jsonarray.put(jsonobject);
        	}
        	catch (Exception e){
        		Log.d(TAG, "JSON ERROR");
        	}
        	
        }*/
        
       
	    
	}
	
	
	//post on open graph
	  public void postOnOpenGraph(String graph_id) {
		  Log.d("Tests", "Testing Open graph API post");
		  try {
		  Bundle graphParams = new Bundle();
		 //graphParams.putString("route", "https://simple-night-1868.herokuapp.com/location.php?fb%3Aapp_id=338618139553202&og%3Atype=geoeventing%3Aroute&og%3Atitle=GeoEvent&og%3Aimage=+https%3A%2F%2Fs-static.ak.fbcdn.net%2Fimages%2Fdevsite%2Fattachment_blank.png&geoeventing%3Astartpoint%3Alatitude=" + startLat + "&geoeventing%3Astartpoint%3Alongitude=" + startLng +"&body=GeoEvent");
		 
		  graphParams.putString("route", "http://geoevent.hostei.com//retrieve.php?graph_id=" + graph_id);
		 // graphParams.putString("route", "https://simple-night-1868.herokuapp.com/location.php?fb%3Aapp_id=338618139553202&og%3Atype=geoeventing%3Aroute&og%3Atitle=GeoEvent&og%3Aimage=+https%3A%2F%2Fs-static.ak.fbcdn.net%2Fimages%2Fdevsite%2Fattachment_blank.png&geoeventing%3Astartpoint%3Alatitude=39.29354891&geoeventing%3Astartpoint%3Alongitude=23.13751019&body=GeoEvent");
		  String response = Utility.mFacebook.request("me/geoeventing:map", graphParams, "POST");
		  if (response == null || response.equals("") || 
                  response.equals("false")) {
             Log.v("Error", "Blank response");
          }
   } catch(Exception e) {
       e.printStackTrace();
   }
		  //Utility.mAsyncRunner.request("me/geoeventing:stay_on", graphParams, "POST", new addToTimelineListener(), null);
	  }
	
	
}
