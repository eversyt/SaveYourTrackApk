package com.example.googlemapv2;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import com.example.googlemapv2.entitys.Point;
import com.example.googlemapv2.entitys.Track;
import com.example.googlemapv2.gps.GPSLocationReceiver;
import com.example.googlemapv2.listeners.RecordClickListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class RecordActivity extends Activity
{
	private RecordClickListener l;
	private GPSLocationReceiver reciever;
	private ImageButton ibPlay, ibPause, ibStop;
	private TextView tvTime, tvDistance, tvAltitude;
	private double lat, lon, ele, distance;
	private EditText etTrackName;
	private Track track;
	private Date startTime;
	private Date currentTime;
	private boolean recordOn;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.record_layout);
		Log.d(ApkConstants.LOG_TAG,"RecordActivity onCreate");
		
		recordOn = false;
		registrationReciever();
		mapPreparetion();
		
		tvTime = (TextView) findViewById(R.id.rl_textview_Time);
		tvDistance = (TextView) findViewById(R.id.rl_textview_Distance);
		tvAltitude = (TextView) findViewById(R.id.rl_textview_Altitude);
		
		tvTime.setText("00:00:00");
		tvDistance.setText("0m");
		tvAltitude.setText("0m");
		
		l = new RecordClickListener(this);
		
		ibPlay = (ImageButton) findViewById(R.id.rl_button_Play_Record);
		ibPause = (ImageButton) findViewById(R.id.rl_button_Pause);
		ibStop = (ImageButton) findViewById(R.id.rl_button_Stop);
		ibPlay.setOnClickListener(l);
		ibPlay.setOnTouchListener(l);
		ibPause.setOnClickListener(l);
		ibPause.setOnTouchListener(l);
		ibStop.setOnClickListener(l);
		ibStop.setOnTouchListener(l);
		setButtonEnable();
		
		SimpleDateFormat timeFormat = new SimpleDateFormat(ApkConstants.TRACK_NAME_FORMAT, Locale.GERMANY);
		etTrackName = (EditText) findViewById(R.id.rl_edittext_Name);
		etTrackName.setText(timeFormat.format(new Date()));
	}
	
	private void registrationReciever()
	{
		reciever = new GPSLocationReceiver(this);
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(ApkConstants.LOCATION_LISTENER_ACTION);
		registerReceiver(reciever, intentFilter);
	}
	
	private boolean mapPreparetion()
	{
		ApkConstants.map = ((MapFragment) getFragmentManager().findFragmentById(R.id.rl_map))
	               .getMap();
		ApkConstants.map.setMyLocationEnabled(true);
		ApkConstants.map.getUiSettings().setMyLocationButtonEnabled(true);
		ApkConstants.map.getUiSettings().setZoomControlsEnabled(true);
		if (ApkConstants.location != null)
		{			
			ApkConstants.map.moveCamera(CameraUpdateFactory.newLatLngZoom
				(new LatLng(ApkConstants.location.getLatitude(), ApkConstants.location.getLongitude()), 15));
			ApkConstants.map.animateCamera(CameraUpdateFactory.zoomTo(15), 2000, null);
		}
		return true;
	}
	
	private void setButtonEnable()
	{
		ibPlay.setEnabled(!recordOn);
		ibPause.setEnabled(recordOn);
		ibStop.setEnabled(recordOn);
	}
	
	public void onPlayClick()
	{
		recordOn = true;
		reciever.setRecordOn(recordOn);
		ibPlay.setImageResource(R.drawable.ic_record_64_on);
		ibPause.setImageResource(R.drawable.ic_pause_64_on);
		ibStop.setImageResource(R.drawable.ic_stop_64_on);
		setButtonEnable();
		createNewTrack();
	}
	
	public void onPauseClick()
	{
		if (reciever.switchRecordOn())
		{
			ibPlay.setImageResource(R.drawable.ic_record_64_on);
			ibPause.setImageResource(R.drawable.ic_pause_64_on);
		} else
		{
			ibPlay.setImageResource(R.drawable.ic_record_64_off);
			ibPause.setImageResource(R.drawable.ic_play_64_on);
		}
	}
	
	public void onStopClick()
	{
		recordOn = false;
		reciever.setRecordOn(recordOn);
		ibPlay.setImageResource(R.drawable.ic_play_64_on);
		ibPause.setImageResource(R.drawable.ic_pause_64_off);
		ibStop.setImageResource(R.drawable.ic_stop_64_off);
		setButtonEnable();
		updateNewTrack();
		finish();
	}
	
	private boolean createNewTrack()
	{
		track = new Track();
		track.setName(etTrackName.getText().toString());
		track = ApkConstants.dataSource.addTrack(track);
		Log.d(ApkConstants.LOG_TAG,"Create new track, id " + track.getId());
		if (ApkConstants.location != null)
			savePointToDB();
		return true;
	}
	
	private void updateNewTrack()
	{
		List <Point> pointList = ApkConstants.dataSource.getPointList(track.getId());
		if (pointList.size() <= 1)
		{
			deleteNewTrack();
		} else
		{
			String name = etTrackName.getText().toString();
			String time = ApkConstants.dataSource.calculateTrackTime(pointList);
			double distance = ApkConstants.dataSource.calculateTrackDistance(pointList);
			double elevation = ApkConstants.dataSource.calculateTrackElevation(pointList);
			ApkConstants.dataSource.updateTrack(track.getId(), name, distance, elevation, time);
			setResultOfRecord();
			Log.d(ApkConstants.LOG_TAG,"Update new track, id " + track.getId());
		}
	}

	private void setResultOfRecord()
	{
		Intent intent = new Intent();
        intent.putExtra(ApkConstants.NEW_TRACK_ID, track.getId());
        setResult(RESULT_OK, intent);
	}
	
	private void deleteNewTrack()
	{
		ApkConstants.dataSource.deleteTrack(track.getId());
		Toast.makeText(this, "Track " + track.getName() + " was not saved.", Toast.LENGTH_LONG).show();
		Log.d(ApkConstants.LOG_TAG,"Delete new track, id " + track.getId());
	}
	
	public void savePointToDB()
	{
		if (startTime == null)
		{
			startTime = new Date();
			lat = ApkConstants.location.getLatitude();
			lon = ApkConstants.location.getLongitude();
		}
		currentTime = new Date();
		tvTime.setText(ApkConstants.dataSource.dateDifference(currentTime, startTime));
		distance += ApkConstants.dataSource.distanceBetween2Points(
							lat, lon,
							ApkConstants.location.getLatitude(), ApkConstants.location.getLongitude());
		if (distance < 1001)
			tvDistance.setText(new DecimalFormat("#,##0").format(distance) + "m");
		else 
			tvDistance.setText(new DecimalFormat("#,##0.0").format(distance/1000) + "km");
		
		lat = ApkConstants.location.getLatitude();
		lon = ApkConstants.location.getLongitude();
		ele = ApkConstants.location.getAltitude();
		
		tvAltitude.setText(new DecimalFormat("#,##0").format(ele) + "m");
		
		Point p = new Point(lat, lon, ele, ApkConstants.dataSource.dateToString(currentTime));
		ApkConstants.dataSource.addPoint(track.getId(), p);
		Log.d(ApkConstants.LOG_TAG,"savePointinDb" 
				+ "\nLat " + ApkConstants.location.getLatitude()
				+ "\nLon " + ApkConstants.location.getLongitude()
				+ "\trackId " + track.getId());
	}
	
	public boolean onCreateOptionsMenu(Menu menu) 
	{
		getMenuInflater().inflate(R.menu.record, menu);
		return true;
	}
	
	public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) 
        {
        case R.id.cancel:
        	finish();
            return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }
	
	public void onDestroy()
	{
    	if (recordOn)
    		deleteNewTrack();
		reciever.unregisterItself(this);
    	Log.d (ApkConstants.LOG_TAG, "RecordActivity OnDestroy");
		super.onDestroy();
	}
}