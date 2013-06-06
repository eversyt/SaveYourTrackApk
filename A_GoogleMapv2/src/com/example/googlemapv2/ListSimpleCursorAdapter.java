package com.example.googlemapv2;

import java.text.DecimalFormat;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

public class ListSimpleCursorAdapter extends SimpleCursorAdapter
{
	private int layout;
	
	public ListSimpleCursorAdapter(Context context, int layout, Cursor c,
											String[] from, int[] to, int flags) 
	{
		super(context, layout, c, from, to, flags);
		this.layout = layout;
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) 
	{
		String name = cursor.getString(cursor.getColumnIndex(ApkConstants.TABLE1_NAME));
		double distance = cursor.getDouble(cursor.getColumnIndex(ApkConstants.TABLE1_DISTANCE));
		String time = cursor.getString(cursor.getColumnIndex(ApkConstants.TABLE1_TIME));
		
		TextView nameTV = (TextView) view.findViewById(R.id.le_textview_Name);
		TextView distanceTV = (TextView) view.findViewById(R.id.le_textview_Distance);
		TextView timeTV = (TextView) view.findViewById(R.id.le_textview_Time);

		nameTV.setText(name);
		
		if (distance < 1001)
			distanceTV.setText(new DecimalFormat("#,##0").format(distance) + "m");
		else 
			distanceTV.setText(new DecimalFormat("#,##0.0").format(distance/1000) + "km");
		
		timeTV.setText(time); 
//		String elevation = cursor.getString(cursor.getColumnIndex(TracksDBHelper.TABLE1_ELEVATION));
//		timeTV.setText(elevation + "m");
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) 
	{
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		return inflater.inflate(layout, parent, false);
	}
}
