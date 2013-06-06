package com.example.googlemapv2.listeners;

import java.util.List;

import com.example.googlemapv2.ApkConstants;
import com.example.googlemapv2.MainActivity;
import com.example.googlemapv2.R;
import com.example.googlemapv2.entitys.ChartView;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import android.database.Cursor;
import android.graphics.Color;
import android.util.Log;
import android.view.Gravity;
import android.view.GestureDetector;
import android.view.View;
import android.view.MotionEvent;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Toast;

public class TrackListClickListener implements AdapterView.OnItemClickListener
											 , AdapterView.OnItemLongClickListener
											 , View.OnTouchListener
											 , GestureDetector.OnGestureListener
{
	private PopupWindow popUp;
	private ChartView chartView;
	private long trackId;
	private GestureDetector gestureDetector;
	private LinearLayout fragmentLayout;
	private MainActivity activity;
	
	public TrackListClickListener (MainActivity activity)
	{
		this.activity = activity;
	}

	public void closePopUp()
	{
		if (popUp != null)
		{
			popUp.dismiss();
		}
		trackId = 0;
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> adapter, View v, int position, long rowId) 
	{
		Cursor c = (Cursor) adapter.getAdapter().getItem(position);
		String name = c.getString(c.getColumnIndex(ApkConstants.TABLE1_NAME));
		long trackId = c.getLong(c.getColumnIndex(ApkConstants.TABLE1_ID));
		Toast.makeText(v.getContext()
				, "Deleting of the track " + name
				, Toast.LENGTH_SHORT).show();
		ApkConstants.dataSource.deleteTrack(trackId);
		activity.listPreparetion();
		return true;
	}
	
	@Override
	public void onItemClick(AdapterView<?> adapter, View v, int position, long rowId) 
	{		
		Cursor c = (Cursor) adapter.getAdapter().getItem(position);
		Long newTrackId = c.getLong(c.getColumnIndex(ApkConstants.TABLE1_ID));
		if (trackId != newTrackId)
		{
			trackId = newTrackId;
			showTrack(v, trackId);
		}
	}
	
	public void showTrack(View v, long newTrackId)
	{
		ApkConstants.map.clear();
		
		PolylineOptions plOptions = new PolylineOptions();
		plOptions.width(3).color(Color.BLUE).geodesic(true);
		
		List<LatLng> latLngList = ApkConstants.dataSource.getLatLngList(newTrackId);
		plOptions.addAll(latLngList);
		
		ApkConstants.map.addPolyline(plOptions);
		if (latLngList.size() == 0)
		{	
			Toast.makeText(v.getContext()
					, "Track is empty"
					, Toast.LENGTH_SHORT).show();
			return;
		}
		
		BitmapDescriptor markerFlag = BitmapDescriptorFactory.fromResource(R.drawable.ic_start_flag);
		MarkerOptions marker = new MarkerOptions();
		marker.title("Start of the track").icon(markerFlag).position(latLngList.get(0));
		ApkConstants.map.addMarker(marker);
		
		markerFlag = BitmapDescriptorFactory.fromResource(R.drawable.ic_finish_flag);
		marker.title("End of the track").icon(markerFlag).position(latLngList.get(latLngList.size() - 1));
		ApkConstants.map.addMarker(marker);
		
		Toast.makeText(v.getContext()
				, ApkConstants.dataSource.getTrack(newTrackId).getName()
				, Toast.LENGTH_LONG).show();
		
		LatLngBounds.Builder builder = new LatLngBounds.Builder();
		for (LatLng l: latLngList)
			builder.include(l);
		
		fragmentLayout = (LinearLayout) v.getRootView().findViewById(R.id.ml_mapLayout);

		CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(builder.build()
					, fragmentLayout.getWidth()
					, fragmentLayout.getHeight()
					, ApkConstants.TRACK_OFFSET);
		
		ApkConstants.map.animateCamera(cu);
		showChart (v, newTrackId);
		Log.d (ApkConstants.LOG_TAG, "showTrack, ApkConstants.map = null "
				+ ((ApkConstants.map == null)||(cu == null)||(builder == null))+ "\nnewTrackId " + newTrackId);
	}
	
	private void showChart (View v, long newTrackId)
	{
		if (chartView == null)
		{
			gestureDetector = new GestureDetector(v.getContext(), this);
			
			chartView = new ChartView(v.getContext(), newTrackId);
			chartView.setOnTouchListener(this);
			popUp = new PopupWindow(chartView);
			popUp.setHeight(fragmentLayout.getHeight()/4);
			popUp.setWidth(fragmentLayout.getWidth()/4);
		}
		else 
		{
			chartView.setTrackId(newTrackId);
			chartView.invalidate();
		}
		popUp.showAtLocation(fragmentLayout, Gravity.LEFT | Gravity.BOTTOM
						   , ApkConstants.POPUP_X, ApkConstants.POPUP_Y); 
	}
	
	public boolean onTouch(View v, MotionEvent e) {
    	if (gestureDetector.onTouchEvent(e))
    	{
    		return true;
    	}
    	return false;
    }
	
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) 
	{
//  top - bottom or right - left
		if (((e2.getY() - e1.getY() > ApkConstants.FLING_DISTANCE)
				|| (e1.getX() - e2.getX() > ApkConstants.FLING_DISTANCE))
        		&& Math.abs(velocityY) > ApkConstants.FLING_VELOCITY)
		{
			popUp.update(fragmentLayout.getWidth()/4, fragmentLayout.getHeight()/4);
			ApkConstants.map.getUiSettings().setZoomControlsEnabled(true);
			return true;
			
//  bottom - top or left - right
		} else if (((e1.getY() - e2.getY() > ApkConstants.FLING_DISTANCE) 
				|| (e2.getX() - e1.getX() > ApkConstants.FLING_DISTANCE))
        		&& Math.abs(velocityY) > ApkConstants.FLING_VELOCITY)
		{
			popUp.update(fragmentLayout.getWidth() - ApkConstants.POPUP_X*2, fragmentLayout.getHeight()/4);
			ApkConstants.map.getUiSettings().setZoomControlsEnabled(false);
			return true;
		}
        return false;
    }

	@Override
	public boolean onDown(MotionEvent e) {return false;}

	@Override
	public void onLongPress(MotionEvent e) {}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {return false;}

	@Override
	public void onShowPress(MotionEvent e) {}

	@Override
	public boolean onSingleTapUp(MotionEvent e) {return false;}
}