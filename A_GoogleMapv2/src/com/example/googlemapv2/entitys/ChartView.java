package com.example.googlemapv2.entitys;

import java.util.List;

import com.example.googlemapv2.ApkConstants;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.Typeface;
import android.util.Log;
import android.view.View;

public class ChartView extends View 
{	
	private Paint paintChart;
	private Paint paintLabel;
	private Paint paintBackground;
	private Path path;
	private Point point1, point2;
	private Long trackId;
	private List<Point> pointList;
	private double min;
	private double max;
	private double horizontalCorrection;
	private double verticalCorrection;
	private int numberPoints;
	private double x;
	private double y;
	
    public ChartView(Context context) 
    {
        super(context);
        paintChart = new Paint();
        paintLabel = new Paint();
        paintBackground = new Paint();
        path = new Path();
    }
    
    public ChartView(Context context, Long trackId)
    {
    	this(context);
    	setTrackId(trackId);
    }
    
    public void setTrackId(Long trackId)
    {
    	this.trackId = trackId;
    	
    	pointList = ApkConstants.dataSource.getPointList(trackId);
    	numberPoints = pointList.size();
    	min = pointList.get(0).getEle();
    	max = pointList.get(0).getEle();
    	double currentEle;
    	for (int i = 0; i < pointList.size(); i++)
    	{
    		currentEle = pointList.get(i).getEle();
    		max = ((currentEle > max) ? currentEle : max);
    		min = ((currentEle < min) ? currentEle : min);
    	}
    }
 
    @Override
    public void onDraw(Canvas canvas) 
    {
    	int width = getWidth();
    	int height = getHeight();
    	verticalCorrection = (height - ApkConstants.CHART_OFFSET_TOP
    								 - ApkConstants.CHART_OFFSET_BOTTOM) / (max - min);
    	horizontalCorrection = (width - ApkConstants.CHART_OFFSET_LEFT
    								  - ApkConstants.CHART_OFFSET_RIGHT)
    								  / ApkConstants.dataSource.getTrack(trackId).getDistance();
    	
    	paintChart.setColor(Color.argb(120, 0, 0, 255));
    	paintChart.setTextSize(20);
    	paintChart.setAntiAlias(true);
    	paintChart.setStyle(Style.FILL_AND_STROKE);
    	paintChart.setStrokeWidth(2);
    	
    	paintLabel.setColor(Color.WHITE);
    	paintLabel.setTextSize(20);
    	paintLabel.setAntiAlias(true);
    	paintLabel.setTypeface(Typeface.DEFAULT_BOLD);
    	
    	paintBackground.setColor(Color.argb(120, 0, 0, 0));
    	
    	canvas.drawPaint(paintBackground);
    	
    	point1 = pointList.get(0);
    	x = ApkConstants.CHART_OFFSET_LEFT;
    	y = (max - point1.getEle()) * verticalCorrection + ApkConstants.CHART_OFFSET_TOP;
    	
    	path.moveTo(ApkConstants.CHART_OFFSET_LEFT, height - ApkConstants.CHART_OFFSET_BOTTOM);
    	path.lineTo((float) x, (float) y);
    	
    	float maxX = (float) x;
    	float minX = (float) x;
    	
    	for (int i = 1; i < numberPoints; i++)
    	{  			
    		point2 = pointList.get(i);

    		x = x + ApkConstants.dataSource.distanceBetween2Points(
    				point1.getLat(), point1.getLon(), point2.getLat(), point2.getLon())
    				* horizontalCorrection;
    		y = (max - point2.getEle()) * verticalCorrection + ApkConstants.CHART_OFFSET_TOP;
    		
        	path.lineTo((float) x, (float) y);
        	
        	point1 = point2;
        	
    		if (max == point1.getEle())
    			maxX = (float) x;
    		if (min == point1.getEle())
    			minX = (float) x;
    	}
    	
		path.lineTo(width - ApkConstants.CHART_OFFSET_RIGHT, height - ApkConstants.CHART_OFFSET_BOTTOM);
		path.lineTo(ApkConstants.CHART_OFFSET_LEFT, height - ApkConstants.CHART_OFFSET_BOTTOM);
		path.close();
		
		canvas.drawPath(path, paintChart);
		path.reset();
		
		canvas.drawLine(maxX, height - ApkConstants.CHART_OFFSET_BOTTOM
					  , maxX, ApkConstants.CHART_OFFSET_TOP - 4, paintLabel);
		canvas.drawCircle(maxX, ApkConstants.CHART_OFFSET_TOP, 4, paintLabel);
		canvas.drawCircle(maxX, ApkConstants.CHART_OFFSET_TOP, 2, paintBackground);
		if (width < maxX + 50)
			canvas.drawText((int) max + "m", maxX - 54, ApkConstants.CHART_OFFSET_TOP, paintLabel);
		else
			canvas.drawText((int) max + "m", maxX + 4, ApkConstants.CHART_OFFSET_TOP, paintLabel);
		
		canvas.drawCircle(minX, height - ApkConstants.CHART_OFFSET_BOTTOM, 4, paintLabel);
		canvas.drawCircle(minX, height - ApkConstants.CHART_OFFSET_BOTTOM, 2, paintBackground);
		if (width < minX + 50)
			canvas.drawText((int) min + "m", minX - 54, height - ApkConstants.CHART_OFFSET_BOTTOM, paintLabel);
		else
			canvas.drawText((int) min + "m", minX + 4, height - ApkConstants.CHART_OFFSET_BOTTOM, paintLabel);
		
    	Log.d(ApkConstants.LOG_TAG, "chart is drawing");
    }
}
