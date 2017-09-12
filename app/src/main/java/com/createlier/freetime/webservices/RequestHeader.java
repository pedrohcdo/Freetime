package com.createlier.freetime.webservices;


import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;

/**
 * Post Parameters
 * @author user
 *
 */
final public class RequestHeader {

	// Final Private Variables
	final private HashMap<String, String> mMap = new HashMap<String, String>();

	/**
	 * Create Json Header
	 * @return
     */
	public static RequestHeader newJsonHeader() {
		final RequestHeader requestHeader = new RequestHeader();
		requestHeader.addParam("Content-Type", "application/json; charset=UTF-8");
		requestHeader.addParam("Accept", "application/json");
		return requestHeader;
	}

	/**
	 * Add Param
	 */
	public void addParam(final String key, final String value) {
		mMap.put(key, value);
	}
	
	/**
	 * Remove Parameter
	 * 
	 * @param key Key
	 */
	public void removeParam(final String key) {
		mMap.remove(key);
	}
	
	/**
	 * Fill to URLConnection
	 * @return
	 */
	public void fillTo(final URLConnection connection) {
		for(Map.Entry<String, String> entry : mMap.entrySet())
			connection.addRequestProperty(entry.getKey(), entry.getValue());
	}
}
