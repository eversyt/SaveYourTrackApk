package com.example.googlemapv2.gps;

import com.example.googlemapv2.ApkConstants;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

public class GPSTrackerService extends Service implements LocationListener
{	
	private LocationManager locationManager;
	private Location location;
	private Context context;
	
	public GPSTrackerService ()	{}
	
	@Override
    public void onCreate() 
	{
		context = getApplicationContext();
		locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
		locationManager.requestLocationUpdates(
							locationManager.getBestProvider(new Criteria(), true)
							, ApkConstants.LOCATION_LISTENER_MIN_TIME
							, ApkConstants.LOCATION_LISTENER_MIN_DISTANCE
							, this);
		
		location = getLastLocation();
		if ((location != null) && (ApkConstants.map != null))
		{
			ApkConstants.location = location;
			ApkConstants.map.moveCamera(CameraUpdateFactory.newLatLngZoom
					(new LatLng(location.getLatitude(), location.getLongitude()), 15));
			ApkConstants.map.animateCamera(CameraUpdateFactory.zoomTo(10), 2000, null);
		}
		Log.d(ApkConstants.LOG_TAG, "Service onCreate. location != 0 " + (location != null));
	}
	
	private void sendBroadcastIntent()
	{
		Intent intent = new Intent();
		intent.setAction(ApkConstants.LOCATION_LISTENER_ACTION);
		intent.putExtra(ApkConstants.LOCATION, location);
		sendBroadcast(intent);
	}

	private Location getLastLocation()
	{
		Criteria criteria = new Criteria();
	    criteria.setAccuracy(Criteria.ACCURACY_FINE);
	    criteria.setCostAllowed(false);
	    String provider = locationManager.getBestProvider(criteria, true);
		return locationManager.getLastKnownLocation(provider);
	}
	
	@Override
	public void onLocationChanged(Location location) 
	{
		this.location = location;
		ApkConstants.location = location;
		sendBroadcastIntent();
		Log.d(ApkConstants.LOG_TAG, "Service onLocationChanged");
	}

	@Override
    public void onDestroy() 
	{
        if (locationManager != null)
            locationManager.removeUpdates(GPSTrackerService.this); 
		Log.d(ApkConstants.LOG_TAG, "Service onDestroy");
		super.onDestroy();
	}
    
	@Override
	public void onProviderDisabled(String provider) {}

	@Override
	public void onProviderEnabled(String provider) {}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {}

	@Override
	public IBinder onBind(Intent arg0) {return null;}
}
