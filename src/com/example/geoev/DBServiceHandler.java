package com.example.geoev;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBServiceHandler {

public static final String KEY_ROWID = "_id";
public static final String KEY_LIFETIME = "LifeTime";
public static final String KEY_STARTONCE = "StartOnce";
public static final String KEY_ENDONCE = "EndOnce";

private static final String TAG = "DBServiceHandler";

private static final String DATABASE_SERVICEHANDLER = "DBServiceHandler";
private static final String DATABASE_TABLELIFETIME = "TableLifeTime";
private static final String DATABASE_TABLESTARTONCE = "TableStartOnce";

private static final int DATABASE_VERSION = 2;

private static final String DATABASE_CREATELIFETIME =
    "create table "+DATABASE_TABLELIFETIME+" (_id integer primary key autoincrement, "
    + "LifeTime text not null);";
private static final String DATABASE_CREATESTARTONCE =
    "create table "+DATABASE_TABLESTARTONCE+" (_id integer primary key autoincrement, "
    + "StartOnce text not null);";       

    
    
    
private final Context context;  

private DatabaseHelper DBHelper;
private SQLiteDatabase db;

public DBServiceHandler(Context ctx) 
{
    this.context = ctx;
    DBHelper = new DatabaseHelper(context);
}
    
private static class DatabaseHelper extends SQLiteOpenHelper 
{
    DatabaseHelper(Context context) 
    {
        super(context, DATABASE_SERVICEHANDLER, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) 
    {
        db.execSQL(DATABASE_CREATELIFETIME);
        db.execSQL(DATABASE_CREATESTARTONCE);
        Log.d(TAG, "Created all Tables");
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
public DBServiceHandler open() throws SQLException 
{
    db = DBHelper.getWritableDatabase();
    return this;
}

//---closes the database---    
public void close() 
{
    DBHelper.close();
}

//---insert lifetime once into the database---
public long insertLifeTime(String time) 
{
    ContentValues initialValues = new ContentValues();
    initialValues.put(KEY_LIFETIME, time);
    
    return db.insert(DATABASE_TABLELIFETIME, null, initialValues);
}

//---insert startOnce-Variable once into the database---
public long insertStartOnce(String start) 
{
    ContentValues initialValues = new ContentValues();
    initialValues.put(KEY_STARTONCE, start);
    
    return db.insert(DATABASE_TABLESTARTONCE, null, initialValues);
}



//---retrieves lifetime from the database---
public Cursor getLifeTime() 
{
    return db.query(DATABASE_TABLELIFETIME, new String[] {
          KEY_ROWID,
          KEY_LIFETIME,
            }, 
            null, 
            null, 
            null, 
            null, 
            null,
            null);
}


//---retrieves StartOnce from the database---
public Cursor getStartOnce() 
{
    return db.query(DATABASE_TABLESTARTONCE, new String[] {
          KEY_ROWID,
          KEY_STARTONCE,
            }, 
            null, 
            null, 
            null, 
            null, 
            null,
            null);
}



}


