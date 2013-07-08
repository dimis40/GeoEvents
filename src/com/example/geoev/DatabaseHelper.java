package com.example.geoev;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.ReentrantLock;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
	
	//
	public static boolean locked = false;
	public static final String KEY_ROWID = "_id";
    
	public static final String KEY_USERNAME = "UserName";
	public static final String KEY_USERID = "UserID";
    public static final String KEY_FEEDNAME = "Name";
    public static final String KEY_CREATEDTIME = "CreatedTime";
    public static final String KEY_UPDATEDTIME = "UpdatedTime";
    public static final String KEY_NAME = "Name";
    public static final String KEY_ID = "ID";
    
    //public static final String KEY_EVID = "EV_ID";
    public static final String KEY_MESSAGE = "message";
    public static final String KEY_EVENT_FOREIGN_ID = "EventID";
    public static final String KEY_START = "Start";
    public static final String KEY_END = "End";
    public static final String KEY_LOCATION = "Location";
    public static final String KEY_CREATOR = "Creator";
    public static final String KEY_UPDATETIME = "Update_time";
    public static final String KEY_DESCRIPTION = "Description";
    public static final String KEY_RSVPSTATUS = "Rsvp_status";
    public static final String KEY_LATITUDE = "Latitude";
    public static final String KEY_LONGITUDE = "Longitude";
    public static final String KEY_POSTID = "PostID";
    public static final String KEY_POSTUSERNAME = "UserName";
    
    
    //added 06/12/2012
    public static final String KEY_EVENTTITLE = "EventTitle";
    public static final String KEY_TIME = "Time";
    public static final String KEY_ALTITUDE = "Altitude";
    public static final String KEY_SPEED = "Speed";
    
  
    private static final String TAG = "DBFacebookEvents";
    private static final String DATABASE_TABLEEVENTS = "Events";
    private static final String DATABASE_TABLEATTENDINGSTATUS = "AttendingStatus";
    private static final String DATABASE_TABLEPOSTS = "EventWallposts";
    private static final String DATABASE_TABLEFRIENDS = "FriendsInfo";
    private static final String DATABASE_GPSDATA = "GpsData";
	
   //http://developer.android.com/reference/java/util/concurrent/locks/ReentrantLock.html
    public final ReentrantLock lock = new ReentrantLock(true);
    
    private Semaphore semMDB = new Semaphore(1);
    
    // db name for this application

	private static final String DATABASE_NAME = "myFBDatabase";

    // version number of this db

    private static final int DATABASE_VERSION = 2;

    // private variable which is going to store the singleton object of this class and return it to caller

    private static DatabaseHelper dbHelper = null;
    // private SQLiteDatabase varaible which is going to be responsible for all our db related operation // no need      touch it

    private static SQLiteDatabase db = null;
  
    // a private contructor which is going to be called from getInstance method of this constructor
    
    private DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        // storing the object of this class to dbHelper
        dbHelper = this;
    }
  
    // Method is called during creation of the database
    /* ***************************
    *
    * BE SURE TO ENTER THE CORRECT CREATE STATEMENT FOR TABLE IN DBTable.java FOR YOUR APPLICATION NEED
    *
    */

    @Override
    public void onCreate(SQLiteDatabase database) {
     DBTable.onCreate(database);
    }
 
    // Method is called during an upgrade of the database,
    // e.g. if you increase the database version

     @Override
     public void onUpgrade(SQLiteDatabase database, int oldVersion,
    int newVersion) {
      DBTable.onUpgrade(database, oldVersion, newVersion);
     }

    // method which is going to be responsible for all our select query
    /* use case:
    * String selectSql = "Select * from table_name";
    * //be careful using try catch:
    * Cursor myCursor = null;
    * try{
    * myCursor = DatabaseHelper.getInstance.executeQuery(selectSql);
    * // ..... get the data using cursor from the db
    * }catch(Exception e){
    * e.printStackTrace(); // be sure to check if any exception is thrown and find out what is the reason
    * }finally{ // be sure to close the cursor or else, might get some unexpected behavior
    * // see if cursor really got something when requesting for something and is not closed already, just for    precaution     
    * if(myCursor!=null && !myCursor.isClosed){
    * myCursor.close();
    * }
    * }
    */

  public Cursor executeQuery(String sql){
   Cursor mCursor =db.rawQuery(sql, null);
    return mCursor;
  }
    // method which is going to be responsible for all insert, update and delete query
    /* use case:
    * String dmlQuery = "Insert into table_name values(....";
    * DatabaseHelper.getInstance.executeDMLQuery(dmlQuery);// will return true or false based on the action succeeded    or   not if checked?
    */

   public boolean executeDMLQuery(String sql){
   try {
     db.execSQL(sql);
      return true;
    } catch (Exception e) {
    // TODO: handle exception
     return false; 
   }
  }

    // will return a singleton object of this class will as well open the connection for convinient

   public static synchronized DatabaseHelper getInstance(Context context){
       if(dbHelper==null){
         dbHelper = new DatabaseHelper(context);
          openConnection();
         }
        return dbHelper;
     }
    // will be called only once when singleton is created

   private static void openConnection(){
         if ( db == null ){
               db = dbHelper.getWritableDatabase();
           }
     }
   // be sure to call this method by: DatabaseHelper.getInstance.closeConnecion() when application is closed by    somemeans most likely
   // onDestroy method of application

    public synchronized void closeConnecion() {
         if(dbHelper!=null){
             dbHelper.close();
                db.close(); 
               dbHelper = null;
               db = null;
           }
       }
    
    //
  //---opens the database---
   
    public synchronized DatabaseHelper open() throws SQLException 
    {
        db = dbHelper.getWritableDatabase();
        return this;
    }

    //---closes the database---    
    public synchronized void close() 
    {
    	if (dbHelper != null)
            db.close();
        //db.close();
    }
    
    
    public long insertEvent(String name, String id, String start, String end, String loc, String creator, String lat, String lon, String update_time, String description) 
    {
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_NAME, name);
        initialValues.put(KEY_ID, id);       
        initialValues.put(KEY_START, start);
        initialValues.put(KEY_END, end);       
        initialValues.put(KEY_LOCATION, loc);
        initialValues.put(KEY_CREATOR, creator);
        initialValues.put(KEY_LATITUDE, lat);
        initialValues.put(KEY_LONGITUDE, lon);
        initialValues.put(KEY_UPDATETIME, update_time);
        initialValues.put(KEY_DESCRIPTION, description);
        
        return db.insert(DATABASE_TABLEEVENTS, null, initialValues);
    }   
    
    public synchronized long insertGpsData(String eid, String title, String lat, String lon, String speed, String alt, String time) 
    {
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_EVENT_FOREIGN_ID, eid);
        initialValues.put(KEY_EVENTTITLE, title);       
        initialValues.put(KEY_LATITUDE, lat);
        initialValues.put(KEY_LONGITUDE, lon);       
        initialValues.put(KEY_SPEED, speed);
        initialValues.put(KEY_ALTITUDE, alt);
        initialValues.put(KEY_TIME, time);
        
        
        return db.insert(DATABASE_GPSDATA, null, initialValues);
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
              KEY_CREATOR,
              KEY_LATITUDE,
              KEY_LONGITUDE,
              KEY_UPDATETIME,
              KEY_DESCRIPTION
                }, 
                null, 
                null, 
                null, 
                null, 
                null,
                null);
    }    
    
    public Cursor getGpsData() 
    {
        return db.query(DATABASE_GPSDATA, new String[] {
              KEY_ROWID,
              KEY_EVENT_FOREIGN_ID,
              KEY_EVENTTITLE,
              KEY_LATITUDE,
              KEY_LONGITUDE,
              KEY_SPEED,
              KEY_TIME
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
    
    public void deleteGpsData()
    {
 	   db.delete(DATABASE_GPSDATA, null, null);
    }
    
    public synchronized long insertFriend(String name, String id) 
    {
    	//db.beginTransaction();
    	//deleteFriend();
    	//int in = Integer.valueOf(id);
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_USERNAME, name);
        initialValues.put(KEY_USERID, id);       
        
        //db.setTransactionSuccessful();
       // db.endTransaction();
        return db.insert(DATABASE_TABLEFRIENDS, null, initialValues);
        
    }  
    
    public long insertAttendingStatus(String uid, String eid, String rsvp_status) 
    {
    	//int in = Integer.valueOf(id);
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_USERID, uid);
        initialValues.put(KEY_EVENT_FOREIGN_ID, eid);
        initialValues.put(KEY_RSVPSTATUS, rsvp_status);
        
        return db.insert(DATABASE_TABLEATTENDINGSTATUS, null, initialValues);
    }


     
    public Cursor getFriend() 
     {
         return db.query(DATABASE_TABLEFRIENDS, new String[] {
               KEY_ROWID,
               KEY_USERNAME,
               KEY_USERID,                  
                 }, 
                 null, 
                 null, 
                 null, 
                 null, 
                 null,
                 null);
     }    
    
    public Cursor getAttendingStatus() 
    {
        return db.query(DATABASE_TABLEATTENDINGSTATUS, new String[] {
              KEY_ROWID,
              KEY_USERID,
              KEY_EVENT_FOREIGN_ID,
              KEY_RSVPSTATUS
                }, 
                null, 
                null, 
                null, 
                null, 
                null,
                null);
    }   

    public void deleteAttendingStatus()
    {
        db.delete(DATABASE_TABLEATTENDINGSTATUS, null, null);
    }
    
    public synchronized void deleteFriend()
    {
        db.delete(DATABASE_TABLEFRIENDS, null, null);
    }
    
    public long insertEventWallpost(String id, String message, String post_id, String created_at, String uId, String name) 
    {
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_EVENT_FOREIGN_ID, id);
        initialValues.put(KEY_MESSAGE, message); 
        initialValues.put(KEY_POSTID, post_id); 
        initialValues.put(KEY_CREATEDTIME, created_at);
        initialValues.put(KEY_USERID, uId);
        initialValues.put(KEY_POSTUSERNAME, name);
        
        
        return db.insert(DATABASE_TABLEPOSTS, null, initialValues);
    }      
    
    public Cursor getEventWallpost() 
    {
        return db.query(DATABASE_TABLEPOSTS, new String[] {
              KEY_ROWID,
              KEY_EVENT_FOREIGN_ID,
              KEY_POSTID,
              KEY_CREATEDTIME,
              KEY_USERID,
              KEY_POSTUSERNAME
                }, 
                null, 
                null, 
                null, 
                null, 
                null);
    }    
    
    public void deleteEventsWallposts()
    {
        db.delete(DATABASE_TABLEPOSTS, null, null);
    }
    
    //added 17/10/2012
    public synchronized List<Person> getAllFriends() {
    	lock.lock();
    	List<Person> friends = new ArrayList<Person>();
    	// Select All Query
        String selectQuery = "SELECT  * FROM " + DATABASE_TABLEFRIENDS;
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                Person person = new Person();
                //contact.setID(Integer.parseInt(cursor.getString(0)));
                person.setName(cursor.getString(1));
                person.setId(cursor.getString(2));
                // Adding contact to list
                friends.add(person);
            } while (cursor.moveToNext());
        }
    
    	//String[] resultColumns = new String[] { KEY_ROWID, KEY_USERNAME, KEY_USERID};
    	lock.unlock();
        return friends;
    	
    }
    
    public synchronized void insertAllFriends(ArrayList<Person> myFriends) {
    	lock.lock();
    	try {
    	deleteFriend();
    	//List<Person> friends = new ArrayList<Person>();
    	// Select All Query
    	for(int i=0;i<myFriends.size();i++){
        insertFriend(myFriends.get(i).getName(), myFriends.get(i).getName());
    		//myFriends.get(i).getName();
    		System.out.println(myFriends.get(i).getName());
    		//System.out.println(itr.next());
    	}
    	} finally {
    		lock.unlock();
    	}
    	}
    
    
    public synchronized void insertAllEventsWallPosts(ArrayList<FbFeed> myFbFeeds) {
    	lock.lock();
    	try {
    	deleteEventsWallposts();
    	//List<Person> friends = new ArrayList<Person>();
    	// Select All Query
    	//for(int i=0;i<myFbFeeds.size();i++){
    	for(int i=0;i<myFbFeeds.size();i++){
        insertEventWallpost(myFbFeeds.get(i).getEvId(), myFbFeeds.get(i).getMessage(), myFbFeeds.get(i).getPostId(), myFbFeeds.get(i).getCreatedTime(), myFbFeeds.get(i).getUserId(), myFbFeeds.get(i).getUserName());
    		//myFriends.get(i).getName();
    		System.out.println("Inserting Message :!!!" + myFbFeeds.get(i).getMessage() + " " + myFbFeeds.get(i).getUserName());
    		System.out.println("MyFbFeed size :!!!"+ myFbFeeds.size());
    		//System.out.println(itr.next()); 
    	}
    	} finally {
    		lock.unlock();
    	}
    	}
    	
    	
    
    //added 24/10/2012
    public synchronized void insertAllEvents(ArrayList<FbEvent> myEvents) {
    	lock.lock();
    	try {
    	deleteEvent();
    	//List<Person> friends = new ArrayList<Person>();
    	// Select All Query
    	for(int i=0;i<myEvents.size();i++){
        insertEvent(myEvents.get(i).getTitle(), myEvents.get(i).getId(),myEvents.get(i).getStartTime(), 
        		myEvents.get(i).getEndTime(), myEvents.get(i).getLocation(), myEvents.get(i).getCreator(), myEvents.get(i).getLatitude(), 
        		myEvents.get(i).getLongitude(), myEvents.get(i).getUpdateTime(), myEvents.get(i).getDescription());
    		//myFriends.get(i).getName();
    		System.out.println("insert event: " + myEvents.get(i).getTitle() + "created by: " + myEvents.get(i).getCreator() 
    				+ "Location : " + myEvents.get(i).getLocation()
    				+ " latitude : " + myEvents.get(i).getLatitude() + " longitude : " + myEvents.get(i).getLongitude()
    				+ " update time : " + myEvents.get(i).getUpdateTime() + " description : " + myEvents.get(i).getDescription() );
    		//System.out.println(itr.next());
    	}
    	} finally {
    		lock.unlock();
    	}
    	}
    
    //added 28/10/2012
    //added 24/10/2012
    public synchronized void insertAllEventAttStatus(ArrayList<FbEventAttendingStatus> myFbEventAttendingStatus) {
    	lock.lock();
    	try {
    	deleteAttendingStatus();
    	//List<Person> friends = new ArrayList<Person>();
    	// Select All Query
    	for(int i=0;i<myFbEventAttendingStatus.size();i++){
        insertAttendingStatus(myFbEventAttendingStatus.get(i).getEid(), myFbEventAttendingStatus.get(i).getUid(), myFbEventAttendingStatus.get(i).getRsvp_status());
    		//myFriends.get(i).getName();
    		//System.out.println("the event with uid: " + myFbEventAttendingStatus.get(i).getEid() + " for the user with user id: " + myFbEventAttendingStatus.get(i).getUid()
    				//+ " has the following status :" + myFbEventAttendingStatus.get(i).getRsvp_status());
    		//System.out.println(itr.next());
    	}
    	} finally {
    		lock.unlock();
    	}
    	}
    
    //added 17/10/2012 but not tested
    //tested ok !!
    public synchronized List<FbEvent> getAllEvents() {
    	List<FbEvent> events = new ArrayList<FbEvent>();
    	// Select All Query
        String selectQuery = "SELECT  * FROM " + DATABASE_TABLEEVENTS;
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                FbEvent event = new FbEvent();
                //contact.setID(Integer.parseInt(cursor.getString(0)));
                event.setTitle(cursor.getString(1));
                event.setId(cursor.getString(2));
                event.setStartTime(cursor.getString(3));
                event.setendTime(cursor.getString(4));
                event.setLocation(cursor.getString(5));
                event.setCreator(cursor.getString(6));
                event.setLatitude(cursor.getString(7));
                event.setLongitude(cursor.getString(8));
                event.setUpdateTime(cursor.getString(9));
                event.setDescription(cursor.getString(10));
                
                // Adding contact to list
                events.add(event);
            } while (cursor.moveToNext());
        }
    	//String[] resultColumns = new String[] { KEY_ROWID, KEY_USERNAME, KEY_USERID};
    	return events;
    }  	
    	
    
  //added 06/12/2012 but not tested
   //tested
    
    public synchronized List<GpsData> getAllGpsData() {
    	List<GpsData> mygpsdata = new ArrayList<GpsData>();
    	// Select All Query
        String selectQuery = "SELECT  * FROM " + DATABASE_GPSDATA;
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                GpsData gpsdata = new GpsData();
                gpsdata.setId(Integer.parseInt(cursor.getString(0)));
                //contact.setID(Integer.parseInt(cursor.getString(0)));
                gpsdata.setEid(cursor.getString(1));
                gpsdata.setTitle(cursor.getString(2));
                gpsdata.setLatitude(cursor.getString(3));
                gpsdata.setLongitude(cursor.getString(4));
                gpsdata.setSpeed(cursor.getString(5));
                gpsdata.setAltitude(cursor.getString(6));
                gpsdata.setTime(cursor.getString(7));
                // Adding gpsdata to list mygpsdata
                mygpsdata.add(gpsdata);
            } while (cursor.moveToNext());
        }
    	//String[] resultColumns = new String[] { KEY_ROWID, KEY_USERNAME, KEY_USERID};
    	return mygpsdata;
    }
    
//added 06/12/2012 but not tested
    
    public synchronized List<GpsData> getAllGpsDataWithSpecificEid(String eid) {
    	List<GpsData> mygpsdata = new ArrayList<GpsData>();
    	// Select All Query
        String selectQuery = "SELECT  * FROM " + DATABASE_GPSDATA + " WHERE EventID = " + eid;
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                GpsData gpsdata = new GpsData();
                gpsdata.setId(Integer.parseInt(cursor.getString(0)));
                //contact.setID(Integer.parseInt(cursor.getString(0)));
                gpsdata.setEid(cursor.getString(1));
                gpsdata.setTitle(cursor.getString(2));
                gpsdata.setLatitude(cursor.getString(3));
                gpsdata.setLongitude(cursor.getString(4));
                gpsdata.setSpeed(cursor.getString(5));
                gpsdata.setAltitude(cursor.getString(6));
                gpsdata.setTime(cursor.getString(7));
                // Adding gpsdata to list mygpsdata
                mygpsdata.add(gpsdata);
            } while (cursor.moveToNext());
        }
    	//String[] resultColumns = new String[] { KEY_ROWID, KEY_USERNAME, KEY_USERID};
    	return mygpsdata;
    }
    
    
    
    	//added 14/11/2012
    	//added 17/10/2012 but not tested
        //tested ok !!
        public synchronized List<FbFeed> getAllEventFeeds() {
        	List<FbFeed> fbFeeds = new ArrayList<FbFeed>();
        	// Select All Query
            String selectQuery = "SELECT  * FROM " + DATABASE_TABLEPOSTS;
            Cursor cursor = db.rawQuery(selectQuery, null);
            if (cursor.moveToFirst()) {
                do {
                    FbFeed newFbFeed = new FbFeed();
                    //contact.setID(Integer.parseInt(cursor.getString(0)));
                    newFbFeed.setEvId(cursor.getString(1));
                    newFbFeed.setMessage(cursor.getString(2));
                    newFbFeed.setPostId(cursor.getString(3));
                    newFbFeed.setCreatedAt(cursor.getString(4));
                    newFbFeed.setUserId(cursor.getString(5));
                    newFbFeed.setUserName(cursor.getString(6));
                    
                    // Adding contact to list
                    fbFeeds.add(newFbFeed);
                } while (cursor.moveToNext());
            }
        	//String[] resultColumns = new String[] { KEY_ROWID, KEY_USERNAME, KEY_USERID};
        	return fbFeeds;	
    }
        
  //---deletes a particular event by providing eid---
    public boolean deleteEvent(String eid) 
    {
        return db.delete(DATABASE_TABLEEVENTS, KEY_ID + "=" + eid, null) > 0;
    }
    
    
    private synchronized SQLiteDatabase lockAndGetDBPointer() {
		try {
			semMDB.acquire();
		} catch (InterruptedException e) {
			//Log.e( "The acquire of semaphore was interrupted!");
			e.printStackTrace();
		}
		return db;
	}

	private void unlockDBPointer() {
		semMDB.release();
	}
    
    }