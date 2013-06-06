package com.example.googlemapv2;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.example.googlemapv2.database.TracksDataSource;
import com.example.googlemapv2.entitys.Track;
import com.example.googlemapv2.gps.GPSTrackerService;
import com.example.googlemapv2.listeners.NewButtonClickListener;
import com.example.googlemapv2.listeners.TrackListClickListener;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;

public class MainActivity extends Activity
{	
	private Intent gpsService;
	private TrackListClickListener tlcListener;
	private ListView lvList;
	private long newTrackId = 0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.map_layout);
		
		ApkConstants.dataSource = new TracksDataSource(this);		
		Log.d (ApkConstants.LOG_TAG, "OnCreate");
		
		tlcListener = new TrackListClickListener(this);
		listPreparetion();
		
		Button buttonNew = (Button) findViewById(R.id.ml_button_New);
		NewButtonClickListener nbcListener = new NewButtonClickListener(this);
		buttonNew.setOnTouchListener(nbcListener);
		buttonNew.setOnClickListener(nbcListener);

		gpsService = new Intent(MainActivity.this, GPSTrackerService.class);
		startService(gpsService);
	}

	private void mapPreparetion()
	{
		ApkConstants.map = ((MapFragment) getFragmentManager().findFragmentById(R.id.ml_map))
	               .getMap();
		ApkConstants.map.setMyLocationEnabled(true);
		ApkConstants.map.getUiSettings().setMyLocationButtonEnabled(true);
		ApkConstants.map.getUiSettings().setZoomControlsEnabled(true);
		ApkConstants.map.clear();
		if ((newTrackId == 0) && (ApkConstants.location != null))
		{
			ApkConstants.map.moveCamera(CameraUpdateFactory.newLatLngZoom
				(new LatLng(ApkConstants.location.getLatitude(), ApkConstants.location.getLongitude()), 15));
			ApkConstants.map.animateCamera(CameraUpdateFactory.zoomTo(10), 2000, null);
		}
	}

	@SuppressWarnings("unused")
	private void importGPXFiles()
	{
		List <Track> trackList = ApkConstants.dataSource.getTrackList();
		for (int i = 0; i < trackList.size(); i++)
			ApkConstants.dataSource.deleteTrack(trackList.get(i).getId());
	
		if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
		      Log.d(ApkConstants.LOG_TAG, "SD-карта не доступна: " + Environment.getExternalStorageState());
		} else
		{
			String folderName = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" 
																			+ getString(R.string.app_name);	
			String fileName;
			GPXDataHandler gpxHandler;
			Track track;
			
			for (int i = 1;i < 9; i++)
			{
				fileName = folderName  + "/" + ApkConstants.SHORTFILENAME + i + ".gpx";
				gpxHandler = parsingGPXFile(fileName);
				track = ApkConstants.dataSource.addTrack(gpxHandler.getTrack());
				ApkConstants.dataSource.addPoints(track.getId(), gpxHandler.getPointList());
			}
		}
	}
	
	public boolean listPreparetion()
	{
		ApkConstants.dataSource.open();

// Test: start import gpx-files from SD
		
//		importGPXFiles();
		
// Test stop
		
		lvList = (ListView) findViewById(R.id.ml_listview_Tracks);
		ListAdapter adapter = new ListSimpleCursorAdapter(this
		        , R.layout.list_element
		        , ApkConstants.dataSource.getTracksCursor()
		        , new String[]{ApkConstants.TABLE1_NAME, ApkConstants.TABLE1_DISTANCE, ApkConstants.TABLE1_ID}
				, new int[]{R.id.le_textview_Name, R.id.le_textview_Distance, R.id.le_textview_Time}
				, 0);
		lvList.setAdapter(adapter);	
		lvList.setOnItemClickListener(tlcListener);
		lvList.setOnItemLongClickListener(tlcListener);
		if (newTrackId != 0)
		{
			LinearLayout ll = (LinearLayout) findViewById(R.id.ml_mapLayout);
			tlcListener.showTrack(ll, newTrackId);
			newTrackId = 0;
			Log.d(ApkConstants.LOG_TAG, "newTrackId is " + newTrackId);
		}
		return true;
	}
	
	private GPXDataHandler parsingGPXFile (String fileName)
	{
		GPXDataHandler gpxHandler = null;
		Log.d(ApkConstants.LOG_TAG, "GPXDataHandler in MainActivity");
		String error = "";
		try
        {
    		SAXParserFactory factory = SAXParserFactory.newInstance();
    		SAXParser parser = factory.newSAXParser();;
    		gpxHandler = new GPXDataHandler();
    		XMLReader xr = parser.getXMLReader();
    		xr.setContentHandler(gpxHandler);
    		xr.parse(new InputSource(new FileInputStream(fileName))); 
        } catch (FileNotFoundException e)
        {
        	error = e.getMessage();
        } catch (ParserConfigurationException e) // Parser Error
        {
        	error = e.getMessage();
        } catch (SAXException e) // SAX Error
        {
        	error = e.getMessage();
        } catch (IOException e) // Input Error
        {
        	error = e.getMessage();
        }
		if (error.length() != 0)
			Toast.makeText(this
					, error
					, Toast.LENGTH_LONG).show();
		return gpxHandler;
	}
	  
	public boolean onCreateOptionsMenu(Menu menu) 
	{
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	public boolean onOptionsItemSelected(MenuItem item) 
	{
        switch (item.getItemId()) 
        {
        case R.id.quit:
        	ApkConstants.dataSource.close();
            finish();
            return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }
	
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == 1 && resultCode == RESULT_OK) {
            newTrackId = intent.getExtras().getLong(ApkConstants.NEW_TRACK_ID, 0);
            Log.d (ApkConstants.LOG_TAG, "onActivityResult " + newTrackId);
        }
    }
	
	@Override	
	protected void onStart() 
	{
		super.onStart();
		mapPreparetion();
		listPreparetion();
		Log.d (ApkConstants.LOG_TAG, "OnStart");
	}
	
	@Override
	protected void onStop() 
	{
		super.onStop();
		tlcListener.closePopUp();
		Log.d (ApkConstants.LOG_TAG, "OnStop");
	}
	
	@Override
	public void onDestroy()
	{
		stopService(gpsService);
    	Log.d (ApkConstants.LOG_TAG, "OnDestroy");
		super.onDestroy();
	}
}