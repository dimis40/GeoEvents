package com.example.geoev;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBFacebookEvents {

	public static final String KEY_ROWID = "_id";

    public static final String KEY_NAME = "Name";
    public static final String KEY_ID = "ID";
    public static final String KEY_START = "Start";
    public static final String KEY_END = "End";
    public static final String KEY_LOCATION = "Location";
  
    private static final String TAG = "DBFacebookEvents";
    
    private static final String DATABASE_NAME = "userdataFBEvents";
    private static final String DATABASE_TABLEEVENTS = "Events";
    private static final String DATABASE_TABLEATTENDING = "Attending";

   
    private static final int DATABASE_VERSION = 2;

    private static final String DATABASE_CREATE_TABLEEVENTS =
 	   "create table "+DATABASE_TABLEEVENTS+" (_id integer primary key autoincrement, "
        + "Name text not null, ID text not null, Start text not null, "
        + "End text not null, Location text not null);";


        
    private static Context context;  

    private DatabaseHelper DBHelper;
    private SQLiteDatabase db;

    public DBFacebookEvents(Context ctx) 
    {
        this.context = ctx;
        DBHelper = new DatabaseHelper(context);
    }
    
    
        
    private static class DatabaseHelper extends SQLiteOpenHelper 
    {
        DatabaseHelper(Context context) 
        {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
     	   Log.d(TAG, "Creating Database...");

        }

        @Override
        public void onCreate(SQLiteDatabase db) 
        {
     	   
            db.execSQL(DATABASE_CREATE_TABLEEVENTS);
            Log.d(TAG, "Created Table");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, 
                              int newVersion) 
        {
            Log.w(TAG, "Upgrading database from version " + oldVersion 
                  + " to "
                  + newVersion + ", which will destroy all old data");
            db.execSQL("DROP TABLE IF EXISTS titles");
            onCreate(db);
        }
    }    

    //---opens the database---
    public DBFacebookEvents open() throws SQLException 
    {
        db = DBHelper.getWritableDatabase();
        return this;
    }

    //---closes the database---    
    public void close() 
    {
        DBHelper.close();
    }
    
    
    public long insertEvent(String name, String id, String start, String end, String loc) 
    {
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_NAME, name);
        initialValues.put(KEY_ID, id);       
        initialValues.put(KEY_START, start);
        initialValues.put(KEY_END, end);       
        initialValues.put(KEY_LOCATION, loc);
        
        return db.insert(DATABASE_TABLEEVENTS, null, initialValues);
    }      

    
     
    public Cursor getEvent() 
     {
         return db.query(DATABASE_TABLEEVENTS, new String[] {
               KEY_ROWID,
               KEY_NAME,
               KEY_ID,
               KEY_START,
               KEY_END,
               KEY_LOCATION,
                 }, 
                 null, 
                 null, 
                 null, 
                 null, 
                 null,
                 null);
     }    
    
    public void deleteEvent()
    {
 	   db.delete(DATABASE_TABLEEVENTS, null, null);
    }

    

}
