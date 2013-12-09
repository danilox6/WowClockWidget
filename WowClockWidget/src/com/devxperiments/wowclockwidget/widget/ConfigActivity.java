package com.devxperiments.wowclockwidget.widget;

import java.util.Calendar;
import java.util.List;
import java.util.Random;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.devxperiments.wowclockwidget.ClockManager;
import com.devxperiments.wowclockwidget.R;
import com.devxperiments.wowclockwidget.apppicker.App;
import com.devxperiments.wowclockwidget.apppicker.AppPickerActivity;
import com.devxperiments.wowclockwidget.clocks.Clock;
import com.viewpagerindicator.LinePageIndicator;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.WallpaperManager;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.TypedValue;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TabHost;
import android.widget.LinearLayout.LayoutParams;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;

public class ConfigActivity extends SherlockFragmentActivity implements OnPageChangeListener, OnSeekBarChangeListener {

	public static final String TAB_ID_HANDS = "hands";
	public static final String TAB_ID_DIAL = "dial";

	private int appWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;

	private FragmentStatePagerAdapter adapter;
	private ViewPager pager;
	private TabHost mTabHost;
	private SeekBar seekBar;
	private CheckBox amPmBox;
	private ImageView appIconImageView;
	private TextView appNameTextView;

	private ColorPicker colorPicker;

	private List<Clock> clocks = ClockManager.getAvailableClocks();;

	private LinearLayout amPmLayout;

	private Clock selectedClock;
	private int selectedClockIndex = -1;

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
		amPmLayout = (LinearLayout) findViewById(R.id.lnlAmPm);
		amPmBox = (CheckBox) findViewById(R.id.ckb12hour);

		if(clocks == null || clocks.isEmpty())
			clocks = ClockManager.getAvailableClocks(); 

		fragments = new ClockFragment[clocks.size()];
		adapter = new ClockAdapter(getSupportFragmentManager(), clocks);

		pager = (ViewPager) findViewById(R.id.pager);
		pager.setOffscreenPageLimit(0);
		pager.setAdapter(adapter);

		LinePageIndicator indicator = (LinePageIndicator) findViewById(R.id.indicator);
		indicator.setViewPager(pager);
		indicator.setOnPageChangeListener(this);

		ImageView backgroundImageView = (ImageView) findViewById(R.id.imgBackground);
		backgroundImageView.setImageDrawable(WallpaperManager.getInstance(this).getFastDrawable());

		RelativeLayout rLayout = (RelativeLayout) findViewById(R.id.previewContainer);
		Display display = getWindowManager().getDefaultDisplay();
		int newHeight = Math.max(display.getHeight(), display.getWidth())/3;
//		if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT)
		rLayout.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, newHeight));

		colorPicker = new ColorPicker(this);

		mTabHost = (TabHost)findViewById(android.R.id.tabhost);
		mTabHost.setOnTabChangedListener(colorPicker);
		mTabHost.setup();
		addTab(R.string.strHandsColor, TAB_ID_HANDS);
		addTab(R.string.strBackColor, TAB_ID_DIAL);


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

		appIconImageView = (ImageView) findViewById(R.id.imgAppIcon);
		appNameTextView = (TextView) findViewById(R.id.txtAppName);
		updateAppPickerPrefView();

		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		if(prefs.getBoolean(appWidgetId+"", false))
			configExistingWidget(prefs);

		startUpdateService();

//		adapter.notifyDataSetChanged(); //FIXME non dovrebbe servire
	}
	
	private void configExistingWidget(SharedPreferences prefs){
		int clockIndex = prefs.getInt(appWidgetId+ClockManager.CLOCK_INDEX_PREF, 0);
		Clock clock = ClockManager.getClock(appWidgetId, prefs, clocks);
		clocks.set(clockIndex, clock);
		pager.setCurrentItem(clockIndex, false);
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
		.putBoolean(appWidgetId + ClockManager.AM_PM_PREF, selectedClock.isAmpm())
		.putString(appWidgetId+App.APP_PKG_CLS_PREF, prefs.getString(App.APP_PKG_CLS_PREF, App.APP_CONFIG))
		.commit();

		AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
		appWidgetManager.updateAppWidget(appWidgetId, selectedClock.getWidgetRemoteViews(this, appWidgetId));
		selectedClock.clear(); 

		Intent resultValue = new Intent();
		resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
		setResult(RESULT_OK, resultValue);
		finish();
	}

	public void onRandomColorsClick(View v) {
		Random rnd = new Random();
		int handsColor = rnd.nextInt(12); //FIXME
		int dialColor = rnd.nextInt(12); //FIXME
		while(handsColor==dialColor)
			dialColor = rnd.nextInt(12);

		selectedClock.setCurrentHandIndex(handsColor);
		selectedClock.setCurrentDialIndex(dialColor);

		ClockFragment fragment = fragments[selectedClockIndex];
		fragment.setDisplayedClock(selectedClock);
		fragment.update();

		colorPicker.updateSelectedColor(selectedClock);
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

		if(fragments[selectedClockIndex]!=null)
			fragments[selectedClockIndex].setDisplayedClock(selectedClock);

		colorPicker.updateSelectedColor(selectedClock);
	}

	public void onColorClick(View colorView){
		int colorIndex = Integer.parseInt((String) colorView.getTag());
		if (mTabHost.getCurrentTabTag().equals(TAB_ID_HANDS))
			selectedClock.setCurrentHandIndex(colorIndex);
		else
			selectedClock.setCurrentDialIndex(colorIndex);
		ClockFragment fragment = fragments[selectedClockIndex];
		fragment.setDisplayedClock(selectedClock);
		fragment.update();

		colorPicker.updateSelectedColor(selectedClock);
	}

	public void onAppPickerClick(View v){
		Intent intent = new Intent(this, AppPickerActivity.class);
		startActivityForResult(intent, 0);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(resultCode == RESULT_OK){
			SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
			updateAppPickerPrefView(prefs.getString(App.APP_PKG_CLS_PREF, App.APP_NONE));
		}
	}

	private void updateAppPickerPrefView(){
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		String appPref = prefs.getString(appWidgetId+App.APP_PKG_CLS_PREF, prefs.getString(App.APP_PKG_CLS_PREF, App.APP_CONFIG)); //Give the pref relative to the actual widget or, i doesn'r exists, the last preference
		updateAppPickerPrefView(appPref);
	}

	private void updateAppPickerPrefView(String appPref) {
		App app;
		try {
			app = App.fromPrefString(this, appPref);
		} catch (NameNotFoundException e) {
			app = App.getConfigApp(this);
			SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
			if(appPref.equals(prefs.getString(App.APP_PKG_CLS_PREF, App.APP_NONE)))
				prefs.edit().putString(App.APP_PKG_CLS_PREF, app.toPrefString()).commit();
		}
		appIconImageView.setImageDrawable(app.getIcon());
		appNameTextView.setText(app.getApplicationName());
	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
		if (hasFocus){
			onPageSelected(selectedClockIndex == -1 ? 0 : selectedClockIndex);
		}
	}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {
		selectedClock.setDialAlpha(seekBar.getProgress());
		fragments[selectedClockIndex].update();
	}



	@Override
	public void onPageScrollStateChanged(int arg0) {}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {}


	@Override
	public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {}

	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {}
	
	private void addTab(int titleResId, String tag) {
		TabSpec tabSpec = mTabHost.newTabSpec(tag).setContent(android.R.id.tabcontent);
		String tabTitle = getString(titleResId);
		
		if(Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB){
			mTabHost.getTabWidget().setDividerDrawable(com.actionbarsherlock.R.drawable.abs__list_divider_holo_light);
			tabSpec.setIndicator(customTabTextView(tabTitle));
		}else
			tabSpec.setIndicator(tabTitle);

		mTabHost.addTab(tabSpec);
	}

	@SuppressLint("DefaultLocale")
	private View customTabTextView(String title){
		TextView tabView = new TextView(this);
		tabView.setText(title.toUpperCase());
		tabView.setPadding(0, 5, 0, 0);
		tabView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
		tabView.setTextColor(Color.DKGRAY);
		tabView.setGravity(Gravity.CENTER);
		tabView.setTypeface(Typeface.MONOSPACE, Typeface.BOLD);
		tabView.setHeight(dp2px(48, this));
		tabView.setBackgroundResource(R.drawable.tab_indicator_holo);
//		style="?attr/actionBarTabStyle" //FIXME Provare così
//		ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) tabView.getLayoutParams();
//		//Fix margins in 2.x, by default there is -2  
//		params.setMargins(0, 0, 0, 0);
		return tabView;
	}
	private int dp2px(int dip, Context context){
        float scale = context.getResources().getDisplayMetrics().density;
        return Math.round(dip * scale + 0.5f);
}

}