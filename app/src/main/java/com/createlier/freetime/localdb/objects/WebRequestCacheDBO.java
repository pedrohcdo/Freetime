package com.createlier.freetime.localdb.objects;

/**
 * Request Object
 * 
 * @author Pedro Henrique
 *
 */
final public class WebRequestCacheDBO {

	// Final Public Static Variables
	final public static int TYPE_POST = 0;
	final public static int TYPE_GET = 1;
	final public static int TYPE_PUT = 2;

	// Final Private Variables
	final private int mId;

	// Private Variables
	private int mType;
	private String mUrl;
	private String mParameters;
	private int mResultCode;
	private String mResultBody;

	/**
	 * Constructor
	 */
	public WebRequestCacheDBO() {
		mId = -1;
	}

	/**
	 * Constructor
	 */
	public WebRequestCacheDBO(final int id) {
		mId = id;
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
	public String getParameters() {
		return mParameters;
	}
	
	/**
	 * Get Result Code
	 * 
	 * @return the mResultCode
	 */
	public int getResultCode() {
		return mResultCode;
	}

	/**
	 * Get Result Body
	 *
	 * @return the mResultBody
	 */
	public String getResultBody() {
		return mResultBody;
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
	public void setParameters(String requestParameters) {
		this.mParameters = requestParameters;
	}

	/**
	 * Set Result Code
	 */
	public void setResultCode(final int resultCode) {
		mResultCode = resultCode;
	}

	/**
	 * Set Result Body
	 */
	public void setResultBody(final String resultBody) {
		mResultBody = resultBody;
	}

}
