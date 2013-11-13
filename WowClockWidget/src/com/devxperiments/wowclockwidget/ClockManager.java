package com.devxperiments.wowclockwidget;

import java.util.ArrayList;
import java.util.List;

import com.devxperiments.wowclockwidget.clocks.ClockLayout2;
import com.devxperiments.wowclockwidget.clocks.SimpleClock;

import android.content.SharedPreferences;
import android.graphics.Color;

public class ClockManager {

	private static List<Clock> availableClocks = new ArrayList<Clock>();

	public static String CLOCK_INDEX_PREF = "clock_index";
	public static String HANDS_INDEX_PREF = "hands_index";
	public static String DIAL_INDEX_PREF = "dial_index";
	public static String DIAL_ALPHA_PREF = "dial_alpha";
	public static String AM_PM_PREF = "am_pm";



	public static Clock getClock(int widgetId,SharedPreferences prefs){
		Clock clock = null;
		if(prefs.getBoolean(widgetId+"", false)){
			int clockIndex = prefs.getInt(widgetId+CLOCK_INDEX_PREF, 0);
			int handsIndex = prefs.getInt(widgetId+HANDS_INDEX_PREF, 0);
			int dialIndex = prefs.getInt(widgetId+DIAL_INDEX_PREF, 0);
			int dialAlpha = prefs.getInt(widgetId+DIAL_ALPHA_PREF, 0);
			boolean ampm = prefs.getBoolean(widgetId+AM_PM_PREF, false);
			if(availableClocks.isEmpty())
				getAvailableClocks();
			clock = availableClocks.get(clockIndex);
			if(clock!=null) {
				clock.setCurrentDialIndex(dialIndex);
				clock.setCurrentHandIndex(handsIndex);
				clock.setDialAlpha(dialAlpha);
				clock.setAmpm(ampm);
			}
		}
		return clock;
	}

	public static void free(){
		availableClocks.clear();
	}

	public static List<Clock> getAvailableClocks(){
		if(availableClocks.isEmpty()){
			Clock c;
			Hand[] hands;
			Dial[] dials;
			
			hands = new Hand[]{new Hand(R.layout.clock1_layout_a, R.color.a_karbon),
					new Hand(R.layout.clock1_layout_b, R.color.b_grigio),
					new Hand(R.layout.clock1_layout_c, R.color.c_chiaro),
					new Hand(R.layout.clock1_layout_d, R.color.d_honest)};
			dials = new Dial[]{new Dial(R.drawable.clock3_dial_a, R.color.a_karbon),
					new Dial(R.drawable.clock3_dial_b, R.color.b_grigio),
					new Dial(R.drawable.clock3_dial_c, R.color.c_chiaro),
					new Dial(R.drawable.clock3_dial_d, R.color.d_honest),
					new Dial(R.drawable.clock3_dial_e, R.color.e_viola),
					new Dial(R.drawable.clock3_dial_f, R.color.f_verde),
					new Dial(R.drawable.clock3_dial_g, R.color.g_giallo),
					new Dial(R.drawable.clock3_dial_h, R.color.h_arancio),
					new Dial(R.drawable.clock3_dial_i, R.color.i_rosso)};
			c = new SimpleClock(hands, dials);
			c.setCurrentHandIndex(3);
			availableClocks.add(c);
			
			
			hands = new Hand[]{new Hand(R.layout.clock2_layout_a, R.color.d_honest),
					new Hand(R.layout.clock2_layout_b, R.color.e_viola),
					new Hand(R.layout.clock2_layout_c, R.color.f_verde),
					new Hand(R.layout.clock2_layout_d, R.color.h_arancio),
					new Hand(R.layout.clock2_layout_e, R.color.i_rosso)};
//			dials = new Dial[]{new Dial(R.drawable.clock2_dial_a, R.color.a_karbon),
//					new Dial(R.drawable.clock2_dial_b, R.color.b_grigio),
//					new Dial(R.drawable.clock2_dial_c, R.color.c_chiaro),
//					new Dial(R.drawable.clock2_dial_d, R.color.d_honest)};
			c = new ClockLayout2(hands, dials);
			c.setToBeUpdated(true);
			availableClocks.add(c);
			
			hands = new Hand[]{//new Hand(R.layout.clock3_layout_a, R.color.a_karbon),
//					new Hand(R.layout.clock3_layout_b, R.color.b_grigio),
					new Hand(R.layout.clock3_layout_c, R.color.c_chiaro)//,
//					new Hand(R.layout.clock3_layout_d, R.color.d_honest)
					};
			c = new SimpleClock(hands, dials);
			availableClocks.add(c);
			
			hands = new Hand[]{//new Hand(R.layout.clock4_layout_a, R.color.a_karbon),
//					new Hand(R.layout.clock4_layout_b, R.color.b_grigio),
					new Hand(R.layout.clock4_layout_c, R.color.c_chiaro)//,
//					new Hand(R.layout.clock4_layout_d, R.color.d_honest)
					};
			c = new SimpleClock(hands, dials);
			availableClocks.add(c);
			
			hands = new Hand[]{//new Hand(R.layout.clock5_layout_a, R.color.a_karbon),
//					new Hand(R.layout.clock5_layout_b, R.color.b_grigio),
					new Hand(R.layout.clock5_layout_c, R.color.c_chiaro)//,
//					new Hand(R.layout.clock5_layout_d, R.color.d_honest)
					};
			c = new SimpleClock(hands, dials);
			availableClocks.add(c);
		}

		return availableClocks;
	}
}
