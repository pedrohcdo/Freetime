package com.createlier.freetime.services.shared;


import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.createlier.freetime.services.ServiceConnector;
import com.createlier.freetime.services.ServiceRunnable;
import com.createlier.freetime.services.ServicesManager;
import com.createlier.freetime.services.ServicesManagerListener;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.SynchronousQueue;

/**
 * Shared Services Manager
 * 
 * @author Pedro Henrique
 * 
 */
final public class SharedServicesManager implements ServicesManager {
	
	/**
	 * Shared Handler
	 * 
	 * @author Pedro Henrique
	 *
	 */
	final private class SharedHandler extends Handler {
		
		/**
		 * Constructor
		 * 
		 * @param looper
		 */
		public SharedHandler(final Looper looper) {
			super(looper);
		}
		
		/**
		 * Handle Message
		 */
		@Override
		public void handleMessage(final Message msg) {
			// Send Message
			if(mServicesManagerListener != null)
				mServicesManagerListener.onServicesManagerResponse(SharedServicesManager.this, msg.what, msg.obj);
		}
	}

	/**
	 * Mode
	 */
	public enum Mode {
		LINKEDBLOCKING,
		ARRAYBLOCKING,
		SYNCHRONOUS;

		/**
		 * Instantiate
		 *
		 * @param length
         * @return
         */
		public BlockingQueue<Runnable> instantiate(int length) {
			switch (this) {
				case LINKEDBLOCKING:
					return new LinkedBlockingQueue<>();
				case ARRAYBLOCKING:
					return new ArrayBlockingQueue<>(length);
				default:
				case SYNCHRONOUS:
					return new SynchronousQueue<>();
			}
		}

		/**
		 * Get Max Threads
		 * @return
         */
		public  int getMaxThreads() {
			switch (this) {
				case LINKEDBLOCKING:
					return 20;
				case ARRAYBLOCKING:
					return 20;
				default:
				case SYNCHRONOUS:
					return 1;
			}
		}
	}

	// Final Private Variables
	final private Context mContext;
	final private SharedHandler mSharedHandler;
	final private ServicesManagerListener mServicesManagerListener;
	final private SharedThreadGroup mThreadGroup;

	/**
	 * Constructor
	 */
	public SharedServicesManager(final Context context, final Mode mode, final ServicesManagerListener listener) {
		mContext = context;
		mServicesManagerListener = listener;
		
		mSharedHandler = new SharedHandler(Looper.getMainLooper());
		mThreadGroup = new SharedThreadGroup(this, mode);
	}

	/**
	 * Constructor
	 */
	public SharedServicesManager(final Context context, final ServicesManagerListener listener) {
		mContext = context;
		mServicesManagerListener = listener;

		mSharedHandler = new SharedHandler(Looper.getMainLooper());
		mThreadGroup = new SharedThreadGroup(this, Mode.LINKEDBLOCKING);
	}
	
	/**
	 * Get Context
	 * 
	 * @return Context
	 */
	@Override
	final public Context getContext() {
		return mContext;
	}
	
	/**
	 * Get Thread Group
	 * @return
	 */
	protected SharedThreadGroup getThreadGroup() {
		return mThreadGroup;
	}
	
	/**
	 * Has Service
	 */
	@Override
	public boolean hasService(int identifier) {
		return mThreadGroup.hasServiceThread(identifier);
	}
	
	/**
	 * Has Service
	 */
	@Override
	public boolean hasService(String identifier) {
		return mThreadGroup.hasServiceThread(identifier);
	}
	
	/**
	 * Add Service
	 * 
	 * @param runnable
	 * @return 
	 */
	@Override
	final public int addService(final ServiceRunnable runnable) {
		return mThreadGroup.addServiceThread(runnable);
	}
	
	/**
	 * Add Service
	 * 
	 * @param runnable
	 * @return 
	 */
	@Override
	final public void addService(final ServiceRunnable runnable, final String identifier) {
		mThreadGroup.addServiceThread(runnable, identifier);
	}
	
	/**
	 * Get Service
	 */
	@Override
	public ServiceRunnable getService(int identifier) {
		return mThreadGroup.getService(identifier);
	}

	/**
	 * Get Service
	 */
	@Override
	public ServiceRunnable getService(String identifier) {
		return mThreadGroup.getService(identifier);
	}
	
	/**
	 * Wait for Service process end.
	 * <p>
	 * Note: If the process is not an end, the current process had been stuck in
	 * this statement.
	 * <p>
	 */
	@Override
	final public void waitForService(final int identifier) {
		// Wait for end service
		mThreadGroup.waitForService(identifier);
	}
	
	/**
	 * Wait for Service process end.
	 * <p>
	 * Note: If the process is not an end, the current process had been stuck in
	 * this statement.
	 * <p>
	 */
	@Override
	final public void waitForService(final String identifier) {
		// Wait for end service
		mThreadGroup.waitForService(identifier);
	}
	
	/**
	 * Remove Service
	 * 
	 * @param identifier
	 * @return
	 */
	@Override
	final public void removeService(final int identifier) {
		mThreadGroup.removeServiceThread(identifier);
	}
	
	/**
	 * Remove Service
	 * 
	 * @param identifier
	 * @return
	 */
	@Override
	final public void removeService(final String identifier) {
		mThreadGroup.removeServiceThread(identifier);
	}
	
	/**
	 * Post Empty Message
	 */
	@Override
	final public void postMessage(final int code) {
		postMessage(code, null);
	}
	
	/**
	 * Post Message
	 */
	@Override
	final public void postMessage(final int code, final Object obj) {
		final Message message = mSharedHandler.obtainMessage();
		message.what = code;
		message.obj = obj;
		mSharedHandler.sendMessage(message);
	}
	
	/**
	 * Post Runnable Message
	 */
	@Override
	final public void postMessage(final Runnable runnable) {
		mSharedHandler.post(runnable);
	}

	/**
	 * Get Service Connector
	 */
	@Override
	public ServiceConnector getServiceConnector(final int identifier) {
		return mThreadGroup.getServiceConnector(identifier);
	}

	/**
	 * Get Service Connector
	 */
	@Override
	public ServiceConnector getServiceConnector(final String identifier) {
		return mThreadGroup.getServiceConnector(identifier);
	}

	/**
	 * Finish
	 */
	final public void finish() {
		// Interrupt Services Group
		mThreadGroup.close();
	}
}
