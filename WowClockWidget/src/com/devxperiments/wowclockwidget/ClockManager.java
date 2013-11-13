package com.devxperiments.wowclockwidget;

import java.util.ArrayList;
import java.util.List;

import com.devxperiments.wowclockwidget.clocks.*;

import android.content.SharedPreferences;

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

			Dial[] backgrounds = new Dial[]{new Dial(R.drawable.clk_background_a, R.color.a_karbon),
					new Dial(R.drawable.clk_background_b, R.color.b_grigio),
					new Dial(R.drawable.clk_background_c, R.color.c_chiaro),
					new Dial(R.drawable.clk_background_d, R.color.d_honest),
					new Dial(R.drawable.clk_background_e, R.color.e_viola),
					new Dial(R.drawable.clk_background_f, R.color.f_verde),
					new Dial(R.drawable.clk_background_g, R.color.g_giallo),
					new Dial(R.drawable.clk_background_h, R.color.h_arancio),
					new Dial(R.drawable.clk_background_i, R.color.i_rosso)};
						
			Hand[] clock1hands = new Hand[]{new Hand(R.layout.clock1_hands_a, R.color.a_karbon),
					new Hand(R.layout.clock1_hands_b, R.color.b_grigio),
					new Hand(R.layout.clock1_hands_c, R.color.c_chiaro),
					new Hand(R.layout.clock1_hands_d, R.color.d_honest)};
			
			Hand[] digitalCutHands = new Hand[]{new Hand(R.layout.clock_hands_digital_a, R.color.a_karbon),
					new Hand(R.layout.clock_hands_digital_b, R.color.b_grigio),
					new Hand(R.layout.clock_hands_digital_c, R.color.c_chiaro),
					new Hand(R.layout.clock_hands_digital_d, R.color.d_honest),
					new Hand(R.layout.clock_hands_digital_e, R.color.e_viola),
					new Hand(R.layout.clock_hands_digital_f, R.color.f_verde),
					new Hand(R.layout.clock_hands_digital_g, R.color.g_giallo),
					new Hand(R.layout.clock_hands_digital_h, R.color.h_arancio),
					new Hand(R.layout.clock_hands_digital_i, R.color.i_rosso)};
			
			Hand[] thinHands = new Hand[]{new Hand(R.layout.clock_hands_thin_a, R.color.a_karbon),
					new Hand(R.layout.clock_hands_thin_b, R.color.b_grigio),
					new Hand(R.layout.clock_hands_thin_c, R.color.c_chiaro),
					new Hand(R.layout.clock_hands_thin_d, R.color.d_honest),
					new Hand(R.layout.clock_hands_thin_e, R.color.e_viola),
					new Hand(R.layout.clock_hands_thin_f, R.color.f_verde),
					new Hand(R.layout.clock_hands_thin_g, R.color.g_giallo),
					new Hand(R.layout.clock_hands_thin_h, R.color.h_arancio),
					new Hand(R.layout.clock_hands_thin_i, R.color.i_rosso)};
			
			Hand[] largeHands = new Hand[]{
					new Hand(R.layout.clock_hands_large_a, R.color.a_karbon),
					new Hand(R.layout.clock_hands_large_b, R.color.b_grigio),
					new Hand(R.layout.clock_hands_large_c, R.color.c_chiaro),
					new Hand(R.layout.clock_hands_large_d, R.color.d_honest),
					new Hand(R.layout.clock_hands_large_e, R.color.e_viola),
					new Hand(R.layout.clock_hands_large_f, R.color.f_verde),
					new Hand(R.layout.clock_hands_large_g, R.color.g_giallo),
					new Hand(R.layout.clock_hands_large_h, R.color.h_arancio),
					new Hand(R.layout.clock_hands_large_i, R.color.i_rosso)};
			
			Hand[] preciseHands = new Hand[]{//new Hand(R.layout.clock4_layout_a, R.color.a_karbon),
//					new Hand(R.layout.clock4_layout_b, R.color.b_grigio),
					new Hand(R.layout.clock_hands_precise_c, R.color.c_chiaro)//,
//					new Hand(R.layout.clock4_layout_d, R.color.d_honest)
					};
			
			Hand[] arrowHands = new Hand[]{//new Hand(R.layout.clock5_layout_a, R.color.a_karbon),
//					new Hand(R.layout.clock5_layout_b, R.color.b_grigio),
					new Hand(R.layout.clock_hands_arrow_c, R.color.c_chiaro)//,
//					new Hand(R.layout.clock5_layout_d, R.color.d_honest)
					};
			
			
			int[] ringOverlays = new int[]{R.drawable.clk_overlay_ring_a,
										R.drawable.clk_overlay_ring_b,
										R.drawable.clk_overlay_ring_c,
										R.drawable.clk_overlay_ring_d,
										R.drawable.clk_overlay_ring_e,
										R.drawable.clk_overlay_ring_f,
										R.drawable.clk_overlay_ring_g,
										R.drawable.clk_overlay_ring_h,
										R.drawable.clk_overlay_ring_i};
			
			int[] arrowOverlays =  new int[] { R.drawable.clk_overlay_arrow_a,
										R.drawable.clk_overlay_arrow_b,
										R.drawable.clk_overlay_arrow_c,
										R.drawable.clk_overlay_arrow_d,
										R.drawable.clk_overlay_arrow_e,
										R.drawable.clk_overlay_arrow_f,
										R.drawable.clk_overlay_arrow_g,
										R.drawable.clk_overlay_arrow_h,
										R.drawable.clk_overlay_arrow_i};
			
			int[] dotsOverlays =  new int[] { R.drawable.clk_overlay_dots_a,
					R.drawable.clk_overlay_dots_b,
					R.drawable.clk_overlay_dots_c,
					R.drawable.clk_overlay_dots_d,
					R.drawable.clk_overlay_dots_e,
					R.drawable.clk_overlay_dots_f,
					R.drawable.clk_overlay_dots_g,
					R.drawable.clk_overlay_dots_h,
					R.drawable.clk_overlay_dots_i};
			 
			Clock c;
			
//			c = new SimpleClock(clock1hands, backgrounds);
//			c.setCurrentHandIndex(3);
//			availableClocks.add(c);
			
			c = new SimpleClock(largeHands, backgrounds);
			c.setCurrentDialIndex(8);
			c.setCurrentHandIndex(2);
			availableClocks.add(c);
			
			c = new SimpleClock(thinHands, backgrounds, dotsOverlays);
			c.setCurrentDialIndex(6);
			c.setCurrentHandIndex(2);
			availableClocks.add(c);
			
			c = new SimpleClock(arrowHands, backgrounds);
			availableClocks.add(c);
			
			c = new ClockLayoutCenterDigital(digitalCutHands, backgrounds, ringOverlays);
			c.setCurrentHandIndex(3);
			c.setToBeUpdated(true);
			availableClocks.add(c);
	
			c = new ClockLayoutCenterDigital(digitalCutHands, backgrounds,arrowOverlays);
			c.setCurrentHandIndex(5);
			c.setToBeUpdated(true);
			availableClocks.add(c);
			
			c = new SimpleClock(preciseHands, backgrounds);
			availableClocks.add(c);
			
		}

		return availableClocks;
	}
}
