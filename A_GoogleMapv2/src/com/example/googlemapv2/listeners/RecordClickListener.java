package com.example.googlemapv2.listeners;

import com.example.googlemapv2.ApkConstants;
import com.example.googlemapv2.R;
import com.example.googlemapv2.RecordActivity;

import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;

public class RecordClickListener implements View.OnClickListener, View.OnTouchListener
{
	private RecordActivity activity;
	
	public RecordClickListener (RecordActivity activity)
	{
		this.activity = activity;
	}
	
	@Override
	public void onClick(View v) 
	{
		switch (v.getId())
		{	
			case R.id.rl_button_Play_Record: 
				activity.onPlayClick();
				Log.d(ApkConstants.LOG_TAG,"onClick play");
				break;
			case R.id.rl_button_Pause:
				activity.onPauseClick();
				Log.d(ApkConstants.LOG_TAG,"onClick pause");
				break;
			case R.id.rl_button_Stop:
				activity.onStopClick();
				Log.d(ApkConstants.LOG_TAG,"onClick stop");
				break;
		}
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) 
	{
		LinearLayout ll = ((LinearLayout) v.getParent());
		switch (event.getAction()) 
		{
		    case MotionEvent.ACTION_DOWN: // touch down
		    	ll.setBackgroundColor(v.getResources().getColor(R.color.recordButtonPressed));
		    	break;
		    case MotionEvent.ACTION_MOVE: // move
		    	break;
		    case MotionEvent.ACTION_UP: // touch up
		    case MotionEvent.ACTION_CANCEL:
		    	ll.setBackgroundColor(v.getResources().getColor(R.color.recordButtonNormal));
		    	break;
	    }
		Log.d(ApkConstants.LOG_TAG,"onTouch");
		return false;
	}
}
