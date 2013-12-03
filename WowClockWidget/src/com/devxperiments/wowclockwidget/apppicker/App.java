package com.devxperiments.wowclockwidget.apppicker;


import android.content.ComponentName;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.drawable.Drawable;

public class App implements Comparable<App>{
	
	public static final String APP_PKG_CLS_PREF = "appPkgClsPref";
	public static final String APP_NONE = "none";
	
	private static final char PKG_CLASS_SEPARATOR = '|';

	private Drawable icon;
	private String applicationName;
	private String packageName;
	private String className;

	
	protected App(String applicationName, Drawable icon){
		this.applicationName = applicationName;
		this.icon = icon;
	}
	
	public App(Context context, String packageName, String className) throws NameNotFoundException{
		this(context.getPackageManager(), new ComponentName(packageName, className));
	}
	
	public App(Context context, ComponentName cn) throws NameNotFoundException{
		this(context.getPackageManager(), cn);
	}
	
	public App(PackageManager pm, ComponentName cn) throws NameNotFoundException{
		this(pm, cn, pm.getApplicationInfo(cn.getPackageName(), PackageManager.GET_META_DATA));
	}
	
	public App(PackageManager pm, ComponentName cn, ApplicationInfo applicationInfo) {
		this.packageName = cn.getPackageName();
		this.className = cn.getClassName();
		this.applicationName = applicationInfo.loadLabel(pm).toString();
		this.icon = applicationInfo.loadIcon(pm);
	}

	public Drawable getIcon() {
		return icon;
	}
	
	public String getName() {
		return applicationName;
	}
	
	public String getPackageName() {
		return packageName;
	}
	
	public String getClassName() {
		return className;
	}
	
	public ComponentName getComponentName() {
		return new ComponentName(getPackageName(), getClassName());
	}


	@Override
	public int compareTo(App another) {
		return getName().compareToIgnoreCase(another.getName());
	}
	
	public static App fromPrefString(Context context, String prefString) throws NameNotFoundException {
		if(prefString.equals(APP_NONE))
			return new NoApp(context);
		int indexOfSeparator = prefString.indexOf(PKG_CLASS_SEPARATOR);
		String packageName = prefString.substring(0, indexOfSeparator);
		String className = prefString.substring(indexOfSeparator+1);
		return new App(context, packageName, className);
	}
	
	public String toPrefString(){
		return getPackageName()+PKG_CLASS_SEPARATOR+getClassName();
	}


}
