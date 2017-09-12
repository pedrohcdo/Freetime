package com.createlier.freetime.session;


import android.content.Context;

import com.createlier.freetime.localdb.LocalDatabase;

/**
 * Account Manager
 * 
 * @author Pedro Henrique
 *
 */
final public class AccountManager {

	// Final Private Variables
	final private Context mContext;
	final private LocalDatabase mLocalDatabase;
	
	/**
	 * Constructor
	 */
	public AccountManager(final Context context, final LocalDatabase localDatabase) {
		mContext = context;
		mLocalDatabase = localDatabase;

	}
	
	public boolean isConnected() {
		return false;
	}
	
}
