package com.devxperiments.wowclockwidget.clocks;

import java.util.Calendar;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.widget.RemoteViews;

import com.devxperiments.wowclockwidget.Dial;
import com.devxperiments.wowclockwidget.Hand;
import com.devxperiments.wowclockwidget.R;

public class ClockLayoutCenterDigital extends Clock{

	public ClockLayoutCenterDigital(Hand[] hands, Dial[] dials){
		this(hands, dials, null);
	}
	
	public ClockLayoutCenterDigital(Hand[] hands, Dial[] dials, int overlay){
		this(hands, dials, new int[]{overlay});
	}
	
	public ClockLayoutCenterDigital(Hand[] hands, Dial[] dials, int... overlays) {
		super(R.layout.clock_digital_layout, hands, dials, overlays);
	}

	@Override
	protected void customizeRemoteViews(Context context, RemoteViews remoteViews) {
		Calendar calendar = Calendar.getInstance();
		int hoursFormat = isAmpm()?Calendar.HOUR:Calendar.HOUR_OF_DAY;
		String hours = ((calendar.get(hoursFormat)<10)?"0":"")+calendar.get(hoursFormat);
		if(hoursFormat == Calendar.HOUR && hours.equals("00"))
			hours = "12";
		String minutes = ((calendar.get(Calendar.MINUTE)<10)?"0":"")+calendar.get(Calendar.MINUTE);
		Bitmap timeBitmap = textAsBitmap(hours+":"+minutes, 70, context.getResources().getColor(getCurrentHands().getColorResId()));
		remoteViews.setImageViewBitmap(R.id.imgTime, timeBitmap);
	}
	
	public Bitmap textAsBitmap(String text, float textSize, int textColor) {
		
		int clockSize = 480;
		
	    Paint paint = new Paint();
	    paint.setTextSize(textSize);
	    paint.setColor(textColor);
	    paint.setAntiAlias(true);
	    paint.setTextAlign(Paint.Align.LEFT);
	    int width = (int) (paint.measureText(text) + 0.5f); // round
	    float baseline = (int) (-paint.ascent() + 0.5f); // ascent() is negative
//	    int height = (int) (baseline + paint.descent() + 0.5f);
	    Bitmap image = Bitmap.createBitmap(clockSize, clockSize, Bitmap.Config.ARGB_8888);
	    Canvas canvas = new Canvas(image);
	    canvas.drawText(text, clockSize/2 - width/2, clockSize/2 + baseline/2, paint);
	    return image;
	}

}
