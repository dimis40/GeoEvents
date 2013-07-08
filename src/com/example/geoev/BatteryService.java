package com.example.geoev;

import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.BatteryManager;
import android.os.Environment;
import android.os.IBinder;
import android.preference.PreferenceManager;

public class BatteryService extends Service {

    private static BroadcastReceiver batteryInfo = new BroadcastReceiver() {
            
            @Override
            public void onReceive(Context context, Intent intent) {
                
                    //if OFF then ignore
                    String status = getONOFF(context);
                    if(status.equalsIgnoreCase("OFF")) {
                        return;
                    }
                    
                    // TODO Auto-generated method stub
                    String action = intent.getAction();
                    
                    if(Intent.ACTION_BATTERY_CHANGED.equals(action)) {
                                                    
                        int rawlevel = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
                        int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
                        int level = -1;
                        if (rawlevel >= 0 && scale > 0) {
                            level = (rawlevel * 100) / scale;
                        } 
                        //save
                        String battery = getBatterStatus(context.getApplicationContext());
                        Calendar cal = Calendar.getInstance();            
                        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");            
                        battery = battery + level + "\t" + sdf.format(cal.getTime()) + "\n";
                        setBatterStatus(context.getApplicationContext(), battery);
                        
                        String backupFilePath = Environment
                                                .getExternalStorageDirectory()
                                                    .getAbsoluteFile().getAbsolutePath();
                        backupFilePath += "/battery_usage.xls";

                         String battery2 = getBatterStatus(context.getApplicationContext());

                            try {
                                FileWriter fw = new FileWriter(backupFilePath);
                                fw.write(battery2);
                                fw.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                    }
            }
    };
    
    @Override
    public IBinder onBind(Intent intent) {
            // TODO Auto-generated method stub
            return null;
    }

    @Override
    public void onStart(Intent intent, int startId) {
            super.onStart(intent, startId);
            //register for intent
            registerReceiver(batteryInfo, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
    }
    
    @Override
    public void onDestroy() {
            super.onDestroy();
            //un register
            unregisterReceiver(batteryInfo);
            
            //if the service was destroyed while ON then set alarm to start again
            String status = getONOFF(this);
            if(status.equalsIgnoreCase("ON")) {
                AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

                long time = Calendar.getInstance().getTimeInMillis() + 5*60*1000; //every minute
                Intent AlarmIntent = new Intent(this, BatteryService.class);
                PendingIntent pintent = PendingIntent.getService(this, 0, AlarmIntent, 0);
                alarmManager.set(AlarmManager.RTC_WAKEUP, time, pintent);
            }
    }
    
    public static String getBatterStatus(Context context) {
        String status = "";
        SharedPreferences settings = PreferenceManager
                                        .getDefaultSharedPreferences(context);
        status = settings.getString("BatteryStatus", "");
        return status;
    }
    
    
    public static void setBatterStatus(Context context, String status) {
        SharedPreferences settings = PreferenceManager
                                            .getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("BatteryStatus",
                           status);
        editor.commit();
    }
    
    public static String getONOFF(Context context) {
        String status = "";
        SharedPreferences settings = PreferenceManager
                                        .getDefaultSharedPreferences(context);
        status = settings.getString("OnOff", "OFF");
        return status;
    }
}
