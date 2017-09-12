package com.createlier.freetime.services.shared;


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
public class SharedService implements Callable<Void> {
	
	// Final Private Variables
	final private SharedServicesManager mShServicesManager;
	final private Object mIdentifier;
	final private ServiceRunnable mThreadRunnable;
	final private ServiceConnector mConnector;

	/**
	 * Constructor
	 */
	public SharedService(final SharedServicesManager shServicesManager, final Object identifier, final ServiceRunnable runnable, final ServiceConnector connector) {
		mShServicesManager = shServicesManager;
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
	final public Void call() throws Exception {
		// Set Thread Name
		Thread.currentThread().setName("SharedServicesThread: " + mIdentifier);
		// Set Thread Policy
		final StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy);
		// Run
		mThreadRunnable.run(mShServicesManager, mConnector);
		//
		mShServicesManager.getThreadGroup().removeServiceControl(this);
		return null;
	};
}
