package com.createlier.freetime.webservices;

/**
 * Request Config
 * 
 * @author user
 *
 */
final public class RequestConfig {
	
	public int connectionTimeout = 0;
	public int readTimeout = 0;
	public boolean doOutput = false;
	public boolean doInput = false;
	public boolean useCaches = false;

	/**
	 * Request Config
	 *
	 * @param doOutput
	 * @param timeout
     * @return
     */
	public static RequestConfig newPostConfig(boolean doOutput, int timeout) {
		final RequestConfig requestConfig = new RequestConfig();
		requestConfig.doInput = true;
		requestConfig.doOutput = doOutput;
		requestConfig.readTimeout = timeout;
		requestConfig.connectionTimeout = 3000;
		return requestConfig;
	}

	/**
	 * Request Config
	 *
	 * @param doOutput
	 * @param timeout
	 * @return
	 */
	public static RequestConfig newPutConfig(boolean doOutput, int timeout) {
		final RequestConfig requestConfig = new RequestConfig();
		requestConfig.doInput = true;
		requestConfig.doOutput = doOutput;
		requestConfig.readTimeout = timeout;
		requestConfig.connectionTimeout = 1000;
		return requestConfig;
	}

	/**
	 * Request Config
	 *
	 * @param doOutput
	 * @param timeout
	 * @return
	 */
	public static RequestConfig newDeleteConfig(boolean doOutput, int timeout) {
		final RequestConfig requestConfig = new RequestConfig();
		requestConfig.doInput = true;
		requestConfig.doOutput = doOutput;
		requestConfig.readTimeout = timeout;
		requestConfig.connectionTimeout = 10000;
		return requestConfig;
	}

	/**
	 * Request Config
	 *
	 * @param timeout
	 * @return
	 */
	public static RequestConfig newGetConfig(int timeout) {
		final RequestConfig requestConfig = new RequestConfig();
		requestConfig.doInput = true;
		requestConfig.readTimeout = timeout;
		requestConfig.connectionTimeout = 10000;
		return requestConfig;
	}

	/**
	 * Request Config
	 *
	 * @return
	 */
	public static RequestConfig newGetConfig() {
		final RequestConfig requestConfig = new RequestConfig();
		requestConfig.doInput = true;
		requestConfig.readTimeout = 10000;
		requestConfig.connectionTimeout = 10000;
		return requestConfig;
	}
}
