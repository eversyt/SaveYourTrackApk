package com.example.googlemapv2.entitys;

public class Track 
{
	private long id;
	private String name;
	private double distance;
	private double elevation;
	private String time;
	
	public Track (long id, String name, double distance, double elevation, String time)
	{
		this.id = id;
		this.name = name;
		this.distance = distance;
		this.elevation = elevation;
		this.time = time;
	}
	
	public Track (String name, double distance, double elevation, String time)
	{
		this(0, name, distance, elevation, time);
	}
	
	public Track ()
	{
		this(0, "", 0, 0, "");
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public double getDistance() {
		return distance;
	}

	public void setDistance(double distance) {
		this.distance = distance;
	}

	public double getElevation() {
		return elevation;
	}

	public void setElevation(double evaluation) {
		this.elevation = evaluation;
	}
	
	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}
}
