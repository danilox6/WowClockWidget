package com.devxperiments.wowclockwidget.clocks;

import com.devxperiments.wowclockwidget.Clock;
import com.devxperiments.wowclockwidget.Dial;
import com.devxperiments.wowclockwidget.Hand;
import com.devxperiments.wowclockwidget.R;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.widget.RemoteViews;

public class PreciseClock extends Clock {
	
	private int[] overlays = null;
	
	int[] innerOverlays =  new int[] {
			R.drawable.clk_overlay_precise_i_a,
			R.drawable.clk_overlay_precise_i_b,
			R.drawable.clk_overlay_precise_i_c,
			R.drawable.clk_overlay_precise_i_d,
			R.drawable.clk_overlay_precise_i_e,
			R.drawable.clk_overlay_precise_i_f,
			R.drawable.clk_overlay_precise_i_g,
			R.drawable.clk_overlay_precise_i_h,
			R.drawable.clk_overlay_precise_i_i};

	public PreciseClock() {}
	
	public PreciseClock(Hand hand, Dial dial) {
		super(hand, dial);
	}
	
	public PreciseClock(Hand[] hands, Dial[] dials) {
		super(hands, dials);
	}
	
	public PreciseClock(Hand[] hands, Dial[] dials, int overlay){
		this(hands, dials, new int[]{overlay});
	}
	
	public PreciseClock(Hand[] hands, Dial[] dials, int[] overlays) {
		this(hands, dials);
		this.overlays = overlays;
	}
	
	@Override
	public RemoteViews createRemoteViews(Context context) {
		RemoteViews baseViews = new RemoteViews(context.getPackageName(), R.layout.clock_precise_layout);
		baseViews.setImageViewBitmap(R.id.imgDial, getDialBitmap(context));
		
		if(overlays != null && overlays.length != 0)
			baseViews.setImageViewBitmap(R.id.imgOverlay, BitmapFactory.decodeResource(context.getResources(), overlays[overlays.length != 1?getCurrentHandsIndex():0]));
		
		baseViews.setImageViewBitmap(R.id.imgInnerOverlay, BitmapFactory.decodeResource(context.getResources(), innerOverlays[getCurrentHandsIndex()]));
		
		RemoteViews handsViews = new RemoteViews(context.getPackageName(),getCurrentHands().getLayoutId());
		baseViews.addView(R.id.clockContainer, handsViews);
		return baseViews;
	}
	

}
