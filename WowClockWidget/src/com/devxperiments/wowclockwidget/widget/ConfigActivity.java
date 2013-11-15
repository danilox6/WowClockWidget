package com.devxperiments.wowclockwidget.widget;

import java.util.Calendar;
import java.util.List;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.devxperiments.wowclockwidget.Clock;
import com.devxperiments.wowclockwidget.ClockComponent;
import com.devxperiments.wowclockwidget.ClockManager;
import com.devxperiments.wowclockwidget.Hand;
import com.devxperiments.wowclockwidget.R;
import com.viewpagerindicator.LinePageIndicator;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.WallpaperManager;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.RemoteViews;
import android.widget.SeekBar;
import android.widget.LinearLayout.LayoutParams;
import android.widget.SeekBar.OnSeekBarChangeListener;

public class ConfigActivity extends SherlockFragmentActivity implements OnPageChangeListener, OnSeekBarChangeListener {

	private int appWidgetId;
	
	private FragmentPagerAdapter adapter;
	private ViewPager pager;
	private SeekBar seekBar;
	
	private static List<Clock> clocks = ClockManager.getAvailableClocks();

	private LinearLayout handsColorsLayout;
	private LinearLayout dialColorsLayout;

	private Clock selectedClock;
	private int selectedClockIndex = -1;
	// private static int selectedHandsIndex = 0;
	// private static int selectedDialIndex = 0;

	private static ClockFragment[] fragments;

	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setResult(RESULT_CANCELED); // Cosicchè se l'operazione viene annullata, il widget manager viene notificato

		
		setContentView(R.layout.config_layout);

		Intent intent = getIntent();
		Bundle extras = intent.getExtras();
		if (extras != null)
			appWidgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);

		if (appWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID)
			finish();
		
		handsColorsLayout = (LinearLayout) findViewById(R.id.handsColorLayout);
		dialColorsLayout = (LinearLayout) findViewById(R.id.dialColorsLayout);
		
		FragmentManager fm = getSupportFragmentManager();
		
//		if(fragments!=null){
//			FragmentTransaction ft = fm.beginTransaction();
//			for(Fragment f:fragments)
//				if(f!=null){
//					ft.detach(f);
//					ft.remove(f);
//				}
//			ft.commit();
//			Log.i("FRAGMNENT_MANGER", "is empty? "+ ft.isEmpty());
//			
//		}
		
		fragments = new ClockFragment[clocks.size()];
		adapter = new ClockAdapter(fm);
		
	
		
		pager = (ViewPager) findViewById(R.id.pager);
		pager.setAdapter(adapter);

		final ImageView imageView = (ImageView) findViewById(R.id.imgBackground);

		LinePageIndicator indicator = (LinePageIndicator) findViewById(R.id.indicator);
		indicator.setViewPager(pager);

		indicator.setOnPageChangeListener(this);

		imageView.setImageDrawable(WallpaperManager.getInstance(this).getFastDrawable());

		RelativeLayout rLayout = (RelativeLayout) findViewById(R.id.previewContainer);

		Display display = getWindowManager().getDefaultDisplay();

		if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT)
			rLayout.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, display.getHeight() / 3));

		seekBar = (SeekBar) findViewById(R.id.opacitySeekBar);

		seekBar.setOnSeekBarChangeListener(this);
		
		startUpdateService();
		
		adapter.notifyDataSetChanged();
	}

	
	private void startUpdateService() {
		Calendar TIME = Calendar.getInstance();
		TIME.set(Calendar.MINUTE, 0);
		TIME.set(Calendar.SECOND, 0);
		TIME.set(Calendar.MILLISECOND, 0);
		Intent serviceIntent = new Intent(this, ClockUpdateService.class);
		PendingIntent service = PendingIntent.getService(this, 0, serviceIntent, PendingIntent.FLAG_CANCEL_CURRENT);
		AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
		alarmManager.setRepeating(AlarmManager.RTC, TIME.getTime().getTime(), 1000 * 60, service);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(R.string.strApply).setIcon(R.drawable.ic_apply).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		applyWidget();
		return true;
	}

	private void applyWidget() {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this.getApplicationContext());
		prefs.edit().putBoolean(appWidgetId + "", true).putInt(appWidgetId + ClockManager.CLOCK_INDEX_PREF, selectedClockIndex)
				.putInt(appWidgetId + ClockManager.HANDS_INDEX_PREF, +selectedClock.getCurrentHandsIndex())
				.putInt(appWidgetId + ClockManager.DIAL_INDEX_PREF, selectedClock.getCurrentDialIndex())
				.putInt(appWidgetId + ClockManager.DIAL_ALPHA_PREF, selectedClock.getDialAlpha())
				.putBoolean(appWidgetId + ClockManager.AM_PM_PREF, selectedClock.isAmpm()).commit();

		AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
		appWidgetManager.updateAppWidget(appWidgetId, selectedClock.createRemoteViews(this));

		Intent resultValue = new Intent();
		resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
		setResult(RESULT_OK, resultValue);
		finish();
	}

	public static boolean areFragmentToBeUpdated() {
		if (fragments != null)
			for (Clock c : clocks)
				if (c != null && c.isToBeUpdated())
					return true;
		return false;
	}

	public static void updateFragments() {
		for (ClockFragment f : fragments) 
			if (f != null)
				f.update();
	}

	private void free() {
//		fragments = null;
		// clocks.clear();
	}

	@Override
	protected void onDestroy() {
		free();
		super.onDestroy();
	}

	@Override
	public void onPageSelected(int position) {
		selectedClockIndex = position;
		selectedClock = clocks.get(position);
		seekBar.setProgress(selectedClock.getDialAlpha());

		handsColorsLayout.removeAllViews();
		int sizeH = selectedClock.getHands().length;
		for (int i = 0; i < sizeH; i++)
			handsColorsLayout.addView(buildColorImageView(selectedClock.getHands()[i], i, sizeH));

		dialColorsLayout.removeAllViews();
		int sizeD = selectedClock.getDials().length;
		for (int i = 0; i < sizeD; i++)
			dialColorsLayout.addView(buildColorImageView(selectedClock.getDials()[i], i, sizeD));

	}
	
//	@Override
//	public void onConfigurationChanged(Configuration newConfig) {
//	    // TODO Auto-generated method stub
//	    super.onConfigurationChanged(newConfig);
//
//	    int ot = getResources().getConfiguration().orientation;
//	    switch (ot) {
//	    case Configuration.ORIENTATION_LANDSCAPE:
//	    	containerLayout.setOrientation(LinearLayout.HORIZONTAL);
//	    	controlsLayout.setLayoutParams(new LayoutParams(0,LayoutParams.WRAP_CONTENT) );
//	    	previewLayout.setLayoutParams(new LayoutParams(0, LayoutParams.WRAP_CONTENT) );
//	        break;
//	    case Configuration.ORIENTATION_PORTRAIT:
//	    	containerLayout.setOrientation(LinearLayout.VERTICAL);
//	    	controlsLayout.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,0) );
//	    	previewLayout.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,0) );
//	        break;
//	    }
//	}

	private ImageView buildColorImageView(final ClockComponent comp, final int index, int size) {
		ImageView imageView = new ImageView(this);
		LayoutParams lp = new LinearLayout.LayoutParams(0, handsColorsLayout.getHeight());
		lp.weight = (float) (1.0 / size);
		// int margin = (int) ( colorsLayout.getHeight() / 15.0);
		// lp.setMargins(margin, margin, margin, margin);
		imageView.setLayoutParams(lp);
		imageView.setBackgroundColor(getResources().getColor(comp.getColorResId()));
		imageView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// selectedClock = hand;
				// selectedHandsIndex = index;
				if (comp instanceof Hand)
					selectedClock.setCurrentHandIndex(index);
				else
					selectedClock.setCurrentDialIndex(index);
				
				Log.i("SELECTED", selectedClockIndex+"");
				ClockFragment fragment = fragments[selectedClockIndex];
				// fragment.setDisplayedClock(hand);
				fragment.update();
			}
		});
		return imageView;
	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
		if (hasFocus)
			onPageSelected(selectedClockIndex == -1 ? 0 : selectedClockIndex);
	}

	@Override
	public void onPageScrollStateChanged(int arg0) {
	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {
	}
	class ClockAdapter extends FragmentPagerAdapter {

		public ClockAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int pos) {
			fragments[pos] = ClockFragment.newInstance(pos);
			return fragments[pos];
		}

		@Override
		public int getCount() {
			return clocks.size();
		}
		 @Override
		 public Object instantiateItem(ViewGroup container, int position) {
		     ClockFragment fragment = (ClockFragment) super.instantiateItem(container, position);
		     fragments[position] = fragment;
		     return fragment;
		 }
	}

	@Override
	public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
		if (fromUser) {
			selectedClock.setDialAlpha(progress);
			fragments[selectedClockIndex].update();
		}
	}

	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {
	}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {
		// fragments[selectedClockIndex].update();
	}
	
	

	public static class ClockFragment extends SherlockFragment {
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

		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			int index = getArguments().getInt("clockIndex");
			clock = clocks.get(index);
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
//			outState.putString("BUGFIX", "BUGFIX"); // BUGFIX...
//			super.onSaveInstanceState(outState);
		}

		public boolean isToBeUpdated() {
			return clock.isToBeUpdated();
		}

		public void update() {
			if(getActivity()!=null)
				update(displayedClock.createRemoteViews(getActivity()));
		}

		public void update(RemoteViews remoteViews) {
			frameLayout.removeAllViews();
			frameLayout.addView(remoteViews.apply(getActivity(), frameLayout));
		}

		public void setDisplayedClock(Clock displayedClock) {
			this.displayedClock = displayedClock;
		}
	}

}
