package com.devxperiments.wowclockwidget.clocks;

import com.devxperiments.wowclockwidget.Dial;
import com.devxperiments.wowclockwidget.Hand;
import com.devxperiments.wowclockwidget.R;
import com.devxperiments.wowclockwidget.apppicker.App;
import com.devxperiments.wowclockwidget.apppicker.NoApp;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Bitmap.Config;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.preference.PreferenceManager;
import android.widget.RemoteViews;

public abstract class Clock {

	protected Hand[] hands;
	protected Dial[] dials;
	private int[] overlays = null;

	protected int currentHandIndex = 0;
	protected int currentDialIndex = 0;

	protected int dialAlpha = 255;

	protected boolean toBeUpdated = false; //FIXME cambia nome

	protected boolean ampm = false;

	private Bitmap dialBitmap;
	
	protected int baseLayoutResId;
	
	private Bitmap overlayBitmap; 

	public Clock(int baseLayoutResId, Hand[] hands, Dial[] dials, int... overlays) {
		this.baseLayoutResId = baseLayoutResId;
		this.hands = hands;
		this.dials = dials;
		this.overlays = overlays;
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

	
	public RemoteViews getWidgetRemoteViews(Context context, boolean clickable){
		RemoteViews remoteViews = new RemoteViews(context.getPackageName(), baseLayoutResId);
		remoteViews.setImageViewBitmap(R.id.imgDial, getDialBitmap(context));
		
		if(overlays != null && overlays.length != 0){
			overlayBitmap =  BitmapFactory.decodeResource(context.getResources(), overlays[overlays.length != 1?getCurrentHandsIndex():0]);
			remoteViews.setImageViewBitmap(R.id.imgOverlay,overlayBitmap);
		}
		
		customizeRemoteViews(context, remoteViews);
		
		remoteViews.removeAllViews(R.id.clockContainer);
		RemoteViews handsViews = new RemoteViews(context.getPackageName(),getCurrentHands().getLayoutId());
		remoteViews.addView(R.id.clockContainer, handsViews);
		
		if(clickable){
			PendingIntent pendingIntent = getAppPendingIntent(context);
			if(pendingIntent!=null)
				remoteViews.setOnClickPendingIntent(R.id.clockContainer, pendingIntent);
		}
		
		return remoteViews;
	}
	
	protected abstract void customizeRemoteViews(Context context, RemoteViews remoteViews);


	public int getDialAlpha() {
		return dialAlpha;
	}

	public void setDialAlpha(int dialAlpha) {
		this.dialAlpha = dialAlpha;
	}

	public void clear(){
		if(dialBitmap!=null)
			dialBitmap.recycle();
		if(overlayBitmap!=null)
			overlayBitmap.recycle();
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

	protected static PendingIntent getAppPendingIntent(Context context){
	
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
		
		String appPref = prefs.getString(App.APP_PKG_CLS_PREF, App.APP_NONE);
		App app;
		try {
			app = App.fromPrefString(context, appPref);
		} catch (NameNotFoundException e) {
			app = new NoApp(context);
		}
		if(app instanceof NoApp)
			return null;
		
		Intent intent = new Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_LAUNCHER);
		intent.setComponent(app.getComponentName());
		return PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
	}
	 
}