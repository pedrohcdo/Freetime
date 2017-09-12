package com.createlier.freetime.services.background;


import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.IBinder;

/**
 * Background Service Connection
 * 
 * @author Pedro Henrique
 *
 */
final public class BackgroundServicesConnection implements ServiceConnection {
	
	// Final Private Variables
	final private BackgroundServicesListener mListener;
	
	// Private Variables
	private BackgroundServicesManager mBackgroundServices;
	
	/**
	 * Constructor
	 */
	public BackgroundServicesConnection(final BackgroundServicesListener listener) {
		mListener = listener;
	}
	
	/**
	 * On Service Connected
	 */
	@Override
	public void onServiceConnected(ComponentName name, IBinder service) {
		mBackgroundServices = ((BackgroundServicesManager.BackgroundBinder) service).getService();
		if(mListener != null)
			mListener.onBackgroundServiceConnected(mBackgroundServices);
	}

	/**
	 * On Service Disconnected
	 */
	@Override
	public void onServiceDisconnected(ComponentName name) {
		mBackgroundServices = null;
	}
	
	/**
	 * Background Service
	 * @return
	 */
	public BackgroundServicesManager getBackgroundServices() {
		return mBackgroundServices;
	}
}
