package com.createlier.freetime.tools;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;

import com.createlier.freetime.FreetimeActivity;
import com.createlier.freetime.exceptions.SharedServicesException;
import com.createlier.freetime.services.ServiceConnector;
import com.createlier.freetime.services.ServiceRunnable;
import com.createlier.freetime.services.ServicesManager;
import com.createlier.freetime.utils.GeneralUtils;
import com.createlier.freetime.utils.HardwareUtils;
import com.google.android.gms.common.api.GoogleApiClient;

/**
 * Shared Services Manager
 * 
 * @author Pedro Henrique
 * 
 */
final public class ToolsManager {
	
	// Conts
	final public static int RESPONSE_WIFI_DISABLED = 10000001;
	final public static int RESPONSE_MOBILEDATA_ENABLED = 10000002;
	final public static int RESPONSE_MOBILEDATA_DISABLED = 10000003;
	
	final public static int RESPONSE_GPS_ENABLED = 10000004;
	final public static int RESPONSE_GPS_ENABLE_ERROR = 10000005;
	
	final public static int RESPONSE_AIRPLANEMODE_DISABLED = 10000006;
	final public static int RESPONSE_AIRPLANEMODE_DISABLE_ERROR = 10000007;
	
	// Final Private Variables
	final private Context mContext;
	final private int mGPSResultCode;
	final private int mAlternativeGPSResultCode;
	final private int mAirplaneModeResultCode;
	final private WifiManager mWifiManager;
	final private ConnectivityManager mConnectivityManager;
	final private ServicesManager mSharedServicesManager;
	
	/**
	 * Constructor
	 */
	public ToolsManager(final Context context, final ServicesManager servicesManager, final int resultCodeRange) {
		mContext = context;
		mSharedServicesManager = servicesManager;
		mGPSResultCode = resultCodeRange;
		mAlternativeGPSResultCode = resultCodeRange + 1;
		mAirplaneModeResultCode = resultCodeRange + 2;
		mWifiManager = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
		mConnectivityManager = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
	}
	
	/**
	 * Get Context
	 * 
	 * @return Context
	 */
	final public Context getContext() {
		return mContext;
	}
	
	/**
	 * On Activity Result
	 * 
	 * @param requestCode
	 * @param resultCode
	 * @param data
	 */
	public void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
		// If GPS Resolution
		if(requestCode == mGPSResultCode) {
	        // Post response
			if (resultCode == Activity.RESULT_OK)
				mSharedServicesManager.postMessage(RESPONSE_GPS_ENABLED);
	        else
	        	mSharedServicesManager.postMessage(RESPONSE_GPS_ENABLE_ERROR);
		// If Alternative GPS Resolution
	    } else if(requestCode == mAlternativeGPSResultCode) {
	    	// Post response
	    	if(isGPSEnabled())
	    		mSharedServicesManager.postMessage(RESPONSE_GPS_ENABLED);
	    	else
	    		mSharedServicesManager.postMessage(RESPONSE_GPS_ENABLE_ERROR);
	    // If Airplane Mode Resolution
	    } else if(requestCode == mAirplaneModeResultCode) {
	    	// Post response
	    	if(isAirplaneModeEnabled())
	    		mSharedServicesManager.postMessage(RESPONSE_AIRPLANEMODE_DISABLE_ERROR);
	    	else
	    		mSharedServicesManager.postMessage(RESPONSE_AIRPLANEMODE_DISABLED);
	    }
	}
	
	
	/**
	 * Get Google Api Client
	 * @return
	 */
	final private GoogleApiClient getGoogleApiClient() {
		if(mContext instanceof FreetimeActivity)
			return ((FreetimeActivity) mContext).getGoogleApiClient();
		throw new SharedServicesException("Use Shared Services on Main Activity instance to use Google Api Services.");
	}
	
	/**
	 * Wait for Wifi States
	 */
	final private void waitForWifiStates() {
		GeneralUtils.SafetyLockedLooper.loop(new GeneralUtils.SafetyLockedLooper.Interception() {
			
			@Override
			public boolean onIntercept() {
				return !(mWifiManager.getWifiState() == WifiManager.WIFI_STATE_ENABLING || mWifiManager.getWifiState() == WifiManager.WIFI_STATE_DISABLING);
			}
		});
        @SuppressWarnings("deprecation")
		final NetworkInfo wifi = mConnectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if(wifi.isAvailable()) {
        	GeneralUtils.SafetyLockedLooper.loop(new GeneralUtils.SafetyLockedLooper.Interception() {
    			
    			@Override
    			public boolean onIntercept() {
    				return !(wifi.getState() == NetworkInfo.State.CONNECTING || wifi.getState() == NetworkInfo.State.DISCONNECTING);
    			}
    		});
        }
	}
	
	/**
	 * Disable Wifi
	 * 
	 * Note: Need Permissions "CHANGE_WIFI_STATE"
	 * 
	 * @return If the service manager is in immediate mode the returned service
	 *         will be null because not released will be no service. Otherwise
	 *         will be returned to instantiate the service launched.
	 */
	final private void disableWifi(final boolean immediate) {
		if(immediate) {
			// Wait For Wifi States
			waitForWifiStates();
				
			// If Wifi Enabled ignore this process
			if (!isWifiEnabled())
				return;
			
			// Disable Wifi
			mWifiManager.setWifiEnabled(false);
			
			// Wait for disable complete
			GeneralUtils.SafetyLockedLooper.loop(5000, new GeneralUtils.SafetyLockedLooper.Interception() {
					
				@Override
				public boolean onIntercept() {
					return !mWifiManager.isWifiEnabled();
				}
			});
		} else {
			// Service SHServiceRunnable
			final ServiceRunnable runnable = new ServiceRunnable() {
				
				/** Runner */
				@Override
				public void run(final ServicesManager shServicesManager, ServiceConnector connector) {
					
					// Wait For Wifi States
					waitForWifiStates();
						
					// If Wifi Enabled ignore this process
					if (!isWifiEnabled())
						return;
					
					// Disable Wifi
					mWifiManager.setWifiEnabled(false);
					
					// Wait for disable complete
					GeneralUtils.SafetyLockedLooper.loop(5000, new GeneralUtils.SafetyLockedLooper.Interception() {
							
						@Override
						public boolean onIntercept() {
							return !mWifiManager.isWifiEnabled();
						}
					});
					
					// Post Message
					mSharedServicesManager.postMessage(RESPONSE_WIFI_DISABLED);
				}
			};
			// Add Service
			mSharedServicesManager.addService(runnable);
		}
	}
	
	
	/**
	 * Get Wifi State
	 * 
	 * Note: Need Permissions "ACCESS_WIFI_STATE"
	 * 
	 * @return True if Wifi enabled/ False if Wifi disabled
	 */
	final public boolean isWifiEnabled() {
		return mWifiManager.isWifiEnabled();
	}
	
	/**
	 * Enable Mobile Data
	 * 
	 * @param immediate
	 *            Immediate Action
	 * 
	 * @return If the service manager is in immediate mode the returned service
	 *         will be null because not released will be no service. Otherwise
	 *         will be returned to instantiate the service launched.
	 */
	final public void enableMobileData(final boolean immediate) {
		if(immediate) {
			// Disable Wifi Immediate
			disableWifi(true);
			// Enable Mobile Data
			HardwareUtils.MobileDataControl.setMobileData(mContext, true);
		} else {
			/** SHServiceRunnable */
			final ServiceRunnable runnable = new ServiceRunnable() {
				
				/**
				 * Runner
				 */
				@Override
				public void run(final ServicesManager shServicesManager, ServiceConnector connector) {
					// Disable Wifi Immediate
					disableWifi(true);
					// Enable Mobile Data
					HardwareUtils.MobileDataControl.setMobileData(mContext, true);
					// Post Message
					mSharedServicesManager.postMessage(RESPONSE_MOBILEDATA_ENABLED);
				}
			};
			
			// Add Service
			mSharedServicesManager.addService(runnable);
		}
	}
	
	/**
	 * Is Data Mobile Enabled Enable
	 * 
	 * @return True if Data Mobile Enabled
	 */
	final public boolean isDataMobileEnabled() {
		return HardwareUtils.MobileDataControl.isMobileDataEnabled(mContext);
	}
	
	/**
	 * Disable Mobile Data
	 * 
	 * @param immediate
	 *            Immediate Action
	 * 
	 * @return If the service manager is in immediate mode the returned service
	 *         will be null because not released will be no service. Otherwise
	 *         will be returned to instantiate the service launched.
	 */
	final public void disableMobileData(final boolean immediate) {
		if(immediate) {
			// Disable Mobile Data
			HardwareUtils.MobileDataControl.setMobileData(mContext, false);
		} else {
			/** SHServiceRunnable */
			final ServiceRunnable runnable = new ServiceRunnable() {
				
				/**
				 * Runner
				 */
				@Override
				public void run(final ServicesManager shServicesManager, ServiceConnector connector) {
					// Disable Mobile Data
					HardwareUtils.MobileDataControl.setMobileData(mContext, false);
					// Post Message
					mSharedServicesManager.postMessage(RESPONSE_MOBILEDATA_DISABLED);
				}
			};
			// Add Service
			mSharedServicesManager.addService(runnable);
		}
	}
	
	/**
	 * Enable GPS
	 * 
	 * <b>Note: This method does not use shared services and isn't immediate. When airplane mode is 
	 * switched off the message will be delivered to the listener normally.</b>
	 */
	final public void enableGPS() {
		// If not calling from activity
		if(!(mContext instanceof Activity))
			throw new SharedServicesException("To enable GPS is necessary to create the Shared Services from Activity.");
		// Disable Airplane Mode
		final Runnable onCancelled = new Runnable() {
			
			/**
			 * Run
			 */
			@Override
			public void run() {
				mSharedServicesManager.postMessage(RESPONSE_GPS_ENABLE_ERROR);
			}
		};
		// Enable GPS
		HardwareUtils.GPSControl.enableGPS((Activity)mContext, getGoogleApiClient(), mGPSResultCode, mAlternativeGPSResultCode, onCancelled);
	}
	
	/**
	 * Is GPS Enable
	 * 
	 * @return True if GPS Enabled
	 */
	final public boolean isGPSEnabled() {
		return HardwareUtils.GPSControl.isGpsEnabled(mContext);
	}
	
	/**
	 * Disable Airplane Mode<br><br>
	 * <b>Note: This method does not use shared services and isn't immediate. When airplane mode is 
	 * switched off the message will be delivered to the listener normally.</b>
	 */
	final public void disableAirplaneMode() {
		// If not calling from activity
		if(!(mContext instanceof Activity))
			throw new SharedServicesException("To disable Airplane Mode is necessary to create the Shared Services from Activity.");
		// Disable Airplane Mode
		final Runnable onCancelled = new Runnable() {
			
			/**
			 * Run
			 */
			@Override
			public void run() {
				mSharedServicesManager.postMessage(RESPONSE_AIRPLANEMODE_DISABLE_ERROR);
			}
		};
		// Disable Airplane Mode
		HardwareUtils.AirplaneModeControl.disableAirplaneModeState((Activity)mContext, mAirplaneModeResultCode, onCancelled);
	}
	
	/**
	 * Is Airplane Mode Enable
	 * 
	 * @return True if Airplane Mode Enabled
	 */
	final public boolean isAirplaneModeEnabled() {
		return HardwareUtils.AirplaneModeControl.isAirplaneMode(mContext);
	}
}
