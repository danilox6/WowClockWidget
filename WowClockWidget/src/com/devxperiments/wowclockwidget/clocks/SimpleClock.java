package com.devxperiments.wowclockwidget.clocks;

import com.devxperiments.wowclockwidget.Clock;
import com.devxperiments.wowclockwidget.Dial;
import com.devxperiments.wowclockwidget.Hand;
import com.devxperiments.wowclockwidget.R;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.RemoteViews;

public class SimpleClock extends Clock {
	
	private int[] overlays = null;
	private Bitmap overlayBitmap; 
	
	public SimpleClock() {}
	
	public SimpleClock(Hand hand, Dial dial) {
		super(hand, dial);
	}
	
	public SimpleClock(Hand[] hands, Dial[] dials) {
		super(hands, dials);
	}
	
	public SimpleClock(Hand[] hands, Dial[] dials, int overlay){
		this(hands, dials, new int[]{overlay});
	}
	
	public SimpleClock(Hand[] hands, Dial[] dials, int[] overlays) {
		this(hands, dials);
		this.overlays = overlays;
	}
	
	@Override
	public RemoteViews createRemoteViews(Context context) {
		RemoteViews baseViews = new RemoteViews(context.getPackageName(), R.layout.base_clock_layout);
		baseViews.setImageViewBitmap(R.id.imgDial, getDialBitmap(context));
		
		if(overlays != null && overlays.length != 0){
			overlayBitmap =  BitmapFactory.decodeResource(context.getResources(), overlays[overlays.length != 1?getCurrentHandsIndex():0]);
			baseViews.setImageViewBitmap(R.id.imgOverlay,overlayBitmap);
		}
		
		baseViews.removeAllViews(R.id.clockContainer);
		RemoteViews handsViews = new RemoteViews(context.getPackageName(),getCurrentHands().getLayoutId());
		baseViews.addView(R.id.clockContainer, handsViews);
		
		baseViews.setOnClickPendingIntent(R.id.clockContainer, getDefaultClockPendingIntent(context));
		return baseViews;
	}
	
	@Override
	public void clear() {
		super.clear();
		if(overlayBitmap!=null)
			overlayBitmap.recycle();
	}

}
