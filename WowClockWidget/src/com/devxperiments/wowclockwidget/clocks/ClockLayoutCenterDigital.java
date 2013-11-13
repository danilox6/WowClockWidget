package com.devxperiments.wowclockwidget.clocks;

import java.util.Calendar;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.widget.RemoteViews;

import com.devxperiments.wowclockwidget.Clock;
import com.devxperiments.wowclockwidget.Dial;
import com.devxperiments.wowclockwidget.Hand;
import com.devxperiments.wowclockwidget.R;

public class ClockLayoutCenterDigital extends Clock{

	private int[] overlays;
	
	public ClockLayoutCenterDigital(Hand[] hands, Dial[] dials, int overlay){
		this(hands, dials, new int[]{overlay});
	}
	
	public ClockLayoutCenterDigital(Hand[] hands, Dial[] dials, int[] overlays) {
		super(hands, dials);
		this.overlays = overlays;
	}

	@Override
	public RemoteViews createRemoteViews(Context context) {
		RemoteViews baseViews = new RemoteViews(context.getPackageName(), R.layout.clock_digital_layout);
		baseViews.setImageViewBitmap(R.id.imgDial, getDialBitmap(context));
		if(overlays != null && overlays.length != 0)
			baseViews.setImageViewBitmap(R.id.imgOverlay, BitmapFactory.decodeResource(context.getResources(), overlays[overlays.length != 1?getCurrentHandsIndex():0]));
		
		Calendar calendar = Calendar.getInstance();
		//			remoteViews.setImageViewBitmap(R.id.imgPoints, Utilities.getFontBitmapCached(this, typeface, ":", Color.BLACK, 40));
		int hoursFormat = isAmpm()?Calendar.HOUR:Calendar.HOUR_OF_DAY;
		String hours = ((calendar.get(hoursFormat)<10)?"0":"")+calendar.get(hoursFormat);
		String minutes = ((calendar.get(Calendar.MINUTE)<10)?"0":"")+calendar.get(Calendar.MINUTE);
		Bitmap timeBitmap = textAsBitmap(hours+":"+minutes, 70, context.getResources().getColor(getCurrentHands().getColorResId()));
		baseViews.setImageViewBitmap(R.id.imgTime, timeBitmap);
		
		RemoteViews handsViews = new RemoteViews(context.getPackageName(),getCurrentHands().getLayoutId());
		baseViews.addView(R.id.clockContainer, handsViews);
		return baseViews;
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
