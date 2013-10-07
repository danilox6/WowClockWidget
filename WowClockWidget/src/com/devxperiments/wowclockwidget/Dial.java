package com.devxperiments.wowclockwidget;

public class Dial extends ClockComponent{
	private int resourceId;
	private int alpha = 255;
	
	public Dial(int resourceId, int color) {
		super(color);
		this.resourceId = resourceId;
	}
	
	public int getResourceId() {
		return resourceId;
	}
	
	public void setAlpha(int alpha) {
		this.alpha = alpha;
	}
	
	public int getAlpha() {
		return alpha;
	}
}
