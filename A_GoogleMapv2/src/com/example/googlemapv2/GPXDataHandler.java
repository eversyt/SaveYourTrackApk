package com.example.googlemapv2;

import java.util.ArrayList;
import java.util.List;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.example.googlemapv2.entitys.Point;
import com.example.googlemapv2.entitys.Track;

import android.util.Log;

public class GPXDataHandler extends DefaultHandler
{	
	private String element = "";
	private String trackName = "";
	private String elevationValue = "";
	private String timeValue = "";
	private long trackId;
	private boolean isPoint;
	private boolean isTrack;
	private int counter;
	private long executionTime;
	private List <Point> pointList;
	private Track track;
	private Point point;
    
	public GPXDataHandler() 
	{
		super();
		counter = 0;
		isPoint = false;
		isTrack = false;
		pointList = new ArrayList<Point>();
		trackId = 0;
		track = null;
	}

	public Track getTrack() {
		return track;
	}

	public long getTrackId() {
		return trackId;
	}
	
	public String getTrackName() {
		return trackName;
	}

	public long getExecutionTime() {
		return executionTime;
	}

	public List<Point> getPointList() {
		return pointList;
	}

	@Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException
    {
        element = localName;
        if (element.equals("trkpt"))
        {
        	point = new Point();
        	isPoint = true;
        	point.setTrackId(trackId);
        	point.setLat(Double.parseDouble(attributes.getValue("lat")));
        	point.setLon(Double.parseDouble(attributes.getValue("lon")));
        } else if (element.equals("trk"))
        {
        	isTrack = true;
        }
    }

    @Override
    public void characters(char[] c, int start, int length) throws SAXException 
    {
    	String chars = new String(c, start, length);
        chars = chars.trim();
        if (isPoint)
        {
	        if (element.equals("ele"))
	        {
	        	elevationValue = elevationValue + chars;
	        } else if (element.equals("time")) 
	        {
	        	timeValue = timeValue + chars;
	        }
	    } else if (isTrack && /*(trackName.length() == 0) &&*/ element.equals("name"))
	    {
	        	trackName = trackName + chars;
	    }
    }
  
    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
    	element = "";
    	if (localName.endsWith("trkpt"))
    	{
    		point.setEle(Double.parseDouble(elevationValue));
    		point.setTime(timeValue);
    		pointList.add(point);
    		isPoint = false;
    		counter ++;
        	elevationValue = "";
        	timeValue = "";
    	}
    }

    @Override
    public void startDocument() throws SAXException {
    	Log.d(ApkConstants.LOG_TAG,"Начало разбора документа!");
    	executionTime = System.currentTimeMillis();
    }

    @Override
    public void endDocument() throws SAXException {
    	if (counter != 0)
    	{
    		if (trackName.length() == 0)
    			trackName = ApkConstants.dataSource.dateToString(ApkConstants.dataSource.stringToDate(point.getTime()));
    		
    		Double dis = ApkConstants.dataSource.calculateTrackDistance(pointList);
    		Double ele = ApkConstants.dataSource.calculateTrackElevation(pointList);
    		String time = ApkConstants.dataSource.calculateTrackTime(pointList);
    		
    		track = new Track(trackName, dis, ele, time);
	        executionTime = System.currentTimeMillis() - executionTime;
	        Log.d(ApkConstants.LOG_TAG,"Track " + trackName + " разобран! Найдено " + pointList.size() + " points" 
	        								+ "\nTime: " + executionTime + "ms"
	        								+ "\nele: " + ele
	        								+ "\ntime: " + time + "\n\n");
    	} else
    	{
            Log.d(ApkConstants.LOG_TAG,"track " + trackName + " is empty!");    		
    	}
    }
}