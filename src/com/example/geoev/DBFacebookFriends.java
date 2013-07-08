package com.example.geoev;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBFacebookFriends {

public static final String KEY_ROWID = "_id";

public static final String KEY_NAME = "UserName";
public static final String KEY_ID = "UserID";

private static final String TAG = "DBFacebookFriends";

private static final String DATABASE_NAME = "userdataFBFriends";
private static final String DATABASE_TABLEFRIENDS = "FriendsInfo";


private static final int DATABASE_VERSION = 2;

private static final String DATABASE_CREATE_TABLEFRIENDS =
    "create table "+DATABASE_TABLEFRIENDS+" (_id integer primary key autoincrement, "
    + "UserName integer not null, UserID text not null);";



    
private final Context context;  

private DatabaseHelper DBHelper;
private SQLiteDatabase db;

public DBFacebookFriends(Context ctx) 
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
            
        db.execSQL(DATABASE_CREATE_TABLEFRIENDS);
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

public List<Person> getAllFriends() {
	List<Person> friends = new ArrayList<Person>();
	return friends;
}

//---opens the database---
public DBFacebookFriends open() throws SQLException 
{
    db = DBHelper.getWritableDatabase();
    return this;
}

//---closes the database---    
public void close() 
{
    DBHelper.close();
}


public long insertFriend(String name, String id) 
{
	//int in = Integer.valueOf(id);
    ContentValues initialValues = new ContentValues();
    initialValues.put(KEY_NAME, name);
    initialValues.put(KEY_ID, id);       
    
    return db.insert(DATABASE_TABLEFRIENDS, null, initialValues);
}      


 
public Cursor getFriend() 
 {
     return db.query(DATABASE_TABLEFRIENDS, new String[] {
           KEY_ROWID,
           KEY_NAME,
           KEY_ID,                  
             }, 
             null, 
             null, 
             null, 
             null, 
             null,
             null);
 }    

public void deleteFriend()
{
    db.delete(DATABASE_TABLEFRIENDS, null, null);
}


}