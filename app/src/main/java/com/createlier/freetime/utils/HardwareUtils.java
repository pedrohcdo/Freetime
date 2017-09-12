package com.createlier.freetime.utils;


import android.app.Activity;

import com.createlier.freetime.R;
import com.google.android.gms.common.api.GoogleApiClient;

/**
 * Hardware Utils
 * 
 * @author Pedro Henrique
 *
 */
final public class HardwareUtils {

	/** Private Constructor */
	private HardwareUtils() {
	}

	/**
	 * GPS Control
	 * 
	 * @author Pedro Henrique
	 *
	 */
	final public static class GPSControl {

		/** Private Constructor */
		private GPSControl() {
		}

		/**
		 * Set GPS State
		 */
		public static void enableGPS(final Activity activity, final GoogleApiClient client, final int gpsResultCode,
									 final int alternativeGPSResultCode, final Runnable onError) {
			// If not enabled
			if (!isGpsEnabled(activity)) {
				LocationRequest balancedAccuracy = LocationRequest.create();
				balancedAccuracy.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

				// Request
				LocationSettingsRequest.Builder request = new LocationSettingsRequest.Builder()
						.addLocationRequest(balancedAccuracy).setAlwaysShow(true);

				// Send Request
				PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi
						.checkLocationSettings(client, request.build());
				result.setResultCallback(new ResultCallback<LocationSettingsResult>() {

					/**
					 * Result
					 */
					@Override
					public void onResult(LocationSettingsResult result) {
						final Status status = result.getStatus();
						switch (status.getStatusCode()) {
						case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
							try {
								status.startResolutionForResult(activity, gpsResultCode);
							} catch (IntentSender.SendIntentException e) {
								enableAlternativeGPS(activity, onError, alternativeGPSResultCode);
							}
							break;
						case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
							enableAlternativeGPS(activity, onError, alternativeGPSResultCode);
							break;
						}
					}
				});
			}
		}

		/**
		 * Set GPS State
		 */
		public static void enableAlternativeGPS(final Activity activity, final Runnable onCancelled,
                                                final int resultCode) {
			// If not enabled
			if (!isGpsEnabled(activity)) {
				// Build Alert Dialog
				final AlertDialog.Builder builder = new AlertDialog.Builder(activity)
						.setMessage(R.string.alternative_gps_alert_dialog).setCancelable(false)
						// Send to GPS configurations
						.setPositiveButton("Sim", new DialogInterface.OnClickListener() {

							/**
							 * On Click
							 */
							public void onClick(DialogInterface dialog, int id) {
								Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
								activity.startActivityForResult(intent, resultCode);
							}
						})
						// Cancell
						.setNegativeButton("Não", new DialogInterface.OnClickListener() {

							/**
							 * On Click
							 */
							public void onClick(DialogInterface dialog, int id) {
								if (onCancelled != null)
									onCancelled.run();
								dialog.cancel();
							}
						});

				// Alert Dialog
				activity.runOnUiThread(new Runnable() {

					/**
					 * Run
					 */
					@Override
					public void run() {
						//
						AlertDialog alertDialog = builder.create();
						alertDialog.show();
					}
				});
			}
		}

		/**
		 * Check if GPS is enabled
		 * 
		 * @param context
		 * @return
		 */
		@TargetApi(Build.VERSION_CODES.KITKAT)
		@SuppressWarnings("deprecation")
		public static boolean isGpsEnabled(Context context) {
			LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
			if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
				return true;
			// If permitted
			final int permission = context.checkCallingOrSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION);
			if (permission == PackageManager.PERMISSION_GRANTED)
				return ((LocationManager) context.getSystemService(Context.LOCATION_SERVICE))
						.isProviderEnabled(LocationManager.GPS_PROVIDER);
			// If the version is less than kitkat
			if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
				String providers = Secure.getString(context.getContentResolver(), Secure.LOCATION_PROVIDERS_ALLOWED);
				if (providers == null || providers.length() == 0)
					return false;

				// If have provider
				return providers.contains(LocationManager.GPS_PROVIDER);
				// If the version is higher
			} else {
				final int mode;
				try {
					mode = Secure.getInt(context.getContentResolver(), Secure.LOCATION_MODE);
					switch (mode) {
					case Secure.LOCATION_MODE_HIGH_ACCURACY:
					case Secure.LOCATION_MODE_SENSORS_ONLY:
						return true;
					case Secure.LOCATION_MODE_BATTERY_SAVING:
					case Secure.LOCATION_MODE_OFF:
					default:
					}
				} catch (SettingNotFoundException e) {
				}
				return false;
			}
		}
	}

	/**
	 * Data Mobile Control
	 * 
	 * @author Pedro Henrique
	 *
	 */
	final public static class MobileDataControl {

		/** Private Constructor */
		private MobileDataControl() {
		}

		/**
		 * Set Mobilie Data for Android version 2.2
		 * 
		 * @param enabled
		 */
		private static void setMobileDataAndroidVersion2_2(final Context context, final boolean enabled) {
			// Get Telephony Manager
			final TelephonyManager telephonyManager = (TelephonyManager) context
					.getSystemService(Context.TELEPHONY_SERVICE);

			// Wait for Mobile Data end Services
			if (!enabled) {
				// Wait for connecting
				GeneralUtils.SafetyLockedLooper.loop(5000, new GeneralUtils.SafetyLockedLooper.Interception() {

					@Override
					public boolean onIntercept() {
						return telephonyManager.getDataState() != TelephonyManager.DATA_CONNECTING;
					}
				});

				// Already disconnected
				if (telephonyManager.getDataState() == TelephonyManager.DATA_DISCONNECTED) {
					return;
				}
			} else {
				if (telephonyManager.getDataState() == TelephonyManager.DATA_CONNECTING
						|| telephonyManager.getDataState() == TelephonyManager.DATA_CONNECTED)
					return;
			}
			// Catch Errors
			try {
				// Reflect Methods
				Class<?> telephonyManagerClass = Class.forName(telephonyManager.getClass().getName());
				Method getITelephonyMethod = telephonyManagerClass.getDeclaredMethod("getITelephony");
				getITelephonyMethod.setAccessible(true);
				Object ITelephonyStub = getITelephonyMethod.invoke(telephonyManager);
				Class<?> ITelephonyClass = Class.forName(ITelephonyStub.getClass().getName());

				// If Enable
				if (enabled) {
					final Method enableDataConnectivity = ITelephonyClass.getDeclaredMethod("enableDataConnectivity");
					enableDataConnectivity.setAccessible(true);
					enableDataConnectivity.invoke(ITelephonyStub);
					// Wait for really connect
					while (telephonyManager.getDataState() != TelephonyManager.DATA_CONNECTED) {
						if (Thread.currentThread().isInterrupted())
							break;
					}
					// If Disable
				} else {
					final Method disableDataConnectivity = ITelephonyClass.getDeclaredMethod("disableDataConnectivity");
					disableDataConnectivity.setAccessible(true);
					disableDataConnectivity.invoke(ITelephonyStub);
					// Wait for really disconect
					while (telephonyManager.getDataState() != TelephonyManager.DATA_DISCONNECTED) {
						if (Thread.currentThread().isInterrupted())
							break;
					}
				}
			} catch (Exception e) {
			}
		}

		/**
		 * Set Mobilie Data for Android 2.3+
		 * 
		 * @param enabled
		 */
		private static void setMobileDataAndroidVersion2_3OrMore(final Context context, boolean enabled) {
			// If ok
			if (isMobileDataEnabled(context) == enabled)
				return;
			// Get Telephony Manager
			final TelephonyManager telephonyManager = (TelephonyManager) context
					.getSystemService(Context.TELEPHONY_SERVICE);
			// Wait for Mobile Data end Services
			if (!enabled) {
				// while (telephonyManager.getDataState() ==
				// TelephonyManager.DATA_CONNECTING) {
				// }
			} else {
				if (telephonyManager.getDataState() == TelephonyManager.DATA_CONNECTING
						|| telephonyManager.getDataState() == TelephonyManager.DATA_CONNECTED)
					return;
			}
			final ConnectivityManager conman = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			try {
				final Class<?> conmanClass = Class.forName(conman.getClass().getName());
				final Field iConnectivityManagerField = conmanClass.getDeclaredField("mService");
				iConnectivityManagerField.setAccessible(true);
				final Object iConnectivityManager = iConnectivityManagerField.get(conman);
				final Class<?> iConnectivityManagerClass = Class.forName(iConnectivityManager.getClass().getName());
				final Method setMobileDataEnabledMethod = iConnectivityManagerClass
						.getDeclaredMethod("setMobileDataEnabled", Boolean.TYPE);
				final Method getMobileDataEnabledMethod = iConnectivityManagerClass
						.getDeclaredMethod("getMobileDataEnabled");
				setMobileDataEnabledMethod.setAccessible(true);
				getMobileDataEnabledMethod.setAccessible(true);
				// If state equal command
				if (enabled == (Boolean) getMobileDataEnabledMethod.invoke(iConnectivityManager)) {
					return;
				}
				setMobileDataEnabledMethod.invoke(iConnectivityManager, enabled);

				while (true) {

					if (enabled)
						GeneralUtils.SafetyLockedLooper.loop(5000, new GeneralUtils.SafetyLockedLooper.Interception() {

							@Override
							public boolean onIntercept() {
								return !(telephonyManager.getDataState() != TelephonyManager.DATA_CONNECTED);
							}
						});

					else
						GeneralUtils.SafetyLockedLooper.loop(5000, new GeneralUtils.SafetyLockedLooper.Interception() {

							@Override
							public boolean onIntercept() {
								return !(telephonyManager.getDataState() != TelephonyManager.DATA_DISCONNECTED);
							}
						});

					break;

				}
			} catch (Exception e) {
			}
		}

		/**
		 * Set Mobilie Data for Android 2.2+
		 * 
		 * @param enabled
		 */
		public static void setMobileData(final Context context, boolean enabled) {
			// Froyo Version
			if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.FROYO)
				setMobileDataAndroidVersion2_2(context, enabled);
			// Set for others versions (2.3+)
			else
				setMobileDataAndroidVersion2_3OrMore(context, enabled);
		}

		/**
		 * Return is Mobile State Enabled
		 * 
		 * @return True if Enabled
		 */
		public static boolean isMobileDataEnabled(final Context contex) {
			// Get Telephony Manager
			final ConnectivityManager conman = (ConnectivityManager) contex
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			try {
				final Class<?> conmanClass = Class.forName(conman.getClass().getName());
				final Field iConnectivityManagerField = conmanClass.getDeclaredField("mService");
				iConnectivityManagerField.setAccessible(true);
				final Object iConnectivityManager = iConnectivityManagerField.get(conman);
				final Class<?> iConnectivityManagerClass = Class.forName(iConnectivityManager.getClass().getName());
				final Method getMobileDataEnabledMethod = iConnectivityManagerClass
						.getDeclaredMethod("getMobileDataEnabled");
				getMobileDataEnabledMethod.setAccessible(true);
				return (Boolean) getMobileDataEnabledMethod.invoke(iConnectivityManager);
			} catch (Exception e) {
			}
			return false;
		}
	}

	/**
	 * Airplane Mode Control
	 * 
	 * @author Pedro Henrique
	 *
	 */
	final public static class AirplaneModeControl {

		/** Private Constructor */
		private AirplaneModeControl() {
		}

		/**
		 * Set Airplane Mode State
		 *
		 * @return
		 */
		public static void disableAirplaneModeState(final Activity activity, final int result,
                                                    final Runnable onCancelled) {
			// If disabled
			if (!isAirplaneMode(activity))
				return;

			// Build Alert Dialog
			final AlertDialog.Builder builder = new AlertDialog.Builder(activity)
					.setMessage(R.string.airplane_alert_dialog).setCancelable(false)
					// Send to GPS configurations
					.setPositiveButton("Sim", new DialogInterface.OnClickListener() {

						/**
						 * On Click
						 */
						public void onClick(DialogInterface dialog, int id) {
							// Send to Airplane Mode Configuration
							openAirplaneModeConfiguration(activity, result);
						}
					})

					// Cancell
					.setNegativeButton("N�o", new DialogInterface.OnClickListener() {

						/**
						 * On Click
						 */
						public void onClick(DialogInterface dialog, int id) {
							if (onCancelled != null)
								onCancelled.run();
							dialog.cancel();
						}
					});

			// If in UI Thread
			if (Looper.myLooper() == Looper.getMainLooper()) {
				//
				AlertDialog alertDialog = builder.create();
				alertDialog.show();

			} else {
				// Alert Dialog
				activity.runOnUiThread(new Runnable() {

					/**
					 * Run
					 */
					@Override
					public void run() {
						//
						AlertDialog alertDialog = builder.create();
						alertDialog.show();
					}
				});
			}

		}

		/**
		 * Open Airplane Mode Configuration
		 * 
		 * @param activity
		 */
		private static void openAirplaneModeConfiguration(final Activity activity, final int resultCode) {
			// If less than Jelly Bean
			if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) {
				try {
					Intent intent = new Intent(Settings.ACTION_AIRPLANE_MODE_SETTINGS);
					activity.startActivityForResult(intent, resultCode);
				} catch (final ActivityNotFoundException e) {
					Log.d("LogTest", "Not found Airplane Configuration");
				}
			} else {
				Intent intent = new Intent("android.settings.WIRELESS_SETTINGS");
				activity.startActivityForResult(intent, resultCode);
			}
		}

		/**
		 * Is Airplane Mode On
		 * 
		 * @param context
		 * @return
		 */
		@SuppressWarnings("deprecation")
		@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
		public static boolean isAirplaneMode(final Context context) {
			// If less than Jelly Bean
			if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1)
				return Settings.System.getInt(context.getContentResolver(), Settings.System.AIRPLANE_MODE_ON, 0) != 0;
			else
				return Settings.Global.getInt(context.getContentResolver(), Settings.Global.AIRPLANE_MODE_ON, 0) != 0;
		}
	}

	/**
	 * Sms Manager
	 * 
	 * @author user
	 *
	 */
	final public static class SmsUtils {

		/** Private Constructor */
		private SmsUtils() {
		}

		/**
		 * Send Sms
		 * 
		 * @param numer
		 * @param body
		 */
		final public static void sendSms(final String numer, final String message) {
			SmsManager smsManager = SmsManager.getDefault();
			smsManager.sendTextMessage(numer, null, message, null, null);
		}
	}

	/**
	 * Internal Utils
	 * 
	 * @author user
	 *
	 */
	final public static class InternetUtils {

		/**
		 * Have Connection
		 * @param context
		 * @return true if have connection
		 */
		@SuppressLint("NewApi")
		public static boolean haveConnection(final Context context) {
			final ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
			//
			if (connectivityManager != null) {
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
					Network[] networks = connectivityManager.getAllNetworks();
					NetworkInfo networkInfo;
					for (Network mNetwork : networks) {
						networkInfo = connectivityManager.getNetworkInfo(mNetwork);
						if(networkInfo == null)
							continue;
						if((networkInfo.getTypeName().equalsIgnoreCase("WIFI") || networkInfo.getTypeName().equalsIgnoreCase("MOBILE")) && networkInfo.isConnected())
							return true;
					}
				} else {
					@SuppressWarnings("deprecation")
					NetworkInfo[] info = connectivityManager.getAllNetworkInfo();
					if (info != null) {
						for (NetworkInfo networkInfo : info) {
							if(networkInfo == null)
								 continue;
							if((networkInfo.getTypeName().equalsIgnoreCase("WIFI") || networkInfo.getTypeName().equalsIgnoreCase("MOBILE")) && networkInfo.isConnected())
								return true;
						}
					}
				}
			}
			// not connected
			return false;
		}
	}

	/**
	 * Display Utils
	 */
	final public static class DisplayUtils {

		/** Private Constructor */
		private DisplayUtils() {
		}

		/**
		 * Get Screen Size
		 */
		final public static GeometricUtils.Size getScreenSize(final Activity activity) {
			WindowManager w = activity.getWindowManager();
			Display d = w.getDefaultDisplay();
			DisplayMetrics metrics = new DisplayMetrics();
			d.getMetrics(metrics);
			return new GeometricUtils.Size(metrics.widthPixels, metrics.heightPixels);
		}

		/**
		 * Get Real Screen Size
		 *
		 * @param display
		 * @param defaultScreenSize
         * @return
         */
		@SuppressLint("NewApi")
		final static public GeometricUtils.Size getRealScreenSize(Display display, GeometricUtils.Size defaultScreenSize) {
			GeometricUtils.Size finalScreenSize = new GeometricUtils.Size(0, 0);
			if (Build.VERSION.SDK_INT >= 19) {
				GeometricUtils.Size outPoint = new GeometricUtils.Size();
				DisplayMetrics metrics = new DisplayMetrics();
				display.getRealMetrics(metrics);
				outPoint.width = metrics.widthPixels;
				outPoint.height = metrics.heightPixels;
				finalScreenSize = new GeometricUtils.Size(outPoint.width, outPoint.height);
			}
			if (finalScreenSize.width < defaultScreenSize.width && finalScreenSize.height < defaultScreenSize.height) {
				return defaultScreenSize;
			}
			return finalScreenSize;
		}
	}
}
