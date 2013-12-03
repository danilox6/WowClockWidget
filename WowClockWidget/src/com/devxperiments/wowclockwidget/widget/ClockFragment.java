package com.devxperiments.wowclockwidget.widget;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RemoteViews;

import com.actionbarsherlock.app.SherlockFragment;
import com.devxperiments.wowclockwidget.ClockManager;
import com.devxperiments.wowclockwidget.clocks.Clock;

public class ClockFragment extends SherlockFragment {
	
	private static final String ARG_CLOCK_INDEX = "clockIndex";
	private static final String ARG_HANDS_INDEX = "handsIndex";
	private static final String ARG_DIAL_INDEX = "dialIndex";
	private static final String ARG_DIAL_ALPHA = "dialAlpha";
	private static final String ARG_AM_PM = "isAmPm";
	
	
	
	private Clock clock;
	private Clock displayedClock;
	private int displayedClockIndex;
	
	private FrameLayout frameLayout;

	static ClockFragment newInstance(int clockIndex, Clock clock) {
		return newInstance(clockIndex, clock.getCurrentHandsIndex(), clock.getCurrentDialIndex(), clock.getDialAlpha(), clock.isAmpm());
	}
	
	static ClockFragment newInstance(int clockIndex, int handsIndex, int dialIndex, int dialOpacity, boolean isAmPm) {
		ClockFragment fragment = new ClockFragment();
		Bundle args = new Bundle();
		args.putInt(ARG_CLOCK_INDEX, clockIndex);
		args.putInt(ARG_HANDS_INDEX, handsIndex);
		args.putInt(ARG_DIAL_INDEX, dialIndex);
		args.putInt(ARG_DIAL_ALPHA, dialOpacity);
		args.putBoolean(ARG_AM_PM, isAmPm);
		fragment.setArguments(args);
		return fragment;
	}

	public void clear() {
		clock.clear(); 
		displayedClock.clear();
		frameLayout.removeAllViews();
		Log.i("CLEAR FRAGMENT", "cleared");
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Bundle args = getArguments();
		displayedClockIndex = args.getInt(ARG_CLOCK_INDEX);
		clock = ClockManager.getAvailableClocks().get(displayedClockIndex);
		displayedClock = clock;
		displayedClock.setCurrentHandIndex(args.getInt(ARG_HANDS_INDEX));
		displayedClock.setCurrentDialIndex(args.getInt(ARG_DIAL_INDEX));
		displayedClock.setDialAlpha(args.getInt(ARG_DIAL_ALPHA));
		displayedClock.setAmpm(args.getBoolean(ARG_AM_PM));
		//			 FragmentTransaction ft = getFragmentManager().beginTransaction();
		//			 ft.add(this, index+"");
		//			 ft.commit();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		frameLayout = new FrameLayout(getActivity());// (FrameLayout)
		// inflater.inflate(R.layout.widget_layout,
		// null, false);
		update();
		return frameLayout;
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		outState.putString("BUGFIX", "BUGFIX"); // BUGFIX...
		super.onSaveInstanceState(outState);
	}

	public boolean isToBeUpdated() {
		return clock.isToBeUpdated();
	}

	public void update() {
		if(getActivity()!=null)
			update(displayedClock.getWidgetRemoteViews(getActivity(),false));
		else
			Log.w("ACTIVITY NULL", "null");
	}

	public void update(RemoteViews remoteViews) {
		//			clear(); //FIXME 
		Log.i("DRAWING","hands: "+ displayedClock.getCurrentHandsIndex() + "dial: "+displayedClock.getCurrentDialIndex());
		frameLayout.removeAllViews();
		frameLayout.addView(remoteViews.apply(getActivity(), frameLayout));
//		displayedClock.clear(); 
	}

	public void setDisplayedClock(Clock displayedClock) {
		this.displayedClock = displayedClock;
	}
	
	public int getDisplayedClockIndex() {
		return displayedClockIndex;
	}
}
