package com.devxperiments.wowclockwidget.widget;

import java.util.Calendar;
import java.util.List;
import java.util.Random;

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
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

public class ConfigActivity extends SherlockFragmentActivity implements OnPageChangeListener, OnSeekBarChangeListener {

	private int appWidgetId;

	private FragmentStatePagerAdapter adapter;
	private ViewPager pager;
	private SeekBar seekBar;
	private CheckBox amPmBox;

	private List<Clock> clocks = ClockManager.getAvailableClocks();;

	private LinearLayout handsColorsLayout;
	private LinearLayout dialColorsLayout;
	private LinearLayout amPmLayout;
	private LinearLayout lnlRandomButton;

	private Clock selectedClock;
	private int selectedClockIndex = -1;
	// private static int selectedHandsIndex = 0;
	// private static int selectedDialIndex = 0;

	protected static ClockFragment[] fragments;

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
		amPmLayout = (LinearLayout) findViewById(R.id.lnlAmPm);
		amPmBox = (CheckBox) findViewById(R.id.ckb12hour);
		lnlRandomButton = (LinearLayout) findViewById(R.id.lnlRndButton);
		
		if(clocks == null || clocks.isEmpty())
			clocks = ClockManager.getAvailableClocks();
		
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
		adapter = new ClockAdapter(fm, clocks);


		pager = (ViewPager) findViewById(R.id.pager);
		pager.setOffscreenPageLimit(0);
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
		
		amPmBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if(fragments[selectedClockIndex]!=null){
					selectedClock.setAmpm(isChecked);
					fragments[selectedClockIndex].setDisplayedClock(selectedClock);
					fragments[selectedClockIndex].update();
				}
			}
		});

		lnlRandomButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				pickRandomColors();
			}
		});
		
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
		prefs.edit()
		.putBoolean(appWidgetId + "", true)
		.putInt(appWidgetId + ClockManager.CLOCK_INDEX_PREF, selectedClockIndex)
		.putInt(appWidgetId + ClockManager.HANDS_INDEX_PREF, +selectedClock.getCurrentHandsIndex())
		.putInt(appWidgetId + ClockManager.DIAL_INDEX_PREF, selectedClock.getCurrentDialIndex())
		.putInt(appWidgetId + ClockManager.DIAL_ALPHA_PREF, selectedClock.getDialAlpha())
		.putBoolean(appWidgetId + ClockManager.AM_PM_PREF, selectedClock.isAmpm()).commit();

		AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
		appWidgetManager.updateAppWidget(appWidgetId, selectedClock.createRemoteViews(this));
		selectedClock.clear(); 

		Intent resultValue = new Intent();
		resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
		setResult(RESULT_OK, resultValue);
		finish();
	}
	
	private void pickRandomColors() {
		Random rnd = new Random();
		int handsColor = rnd.nextInt(9); //FIXME
		int dialColor = rnd.nextInt(9); //FIXME
		while(handsColor==dialColor)
			dialColor = rnd.nextInt(9);
		
		selectedClock.setCurrentHandIndex(handsColor);
		selectedClock.setCurrentDialIndex(dialColor);

		ClockFragment fragment = fragments[selectedClockIndex];
		fragment.setDisplayedClock(selectedClock);
		fragment.update();
	}

	public static boolean areFragmentToBeUpdated() {
		if (fragments != null){
			
			return true; //FIXME
//			for(ClockFragment f : fragments)
//				if (f != null && f.isToBeUpdated())
//					return true;
		}
		return false;
	}

	public static void updateFragments() {
		for (ClockFragment f : fragments) 
			if (f != null)
				f.update();
	}

	private void free() {
		//		for(ClockFragment f :fragments )
		//			if(f!=null)
		//				f.clear();
		ClockManager.free();
		fragments = null;
		
	}

	@Override
	protected void onDestroy() {
		free();
		if (isFinishing()){
			clocks.clear();
			clocks = null;
			System.exit(0);
		}
		super.onDestroy();
	}

	@Override
	public void onPageSelected(int position) {
		selectedClockIndex = position;
		if(clocks == null || clocks.size() == 0)
			clocks = ClockManager.getAvailableClocks();
		selectedClock = clocks.get(position);
		seekBar.setProgress(selectedClock.getDialAlpha());
		amPmBox.setChecked(selectedClock.isAmpm());
		
		amPmLayout.setVisibility(selectedClock.isToBeUpdated()?View.VISIBLE:View.GONE);

		handsColorsLayout.removeAllViews();
		int sizeH = selectedClock.getHands().length;
		for (int i = 0; i < sizeH; i++)
			handsColorsLayout.addView(buildColorImageView(selectedClock.getHands()[i], i, sizeH));

		dialColorsLayout.removeAllViews();
		int sizeD = selectedClock.getDials().length;
		for (int i = 0; i < sizeD; i++)
			dialColorsLayout.addView(buildColorImageView(selectedClock.getDials()[i], i, sizeD));
		
		if(fragments[selectedClockIndex]!=null){
			fragments[selectedClockIndex].setDisplayedClock(selectedClock);
//			fragments[selectedClockIndex].update();
		}
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

				Log.i("SELECTED", selectedClockIndex+" comp "+index);
				ClockFragment fragment = fragments[selectedClockIndex];
				// fragment.setDisplayedClock(hand);
				fragment.setDisplayedClock(selectedClock);
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
	public void onPageScrolled(int arg0, float arg1, int arg2) {}
	

	@Override
	public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
		if (fromUser) {
//			selectedClock.setDialAlpha(progress);
//			fragments[selectedClockIndex].update();
		}
	}

	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {
	}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {
		selectedClock.setDialAlpha(seekBar.getProgress());
		fragments[selectedClockIndex].update();
	}



	

}
