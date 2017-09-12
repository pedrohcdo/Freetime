package com.createlier.freetime.services.background;

import android.os.StrictMode;
import com.createlier.freetime.services.ServiceConnector;
import com.createlier.freetime.services.ServiceRunnable;

import java.util.concurrent.Callable;

/**
 * Shared Service Runnable
 * 
 * @author Pedro Henrique
 *
 */
public class BackgroundService implements Callable<Void> {
	
	// Final Private Variables
	final private BackgroundServicesManager mBackgroundServices;
	final private Object mIdentifier;
	final private ServiceRunnable mThreadRunnable;
	final private ServiceConnector mConnector;

	/**
	 * Constructor
	 */
	public BackgroundService(final BackgroundServicesManager backgroundServices, final Object identifier, final ServiceRunnable runnable, final ServiceConnector connector) {
		mBackgroundServices = backgroundServices;
		mIdentifier = identifier;
		mThreadRunnable = runnable;
		mConnector = connector;
	}
	
	/**
	 * Get Identifier
	 *
	 * @return
	 */
	final protected Object getIdentifier() {
		return mIdentifier;
	}

	/**
	 * Call
	 */
	@Override
	final public Void call() {
		//
		android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);
		// Set Thread Name
		Thread.currentThread().setName("BackgroundServiceThread: " + mIdentifier);
		// Set Thread Policy
		final StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy);
		// Run
		mThreadRunnable.run(mBackgroundServices, mConnector);
		//
		mBackgroundServices.getThreadGroup().removeServiceControl(this);
		return null;
	};
}
