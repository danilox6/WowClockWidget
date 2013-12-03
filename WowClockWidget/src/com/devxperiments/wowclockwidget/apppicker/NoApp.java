package com.devxperiments.wowclockwidget.apppicker;

import com.devxperiments.wowclockwidget.R;

import android.content.Context;

public class NoApp extends App{

	public NoApp(Context context) {
		super("No app", context.getResources().getDrawable(R.drawable.ic_launcher)); //FIXME
	}
	
	@Override
	public int compareTo(App another) {
		return -1;
	}
	
	@Override
	public String toPrefString() {
		return APP_NONE;
	}
}
