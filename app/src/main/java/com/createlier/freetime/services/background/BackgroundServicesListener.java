package com.createlier.freetime.services.background;

/**
 * Background Services Receiver Listener
 * 
 * @author user
 *
 */
public interface BackgroundServicesListener {

	/**
	 * On Background Service Connected
	 * 
	 * @param backgroundServices
	 */
	public void onBackgroundServiceConnected(final BackgroundServicesManager backgroundServices);
}
