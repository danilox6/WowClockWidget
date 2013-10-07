package com.devxperiments.wowclockwidget;

public class Hand extends ClockComponent{
	private int layoutId;
	
	public Hand(int layoutId, int color) {
		super(color);
		this.layoutId = layoutId;
	}

	public int getLayoutId() {
		return layoutId;
	}
}
