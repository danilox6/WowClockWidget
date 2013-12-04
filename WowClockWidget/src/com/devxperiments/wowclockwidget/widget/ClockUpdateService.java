package com.devxperiments.wowclockwidget.widget;


import java.util.List;

import com.devxperiments.wowclockwidget.ClockManager;
import com.devxperiments.wowclockwidget.clocks.Clock;


import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.IBinder;
import android.preference.PreferenceManager;

public class ClockUpdateService  extends Service{  

	Bitmap hourHand;
	Bitmap minuteHand;
//	static Bitmap secondHand;
//	static int prevTotalMinutes = -1;

	@Override  
	public void onCreate(){  
		super.onCreate();  
	}  

	@Override  
	public int onStartCommand(Intent intent, int flags, int startId){  
		buildUpdate();  
		return super.onStartCommand(intent, flags, startId);  
	}  

	private void buildUpdate(){  
		
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		
		AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
		ComponentName thisWidget = new ComponentName(this, ClockWidgetProvider.class);
		int appWidgetIds[] = appWidgetManager.getAppWidgetIds(thisWidget);

		boolean stopService = true;
		
//		Toast.makeText(this, "TICK", Toast.LENGTH_SHORT).show();
		List<Clock> availableClocks = ClockManager.getAvailableClocks();
		for (int widgetId : appWidgetIds) {
			//			FIXME il container serve?
			Clock clock = ClockManager.getClock(widgetId, prefs, availableClocks); 
			if(clock!=null && clock.isToBeUpdated()){
//				Log.i("UPDATE_SERVICE", "ci sono widget che devono essere aggiornati");
				stopService = false;
				appWidgetManager.updateAppWidget(widgetId, clock.getWidgetRemoteViews(this, widgetId));
			}
		}
		
		if(ConfigActivity.areFragmentToBeUpdated()){
//			Log.i("UPDATE_SERVICE", "ci sono fragment che devono essere aggiornati");
			stopService = false;
			ConfigActivity.updateFragments();
		}
		
		if(stopService){
//			Log.i("UPDATE_SERVICE", "mi posso fermare, non c'è niente da aggiornare");
//			Toast.makeText(this, "Trying to stop service...", Toast.LENGTH_SHORT).show();
			Intent intent = new Intent(this, ClockUpdateService.class);
			PendingIntent service = PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
			AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
			alarmManager.cancel(service);
		}
		/*
		
		RemoteViews remoteViews = new RemoteViews(getPackageName(),R.layout.widget_layout);  
		
		Calendar calendar = Calendar.getInstance();

		
		
		//			remoteViews.setImageViewBitmap(R.id.imgPoints, Utilities.getFontBitmapCached(this, typeface, ":", Color.BLACK, 40));
		int hoursFormat = showAmPM()?Calendar.HOUR:Calendar.HOUR_OF_DAY;
		String hours = ((calendar.get(hoursFormat)<10)?"0":"")+calendar.get(hoursFormat);
		String minutes = ((calendar.get(Calendar.MINUTE)<10)?"0":"")+calendar.get(Calendar.MINUTE);
//		remoteViews.setTextViewText(R.id.txtTime, hours+":"+minutes);
//		remoteViews.setTextColor(R.id.txtTime, Color.rgb(230, 126, 56));

		 
		String ampm = null;
		if(showAmPM())
			ampm = calendar.get(Calendar.AM_PM)==Calendar.AM?"AM":"PM";
		//			remoteViews.setImageViewBitmap(R.id.imgAmPm, showAmPM()? Utilities.getFontBitmapCached(this, typeface, ampm, Color.BLACK, 20):null);
		for (int widgetId : appWidgetIds)
			appWidgetManager.updateAppWidget(widgetId, remoteViews);

		//		hourHand.recycle();
		//		rotatedHour.recycle();
		//		secondsHand.recycle();
		//		rotatedSeconds.recycle();
*/
	}  


	@Override  
	public IBinder onBind(Intent intent){  
		return null;  
	}  
}  