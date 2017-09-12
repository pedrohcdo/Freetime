package com.createlier.freetime.location;


import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;

import com.createlier.freetime.R;

/**
 * Location Based Services
 * 
 * @author Pedro Henrique
 *
 */
final public class LocationBasedServices implements LocationListener {

	// Final Private Variables
	final private Context mContext;
	final private Looper mLooper;
	final private LocationManager mLocationManager;
	
	// Private Variables
	private Location mLocation;
	
	/**
	 * Constructor
	 * 
	 * @param context
	 */
	public <T extends Context & LocationBasedServicesListener> LocationBasedServices(final T context, final Looper looper) {
		mContext = context;
		mLooper = looper;
		mLocationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
	}
	
	/**
	 * Start Update
	 */
	public void start() {
		//
		try {
			// Remove any pending updates
			mLocationManager.removeUpdates(this);
			// Power Save Criteria
			final Criteria criteria = new Criteria();
			criteria.setAccuracy(Criteria.ACCURACY_COARSE);
			criteria.setPowerRequirement(Criteria.POWER_LOW);
			criteria.setAltitudeRequired(false);
			criteria.setBearingRequired(false);
			criteria.setSpeedRequired(false);
			
			// Request Location Updates
			mLocationManager.requestLocationUpdates(
					mContext.getResources().getInteger(R.integer.location_min_time_update),
					mContext.getResources().getInteger(R.integer.location_min_distance_update),
					criteria, this, mContext.getMainLooper());
			//
			//Log.d("LogTest", "Time: " + mContext.getResources().getInteger(R.integer.location_min_time_update));
			//Log.d("LogTest", "Distance: " + mContext.getResources().getInteger(R.integer.location_min_distance_update));
		} catch (final SecurityException e) {
			Log.d("LogTest", "Security Error on Location Based Service");
		}
		updateLocation();
		informLocation();
	}
	
	/**
	 * Stop Update
	 */
	public void stop() {
		try {
			mLocationManager.removeUpdates(this);
		} catch (final SecurityException e) {
			Log.d("LogTest", "Security Error on Location Based Service");
		}
	}
	
	/**
	 * Inform Location
	 */
	private void updateLocation() {
		try {
			final Location netLocation = mLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
			final Location gpsLocation = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
			mLocation = getBestLocation(netLocation, mLocation);
			mLocation = getBestLocation(gpsLocation, mLocation);
		} catch (final SecurityException e) {
			Log.d("LogTest", "Security Error on Location Based Service");
		}
	}
	
	/**
	 * Get Best Location
	 *
	 * @return Best Location
	 */
	protected Location getBestLocation(final Location locationA, Location locationB) {
		//
		if(locationA == null && locationB == null)
			return mLocation;
		else if (locationA == null)
	        return locationB;
	    else if(locationB == null)
	    	return locationA;
		
		//
	    long diffTime = locationA.getTime() - locationB.getTime();
	    int diffAccuracy = (int) (locationA.getAccuracy() - locationB.getAccuracy());
	    boolean same = (locationA.getProvider() == null && locationB.getProvider() == null) || (locationA.getProvider().equals(locationB.getProvider()));
	    
	    if(diffTime > 30000)
	        return locationA;
	    else if(diffTime < 30000)
	        return locationB;
	    if (diffAccuracy > 0)
	        return locationA;
	    else if (diffTime >= 0 && diffAccuracy < 0)
	        return locationA;
	    else if (diffTime >= 0 && !(diffAccuracy > 250) && same)
	        return locationA;
	    return locationB;
	}
	
	/**
	 * Inform Loca
	 */
	private void informLocation() {
		if(mLocation != null)
			((LocationBasedServicesListener) mContext).onLocationChanged(mLocation);
	}
	
	/** Unused */
	@Override
	public void onLocationChanged(Location location) {
		mLocation = getBestLocation(location, mLocation);
		informLocation();
	}

	/** Unused */
	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		Log.d("LogTest", "Provider Status Changed: " + provider);
	}

	/** Unused */
	@Override
	public void onProviderEnabled(String provider) {
		//
	}

	/** Unused */
	@Override
	public void onProviderDisabled(String provider) {}
	
	
}
