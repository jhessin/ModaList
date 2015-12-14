package com.grillbrickstudios.modalist;

import android.app.Application;
import android.content.Context;

/**
 * Created by jhess on 11/29/2015 for ModaList.
 * Used for a global Application Context.
 */
public class App extends Application {
	public static final String CREATE_LIST = "com.grillbrickstudios.modalist.CREATE";
	private static Context _context;
	private static Context _activityContext;

	public static Context getActivityContext() {
		if (_activityContext == null) return _context;
		return _activityContext;
	}

	public static void setActivityContext(Context context) {
		_activityContext = context;
	}

	public static Context getContext() {
		return _context;
	}

	/**
	 * Called when the application is starting, before any activity, service,
	 * or receiver objects (excluding content providers) have been created.
	 * Implementations should be as quick as possible (for example using
	 * lazy initialization of state) since the time spent in this function
	 * directly impacts the performance of starting the first activity,
	 * service, or receiver in a process.
	 * If you override this method, be sure to call super.onCreate().
	 */
	@Override
	public void onCreate() {
		super.onCreate();
		_context = this;
	}

}
