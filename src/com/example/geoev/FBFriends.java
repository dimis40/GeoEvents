package com.example.geoev;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;


public final class FBFriends {
	
	private ArrayList<Person> persons = new ArrayList<Person>();
	public static final String TAG = "FBFriends";
	
	ArrayList <Person> getClickedPersons = new ArrayList <Person>();
	
	//Constructor
	public FBFriends() {
		//Call the graph Api
		Utility.mAsyncRunner.request("me/friends", new FriendsRequestListener());
		Log.v("INFO", "REQUEST FRIENDS");
		
	}
	
	/*
     * Callback for fetching user's facebook friends
     */
    public class FriendsRequestListener extends BaseRequestListener {

		public void onComplete(String response, Object state) {
			try {
				// process the response here: executed in background thread
				Log.d(TAG, "Response: " + response.toString());
				final JSONObject json = new JSONObject(response);
				JSONArray d = json.getJSONArray("data");
 
				for (int i = 0; i < d.length(); i++) {
					JSONObject person = d.getJSONObject(i);
					Person newPerson = new Person(person.getString("id"),
							person.getString("name"));
							
					persons.add(newPerson);
 
				}
 
				
			} catch (JSONException e) {
				Log.w(TAG, "JSON Error in response");
			}
		}

    }
	
		public ArrayList <Person> getPersons(){
		return persons;
		}
}
