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

public class ClockLayout2 extends Clock{

	private final int[] overlays = new int[]{R.drawable.clock2_overlay_a,R.drawable.clock2_overlay_b,R.drawable.clock2_overlay_c,R.drawable.clock2_overlay_d,R.drawable.clock2_overlay_e};
	
	
	public ClockLayout2(Hand[] hands, Dial[] dials) {
		super(hands, dials);
	}

	@Override
	public RemoteViews createRemoteViews(Context context) {
		RemoteViews baseViews = new RemoteViews(context.getPackageName(), R.layout.clock2_base_layout);
		baseViews.setImageViewBitmap(R.id.imgDial, getDialBitmap(context));
		baseViews.setImageViewBitmap(R.id.imgOverlay, BitmapFactory.decodeResource(context.getResources(), overlays[getCurrentHandsIndex()]));
		
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
