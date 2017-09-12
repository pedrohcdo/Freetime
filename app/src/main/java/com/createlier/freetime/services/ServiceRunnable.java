package com.createlier.freetime.services;

/**
 * Shared Service Runnable
 * 
 * @author Pedro Henrique
 *
 */
public interface ServiceRunnable  {

	/** Run */
	public void run(final ServicesManager servicesManager, final ServiceConnector connector);
}
