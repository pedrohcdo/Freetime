package com.createlier.freetime.localdb.objects;


import com.createlier.freetime.webservices.RequestConfig;
import com.createlier.freetime.webservices.RequestParameters;

/**
 * Request Object
 * 
 * @author Pedro Henrique
 *
 */
final public class WebRequestDBO {

	// Final Public Static Variables
	final public static int REQUEST_TYPE_POST = 0;
	final public static int REQUEST_TYPE_GET = 1;
	final public static int REQUEST_TYPE_PUT = 2;
	
	// Final Private Variables
	final private int mId;
	
	// Private Variables
	private int mType;
	private String mUrl;
	private RequestParameters mParameters;
	private RequestConfig mConfig;
	
	/**
	 * Constructor
	 */
	public WebRequestDBO() {
		mId = -1;
		mType = REQUEST_TYPE_POST;
		mUrl = "";
		mParameters = new RequestParameters();
		mConfig = new RequestConfig();
	}

	/**
	 * Constructor
	 */
	public WebRequestDBO(final int id) {
		mId = id;
		mType = REQUEST_TYPE_POST;
		mUrl = "";
		mParameters = new RequestParameters();
		mConfig = new RequestConfig();
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
	 * Get Request Type
	 * @return the mRequestType
	 */
	public int getType() {
		return mType;
	}
	
	/**
	 * Get Url
	 * 
	 * @return the mUrl
	 */
	public String getUrl() {
		return mUrl;
	}
	
	/**
	 * Get Request Parameters
	 * 
	 * @return the mRequestParameters
	 */
	public RequestParameters getParameters() {
		return mParameters;
	}
	
	/**
	 * Get Request Config
	 * 
	 * @return the mRequestConfig
	 */
	public RequestConfig getConfig() {
		return mConfig;
	}
	
	/**
	 * Set Url
	 * 
	 * @param mUrl the mUrl to set
	 */
	public void setUrl(String mUrl) {
		this.mUrl = mUrl;
	}
	
	/**
	 * Set Request Type
	 *
	 */
	public void setType(int requestType) {
		this.mType = requestType;
	}

	/**
	 * Set Request Parameters
	 *
	 */
	public void setParameters(RequestParameters requestParameters) {
		if(requestParameters == null)
			requestParameters = new RequestParameters();
		this.mParameters = requestParameters;
	}

	/**
	 * Set Request Config
	 *
	 */
	public void setConfig(RequestConfig requestConfig) {
		if(requestConfig == null)
			requestConfig = new RequestConfig();
		this.mConfig = requestConfig;
	}
}
