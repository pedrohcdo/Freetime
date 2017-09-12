package com.createlier.freetime.services.background;


import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Binder;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Process;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import com.createlier.freetime.broadcasts.BackgroundServicesReceiver;
import com.createlier.freetime.db.FreetimeDatabase;
import com.createlier.freetime.localdb.LocalDatabase;
import com.createlier.freetime.localdb.dao.LocationDao;
import com.createlier.freetime.localdb.objects.LocationDBO;
import com.createlier.freetime.location.LocationBasedServices;
import com.createlier.freetime.location.LocationBasedServicesListener;
import com.createlier.freetime.services.ServiceConnector;
import com.createlier.freetime.services.ServiceRunnable;
import com.createlier.freetime.services.ServicesManager;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Service background
 * 
 * @author Pedro Henrique
 *
 */
final public class BackgroundServicesManager extends Service implements LocationBasedServicesListener, ServicesManager {

	/**
	 * Serializable Message
	 * 
	 * @author user
	 *
	 */
	final public class PostMessage  {
		
		// Final Public Variables
		final public int type;
		final public int what;
		final public Object obj;
		
		/**
		 * Constructor
		 * 
		 * @param what
		 * @param obj
		 */
		public PostMessage(final int type, final int what, final Object obj) {
			this.type = type;
			this.what = what;
			this.obj = obj;
		}
	}
	
	/**
	 * Background Binder
	 * 
	 * @author PedroH, RaphaelB
	 * 
	 *         Property Createlier.
	 */
	final public class BackgroundBinder extends Binder {

		/**
		 * Ger Support Service
		 * 
		 * @return
		 */
		final public BackgroundServicesManager getService() {
			return BackgroundServicesManager.this;
		}
	}

	/**
	 * Background Handler
	 * 
	 * @author user
	 *
	 */
	final private class BackgroundHandler extends Handler {

		/**
		 * Constructor
		 * 
		 * @param looper
		 */
		public BackgroundHandler(Looper looper) {
			super(looper);
		}

		/**
		 * Handle Message
		 */
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case LOCATION_MESSAGE:
				if(mLocalDatabase.isOpened()) {
					final Location location = (Location) msg.obj;
					final LocationDao locationDao = new LocationDao(mLocalDatabase);
					locationDao.insert(new LocationDBO(location, new Date().getTime()));
					
					//Log.d("LogTest", "Position: " + location.getLatitude() + ", " + location.getLongitude());
				}
				break;
			case POST_MESSAGE:
				// Send broadcast
				final Intent intent = new Intent(BackgroundServicesReceiver.BROADCAST_MESSAGE_ACTION);
				sendBroadcast(intent);
				break;
			}
		}
	}

	// Conts
	final private int LOCATION_MESSAGE = 0;
	final private int POST_MESSAGE = 1;
	
	// Final Private Variables
	final private BackgroundBinder mBackgroundBinder = new BackgroundBinder();
	final private List<PostMessage> mMessages = new ArrayList<PostMessage>();
	
	// Private Variables
	private boolean mCreated;
	private LocalDatabase mLocalDatabase;
	private BackgroundHandler mBackgroundHandler;
	private LocationBasedServices mLocationBasedServices;
	private BackgroundThreadGroup mBackgroundThreadgroup;
	private volatile boolean mPersistMessages = false;
	private volatile boolean mConnected = false;
	
	/**
	 * Called on app created after really closed.
	 */
	@Override
	final public void onCreate() {
		
		// Open Database
		mLocalDatabase = FreetimeDatabase.newInstance(this);
		mLocalDatabase.open();
		
		//
		mBackgroundThreadgroup = new BackgroundThreadGroup(this);
		
		
		// Handler Thread
		final HandlerThread handlerThread = new HandlerThread("BackgroundServicesThread", Process.THREAD_PRIORITY_BACKGROUND);
		handlerThread.setDaemon(true);
		handlerThread.start();
		
		// Handler
		mBackgroundHandler = new BackgroundHandler(handlerThread.getLooper());
		
		// !! After of LocationBasedServices instantiate
		mCreated = true;
		
		// Create Location Based Services
		mLocationBasedServices = new LocationBasedServices(this, handlerThread.getLooper());
	}
	
	
	/**
	 * Get Thread Group
	 * @return
	 */
	protected BackgroundThreadGroup getThreadGroup() {
		return mBackgroundThreadgroup;
	}
	
	/**
	 * Has Service
	 */
	@Override
	public boolean hasService(int identifier) {
		return mBackgroundThreadgroup.hasServiceThread(identifier);
	}
	
	/**
	 * Has Service
	 */
	@Override
	public boolean hasService(String identifier) {
		return mBackgroundThreadgroup.hasServiceThread(identifier);
	}
	
	/**
	 * Add Service
	 * 
	 * @param runnable
	 * @return
	 */
	@Override
	public int addService(final ServiceRunnable runnable) {
		return mBackgroundThreadgroup.addServiceThread(runnable);
	}
	
	/**
	 * Add Service
	 * 
	 * @param runnable
	 * @param identifier
	 */
	@Override
	public void addService(final ServiceRunnable runnable, final String identifier) {
		mBackgroundThreadgroup.addServiceThread(runnable, identifier);
	}
	
	/**
	 * Get Service
	 */
	@Override
	public ServiceRunnable getService(int identifier) {
		return mBackgroundThreadgroup.getService(identifier);
	}

	/**
	 * Get Service
	 */
	@Override
	public ServiceRunnable getService(String identifier) {
		return mBackgroundThreadgroup.getService(identifier);
	}
	
	/**
	 * Wait for Service
	 * 
	 * @param identifier
	 */
	@Override
	public void waitForService(int identifier) {
		mBackgroundThreadgroup.waitForService(identifier);
	}
	
	/**
	 * Wait for Service
	 * 
	 * @param identifier
	 */
	@Override
	public void waitForService(String identifier) {
		mBackgroundThreadgroup.waitForService(identifier);
	}
	
	/**
	 * Remove Service
	 * 
	 * @param identifier
	 */
	@Override
	public void removeService(final int identifier) {
		mBackgroundThreadgroup.removeServiceThread(identifier);
	}
	
	/**
	 * Remove Service
	 * 
	 * @param identifier
	 */
	@Override
	public void removeService(final String identifier) {
		mBackgroundThreadgroup.removeServiceThread(identifier);
	}
	
	/**
	 * Post Empty Message
	 */
	@Override
	final public void postMessage(final int code) {
		postMessage(code, null);
	}
	
	/**
	 * Post Message
	 */
	@Override
	final public void postMessage(final int code, final Object obj) {
		if(!mConnected && !mPersistMessages)
			return;
		synchronized(mMessages) {
			mMessages.add(new PostMessage(0, code, obj));
		}
		final Message message = mBackgroundHandler.obtainMessage();
		message.what = POST_MESSAGE;
		mBackgroundHandler.sendMessage(message);
	}
	
	/**
	 * Consume Messages
	 * 
	 * @return
	 */
	final public List<PostMessage> consumeMessages() {
		List<PostMessage> messages = new ArrayList<PostMessage>();
		synchronized(mMessages) {
			messages.addAll(mMessages);
			mMessages.clear();
		}
		return messages;
	}
	
	/**
	 * Clear Messages
	 */
	final public void clearMessages() {
		synchronized(mMessages) {
			mMessages.clear();
		}
	}
	
	/**
	 * Post Runnable Message
	 */
	@Override
	final public void postMessage(final Runnable runnable) {
		if(!mConnected && !mPersistMessages)
			return;
		synchronized(mMessages) {
			mMessages.add(new PostMessage(1, 0, runnable));
		}
		final Message message = mBackgroundHandler.obtainMessage();
		message.what = POST_MESSAGE;
		mBackgroundHandler.sendMessage(message);
	}

	/**
	 * Get Service Connector
	 */
	@Override
	public ServiceConnector getServiceConnector(final int identifier) {
		return mBackgroundThreadgroup.getServiceConnector(identifier);
	}

	/**
	 * Get Service Connector
	 */
	@Override
	public ServiceConnector getServiceConnector(final String identifier) {
		return mBackgroundThreadgroup.getServiceConnector(identifier);
	}


	/**
	 * Get Context
	 */
	@Override
	public Context getContext() {
		return this;
	}
	
	/**
	 * Start Foreground
	 */
	final public void startForeground(final String title, final String text, final int icon) {
		// Get Notification Manager
		final NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		// Build default notification
		Intent notificationIntent = new Intent(this, this.getClass());
		notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
		PendingIntent intent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
		final Notification notification = new NotificationCompat
			.Builder(this)
			.setOngoing(true)
			.setSmallIcon(icon)
			.setContentTitle(title)
			.setContentText(text)
			.setContentIntent(intent)
			.build();
		// Notify
		notificationManager.notify(1450, notification);
		startForeground(1450, notification);
	}
	
	
	/**
	 * Location Changed
	 */
	@Override
	public void onLocationChanged(Location locale) {
		if(mCreated) {
			final Message locationMessage = mBackgroundHandler.obtainMessage();
			locationMessage.what = LOCATION_MESSAGE;
			locationMessage.obj = locale;
			mBackgroundHandler.sendMessage(locationMessage);
		}
	}
	
	/**
	 * Start Location Store
	 */
	final public void startLocationStore() {
		mLocationBasedServices.start();
	}
	
	/**
	 * Stop Location Store
	 */
	final public void stopLocationStore() {
		mLocationBasedServices.stop();
	}
	
	/**
	 * Destroy<br>
	 * <b>Note: This segment used for optimization</b>
	 */
	@Override
	public void onDestroy() {
		Log.d("LogTest", "Destroyed");
		mLocationBasedServices.stop();
	}
	
	/**
	 * Set Persist Message
	 * 
	 * @param persist
	 */
	public void setMessagesPersist(final boolean persist) {
		mPersistMessages = persist;
	}
	
	/**
	 * On Bind from clients
	 * 
	 * Called in the first time and also when the onUnbind() returns false.
	 */
	@Override
	final public IBinder onBind(Intent intent) {
		if(!mPersistMessages)
			clearMessages();
		mConnected = true;
		return mBackgroundBinder;
	}

	/**
	 * On Rebind.
	 * 
	 * If the onUnbind() returns true, this will be called instead of onBind().
	 */
	@Override
	public void onRebind(Intent intent) {
		if(!mPersistMessages)
			clearMessages();
		mConnected = true;
	}

	/**
	 * On Unbind from clients
	 * 
	 * Called when the application disconnects this service. As in cases that
	 * the application has been paused or else closed.
	 */
	@Override
	final public boolean onUnbind(Intent intent) {
		if(!mPersistMessages)
			clearMessages();
		mConnected = false;
		return true;
	}
}
