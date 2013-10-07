package com.devxperiments.wowclockwidget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Bitmap.Config;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.widget.RemoteViews;

public abstract class Clock {
	
	
	protected Hand[] hands;
	protected Dial[] dials;
	
	protected int currentHandIndex = 0;
	protected int currentDialIndex = 0;
	
	protected int dialAlpha = 255;
	
	
	protected boolean toBeUpdated = false; //FIXME cambia nome
	
	protected  boolean ampm = false;
	
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
	
	protected Bitmap getDialBitmap(Context context){
		Drawable bitmapDrawable = new BitmapDrawable(context.getResources(),
				BitmapFactory.decodeResource(context.getResources(), getCurrentDial().getResourceId()));
		bitmapDrawable.setAlpha(getDialAlpha());
		return  drawableToBitmap(bitmapDrawable);
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
}
