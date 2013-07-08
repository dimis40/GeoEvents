package com.example.geoev;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class DBTable {
	   // Database creation SQL statement
	   // Modify this class based on the need of the app's database
	
	
    private static final String DATABASE_TABLEEVENTS = "Events";
    private static final String DATABASE_TABLEATTENDINGSTATUS = "AttendingStatus";
    private static final String DATABASE_TABLEPOSTS = "EventWallposts";
    private static final String DATABASE_TABLEFRIENDS = "FriendsInfo";
    private static final String DATABASE_GPSDATA = "GpsData";

	private static final String DATABASE_CREATE_TABLEEVENTS =
		 	   "create table "+DATABASE_TABLEEVENTS+" (_id integer primary key autoincrement, "
		        + "Name text not null, ID text not null, Start text, "
		        + "End text, Location text, Creator text not null, Latitude text, Longitude text, Update_time text not null, Description text);";
    

   
	private static final String DATABASE_CREATE_TABLEATTENDINGSTATUS =
		 	   "create table "+DATABASE_TABLEATTENDINGSTATUS+" (_id integer primary key autoincrement, "
    + "UserID text not null, EventID text not null, Rsvp_status);";
	
	private static final String DATABASE_CREATE_TABLEPOSTS =
	           "create table "+DATABASE_TABLEPOSTS+" (_id integer primary key autoincrement, "
	           + "EventID text, message text, PostID text, CreatedTime text, "
	           + "UserID text, UserName text);";
	
	private static final String DATABASE_CREATE_TABLEFRIENDS =
		    "create table "+DATABASE_TABLEFRIENDS+" (_id integer primary key autoincrement, "
		    + "UserName text not null, UserID text not null);";
	
	private static final String DATABASE_CREATE_GPSDATA =
		    "create table "+DATABASE_GPSDATA+" (_id integer primary key autoincrement, "
		    + "EventID text, EventTitle, Latitude text not null, Longitude text not null, Speed text, Altitude text, Time text);";
	   
	      public static void onCreate(SQLiteDatabase database) {
	          database.execSQL(DATABASE_CREATE_TABLEEVENTS);
	          database.execSQL(DATABASE_CREATE_TABLEATTENDINGSTATUS);
	          database.execSQL(DATABASE_CREATE_TABLEPOSTS);
	          database.execSQL(DATABASE_CREATE_TABLEFRIENDS);
	          database.execSQL(DATABASE_CREATE_GPSDATA);
	      }
	      
	   
	      public static void onUpgrade(SQLiteDatabase database, int oldVersion,
	   int newVersion) {
	         Log.w(DBTable.class.getName(), "Upgrading database from version "
	           + oldVersion + " to " + newVersion
	           + ", which will destroy all old data");
	           database.execSQL("DROP TABLE IF EXISTS todo");
	           onCreate(database);
	         }
	    }
