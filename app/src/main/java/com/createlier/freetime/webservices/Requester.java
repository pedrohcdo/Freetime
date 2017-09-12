package com.createlier.freetime.webservices;


import android.content.Context;
import android.os.Looper;

import com.createlier.freetime.exceptions.RequesterException;
import com.createlier.freetime.services.ServicesManager;
import com.createlier.freetime.utils.GeneralUtils;

/**
 * Data Resolver
 * 
 * @author Pedro Henrique
 *
 */
final public class Requester {
	
	/**
	 * Resolver Handler
	 *
	 * @author Pedro Henrique
	 *
	 */
	final private class ResolverHandler extends Handler {

		/**
		 * Constructor
		 * @param looper
		 */
		public ResolverHandler(final Looper looper) {
			super(looper);
		}

		/**
		 * Handler Message
		 */
		@Override
		public void handleMessage(Message msg) {
			// Not usable until this moment
		}
	}

	/**
	 * Request Type
	 * 
	 * @author Pedro
	 *
	 */
	public enum RequestType {
		
		GET,
		POST,
		PUT,
		DELETE,
		PATCH;
	}
	
	/**
	 * Request Mode
	 * 
	 * @author Pedro
	 *
	 */
	public enum RequestMode {
		
		IMMEDIATE,
		FUTURE,
		PERSISTENT;
	}
	
	/**
	 * Request State
	 * 
	 * @author Pedro
	 *
	 */
	public class RequestState {
		
		// Final Private Variables
		final private RequestType mRequestType;
		
		// Private Variables
		private RequestMode mRequestMode;
		private GeneralUtils.OnResultListener<RequestResult> mRequestListener;
		private String mRequestUrl;
		private RequestParameters mRequestParameters;
		private RequestConfig mRequestConfig;
		private RequestHeader mRequestHeader;
		private String mRequestBody;
		private RequestBody mRequestBodyListener;
		private Treaty mTreaty;

		/**
		 * Constructor
		 */
		private RequestState(final RequestType type) {
			if(type == null)
				throw new RequesterException("The request type can not be null.");
			mRequestType = type;
			mRequestMode = RequestMode.IMMEDIATE;
			mRequestListener = null;
			mRequestUrl = "";
			mRequestParameters = null;
			mRequestHeader = null;
			mRequestConfig = null;
			mRequestBody = null;
			mRequestBodyListener = null;
			mTreaty = null;
		}
		
		/**
		 * Set mode
		 * 
		 * @param mode
		 * @return
		 */
		public RequestState setMode(final RequestMode mode) {
			if(mode == null)
				throw new RequesterException("The request mode can not be null.");
			mRequestMode = mode;
			return this;
		}
		
		/**
		 * Set On Result Listener<br>
		 * obs(Set null to use the default)
		 * 
		 * @param listener
		 * @return
		 */
		public RequestState setOnResultListener(final GeneralUtils.OnResultListener<RequestResult> listener) {
			mRequestListener = listener;
			return this;
		}
		
		/**
		 * Set Url
		 * 
		 * @param url
		 * @return
		 */
		public RequestState setUrl(final String url) {
			if(url == null)
				throw new RequesterException("The URL can not be null.");
			mRequestUrl = url;
			return this;
		}
		
		/**
		 * Set Parameters<br>
		 * obs(Set null to use the default)
		 *
		 * @return
		 */
		public RequestState setParameters(final RequestParameters params) {
			mRequestParameters = params;
			return this;
		}

		/**
		 * Set Header<br>
		 * @param requestHeader
         * @return
         */
		public RequestState setHeader(final RequestHeader requestHeader) {
			mRequestHeader = requestHeader;
			return this;
		}

		/**
		 * Set Config<br>
		 * obs(Set null to use the default)
		 * 
		 * @param config
		 * @return
		 */
		public RequestState setConfig(final RequestConfig config) {
			mRequestConfig = config;
			return this;
		}
		
		/**
		 * Set Body<br>
		 * obs(Set null to use the default)
		 * 
		 * @param body
		 * @return
		 */
		public RequestState setBody(final String body) {
			mRequestBody = body;
			mRequestBodyListener = null;
			return this;
		}

		/**
		 * Set Body<Br>
		 * obs(Set null to use the default)
		 *
		 * @param body
         * @return
         */
		public RequestState setBody(final RequestBody body) {
			mRequestBodyListener = body;
			mRequestBody = null;
			return this;
		}

		/**
		 * Set Treaty
		 *
		 * @param treaty
         * @return
         */
		public RequestState setTreaty(final Treaty treaty) {
			mTreaty = treaty;
			return this;
		}
		
		/**
		 * Request Data
		 */
		public RequestResult request() {
			switch(mRequestType) {
			case DELETE:
				return mWebServices.deleteRequest(mRequestMode, mRequestUrl, mRequestParameters, mTreaty, mRequestConfig, mRequestHeader, mRequestBody, mRequestListener);
			case GET:
				return mWebServices.getRequest(mRequestMode, mRequestUrl, mRequestParameters, mTreaty, mRequestConfig, mRequestHeader, mRequestListener);
			case PATCH:
				break;
			default:
				////
			case POST:
				return mWebServices.postRequest(mRequestMode, mRequestUrl, mRequestParameters, mTreaty, mRequestConfig, mRequestHeader, mRequestBody, mRequestBodyListener, mRequestListener);
			case PUT:
				return mWebServices.putRequest(mRequestMode, mRequestUrl, mRequestParameters, mTreaty, mRequestConfig, mRequestHeader, mRequestBody, mRequestListener);
			}
			return null;
		}
	}
	
	// Final Private Variables
	final private Context mContext;
	final private ServicesManager mServicesManager;
	final private WebServices mWebServices;
	final private ResolverHandler mResolverHandler;
	
	/**
	 * Constructor
	 * 
	 */
	public Requester(final Context context, final ServicesManager servicesManager) {
		mContext = context;
		mServicesManager = servicesManager;
		mResolverHandler = new ResolverHandler(context.getMainLooper());
		/** The order is very important */
		mWebServices = new WebServices(context, this, mServicesManager, mResolverHandler);
	}
	
	/**
	 * Prepare Request
	 */
	public RequestState prepare(RequestType type) {
		return new RequestState(type);
	}
}
