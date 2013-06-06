package com.example.googlemapv2.database;

import com.example.googlemapv2.ApkConstants;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class TracksDBHelper extends SQLiteOpenHelper
{
    public TracksDBHelper(Context context) 
    {
    	super(context, ApkConstants.DATABASE_NAME, null, ApkConstants.DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) 
    {
    	String createBatch = "create table " + ApkConstants.TRACKS_TABLE1_NAME + "( " 
    			+ ApkConstants.TABLE1_ID + " integer primary key autoincrement, " 
    			+ ApkConstants.TABLE1_NAME + " text not null, "
    			+ ApkConstants.TABLE1_DISTANCE + " real not null, "
    			+ ApkConstants.TABLE1_ELEVATION + " real not null, "
    			+ ApkConstants.TABLE1_TIME + " text not null);";
    	db.execSQL(createBatch);
    	createBatch = "create table " + ApkConstants.TRACKS_TABLE2_NAME + "( " 
    			+ ApkConstants.TABLE2_ID + " integer primary key autoincrement, " 
    			+ ApkConstants.TABLE2_TRACKID + " integer not null, "
    			+ ApkConstants.TABLE2_LAT + " real not null, "
    			+ ApkConstants.TABLE2_LON + " real not null, "
    			+ ApkConstants.TABLE2_ELE + " real not null, "
    			+ ApkConstants.TABLE2_TIME + " text not null, " 
    			+ "FOREIGN KEY(" + ApkConstants.TABLE2_TRACKID 
    				+ ") REFERENCES " + ApkConstants.TRACKS_TABLE1_NAME + " (" + ApkConstants.TABLE1_ID + "));";
    	db.execSQL(createBatch);
    } 

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) 
    {
    	db.execSQL("DROP TABLE IF EXISTS" + ApkConstants.TRACKS_TABLE1_NAME);
       	db.execSQL("DROP TABLE IF EXISTS" + ApkConstants.TRACKS_TABLE2_NAME);
    	onCreate(db);      
    }
}
