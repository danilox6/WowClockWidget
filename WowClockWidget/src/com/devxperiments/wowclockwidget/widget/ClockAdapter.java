package com.devxperiments.wowclockwidget.widget;

import java.util.List;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.ViewGroup;

import com.devxperiments.wowclockwidget.ClockManager;
import com.devxperiments.wowclockwidget.clocks.Clock;

class ClockAdapter extends FragmentStatePagerAdapter {
	
	List<Clock> clocks;

	public ClockAdapter(FragmentManager fm, List<Clock> clocks) {
		super(fm);
		this.clocks = clocks;
	}

	@Override
	public Fragment getItem(int pos) {
		Log.i("ADAPTER", "Instantiating position "+pos);
		ClockFragment fragment = ClockFragment.newInstance(pos, clocks.get(pos));
//		fragment.setDisplayedClock(clocks.get(fragment.getDisplayedClockIndex()));
		ConfigActivity.fragments[pos] = fragment;
		return ConfigActivity.fragments[pos];
	}

	@Override
	public int getCount() {
		return ClockManager.getClockCount();
	}
	@Override
	public Object instantiateItem(ViewGroup container, int position) {
		ClockFragment fragment = (ClockFragment) super.instantiateItem(container, position);
//		fragment.setDisplayedClock(clocks.get(fragment.getDisplayedClockIndex()));
		ConfigActivity.fragments[position] = fragment;
		return fragment;
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
//		((ClockFragment)object).clear();
		ConfigActivity.fragments[position] = null;
		super.destroyItem(container, position, object);
	}
	@Override
	public int getItemPosition(Object object){
		return PagerAdapter.POSITION_UNCHANGED;
	}
}
