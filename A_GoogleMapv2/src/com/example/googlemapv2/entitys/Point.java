package com.example.googlemapv2.entitys;

public class Point 
{
	private long id;
	private long trackId;
	private double lat;
	private double lon;
	private double ele;
	private String time;
	
	public Point (long id, long trackId, double lat, double lon, double ele, String time)
	{
		this.id = id;
		this.trackId = trackId;
		this.lat = lat;
		this.lon = lon;
		this.ele = ele;
		this.time = time;
	}
	
	public Point (double lat, double lon, double ele, String time)
	{
		this(0, 0, lat, lon, ele, time);
	}
	
	public Point ()
	{
		this(0, 0, 0, 0, 0, "");
	}
	
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getTrackId() {
		return trackId;
	}

	public void setTrackId(long trackId) {
		this.trackId = trackId;
	}
	
	public double getLat() {
		return lat;
	}

	public void setLat(double lat) {
		this.lat = lat;
	}

	public double getLon() {
		return lon;
	}

	public void setLon(double lon) {
		this.lon = lon;
	}

	public double getEle() {
		return ele;
	}

	public void setEle(double ele) {
		this.ele = ele;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}
}
