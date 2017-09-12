package com.createlier.freetime.services;

import android.content.Context;

/**
 * Services Manager Base
 * 
 * @author user
 *
 */
public interface ServicesManager {
	
	/**
	 * Add Service
	 * 
	 * @param runnable
	 * 
	 * @return Service Id
	 */
	public int addService(final ServiceRunnable runnable);
	
	/**
	 * Add Service
	 * 
	 * @param runnable
	 * @param identifier
	 */
	public void addService(final ServiceRunnable runnable, final String identifier);
	
	/**
	 * Get Service
	 * 
	 * @param identifier
	 * @return
	 */
	public ServiceRunnable getService(final int identifier);
	
	/**
	 * Get Service
	 * 
	 * @param identifier
	 * @return
	 */
	public ServiceRunnable getService(final String identifier);
		
	/**
	 * Has Service
	 * 
	 * @param identifier
	 * @return
	 */
	public boolean hasService(final int identifier);
	
	/**
	 * Has Service
	 * 
	 * @param identifier
	 * @return
	 */
	public boolean hasService(final String identifier);
	
	/**
	 * Wait for Service process end.
	 * <p>
	 * Note: If the process is not an end, the current process had been stuck in
	 * this statement.
	 * <p>
	 *
	 */
	public void waitForService(final int identifier);
	
	/**
	 * Wait for Service process end.
	 * <p>
	 * Note: If the process is not an end, the current process had been stuck in
	 * this statement.
	 * <p>
	 *
	 */
	public void waitForService(final String identifier);
	
	/**
	 * Remove Service
	 *
	 */
	public void removeService(final int identifier);
	
	/**
	 * Remove Service
	 *
	 */
	public void removeService(final String identifier);

	/**
	 * Post Empty Message
	 * 
	 * @param code
	 */
	public void postMessage(final int code);
	
	/**
	 * Post Message
	 * 
	 * @param code
	 * @param obj
	 */
	public void postMessage(final int code, final Object obj);
	
	/**
	 * Post Runnable Message
	 * 
	 * @param runnable
	 */
	public void postMessage(final Runnable runnable);

	/**
	 * Get Service Connector
	 */
	public ServiceConnector getServiceConnector(final int identifier);

	/**
	 * Get Service Connector
	 */
	public ServiceConnector getServiceConnector(final String identifier);

	/**
	 * Get Context
	 * @return
	 */
	public Context getContext();
}
