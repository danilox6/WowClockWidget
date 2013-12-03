package com.devxperiments.wowclockwidget.widget;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import com.devxperiments.wowclockwidget.ClockManager;
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
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;

public class ClockWidgetProvider extends AppWidgetProvider{
	private PendingIntent service = null;

	@Override
	public void onEnabled(Context context) {
		super.onEnabled(context);
	}

	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager,int[] appWidgetIds) {

		List<Clock> clocks = new ArrayList<Clock>(); 

		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
		ComponentName thisWidget = new ComponentName(context, ClockWidgetProvider.class);
		int[] allWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget);
		List<Clock> availableClocks = ClockManager.getAvailableClocks();
		for (int widgetId : allWidgetIds) {
			//			FIXME il container serve?
			Clock clock = ClockManager.getClock(widgetId, prefs, availableClocks); 
			if(clock!=null){
				clocks.add(clock);
				//				appWidgetManager.updateAppWidget(widgetId, null);
				appWidgetManager.updateAppWidget(widgetId, clock.getWidgetRemoteViews(context, true));
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
			if(c!=null){
				c.clear();
				Log.i("ON DELETED", "cleared");
			}
			else
				Log.i("ON DELETED", "null");
			editor.putBoolean(widgetId + "", false);
			editor.remove(widgetId+ClockManager.CLOCK_INDEX_PREF);
			editor.remove(widgetId+ClockManager.HANDS_INDEX_PREF);
			editor.remove(widgetId+ClockManager.DIAL_INDEX_PREF);
			editor.remove(widgetId+ClockManager.DIAL_ALPHA_PREF);
			editor.remove(widgetId+ClockManager.AM_PM_PREF);
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

		updateService = ConfigActivity.areFragmentToBeUpdated(); //FIXME non credo che serva qui

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
