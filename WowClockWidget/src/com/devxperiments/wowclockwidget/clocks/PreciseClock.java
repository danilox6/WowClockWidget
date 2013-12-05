package com.devxperiments.wowclockwidget.clocks;

import com.devxperiments.wowclockwidget.Dial;
import com.devxperiments.wowclockwidget.Hand;
import com.devxperiments.wowclockwidget.R;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.RemoteViews;

public class PreciseClock extends Clock {
	
	private Bitmap innerOverlayBitmap;
	
	private int[] innerOverlays =  new int[] {
			R.drawable.clk_overlay_precise_i_a,
			R.drawable.clk_overlay_precise_i_b,
			R.drawable.clk_overlay_precise_i_c,
			R.drawable.clk_overlay_precise_i_d,
			R.drawable.clk_overlay_precise_i_e,
			R.drawable.clk_overlay_precise_i_f,
			R.drawable.clk_overlay_precise_i_g,
			R.drawable.clk_overlay_precise_i_h,
			R.drawable.clk_overlay_precise_i_i,
			R.drawable.clk_overlay_precise_i_j,
			R.drawable.clk_overlay_precise_i_k,
			R.drawable.clk_overlay_precise_i_l};

	public PreciseClock(Hand[] hands, Dial[] dials, int... overlays) {
		super(R.layout.clock_precise_layout, hands, dials, overlays);
	}
	
	@Override
	protected void customizeRemoteViews(Context context, RemoteViews remoteViews) {
		innerOverlayBitmap = BitmapFactory.decodeResource(context.getResources(), innerOverlays[getCurrentHandsIndex()]);
		remoteViews.setImageViewBitmap(R.id.imgInnerOverlay, innerOverlayBitmap);
	}
	
	@Override
	public void clear() {
		super.clear();
		if(innerOverlayBitmap!=null)
			innerOverlayBitmap.recycle();
	}

}
