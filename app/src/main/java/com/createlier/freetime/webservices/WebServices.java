package com.createlier.freetime.webservices;


import android.content.Context;
import android.os.Looper;
import android.util.Log;

import com.createlier.freetime.caching.requests.RequestCache;
import com.createlier.freetime.db.FreetimeDatabase;
import com.createlier.freetime.exceptions.WebServicesException;
import com.createlier.freetime.localdb.LocalDatabase;
import com.createlier.freetime.localdb.dao.WebRequestDao;
import com.createlier.freetime.localdb.objects.WebRequestCacheDBO;
import com.createlier.freetime.localdb.objects.WebRequestDBO;
import com.createlier.freetime.services.ServiceConnector;
import com.createlier.freetime.services.ServiceRunnable;
import com.createlier.freetime.services.ServicesManager;
import com.createlier.freetime.utils.GeneralUtils;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Data Resolver
 * 
 * @author Pedro Henrique
 *
 */
final public class WebServices {
	
	/**
	 * Block Request Result
	 * 
	 * @author user
	 *
	 */
	final private class BlockRequestResult extends RequestResult {

		// Private Variables
		volatile private boolean mReleased = false;
		volatile private RequestResult mValidResult;

		/**
		 * Constructor
		 */
		public BlockRequestResult() {
			super("", 0, null, false);
		}

		/**
		 * Release Result
		 * 
		 * @param result
		 */
		private void releaseResult(final RequestResult result) {
			mValidResult = result;
			/** This order is very important. */
			mReleased = true;
		}

		/**
		 * Get Response
		 * 
		 * @return
		 */
		@Override
		public String getResponse() {
			assertRequest();
			return mValidResult.getResponse();
		}

		/**
		 * Get Response Code
		 * 
		 * @return
		 */
		public int getResponseCode() {
			assertRequest();
			return mValidResult.getResponseCode();
		}

		/**
		 * Get Treaty
		 *
		 * @return
         */
		@Override
		public Treaty getTreaty() {
			assertRequest();
			return mValidResult.getTreaty();
		}

		/**
		 * Is Cache
		 *
		 * @return
         */
		@Override
		public boolean isCache() {
			assertRequest();
			return mValidResult.isCache();
		}

		/**
		 * 
		 * @return
		 */
		public boolean isSuccessfullyResponse() {
			assertRequest();
			return mValidResult.isSuccessfullyResponse();
		}

		/**
		 * Assert Request
		 */
		public void assertRequest() {
			if (!mReleased)
				throw new WebServicesException(
						"This result was obtained through a future request for access is necessary to await the end of the request.");
		}

	}

	// Conts


	final public static int RESPONSE_GET = 0;

	// Final Private Variables
	final Context mContext;
	final Requester mDataResolver;
	final ServicesManager mServicesManager;
	final Handler mHandler;
	final LocalDatabase mLocalDatabase;
	
	/**
	 * Constructor
	 * 
	 */
	public WebServices(final Context context, final Requester dataResolver, final ServicesManager servicesManager, final Handler handler) {
		mContext = context;
		mDataResolver = dataResolver;
		mServicesManager = servicesManager;
		mHandler = handler;
		mLocalDatabase = FreetimeDatabase.newInstance(context);
	}

	/**
	 * Get Request
	 */
	public RequestResult getRequest(final Requester.RequestMode mode, final String url, final RequestParameters params, Treaty treaty, final RequestConfig config, final RequestHeader header, final GeneralUtils.OnResultListener<RequestResult> onResult) {
		//
		if(treaty == null)
			treaty = new Treaty();
		// Modes
		if (mode == Requester.RequestMode.IMMEDIATE) {
			//
			if(treaty != null && treaty.caching()) {
				Log.d("FreetimeLog", "From: " + url);
				final WebRequestCacheDBO webRequestCacheDBO = RequestCache.searchrequest(url, mode.ordinal(), null);
				Log.d("FreetimeLog", "Result A: " + webRequestCacheDBO);
				if(webRequestCacheDBO != null) {
					Log.d("FreetimeLog", "Result: " + webRequestCacheDBO);
					if (onResult != null) {
						onResult.onResult(new RequestResult(webRequestCacheDBO.getResultBody(), webRequestCacheDBO.getResultCode(), treaty, true));
						Log.d("FreetimeLog", "Called: " + webRequestCacheDBO.getResultBody());
					}
				}
			}
			//
			final RequestResult result = GETRequest(url, params, treaty, config, header);
			if(treaty != null && treaty.caching()) {
				RequestCache.cahceRequest(url, mode.ordinal(), "", result.getResponseCode(), result.getResponse());
				Log.d("FreetimeLog", "Inserted: " + result.getResponseCode() + ", " +result.getResponse());
			}
			if (onResult != null)
				onResult.onResult(result);
			return result;
		} else if (mode == Requester.RequestMode.FUTURE) {
			//
			final BlockRequestResult blockRequestResult = new BlockRequestResult();

			/**
			 * Create Service
			 */
			//
			final Treaty finalTreaty = treaty;

			///
			WebRequestCacheDBO webRequestCacheDBO = null;
			if(finalTreaty != null) {

				if(finalTreaty.caching() || finalTreaty.cachingCompare())
					webRequestCacheDBO = RequestCache.searchrequest(url, mode.ordinal(), null);

				if(webRequestCacheDBO != null && finalTreaty.caching() && finalTreaty.showCache()) {
					Log.d("FreetimeLog", "Ok");
					final WebRequestCacheDBO finalWebRequestCacheDBO = webRequestCacheDBO;
					if (onResult != null && Thread.currentThread() == Looper.getMainLooper().getThread()) {
						Log.d("FreetimeLog", "Ok -----");
						onResult.onResult(new RequestResult(finalWebRequestCacheDBO.getResultBody(), finalWebRequestCacheDBO.getResultCode(), finalTreaty, true));

					}
				}
			}


			//
			final WebRequestCacheDBO finalWebRequestCacheDBO = webRequestCacheDBO;
			final ServiceRunnable serviceRunnable = new ServiceRunnable() {

				/**
				 * Run Service
				 */
				@Override
				public void run(ServicesManager shServicesManager, ServiceConnector connector) {
					//
					try {
						//

						// Get JSon
						final RequestResult requestResult = GETRequest(url, params, finalTreaty, config, header);
						blockRequestResult.releaseResult(requestResult);

						// If compare
						if(finalTreaty != null && finalTreaty.cachingCompare() && finalWebRequestCacheDBO != null) {
							if(finalWebRequestCacheDBO.getResultCode() == requestResult.getResponseCode() &&
									finalWebRequestCacheDBO.getResultBody().trim().equals(requestResult.getResponse().trim())) {
								//
								Log.d("LogTest", "Equalsss")
								;								//
								return;
							}
						}

						// Cache
						if(finalTreaty != null && (finalTreaty.caching() || finalTreaty.cachingCompare()))
							RequestCache.cahceRequest(url, mode.ordinal(), "", requestResult.getResponseCode(), requestResult.getResponse());


						// Post
						mHandler.post(new Runnable() {

							@Override
							public void run() {
								if(onResult != null)
									onResult.onResult(requestResult);
							}
						});
						// Error
					} catch (final Exception e) {
						//
						final RequestResult error = new RequestResult(null, 404, finalTreaty, false);
						blockRequestResult.releaseResult(error);
						// Post Error
						mHandler.post(new Runnable() {

							@Override
							public void run() {
								if(onResult != null)
									onResult.onResult(error);
							}
						});
					}
				}
			};
			//
			mServicesManager.addService(serviceRunnable);
			//
			return blockRequestResult;
		}
		return null;
	}

	/**
	 * Post Request
	 * 
	 * @return
	 * @throws IOException
	 * @throws MalformedURLException
	 */
	/**
	 * Get Request
	 */
	public RequestResult postRequest(final Requester.RequestMode mode, final String url, final RequestParameters params, final Treaty treaty, final RequestConfig config, final RequestHeader header, final String data, final RequestBody body, final GeneralUtils.OnResultListener<RequestResult> onResult) {
		// Immediate
		if (mode == Requester.RequestMode.IMMEDIATE) {
			final RequestResult result = POSTRequest(url, params, treaty, config, header, data, body);
			if (onResult != null)
				onResult.onResult(result);
			return result;
		// Future
		} else if (mode == Requester.RequestMode.FUTURE) {
			//
			final BlockRequestResult blockRequestResult = new BlockRequestResult();
			/**
			 * Create Service
			 */
			final ServiceRunnable serviceRunnable = new ServiceRunnable() {

				/**
				 * Run Service
				 */
				@Override
				public void run(ServicesManager shServicesManager, ServiceConnector connector) {
					//
					try {
						// Get JSon
						final RequestResult requestResult = POSTRequest(url, params, treaty, config, header, data, body);
						blockRequestResult.releaseResult(requestResult);

						// Post
						mHandler.post(new Runnable() {

							@Override
							public void run() {
								if(onResult != null)
									onResult.onResult(requestResult);
							}
						});

						// Error
					} catch (final Exception e) {
						//
						final RequestResult error = new RequestResult(null, 404, treaty, false);
						blockRequestResult.releaseResult(error);
						// Post Error
						mHandler.post(new Runnable() {

							@Override
							public void run() {
								if(onResult != null)
									onResult.onResult(error);
							}
						});
					}
				}
			};
			//
			mServicesManager.addService(serviceRunnable);
			//
			return blockRequestResult;
		// Persistent
		} else if(mode == Requester.RequestMode.PERSISTENT) {
			// Attempt
			postRequest(Requester.RequestMode.FUTURE, url, params, treaty, config, header, data, body, new GeneralUtils.OnResultListener<RequestResult>() {
				
				/**
				 * On Result
				 */
				@Override
				public void onResult(RequestResult result) {
					// If Successfully Responsed
					if(result.isSuccessfullyResponse()) {
						// Pass
						if(onResult != null)
							onResult.onResult(result);
					// Error
					} else {
						// Obtain
						mLocalDatabase.open();
						WebRequestDao webRequestDao = new WebRequestDao(mLocalDatabase);
						try {
							GeneralUtils.SyncThreadPass.obtainSyncThreadPass(WebRequestDao.class, 10, -1);
						} catch (Exception e) {
							return;
						}
						// Add Request
						WebRequestDBO webRequestDBO = new WebRequestDBO();
						webRequestDBO.setType(WebRequestDBO.REQUEST_TYPE_POST);
						webRequestDBO.setUrl(url);
						webRequestDBO.setParameters(params);
						webRequestDBO.setConfig(config);
						webRequestDao.insert(webRequestDBO);
						// Release
						GeneralUtils.SyncThreadPass.releaseSyncThreadPass(WebRequestDao.class);
						mLocalDatabase.close();
					}
				}
			});
		}

		return null;
	}
	
	/**
	 * Post Request
	 * 
	 * @return
	 * @throws IOException
	 * @throws MalformedURLException
	 */
	/**
	 * Get Request
	 */
	public RequestResult putRequest(final Requester.RequestMode mode, final String url, final RequestParameters params, final Treaty treaty, final RequestConfig config, final RequestHeader header, final String data, final GeneralUtils.OnResultListener<RequestResult> onResult) {
		// Immediate
		if (mode == Requester.RequestMode.IMMEDIATE) {
			final RequestResult result = PUTRequest(url, params, treaty, config, header, data);
			if (onResult != null)
				onResult.onResult(result);
			return result;
		// Future
		} else if (mode == Requester.RequestMode.FUTURE) {
			//
			final BlockRequestResult blockRequestResult = new BlockRequestResult();
			/**
			 * Create Service
			 */
			final ServiceRunnable serviceRunnable = new ServiceRunnable() {

				/**
				 * Run Service
				 */
				@Override
				public void run(ServicesManager shServicesManager, ServiceConnector connector) {
					//
					try {
						// Get JSon
						final RequestResult requestResult = PUTRequest(url, params, treaty, config, header, data);
						blockRequestResult.releaseResult(requestResult);

						// Post
						mHandler.post(new Runnable() {

							@Override
							public void run() {
								if(onResult != null)
									onResult.onResult(requestResult);
							}
						});

						// Error
					} catch (final Exception e) {
						//
						final RequestResult error = new RequestResult(null, 404, treaty, false);
						blockRequestResult.releaseResult(error);
						// Post Error
						mHandler.post(new Runnable() {

							@Override
							public void run() {
								if(onResult != null)
									onResult.onResult(error);
							}
						});
					}
				}
			};
			//
			mServicesManager.addService(serviceRunnable);
			//
			return blockRequestResult;
		// Persistent
		} else if(mode == Requester.RequestMode.PERSISTENT) {
			// Attempt
			putRequest(Requester.RequestMode.FUTURE, url, params, treaty, config, header, data, new GeneralUtils.OnResultListener<RequestResult>() {
				
				/**
				 * On Result
				 */
				@Override
				public void onResult(RequestResult result) {
					// If Successfully Responsed
					if(result.isSuccessfullyResponse()) {
						// Pass
						if(onResult != null)
							onResult.onResult(result);
					// Error
					} else {
						// Obtain
						mLocalDatabase.open();
						WebRequestDao webRequestDao = new WebRequestDao(mLocalDatabase);
						try {
							GeneralUtils.SyncThreadPass.obtainSyncThreadPass(WebRequestDao.class, 10, -1);
						} catch (Exception e) {
							return ;
						}
						// Add Request
						WebRequestDBO webRequestDBO = new WebRequestDBO();
						webRequestDBO.setType(WebRequestDBO.REQUEST_TYPE_PUT);
						webRequestDBO.setUrl(url);
						webRequestDBO.setParameters(params);
						webRequestDBO.setConfig(config);
						webRequestDao.insert(webRequestDBO);
						// Release
						GeneralUtils.SyncThreadPass.releaseSyncThreadPass(WebRequestDao.class);
						mLocalDatabase.close();
					}
				}
			});
		}

		return null;
	}
	
	/**
	 * Delete Request
	 * 
	 * @return
	 * @throws IOException
	 * @throws MalformedURLException
	 */
	public RequestResult deleteRequest(final Requester.RequestMode mode, final String url, final RequestParameters params, final Treaty treaty, final RequestConfig config, final RequestHeader header, final String data, final GeneralUtils.OnResultListener<RequestResult> onResult) {
		// Immediate
		if (mode == Requester.RequestMode.IMMEDIATE) {
			final RequestResult result = DELETERequest(url, params, treaty, config, header, data);
			if (onResult != null)
				onResult.onResult(result);
			return result;
		// Future
		} else if (mode == Requester.RequestMode.FUTURE) {
			//
			final BlockRequestResult blockRequestResult = new BlockRequestResult();
			/**
			 * Create Service
			 */
			final ServiceRunnable serviceRunnable = new ServiceRunnable() {

				/**
				 * Run Service
				 */
				@Override
				public void run(ServicesManager shServicesManager, ServiceConnector connector) {
					//
					try {
						// Get JSon
						final RequestResult requestResult = DELETERequest(url, params, treaty, config, header, data);
						blockRequestResult.releaseResult(requestResult);

						// Post
						mHandler.post(new Runnable() {

							@Override
							public void run() {
								if(onResult != null)
									onResult.onResult(requestResult);
							}
						});

						// Error
					} catch (final Exception e) {
						//
						final RequestResult error = new RequestResult(null, 404, treaty, false);
						blockRequestResult.releaseResult(error);
						// Post Error
						mHandler.post(new Runnable() {

							@Override
							public void run() {
								if(onResult != null)
									onResult.onResult(error);
							}
						});
					}
				}
			};
			//
			mServicesManager.addService(serviceRunnable);
			//
			return blockRequestResult;
		// Persistent
		} else if(mode == Requester.RequestMode.PERSISTENT) {
			// Attempt
			deleteRequest(Requester.RequestMode.FUTURE, url, params, treaty, config, header, data, new GeneralUtils.OnResultListener<RequestResult>() {
				
				/**
				 * On Result
				 */
				@Override
				public void onResult(RequestResult result) {
					// If Successfully Responsed
					if(result.isSuccessfullyResponse()) {
						// Pass
						if(onResult != null)
							onResult.onResult(result);
					// Error
					} else {
						// Obtain
						mLocalDatabase.open();
						WebRequestDao webRequestDao = new WebRequestDao(mLocalDatabase);
						try {
							GeneralUtils.SyncThreadPass.obtainSyncThreadPass(WebRequestDao.class, 10, -1);
						} catch (Exception e) {
							return;
						}
						// Add Request
						WebRequestDBO webRequestDBO = new WebRequestDBO();
						webRequestDBO.setType(WebRequestDBO.REQUEST_TYPE_POST);
						webRequestDBO.setUrl(url);
						webRequestDBO.setParameters(params);
						webRequestDBO.setConfig(config);
						webRequestDao.insert(webRequestDBO);
						// Release
						GeneralUtils.SyncThreadPass.releaseSyncThreadPass(WebRequestDao.class);
						mLocalDatabase.close();
					}
				}
			});
		}

		return null;
	}

	/**
	 * Delete Request
	 * 
	 * @return
	 */
	public RequestResult DELETERequest(final String url, final RequestParameters params, final Treaty treaty, final RequestConfig config, final RequestHeader header, final String data) {
		try {
			URL requestUrl;
			if (params == null) {
				requestUrl = new URL(url);
			} else {
				final String paramsMolded = params.mold();
				requestUrl = new URL(url + "?" + paramsMolded);
			}
			// Open Connection
			final HttpURLConnection connection = (HttpURLConnection) requestUrl.openConnection();

			try {
				connection.setRequestMethod("DELETE");
				if (config != null) {
					connection.setConnectTimeout(config.connectionTimeout);
					connection.setReadTimeout(config.readTimeout);
					connection.setDoOutput(config.doOutput);
					connection.setDoInput(config.doInput);
					connection.setUseCaches(config.useCaches);
				}
				if (header != null)
					header.fillTo(connection);

				// If send Data
				if (data != null) {
					OutputStream os = connection.getOutputStream();
					os.write(data.toString().getBytes("UTF-8"));
					os.close();
				}

				// Return result
				return new RequestResult(readInput(connection.getInputStream()), connection.getResponseCode(), treaty, false);
			} catch (Exception intern) {
				return new RequestResult("", connection.getResponseCode(), treaty, false);
			}
		} catch (final Exception e) {
			return new RequestResult("", 404, treaty, false);
		}
	}
	
	/**
	 * Put Request
	 * 
	 * @return
	 */
	public RequestResult PUTRequest(final String url, final RequestParameters params, final Treaty treaty, final RequestConfig config, final RequestHeader header, final String data) {
		try {
			URL requestUrl;
			if (params == null) {
				requestUrl = new URL(url);
			} else {
				final String paramsMolded = params.mold();
				requestUrl = new URL(url + "?" + paramsMolded);
			}
			// Open Connection
			final HttpURLConnection connection = (HttpURLConnection) requestUrl.openConnection();
			try {

				connection.setRequestMethod("PUT");

				if (config != null) {
					connection.setConnectTimeout(config.connectionTimeout);
					connection.setReadTimeout(config.readTimeout);
					connection.setDoOutput(config.doOutput);
					connection.setDoInput(config.doInput);
					connection.setUseCaches(config.useCaches);
				}
				if (header != null)
					header.fillTo(connection);

				// If send Data
				if (data != null) {
					OutputStream os = connection.getOutputStream();
					os.write(data.toString().getBytes("UTF-8"));
					os.close();
				}

				// Return result
				return new RequestResult(readInput(connection.getInputStream()), connection.getResponseCode(), treaty, false);
			} catch (Exception intern) {
				return new RequestResult("", connection.getResponseCode(), treaty, false);
			}
		} catch (final Exception e) {
			return new RequestResult("", 404, treaty, false);
		}
	}

	/**
	 * GET Request
	 * 
	 * @param url
	 * 
	 * @return Json
	 */
	private RequestResult GETRequest(final String url, final RequestParameters params, final Treaty treaty, final RequestConfig config, final RequestHeader header) {
		try {
			URL requestUrl;
			if (params == null) {
				requestUrl = new URL(url);
			} else {
				final String paramsMolded = params.mold();
				requestUrl = new URL(url + "?" + paramsMolded);
			}
			// Open Connection
			final HttpURLConnection connection = (HttpURLConnection) requestUrl.openConnection();

			try {
				connection.setRequestMethod("GET");

				if (config != null) {
					connection.setConnectTimeout(config.connectionTimeout);
					connection.setReadTimeout(config.readTimeout);
					connection.setDoOutput(config.doOutput);
					connection.setDoInput(config.doInput);
					connection.setUseCaches(config.useCaches);
				}

				if (header != null)
					header.fillTo(connection);

				//
				return new RequestResult(readInput(connection.getInputStream()), connection.getResponseCode(), treaty, false);
			} catch (Exception intern) {
				return new RequestResult("", connection.getResponseCode(), treaty, false);
			}
		} catch (final Exception e) {
			return new RequestResult("", 404, treaty, false);
		}
	}

	/**
	 * Post Request
	 */
	private RequestResult POSTRequest(final String url, final RequestParameters params, final Treaty treaty, final RequestConfig config, final RequestHeader header, final String data, final RequestBody dataB) {
		try {
			URL requestUrl;
			if (params == null) {
				requestUrl = new URL(url);
			} else {
				final String paramsMolded = params.mold();
				requestUrl = new URL(url + "?" + paramsMolded);
			}
			final HttpURLConnection connection = (HttpURLConnection) requestUrl.openConnection();
			try {
				if (config != null) {
					connection.setConnectTimeout(config.connectionTimeout);
					connection.setReadTimeout(config.readTimeout);
					connection.setDoOutput(config.doOutput);
					connection.setDoInput(config.doInput);
					connection.setUseCaches(config.useCaches);
				}

				//
				if (header != null)
					header.fillTo(connection);

				connection.setRequestMethod("POST");

				// If send Data
				if (data != null) {
					DataOutputStream os = new DataOutputStream(connection.getOutputStream());
					os.writeBytes(data);
					os.close();
				} else if (dataB != null) {
					DataOutputStream os = new DataOutputStream(connection.getOutputStream());
					dataB.onWrite(os);
					os.flush();
					os.close();
				}

				// Return result
				return new RequestResult(readInput(connection.getInputStream()), connection.getResponseCode(), treaty, false);
			} catch (Exception intern) {
				return new RequestResult("", connection.getResponseCode(), treaty, false);
			}
		} catch (final Exception e) {
			return new RequestResult("", 404, treaty, false);
		}
	}
	
	/**
	 * 
	 * @param inputStream
	 * @return
	 * @throws IOException
	 */
	private String readInput(InputStream inputStream) throws IOException {
		// Open Reader
		final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
		final StringBuilder stringBuilder = new StringBuilder();
		String line = "";
		while ((line = bufferedReader.readLine()) != null)
			stringBuilder.append(line);
		inputStream.close();
		// Return Json
		return stringBuilder.toString();
	}
}
