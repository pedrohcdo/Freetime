package com.createlier.freetime.location;

import android.location.Location;

/**
 * Location Based Services Listener
 * 
 * @author Pedro Henrique
 *
 */
public interface LocationBasedServicesListener {

	/** On Location Changed */
	public void onLocationChanged(final Location locale);
}
