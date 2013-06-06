package com.example.googlemapv2.database;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import com.example.googlemapv2.ApkConstants;
import com.example.googlemapv2.entitys.Point;
import com.example.googlemapv2.entitys.Track;
import com.google.android.gms.maps.model.LatLng;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class TracksDataSource 
{	
	private SQLiteDatabase db;
	private TracksDBHelper dbHelper;
	private String[] columnsTable1 = {ApkConstants.TABLE1_ID
									, ApkConstants.TABLE1_NAME
									, ApkConstants.TABLE1_DISTANCE
									, ApkConstants.TABLE1_ELEVATION
									, ApkConstants.TABLE1_TIME};
	private String[] columnsTable2 = {ApkConstants.TABLE2_ID
									, ApkConstants.TABLE2_TRACKID
									, ApkConstants.TABLE2_LAT
									, ApkConstants.TABLE2_LON
									, ApkConstants.TABLE2_ELE
									, ApkConstants.TABLE2_TIME};
	public TracksDataSource (Context context)
	{
		dbHelper = new TracksDBHelper(context);
	}
	
	public void open()
	{
		db = dbHelper.getWritableDatabase();
	}
	
	public void close()
	{
		db.close();
	}

// get Track from SQLite database by id	
	public Track getTrack(long id)
	{
        String whereBatch = ApkConstants.TABLE1_ID + " = ?";
        Cursor c = db.query(ApkConstants.TRACKS_TABLE1_NAME
		        		, columnsTable1
		        		, whereBatch
		        		, new String[]{Long.toString(id)}
				        , null, null
				        , ApkConstants.TABLE1_ID);
        Track track = null;
        if(c.getCount() > 0)
        {
               c.moveToFirst();                                       
               track = cursorToTrack(c);
        }
        c.close();
        return track;
	}
	
// get Track from SQLite database by name	
	public Track getTrack(String name)
	{
        String whereBatch = ApkConstants.TABLE1_NAME + " = ?";
        Cursor c = db.query(ApkConstants.TRACKS_TABLE1_NAME
		        		, columnsTable1
		        		, whereBatch
		        		, new String[]{name}
				        , null, null
				        , ApkConstants.TABLE1_ID);
        Track track = null;
        if(c.getCount() > 0)
        {
               c.moveToFirst();                                       
               track = cursorToTrack(c);
        }
        c.close();
        return track;
	}
	
// get Track from SQLite database by Cursor
    private Track cursorToTrack(Cursor c)
    {            
        return new Track(c.getLong(0), c.getString(1), c.getDouble(2), c.getDouble(3), c.getString(4));
    }
    
    public Track addTrack(String name, double distance, double elevation, String time)
    {
        ContentValues values = new ContentValues();
        values.put(ApkConstants.TABLE1_NAME, name);
        values.put(ApkConstants.TABLE1_DISTANCE, distance);
        values.put(ApkConstants.TABLE1_ELEVATION, elevation);
        values.put(ApkConstants.TABLE1_TIME, time);
        long insertId = db.insert(ApkConstants.TRACKS_TABLE1_NAME, null, values);            
        return this.getTrack(insertId);
	}
    
    public Track addTrack(Track track)
    {
    	return addTrack(track.getName(), track.getDistance(), track.getElevation(), track.getTime());
	}
    
    public Track updateTrack(Long id, String name, double distance, double elevation, String time)
    {
        ContentValues values = new ContentValues();
        values.put(ApkConstants.TABLE1_NAME, name);
        values.put(ApkConstants.TABLE1_DISTANCE, distance);
        values.put(ApkConstants.TABLE1_ELEVATION, elevation); 
        values.put(ApkConstants.TABLE1_TIME, time);
        String whereClause = ApkConstants.TABLE1_ID + " = ?";            
        db.update(ApkConstants.TRACKS_TABLE1_NAME, values, whereClause, new String[]{Long.toString(id)});
        return this.getTrack(id);
	}
    
    public void deleteTrack(long id)
    {
        String whereBatch = ApkConstants.TABLE1_ID + " = ?";
        db.delete(ApkConstants.TRACKS_TABLE1_NAME, whereBatch, new String[]{Long.toString(id)});
        whereBatch = ApkConstants.TABLE2_TRACKID + " = ?";
        db.delete(ApkConstants.TRACKS_TABLE2_NAME, whereBatch, new String[]{Long.toString(id)});
	}
    
// get list of all tracks
    public List<Track> getTrackList()
    {
        List<Track> result = new ArrayList<Track>();            
        Cursor c = this.getTracksCursor();
        c.moveToFirst();
        while(!c.isAfterLast()){
               result.add(cursorToTrack(c));
               c.moveToNext();
        }
        c.close();
        return result;
	}
   
// get Cursor with all tracks
    public Cursor getTracksCursor()
    {
        return db.query(ApkConstants.TRACKS_TABLE1_NAME
        			  , columnsTable1, null, null, null, null
        			  , ApkConstants.TABLE1_ID + " DESC");
	}
    
// get Point from SQLite database by id
	public Point getPoint(long id)
	{
        String whereBatch = ApkConstants.TABLE2_ID + " = ?";
        Cursor c = db.query(ApkConstants.TRACKS_TABLE2_NAME
		        		, columnsTable2
		        		, whereBatch
		        		, new String[]{Long.toString(id)}
				        , null, null
				        , ApkConstants.TABLE2_ID);
        Point point = null;
        if(c.getCount() > 0)
        {
               c.moveToFirst();                                       
               point = cursorToPoint(c);
        }
        c.close();
        return point;
	}
    
// get Point from SQLite database by cursor	
    private Point cursorToPoint(Cursor c)
    {            
        return new Point(c.getLong(0), c.getLong(1), c.getDouble(2), c.getDouble(3), c.getDouble(4), c.getString(5));
    }
	
    public Point addPoint(long trackId, Point p)
    {
        ContentValues values = new ContentValues();
        values.put(ApkConstants.TABLE2_TRACKID, trackId);
        values.put(ApkConstants.TABLE2_LAT, p.getLat());
        values.put(ApkConstants.TABLE2_LON, p.getLon());
        values.put(ApkConstants.TABLE2_ELE, p.getEle());
        values.put(ApkConstants.TABLE2_TIME, p.getTime());
        long insertId = db.insert(ApkConstants.TRACKS_TABLE2_NAME, null, values);            
        return this.getPoint(insertId);
	}
    
    public boolean addPoints(long trackId, List <Point> list)
    {
        for (Point p: list)
        	addPoint(trackId, p);
    	return true;
	}

// get list of all Points of a Track
    public List<Point> getPointList(long trackId)
    {
        List<Point> result = new ArrayList<Point>();
        Cursor c = this.getPointsCursor(trackId);
        c.moveToFirst();
        while(!c.isAfterLast()){
               result.add(cursorToPoint(c));
               c.moveToNext();
        }
        c.close();
        return result;
	}
    
// get list of all Points of a Track as LatLng
    public List<LatLng> getLatLngList(long trackId)
    {
    	List<LatLng> result = new ArrayList<LatLng>();
    	List<Point> pointList = this.getPointList(trackId);
    	for(int i = 0; i < pointList.size(); i++)
    		result.add(new LatLng(pointList.get(i).getLat(), pointList.get(i).getLon()));
    	return result;
    }
    
// get Cursor with all Points of a Track
    private Cursor getPointsCursor(long trackId)
    {
        String whereBatch = ApkConstants.TABLE2_TRACKID + " = ?";
        return db.query(ApkConstants.TRACKS_TABLE2_NAME
        		, columnsTable2
        		, whereBatch
        		, new String[]{Long.toString(trackId)}
		        , null, null
		        , ApkConstants.TABLE2_ID);
    }
    
// calculate the general distance of the Track
    public double calculateTrackDistance(List <Point> pointList)
    {
    	double result = 0;
    	if (pointList.size() <= 1)
    		return result;
    	Point point1, point2;
    	for (int i = 1; i < pointList.size(); i++)
    	{
    		point1 = pointList.get(i-1);
    		point2 = pointList.get(i);
    		result += distanceBetween2Points(point1.getLat(), point1.getLon(), point2.getLat(), point2.getLon());
    	}
    	return result;
    }
    
// calculate a distance between two points accordingly Haversine formula
    public double distanceBetween2Points (double lat1, double lon1, double lat2, double lon2)
    {
    	double dLat = Math.toRadians(lat2-lat1);
        double dlon = Math.toRadians(lon2-lon1);
        double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
                   Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                   Math.sin(dlon/2) * Math.sin(dlon/2);
    	return 2 * ApkConstants.MEAN_EARTH_RADIUS * Math.asin(Math.sqrt(a));
    }
    
 // calculate the general elevation of the Track
    public double calculateTrackElevation(List <Point> pointList)
    {
    	double result = 0;
    	if (pointList.size() <= 1)
    		return result;
    	double startPoint = pointList.get(0).getEle();
    	double runningPoint = 0;
    	for (int i = 1; i < pointList.size(); i++)
    	{
    		runningPoint = pointList.get(i).getEle();
    		
    		if (runningPoint - startPoint > ApkConstants.ELEVATION_LIMIT)
    		{
    			result = result + runningPoint - startPoint;
    			startPoint = runningPoint;
    		}
    		
    		if (startPoint - runningPoint > ApkConstants.ELEVATION_LIMIT)
    			startPoint = runningPoint;
    	}
    	
    	if (runningPoint > startPoint)
    		result = result + runningPoint - startPoint;
    	return result;
    }
    
    public String calculateTrackTime(List <Point> pointList)
    {
    	String timeStart = pointList.get(0).getTime();
		String timeEnd = pointList.get(pointList.size() - 1).getTime();
		return dateDifference(stringToDate(timeStart), stringToDate(timeEnd));
    }
    
    public String dateDifference (Date date1, Date date2)
    {
    	Calendar c1 = Calendar.getInstance();
    	Calendar c2 = Calendar.getInstance();
    	c1.setTime(date1);
    	c2.setTime(date2);
    	int diff = (int) Math.abs((c1.getTimeInMillis() - c2.getTimeInMillis()) / 1000);
    	int diffDays = diff / (24 * 60 * 60);
    	int diffHour = diff / (60 * 60) - diffDays * 24;
    	int diffMinutes = diff / 60 - diffDays * 24 * 60 - diffHour * 60;
    	int diffSeconds = diff - diffDays * 24 * 60 * 60 - diffHour * 60 * 60 - diffMinutes * 60;
    	c1.set(0, 0, diffDays, diffHour, diffMinutes, diffSeconds);
    	SimpleDateFormat sdf;
    	if (diff < 86400)
    	{
    		sdf = new SimpleDateFormat("HH':'mm':'ss", Locale.GERMANY);
    	} else if (diff < 172800)
    	{
    		sdf = new SimpleDateFormat("D' day 'HH':'mm':'ss", Locale.GERMANY);
    	} else
    	{
    		sdf = new SimpleDateFormat("DD' days 'HH':'mm':'ss", Locale.GERMANY);
    	}
		return sdf.format(c1.getTime());
    }

 // convert String to Date
    public Date stringToDate (String time)
    {
    	Date date;
    	SimpleDateFormat sdf = new SimpleDateFormat(ApkConstants.TIME_FORMAT, Locale.GERMANY);
    	try {
			date = sdf.parse(time);
		} catch (ParseException e) {
			date = new Date();
		}
    	return date;
    }
    
// convert Date to String
    public String dateToString (Date date)
    {
    	SimpleDateFormat sdf = new SimpleDateFormat(ApkConstants.TIME_FORMAT, Locale.GERMANY);
		return sdf.format(date);
    }
}
