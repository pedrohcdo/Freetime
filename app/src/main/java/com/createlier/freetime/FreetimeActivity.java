package com.createlier.freetime;

import android.support.v7.app.AppCompatActivity;

import com.createlier.freetime.broadcasts.BackgroundServicesReceiver;
import com.createlier.freetime.broadcasts.SmsBroadcastReceiver;
import com.createlier.freetime.db.FreetimeDatabase;
import com.createlier.freetime.localdb.LocalDatabase;
import com.createlier.freetime.localdb.dao.WebRequestDao;
import com.createlier.freetime.localdb.objects.WebRequestDBO;
import com.createlier.freetime.services.ServicesManager;
import com.createlier.freetime.services.background.BackgroundServicesConnection;
import com.createlier.freetime.services.background.BackgroundServicesManager;
import com.createlier.freetime.services.shared.SharedServicesManager;
import com.createlier.freetime.tools.ToolsManager;
import com.createlier.freetime.utils.GeneralUtils;
import com.createlier.freetime.utils.HardwareUtils;
import com.createlier.freetime.webservices.Requester;

/**
 * Main Activity
 * 
 * @author Pedro Henrique
 *
 */
public class FreetimeActivity extends AppCompatActivity
		implements BackgroundServicesListener, OnConnectionFailedListener, ServicesManagerListener {

	/**
	 * Connectivity Changed Receiver
	 * 
	 * @author user
	 *
	 */
	final private class ConnectivityChangedReceiver extends BroadcastReceiver {

		// Variables
		final FreetimeActivity mActivity;
		
		/**
		 * Constructor
		 */
		public ConnectivityChangedReceiver(final FreetimeActivity activity) {
			mActivity = activity;
		}
		
		/**
		 * On Receive
		 * 
		 * @param context
		 * @param intent
		 */
	    @Override
	    public void onReceive(final Context context, final Intent intent) {
	    	onConnectivityChanged();
	    }
	}
	
	/**
	 * Web Services Helper
	 */
	final private Runnable mWebServicesHelper = new Runnable() {
		
		/**
		 * Run
		 */
		@Override
		public void run() {
			consumeWebServicesMessages();
		}
	};
	
	// Final Private Static Variables
	final private static int REQUEST_RESOLVE_ERROR = 9991;

	/** 9992.. +2 */
	final private static int TOOLS_MANAGER_RESOLVE_RANGE = 9992;
	final private static String RESOLVING_ERROR_IDENTIFIER = "resolving_error";

	// Private Variables
	private Handler mHandler;
	
	private ToolsManager mToolsManager;
	private SharedServicesManager mSharedServicesManager;
	private Intent mBackgroundServiceIntent;
	private BackgroundServicesConnection mBackgroundServicesConnection;

	private GoogleApiClient mGoogleApiClient;
	private boolean mGAPIResolvingError = false;

	private SmsBroadcastReceiver mSmsBroadcastReceiver;
	private BackgroundServicesReceiver mBackgroundServicesReceiver;
	private ConnectivityChangedReceiver mConnectivityChangedReceiver;
	
	private LocalDatabase mLocalDatabase;
	private Requester mDataResolver;
	private static Activity mCurrentActivity;

	
	/**
	 * Setup Freetime<br>
	 * <b>Note: Call in onCreate() after setContentView()</b>
	 */
	final public void setupFreetime(final Bundle savedInstanceState) {
		// For Tests //
		//deleteDatabase("local_database");
		//
		FreetimeDatabase.setupDatabase(this.getApplicationContext());
		FreetimeDatabase.singleton().open();

		// Handler
		mHandler = new Handler();
		
		//
		mSharedServicesManager = new SharedServicesManager(this, this);
		mToolsManager = new ToolsManager(this, getSharedServicesManager(), TOOLS_MANAGER_RESOLVE_RANGE);
		
		// Build Google Api Client
		mGoogleApiClient = new GoogleApiClient.Builder(this).addOnConnectionFailedListener(this).addApi(LocationServices.API).build();
		if (savedInstanceState != null && savedInstanceState.getBoolean(RESOLVING_ERROR_IDENTIFIER, false))
			mGAPIResolvingError = !mGoogleApiClient.isConnecting() && !mGoogleApiClient.isConnected();
		
		// Receiver
		mSmsBroadcastReceiver = new SmsBroadcastReceiver(this);
		mBackgroundServicesReceiver = new BackgroundServicesReceiver(this);
		mConnectivityChangedReceiver = new ConnectivityChangedReceiver(this);
		
		//
		
		mLocalDatabase = FreetimeDatabase.newInstance(this);
		mDataResolver = new Requester(this, getSharedServicesManager());
		
		// Register SMS Broadcast Receiver
		IntentFilter filter = new IntentFilter();
		filter.addAction("android.provider.Telephony.SMS_RECEIVED");
		filter.setPriority(Integer.MAX_VALUE);
		registerReceiver(mSmsBroadcastReceiver, filter);

		// Register Background Services Receiver
		filter = new IntentFilter();
		filter.addAction(BackgroundServicesReceiver.BROADCAST_MESSAGE_ACTION);
		registerReceiver(mBackgroundServicesReceiver, filter);
		
		// Register Connectivity Broadcast Receiver
		filter = new IntentFilter();
		filter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
		registerReceiver(mConnectivityChangedReceiver, filter);
		
		//
		mBackgroundServiceIntent = new Intent(this, BackgroundServicesManager.class);
		mBackgroundServicesConnection = new BackgroundServicesConnection(this);
		
		// Start Background Service
		startService(mBackgroundServiceIntent);
	}
	
	/**
	 * Create View
	 */
	@SuppressLint("NewApi")
	public View onCreateView(View parent, String name, Context context, AttributeSet attrs) {
		if (Build.VERSION.SDK_INT >= 11)
			return super.onCreateView(parent, name, context, attrs);
		return null;
	}

	/**
	 * On Connectivity Changed
	 */
	final private void onConnectivityChanged() {
		consumeWebServicesMessages();
	}
	
	/**
	 * Consume Web Services Messages
	 */
	final private void consumeWebServicesMessages() {
		mHandler.removeCallbacks(mWebServicesHelper);
		if(HardwareUtils.InternetUtils.haveConnection(this)) {
			mHandler.postDelayed(mWebServicesHelper, 1000);
		}
		// Read Requests
		mLocalDatabase.open();
		WebRequestDao webRequestDao = new WebRequestDao(mLocalDatabase);
		try {
			GeneralUtils.SyncThreadPass.obtainSyncThreadPass(WebRequestDao.class, 10, -1);
		} catch (Exception e) {
			return;
		}
		List<WebRequestDBO> requests = webRequestDao.listAll();
		webRequestDao.clearAll();
		//
		for(final WebRequestDBO request : requests) {
			switch(request.getType()) {
			case WebRequestDBO.REQUEST_TYPE_GET:
				mDataResolver.prepare(Requester.RequestType.GET)
							 .setMode(Requester.RequestMode.PERSISTENT)
							 .setUrl(request.getUrl())
							 .setParameters(request.getParameters())
							 .setConfig(request.getConfig())
							 .request();
				break;
			case WebRequestDBO.REQUEST_TYPE_POST:
				mDataResolver.prepare(Requester.RequestType.POST)
				 .setMode(Requester.RequestMode.PERSISTENT)
				 .setUrl(request.getUrl())
				 .setParameters(request.getParameters())
				 .setConfig(request.getConfig())
				 .request();
				break;
			case WebRequestDBO.REQUEST_TYPE_PUT:
				
				break;
			}
		}
		// Release
		GeneralUtils.SyncThreadPass.releaseSyncThreadPass(WebRequestDao.class);
		mLocalDatabase.close();
		
	}

	/**
	 * Toast on UI Thread
	 * @param message
     */
	final public void toastOnUiThread(final String message, final int duration) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				Toast.makeText(FreetimeActivity.this, message, Toast.LENGTH_SHORT).show();
			}
		});
	}

	/**
	 * Get Shared Services Manager
	 * 
	 * @return
	 */
	final public SharedServicesManager getSharedServicesManager() {
		return mSharedServicesManager;
	}

	/**
	 * Get Background Services
	 * 
	 * @return
	 */
	final public BackgroundServicesManager getBackgroundServices() {
		return mBackgroundServicesConnection.getBackgroundServices();
	}

	/**
	 * Get Tools Manager
	 * 
	 * @return
	 */
	final public ToolsManager getToolsManager() {
		return mToolsManager;
	}

	/**
	 * Treat Background Services Messages
	 */
	public void treatBackgroundServicesMessages() {
		if(this != mCurrentActivity)
			return;
		// Get Background Services Manager
		final BackgroundServicesManager backgroundServicesManager = getBackgroundServices();
		// If Message action
		if (backgroundServicesManager != null) {
			final List<BackgroundServicesManager.PostMessage> messages = getBackgroundServices().consumeMessages();
			for (final BackgroundServicesManager.PostMessage message : messages) {
				switch (message.type) {
					// Normal Message
					case 0:
						onServicesManagerResponse(backgroundServicesManager, message.what, message.obj);
						break;
					// Runnable Message
					case 1:
						((Runnable) message.obj).run();
						break;
				}
			}
		}
	}

	/**
	 * On Service Connected
	 */
	public void onBackgroundServiceConnected(final BackgroundServicesManager backgroundServices) {
		treatBackgroundServicesMessages();
	}
	
	/**
	 * On Sms Received
	 * 
	 * @param messages
	 */
	public void onSmsReceived(final SmsMessage[] messages) {}
	
	/**
	 * On Resume
	 */
	@Override
	protected void onResume() {
		super.onResume();
		mCurrentActivity = this;
		// Connect to GApi
		if (!mGAPIResolvingError)
			mGoogleApiClient.connect();
		// Bind Background Service
		bindService(mBackgroundServiceIntent, mBackgroundServicesConnection, Service.BIND_AUTO_CREATE);
	}

	/**
	 * Save State
	 */
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		// Save GAPI State
		outState.putBoolean(RESOLVING_ERROR_IDENTIFIER, mGAPIResolvingError);
	}

	/**
	 * On Pause
	 */
	@Override
	protected void onPause() {
		// Disconnect from Google Play Services
		if (mGoogleApiClient.isConnecting() || mGoogleApiClient.isConnected())
			mGoogleApiClient.disconnect();
		//
		// Unbind Background Service
		unbindService(mBackgroundServicesConnection);
		//
		super.onPause();
	}

	/**
	 * Activity Result
	 */
	@Override
	protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
		// If Google Play Services resolve Error
		if (requestCode == REQUEST_RESOLVE_ERROR) {
			// Error resolved
			mGAPIResolvingError = false;
			//
			if (resultCode == RESULT_OK) {
				// Try Again
				if (!mGoogleApiClient.isConnecting() && !mGoogleApiClient.isConnected())
					mGoogleApiClient.connect();
			}
			//
			return;
		}
		// Shared Services Resolve Error
		mToolsManager.onActivityResult(requestCode, resultCode, data);
	}

	/**
	 * On Destroy<br>
	 * 
	 * <b>Nor will always be invoked by the life cycle. When called , will help
	 * in optimization.</b>
	 */
	@Override
	protected void onDestroy() {
		if(mSmsBroadcastReceiver != null)
			unregisterReceiver(mSmsBroadcastReceiver);
		if(mBackgroundServicesReceiver != null)
			unregisterReceiver(mBackgroundServicesReceiver);
		if(mConnectivityChangedReceiver != null)
			unregisterReceiver(mConnectivityChangedReceiver);
		mSmsBroadcastReceiver = null;
		mBackgroundServicesReceiver = null;
		mConnectivityChangedReceiver = null;
		super.onDestroy();
	}

	/**
	 * Get Google Api Client
	 * 
	 * @return
	 */
	public GoogleApiClient getGoogleApiClient() {
		return mGoogleApiClient;
	}

	/**
	 * Google Play Services connection failed
	 */
	@Override
	public void onConnectionFailed(ConnectionResult result) {
		// If Already attempting to resolve an error.
		if (mGAPIResolvingError)
			return;
		// If has Resolution
		else if (result.hasResolution()) {
			try {
				mGAPIResolvingError = true;
				result.startResolutionForResult(this, REQUEST_RESOLVE_ERROR);
			} catch (final SendIntentException e) {
				mGoogleApiClient.connect();
			}
		}
	}

	/**
	 * On Shared Services Message (UI Thread Safe)
	 */
	@Override
	public void onServicesManagerResponse(ServicesManager manager, int code, Object obj) {
		// Testing
		switch (code) {
		case ToolsManager.RESPONSE_GPS_ENABLED:
			Log.d("LogTest", "GPS Enabled");
			break;
		case ToolsManager.RESPONSE_GPS_ENABLE_ERROR:

			break;
		case ToolsManager.RESPONSE_AIRPLANEMODE_DISABLED:
			Log.d("LogTest", "Airplane Mode Disabled");
			break;
		case ToolsManager.RESPONSE_AIRPLANEMODE_DISABLE_ERROR:
			Log.d("LogTest", "Airplane Mode Disable Error");
			break;
		}
	}
}