package com.example.geoev;

import java.io.FileWriter;
import java.io.IOException;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class BatteryMonitor extends Activity implements OnClickListener{

    @Override

    public void onCreate(Bundle icicle) {

      super.onCreate(icicle);
      setContentView(R.layout.battery);
      
      ((Button) findViewById(R.id.startB)).setOnClickListener(this);
      ((Button) findViewById(R.id.backupB)).setOnClickListener(this);
      ((Button) findViewById(R.id.resetB)).setOnClickListener(this);
      
      String status = getONOFF();
      if(status.equalsIgnoreCase("ON")) {
          ((Button) findViewById(R.id.startB)).setText("Stop Monitor");
      } else {
          ((Button) findViewById(R.id.startB)).setText("Start Monitor");
      }

    }
    
    
    public String getBatterStatus() {
                String status = "";
                SharedPreferences settings = PreferenceManager
                                                        .getDefaultSharedPreferences(getApplicationContext());
                status = settings.getString("BatteryStatus", "");
                return status;
        }
    
    
    public void setBatterStatus(String status) {
                SharedPreferences settings = PreferenceManager
                                                    .getDefaultSharedPreferences(getApplicationContext());
                SharedPreferences.Editor editor = settings.edit();
                editor.putString("BatteryStatus",
                                           status);
                editor.commit();
        }
    
    
    public String getONOFF() {
        String status = "";
        SharedPreferences settings = PreferenceManager
                                        .getDefaultSharedPreferences(getApplicationContext());
        status = settings.getString("OnOff", "OFF");
        return status;
    }
    
    
    public void setONOFF(String status) {
        SharedPreferences settings = PreferenceManager
                                            .getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("OnOff",
                           status);
        editor.commit();
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch(id) {
            case R.id.startB:
               String status = getONOFF();
                if(status.equalsIgnoreCase("ON")) {
                    setONOFF("OFF");
                    ((Button) findViewById(R.id.startB)).setText("Start Monitor");
                    //stop service
                    stopService(new Intent(BatteryMonitor.this, BatteryService.class));
                    Toast.makeText(this, "Stopped", Toast.LENGTH_SHORT).show();
                } else {
                    setONOFF("ON");
                    ((Button) findViewById(R.id.startB)).setText("Stop Monitor");
                    //start service
                    startService(new Intent(BatteryMonitor.this, BatteryService.class));
                    Toast.makeText(this, "Started", Toast.LENGTH_SHORT).show();
                }
                break;
                
            case R.id.backupB:
                String backupFilePath = Environment
                                            .getExternalStorageDirectory()
                                                .getAbsoluteFile().getAbsolutePath();
                backupFilePath += "/battery_usage.xls";
                
                String battery = getBatterStatus();
                
                try {
                    FileWriter fw = new FileWriter(backupFilePath);
                    fw.write(battery);
                    fw.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                
                Toast.makeText(
                        this, 
                        "Saved to " + backupFilePath, 
                        Toast.LENGTH_SHORT).show();
                
                break;
                
            case R.id.resetB:
                setBatterStatus("");
                Toast.makeText(this, "Reset", Toast.LENGTH_SHORT).show();
                break;
        }
        
    }
    
    
}
