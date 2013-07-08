package com.example.geoev;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

public class FbFeeds extends Activity{
	//
	private ProgressDialog dialogfetchdata;
	private Context context = this;
	//
	 private DatabaseHelper singleton;
	 private List<FbFeed> fbFeeds = new ArrayList<FbFeed>();
	 //final ListAdapter listAdapter = getListAdapter();
	
	public static final String TAG = "FbFeeds";
	//i uses this boolean in order to get on click item correctly
	//in base of i have an adapter with freshed or cached data
	public Boolean hasCashedData = false;
	private String eid = "";
	
	public FeedItemAdapter mAdapter;
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.feed);
        //
        Bundle extras = getIntent().getExtras();
        eid = extras.getString("eid");
        
        //
        dialogfetchdata = new ProgressDialog(context);
        dialogfetchdata.setMessage("fetching data, please wait...");
        dialogfetchdata.show();
        //
        singleton = DatabaseHelper.getInstance(getApplicationContext());
 
        //
 // if (FBService.gotFriends == false || singleton.lock.isLocked()){
        	//get fresh data
        	//Get Events Information using FQL
           
            Bundle params_feed = new Bundle();
            params_feed.putString("fields", "feed");
            Utility.mAsyncRunner.request(eid, params_feed,
            		new EventFeedRequestListener());
        
        
        
       mAdapter = new FeedItemAdapter(this, R.layout.feed_item, fbFeeds);
       
     
                    
	/*}
  else {
        	mAdapter = new FeedItemAdapter(this, R.layout.feed_item, singleton.getAllEventFeeds());
        	hasCashedData = true;
     }*/
       
        
        ListView eventfeedListView = (ListView) findViewById(R.id.feed_list);
       eventfeedListView.setAdapter(mAdapter);
        
       /* eventsListView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {
			
				//launchEventDetailsActivity((Event) parent
				FbEvent event = new FbEvent();
		        event = (FbEvent) parent.getItemAtPosition(position);
				Intent myNewIntent = new Intent(getApplicationContext(), EventInfo.class);
				myNewIntent.putExtra("eid", event.getId());
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
		//	}
	//	});
    }
	
	
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
		
	}

	
	//
public class EventFeedRequestListener extends BaseRequestListener {
        
        @Override
        public void onComplete(String response, Object state) {
                try {
                        // process the response here: executed in background thread
                        Log.d(TAG, "Response from Event feed: " + response.toString());
                        final JSONObject json = new JSONObject(response);
                        String id = json.getString("id");
                        JSONObject feed = json.getJSONObject("feed");
                        final JSONArray d = feed.getJSONArray("data");
                        for (int i = 0; i < d.length(); i++) {
                        JSONObject outer = d.getJSONObject(i);
                        String message = outer.getString("message");
                        String post_id = outer.getString("id");
                        String created_time = outer.getString("created_time");
                        JSONObject from = outer.getJSONObject("from");
                        String uid = from.getString("id");
                        String name = from.getString("name");
                        //JSONArray from = outer.getJSONArray("from");
                        //JSONObject from_array = from.getJSONObject(0);
                       // String name = from_array.getString("name");
                        /*JSONObject feed = outer.getJSONObject("feed");
                        JSONArray data_int = feed.getJSONArray("data");
                        for (int j = 0; j < data_int.length(); j++) {
                        JSONObject inner = data_int.getJSONObject(j);
                        //JSONObject myMessage = data_int.getJSONObject(j);
                        //JSONArray from = data_int.getJSONArray(1);
                        String message = inner.getString("message");
                        String created_time = inner.getString("created_time");
                        String ide = inner.getString("id");
                        JSONObject from = inner.getJSONObject("from");
                        String user_id = from.getString("id");*/
                        Log.d(TAG, "message from:....................//.... "  + post_id + message +  created_time + " " + uid + " " + name);
                       // eventFeedMessages.add(id + " "+ message + " " + user_id + " " + created_time);
                        
                       FbFeed newFbFeed = new FbFeed(id, message, post_id, created_time, uid, name);
                       fbFeeds.add(newFbFeed);
                       
                      }
            			
            			
                      //very important etsi ananeono to periexomeno tou adapter
        				FbFeeds.this.runOnUiThread(new Runnable() {
        					public void run() {
        						if (dialogfetchdata != null){
        						mAdapter.notifyDataSetChanged();
        						dialogfetchdata.dismiss();
        						}
        						
        					}
        				});


                } catch (JSONException e) {
                        Log.w(TAG, "JSON Error in response");
                        //yes it is working
                        dialogfetchdata.dismiss();
                }
        }
    

}



	
}
