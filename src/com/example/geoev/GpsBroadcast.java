package com.example.geoev;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.google.android.maps.GeoPoint;

public class GpsBroadcast extends Service {
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		return super.onStartCommand(intent, flags, startId);
	}

	public Thread triggerService;


	private static final String TAG = GpsBroadcast.class.getSimpleName();	
	private LocationManager lm;
	private LocationListener locationListener;
	
	//
	//private static String best;
	//public static Location location;
	
	private Location currentBestLocation;
	private static final int TWO_MINUTES = 1000 * 60 * 2;
	
	/**
	 * Determines whether one Location reading is better than the current
	 * Location fix
	 *
	 * @param location
	 *            The new Location that you want to evaluate
	 * @param currentBestLocation
	 *            The current Location fix, to which you want to compare the new
	 *            one
	 */
	protected boolean isBetterLocation(Location location,
			Location currentBestLocation) {
		if (currentBestLocation == null)
			// A new location is always better than no location
			return true;

		// Check whether the new location fix is newer or older
		long timeDelta = location.getTime() - currentBestLocation.getTime();
		boolean isSignificantlyNewer = timeDelta > TWO_MINUTES;
		boolean isSignificantlyOlder = timeDelta < -TWO_MINUTES;
		boolean isNewer = timeDelta > 0;

		// If it's been more than two minutes since the current location, use
		// the new location because the user has likely moved
		if (isSignificantlyNewer)
			return true;
			// If the new location is more than two minutes older, it must be
			// worse
		else if (isSignificantlyOlder)
			return false;

		// Check whether the new location fix is more or less accurate
		int accuracyDelta = (int) (location.getAccuracy() - currentBestLocation.getAccuracy());
		boolean isLessAccurate = accuracyDelta > 0;
		boolean isMoreAccurate = accuracyDelta < 0;
		boolean isSignificantlyLessAccurate = accuracyDelta > 200;

		// Check if the old and new location are from the same provider
		boolean isFromSameProvider = isSameProvider(location.getProvider(), currentBestLocation.getProvider());

		// Determine location quality using a combination of timeliness and
		// accuracy
		if (isMoreAccurate)
			return true;
		else if (isNewer && !isLessAccurate)
			return true;
		else if (isNewer && !isSignificantlyLessAccurate
				&& isFromSameProvider)
			return true;
		return false;
	}

	/** Checks whether two providers are the same */
	private boolean isSameProvider(String provider1, String provider2) {
		if (provider1 == null)
			return provider2 == null;
		return provider1.equals(provider2);
	}

	@Override
	public void onCreate() {
		super.onCreate();
		 Log.i(TAG, "Service creating");
		/*lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        
		//
		Criteria criteria = new Criteria();
		//best = lm.getBestProvider(criteria, true);
		List<String> providers=lm.getProviders(true);
		
		
		//locationListener = new MyLocationListener();
		//lm.requestLocationUpdates(best, 0, 0,
		//locationListener);
		
		Location locationGPS = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		Location locationWIFI = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
		if (locationWIFI != null && isBetterLocation(locationWIFI, locationGPS))
			updateWithNewLocation(locationWIFI);
		else if (locationGPS != null)
			updateWithNewLocation(locationGPS);*/

		//lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 100, 2, locationListener);
		//lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 100, 2, locationListener);
	}

	@Override
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);
		triggerService = new Thread(new Runnable() {
	        public void run() {
	            try {
	                Looper.prepare();
	                lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
	                
	        		//
	        		Criteria criteria = new Criteria();
	        		//best = lm.getBestProvider(criteria, true);
	        		List<String> providers=lm.getProviders(true);
	        		
	        		
	        		//locationListener = new MyLocationListener();
	        		//lm.requestLocationUpdates(best, 0, 0,
	        		//locationListener);
	        		
	        		Location locationGPS = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
	        		Location locationWIFI = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
	        		if (locationWIFI != null && isBetterLocation(locationWIFI, locationGPS))
	        			updateWithNewLocation(locationWIFI);
	        		else if (locationGPS != null)
	        			updateWithNewLocation(locationGPS);

	                locationListener = new MyLocationListener();
	                //myLocationListener = new GPSListener();
	                //long minTime = 10000; // frequency update: 10 seconds
	               // float minDistance = 50; // frequency update: 50 meter
	               /* lm.requestLocationUpdates(
	                        // request GPS updates
	                        LocationManager.GPS_PROVIDER, minTime, minDistance,
	                        myLocationListener);*/
	                lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 100, 2, locationListener);
	        		lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 100, 2, locationListener);
	                Looper.loop();
	            } catch (Exception e) {
	                Log.e("MYGPS", e.getMessage());
	            }
	        }// run
	    });
	    triggerService.start();
	}

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	private class MyLocationListener implements LocationListener {
		@Override
		public void onLocationChanged(Location location) {
			updateWithNewLocation(location);
		}

		@Override
		public void onProviderDisabled(String provider) {
			Log.d(TAG, "onProviderDisabled");
		    Toast.makeText(
		            getApplicationContext(),
		            "Attempted to ping your location, and GPS was disabled.",
		            Toast.LENGTH_LONG).show();
		}

		@Override
		public void onProviderEnabled(String provider) {
			// TODO Auto-generated method stub
		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
			// TODO Auto-generated method stub
		}
	}

	private void sendMsg(double msg, double msg2, double msg3, double msg4, long msg5) {
		Intent intent = new Intent(FBPost.NEW_MSG);
		intent.putExtra("Longtitude", msg);
		intent.putExtra("Latitude", msg2);
		//added 6/12/2012
		intent.putExtra("Altitude", msg3);
		intent.putExtra("Speed", msg4);
		intent.putExtra("Time", msg5);
		sendBroadcast(intent);
	}
	
	private void sendMsgEvent(double msg, double msg2) {
		Intent intent = new Intent(CreateEvent.NEW_MSG_EVENT);
		intent.putExtra("Longtitude", msg);
		intent.putExtra("Latitude", msg2);
		sendBroadcast(intent);
	}

	private void sendMsgMap(double msg, double msg2) {
		Intent intent = new Intent(MapEvents.NEW_MSG_MAP);
		intent.putExtra("Longtitude", msg);
		intent.putExtra("Latitude", msg2);
		sendBroadcast(intent);
	}
	
	private void sendMsgDirections(double msg, double msg2) {
		Intent intent = new Intent(Event_details.NEW_MSG_DETAILS);
		intent.putExtra("Longtitude", msg);
		intent.putExtra("Latitude", msg2);
		sendBroadcast(intent);
	}
	
	/*private void sendMsgDirections(double msg, double msg2, int msg3){
		Intent intent = new Intent(MapEvents.NEW_MSG_MAP);
		intent.putExtra("Longtitude", msg);
		intent.putExtra("Latitude", msg2);
		intent.putExtra("sizeOfDirArrayList", msg3);
		sendBroadcast(intent);
	}*/
	
	
	//Get Directions
	public static ArrayList getDirections(double lat1, double lon1, double lat2, double lon2) {
        String url = "http://maps.googleapis.com/maps/api/directions/xml?origin=" +lat1 + "," + lon1  + "&destination=" + lat2 + "," + lon2 + "&sensor=false&units=metric";
        String tag[] = { "lat", "lng" };
        ArrayList list_of_geopoints = new ArrayList();
        HttpResponse response = null;
        try {
            HttpClient httpClient = new DefaultHttpClient();
            HttpContext localContext = new BasicHttpContext();
            HttpPost httpPost = new HttpPost(url);
            response = httpClient.execute(httpPost, localContext);
            InputStream in = response.getEntity().getContent();
            DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document doc = builder.parse(in);
            if (doc != null) {
                NodeList nl1, nl2;
                nl1 = doc.getElementsByTagName(tag[0]);
                nl2 = doc.getElementsByTagName(tag[1]);
                if (nl1.getLength() > 0) {
                    list_of_geopoints = new ArrayList();
                    for (int i = 0; i < nl1.getLength(); i++) {
                        Node node1 = nl1.item(i);
                        Node node2 = nl2.item(i);
                        double lat = Double.parseDouble(node1.getFirstChild().getNodeValue());
                        double lng = Double.parseDouble(node2.getFirstChild().getNodeValue());
                        list_of_geopoints.add(new GeoPoint((int) (lat * 1E6), (int) (lng * 1E6)));
                    }
                } else {
                    // No points found
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list_of_geopoints;
    }
	
	
	
	
	
	/**
	 * Update location
	 *
	 * @param location
	 *            New location
	 */
	private void updateWithNewLocation(Location location) {
		if (location != null && isBetterLocation(location, currentBestLocation)) {
			ArrayList listOfGeopoints = new ArrayList();
			this.currentBestLocation = new Location(location);
			double speed = (double) location.getSpeed() * 3600.0 / 1000.0;
			double altitude = (double) location.getAltitude();
			double longtitude = location.getLongitude();
			double latitude = location.getLatitude();
			//added 06/12/2012
			long time = location.getTime();
			
			//listOfGeopoints = getDirections(longtitude, latitude, 39.291777, 23.144932);
			
			int k = (int) speed;
			sendMsg(longtitude, latitude, altitude, speed, time);
			sendMsgMap(longtitude, latitude);
			sendMsgEvent(longtitude, latitude);
			sendMsgDirections(longtitude, latitude);
			
	

			// Update notification
			Geocoder g = new Geocoder(getApplicationContext());
			String address = "";
			try {
				Address add = g.getFromLocation(location.getLatitude(), location.getLongitude(), 1).get(0);
				int i = add.getMaxAddressLineIndex();
				for (int j=0; j<i; j++) {
					if (j != 0) address += ", ";
					address += add.getAddressLine(j);
				}
			} catch (IOException e) {
			}
			/*if (!address.equals("") && contentIntent != null) {
				notification.setLatestEventInfo(app, "AR Walks Location", address, contentIntent);
				mNotificationManager.notify(HELLO_ID, notification);
			}*/
		}
		
		
		
	}
	

}