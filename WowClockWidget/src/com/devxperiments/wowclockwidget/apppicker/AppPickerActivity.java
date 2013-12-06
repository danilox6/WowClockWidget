package com.devxperiments.wowclockwidget.apppicker;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.ListView;

import com.actionbarsherlock.app.SherlockListActivity;
import com.actionbarsherlock.view.MenuItem;
import com.devxperiments.wowclockwidget.R;

public class AppPickerActivity extends SherlockListActivity{

	private AppAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.app_picker_layout); 

		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		adapter = new AppAdapter(this);
		setListAdapter(adapter);
		
		new ListUpdateTask().execute();
	}
	
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		App app = adapter.getItem(position);
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		if(app instanceof NoApp)
			prefs.edit().putString(App.APP_PKG_CLS_PREF, App.APP_NONE).commit();
		else
			prefs.edit().putString(App.APP_PKG_CLS_PREF, app.toPrefString()).commit();
		setResult(RESULT_OK);
		finish();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			setResult(RESULT_CANCELED);
			finish();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	public class ListUpdateTask extends AsyncTask<Void, Void, List<App>> {

		@Override
		protected List<App> doInBackground(Void... params) {
			List<App> appList = new ArrayList<App>();
			PackageManager pm = getPackageManager();
		    List<ApplicationInfo> infos = pm.getInstalledApplications(PackageManager.GET_META_DATA);
		    for (ApplicationInfo appInfo : infos) {
		    	Intent intent =  pm.getLaunchIntentForPackage(appInfo.packageName);
		    	if(intent != null )
		    		appList.add(new App(pm,intent.getComponent(), appInfo));
		    	
		    }
		    appList.add(new NoApp(getApplicationContext()));
		    appList.add(App.getConfigApp(getApplicationContext()));
		    
		    Collections.sort(appList);
			return appList;
		}
		
		@Override
		protected void onPostExecute(List<App> results) {
			adapter.clear();
			for(App app: results)
				adapter.add(app);
		}
		
	}
}
