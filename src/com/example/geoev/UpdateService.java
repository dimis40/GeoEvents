package com.example.geoev;

import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.widget.Toast;

public class UpdateService extends Service {
	public LocationManager locMan;
	public Location curLocation;
	public Boolean locationChanged;

	
	// GPS Listener Class
    LocationListener gpsListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                    // Log.w("GPS", "Started");
                    if (curLocation == null) {
                            curLocation = location;
                            locationChanged = true;
                    }

                    if (curLocation.getLatitude() == location.getLatitude() && curLocation.getLongitude() == location.getLongitude())
                            locationChanged = false;
                    else
                            locationChanged = true;

                    curLocation = location;

                    if (locationChanged) 
                            locMan.removeUpdates(gpsListener);

            }

            public void onProviderDisabled(String provider) {

            }

            public void onProviderEnabled(String provider) {
                    // Log.w("GPS", "Location changed", null);
            }

            public void onStatusChanged(String provider, int status,
                            Bundle extras) {
                    if (status == 0)// UnAvailable
                    {

                    } else if (status == 1)// Trying to Connect
                    {

                    } else if (status == 2) {// Available

                    }
            }

    };

    // In service start method, I am registering for GPS Updates
    @Override
    public void onStart(Intent intent, int startId) {

            locMan = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            if (locMan.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    locMan.requestLocationUpdates(LocationManager.GPS_PROVIDER,20000, 1, gpsListener);
            } else {
                    this.startActivity(new Intent("android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS"));
            }

            if (curLocation != null) {
                    double lat = curLocation.getLatitude();
                    double lng = curLocation.getLongitude();
                    Toast.makeText(getBaseContext(),"Lat : " + String.valueOf(lat) + "\n Long : "+ String.valueOf(lng), Toast.LENGTH_LONG).show();

            }
            // Build the widget update for today
            //RemoteViews updateViews = buildUpdate(this);

            // Push update for this widget to the home screen
            //ComponentName thisWidget = new ComponentName(this,InKakinadaWidget.class);
            AppWidgetManager manager = AppWidgetManager.getInstance(this);
            //manager.updateAppWidget(thisWidget, updateViews);

    }
    /*
    public RemoteViews buildUpdate(Context context) {
            // Here I am updating the remoteview
            return updateViews;
    }*/

    @Override
    public IBinder onBind(Intent intent) {
            // We don't need to bind to this service
            return null;
    }

}
