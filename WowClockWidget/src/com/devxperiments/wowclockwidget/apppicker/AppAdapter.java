package com.devxperiments.wowclockwidget.apppicker;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.devxperiments.wowclockwidget.R;

public class AppAdapter extends ArrayAdapter<App> {
	private static final int ITEM_RESOURCE = R.layout.app_picker_listitem;

	public AppAdapter(Context context) {
		super(context, ITEM_RESOURCE);
	}
	
	private class ViewHolder {
		TextView txtAppName;
		ImageView imgIcon;
	}

	@Override
	public View getView(int position, View view, ViewGroup parent) {
		ViewHolder holder = null;

		if(view == null){
			LayoutInflater layoutInflater = ((Activity)getContext()).getLayoutInflater();
			view = layoutInflater.inflate(ITEM_RESOURCE, null);
			holder = new ViewHolder();
			holder.txtAppName = (TextView) view.findViewById(R.id.txtAppName);
			holder.imgIcon = (ImageView) view.findViewById(R.id.imgAppIcon);        
			view.setTag(R.id.tag_viewholder, holder);
		} else {
			holder = (ViewHolder) view.getTag(R.id.tag_viewholder);
		}

		App app = getItem(position);  
		holder.imgIcon.setImageDrawable(app.getIcon());
		holder.txtAppName.setText(app.getApplicationName());

		return view;
	}
}