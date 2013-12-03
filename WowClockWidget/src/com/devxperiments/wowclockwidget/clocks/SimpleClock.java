package com.devxperiments.wowclockwidget.clocks;

import com.devxperiments.wowclockwidget.Dial;
import com.devxperiments.wowclockwidget.Hand;
import com.devxperiments.wowclockwidget.R;

import android.content.Context;
import android.widget.RemoteViews;

public class SimpleClock extends Clock {
	
	public SimpleClock(Hand[] hands, Dial[] dials, int... overlays) {
		super(R.layout.base_clock_layout, hands, dials, overlays);
	}
	
	@Override
	protected void customizeRemoteViews(Context context, RemoteViews remoteViews) {}

}
