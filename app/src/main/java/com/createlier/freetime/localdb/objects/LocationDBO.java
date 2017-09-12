package com.createlier.freetime.localdb.objects;

import android.location.Location;

/**
 * Simple Location
 * 
 * @author Pedro Henrique
 *
 */
final public class LocationDBO {

	// Final Private Variables
	final private int mId;
	final private double mLatitude;
	final private double mLongitude;
	final private long mDate;
	
	/**
	 * Constructor
	 * 
	 * @param latitude
	 * @param longitude
	 */
	public LocationDBO(final int id, final double latitude, final double longitude, final long date) {
		mId = id;
		mLatitude = latitude;
		mLongitude = longitude;
		mDate = date;
	}
	
	/**
	 * Constructor
	 * 
	 * @param latitude
	 * @param longitude
	 */
	public LocationDBO(final double latitude, final double longitude, final long date) {
		mId = -1;
		mLatitude = latitude;
		mLongitude = longitude;
		mDate = date;
	}
	
	/**
	 * Constructor
	 * 
	 */
	public LocationDBO(final LocationDBO reducedLocation) {
		mId = reducedLocation.mId;
		mLatitude = reducedLocation.mLatitude;
		mLongitude = reducedLocation.mLongitude;
		mDate = reducedLocation.mDate;
	}
	
	
	/**
	 * Constructor
	 * 
	 * @param location
	 */
	public LocationDBO(final Location location, final long date) {
		mId = -1;
		mLatitude = location.getLatitude();
		mLongitude = location.getLongitude();
		mDate = date;
	}
	
	/**
	 * Get Id
	 * 
	 * @return
	 */
	public int getId() {
		return mId;
	}
	
	/**
	 * Get Latitude
	 * 
	 * @return
	 */
	public double getLatitude() {
		return mLatitude;
	}
	
	/**
	 * Get Longitude
	 * 
	 * @return
	 */
	public double getLongitude() {
		return mLongitude;
	}
	
	/**
	 * Get Date
	 * @return
	 */
	public long getDate() {
		return mDate;
	}
}
