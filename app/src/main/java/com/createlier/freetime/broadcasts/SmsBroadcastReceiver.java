package com.createlier.freetime.broadcasts;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Telephony;
import android.telephony.SmsMessage;

import com.createlier.freetime.FreetimeActivity;


/**
 * Sms Broadcast Receiver
 * @author user
 *
 */
public class SmsBroadcastReceiver extends BroadcastReceiver {

	// Final Private Variables
	final private FreetimeActivity mFreetimeActivity;
	
	/**
	 * Constructor
	 * 
	 * @param freetimeActivity
	 */
	public SmsBroadcastReceiver(final FreetimeActivity freetimeActivity) {
		mFreetimeActivity = freetimeActivity;
	}
	
	/**
	 * On Receive
	 */
	@SuppressLint("NewApi")
	@SuppressWarnings("deprecation")
	@Override
	public void onReceive(Context context, Intent intent) {
		SmsMessage[] smsMessages = null;
		// If is Kitkat
		if (Build.VERSION.SDK_INT >= 19) {
			smsMessages = Telephony.Sms.Intents.getMessagesFromIntent(intent);
		} else {
			Bundle extras = intent.getExtras();
			if (extras != null) {
				Object[] sms = (Object[]) extras.get("pdus");
				smsMessages = new SmsMessage[sms.length];
				for (int i = 0; i < sms.length; ++i)
					smsMessages[i] = SmsMessage.createFromPdu((byte[]) sms[i]);
			}
		}
		//
		mFreetimeActivity.onSmsReceived(smsMessages);
	}
}