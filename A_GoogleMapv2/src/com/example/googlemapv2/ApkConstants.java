package com.example.googlemapv2;

import android.location.Location;

import com.example.googlemapv2.database.TracksDataSource;
import com.google.android.gms.maps.GoogleMap;

public class ApkConstants 
{
// Logging, Debugging
	public static final String LOG_TAG = "My App";
	public static final String ERROR_MSG = "An error occured, please try again later";
// Import gpx-files
	public static final String SHORTFILENAME = "file";
	public static final String TIME_FORMAT = "yyyy'-'MM'-'dd'T'HH':'mm':'ss'Z'";
	public static final String TRACK_NAME_FORMAT = "'Track_'ddMMyy_HHmm";
// Location Listener`s constants
	public static final String LOCATION_LISTENER_ACTION = "GPS_ACTION";
	public static final String LOCATION = "LOCATION";
	public static final long LOCATION_LISTENER_MIN_TIME = 4*1000;
	public static final float LOCATION_LISTENER_MIN_DISTANCE = 20.0f; //50.0f
// Minimal gesture detection
	public static final int FLING_DISTANCE = 50;
	public static final int FLING_VELOCITY = 150;
// Popup-Windows location
	public static final int POPUP_X = 10;
	public static final int POPUP_Y = 10;
// Display track on map offset
	public static final int TRACK_OFFSET = 20;
// Display chart offset
	public static final int CHART_OFFSET_TOP = 20;
	public static final int CHART_OFFSET_BOTTOM = 10;
	public static final int CHART_OFFSET_LEFT = 10;
	public static final int CHART_OFFSET_RIGHT = 10;
// Mean Earth Radius
	public static final double MEAN_EARTH_RADIUS = 6371302.0;
// Minimal altitude for calculation of track elevation
	public static final double ELEVATION_LIMIT = 10;
// Database constants
    public static final String TRACKS_TABLE1_NAME = "TRACKLIST";
    public static final String TRACKS_TABLE2_NAME = "TRACKPOINTS";
    public static final String TABLE1_ID = "_id";
    public static final String TABLE1_NAME = "Name";
    public static final String TABLE1_DISTANCE = "Distance";
    public static final String TABLE1_ELEVATION = "Elevation";
    public static final String TABLE1_TIME = "Time";
    public static final String TABLE2_ID = "_id";
    public static final String TABLE2_TRACKID = "Track_id";
    public static final String TABLE2_LAT = "Latitude";
    public static final String TABLE2_LON = "Longitude";
    public static final String TABLE2_ELE = "Elevation";
    public static final String TABLE2_TIME = "Time";
    public static final String DATABASE_NAME = "TRACKS.db";
    public static final int DATABASE_VERSION = 1;
// Result of RecordActivity
    public static final String NEW_TRACK_ID = "NEW_TRACK_ID";
// Main variables
    // google map
 	public static GoogleMap map = null;
 	// work with database
 	public static TracksDataSource dataSource = null;
	// current location
 	public static Location location = null;
}
