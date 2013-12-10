package com.devxperiments.wowclockwidget.widget;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import com.devxperiments.wowclockwidget.ClockManager;
import com.devxperiments.wowclockwidget.apppicker.App;
import com.devxperiments.wowclockwidget.apppicker.App.NoApp;
import com.devxperiments.wowclockwidget.apppicker.App.ConfigApp;
import com.devxperiments.wowclockwidget.clocks.Clock;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.preference.PreferenceManager;

public class ClockWidgetProvider extends AppWidgetProvider{
	private PendingIntent service = null;

	public static String ACTION_WIDGET_CLICK = "action_click";

	@Override
	public void onReceive(Context context, Intent receivedIntent) {
		if (receivedIntent.getAction().equals(ACTION_WIDGET_CLICK))
			launchApp(context, receivedIntent);
		else
			super.onReceive(context, receivedIntent);
	}

	private void launchApp(Context context, Intent receivedIntent) {
		int widgetId = receivedIntent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
		String appPref = receivedIntent.getStringExtra(App.APP_PKG_CLS_PREF);
		App app;
		try {
			app = App.fromPrefString(context, appPref);
		} catch (NameNotFoundException e) {
			app = App.getConfigApp(context);
		}
		if(app instanceof NoApp){
			return;
		}
		Intent intent = new Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_LAUNCHER);
		intent.setComponent(app.getComponentName());
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		if(app instanceof ConfigApp)
			intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId);
		context.startActivity(intent);
	}

	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager,int[] appWidgetIds) {

		List<Clock> clocks = new ArrayList<Clock>(); 

		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
		ComponentName thisWidget = new ComponentName(context, ClockWidgetProvider.class);
		int[] allWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget);
		List<Clock> availableClocks = ClockManager.getAvailableClocks();
		for (int widgetId : allWidgetIds) {
			Clock clock = ClockManager.getClock(widgetId, prefs, availableClocks); 
			if(clock!=null){
				clocks.add(clock);
				appWidgetManager.updateAppWidget(widgetId, clock.getWidgetRemoteViews(context, widgetId));
			}
		}
		ClockManager.free();
		boolean updateService = ConfigActivity.areFragmentToBeUpdated();
		for(Clock clock: clocks){
			if(clock.isToBeUpdated() || updateService){
				//				Toast.makeText(context, "Starting service", Toast.LENGTH_SHORT).show();

				final AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

				final Calendar TIME = Calendar.getInstance();
				TIME.set(Calendar.MINUTE, 0);
				TIME.set(Calendar.SECOND, 0);
				TIME.set(Calendar.MILLISECOND, 0);

				final Intent intent = new Intent(context, ClockUpdateService.class);

				if (service == null) 
					service = PendingIntent.getService(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);

				alarmManager.setRepeating(AlarmManager.RTC, TIME.getTime().getTime(), 1000 * 60,service); 
				break;
			}
		}

	}

	@Override
	public void onDeleted(Context context, int[] appWidgetIds) { 
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
		Editor editor = prefs.edit();
		List<Clock> availableClocks = ClockManager.getAvailableClocks();
		for(int widgetId : appWidgetIds){
			Clock c = ClockManager.getClock(widgetId, prefs, availableClocks);
			if(c!=null)
				c.clear();
			editor.remove(widgetId+"");
			editor.remove(widgetId+ClockManager.CLOCK_INDEX_PREF);
			editor.remove(widgetId+ClockManager.HANDS_INDEX_PREF);
			editor.remove(widgetId+ClockManager.DIAL_INDEX_PREF);
			editor.remove(widgetId+ClockManager.DIAL_ALPHA_PREF);
			editor.remove(widgetId+ClockManager.AM_PM_PREF);
			editor.remove(widgetId+App.APP_PKG_CLS_PREF);
		}
		editor.commit();


		AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
		ComponentName thisWidget = new ComponentName(context, ClockWidgetProvider.class);
		int[] allWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget);


		boolean updateService = false;
		for (int widgetId : allWidgetIds){
			Clock clock = ClockManager.getClock(widgetId, prefs, availableClocks); 
			if(clock != null && clock.isToBeUpdated()){
				updateService = true;
				break; //Me ne basta trovare uno
			}
		}

		updateService = ConfigActivity.areFragmentToBeUpdated();

		if(!updateService && service!=null){
			final AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
			alarmManager.cancel(service);
		}
		System.gc();
	}

	@Override
	public void onDisabled(Context context) {
		if(service!=null){
			final AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
			alarmManager.cancel(service);
		}
	}

	@Override
	public void onAppWidgetOptionsChanged(Context context, AppWidgetManager appWidgetManager, int appWidgetId, Bundle newOptions) {}

}
