package com.example.geoev;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;



/** 
 * Activity for letting a user to locate him/her-self by inputing the address 
 * 
 * This activity is used to let user input an incomplete address and then connect to the GoogleMap
 * service to validate the address with a list of result address. Then user can choose from the list to
 * set a current location. This activity also give user an option to disable the GPS location.  
 * 
 * @author XH 
 * @version 2011.1105 
 * @since 0.2 
 */
public class LocateMe extends Activity {
	private EditText etAddress;
	private Button btnLocateMe;
	private Button btnSave;
	private Button btnCancel;
	private TextView lblResult;
	private TextView txtResult;
	private Geocoder gc; // the geocoder object from Android map library
	
	private String mLocation_Lat=" ";
	private String mLocation_Lng=" ";
	private String mLocation_Address=" ";
	
	/* gia na do an xreiazontai
	/** 
     * the flag that tells if user has made changes to any settings in the current activity
     */
    public int iChangeFlag=0;
    
    /** 
     * Number of returned results from search
     */  
    private String mSet_Number="3";
	
	private int iUseGPSFlag=1; // the flag which tells if GPS location should be used or not. 
	ListView mLV;
	Vector<searchResult> vec_result=new Vector<searchResult>();
	//LIST OF ARRAY STRINGS WHICH WILL SERVE AS LIST ITEMS
	ArrayList<String> listItems=new ArrayList<String>();
	//DEFINING STRING ADAPTER WHICH WILL HANDLE DATA OF LISTVIEW
	ArrayAdapter<String> adapter;

	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.locateme);
     // Get the EditText and Button References
        etAddress = (EditText)findViewById(R.id.locateme_locationText);
        btnLocateMe = (Button)findViewById(R.id.locateme_btnlocate);
        //btnSave = (Button)findViewById(R.id.locateme_btnlocationOK);
        //btnCancel = (Button)findViewById(R.id.locateme_btnlocationCancel);
        gc = new Geocoder(this); //create new geocoder instance
        
        adapter=new ArrayAdapter<String>(this,
        	    android.R.layout.simple_list_item_1,
        	    listItems);
        // the following code defines the adapter for the List view
        mLV = (ListView) findViewById(R.id.list);
        mLV.setAdapter(adapter);
        mLV.setOnItemSelectedListener(new ResultsOnItemSelectedListener());
        mLV.setOnItemClickListener(new ResultsOnItemClickListener());        
    
        /** 
         * Method is triggered when Save button is clicked 
         * The location latitude and longitude, the address and the flag of using GPS or not, are saved and 
         * sent to the parent activity. 
         *  
         */  
    /*btnSave.setOnClickListener(new OnClickListener() {
	@Override
	public void onClick(View v) {
		CheckBox chkGPS=(CheckBox)findViewById(R.id.locateme_nogps);
    	if(chkGPS.isChecked()){
			 iUseGPSFlag=0;
        }
    	else{
    		iUseGPSFlag=1;
    	}
		//Intent resultIntent = new Intent();
    	Intent resultIntent = new Intent(LocateMe.this, CreateEvent.class);
		//resultIntent.putExtra("Latitude", mLocation_Lat);
		//resultIntent.putExtra("Longitude", mLocation_Lng);
		resultIntent.putExtra("Address", mLocation_Address);
		//resultIntent.putExtra("GPS_FLAG", String.valueOf(iUseGPSFlag));
		//setResult(Activity.RESULT_OK, resultIntent);
		resultIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(resultIntent);
		finish();
	}
});
    btnCancel.setOnClickListener(new OnClickListener() {
	@Override
	public void onClick(View v) {
		finish();
	}
});*/
    /** 
     * Method that is triggered when the LocateMe button is clicked. 
     * After user has inputed an address and clicked on LocateMe, the user'input is sent to the geocoder
     * service in GoogleMap to validate by the getFromLocationName method. 
     * The returned result is then put to a list which can be used by the user to choose from. 
     * 
     */  
    btnLocateMe.setOnClickListener(new OnClickListener() {
    	@Override
    	public void onClick(View v) {
    		String addressInput = etAddress.getText().toString(); //Get input text
            
            try {
              //prosoxi edo evala 5 gia na perioriso ta apotel;esmata                            
              List<Address> foundAdresses = gc.getFromLocationName(addressInput, 5); //Search addresses
                                           
              if (foundAdresses.size() <= 0) { //if no address found, display an error
             	 lblResult = (TextView)findViewById(R.id.locateme_lbllocation);
            	 txtResult = (TextView)findViewById(R.id.locateme_txtlocation);
            	 lblResult.setVisibility(lblResult.GONE);
            	 txtResult.setVisibility(txtResult.GONE);  
                Dialog locationError = new AlertDialog.Builder(LocateMe.this)
                  .setIcon(0)
                  .setTitle("Error")
                  .setPositiveButton("OK", null)
                  .setMessage("Sorry, your address doesn't exist.")
                  .create();
                locationError.show();
              }
              else { //else display address on map
            	if(!listItems.isEmpty())
            		listItems.clear();
            	if(vec_result.size()>0){
                   vec_result.clear();
            	}
            	int a=foundAdresses.size();
            	if(a>5)
            		a=5;
                
                for (int i = 0; i < foundAdresses.size(); i++) {
                  //Save results as Longitude and Latitude
                  //@todo: if more than one result, then show a select-list
                  searchResult mSR=new searchResult();
                  Address x = foundAdresses.get(i);
                  mSR.mAddress=x;
                  try{
                	  mSR.mLat = x.getLatitude();
                  }
                  catch(Exception e){
                	  mSR.mLat=0;
                	  continue;
                  }
                  try{
                	  mSR.mLng= x.getLongitude();
                  }
                  catch(Exception e){
                	  mSR.mLng=0;
                	  continue;
                  }
                  vec_result.add(mSR);
                  try{
                    StringBuilder strReturnedAddress = new StringBuilder("");
                    //for(int j=0; j<mSR.mAddress.getMaxAddressLineIndex(); j++) {
                        //strReturnedAddress.append(mSR.mAddress.getAddressLine(j)).append("  ");
                    //}
                    //first convert to atitudeE6, longitudeE6 to use it for geopoint
                    int latE6 = (int) (mSR.mAddress.getLatitude() * 1e6);
                    int lonE6 = (int) (mSR.mAddress.getLongitude() * 1e6);
                    strReturnedAddress.append("Address:").
                    				   append(mSR.mAddress.getAddressLine(0)).append(", Locality:").
                                       append(mSR.mAddress.getLocality()).append(", Country:").
                                       append(mSR.mAddress.getCountryName()).append(", Coordinates:").
                                       append(String.valueOf(latE6)).append(",").	
                                       append(String.valueOf(lonE6));
                    
                    mSR.mAddressString=strReturnedAddress.toString();
                  }
                  catch (Exception e){
                	 mSR.mAddressString="Address not found";
                  }
                  listItems.add(mSR.mAddressString);
                }
                adapter.notifyDataSetChanged();
              }
            }
            catch (Exception e) {
              
            	 Dialog locationError = new AlertDialog.Builder(LocateMe.this)
                 .setIcon(0)
                 .setTitle("Error, sorry!")
                 .setPositiveButton("OK", null)
                 .setMessage(e.getMessage())
                 .create();
               locationError.show();
            }
    	}
    });
    
    }
    /**
     * The method which navigates a given MapView to the specified Longitude and Latitude
     * 
     * @param latitude The Latitude data
     * @param longitude The longitude data
     * @param mv the MapView control
     */
     public static void navigateToLocation (int latitude, int longitude, MapView mv) {
       GeoPoint p = new GeoPoint(latitude, longitude); //new GeoPoint
      // mv.displayZoomControls(true); //display Zoom (seems that it doesn't work yet)
       MapController mc = mv.getController();
       mc.animateTo(p); //move map to the given point
       int zoomlevel = mv.getMaxZoomLevel(); //detect maximum zoom level
       mc.setZoom(zoomlevel - 1); //zoom
       mv.setSatellite(false); //display only "normal" mapview    
     }
     /** 
      * The inner class that is used  when one item from the list is selected
      * 
      * When an item is selected, the relevant address data is saved to the lblResult and txtResult controls which 
      * will then be visible on the screen.
      * 
      */  
     public class ResultsOnItemSelectedListener implements OnItemSelectedListener {

         public void onItemSelected(AdapterView<?> parent,
             View view, int pos, long id) {
         	//String str=parent.getItemAtPosition(pos).toString().trim();
        	 searchResult aSR=vec_result.get(pos);
        	
        	 mLocation_Lat=String.valueOf(aSR.mLat * 1000000);
        	 mLocation_Lng=String.valueOf(aSR.mLng * 1000000);
  //      	 navigateToLocation(Integer.valueOf(mLocation_Lat),Integer.valueOf(mLocation_Lng), mapView); //display the found address
        	 mLocation_Address=aSR.mAddressString;
        	 /*lblResult = (TextView)findViewById(R.id.locateme_lbllocation);
        	 txtResult = (TextView)findViewById(R.id.locateme_txtlocation);
        	 lblResult.setVisibility(lblResult.VISIBLE);
        	 txtResult.setVisibility(txtResult.VISIBLE);
        	 txtResult.setText(mLocation_Address);*/
        	 
        	
         }

         public void onNothingSelected(AdapterView parent) {
           // Do nothing.
         }
     }
     /** 
      * The inner class that is used when one item from the list is clicked
      * 
      * When an item is selected, the relevant address data is saved to the lblResult and txtResult controls which 
      * will then be visible on the screen.
      * 
      */ 
     public class ResultsOnItemClickListener implements OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			// TODO Auto-generated method stub
	       	 searchResult aSR=vec_result.get(arg2);
	         double b=aSR.mLat * 1000000.0;
	       	 int a=	(int)b;
        	 mLocation_Lat=String.valueOf(a);
        	 b=aSR.mLng* 1000000.0;
        	 a=(int)b;
        	 mLocation_Lng=String.valueOf(a);
  //      	 navigateToLocation(Integer.valueOf(mLocation_Lat),Integer.valueOf(mLocation_Lng), mapView); //display the found address
        	 //mLocation_Address=aSR.mAddressString+"  "+mLocation_Lat+";"+mLocation_Lng;
        	 mLocation_Address=aSR.mAddressString;
        	 /*lblResult = (TextView)findViewById(R.id.locateme_lbllocation);
        	 txtResult = (TextView)findViewById(R.id.locateme_txtlocation);
        	 lblResult.setVisibility(lblResult.VISIBLE);
        	 txtResult.setVisibility(txtResult.VISIBLE);
        	 txtResult.setText(mLocation_Address);		*/
        	 
        	 
        	 Intent resultIntent = new Intent(LocateMe.this, CreateEvent.class);
      		//resultIntent.putExtra("Latitude", mLocation_Lat);
      		//resultIntent.putExtra("Longitude", mLocation_Lng);
      		resultIntent.putExtra("Address", mLocation_Address);
      		//resultIntent.putExtra("GPS_FLAG", String.valueOf(iUseGPSFlag));
      		//setResult(Activity.RESULT_OK, resultIntent);
      		resultIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
      		startActivity(resultIntent);
      		finish();
		}
     }
     
     /** 
      * The class that keeps the a single searchResult
      * 
      */ 
     public class searchResult{
    	 public double mLat=0;
    	 public double mLng=0;
    	 public Address mAddress;
    	 public String mAddressString;
     }
     
     /** 
      * The inner class that is used when an item is picked on the NumberofResult  list control 
          *
          * When an item is picked, the mSet_Number variable changes it value. And the changeFlag variable is notified. 
      */
     public class NumOfResultsOnItemSelectedListener implements OnItemSelectedListener {

		@Override
		public void onItemSelected(AdapterView<?> parent,
	             View view, int pos, long id) {
	                 String str=parent.getItemAtPosition(pos).toString().trim();
	                 if(str!=mSet_Number){
	                         mSet_Number=str;
	                         iChangeFlag=1;
	                 }
	                 }
        
		@Override
		public void onNothingSelected(AdapterView<?> arg0) {
			// TODO Auto-generated method stub
			
		}
     } 
     /*
         public void onItemSelected(AdapterView<?> parent,
             View view, int pos, long id) {
                 String str=parent.getItemAtPosition(pos).toString().trim();
                 if(str!=mSet_Number){
                         mSet_Number=str;
                         iChangeFlag=1;
                 }
         }
*/
		

}
