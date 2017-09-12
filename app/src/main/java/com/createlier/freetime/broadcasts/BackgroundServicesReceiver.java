package com.createlier.freetime.broadcasts;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.createlier.freetime.FreetimeActivity;

/**
 * Background Services Receiver
 * 
 * @author user
 *
 */
public class BackgroundServicesReceiver extends BroadcastReceiver {

	// Final Private Static Variables
	public static final String BROADCAST_MESSAGE_ACTION = "com.ecv.freetime.backgroundservices.availablemessages";

	// Final Private Variables
	final private FreetimeActivity mFreetimeActivity;

	/**
	 * Constructor
	 */
	public BackgroundServicesReceiver(final FreetimeActivity activity) {
		mFreetimeActivity = activity;
	}

	/**
	 * Receive
	 */
	@Override
	public void onReceive(Context context, Intent intent) {
		// If Message action
		if (intent.getAction().equals(BROADCAST_MESSAGE_ACTION))
			mFreetimeActivity.treatBackgroundServicesMessages();
	}
}
