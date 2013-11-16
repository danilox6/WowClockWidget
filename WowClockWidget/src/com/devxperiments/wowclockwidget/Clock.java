package com.devxperiments.wowclockwidget;

import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Bitmap.Config;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.widget.RemoteViews;

public abstract class Clock {


	protected Hand[] hands;
	protected Dial[] dials;

	protected int currentHandIndex = 0;
	protected int currentDialIndex = 0;

	protected int dialAlpha = 255;

	protected boolean toBeUpdated = false; //FIXME cambia nome

	protected  boolean ampm = false;

	private Bitmap dialBitmap;

	public Clock(){}


	public Clock(Hand hand, Dial dial) {
		hands = new Hand[]{hand};
		dials = new Dial[]{dial};
	}

	public Clock(Hand[] hands, Dial[] dials) {
		this.hands = hands;
		this.dials = dials;
	}

	public Dial[] getDials() {
		return dials;
	}

	public Hand[] getHands() {
		return hands;
	}

	public void setCurrentDialIndex(int currentDialIndex) {
		this.currentDialIndex = currentDialIndex;
	}

	public void setCurrentHandIndex(int currentHandIndex) {
		this.currentHandIndex = currentHandIndex;
	}

	public void setDials(Dial[] dials) {
		this.dials = dials;
	}

	public void setHands(Hand[] hands) {
		this.hands = hands;
	}

	public boolean isAmpm() {
		return ampm;
	}

	public void setAmpm(boolean ampm) {
		this.ampm = ampm;
	}

	public boolean isToBeUpdated() {
		return toBeUpdated;
	}

	public void setToBeUpdated(boolean toBeUpdated) {
		this.toBeUpdated = toBeUpdated;
	}

	public int getCurrentHandsIndex() {
		return currentHandIndex;
	}

	public int getCurrentDialIndex() {
		return currentDialIndex;
	}

	public Dial getCurrentDial(){
		return dials[currentDialIndex];
	}

	public Hand getCurrentHands(){
		return hands[currentHandIndex];
	}

	public abstract RemoteViews createRemoteViews(Context context);


	public int getDialAlpha() {
		return dialAlpha;
	}

	public void setDialAlpha(int dialAlpha) {
		this.dialAlpha = dialAlpha;
	}

	public void clear(){
		if(dialBitmap!=null)
			dialBitmap.recycle();
	}



	protected Bitmap getDialBitmap(Context context){
		Drawable bitmapDrawable = new BitmapDrawable(context.getResources(),
				BitmapFactory.decodeResource(context.getResources(), getCurrentDial().getResourceId()));
		bitmapDrawable.setAlpha(getDialAlpha());
		dialBitmap = drawableToBitmap(bitmapDrawable);
		return  dialBitmap;
	}

	protected static Bitmap drawableToBitmap (Drawable drawable) {

		int width = drawable.getIntrinsicWidth();
		width = width > 0 ? width : 1;
		int height = drawable.getIntrinsicHeight();
		height = height > 0 ? height : 1;

		Bitmap bitmap = Bitmap.createBitmap(width, height, Config.ARGB_8888);
		Paint paint = new Paint();
		Canvas canvas = new Canvas(bitmap);
		paint.setAlpha(drawable.getOpacity());
		drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
		drawable.draw(canvas);

		return bitmap;
	}

	protected static PendingIntent getDefaultClockPendingIntent(Context context){
		PackageManager packageManager = context.getPackageManager();
		Intent alarmClockIntent = new Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_LAUNCHER);

		// Verify clock implementation
		String clockImpls[][] = {
				{"HTC Alarm Clock", "com.htc.android.worldclock", "com.htc.android.worldclock.WorldClockTabControl" },
				{"Standar Alarm Clock", "com.android.deskclock", "com.android.deskclock.AlarmClock"},
				{"Standar Alarm Clock2", "com.google.android.deskclock", "com.android.deskclock.AlarmClock"},
				{"Froyo Nexus Alarm Clock", "com.google.android.deskclock", "com.android.deskclock.DeskClock"},
				{"Moto Blur Alarm Clock", "com.motorola.blur.alarmclock",  "com.motorola.blur.alarmclock.AlarmClock"},
				{"Samsung Galaxy Clock", "com.sec.android.app.clockpackage","com.sec.android.app.clockpackage.ClockPackage"}
		};

		boolean foundClockImpl = false;

		for(int i=0; i<clockImpls.length; i++) {
			String vendor = clockImpls[i][0];
			String packageName = clockImpls[i][1];
			String className = clockImpls[i][2];
			try {
				ComponentName cn = new ComponentName(packageName, className);
//				ActivityInfo aInfo = packageManager.getActivityInfo(cn, PackageManager.GET_META_DATA);
				packageManager.getActivityInfo(cn, PackageManager.GET_META_DATA);
				alarmClockIntent.setComponent(cn);
				Log.i("CLOCK_PENDINGINTENT", "Found " + vendor + " --> " + packageName + "/" + className);
				foundClockImpl = true;
			} catch (NameNotFoundException e) {
				Log.i("CLOCK_PENDINGINTENT", vendor + " does not exists");
			}
		}

		if (foundClockImpl) 
			return PendingIntent.getActivity(context, 0, alarmClockIntent, 0);
		return null;
		
	}
}