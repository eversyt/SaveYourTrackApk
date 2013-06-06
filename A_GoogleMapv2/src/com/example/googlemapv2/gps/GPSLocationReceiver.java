package com.example.googlemapv2.gps;

import com.example.googlemapv2.ApkConstants;
import com.example.googlemapv2.RecordActivity;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.util.Log;

public class GPSLocationReceiver extends BroadcastReceiver
{
	private PolylineOptions route;
	private RecordActivity activity;
	private boolean recordOn;
	
	public GPSLocationReceiver(RecordActivity activity)
	{
		super();
		this.activity = activity;
		route = new PolylineOptions().width(3).color(Color.BLUE);
		recordOn = false;
	}
	
	public void setRecordOn(boolean recordOn)
	{
		this.recordOn = recordOn;
	}
	
	public boolean switchRecordOn()
	{
		recordOn = !recordOn;
		return recordOn;
	}
	
	@Override
	public void onReceive(Context context, Intent intent) 
	{
		Log.d(ApkConstants.LOG_TAG, "Receiver onReceive");
		ApkConstants.location = (Location) intent.getParcelableExtra(ApkConstants.LOCATION);
		if (recordOn)
		{
			activity.savePointToDB();
			showRouteOnMap();
		}
	}
	
	private void showRouteOnMap()
	{
		route.add(new LatLng(ApkConstants.location.getLatitude(), ApkConstants.location.getLongitude()));
		ApkConstants.map.addPolyline(route);
		ApkConstants.map.moveCamera(CameraUpdateFactory.newLatLng
				(new LatLng(ApkConstants.location.getLatitude(), ApkConstants.location.getLongitude())));
	}
	
	public boolean unregisterItself(Context context)
	{
		try {
			context.unregisterReceiver(this);
			Log.d(ApkConstants.LOG_TAG, "Receiver was unregistered");
		} catch (IllegalArgumentException e) {
			return false;
		}
		return true;		
	}
}