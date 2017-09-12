package com.createlier.freetime.localdb;


import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.createlier.freetime.exceptions.LocalDatabaseException;

/**
 * Local Database
 * 
 * @author Pedro Henrique
 *
 */
final public class LocalDatabase {

	// Final Private Variables
	final private Context mContext;
	final private LocalDatabaseController mManager;
	final private LocalDatabaseListener mListener;
	final private String mName;
	final int mVersion;
	
	// Private Variables
	private LocalDatabaseHelper mHelper;
	private SQLiteDatabase mDatabase;
	
	/**
	 * Constructor
	 */
	public LocalDatabase(final Context context, final String name, final int version, final LocalDatabaseListener listener) {
		mContext = context;
		mManager = new LocalDatabaseController(mContext, this);
		mName = name;
		mVersion = version;
		mListener = listener;
	}
	
	/**
	 * Constructor
	 */
	public LocalDatabase(final Context context, final String name, final int version) {
		mContext = context;
		mManager = new LocalDatabaseController(mContext, this);
		mName = name;
		mVersion = version;
		mListener = null;
	}

	/**
	 * Open
	 *
	 */
	public void open() {
		if (mHelper != null)
			return;
		try {
			mHelper = new LocalDatabaseHelper(mContext, mName, mVersion, mListener);
			mDatabase = mHelper.getWritableDatabase();
			if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN)
				mDatabase.setForeignKeyConstraintsEnabled(true);
		} catch(final SQLException e) {
			throw new LocalDatabaseException("SQLException -> " + e.getMessage());
		}
	}

	/**
	 * Is opened
	 * 
	 * @return
	 */
	public boolean isOpened() {
		return mDatabase != null;
	}

	/**
	 * Close
	 */
	public void close() {
		if (mDatabase == null)
			return;
		mHelper.close();
		mHelper = null;
		mDatabase = null;
	}
	
	/**
	 * Get DAO Manager
	 * @return
	 */
	public LocalDatabaseController getLocalDatabaseController() {
		return mManager;
	}

	/**
	 * Get Database
	 * 
	 * @return
	 */
	protected SQLiteDatabase getDatabase() {
		if(!isOpened())
			throw new LocalDatabaseException("The database was not opened.");
		return mDatabase;
	}
	
	/**
	 * Get Context
	 * 
	 * @return
	 */
	protected Context getContext() {
		return mContext;
	}
}