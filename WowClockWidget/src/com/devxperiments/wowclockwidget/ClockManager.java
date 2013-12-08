package com.devxperiments.wowclockwidget;

import java.util.ArrayList;
import java.util.List;

import com.devxperiments.wowclockwidget.clocks.*;

import android.content.SharedPreferences;

public class ClockManager {

	//	private  List<Clock> availableClocks = new ArrayList<Clock>();

	public static String CLOCK_INDEX_PREF = "clock_index";
	public static String HANDS_INDEX_PREF = "hands_index";
	public static String DIAL_INDEX_PREF = "dial_index";
	public static String DIAL_ALPHA_PREF = "dial_alpha";
	public static String AM_PM_PREF = "am_pm";



	public static Clock getClock(int widgetId,SharedPreferences prefs, List<Clock> availableClocks){
		Clock clock = null; 
		if(prefs.getBoolean(widgetId+"", false)){
			int clockIndex = prefs.getInt(widgetId+CLOCK_INDEX_PREF, 0);
			int handsIndex = prefs.getInt(widgetId+HANDS_INDEX_PREF, 0);
			int dialIndex = prefs.getInt(widgetId+DIAL_INDEX_PREF, 0);
			int dialAlpha = prefs.getInt(widgetId+DIAL_ALPHA_PREF, 0);
			boolean ampm = prefs.getBoolean(widgetId+AM_PM_PREF, false);

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

	public static Clock getClock(int widgetId, SharedPreferences prefs){
		return getClock(widgetId, prefs, getAvailableClocks());
	}


	public static void free(){
		//		availableClocks.clear();
	}

	public static List<Clock> getAvailableClocks(){
		List<Clock> availableClocks = new ArrayList<Clock>();
		if(availableClocks.isEmpty()){

			Dial[] backgrounds = new Dial[]{new Dial(R.drawable.clk_background_a, R.color.a_karbon),
					new Dial(R.drawable.clk_background_b, R.color.b_grigio),
					new Dial(R.drawable.clk_background_c, R.color.c_chiaro),
					new Dial(R.drawable.clk_background_d, R.color.d_honest),
					new Dial(R.drawable.clk_background_e, R.color.e_viola),
					new Dial(R.drawable.clk_background_f, R.color.f_verde),
					new Dial(R.drawable.clk_background_g, R.color.g_giallo),
					new Dial(R.drawable.clk_background_h, R.color.h_arancio),
					new Dial(R.drawable.clk_background_i, R.color.i_rosso),
					new Dial(R.drawable.clk_background_j, R.color.j_fucsia),
					new Dial(R.drawable.clk_background_k, R.color.k_agua),
					new Dial(R.drawable.clk_background_l, R.color.l_verdino)};

			Hand[] digitalCutHands = new Hand[]{new Hand(R.layout.clock_hands_digital_a, R.color.a_karbon),
					new Hand(R.layout.clock_hands_digital_b, R.color.b_grigio),
					new Hand(R.layout.clock_hands_digital_c, R.color.c_chiaro),
					new Hand(R.layout.clock_hands_digital_d, R.color.d_honest),
					new Hand(R.layout.clock_hands_digital_e, R.color.e_viola),
					new Hand(R.layout.clock_hands_digital_f, R.color.f_verde),
					new Hand(R.layout.clock_hands_digital_g, R.color.g_giallo),
					new Hand(R.layout.clock_hands_digital_h, R.color.h_arancio),
					new Hand(R.layout.clock_hands_digital_i, R.color.i_rosso),
					new Hand(R.layout.clock_hands_digital_j, R.color.j_fucsia),
					new Hand(R.layout.clock_hands_digital_k, R.color.k_agua),
					new Hand(R.layout.clock_hands_digital_l, R.color.l_verdino)};

			Hand[] thinHands = new Hand[]{new Hand(R.layout.clock_hands_thin_a, R.color.a_karbon),
					new Hand(R.layout.clock_hands_thin_b, R.color.b_grigio),
					new Hand(R.layout.clock_hands_thin_c, R.color.c_chiaro),
					new Hand(R.layout.clock_hands_thin_d, R.color.d_honest),
					new Hand(R.layout.clock_hands_thin_e, R.color.e_viola),
					new Hand(R.layout.clock_hands_thin_f, R.color.f_verde),
					new Hand(R.layout.clock_hands_thin_g, R.color.g_giallo),
					new Hand(R.layout.clock_hands_thin_h, R.color.h_arancio),
					new Hand(R.layout.clock_hands_thin_i, R.color.i_rosso),
					new Hand(R.layout.clock_hands_thin_j, R.color.j_fucsia),
					new Hand(R.layout.clock_hands_thin_k, R.color.k_agua),
					new Hand(R.layout.clock_hands_thin_l, R.color.l_verdino)};

			Hand[] largeHands = new Hand[]{
					new Hand(R.layout.clock_hands_large_a, R.color.a_karbon),
					new Hand(R.layout.clock_hands_large_b, R.color.b_grigio),
					new Hand(R.layout.clock_hands_large_c, R.color.c_chiaro),
					new Hand(R.layout.clock_hands_large_d, R.color.d_honest),
					new Hand(R.layout.clock_hands_large_e, R.color.e_viola),
					new Hand(R.layout.clock_hands_large_f, R.color.f_verde),
					new Hand(R.layout.clock_hands_large_g, R.color.g_giallo),
					new Hand(R.layout.clock_hands_large_h, R.color.h_arancio),
					new Hand(R.layout.clock_hands_large_i, R.color.i_rosso),
					new Hand(R.layout.clock_hands_large_j, R.color.j_fucsia),
					new Hand(R.layout.clock_hands_large_k, R.color.k_agua),
					new Hand(R.layout.clock_hands_large_l, R.color.l_verdino)};

			Hand[] preciseHands = new Hand[]{
					new Hand(R.layout.clock_hands_precise_a, R.color.a_karbon),
					new Hand(R.layout.clock_hands_precise_b, R.color.b_grigio),
					new Hand(R.layout.clock_hands_precise_c, R.color.c_chiaro),
					new Hand(R.layout.clock_hands_precise_d, R.color.d_honest),
					new Hand(R.layout.clock_hands_precise_e, R.color.e_viola),
					new Hand(R.layout.clock_hands_precise_f, R.color.f_verde),
					new Hand(R.layout.clock_hands_precise_g, R.color.g_giallo),
					new Hand(R.layout.clock_hands_precise_h, R.color.h_arancio),
					new Hand(R.layout.clock_hands_precise_i, R.color.i_rosso),
					new Hand(R.layout.clock_hands_precise_j, R.color.j_fucsia),
					new Hand(R.layout.clock_hands_precise_k, R.color.k_agua),
					new Hand(R.layout.clock_hands_precise_l, R.color.l_verdino)};

			Hand[] preciseFloatingHands = new Hand[]{
					new Hand(R.layout.clock_hands_precisef_a, R.color.a_karbon),
					new Hand(R.layout.clock_hands_precisef_b, R.color.b_grigio),
					new Hand(R.layout.clock_hands_precisef_c, R.color.c_chiaro),
					new Hand(R.layout.clock_hands_precisef_d, R.color.d_honest),
					new Hand(R.layout.clock_hands_precisef_e, R.color.e_viola),
					new Hand(R.layout.clock_hands_precisef_f, R.color.f_verde),
					new Hand(R.layout.clock_hands_precisef_g, R.color.g_giallo),
					new Hand(R.layout.clock_hands_precisef_h, R.color.h_arancio),
					new Hand(R.layout.clock_hands_precisef_i, R.color.i_rosso),
					new Hand(R.layout.clock_hands_precisef_j, R.color.j_fucsia),
					new Hand(R.layout.clock_hands_precisef_k, R.color.k_agua),
					new Hand(R.layout.clock_hands_precisef_l, R.color.l_verdino)};

			Hand[] arrowHands = new Hand[]{
					new Hand(R.layout.clock_hands_arrow_a, R.color.a_karbon),
					new Hand(R.layout.clock_hands_arrow_b, R.color.b_grigio),
					new Hand(R.layout.clock_hands_arrow_c, R.color.c_chiaro),
					new Hand(R.layout.clock_hands_arrow_d, R.color.d_honest),
					new Hand(R.layout.clock_hands_arrow_e, R.color.e_viola),
					new Hand(R.layout.clock_hands_arrow_f, R.color.f_verde),
					new Hand(R.layout.clock_hands_arrow_g, R.color.g_giallo),
					new Hand(R.layout.clock_hands_arrow_h, R.color.h_arancio),
					new Hand(R.layout.clock_hands_arrow_i, R.color.i_rosso),
					new Hand(R.layout.clock_hands_arrow_j, R.color.j_fucsia),
					new Hand(R.layout.clock_hands_arrow_k, R.color.k_agua),
					new Hand(R.layout.clock_hands_arrow_l, R.color.l_verdino)};

			int[] ringOverlays = new int[]{R.drawable.clk_overlay_ring_a,
					R.drawable.clk_overlay_ring_b,
					R.drawable.clk_overlay_ring_c,
					R.drawable.clk_overlay_ring_d,
					R.drawable.clk_overlay_ring_e,
					R.drawable.clk_overlay_ring_f,
					R.drawable.clk_overlay_ring_g,
					R.drawable.clk_overlay_ring_h,
					R.drawable.clk_overlay_ring_i,
					R.drawable.clk_overlay_ring_j,
					R.drawable.clk_overlay_ring_k,
					R.drawable.clk_overlay_ring_l};

			int[] arrowOverlays =  new int[] { R.drawable.clk_overlay_arrow_a,
					R.drawable.clk_overlay_arrow_b,
					R.drawable.clk_overlay_arrow_c,
					R.drawable.clk_overlay_arrow_d,
					R.drawable.clk_overlay_arrow_e,
					R.drawable.clk_overlay_arrow_f,
					R.drawable.clk_overlay_arrow_g,
					R.drawable.clk_overlay_arrow_h,
					R.drawable.clk_overlay_arrow_i,
					R.drawable.clk_overlay_arrow_j,
					R.drawable.clk_overlay_arrow_k,
					R.drawable.clk_overlay_arrow_l};

			int[] dotsOverlays =  new int[] { R.drawable.clk_overlay_dots_a,
					R.drawable.clk_overlay_dots_b,
					R.drawable.clk_overlay_dots_c,
					R.drawable.clk_overlay_dots_d,
					R.drawable.clk_overlay_dots_e,
					R.drawable.clk_overlay_dots_f,
					R.drawable.clk_overlay_dots_g,
					R.drawable.clk_overlay_dots_h,
					R.drawable.clk_overlay_dots_i,
					R.drawable.clk_overlay_dots_j,
					R.drawable.clk_overlay_dots_k,
					R.drawable.clk_overlay_dots_l};

			int[] preciseOverlays =  new int[] {
					R.drawable.clk_overlay_precise_a,
					R.drawable.clk_overlay_precise_b,
					R.drawable.clk_overlay_precise_c,
					R.drawable.clk_overlay_precise_d,
					R.drawable.clk_overlay_precise_e,
					R.drawable.clk_overlay_precise_f,
					R.drawable.clk_overlay_precise_g,
					R.drawable.clk_overlay_precise_h,
					R.drawable.clk_overlay_precise_i,
					R.drawable.clk_overlay_precise_j,
					R.drawable.clk_overlay_precise_k,
					R.drawable.clk_overlay_precise_l};

			Clock c;

			//			c = new SimpleClock(clock1hands, backgrounds);
			//			c.setCurrentHandIndex(3);
			//			availableClocks.add(c);

			// 1
			c = new SimpleClock(largeHands, backgrounds);
			c.setCurrentDialIndex(8);
			c.setCurrentHandIndex(2);
			availableClocks.add(c);

			// 2
			c = new SimpleClock(largeHands, backgrounds, ringOverlays);
			c.setCurrentDialIndex(3);
			c.setCurrentHandIndex(2);
			availableClocks.add(c);

			// 3
			c = new SimpleClock(thinHands, backgrounds, dotsOverlays);
			c.setCurrentDialIndex(6);
			c.setCurrentHandIndex(2);
			availableClocks.add(c);

			// 4
			c = new SimpleClock(thinHands, backgrounds, arrowOverlays);
			c.setCurrentDialIndex(7);
			c.setCurrentHandIndex(0);
			availableClocks.add(c);

			// 5
			c = new SimpleClock(thinHands, backgrounds, preciseOverlays);
			c.setCurrentDialIndex(3);
			c.setCurrentHandIndex(0);
			availableClocks.add(c);

			// 6
			c = new SimpleClock(arrowHands, backgrounds, arrowOverlays);
			c.setCurrentDialIndex(2);
			c.setCurrentHandIndex(4);
			availableClocks.add(c);

			// 7
			c = new SimpleClock(arrowHands, backgrounds, dotsOverlays);
			c.setCurrentDialIndex(0);
			c.setCurrentHandIndex(6);
			availableClocks.add(c);

			// 8
			c = new SimpleClock(arrowHands, backgrounds, preciseOverlays);
			c.setCurrentDialIndex(5);
			c.setCurrentHandIndex(0);
			availableClocks.add(c);

			// 9
			c = new ClockLayoutCenterDigital(digitalCutHands, backgrounds, ringOverlays);
			c.setCurrentDialIndex(0);
			c.setCurrentHandIndex(3);
			c.setToBeUpdated(true);
			availableClocks.add(c);

			// 10
			c = new ClockLayoutCenterDigital(digitalCutHands, backgrounds,dotsOverlays);
			c.setCurrentDialIndex(0);
			c.setCurrentHandIndex(6);
			c.setToBeUpdated(true);
			availableClocks.add(c);

			// 11
			c = new ClockLayoutCenterDigital(digitalCutHands, backgrounds,arrowOverlays);
			c.setCurrentDialIndex(5);
			c.setCurrentHandIndex(0);
			c.setToBeUpdated(true);
			availableClocks.add(c);

			// 12
			c = new ClockLayoutCenterDigital(digitalCutHands, backgrounds, preciseOverlays);
			c.setCurrentDialIndex(0);
			c.setCurrentHandIndex(5);
			c.setToBeUpdated(true);
			availableClocks.add(c);

			// 13
			c = new SimpleClock(preciseFloatingHands, backgrounds, dotsOverlays);
			c.setCurrentDialIndex(7);
			c.setCurrentHandIndex(0);
			availableClocks.add(c);

			// 14
			c = new SimpleClock(preciseFloatingHands, backgrounds, arrowOverlays);
			c.setCurrentDialIndex(0);
			c.setCurrentHandIndex(4);
			availableClocks.add(c);

			// 15
			c = new SimpleClock(preciseFloatingHands, backgrounds, preciseOverlays);
			c.setCurrentDialIndex(4);
			c.setCurrentHandIndex(0);
			availableClocks.add(c);

			// 16
			c = new PreciseClock(preciseHands, backgrounds, dotsOverlays);
			c.setCurrentDialIndex(6);
			c.setCurrentHandIndex(0);
			availableClocks.add(c);

			// 17
			c = new PreciseClock(preciseHands, backgrounds, arrowOverlays);
			c.setCurrentDialIndex(8);
			c.setCurrentHandIndex(0);
			availableClocks.add(c);

			// 18
			c = new PreciseClock(preciseHands, backgrounds, preciseOverlays);
			c.setCurrentDialIndex(0);
			c.setCurrentHandIndex(5);
			availableClocks.add(c);

		}

		return availableClocks;
	}

	public static int getClockCount(){
		return 18; //XXX
	}
}
