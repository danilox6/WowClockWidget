package com.devxperiments.wowclockwidget.clocks;

import com.devxperiments.wowclockwidget.Dial;
import com.devxperiments.wowclockwidget.Hand;
import com.devxperiments.wowclockwidget.R;
import com.devxperiments.wowclockwidget.apppicker.App;
import com.devxperiments.wowclockwidget.widget.ClockWidgetProvider;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
	
	public RemoteViews getWidgetRemoteViews(Context context){
		return getWidgetRemoteViews(context, AppWidgetManager.INVALID_APPWIDGET_ID);
	}

	
	public RemoteViews getWidgetRemoteViews(Context context, int widgetId){
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
		
		if(widgetId != AppWidgetManager.INVALID_APPWIDGET_ID){
			PendingIntent pendingIntent = getAppPendingIntent(context, widgetId);
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

	private PendingIntent getAppPendingIntent(Context context, int widgetId) {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
		Intent intent = new Intent(context, ClockWidgetProvider.class);
        intent.setAction(ClockWidgetProvider.ACTION_WIDGET_CLICK);
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId);
        intent.putExtra(App.APP_PKG_CLS_PREF, prefs.getString(widgetId+App.APP_PKG_CLS_PREF, App.APP_NONE));
        return  PendingIntent.getBroadcast(context, widgetId, intent , Intent.FILL_IN_DATA|PendingIntent.FLAG_UPDATE_CURRENT);
	}
}