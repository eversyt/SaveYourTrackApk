package com.example.googlemapv2.listeners;

import com.example.googlemapv2.MainActivity;
import com.example.googlemapv2.R;
import com.example.googlemapv2.RecordActivity;

import android.content.Intent;
import android.view.MotionEvent;
import android.view.View;

public class NewButtonClickListener implements View.OnClickListener, View.OnTouchListener
{	
	private MainActivity activity;
	
	public NewButtonClickListener(MainActivity activity)
	{
		this.activity = activity;
	}
	
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		switch (event.getAction()) 
		{
		    case MotionEvent.ACTION_DOWN: // touch down
		    	v.setBackgroundColor(v.getResources().getColor(R.color.recordButtonPressed));
		    	break;
		    case MotionEvent.ACTION_MOVE: // move
		    	break;
		    case MotionEvent.ACTION_UP: // touch up
		    case MotionEvent.ACTION_CANCEL:
		    	v.setBackgroundColor(v.getResources().getColor(R.color.recordButtonNormal));
		    	break;
	   }
		return false;
	}

	@Override
	public void onClick(View v) 
	{
		Intent intent = new Intent(v.getContext(), RecordActivity.class);
//		intent.putExtra(ApkConstants.LOCATION, ApkConstants.location);
		activity.startActivityForResult(intent, 1);
	}
}
