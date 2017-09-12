package com.createlier.freetime.webservices;


import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Set;

/**
 * Post Parameters
 * @author user
 *
 */
final public class RequestParameters {

	// Final Private Variables
	final private HashMap<String, String> mMap = new HashMap<String, String>();
		
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
	 * Mold Parameters
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	public String mold() {
		try {
			final StringBuilder builder = new StringBuilder();
			final Set<String> set = mMap.keySet();
			int count = 0;
			for(final String key : set) {
				final String value = mMap.get(key);
				builder.append(key + "=" + URLEncoder.encode(value, "UTF-8"));
				if(count < set.size() - 1)
					builder.append("&");
				count++;
			}
			return builder.toString();
		} catch (final Exception e) {
			return null;
		}
	}
	
	/**
	 * Make Request Parameters with String Parameters
	 * @return
	 */
	public static RequestParameters make(final String params) {
		final RequestParameters requestParameters = new RequestParameters();
		final String[] paramsSplit = params.split("&");
		for(final String param : paramsSplit) {
			if(param.matches(".*=.*")) {
				final String[] paramSplit = param.split("=");
				requestParameters.addParam(paramSplit[0], paramSplit[1]);
			}
		}
		return requestParameters;
	}
}
