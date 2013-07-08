package com.example.geoev;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.json.JSONArray;
import org.json.JSONObject;

public class GetDirectionsList {
	
	public interface SearchResultCallBack {
		public void OnSearchCompleted(ArrayList<DirectionItem> list,int code);
		public static final int SUCCESS 	= 1;
		public static final int NETWORK_FAILURE		= 2;
		public static final int NO_ROUTE		= 3;
	}
	
	public class DirectionItem
	{
		
		double latitude;
		double longitude;
		String duration;
		String distance;
		String instructions;
	}
    

	private ArrayList<DirectionItem> DirectionItemList;
	private String result;
	private String startLocation;
	private String destination;
	private SearchResultCallBack mCB;
	
	GetDirectionsList(String start, String dest,SearchResultCallBack aCB)
	{
		startLocation = start;
		destination = dest;
		mCB = aCB;
		
	}
	
	public void searchRoutes()
	{
		  	HttpClient client = new DefaultHttpClient();
			String query = "http://maps.googleapis.com/maps/api/directions/json?origin="+startLocation+"&destination="+destination+"&sensor=false";

			try {
				URL url = new URL(query);
				URI uri = new URI(url.getProtocol(), url.getHost(), url.getPath(), url.getQuery(), null);
				HttpGet request = new HttpGet(uri);
				// Set the timeout in milliseconds until a connection is established.
				int timeoutConnection = 3000;
				HttpConnectionParams.setConnectionTimeout(client.getParams(), timeoutConnection);
				// Set the default socket timeout (SO_TIMEOUT) 
				// in milliseconds which is the timeout for waiting for data.
				int timeoutSocket = 3000;
				HttpConnectionParams.setSoTimeout(client.getParams(), timeoutSocket);
				
				HttpResponse response = client.execute(request);
				Userrequest(response);
			} catch ( ConnectTimeoutException e) {
				 mCB.OnSearchCompleted(null,SearchResultCallBack.NETWORK_FAILURE);
			} catch (java.net.SocketTimeoutException e) {
				 mCB.OnSearchCompleted(null,SearchResultCallBack.NETWORK_FAILURE);
			}catch(Exception ex){
				 mCB.OnSearchCompleted(null,SearchResultCallBack.NETWORK_FAILURE);
			}
			
		}
		 public  void Userrequest(HttpResponse response){
		    
		    	try{
		    	        InputStream in = response.getEntity().getContent();
		    	        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
		    	        StringBuilder str = new StringBuilder();
		    	        String line = null;
		    	        while((line = reader.readLine()) != null){
		    	            str.append(line + "\n");
		    	        }
		    	        in.close();
		    	        result = str.toString();
		    	        updateData(result);
		    	   }catch(Exception ex) {
		    	        mCB.OnSearchCompleted(null,SearchResultCallBack.NO_ROUTE);
		    	   }
		    	    
    	   }
		 public  void updateData(String result)
		    {
			 
		   	 try
		        {
		         JSONObject json=new JSONObject(result);
		         JSONArray ja;
		         ja = json.getJSONArray("routes");
		         
		         if(ja.length() > 0 )
		        	 json = ja.getJSONObject(0);
		         else  {
		        	 mCB.OnSearchCompleted(null,SearchResultCallBack.NO_ROUTE);
		        	 return;
		         }
		         
		         if(json.length() > 0 )
			         ja=json.getJSONArray("legs");   
			     else  {
			        	 mCB.OnSearchCompleted(null,SearchResultCallBack.NO_ROUTE);
			        	 return;
			         }
		         
		         if(ja.length() > 0 )
	                 json = ja.getJSONObject(0);
			         else  {
			        	 mCB.OnSearchCompleted(null,SearchResultCallBack.NO_ROUTE);
			        	 return;
			         }

		         if(json.length() > 0 )
	                 ja = json.getJSONArray("steps");
			         else  {
			        	 mCB.OnSearchCompleted(null,SearchResultCallBack.NO_ROUTE);
			        	 return;
			         }

		         	         
		         int resultCount = ja.length();
		         if(resultCount > 0 ) {
			         DirectionItemList = new ArrayList<DirectionItem>();
			         for (int i = 0; i < resultCount; i++)
			           {
			           JSONObject resultObject = ja.getJSONObject(i);
			           DirectionItem item = new DirectionItem();
			           JSONObject obj;
			           
			           // Get the distance
			           obj = resultObject.getJSONObject("distance");
			           item.distance = obj.getString("text");
			           
			           // Get the duration
			           obj = resultObject.getJSONObject("duration");
			           item.duration = obj.getString("text");
			           
			           // Get the lat,long
			           obj = resultObject.getJSONObject("start_location");
			           
			           item.latitude = Double.parseDouble(obj.getString("lat"));
			           item.longitude = Double.parseDouble(obj.getString("lng"));
			           
			           // Get the instruction
			           item.instructions = resultObject.getString("html_instructions"); 
			           DirectionItemList.add(item);
		           }
			         mCB.OnSearchCompleted(DirectionItemList,SearchResultCallBack.SUCCESS);
		         } else {
		        	 mCB.OnSearchCompleted(null,SearchResultCallBack.NO_ROUTE);
		         }
		        }
		         catch(Exception e)
		         {
		         	
		         System.out.println("Exception is "+e.toString());
		         mCB.OnSearchCompleted(null,SearchResultCallBack.NO_ROUTE);
		         }
		    }
}
