package com.createlier.freetime.webservices;

import android.util.Log;

/**
 * Request Result
 * @author user
 *
 */
public class RequestResult {
	
	//
	final private String mResponse;
	final private int mResponseCode;
	final private Treaty mTreaty;
	final private boolean mIsCache;
	
	/**
	 * Constructor
	 * 
	 * @param response
	 * @param responseCode
	 */
	protected RequestResult(final String response, final int responseCode, final Treaty treaty, final boolean isCache) {
		mResponse = response;
		mResponseCode = responseCode;
		mTreaty = treaty;
		mIsCache = isCache;
	}
	
	/**
	 * Get Response
	 *
	 * @return
	 */
	public String getResponse() {
		return mResponse;
	}
	
	/**
	 * Get Response Code
	 *
	 * @return
	 */
	public int getResponseCode() {
		return mResponseCode;
	}

	/**
	 * Get Treaty
	 *
	 * @return
     */
	public Treaty getTreaty() {
		return mTreaty;
	}

	/**
	 * Is Cache
	 *
	 * @return
     */
	public boolean isCache() {
		return mIsCache;
	}

	/**
	 * Is Successfully Response, default digit is 2.
	 *
	 * @return
	 */
	public boolean isSuccessfullyResponse(int... codes) {
		for(final int code : codes) {
			if(code == mResponseCode)
				return true;
		}
		if(Integer.parseInt(""+(""+mResponseCode).charAt(0)) == 2) {
			Log.w("BookclassLog", "Foi detectado uma rota que talvez possa ter sido modificada.");
			return true;
		}
		return false;
	}
}