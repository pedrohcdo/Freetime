package com.createlier.freetime.services;

/**
 * Services Manager Listener
 * @author user
 *
 */
public interface ServicesManagerListener {
	
	/**
	 * Message
	 * 
	 * @param code
	 */
	void onServicesManagerResponse(final ServicesManager servicesManager, final int code, final Object obj);
}
