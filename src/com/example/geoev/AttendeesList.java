package com.example.geoev;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

public class AttendeesList extends Activity {

	//Facebook facebook = new Facebook("424405194241987");

	Dialog dialog;

	ArrayList<FBFriend> friends;
	ArrayList<String> mySelectedFriends = new ArrayList<String>();
	
	AttendeesListAdapter friendListAdapter;
	ListView friendListView;
	private String eid = "";
	private StringBuffer invited = new StringBuffer();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.attendees_list);
		//Get facebook access info
		final Intent recieve_intent = getIntent();
		String access_token = recieve_intent.getStringExtra(Constants.bundle_Access_Token);
		long expires = recieve_intent.getLongExtra(Constants.bundle_Access_Expires, 0);
		if(access_token != null) Utility.mFacebook.setAccessToken(access_token);
		if(expires != 0) Utility.mFacebook.setAccessExpires(expires);

		Bundle extras = getIntent().getExtras();
        eid = extras.getString("eid");
		
		friends = new ArrayList<FBFriend>();

		//Show dialog to distract user while downloading friends
		dialog = new ProgressDialog(this);
		dialog.show();
		//dialog.setContentView(R.layout.custom_progress_dialog);

		
		
	

		//Load friends if cached
		String jsonFriends = Tools.getStringFromFile(getFriendsJSONCacheFile());
		if(jsonFriends != null){
			parseJSONFriendsToArrayList(jsonFriends,friends);
			dialog.cancel();

			for(FBFriend f : friends){
				File file = new File(getCacheDir(), f.getUID());
				Bitmap b = Tools.getBitmapFromFile(file);
				if(b != null) f.setProfilePic(b);
			}
		}
		
		
		friendListAdapter = new AttendeesListAdapter(AttendeesList.this ,friends);
		friendListView = (ListView) findViewById(R.id.friend_list);
		//friendListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
		friendListView.setAdapter(friendListAdapter);

		
		

		//Download all friends
		downloadFacebookFriends_async();
		
		
		
		//
		friendListView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {
				
				FBFriend friend = (FBFriend) parent.getItemAtPosition(position);
			    Log.v("eros", friend.getName().toString());
				Toast.makeText(getApplicationContext(),
			      "Clicked on Row: " + friend.getName(), 
			      Toast.LENGTH_LONG).show();
			
				/*CheckBox chk = (CheckBox) view.findViewById(R.id.chkfriend);
				FBFriend friend = friends.get(position);
				
				if (friend.isSelected()){
					friend.setSelected(false);
					chk.setChecked(false);
				} else {
					friend.setSelected(true);
					chk.setChecked(true);
				}
				
				AlertDialog.Builder adb = new AlertDialog.Builder(
						FriendList.this);
				        
				        adb.setTitle("Test");
						adb.setPositiveButton("Ok", null);
						adb.setPositiveButton("Ok", null);
						adb.show();     
				
						//parent.getItemAtPosition(position);*/
			}
		});
		
		
           	
}
	

	//Download friends and put them in cache
	protected void downloadFacebookFriends_async() {
		new Thread(new Runnable() {
			@Override
			public void run() {

				//Get all friends with FQL
				 String query_attendees = "SELECT uid, name, pic_square FROM user WHERE uid IN (select uid from event_member where eid = " + eid + " AND rsvp_status = \"attending\")";
		            Bundle params = new Bundle();
		            params.putString("method", "fql.query");
		            params.putString("query", query_attendees);
				try {
					String response = Utility.mFacebook.request(params);
					File file = new File(getCacheDir(), Constants.cache_JSON_Friends);
					Tools.saveStringToFile(response, file);

				} catch (MalformedURLException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}

				friendDownloadCompleteHandler.sendEmptyMessage(0);
			}


		}).start();
	}
	private void parseJSONFriendsToArrayList(String jsonFriends, ArrayList<FBFriend> arraylist) {
		try {
			JSONArray array = new JSONArray(jsonFriends);
			for(int i = 0; i < array.length(); i++){
				arraylist.add(new FBFriend(array.getJSONObject(i)));
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

	}

	protected Handler friendDownloadCompleteHandler = new Handler() {
		@Override
		public void handleMessage(Message msg){

			if(updateToLatestFriendList() || friends.size() == 0){
				//TODO: should notify user
				friendListAdapter = new AttendeesListAdapter(AttendeesList.this, friends);
				friendListView.setAdapter(friendListAdapter);
			}

			downloadProfilePictures_async();
			dialog.cancel();

		}
	};

	protected void downloadProfilePictures_async() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				for(int i = 0; i < friends.size(); i++){
					int bestIndex = getBestFriend(i);
					final FBFriend f = friends.get(bestIndex);

					if(f.hasDownloadedProfilePicture()) continue;

					File file = new File(getCacheDir(), f.getUID());
					Bitmap picture = Tools.getBitmapFromFile(file); 

					if(picture == null){
						picture = Tools.downloadBitmap(f.getProfilePictureURL());
					}
					if(picture != null){
						f.setProfilePic(picture);
						Tools.storePictureToFile(file, picture);
					}

					updateProfilePictureAtIndex(f, bestIndex);

					//need to retry to download the old picture
					if(bestIndex != i) i--;
				}

			}

			//Updates the picture in the listView
			private void updateProfilePictureAtIndex(final FBFriend f, final int index) {
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						View v = friendListView.getChildAt(index - friendListView.getFirstVisiblePosition());
						if(v != null){
							ImageView iv = (ImageView) v.findViewById(R.id.profile_picture);
							if(f.hasDownloadedProfilePicture()){
								iv.setImageBitmap(f.getProfilePicture());
							}
						}
					}
				});
			}

			//Simple method to find the best friend to download right now (one that the user is looking at is better than one that is not visible)
			private int getBestFriend(int index) {
				int viewingIndex = friendListView.getFirstVisiblePosition();
				int lastViewingIndex = friendListView.getLastVisiblePosition();
				while(viewingIndex <= lastViewingIndex && viewingIndex <= friends.size()){
					if(!friends.get(viewingIndex).hasDownloadedProfilePicture()){
						index = viewingIndex;
						break;
					}
					viewingIndex++;
				}

				return index;
			}

		}).start();

	}

	//Updates friends to friends found in cache (returns true if updated)
	protected boolean updateToLatestFriendList() {
		ArrayList<FBFriend> newFriendList = new ArrayList<FBFriend>();
		String jsonFriends = Tools.getStringFromFile(getFriendsJSONCacheFile());
		parseJSONFriendsToArrayList(jsonFriends, newFriendList);

		if(!friendListsAreEqual(friends,newFriendList)){
			friends = newFriendList;
			return true;
		}

		return false;
	}

	private boolean friendListsAreEqual(ArrayList<FBFriend> a, ArrayList<FBFriend> b) {
		if(a.size() != b.size()) return false;
		for(int i = 0; i < a.size(); i++) if(!a.get(i).equals(b.get(i))) return false;
		return true;
	}

	private File getFriendsJSONCacheFile(){
		return new File(getCacheDir(), Constants.cache_JSON_Friends);
	}
	
	//async task to invite friends to geoevent
	class Invite extends AsyncTask<String, String, String>{
		 Context mContext;
		 
		 public Invite(Context context){
				super();
				mContext = context;
		       
			}
		   
		 ProgressDialog invitationDialog;
		    
		    protected void onPreExecute() {
		    	invitationDialog = new ProgressDialog(AttendeesList.this);
		    	invitationDialog.setMessage("Your invitations is sending, please wait...");
		    	invitationDialog.show();
		    }
		    
		    
		    @Override
		    protected String doInBackground(String... invited) {
		        HttpClient httpclient = new DefaultHttpClient();
		        //HttpResponse response;
		        //HttpPost httppost;
		        String responseString = null;
		        String response = null;
		        Log.v("SVISO", "BOYYYUUKA!!!!!!!!!!!!!!!!!");
		        
		        
		        for(int h=0; h<mySelectedFriends.size();h++){
        		Bundle extras = getIntent().getExtras();
                //String eid = extras.getString("eid");
        		Bundle invitation = new Bundle(); 
    			invitation.putString("method", "events.invite"); 
    			invitation.putString("eid", eid); 
    			invitation.putString("uids", mySelectedFriends.get(h).toString()); 
    			try {
					String responseInvitation = Utility.mFacebook.request(invitation);
    				Log.v("einai",responseInvitation);
				} catch (MalformedURLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
        	}
		        
		        
		        try {
		        	 Bundle parameters = new Bundle();
		             parameters.putString("", "");
		             response = Utility.mFacebook.request(eid + "/invited?users=" + invited[0], parameters, 
		                        "POST");
		                Log.d("Tests", "got response: " + response);
		                if (response == null || response.equals("") || 
		                        response.equals("false")) {
		                   Log.v("Error", "Blank response");
		                }
		        	
	         } catch(Exception e) {
	             e.printStackTrace();
	         
		        }
		       // return responseString;
		       return response;
		    }

		    @Override
		    protected void onPostExecute(String response) {
		        super.onPostExecute(response);
		        invitationDialog.dismiss();
		        Toast.makeText(getBaseContext(), 
		                "Your invitation request has been completed", 
		                 Toast.LENGTH_SHORT).show();
		        Intent myNewIntent = new Intent(getApplicationContext(), Geoevent.class);
		        myNewIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		        startActivity(myNewIntent);
		    }
		}

}