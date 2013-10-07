package com.devxperiments.wowclockwidget.clocks;

import com.devxperiments.wowclockwidget.Clock;
import com.devxperiments.wowclockwidget.Dial;
import com.devxperiments.wowclockwidget.Hand;
import com.devxperiments.wowclockwidget.R;

import android.content.Context;
import android.widget.RemoteViews;

public class SimpleClock extends Clock {

	public SimpleClock() {}
	
	public SimpleClock(Hand hand, Dial dial) {
		super(hand, dial);
	}
	
	public SimpleClock(Hand[] hands, Dial[] dials) {
		super(hands, dials);
	}
	
	@Override
	public RemoteViews createRemoteViews(Context context) {
		RemoteViews baseViews = new RemoteViews(context.getPackageName(), R.layout.base_clock_layout);
		baseViews.setImageViewBitmap(R.id.imgDial, getDialBitmap(context));
		RemoteViews handsViews = new RemoteViews(context.getPackageName(),getCurrentHands().getLayoutId());
		baseViews.addView(R.id.clockContainer, handsViews);
		return baseViews;
	}
	

}
