package com.devxperiments.wowclockwidget.widget;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RemoteViews;

import com.actionbarsherlock.app.SherlockFragment;
import com.devxperiments.wowclockwidget.Clock;
import com.devxperiments.wowclockwidget.ClockManager;

public class ClockFragment extends SherlockFragment {
	private Clock clock;
	private Clock displayedClock;

	private FrameLayout frameLayout;

	static ClockFragment newInstance(int index) {
		ClockFragment fragment = new ClockFragment();
		// Supply num input as an argument.
		Bundle args = new Bundle();
		args.putInt("clockIndex", index);
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
		int index = getArguments().getInt("clockIndex");
		clock = ClockManager.getAvailableClocks().get(index);
		displayedClock = clock;
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
			update(displayedClock.createRemoteViews(getActivity()));
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
}
