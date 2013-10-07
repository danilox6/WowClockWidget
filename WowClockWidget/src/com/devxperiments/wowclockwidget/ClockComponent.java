package com.devxperiments.wowclockwidget;

public class ClockComponent {
	
	public static final int COLOR_KARBON = 0x313131;
	
	
	private int color;
	
	public ClockComponent(int color) {
		this.color = color;
	}

	public int getColorResId() {
		return color;
	}

}
