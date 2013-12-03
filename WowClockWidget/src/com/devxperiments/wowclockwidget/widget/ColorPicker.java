package com.devxperiments.wowclockwidget.widget;

import android.app.Activity;
import android.widget.ImageView;
import android.widget.TabHost.OnTabChangeListener;

import com.devxperiments.wowclockwidget.R;
import com.devxperiments.wowclockwidget.clocks.Clock;

public class ColorPicker implements OnTabChangeListener{

	private final int SELECTOR_DARK_ID  = R.drawable.clk_background_b;
	private final int SELECTOR_LIGHT_ID  = R.drawable.clk_background_c;
	
	private final int[] colorViewsIds = new int[]{R.id.img_a_karbon, R.id.img_b_grigio, R.id.img_c_chiaro, R.id.img_d_honest, R.id.img_e_viola, R.id.img_f_verde, R.id.img_g_giallo, R.id.img_h_arancio, R.id.img_i_rosso, R.id.img_j_fucsia, R.id.img_k_agua, R.id.img_l_verdino};
	
	private String currentTabId = ConfigActivity.TAB_ID_HANDS;
	private Clock selectedClock;
	private ImageView selectedColorView;
	
	private Activity inflater;
	
	public ColorPicker(Activity inflater) {
		this.inflater = inflater;
	}
	
	public void updateSelectedColor(Clock selectedClock){
		if(selectedClock == null)
			return;
		
		this.selectedClock = selectedClock;
		
		if(selectedColorView != null)
			selectedColorView.setImageDrawable(null);
		
		int viewId = currentTabId.equals(ConfigActivity.TAB_ID_HANDS)? colorViewsIds[selectedClock.getCurrentHandsIndex()]:colorViewsIds[selectedClock.getCurrentDialIndex()];
		
		selectedColorView = (ImageView) inflater.findViewById(viewId);
		
		selectedColorView.setImageResource(viewId == R.id.img_c_chiaro? SELECTOR_DARK_ID : SELECTOR_LIGHT_ID);
	}
	
	private void updateSelectedColor() {
		updateSelectedColor(selectedClock);
	}

	@Override
	public void onTabChanged(String tabId) {
		currentTabId = tabId;
		updateSelectedColor();
	}

	
}
